package org.jboss.qa.brms.performance.localsearch.projectjobscheduling.moveselector;

import org.openjdk.jmh.annotations.Benchmark;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.PillarChangeMoveSelectorConfig;

import java.util.Arrays;
import java.util.List;

public class ProjectJobSchedulingPillarChangeMoveSelectorBenchmark extends AbstractProjectJobSchedulingMoveSelectorBenchmark {

    @Override
    public List<MoveSelectorConfig> createMoveSelectorConfigList() {
        PillarChangeMoveSelectorConfig pillarChangeMoveSelectorConfig = new PillarChangeMoveSelectorConfig();
        PillarChangeMoveSelectorConfig pillarChangeMoveSelectorConfig2 = new PillarChangeMoveSelectorConfig();
        pillarChangeMoveSelectorConfig.setValueSelectorConfig(this.getExecutionModeValueSelectorConfig());
        pillarChangeMoveSelectorConfig2.setValueSelectorConfig(this.getDelayValueSelectorConfig());
        return Arrays.<MoveSelectorConfig>asList(pillarChangeMoveSelectorConfig, pillarChangeMoveSelectorConfig2);
    }

    @Benchmark
    @Override
    public void benchmark() {
        super.benchmark();
    }
}
