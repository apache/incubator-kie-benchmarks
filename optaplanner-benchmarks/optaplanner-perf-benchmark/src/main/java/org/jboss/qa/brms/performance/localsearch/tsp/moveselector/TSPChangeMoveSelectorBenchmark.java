package org.jboss.qa.brms.performance.localsearch.tsp.moveselector;

import org.jboss.qa.brms.performance.configuration.MoveSelectorConfigurations;
import org.optaplanner.examples.tsp.domain.TspSolution;
import org.openjdk.jmh.annotations.Benchmark;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;

import java.util.List;

public class TSPChangeMoveSelectorBenchmark extends AbstractTSPMoveSelectorBenchmark {

    @Override
    public List<MoveSelectorConfig> createMoveSelectorConfigList() {
        return MoveSelectorConfigurations.createChangeMoveSelectorList();
    }

    @Benchmark
    public TspSolution benchmark() {
        return runBenchmark();
    }

}
