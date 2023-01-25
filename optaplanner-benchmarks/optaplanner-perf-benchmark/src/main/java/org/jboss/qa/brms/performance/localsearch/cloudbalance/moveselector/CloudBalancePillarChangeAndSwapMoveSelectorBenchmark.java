package org.jboss.qa.brms.performance.localsearch.cloudbalance.moveselector;

import org.jboss.qa.brms.performance.configuration.MoveSelectorConfigurations;
import org.openjdk.jmh.annotations.Benchmark;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;

import java.util.List;

public class CloudBalancePillarChangeAndSwapMoveSelectorBenchmark extends AbstractCloudBalanceMoveSelectorBenchmark {
    @Override
    public List<MoveSelectorConfig> createMoveSelectorConfigList() {
        return MoveSelectorConfigurations.createMultiPilarSelectorList();
    }
    @Override
    public int getAcceptedCountLimit() {
        //to check that pillar move selector caches on each new step
        return 100;
    }

    @Benchmark
    public CloudBalance benchmark() {
        return runBenchmark();
    }

}
