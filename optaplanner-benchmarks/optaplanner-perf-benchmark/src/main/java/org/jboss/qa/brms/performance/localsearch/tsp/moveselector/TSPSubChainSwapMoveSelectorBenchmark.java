package org.jboss.qa.brms.performance.localsearch.tsp.moveselector;

import org.jboss.qa.brms.performance.examples.tsp.termination.EasyTSPCalculateCountTermination;
import org.jboss.qa.brms.performance.configuration.MoveSelectorConfigurations;
import org.optaplanner.examples.tsp.domain.TspSolution;
import org.openjdk.jmh.annotations.Benchmark;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

import java.util.List;

public class TSPSubChainSwapMoveSelectorBenchmark extends AbstractTSPMoveSelectorBenchmark {

    @Override
    public List<MoveSelectorConfig> createMoveSelectorConfigList() {
        return MoveSelectorConfigurations.createSubChainSwapMoveSelectorList();
    }

    @Override
    public TerminationConfig getTerminationConfig() {
        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setTerminationClass(EasyTSPCalculateCountTermination.class);
        return terminationConfig;
    }

    @Benchmark
    public TspSolution benchmark() {
        return runBenchmark();
    }
}
