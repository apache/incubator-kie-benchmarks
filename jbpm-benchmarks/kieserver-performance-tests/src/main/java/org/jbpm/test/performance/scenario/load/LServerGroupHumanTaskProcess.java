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

public class LServerGroupHumanTaskProcess implements IPerfTest {

    private Timer startProcess;
    private Timer queryTaskDuration;
    private Timer claimTaskDuration;
    private Timer startTaskDuration;
    private Timer completeTaskDuration;
    private Meter completedProcess;
    
    private KieServerClient client;
    
    private ProcessServicesClient processClient;
    private UserTaskServicesClient taskClient;
    private QueryServicesClient queryClient;

    @Override
    public void init() {
        client = new KieServerClient(UserStorage.EngUser.getUserId(), UserStorage.EngUser.getPassword());
        processClient = client.getProcessClient();
        taskClient = client.getTaskClient();
        queryClient = client.getQueryClient();
    }

    @Override
    public void initMetrics() {
        MetricRegistry metrics = SharedMetricRegistry.getInstance();
        completedProcess = metrics.meter(MetricRegistry.name(LServerGroupHumanTaskProcess.class, "scenario.process.completed"));
        startProcess = metrics.timer(MetricRegistry.name(LServerGroupHumanTaskProcess.class, "scenario.process.start.duration"));
        queryTaskDuration = metrics.timer(MetricRegistry.name(LServerGroupHumanTaskProcess.class, "scenario.task.query.duration"));
        claimTaskDuration = metrics.timer(MetricRegistry.name(LServerGroupHumanTaskProcess.class, "scenario.task.claim.duration"));
        startTaskDuration = metrics.timer(MetricRegistry.name(LServerGroupHumanTaskProcess.class, "scenario.task.start.duration"));
        completeTaskDuration = metrics.timer(MetricRegistry.name(LServerGroupHumanTaskProcess.class, "scenario.task.complete.duration"));
    }

    @Override
    public void execute() {
        Timer.Context context;

        context = startProcess.time();
        Long pid = processClient.startProcess(KieServerClient.containerId, ProcessStorage.GroupHumanTask.getProcessDefinitionId());
        context.stop();

        context = queryTaskDuration.time();
        List<String> status = new ArrayList<String>();
        List<TaskSummary> tasks = taskClient.findTasksByStatusByProcessInstanceId(pid, status, 0, 1);
        Long taskSummaryId = tasks.get(0).getId();
        context.stop();

        context = claimTaskDuration.time();
        taskClient.claimTask(KieServerClient.containerId, taskSummaryId, UserStorage.EngUser.getUserId());
        context.stop();

        context = startTaskDuration.time();
        taskClient.startTask(KieServerClient.containerId, taskSummaryId, UserStorage.EngUser.getUserId());
        context.stop();

        context = completeTaskDuration.time();
        taskClient.completeTask(KieServerClient.containerId, taskSummaryId, UserStorage.EngUser.getUserId(), null);
        context.stop();

        ProcessInstance plog = queryClient.findProcessInstanceById(pid);

        if (plog != null && plog.getState() == org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED) {
            completedProcess.mark();
        }
    }

    @Override
    public void close() {

    }

}
