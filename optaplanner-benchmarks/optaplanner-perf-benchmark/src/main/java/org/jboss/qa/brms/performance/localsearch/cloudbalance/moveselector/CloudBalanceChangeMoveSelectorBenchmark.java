package org.jboss.qa.brms.performance.localsearch.cloudbalance.moveselector;

import org.jboss.qa.brms.performance.configuration.MoveSelectorConfigurations;
import org.jboss.qa.brms.performance.examples.cloudbalancing.domain.CloudBalance;
import org.openjdk.jmh.annotations.Benchmark;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;

import java.util.List;

public class CloudBalanceChangeMoveSelectorBenchmark extends AbstractCloudBalanceMoveSelectorBenchmark {

    @Override
    public List<MoveSelectorConfig> createMoveSelectorConfigList() {
        return MoveSelectorConfigurations.createChangeMoveSelectorList();
    }

    @Benchmark
    @Override
    public CloudBalance benchmark() {
        return super.benchmark();
    }
}
