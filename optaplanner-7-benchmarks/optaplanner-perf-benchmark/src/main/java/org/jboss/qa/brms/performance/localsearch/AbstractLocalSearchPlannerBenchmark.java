package org.jboss.qa.brms.performance.localsearch;

import org.jboss.qa.brms.performance.AbstractPlannerBenchmark;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

import java.util.List;

public abstract class AbstractLocalSearchPlannerBenchmark<Solution_> extends AbstractPlannerBenchmark<Solution_> {

    public abstract AcceptorConfig createAcceptorConfig();

    public abstract List<MoveSelectorConfig> createMoveSelectorConfigList();

    public abstract int getAcceptedCountLimit();

    public abstract TerminationConfig getTerminationConfig();
}
