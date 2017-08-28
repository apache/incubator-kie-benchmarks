package org.jbpm.test.performance.scenario.load;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.test.performance.jbpm.constant.ProcessStorage;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.perf.SharedMetricRegistry;
import org.jbpm.test.performance.jbpm.JBPMController;
import org.jbpm.test.performance.jbpm.constant.RuleStorage;
import org.jbpm.test.performance.jbpm.model.UserFact;
import org.kie.perf.scenario.IPerfTest;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

public class LRuleTaskProcess implements IPerfTest {

    private JBPMController jc;

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

        Map<String, ResourceType> res = new HashMap<String, ResourceType>();
        res.put(ProcessStorage.RuleTask.getPath(), ResourceType.BPMN2);
        res.put(RuleStorage.ValidationUserFact.getPath(), ResourceType.DRL);
        jc.createRuntimeManager(res);
    }

    @Override
    public void initMetrics() {
        MetricRegistry metrics = SharedMetricRegistry.getInstance();
        completedProcess = metrics.meter(MetricRegistry.name(LRuleTaskProcess.class, "scenario.process.completed"));
    }

    @Override
    public void execute() {
        RuntimeEngine runtimeEngine = jc.getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user", new UserFact("user", 15));
        ksession.startProcess(ProcessStorage.RuleTask.getProcessDefinitionId(), params);
    }

    @Override
    public void close() {
        jc.tearDown();
    }

}
