package org.jbpm.test.performance.scenario.load;

import java.util.List;

import org.jbpm.test.performance.jbpm.constant.ProcessStorage;
import org.jbpm.services.task.events.DefaultTaskEventListener;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskEvent;
import org.kie.api.task.TaskService;
import org.kie.perf.SharedMetricRegistry;
import org.jbpm.test.performance.jbpm.JBPMController;
import org.jbpm.test.performance.jbpm.constant.UserStorage;
import org.kie.perf.scenario.IPerfTest;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

public class LHumanTaskProcessWithListeners implements IPerfTest {

    private JBPMController jc;

    private Timer startProcess;
    private Timer startTaskDuration;
    private Timer completeTaskDuration;
    private Meter completedProcess;

    private Meter taskStarted;
    private Meter taskCompleted;

    @Override
    public void init() {
        jc = JBPMController.getInstance();
        jc.setProcessEventListener(new DefaultProcessEventListener() {
            @Override
            public void afterProcessCompleted(ProcessCompletedEvent event) {
                completedProcess.mark();
            }
        });
        jc.setTaskEventListener(new DefaultTaskEventListener() {
        	@Override
        	public void afterTaskStartedEvent(TaskEvent event) {
        		taskStarted.mark();
        	}
        	
        	@Override
        	public void afterTaskCompletedEvent(TaskEvent event) {
        		taskCompleted.mark();
        	}
        });

        jc.createRuntimeManager(ProcessStorage.HumanTask.getPath());
    }

    @Override
    public void initMetrics() {
        MetricRegistry metrics = SharedMetricRegistry.getInstance();
        startProcess = metrics.timer(MetricRegistry.name(LHumanTaskProcessWithListeners.class, "scenario.process.start.duration"));
        startTaskDuration = metrics.timer(MetricRegistry.name(LHumanTaskProcessWithListeners.class, "scenario.task.start.duration"));
        completeTaskDuration = metrics.timer(MetricRegistry.name(LHumanTaskProcessWithListeners.class, "scenario.task.complete.duration"));
        completedProcess = metrics.meter(MetricRegistry.name(LHumanTaskProcessWithListeners.class, "scenario.process.completed"));
        taskStarted = metrics.meter(MetricRegistry.name(L1000HumanTasksStart.class, "scenario.task.started"));
        taskCompleted = metrics.meter(MetricRegistry.name(L1000HumanTasksComplete.class, "scenario.task.completed"));
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
