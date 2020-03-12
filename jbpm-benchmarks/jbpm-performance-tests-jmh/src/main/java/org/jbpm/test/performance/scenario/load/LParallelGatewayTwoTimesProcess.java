package org.jbpm.test.performance.scenario.load;

import org.jbpm.test.performance.jbpm.JBPMController;
import org.jbpm.test.performance.jbpm.constant.ProcessStorage;
import org.jbpm.test.performance.jbpm.wih.ManualTaskWorkItemHandler;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-Xms2G", "-Xmx2G"})
@Warmup(iterations = 1, time = 30)
@Measurement(iterations = 1, time = 30)
@Threads(1)

public class LParallelGatewayTwoTimesProcess {

    private JBPMController jc;

    @Setup
    public void init() {
        jc = JBPMController.getInstance();
        jc.addWorkItemHandler("Manual Task", new ManualTaskWorkItemHandler());
        jc.createRuntimeManager(ProcessStorage.ParallelGatewayTwoTimes.getPath());
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
        ksession.startProcess(ProcessStorage.ParallelGatewayTwoTimes.getProcessDefinitionId());
    }

    @TearDown
    public void close() {
        jc.tearDown();
    }

}
