package org.jboss.qa.brms.performance.localsearch.vrptw.heuristics;

import org.jboss.qa.brms.performance.configuration.AcceptorConfigurations;
import org.openjdk.jmh.annotations.Benchmark;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorConfig;

public class VRPTWStepHillClimbingBenchmark extends AbstractVRPTWHeuristicBenchmark {

    @Override
    public AcceptorConfig createAcceptorConfig() {
        return AcceptorConfigurations.createStepCountHillClimbingAcceptor(50);
    }

    @Benchmark
    @Override
    public void benchmark() {
        super.benchmark();
    }
}
