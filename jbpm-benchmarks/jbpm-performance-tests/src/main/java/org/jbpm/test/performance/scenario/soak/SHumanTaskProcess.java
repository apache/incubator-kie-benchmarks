package org.jbpm.test.performance.scenario.soak;

import java.util.List;

import org.jbpm.test.performance.jbpm.JBPMController;
import org.jbpm.test.performance.jbpm.constant.ProcessStorage;
import org.jbpm.test.performance.jbpm.constant.UserStorage;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.perf.SharedMetricRegistry;
import org.kie.perf.scenario.IPerfTest;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

public class SHumanTaskProcess implements IPerfTest {

    private JBPMController jc;

    private Timer startProcess;
    private Timer startTaskDuration;
    private Timer completeTaskDuration;

    @Override
    public void init() {
        jc = JBPMController.getInstance();
        jc.clear();
        jc.createRuntimeManager(ProcessStorage.HumanTask.getPath());
    }

    @Override
    public void initMetrics() {
        MetricRegistry metrics = SharedMetricRegistry.getInstance();
        startProcess = metrics.timer(MetricRegistry.name(SHumanTaskProcess.class, "scenario.process.start.duration"));
        startTaskDuration = metrics.timer(MetricRegistry.name(SHumanTaskProcess.class, "scenario.task.start.duration"));
        completeTaskDuration = metrics.timer(MetricRegistry.name(SHumanTaskProcess.class, "scenario.task.complete.duration"));
    }

    @Override
    public void execute() {
        Timer.Context context;

        context = startProcess.time();
        RuntimeEngine runtimeEngine = jc.getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();
        ProcessInstance pi = ksession.startProcess(ProcessStorage.HumanTask.getProcessDefinitionId());
        context.stop();

        TaskService taskService = runtimeEngine.getTaskService();
        List<Long> tasks = taskService.getTasksByProcessInstanceId(pi.getId());
        Long taskSummaryId = tasks.get(0);

        context = startTaskDuration.time();
        taskService.start(taskSummaryId, UserStorage.PerfUser.getUserId());
        context.stop();

        context = completeTaskDuration.time();
        taskService.complete(taskSummaryId, UserStorage.PerfUser.getUserId(), null);
        context.stop();
    }

    @Override
    public void close() {
        jc.tearDown();
    }

}
