package org.jboss.qa.brms.performance.localsearch.projectjobscheduling.moveselector;

import org.openjdk.jmh.annotations.Benchmark;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;

import java.util.Arrays;
import java.util.List;

public class ProjectJobSchedulingChangeMoveSelectorBenchmark extends AbstractProjectJobSchedulingMoveSelectorBenchmark {

    @Override
    public List<MoveSelectorConfig> createMoveSelectorConfigList() {
        ChangeMoveSelectorConfig changeMoveSelectorConfig = new ChangeMoveSelectorConfig();
        ChangeMoveSelectorConfig changeMoveSelectorConfig2 = new ChangeMoveSelectorConfig();
        changeMoveSelectorConfig.setValueSelectorConfig(this.getExecutionModeValueSelectorConfig());
        changeMoveSelectorConfig2.setValueSelectorConfig(this.getDelayValueSelectorConfig());
        return Arrays.<MoveSelectorConfig>asList(changeMoveSelectorConfig, changeMoveSelectorConfig2);
    }

    @Benchmark
    @Override
    public void benchmark() {
        super.benchmark();
    }
}
