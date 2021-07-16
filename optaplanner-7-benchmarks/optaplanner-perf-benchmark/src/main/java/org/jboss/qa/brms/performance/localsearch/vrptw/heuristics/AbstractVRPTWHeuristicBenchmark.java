package org.jboss.qa.brms.performance.localsearch.vrptw.heuristics;

import org.jboss.qa.brms.performance.configuration.MoveSelectorConfigurations;
import org.jboss.qa.brms.performance.localsearch.vrptw.AbstractVRPTWLocalSearchBenchmark;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

import java.util.List;

public abstract class AbstractVRPTWHeuristicBenchmark extends AbstractVRPTWLocalSearchBenchmark {

    @Override
    public List<MoveSelectorConfig> createMoveSelectorConfigList() {
        return MoveSelectorConfigurations.createAllChainedSelectorList();
    }

    @Override
    public int getAcceptedCountLimit() {
        return 100;
    }

    @Override
    public TerminationConfig getTerminationConfig() {
        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setStepCountLimit(100);
        return terminationConfig;
    }
}
