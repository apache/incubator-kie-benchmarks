package org.jboss.qa.brms.performance.localsearch.tsp.moveselector;

import org.jboss.qa.brms.performance.calculatecounttermination.EasyTSPCalculateCountTermination;
import org.jboss.qa.brms.performance.configuration.MoveSelectorConfigurations;
import org.openjdk.jmh.annotations.Benchmark;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

import java.util.List;

public class TSPSubChainChangeMoveSelectorBenchmark extends AbstractTSPMoveSelectorBenchmark {

    @Override
    public List<MoveSelectorConfig> createMoveSelectorConfigList() {
        return MoveSelectorConfigurations.createSubChainChangeMoveSelectorList();
    }

    @Override
    public TerminationConfig getTerminationConfig() {
        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setTerminationClass(EasyTSPCalculateCountTermination.class);
        return terminationConfig;
    }

    @Benchmark
    @Override
    public void benchmark() {
        super.benchmark();
    }
}
