package org.jboss.qa.brms.performance.localsearch.cloudbalance.heuristics;

import org.jboss.qa.brms.performance.configuration.AcceptorConfigurations;
import org.openjdk.jmh.annotations.Benchmark;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorConfig;

public class CloudBalanceHillClimbingSearchBenchmark extends AbstractCloudBalanceHeuristicBenchmark {

    @Override
    public AcceptorConfig createAcceptorConfig() {
        return AcceptorConfigurations.createHillClimbingAcceptor();
    }

    @Benchmark
    @Override
    public void benchmark() {
        super.benchmark();
    }
}
