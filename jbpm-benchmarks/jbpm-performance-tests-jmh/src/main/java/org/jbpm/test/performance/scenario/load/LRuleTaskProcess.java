package org.jbpm.test.performance.scenario.load;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jbpm.test.performance.jbpm.JBPMController;
import org.jbpm.test.performance.jbpm.constant.ProcessStorage;
import org.jbpm.test.performance.jbpm.constant.RuleStorage;
import org.jbpm.test.performance.jbpm.model.UserFact;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-Xms2G", "-Xmx2G"})
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 1, time = 1)
@Threads(1)
public class LRuleTaskProcess {

    // ! Must be overridden using -p from command line
    @Param("")
    public String runtimeManagerStrategy;
    private JBPMController jc;

    @Setup
    public void init() {
        // Sets jvm argument to runtimeManagerStrategy
        System.setProperty("jbpm.runtimeManagerStrategy", runtimeManagerStrategy);
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

    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Benchmark
    public void sampleTime() {
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
