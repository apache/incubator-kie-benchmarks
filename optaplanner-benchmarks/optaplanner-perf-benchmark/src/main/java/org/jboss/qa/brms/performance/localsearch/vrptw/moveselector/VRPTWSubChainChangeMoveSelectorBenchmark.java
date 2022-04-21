package org.jboss.qa.brms.performance.localsearch.vrptw.moveselector;

import org.jboss.qa.brms.performance.configuration.MoveSelectorConfigurations;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.openjdk.jmh.annotations.Benchmark;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;

import java.util.List;

public class VRPTWSubChainChangeMoveSelectorBenchmark extends AbstractVRPTWMoveSelectorBenchmark {

    @Override
    public List<MoveSelectorConfig> createMoveSelectorConfigList() {
        return MoveSelectorConfigurations.createSubChainChangeMoveSelectorList();
    }

    @Benchmark
    public VehicleRoutingSolution benchmark() {
        return runBenchmark();
    }
}
