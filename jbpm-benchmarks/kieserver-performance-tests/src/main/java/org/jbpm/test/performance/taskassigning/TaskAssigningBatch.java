package org.jbpm.test.performance.taskassigning;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.jbpm.test.performance.kieserver.KieServerClient;
import org.jbpm.test.performance.kieserver.KieServerTestConfig;
import org.kie.perf.SharedMetricRegistry;
import org.kie.perf.scenario.IPerfTest;
import org.kie.server.api.model.definition.QueryDefinition;
import org.kie.server.api.model.definition.TaskQueryFilterSpec;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.api.model.instance.TaskInstance;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This scenario simulates a batch task assignment. The measured metric is a delay between a moment when all tasks have
 * been created and all of them have been assigned to users.
 *
 * Assumptions:
 * <li>
 *      Two KIE servers are configured; the first one hosts a task planning extension and the second one task assignment
 *      and jBPM extensions. However, the scenario communicates only with the jBPM extension via KIE server remote API.
 * </li>
 * <li>
 *     KIE server task planning extension is configured to optimize for a planning window that can contain all tasks
 *     created in this scenario.
 * </li>
 * */
abstract class TaskAssigningBatch implements IPerfTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskAssigningBatch.class);

    private static final String DATA_SOURCE_JNDI = KieServerTestConfig.getInstance().getDataSourceJndi();
    private static final String CONTAINER_ID = "kieserver-assets";
    private static final String PROCESS_ID = "test-jbpm-assignment.testTaskAssignment";
    private static final String UNASSIGNED_TASKS_QUERY_NAME = "unassignedTasksQuery";
    private static final String ASSIGNED_TASKS_QUERY_NAME = "assignedTasksQuery";
    private static final String UNASSIGNED_TASKS_QUERY =
            "select ti.taskId from AuditTaskImpl ti where ti.actualOwner = '' and ti.status != 'Exited'";
    private static final String ASSIGNED_TASKS_QUERY =
            "select ti.taskId from AuditTaskImpl ti where ti.actualOwner != '' and ti.status = 'Reserved'";

    private final KieServerClient client = new KieServerClient();
    private final ProcessServicesClient processClient = client.getProcessClient();
    private final QueryServicesClient queryClient = client.getQueryClient();

    private ScheduledExecutorService scheduledExecutorService;
    private CountDownLatch allAssignedLatch;

    private final int processCount;

    private Meter completedScenario;
    private Timer startProcessesDuration;
    private Timer taskAssignmentDuration;

    public TaskAssigningBatch(int processCount) {
        this.processCount = processCount;
    }

    @Override
    public void init() {
        registerQuery(UNASSIGNED_TASKS_QUERY_NAME, UNASSIGNED_TASKS_QUERY);
        registerQuery(ASSIGNED_TASKS_QUERY_NAME, ASSIGNED_TASKS_QUERY);
    }

    private void abortAllProcesses() {
        List<ProcessInstance> processInstanceList = processClient.findProcessInstances(CONTAINER_ID, 0, Integer.MAX_VALUE);
        List<Long> processInstanceIdList = processInstanceList.stream()
                .map(processInstance -> processInstance.getId())
                .collect(Collectors.toList());
        processClient.abortProcessInstances(CONTAINER_ID, processInstanceIdList);
    }

    private void registerQuery(String queryName, String queryCode) {
        QueryDefinition query = new QueryDefinition();
        query.setName(queryName);
        query.setSource(DATA_SOURCE_JNDI);
        query.setExpression(queryCode);
        query.setTarget("CUSTOM");
        queryClient.replaceQuery(query);
    }

    @Override
    public void initMetrics() {
        MetricRegistry metrics = SharedMetricRegistry.getInstance();
        completedScenario = metrics.meter(MetricRegistry.name(getClass(), "taskassigning.batch.completed"));
        startProcessesDuration = metrics.timer(MetricRegistry.name(getClass(), "taskassigning.batch.processes_started.duration"));
        taskAssignmentDuration = metrics.timer(MetricRegistry.name(getClass(), "taskassigning.batch.tasks_assigned.duration"));
    }

    private void beforeScenario() {
        abortAllProcesses();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        allAssignedLatch = new CountDownLatch(1);
    }

    @Override
    public void execute() {
        beforeScenario();

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            TaskQueryFilterSpec filterSpec = new TaskQueryFilterSpec();
            List<TaskInstance> unassignedTasks = queryClient.findHumanTasksWithFilters(UNASSIGNED_TASKS_QUERY_NAME,
                    filterSpec, 0, 100);
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Unassigned tasks present: " + unassignedTasks.size());
            }
            if (unassignedTasks.isEmpty()) {
                List<TaskInstance> assignedTasks = queryClient.findHumanTasksWithFilters(ASSIGNED_TASKS_QUERY_NAME,
                        filterSpec, 0, Integer.MAX_VALUE);
                if (assignedTasks.size() == 3 * processCount) {
                    LOGGER.debug("No more unassigned tasks found.");
                    allAssignedLatch.countDown();
                }
            }
        }, 0, 500, TimeUnit.MILLISECONDS);

        LOGGER.debug("Creating processes ...");
        Timer.Context startProcessDurationContext = startProcessesDuration.time();
        Timer.Context taskAssignmentDurationContext = taskAssignmentDuration.time();
        for (int i = 0; i < processCount; i++) {
            processClient.startProcess(CONTAINER_ID, PROCESS_ID);
        }
        startProcessDurationContext.stop();
        LOGGER.debug("All processes have been started");

        try {
            allAssignedLatch.await();
            taskAssignmentDurationContext.stop();
            LOGGER.debug("All tasks have been assigned");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted waiting during test.", e);
        }
        completedScenario.mark();

        afterScenario();
    }

    private void shutdownExecutor() {
        scheduledExecutorService.shutdown();
        try {
            if (!scheduledExecutorService.awaitTermination(1, TimeUnit.SECONDS)) {
                scheduledExecutorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduledExecutorService.shutdownNow();
        }
    }

    private void afterScenario() {
        shutdownExecutor();
        abortAllProcesses();
    }

    @Override
    public void close() {
        // nothing to do
    }
}
