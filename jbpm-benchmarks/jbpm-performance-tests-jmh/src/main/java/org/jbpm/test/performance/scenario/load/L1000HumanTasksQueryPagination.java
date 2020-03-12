package org.jbpm.test.performance.scenario.load;

import org.jbpm.services.task.audit.TaskAuditServiceFactory;
import org.jbpm.services.task.audit.service.TaskAuditService;
import org.jbpm.test.performance.jbpm.JBPMController;
import org.jbpm.test.performance.jbpm.constant.UserStorage;
import org.jbpm.test.performance.scenario.PrepareEngine;
import org.kie.api.task.TaskService;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.task.api.AuditTask;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-Xms2G", "-Xmx2G"})
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 1, time = 5)
@Threads(1)

public class L1000HumanTasksQueryPagination {

    private JBPMController jc;

    private TaskService taskService;

    private List<AuditTask> tasks = new ArrayList<AuditTask>();

    @Setup
    public void init() {
        jc = JBPMController.getInstance();
        jc.createRuntimeManager();

        taskService = jc.getRuntimeEngine().getTaskService();

        PrepareEngine.createNewTasks(false, 5000, taskService, jc.getRuntimeManagerIdentifier());
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
        TaskAuditService taskAuditService = TaskAuditServiceFactory.newTaskAuditServiceConfigurator().setTaskService(taskService).getTaskAuditService();
        tasks = taskAuditService.getAllAuditTasksByUser(UserStorage.PerfUser.getUserId(), new QueryFilter(0, 100));
    }

    @TearDown
    public void close() {
        jc.tearDown();
    }

}
