package org.jbpm.test.performance.scenario.load;

import java.util.concurrent.TimeUnit;

import org.jbpm.test.performance.jbpm.JBPMController;
import org.jbpm.test.performance.jbpm.constant.ProcessStorage;
import org.jbpm.test.performance.jbpm.wih.ManualTaskWorkItemHandler;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
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
public class LParallelGatewayTwoTimesProcess {

    // ! Must be overridden using -p from command line
    @Param("")
    public String runtimeManagerStrategy;
    private JBPMController jc;
    private RuntimeManager manager;

    @State(Scope.Thread)
    public static class ThreadScope {

        private RuntimeEngine runtimeEngine;

        @TearDown(Level.Invocation)
        public void close(LParallelGatewayTwoTimesProcess lParallelGatewayTwoTimesProcess) {
            lParallelGatewayTwoTimesProcess.manager.disposeRuntimeEngine(runtimeEngine);
        }
    }

    @Setup(Level.Iteration)
    public void init() {
        // Sets jvm argument to runtimeManagerStrategy
        System.setProperty("jbpm.runtimeManagerStrategy", runtimeManagerStrategy);
        jc = JBPMController.getInstance();
        jc.addWorkItemHandler("Manual Task", new ManualTaskWorkItemHandler());
        manager = jc.createRuntimeManager(ProcessStorage.ParallelGatewayTwoTimes.getPath());

        // It seems that something is not thread safe and this initialization helps to prevent null-pointer exception.
        RuntimeEngine runtimeEngine = jc.getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();
        manager.disposeRuntimeEngine(runtimeEngine);
    }

    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Benchmark
    public void Throughput(ThreadScope threadScope) {
        execute(threadScope);
    }

    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Benchmark
    public void sampleTime(ThreadScope threadScope) {
        execute(threadScope);
    }

    private void execute(ThreadScope threadScope) {
        threadScope.runtimeEngine = jc.getRuntimeEngine();
        KieSession ksession = threadScope.runtimeEngine.getKieSession();
        ksession.startProcess(ProcessStorage.ParallelGatewayTwoTimes.getProcessDefinitionId());
    }

    @TearDown(Level.Iteration)
    public void close() {
        jc.tearDown();
    }
}
