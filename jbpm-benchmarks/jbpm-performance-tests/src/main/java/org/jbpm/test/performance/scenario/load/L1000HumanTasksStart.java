package org.jbpm.test.performance.scenario.load;

import java.util.List;

import org.jbpm.services.task.events.DefaultTaskEventListener;
import org.kie.api.task.TaskEvent;
import org.kie.api.task.TaskService;
import org.kie.perf.SharedMetricRegistry;
import org.kie.perf.annotation.KPKLimit;
import org.jbpm.test.performance.jbpm.JBPMController;
import org.jbpm.test.performance.jbpm.constant.UserStorage;
import org.kie.perf.scenario.IPerfTest;
import org.jbpm.test.performance.scenario.PrepareEngine;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

@KPKLimit(1000)
public class L1000HumanTasksStart implements IPerfTest {

    private JBPMController jc;

    private TaskService taskService;

    private Meter taskStarted;

    private List<Long> taskIds;

    @Override
    public void init() {
        jc = JBPMController.getInstance();

        jc.setTaskEventListener(new DefaultTaskEventListener() {
            @Override
            public void afterTaskStartedEvent(TaskEvent event) {
                taskStarted.mark();
            }
        });

        jc.createRuntimeManager();

        taskService = jc.getRuntimeEngine().getTaskService();
    }

    @Override
    public void initMetrics() {
        taskId = 0;
        taskIds = PrepareEngine.createNewTasks(false, 1000, taskService);
        
        MetricRegistry metrics = SharedMetricRegistry.getInstance();
        taskStarted = metrics.meter(MetricRegistry.name(L1000HumanTasksStart.class, "scenario.task.started"));
    }

    static int taskId = 0;

    @Override
    public void execute() {
        Long tid = taskIds.get(taskId);
        taskService.start(tid, UserStorage.PerfUser.getUserId());
        taskId++;
    }

    @Override
    public void close() {
        jc.tearDown();
    }

}
