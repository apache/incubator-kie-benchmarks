package org.jboss.qa.brms.performance.localsearch.vrp.moveselector;

import org.jboss.qa.brms.performance.calculatecounttermination.EasyVRPCalculateCountTermination;
import org.jboss.qa.brms.performance.configuration.MoveSelectorConfigurations;
import org.openjdk.jmh.annotations.Benchmark;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

import java.util.List;

public class VRPTailChainSwapMoveSelectorBenchmark extends AbstractVRPMoveSelectorBenchmark {

    @Override
    public List<MoveSelectorConfig> createMoveSelectorConfigList() {
        return MoveSelectorConfigurations.createTailChainSwapMoveSelectorList();
    }

    @Override
    public TerminationConfig getTerminationConfig() {
        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setTerminationClass(EasyVRPCalculateCountTermination.class);
        return terminationConfig;
    }

    @Benchmark
    @Override
    public void benchmark() {
        super.benchmark();
    }
}
