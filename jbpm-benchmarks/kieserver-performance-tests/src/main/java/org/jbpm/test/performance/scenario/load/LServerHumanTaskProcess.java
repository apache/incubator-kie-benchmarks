package org.jbpm.test.performance.scenario.load;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.test.performance.kieserver.KieServerClient;
import org.jbpm.test.performance.kieserver.constant.UserStorage;
import org.kie.perf.SharedMetricRegistry;
import org.jbpm.test.performance.kieserver.constant.ProcessStorage;
import org.kie.perf.scenario.IPerfTest;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.api.model.instance.TaskSummary;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;
import org.kie.server.client.UserTaskServicesClient;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

public class LServerHumanTaskProcess implements IPerfTest {

    private Timer startProcess;
    private Timer queryTaskDuration;
    private Timer startTaskDuration;
    private Timer completeTaskDuration;
    private Timer queryProcessInstanceDuration;
    private Meter completedProcess;
    
    private KieServerClient client;
    
    private ProcessServicesClient processClient;
    private UserTaskServicesClient taskClient;
    private QueryServicesClient queryClient;

    @Override
    public void init() {
    	client = new KieServerClient();
    	processClient = client.getProcessClient();
        taskClient = client.getTaskClient();
        queryClient = client.getQueryClient();
    }

    @Override
    public void initMetrics() {
        MetricRegistry metrics = SharedMetricRegistry.getInstance();
        startProcess = metrics.timer(MetricRegistry.name(LServerHumanTaskProcess.class, "scenario.process.start.duration"));
        queryTaskDuration = metrics.timer(MetricRegistry.name(LServerGroupHumanTaskProcess.class, "scenario.task.query.duration"));
        startTaskDuration = metrics.timer(MetricRegistry.name(LServerHumanTaskProcess.class, "scenario.task.start.duration"));
        completeTaskDuration = metrics.timer(MetricRegistry.name(LServerHumanTaskProcess.class, "scenario.task.complete.duration"));
        queryProcessInstanceDuration = metrics.timer(MetricRegistry.name(LServerHumanTaskProcess.class, "scenario.process.query.duration"));
        completedProcess = metrics.meter(MetricRegistry.name(LServerHumanTaskProcess.class, "scenario.process.completed"));
    }

    @Override
    public void execute() {
        Timer.Context context;
        
        context = startProcess.time();
        long pid = processClient.startProcess(KieServerClient.containerId, ProcessStorage.HumanTask.getProcessDefinitionId());
        context.stop();

        context = queryTaskDuration.time();
        List<String> status = new ArrayList<String>();
        List<TaskSummary> tasks = taskClient.findTasksByStatusByProcessInstanceId(pid, status, 0, 1);
        Long taskSummaryId = tasks.get(0).getId();
        context.stop();

        context = startTaskDuration.time();
        taskClient.startTask(KieServerClient.containerId, taskSummaryId, UserStorage.PerfUser.getUserId());
        context.stop();

        context = completeTaskDuration.time();
        taskClient.completeTask(KieServerClient.containerId, taskSummaryId, UserStorage.PerfUser.getUserId(), null);
        context.stop();

        context = queryProcessInstanceDuration.time();
        ProcessInstance plog = queryClient.findProcessInstanceById(pid);
        context.stop();

        if (plog != null && plog.getState() == org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED) {
            completedProcess.mark();
        }
    }

    @Override
    public void close() {
    	
    }

}
