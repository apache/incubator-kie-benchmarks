package org.jboss.qa.brms.performance.localsearch.cloudbalance.heuristics;

import org.jboss.qa.brms.performance.configuration.AcceptorConfigurations;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.openjdk.jmh.annotations.Benchmark;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorConfig;

public class CloudBalanceStepHillClimbingBenchmark extends AbstractCloudBalanceHeuristicBenchmark {

    @Override
    public AcceptorConfig createAcceptorConfig() {
        return AcceptorConfigurations.createStepCountHillClimbingAcceptor(50);
    }

    @Benchmark
    public CloudBalance benchmark() {
        return runBenchmark();
    }
}
