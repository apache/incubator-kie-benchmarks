package org.jbpm.test.performance.scenario.load.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.jbpm.services.api.AdvanceRuntimeDataService;
import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.query.model.QueryParam;
import org.jbpm.test.performance.jbpm.JBPMKieServicesController;
import org.jbpm.test.performance.jbpm.constant.ProcessStorage;
import org.kie.api.task.model.Status;
import org.kie.internal.query.QueryContext;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.task.api.TaskVariable;
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
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Collections.singletonList;
import static org.jbpm.services.api.AdvanceRuntimeDataService.PROCESS_ATTR_DEFINITION_ID;
import static org.jbpm.services.api.AdvanceRuntimeDataService.PROCESS_ATTR_DEPLOYMENT_ID;
import static org.jbpm.services.api.AdvanceRuntimeDataService.TASK_ATTR_STATUS;
import static org.jbpm.services.api.query.model.QueryParam.equalsTo;
import static org.jbpm.services.api.query.model.QueryParam.in;
import static org.jbpm.services.api.query.model.QueryParam.list;
import static org.jbpm.test.performance.jbpm.JBPMKieServicesController.DEPLOYMENT_ID;

@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-Xms2G", "-Xmx2G"})
@Warmup(iterations = 1, time = 30)
@Measurement(iterations = 1, time = 30)
@Threads(1)
public class QueryProcessesAndTasksByVariables {

    protected static final Logger log = LoggerFactory.getLogger(QueryProcessesAndTasksByVariables.class);

    @Param("0")
    // ! Must be overridden using -p from command line
    private int processes;

    private JBPMKieServicesController jc;

    private List<Long> processIds = new ArrayList<>();

    private Map<String, Object> processVariables = new HashMap<>();

    private Map<String, Object> taskVariables = new HashMap<>();

    private AdvanceRuntimeDataService advanceRuntimeDataService;
    private ProcessService processService;
    private RuntimeDataService runtimeDataService;
    private UserTaskService userTaskService;
    private DefinitionService definitionService;
    private DeploymentUnit deploymentUnit;

    private static final String PU_NAME = "org.jbpm.domain.query-process-task-variables";

    String processDefinitionId = ProcessStorage.QueryProcessesAndTasksByVariables.getProcessDefinitionId();

    @Setup
    public void init() throws Exception {
        log.debug("init() - starting up jBPM processes...");
        jc = JBPMKieServicesController.getInstance(singletonList(ProcessStorage.QueryProcessesAndTasksByVariables.getPath()), PU_NAME);

        advanceRuntimeDataService = jc.getAdvanceRuntimeDataService();
        processService = jc.getProcessService();
        runtimeDataService = jc.getRuntimeDataService();
        userTaskService = jc.getUserTaskService();
        definitionService = jc.getDefinitionService();
        deploymentUnit = jc.getDeploymentUnit();
        AtomicInteger counter = new AtomicInteger(0);

        // Get process variables
        Map<String, String> processVarDefs = definitionService.getProcessVariables(deploymentUnit.getIdentifier(), processDefinitionId);
        processVarDefs.entrySet().stream()
                .filter( entry -> "integer".equalsIgnoreCase(entry.getValue()))
                .forEach(entry -> processVariables.put(entry.getKey(), counter.getAndIncrement()));

        // Start processes
        for (int i = 0; i < processes; i++) {
            processIds.add(processService.startProcess(deploymentUnit.getIdentifier(), processDefinitionId, processVariables));
        }

        taskVariables = definitionService.getTasksDefinitions(deploymentUnit.getIdentifier(), processDefinitionId).stream()
                .flatMap( userTaskDefinition -> userTaskDefinition.getTaskOutputMappings().entrySet().stream())
                .filter(entry -> entry.getKey().contains("task_var_out") && entry.getValue().equalsIgnoreCase("Integer"))
                .collect(Collectors.toMap(Map.Entry::getKey,
                                          Map.Entry::getValue,
                                          (a1, a2) -> a1));

        // Update process and task variables twice
        for (int i=0; i<2; i++) {

            processVariables.forEach( (k, v) -> processVariables.replace(k, counter.getAndIncrement()));
            processIds.forEach( id -> {
                processService.setProcessVariables(deploymentUnit.getIdentifier(), id, processVariables);
            });

            taskVariables.forEach( (k, v) -> taskVariables.replace(k, counter.getAndIncrement()));
            runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter())
                    .forEach( taskSummary -> {
                        Map<String, Object> newOutputTask = new HashMap<>();
                        definitionService.getTaskOutputMappings(deploymentUnit.getIdentifier(), processDefinitionId, taskSummary.getName()).keySet().stream()
                                .filter(s -> taskVariables.containsKey(s))
                                .forEach(s -> {
                                    newOutputTask.put(s, taskVariables.get(s));
                                });
                        userTaskService.saveContentFromUser(taskSummary.getId(), "salaboy", newOutputTask);
                    });
        }

        log.debug("init() - end up jBPM processes...");
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

    @TearDown
    public void close() {
        log.debug("close() - tear down jBPM processes...");
        jc.tearDown(processIds);
        processIds.clear();
        processVariables.clear();
        taskVariables.clear();
        log.debug("close() - end tear down jBPM processes...");
    }

    private void queryProcessByVariables(Blackhole blackhole) {
        List<QueryParam> variables = new ArrayList<>();
        processVariables.entrySet().stream()
                .limit(5)
                .forEach( var -> variables.add( equalsTo(var.getKey(), String.valueOf(var.getValue())) ));
        List<QueryParam> attributes = list(equalsTo(PROCESS_ATTR_DEFINITION_ID, ProcessStorage.QueryProcessesAndTasksByVariables.getProcessDefinitionId()));
        QueryContext queryContext = new QueryContext(0, 100);

        blackhole.consume(advanceRuntimeDataService.queryProcessByVariables(attributes, variables, queryContext));
    }

    private void queryUserTasksByVariables(Blackhole blackhole) {
        List<QueryParam> taskVariables = new ArrayList<>();
        this.taskVariables.entrySet().stream()
                .limit(5)
                .forEach( var -> taskVariables.add(equalsTo(var.getKey(), String.valueOf(var.getValue()))) );
        List<QueryParam> processVariables = new ArrayList<>();
        this.processVariables.entrySet().stream()
                .limit(5)
                .forEach( var -> processVariables.add( equalsTo(var.getKey(), String.valueOf(var.getValue())) ));

        List<QueryParam> attributes = list(equalsTo(PROCESS_ATTR_DEPLOYMENT_ID, DEPLOYMENT_ID));
        QueryContext queryContext = new QueryContext(0, 100);

        List<String> potOwners = singletonList("salaboy");

        blackhole.consume(advanceRuntimeDataService.queryUserTasksByVariables(attributes, taskVariables, processVariables, potOwners, queryContext));
    }

    private void queryProcessByVariablesAndTask(Blackhole blackhole){
        List<QueryParam> attributes = list(in(TASK_ATTR_STATUS, Status.Ready.toString(), Status.Created.toString(), Status.Reserved.toString()));
        List<QueryParam> taskVariables = new ArrayList<>();
        this.taskVariables.entrySet().stream()
                .limit(5)
                .forEach( var -> {
                    taskVariables.add(equalsTo(var.getKey(), String.valueOf(var.getValue())));
                    taskVariables.add(QueryParam.type(var.getKey(), TaskVariable.VariableType.OUTPUT.ordinal()));
                });
        List<QueryParam> processVariables = new ArrayList<>();
        this.processVariables.entrySet().stream()
                .limit(5)
                .forEach( var -> processVariables.add( equalsTo(var.getKey(), String.valueOf(var.getValue())) ));
        List<String> potOwners = singletonList("salaboy");
        QueryContext queryContext = new QueryContext(0, 100);

        blackhole.consume(advanceRuntimeDataService.queryProcessByVariablesAndTask(attributes, processVariables, taskVariables, potOwners, queryContext));
    }

}
