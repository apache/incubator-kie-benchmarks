package org.jboss.qa.brms.performance.localsearch.cloudbalance.moveselector;

import org.jboss.qa.brms.performance.configuration.MoveSelectorConfigurations;
import org.openjdk.jmh.annotations.Benchmark;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;

import java.util.List;

public class CloudBalanceSwapMoveSelectorBenchmark extends AbstractCloudBalanceMoveSelectorBenchmark {

    @Override
    public List<MoveSelectorConfig> createMoveSelectorConfigList() {
        return MoveSelectorConfigurations.createSwapMoveSelectorList();
    }

//    @Override
//    public int getAcceptedCountLimit() {
//        return 1000;
//    }
//    @Override
//    public int getNumOfStepsTermination() {
//        return 100;
//    }
    @Benchmark
    @Override
    public void benchmark() {
        super.benchmark();
    }
}
