package org.jbpm.test.performance.scenario.load;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jbpm.services.task.audit.TaskAuditServiceFactory;
import org.jbpm.services.task.audit.service.TaskAuditService;
import org.jbpm.test.performance.jbpm.JBPMController;
import org.jbpm.test.performance.jbpm.constant.UserStorage;
import org.jbpm.test.performance.scenario.PrepareEngine;
import org.jbpm.test.performance.test.common.AbstractJmhTest;
import org.kie.api.task.TaskService;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.task.api.AuditTask;
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

@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-Xms2G", "-Xmx2G"})
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 1, time = 1)
@Threads(1)
public class L1000HumanTasksQueryPagination extends AbstractJmhTest {

    // ! Must be overridden using -p from command line
    @Param("")
    public String runtimeManagerStrategy;
    private JBPMController jc;
    private TaskService taskService;
    private List<AuditTask> tasks = new ArrayList<AuditTask>();

    @Setup
    public void init() {
        // Sets jvm argument to runtimeManagerStrategy
        System.setProperty("jbpm.runtimeManagerStrategy", runtimeManagerStrategy);
        jc = JBPMController.getInstance();
        jc.createRuntimeManager();

        taskService = jc.getRuntimeEngine().getTaskService();

        PrepareEngine.createNewTasks(false, 10000, taskService, jc.getRuntimeManagerIdentifier());
    }

    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Benchmark
    public void Throughput() {
        execute();
    }

    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Benchmark
    public void sampleTime() {
        execute();
    }

    private void execute() {
        TaskAuditService taskAuditService = TaskAuditServiceFactory.newTaskAuditServiceConfigurator().setTaskService(taskService).getTaskAuditService();
        tasks = taskAuditService.getAllAuditTasksByUser(UserStorage.PerfUser.getUserId(), new QueryFilter(0, 100));
    }

    @TearDown
    public void close() {
        jc.tearDown();
    }
}
