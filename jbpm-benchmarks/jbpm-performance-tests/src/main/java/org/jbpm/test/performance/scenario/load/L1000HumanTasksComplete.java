package org.jbpm.test.performance.scenario.load;

import java.util.List;

import org.jbpm.test.performance.jbpm.JBPMController;
import org.jbpm.test.performance.jbpm.constant.UserStorage;
import org.jbpm.test.performance.scenario.PrepareEngine;
import org.jbpm.services.task.events.DefaultTaskEventListener;
import org.kie.api.task.TaskEvent;
import org.kie.api.task.TaskService;
import org.kie.perf.SharedMetricRegistry;
import org.kie.perf.annotation.KPKLimit;
import org.kie.perf.scenario.IPerfTest;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

@KPKLimit(1000)
public class L1000HumanTasksComplete implements IPerfTest {

    private JBPMController jc;

    private TaskService taskService;

    private Meter taskCompleted;

    private List<Long> taskIds;

    @Override
    public void init() {

        jc = JBPMController.getInstance();

        jc.setTaskEventListener(new DefaultTaskEventListener() {
            @Override
            public void afterTaskCompletedEvent(TaskEvent event) {
                taskCompleted.mark();
            }
        });

        jc.createRuntimeManager();

        taskService = jc.getRuntimeEngine().getTaskService();

    }

    @Override
    public void initMetrics() {
        taskId = 0;
        taskIds = PrepareEngine.createNewTasks(true, 1000, taskService);
        
        MetricRegistry metrics = SharedMetricRegistry.getInstance();
        taskCompleted = metrics.meter(MetricRegistry.name(L1000HumanTasksComplete.class, "scenario.task.completed"));
    }

    static int taskId = 0;

    @Override
    public void execute() {
        Long tid = taskIds.get(taskId);
        taskService.complete(tid, UserStorage.PerfUser.getUserId(), null);
        taskId++;
    }

    @Override
    public void close() {
        jc.tearDown();
    }

}
