package org.jbpm.test.performance.scenario.load;

import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.perf.SharedMetricRegistry;
import org.jbpm.test.performance.jbpm.JBPMController;
import org.jbpm.test.performance.jbpm.constant.ProcessStorage;
import org.kie.perf.scenario.IPerfTest;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

public class LIntermediateSignalProcess implements IPerfTest {

    private JBPMController jc;

    private Timer startProcess;
    private Timer signalDuration;
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

        jc.createRuntimeManager(ProcessStorage.IntermediateSignal.getPath());
    }

    @Override
    public void initMetrics() {
        MetricRegistry metrics = SharedMetricRegistry.getInstance();
        completedProcess = metrics.meter(MetricRegistry.name(LIntermediateSignalProcess.class, "scenario.process.completed"));
        startProcess = metrics.timer(MetricRegistry.name(LIntermediateSignalProcess.class, "scenario.process.start.duration"));
        signalDuration = metrics.timer(MetricRegistry.name(LIntermediateSignalProcess.class, "scenario.signal.duration"));
    }

    @Override
    public void execute() {
        Timer.Context context;

        context = startProcess.time();
        RuntimeEngine runtimeEngine = jc.getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();
        ProcessInstance pi = ksession.startProcess(ProcessStorage.IntermediateSignal.getProcessDefinitionId());
        context.stop();

        context = signalDuration.time();
        ksession.signalEvent("MySignal", "value", pi.getId());
        context.stop();
    }

    @Override
    public void close() {
        jc.tearDown();
    }

}
