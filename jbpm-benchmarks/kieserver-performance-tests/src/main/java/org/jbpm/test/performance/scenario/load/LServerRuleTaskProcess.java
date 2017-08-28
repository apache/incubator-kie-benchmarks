package org.jbpm.test.performance.scenario.load;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.test.performance.jbpm.model.UserFact;
import org.jbpm.test.performance.kieserver.KieServerClient;
import org.jbpm.test.performance.kieserver.constant.ProcessStorage;
import org.kie.perf.SharedMetricRegistry;
import org.kie.perf.scenario.IPerfTest;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

public class LServerRuleTaskProcess implements IPerfTest {

    private Meter completedProcess;

    private KieServerClient client;
    
    private ProcessServicesClient processClient;
    private QueryServicesClient queryClient;

    @Override
    public void init() {
    	client = new KieServerClient(UserFact.class);
    	processClient = client.getProcessClient();
        queryClient = client.getQueryClient();
    }

    @Override
    public void initMetrics() {
        MetricRegistry metrics = SharedMetricRegistry.getInstance();
        completedProcess = metrics.meter(MetricRegistry.name(LServerRuleTaskProcess.class, "scenario.process.completed"));
    }

    @Override
    public void execute() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user", new UserFact("user", 15));
        Long pid = processClient.startProcess(KieServerClient.containerId, ProcessStorage.RuleTask.getProcessDefinitionId(), params);
        ProcessInstance pi = queryClient.findProcessInstanceById(pid);
        if (pi != null && pi.getState() == org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED) {
            completedProcess.mark();
        }
    }

    @Override
    public void close() {

    }

}
