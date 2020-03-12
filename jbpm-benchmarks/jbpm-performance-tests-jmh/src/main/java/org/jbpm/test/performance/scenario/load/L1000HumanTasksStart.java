package org.jbpm.test.performance.scenario.load;

import org.jbpm.test.performance.jbpm.JBPMController;
import org.jbpm.test.performance.jbpm.constant.UserStorage;
import org.jbpm.test.performance.scenario.PrepareEngine;
import org.kie.api.task.TaskService;
import org.openjdk.jmh.annotations.*;

import java.util.List;
import java.util.concurrent.TimeUnit;


@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-Xms2G", "-Xmx2G"})
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 1, time = 5)
@Threads(1)


public class L1000HumanTasksStart {

    private JBPMController jc;

    private TaskService taskService;

    private List<Long> taskIds;

    @Setup
    public void init() {
        jc = JBPMController.getInstance();
        jc.createRuntimeManager();

        taskService = jc.getRuntimeEngine().getTaskService();

        taskId = 0;
        taskIds = PrepareEngine.createNewTasks(false, 5000, taskService, jc.getRuntimeManagerIdentifier());
    }


    static int taskId = 0;


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
        Long tid = taskIds.get(taskId);
        taskService.start(tid, UserStorage.PerfUser.getUserId());
        taskId++;
    }

    @TearDown
    public void close() {
        jc.tearDown();
    }

}
