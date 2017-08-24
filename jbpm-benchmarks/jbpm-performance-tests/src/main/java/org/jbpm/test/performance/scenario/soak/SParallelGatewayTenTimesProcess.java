package org.jbpm.test.performance.scenario.soak;

import org.jbpm.test.performance.jbpm.JBPMController;
import org.jbpm.test.performance.jbpm.constant.ProcessStorage;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.perf.SharedMetricRegistry;
import org.kie.perf.scenario.IPerfTest;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

public class SParallelGatewayTenTimesProcess implements IPerfTest {

    private JBPMController jc;

    private Meter completedProcess;

    @Override
    public void init() {
        jc = JBPMController.getInstance();
        jc.clear();
        jc.setProcessEventListener(new DefaultProcessEventListener() {
            @Override
            public void afterProcessCompleted(ProcessCompletedEvent event) {
                completedProcess.mark();
            }
        });

        jc.createRuntimeManager(ProcessStorage.ParallelGatewayTenTimes.getPath());
    }

    @Override
    public void initMetrics() {
        MetricRegistry metrics = SharedMetricRegistry.getInstance();
        completedProcess = metrics.meter(MetricRegistry.name(SParallelGatewayTenTimesProcess.class, "scenario.process.completed"));
    }

    @Override
    public void execute() {
        RuntimeEngine runtimeEngine = jc.getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();
        ksession.startProcess(ProcessStorage.ParallelGatewayTenTimes.getProcessDefinitionId());
    }

    @Override
    public void close() {
        jc.tearDown();
    }

}
