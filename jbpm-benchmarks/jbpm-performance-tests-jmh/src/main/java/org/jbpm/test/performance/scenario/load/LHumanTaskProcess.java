package org.jbpm.test.performance.scenario.load;

import org.jbpm.test.performance.jbpm.JBPMController;
import org.jbpm.test.performance.jbpm.constant.ProcessStorage;
import org.jbpm.test.performance.jbpm.constant.UserStorage;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.openjdk.jmh.annotations.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-Xms2G", "-Xmx2G"})
@Warmup(iterations = 1, time = 30)
@Measurement(iterations = 1, time = 30)
@Threads(1)

public class LHumanTaskProcess {

    private JBPMController jc;

    @Setup
    public void init() {
        jc = JBPMController.getInstance();
        jc.createRuntimeManager(ProcessStorage.HumanTask.getPath());

        // Used to set up H2 db in single thread.
        execute();
    }

    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Benchmark
    public void Throughput() {
        execute();
    }

    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Benchmark
    public void averageTime() {
        execute();
    }

    private void execute() {
        RuntimeEngine runtimeEngine = jc.getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();
        ProcessInstance pi = ksession.startProcess(ProcessStorage.HumanTask.getProcessDefinitionId());

        TaskService taskService = runtimeEngine.getTaskService();
        List<Long> tasks = taskService.getTasksByProcessInstanceId(pi.getId());
        Long taskSummaryId = tasks.get(0);

        taskService.start(taskSummaryId, UserStorage.PerfUser.getUserId());

        taskService.complete(taskSummaryId, UserStorage.PerfUser.getUserId(), null);

    }

    @TearDown
    public void close() {
        jc.tearDown();
    }

}
