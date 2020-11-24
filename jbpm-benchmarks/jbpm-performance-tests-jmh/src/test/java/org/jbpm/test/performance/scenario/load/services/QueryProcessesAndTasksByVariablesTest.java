package org.jbpm.test.performance.scenario.load.services;

import org.jbpm.services.api.model.UserTaskDefinition;
import org.jbpm.services.api.model.UserTaskInstanceDesc;
import org.jbpm.test.performance.jbpm.constant.ProcessStorage;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.task.model.TaskSummary;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static java.lang.String.valueOf;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toMap;
import static org.jbpm.test.performance.jbpm.util.JbpmJmhPerformanceUtil.TEMP_FOLDER;
import static org.jbpm.test.performance.jbpm.util.JbpmJmhPerformanceUtil.writeObjectToFile;
import static org.jbpm.test.performance.scenario.load.services.QueryProcessesAndTasksByVariables.PROCESS_VARIABLES_FILENAME;
import static org.jbpm.test.performance.scenario.load.services.QueryProcessesAndTasksByVariables.PU_NAME;
import static org.jbpm.test.performance.scenario.load.services.QueryProcessesAndTasksByVariables.TASK_VARIABLES_FILENAME;

public class QueryProcessesAndTasksByVariablesTest extends AbstractServicesPerformanceBaseTest {

    private static final Logger log = LoggerFactory.getLogger(QueryProcessesAndTasksByVariablesTest.class);

    private static final List<Long> processIds = new CopyOnWriteArrayList<>();

    private static final Map<String, Object> processVariables = new ConcurrentHashMap<>();

    private static Map<String, QueryProcessesAndTasksByVariables.QueryTaskVariable> taskVariables = new ConcurrentHashMap<>();

    private static final AtomicInteger counter = new AtomicInteger(0);

    private static final ExecutorService executorService = Executors.newFixedThreadPool(100);
    private static final String processDefinitionId = ProcessStorage.QueryProcessesAndTasksByVariables.getProcessDefinitionId();

    static {
        puName = PU_NAME;
    }

    @BeforeClass
    public static void loadScenario() throws Exception {

        log.debug("initiating jBPM processes...");

        initServices(singletonList(ProcessStorage.QueryProcessesAndTasksByVariables.getPath()));

        // Get process variables
        Map<String, String> processVarDefs = definitionService.getProcessVariables(deploymentUnit.getIdentifier(), processDefinitionId);
        processVarDefs.entrySet().stream()
                .filter( entry -> "integer".equalsIgnoreCase(entry.getValue()))
                .forEach(entry -> processVariables.put(entry.getKey(), counter.getAndIncrement()));

        // Start processes
        // Workaround for https://issues.redhat.com/browse/RHPAM-3145 issue when starting up several processes in parallel
        processIds.add(processService.startProcess(deploymentUnit.getIdentifier(), processDefinitionId, processVariables));

        log.debug("starting up {} processes", processes);
        CountDownLatch latch = new CountDownLatch(processes-1);
        for (int i = 1; i < processes; i++) {
            executorService.execute( () -> {
                    processIds.add(processService.startProcess(deploymentUnit.getIdentifier(), processDefinitionId, processVariables));
                    latch.countDown();
            });
        }
        latch.await();
        log.debug("end starting up {} processes", processes);

        // Get input and output task vars
        taskVariables = getInputOutputTaskVars();

        // Initialize output task variables
        log.debug("initializing output task variables...");
        List<TaskSummary> tasks = internalTaskService.getTasksAssignedAsPotentialOwnerByProcessId("perfUser", processDefinitionId);
        Predicate<TaskSummary> filterOutputTaskVars = task -> filterByTaskVarName(task) && filterByOutputTaskVarName(task);
        int totalOutputTasks = (int)tasks.parallelStream()
                .filter(filterOutputTaskVars)
                .count();
        CountDownLatch initialOutputTaskVarsLatch = new CountDownLatch(totalOutputTasks);
        tasks.parallelStream()
                .filter(filterOutputTaskVars)
                .forEach(task -> executorService.execute(() -> initOutputTaskVariables(task, initialOutputTaskVarsLatch)));
        initialOutputTaskVarsLatch.await();

        // Update process and task variables twice
        for (int i=0; i<2; i++) {
            log.debug("updating process and task variables twice - iteration {}...", i);
            CountDownLatch updateProcessVarsLatch = new CountDownLatch(processIds.size());
            processVariables.forEach( (k, v) -> processVariables.replace(k, counter.getAndIncrement()));

            processIds.parallelStream().forEach( id -> {
                executorService.execute(() -> {
                    processService.setProcessVariables(deploymentUnit.getIdentifier(), id, processVariables);
                    updateProcessVarsLatch.countDown();
                });
            });
            updateProcessVarsLatch.await();

            int totalTasksToUpdate = (int)tasks.parallelStream()
                    .filter(QueryProcessesAndTasksByVariablesTest::filterByTaskVarName)
                    .count();
            CountDownLatch updateTaskVarsLatch = new CountDownLatch(totalTasksToUpdate);
            tasks.parallelStream()
                    .filter(QueryProcessesAndTasksByVariablesTest::filterByTaskVarName)
                    .forEach(task -> executorService.execute(() -> updateTaskVariables(task, updateTaskVarsLatch)));
            updateTaskVarsLatch.await();
        }

        writeObjectToFile(processVariables, PROCESS_VARIABLES_FILENAME);
        writeObjectToFile(taskVariables, TASK_VARIABLES_FILENAME);

        log.debug("end starting up jBPM processes...");
    }

    @AfterClass
    public static void tearDown() {
        log.debug("tearing down jBPM processes...");
        // Workaround for https://issues.redhat.com/browse/RHPAM-3145 issue when starting up several processes in parallel
        if (!processIds.isEmpty()) {
            jc.abortProcessInstances(singletonList(processIds.get(0)));
            processIds.remove(0);
        }

        if (processes > 0) {
            CountDownLatch latch = new CountDownLatch(processes-1);
            processIds.parallelStream().forEach(
                    id -> executorService.execute(
                            () -> {
                                jc.abortProcessInstances(singletonList(id));
                                latch.countDown();
                            }
                    )
            );
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException("Error while closing down processes: " + e.getMessage(), e);
            }
        }

        if (jc != null) {
            jc.undeployProcess();
        }
        processIds.clear();
        processVariables.clear();
        taskVariables.clear();
        executorService.shutdownNow();
        try {
            Files.delete(Paths.get(TEMP_FOLDER+PROCESS_VARIABLES_FILENAME));
            Files.delete(Paths.get(TEMP_FOLDER+TASK_VARIABLES_FILENAME));
        } catch (IOException ignored) {}
        log.debug("end tearing down jBPM processes...");
    }

    @Test
    public void launchBenchmark() throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(QueryProcessesAndTasksByVariables.class.getSimpleName())
                .param("processes", valueOf(processes))
                .threads(threads)
                .shouldFailOnError(failOnError)
                .shouldDoGC(doGc)
                .measurementIterations(measurementIterations)
                .forks(forks)
                .measurementTime(new TimeValue(iterationTime, TimeUnit.SECONDS))
                .warmupTime(new TimeValue(warmupTime, TimeUnit.SECONDS))
                .timeout(new TimeValue(timeout, TimeUnit.MINUTES))
                .jvmArgsAppend(jvmArgsAppend.toArray(new String[0]))
                .resultFormat(ResultFormatType.CSV)
                .result("results.csv")
                .build();
        new Runner(opt).run();
    }

    private static void initOutputTaskVariables (TaskSummary task, CountDownLatch latch) {
        QueryProcessesAndTasksByVariables.QueryTaskVariable queryTaskVariable = taskVariables.get(task.getName());
        Map<String, Object> outputVars = queryTaskVariable.getOutputVars();
        userTaskService.saveContent(task.getId(), outputVars);
        latch.countDown();
    }

    private static void updateTaskVariables (TaskSummary task, CountDownLatch latch) {
        UserTaskInstanceDesc userTaskDesc = runtimeDataService.getTaskById(task.getId());
        QueryProcessesAndTasksByVariables.QueryTaskVariable queryTaskVariable = taskVariables.get(userTaskDesc.getName());
        Map<String, Object> oldOutputTask = userTaskService.getTaskOutputContentByTaskId(task.getId());
        Map<String, Object> newOutputTask = queryTaskVariable.getOutputVars();

        Map<String, Object> mergedOutput = newOutputTask.entrySet()
                .stream()
                .collect(toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> counter.getAndIncrement(),
                        () -> new HashMap<>(oldOutputTask)));

        Map<String, Object> oldInputTask = userTaskService.getTaskInputContentByTaskId(task.getId());
        Map<String, Object> newInputTask = queryTaskVariable.getInputVars();

        Map<String, Object> mergedInput = newInputTask.entrySet()
                .stream()
                .collect(toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> counter.getAndIncrement(),
                        () -> new HashMap<>(oldInputTask)));

        userTaskService.updateTask(task.getId(), "perfUser", userTaskDesc, mergedInput, mergedOutput);

        taskVariables.get(userTaskDesc.getName()).setInputVars(mergedInput);
        taskVariables.get(userTaskDesc.getName()).setOutputVars(mergedOutput);
        latch.countDown();
    }

    private static boolean filterByTaskVarName(TaskSummary taskName) {
        return taskVariables.containsKey(taskName.getName());
    }

    private static boolean filterByOutputTaskVarName(TaskSummary task) {
        return (filterByTaskVarName(task) &&
                taskVariables.get(task.getName()).getOutputVars() != null &&
                !taskVariables.get(task.getName()).getOutputVars().isEmpty());
    }

    private static Map<String, QueryProcessesAndTasksByVariables.QueryTaskVariable> getInputOutputTaskVars() {
        return definitionService.getTasksDefinitions(deploymentUnit.getIdentifier(), processDefinitionId).stream()
            .filter(userTaskDefinition -> userTaskDefinition.getTaskInputMappings().keySet().stream().anyMatch(s -> s.contains("task_var")))
            .collect(toMap(
                    UserTaskDefinition::getName,
                    userTaskDefinition -> {
                        QueryProcessesAndTasksByVariables.QueryTaskVariable queryTaskVariable = new QueryProcessesAndTasksByVariables.QueryTaskVariable(userTaskDefinition.getName());
                        userTaskDefinition.getTaskInputMappings().keySet().forEach(
                                key -> queryTaskVariable.getInputVars().put(key, counter.getAndIncrement())
                        );
                        userTaskDefinition.getTaskOutputMappings().keySet().forEach(
                                key -> queryTaskVariable.getOutputVars().put(key, counter.getAndIncrement())
                        );
                        return queryTaskVariable;
                }
            ));
    }

    public static void main(String[] args) throws Exception {
        setup();
        loadScenario();
        QueryProcessesAndTasksByVariables queries = new QueryProcessesAndTasksByVariables();
        Blackhole blackhole = new Blackhole("Today's password is swordfish. I understand instantiating Blackholes directly is dangerous.");
        queries.init();
        queries.averageTimeQueryProcessByVariables(blackhole);
        queries.averageTimeQueryProcessByVariablesAndTask(blackhole);
        queries.averageTimeQueryUserTasksByVariables(blackhole);
        queries.throughputQueryProcessByVariables(blackhole);
        queries.throughputQueryProcessByVariablesAndTask(blackhole);
        queries.throughputQueryUserTasksByVariables(blackhole);
        queries.sampleTimeQueryProcessByVariables(blackhole);
        queries.sampleTimeQueryProcessByVariablesAndTask(blackhole);
        queries.sampleTimeQueryUserTasksByVariables(blackhole);
        System.out.println("finished main");
    }
}
