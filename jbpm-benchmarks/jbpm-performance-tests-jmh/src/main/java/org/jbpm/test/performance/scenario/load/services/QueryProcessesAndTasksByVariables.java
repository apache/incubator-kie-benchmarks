package org.jbpm.test.performance.scenario.load.services;

import org.jbpm.services.api.AdvanceRuntimeDataService;
import org.jbpm.services.api.model.ProcessInstanceWithVarsDesc;
import org.jbpm.services.api.model.UserTaskInstanceWithPotOwnerDesc;
import org.jbpm.services.api.query.model.QueryParam;
import org.jbpm.test.performance.jbpm.JBPMKieServicesController;
import org.jbpm.test.performance.jbpm.constant.ProcessStorage;
import org.kie.api.task.model.Status;
import org.kie.internal.query.QueryContext;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.singletonList;
import static org.jbpm.services.api.AdvanceRuntimeDataService.PROCESS_ATTR_DEFINITION_ID;
import static org.jbpm.services.api.AdvanceRuntimeDataService.PROCESS_ATTR_DEPLOYMENT_ID;
import static org.jbpm.services.api.AdvanceRuntimeDataService.TASK_ATTR_STATUS;
import static org.jbpm.services.api.query.model.QueryParam.equalsTo;
import static org.jbpm.services.api.query.model.QueryParam.in;
import static org.jbpm.services.api.query.model.QueryParam.list;
import static org.jbpm.services.api.query.model.QueryParam.type;
import static org.kie.internal.task.api.TaskVariable.VariableType.OUTPUT;
import static org.jbpm.test.performance.jbpm.JBPMKieServicesController.DEPLOYMENT_ID;
import static org.jbpm.test.performance.jbpm.util.JbpmJmhPerformanceUtil.readObjectFromFile;

@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-Xms2G", "-Xmx2G"})
@Warmup(iterations = 1, time = 30)
@Measurement(iterations = 1, time = 30)
@Threads(1)
public class QueryProcessesAndTasksByVariables {

    private static final Logger log = LoggerFactory.getLogger(QueryProcessesAndTasksByVariables.class);

    @Param("")
    private String processes;

    private Map<String, Object> processVariables;
    private Map<String, QueryTaskVariable> taskVariables;
    private AdvanceRuntimeDataService advanceRuntimeDataService;

    public static final String PROCESS_VARIABLES_FILENAME = "processVariables.dat";
    public static final String TASK_VARIABLES_FILENAME = "taskVariables.dat";

    public static final String PU_NAME = "org.jbpm.domain.query-process-task-variables";

    @Setup
    public void init() throws Exception {
        processVariables = (Map<String, Object>) readObjectFromFile(PROCESS_VARIABLES_FILENAME);
        taskVariables = (Map<String, QueryTaskVariable>) readObjectFromFile(TASK_VARIABLES_FILENAME);
        advanceRuntimeDataService = JBPMKieServicesController
                .getInstance(singletonList(ProcessStorage.QueryProcessesAndTasksByVariables.getPath()), PU_NAME)
                .getAdvanceRuntimeDataService();
    }

    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Benchmark
    public void throughputQueryProcessByVariables(Blackhole blackhole) {
        queryProcessByVariables(blackhole);
    }

    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Benchmark
    public void sampleTimeQueryProcessByVariables(Blackhole blackhole) {
        queryProcessByVariables(blackhole);
    }

    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Benchmark
    public void averageTimeQueryProcessByVariables(Blackhole blackhole) {
        queryProcessByVariables(blackhole);
    }

    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Benchmark
    public void throughputQueryUserTasksByVariables(Blackhole blackhole) {
        queryUserTasksByVariables(blackhole);
    }

    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Benchmark
    public void sampleTimeQueryUserTasksByVariables(Blackhole blackhole) {
        queryUserTasksByVariables(blackhole);
    }

    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Benchmark
    public void averageTimeQueryUserTasksByVariables(Blackhole blackhole) {
        queryUserTasksByVariables(blackhole);
    }

    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Benchmark
    public void throughputQueryProcessByVariablesAndTask(Blackhole blackhole) {
        queryProcessByVariablesAndTask(blackhole);
    }

    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Benchmark
    public void sampleTimeQueryProcessByVariablesAndTask(Blackhole blackhole) {
        queryProcessByVariablesAndTask(blackhole);
    }

    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Benchmark
    public void averageTimeQueryProcessByVariablesAndTask(Blackhole blackhole) {
        queryProcessByVariablesAndTask(blackhole);
    }

    private void queryProcessByVariables(Blackhole blackhole) {
        List<QueryParam> variables = new ArrayList<>();
        processVariables.forEach((key, value) -> variables.add(equalsTo(key, String.valueOf(value))));
        List<QueryParam> attributes = list(equalsTo(PROCESS_ATTR_DEFINITION_ID, ProcessStorage.QueryProcessesAndTasksByVariables.getProcessDefinitionId()));
        QueryContext queryContext = new QueryContext(0, 100);

        List<ProcessInstanceWithVarsDesc> queriedProcesses = advanceRuntimeDataService.queryProcessByVariables(attributes, variables, queryContext);
        if (queriedProcesses == null || queriedProcesses.isEmpty()){
            throw new IllegalStateException("Number of processes returned by query is null or do not match with expected values");
        }
        blackhole.consume(queriedProcesses);
    }

    private void queryUserTasksByVariables(Blackhole blackhole) {
        List<QueryParam> taskVariables = new ArrayList<>();
        this.taskVariables.values().stream()
                .map(taskVar -> {
                    Map<String, Object> allTaskVars = new HashMap<>();
                    allTaskVars.putAll(taskVar.getInputVars());
                    allTaskVars.putAll(taskVar.getOutputVars());
                    return allTaskVars;
                })
                .flatMap(taskVar -> taskVar.entrySet().stream())
                .filter(taskVar -> taskVar.getKey().contains("task_var"))
                .limit(1)
                .forEach( taskVar -> {
                    taskVariables.add(equalsTo(taskVar.getKey(), String.valueOf(taskVar.getValue())));
                });
        List<QueryParam> processVariables = new ArrayList<>();
        this.processVariables.forEach((key, value) -> processVariables.add(equalsTo(key, String.valueOf(value))));

        List<QueryParam> attributes = list(equalsTo(PROCESS_ATTR_DEPLOYMENT_ID, DEPLOYMENT_ID));
        QueryContext queryContext = new QueryContext(0, 100);

        List<String> potOwners = singletonList("perfUser");

        List<UserTaskInstanceWithPotOwnerDesc> queriedUserTasks = advanceRuntimeDataService.queryUserTasksByVariables(attributes, taskVariables, processVariables, potOwners, queryContext);
        if (queriedUserTasks == null || queriedUserTasks.isEmpty()) {
            throw new IllegalStateException("Number of user tasks returned by query is null or do not match with expected values");
        }
        blackhole.consume(queriedUserTasks);
    }

    private void queryProcessByVariablesAndTask(Blackhole blackhole){
        List<QueryParam> attributes = list(in(TASK_ATTR_STATUS, Status.Ready.toString(), Status.Created.toString(), Status.Reserved.toString()));
        List<QueryParam> taskVariables = new ArrayList<>();
        this.taskVariables.values().stream()
                .filter(queryTaskVariable ->
                        queryTaskVariable.getOutputVars() != null && !queryTaskVariable.getOutputVars().isEmpty())
                .flatMap(queryTaskVariable -> queryTaskVariable.getOutputVars().entrySet().stream())
                .limit(1)
                .forEach( var -> {
                    taskVariables.add(equalsTo(var.getKey(), String.valueOf(var.getValue())));
                    taskVariables.add(type(var.getKey(), OUTPUT.ordinal()));
                });
        List<QueryParam> processVariables = new ArrayList<>();
        this.processVariables.forEach((key, value) -> processVariables.add(equalsTo(key, String.valueOf(value))));
        List<String> potOwners = singletonList("perfUser");
        QueryContext queryContext = new QueryContext(0, 100);

        List<ProcessInstanceWithVarsDesc> queriedProcesses = advanceRuntimeDataService.queryProcessByVariablesAndTask(attributes, processVariables, taskVariables, potOwners, queryContext);
        if (queriedProcesses == null || queriedProcesses.isEmpty()) {
            throw new IllegalStateException("Number of processes returned by query is null or do not match with expected values");
        }
        blackhole.consume(queriedProcesses);
    }

    public static class QueryTaskVariable implements Serializable {

        private static final long serialVersionUID = 1L;
        private final String taskName;
        private Map<String, Object> inputVars;
        private Map<String, Object> outputVars;

        public QueryTaskVariable(String taskName) {
            this.taskName = taskName;
            this.inputVars = new HashMap<>();
            this.outputVars = new HashMap<>();;
        }

        public String getTaskName() {
            return taskName;
        }

        public Map<String, Object> getInputVars() {
            return inputVars;
        }

        public Map<String, Object> getOutputVars() {
            return outputVars;
        }

        public void setInputVars(Map<String, Object> inputVars) {
            this.inputVars = new HashMap<>(inputVars);
        }

        public void setOutputVars(Map<String, Object> outputVars) {
            this.outputVars = new HashMap<>(outputVars);
        }
    }
}
