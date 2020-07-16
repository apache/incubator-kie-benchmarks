package org.jbpm.test.performance.scenario.load;

import java.util.concurrent.TimeUnit;

import org.jbpm.test.performance.jbpm.JBPMController;
import org.jbpm.test.performance.jbpm.constant.ProcessStorage;
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
public class L1000ProcessesSignal {

    // ! Must be overridden using -p from command line
    @Param("")
    public String runtimeManagerStrategy;
    private JBPMController jc;

    @Setup
    public void init() {
        // Sets jvm argument to runtimeManagerStrategy
        System.setProperty("jbpm.runtimeManagerStrategy", runtimeManagerStrategy);
        jc = JBPMController.getInstance();
        jc.createRuntimeManager(ProcessStorage.IntermediateSignal.getPath());
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
        KieSession ksession = null;
        for (int i = 0; i < 1000; ++i) {
            RuntimeEngine runtimeEngine = jc.getRuntimeEngine();
            ksession = runtimeEngine.getKieSession();
            ksession.startProcess(ProcessStorage.IntermediateSignal.getProcessDefinitionId());
        }

        ksession.signalEvent("MySignal", "value");
    }

    @TearDown
    public void close() {
        jc.tearDown();
    }
}
