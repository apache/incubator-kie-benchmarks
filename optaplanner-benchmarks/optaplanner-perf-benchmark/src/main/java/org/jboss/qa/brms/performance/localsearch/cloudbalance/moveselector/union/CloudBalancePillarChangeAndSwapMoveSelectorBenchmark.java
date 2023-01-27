package org.jboss.qa.brms.performance.localsearch.cloudbalance.moveselector.union;

import org.jboss.qa.brms.performance.configuration.MoveSelectorConfigurations;
import org.openjdk.jmh.annotations.Benchmark;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;

import java.util.List;

public class CloudBalancePillarChangeAndSwapMoveSelectorBenchmark extends AbstractCloudBalanceMoveSelectorUnionBenchmark {
    @Override
    public List<MoveSelectorConfig> createMoveSelectorConfigList() {
        return MoveSelectorConfigurations.createMultiPilarSelectorList();
    }

    @Benchmark
    public CloudBalance benchmark() {
        return runBenchmark();
    }

}
