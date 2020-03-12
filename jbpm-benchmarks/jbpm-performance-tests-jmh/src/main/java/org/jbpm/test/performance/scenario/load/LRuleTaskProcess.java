package org.jbpm.test.performance.scenario.load;

import org.jbpm.test.performance.jbpm.JBPMController;
import org.jbpm.test.performance.jbpm.constant.ProcessStorage;
import org.jbpm.test.performance.jbpm.constant.RuleStorage;
import org.jbpm.test.performance.jbpm.model.UserFact;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.openjdk.jmh.annotations.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-Xms2G", "-Xmx2G"})
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 1, time = 300)
@Threads(1)

public class LRuleTaskProcess {

    private JBPMController jc;

    @Setup
    public void init() {
        jc = JBPMController.getInstance();

        Map<String, ResourceType> res = new HashMap<String, ResourceType>();
        res.put(ProcessStorage.RuleTask.getPath(), ResourceType.BPMN2);
        res.put(RuleStorage.ValidationUserFact.getPath(), ResourceType.DRL);
        jc.createRuntimeManager(res);
    }

    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Benchmark
    public void Throughput() {
        execute();
    }

    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Benchmark
    public void averageTime() {
        execute();
    }


    private void execute() {
        RuntimeEngine runtimeEngine = jc.getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user", new UserFact("user", 15));
        ksession.startProcess(ProcessStorage.RuleTask.getProcessDefinitionId(), params);
    }

    @TearDown
    public void close() {
        jc.tearDown();
    }

}
