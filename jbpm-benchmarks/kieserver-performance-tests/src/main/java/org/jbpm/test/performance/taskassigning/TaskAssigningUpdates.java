package org.jbpm.test.performance.taskassigning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.kie.perf.SharedMetricRegistry;
import org.kie.perf.scenario.IPerfTest;
import org.kie.server.api.model.definition.TaskQueryFilterSpec;
import org.kie.server.api.model.instance.TaskEventInstance;
import org.kie.server.api.model.instance.TaskInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This scenario tests impact of frequent changes to task assigning. After being created by starting processes, tasks
 * are retrieved, started and completed. Together with a delay between starting and completing a task, this scenario
 * simulates users working on their tasks, while each completion of a task represents a problem fact change for the
 * planning extension.
 * The measured metric is a median of delay between a moment when a task was created and when it was started.
 */
abstract class TaskAssigningUpdates extends TaskAssigning implements IPerfTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskAssigningUpdates.class);

    private static final String UNASSIGNED_TASKS_QUERY_NAME = "unassignedTasksQuery";
    private static final String ASSIGNED_TASKS_QUERY_NAME = "assignedTasksQuery";
    private static final String UNASSIGNED_TASKS_QUERY =
            "select ti.taskId from AuditTaskImpl ti where ti.actualOwner = '' and ti.status != 'Exited'";
    private static final String ASSIGNED_TASKS_QUERY =
            "select ti.taskId,ti.actualOwner from AuditTaskImpl ti where ti.actualOwner != '' and ti.status = 'Reserved'";

    private static final int START_PROCESS_THREADS = 10;
    private static final int CONCURRENT_TASK_COMPLETION_THREADS = 1000;
    private static final long TASK_COMPLETION_DELAY_MILLIS = 5000L;

    private final int processCount;
    private final int taskCount;

    private Set<Long> taskIdSet;
    private ExecutorService threadPoolExecutor;
    private Timer taskAssignmentDuration;

    public TaskAssigningUpdates(int processCount) {
        this.processCount = processCount;
        this.taskCount = processCount * 3;
    }

    private void beforeScenario() {
        abortAllProcesses();
        taskIdSet = new ConcurrentHashMap<>().newKeySet();
        threadPoolExecutor = Executors.newFixedThreadPool(CONCURRENT_TASK_COMPLETION_THREADS);
    }

    @Override
    public void init() {
        registerQuery(UNASSIGNED_TASKS_QUERY_NAME, UNASSIGNED_TASKS_QUERY);
        registerQuery(ASSIGNED_TASKS_QUERY_NAME, ASSIGNED_TASKS_QUERY);
    }

    @Override
    public void initMetrics() {
        MetricRegistry metrics = SharedMetricRegistry.getInstance();
        taskAssignmentDuration = metrics.timer(MetricRegistry.name(getClass(), "taskassigning.update.task_assigned.duration"));
    }

    // TODO: Think of a better metric:
    //    1. Set a publish window to 1 task per user. Create more tasks than users
    //    2. check tasks per user
    //    3. measure the time between finishing the first task and assigning a second task for the user,
    //         which represents a reaction to a change (task completion)
    @Override
    public void execute() {
        beforeScenario();

        // Create tasks by starting new processes.
        startProcessesAsync(processCount, START_PROCESS_THREADS);

        // Keep completing tasks to introduce changes and trigger incremental re-planning.
        while (taskIdSet.size() < taskCount) {
            TaskQueryFilterSpec filterSpec = new TaskQueryFilterSpec();
            List<TaskInstance> assignedTasks = getQueryClient()
                    .findHumanTasksWithFilters(ASSIGNED_TASKS_QUERY_NAME, filterSpec, 0, CONCURRENT_TASK_COMPLETION_THREADS);
            if (assignedTasks.isEmpty()) { // If there are no tasks to work on, wait and retry.
                sleep(500L);
                continue;
            }

            LOGGER.debug(String.format("Completing %d tasks.", assignedTasks.size()));
            CompletableFuture[] completableFutures = new CompletableFuture[CONCURRENT_TASK_COMPLETION_THREADS];
            int i = 0;
            for (TaskInstance task : assignedTasks) { // Run each task in a separate thread.
                completableFutures[i++] = CompletableFuture.runAsync(() -> {
                    getTaskClient().startTask(CONTAINER_ID, task.getId(), task.getActualOwner());
                    sleep(TASK_COMPLETION_DELAY_MILLIS);
                    getTaskClient().completeTask(CONTAINER_ID, task.getId(), task.getActualOwner(), new HashMap<>());
                    taskIdSet.add(task.getId());
                }, threadPoolExecutor);
            }

            // Wait for the entire batch of tasks complete before fetching more tasks.
            for (i = 0; i < completableFutures.length; i++) {
                try {
                    if (completableFutures[i] != null) {
                        completableFutures[i].get();
                    }
                } catch (ExecutionException e) {
                    throw new RuntimeException("Exception during an asynchronous task start and completion.",
                            e.getCause());
                } catch (InterruptedException e) {
                    throw new RuntimeException("Interrupted during waiting for a task start and completion.", e);
                }
            }
        }

        List<Long> delayPerTaskList = getDelayPerTask();
        long median = median(delayPerTaskList);
        taskAssignmentDuration.update(median, TimeUnit.MILLISECONDS);

        afterScenario();
    }

    private void afterScenario() {
        shutdownExecutorService(threadPoolExecutor);
        abortAllProcesses();
    }

    private List<Long> getDelayPerTask() {
        List<Long> taskAssignmentDelayList = new ArrayList<>(taskCount);
        for (Long taskId : taskIdSet) {
            List<TaskEventInstance> taskEvents = getTaskClient().findTaskEvents(taskId, 0, Integer.MAX_VALUE);

            long addedMillis = 0;
            long startedMillis = 0;
            for (TaskEventInstance taskEvent : taskEvents) {
                if ("ADDED".equals(taskEvent.getType())) {
                    addedMillis = taskEvent.getLogTime().getTime();
                } else if ("STARTED".equals(taskEvent.getType())) {
                    startedMillis = taskEvent.getLogTime().getTime();
                }
            }
            taskAssignmentDelayList.add(startedMillis - addedMillis);
        }
        return taskAssignmentDelayList;
    }

    private long median(List<Long> numbers) {
        Collections.sort(numbers);
        int length = numbers.size();
        if (length % 2 == 1) {
            return numbers.get(length / 2);
        } else {
            long leftMiddle = numbers.get(length / 2 - 1);
            long rightMiddle = numbers.get(length / 2);
            return Math.round(((double) (leftMiddle + rightMiddle)) / 2);
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.warn("Interrupted during Thread.sleep()");
        }
    }

    @Override
    public void close() {
        // nothing to do
    }
}
