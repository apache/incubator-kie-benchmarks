package org.jboss.qa.brms.performance.localsearch.cloudbalance.heuristics;

import org.jboss.qa.brms.performance.configuration.AcceptorConfigurations;
import org.openjdk.jmh.annotations.Benchmark;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorConfig;

public class CloudBalanceStepHillClimbingBenchmark extends AbstractCloudBalanceHeuristicBenchmark {

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
