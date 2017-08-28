package org.jbpm.test.performance.scenario.load;

import java.util.List;

import org.jbpm.test.performance.jbpm.JBPMController;
import org.jbpm.test.performance.jbpm.constant.ProcessStorage;
import org.jbpm.test.performance.jbpm.constant.UserStorage;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.perf.SharedMetricRegistry;
import org.kie.perf.scenario.IPerfTest;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

public class LGroupHumanTaskProcess implements IPerfTest {

    private JBPMController jc;

    private Timer startProcess;
    private Timer queryTaskDuration;
    private Timer claimTaskDuration;
    private Timer startTaskDuration;
    private Timer completeTaskDuration;
    private Meter completedProcess;

    @Override
    public void init() {

        jc = JBPMController.getInstance();
        jc.setProcessEventListener(new DefaultProcessEventListener() {
            @Override
            public void afterProcessCompleted(ProcessCompletedEvent event) {
                completedProcess.mark();
            }
        });

        jc.createRuntimeManager(ProcessStorage.GroupHumanTask.getPath());
    }

    @Override
    public void initMetrics() {
        MetricRegistry metrics = SharedMetricRegistry.getInstance();
        completedProcess = metrics.meter(MetricRegistry.name(LGroupHumanTaskProcess.class, "scenario.process.completed"));
        startProcess = metrics.timer(MetricRegistry.name(LGroupHumanTaskProcess.class, "scenario.process.start.duration"));
        queryTaskDuration = metrics.timer(MetricRegistry.name(LGroupHumanTaskProcess.class, "scenario.task.query.duration"));
        claimTaskDuration = metrics.timer(MetricRegistry.name(LGroupHumanTaskProcess.class, "scenario.task.claim.duration"));
        startTaskDuration = metrics.timer(MetricRegistry.name(LGroupHumanTaskProcess.class, "scenario.task.start.duration"));
        completeTaskDuration = metrics.timer(MetricRegistry.name(LGroupHumanTaskProcess.class, "scenario.task.complete.duration"));
    }

    @Override
    public void execute() {
        Timer.Context context;

        context = startProcess.time();
        RuntimeEngine runtimeEngine = jc.getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();
        ProcessInstance pi = ksession.startProcess(ProcessStorage.GroupHumanTask.getProcessDefinitionId());
        context.stop();

        context = queryTaskDuration.time();
        TaskService taskService = runtimeEngine.getTaskService();
        List<Long> tasks = taskService.getTasksByProcessInstanceId(pi.getId());
        Long taskSummaryId = tasks.get(0);
        context.stop();

        context = claimTaskDuration.time();
        taskService.claim(taskSummaryId, UserStorage.EngUser.getUserId());
        context.stop();

        context = startTaskDuration.time();
        taskService.start(taskSummaryId, UserStorage.EngUser.getUserId());
        context.stop();

        context = completeTaskDuration.time();
        taskService.complete(taskSummaryId, UserStorage.EngUser.getUserId(), null);
        context.stop();
    }

    @Override
    public void close() {
        jc.tearDown();
    }

}
