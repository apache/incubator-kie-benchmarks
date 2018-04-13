package org.jboss.qa.brms.performance.localsearch.cloudbalance.moveselector;

import org.jboss.qa.brms.performance.configuration.MoveSelectorConfigurations;
import org.openjdk.jmh.annotations.Benchmark;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;

import java.util.List;

public class CloudBalancePillarChangeMoveSelectorBenchmark extends AbstractCloudBalanceMoveSelectorBenchmark {

    @Override
    public List<MoveSelectorConfig> createMoveSelectorConfigList() {
        return MoveSelectorConfigurations.createPillarChangeMoveSelectorList();
    }

    @Benchmark
    @Override
    public void benchmark() {
        super.benchmark();
    }
}
