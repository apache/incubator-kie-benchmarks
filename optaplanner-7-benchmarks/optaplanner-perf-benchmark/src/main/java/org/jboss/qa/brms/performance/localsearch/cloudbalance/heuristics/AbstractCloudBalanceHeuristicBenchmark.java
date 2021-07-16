package org.jboss.qa.brms.performance.localsearch.cloudbalance.heuristics;

import org.jboss.qa.brms.performance.configuration.MoveSelectorConfigurations;
import org.jboss.qa.brms.performance.localsearch.cloudbalance.AbstractCloudBalanceLocalSearchBenchmark;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

import java.util.List;

public abstract class AbstractCloudBalanceHeuristicBenchmark extends AbstractCloudBalanceLocalSearchBenchmark {

    @Override
    public List<MoveSelectorConfig> createMoveSelectorConfigList() {
        return MoveSelectorConfigurations.createAllNonChainedSelectorList();
    }

    @Override
    public int getAcceptedCountLimit() {
        return 100;
    }

    @Override
    public TerminationConfig getTerminationConfig() {
        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setStepCountLimit(500);
        return terminationConfig;
    }
}
