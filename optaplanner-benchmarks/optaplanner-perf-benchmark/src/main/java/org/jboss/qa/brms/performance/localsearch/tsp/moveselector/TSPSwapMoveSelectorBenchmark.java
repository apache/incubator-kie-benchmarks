package org.jboss.qa.brms.performance.localsearch.tsp.moveselector;

import org.jboss.qa.brms.performance.configuration.MoveSelectorConfigurations;
import org.jboss.qa.brms.performance.examples.tsp.domain.TravelingSalesmanTour;
import org.openjdk.jmh.annotations.Benchmark;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;

import java.util.List;

public class TSPSwapMoveSelectorBenchmark extends AbstractTSPMoveSelectorBenchmark {

    @Override
    public List<MoveSelectorConfig> createMoveSelectorConfigList() {
        return MoveSelectorConfigurations.createSwapMoveSelectorList();
    }

    @Benchmark
    @Override
    public TravelingSalesmanTour benchmark() {
        return super.benchmark();
    }
}
