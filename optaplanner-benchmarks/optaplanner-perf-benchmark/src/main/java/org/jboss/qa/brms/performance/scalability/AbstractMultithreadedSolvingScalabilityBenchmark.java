package org.jboss.qa.brms.performance.scalability;

import java.util.Collections;

import org.jboss.qa.brms.performance.AbstractPlannerBenchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorConfig;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchForagerConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

public abstract class AbstractMultithreadedSolvingScalabilityBenchmark<Solution_> extends AbstractPlannerBenchmark<Solution_> {

    @Param({"NONE", "2", "4", "8"})
    private String moveThreadCount;

    protected abstract SolverConfig getBaseSolverConfig();

    protected abstract TerminationConfig getTerminationConfig();

    protected abstract MoveSelectorConfig getMoveSelectorConfig();

    protected abstract AcceptorConfig getAcceptorConfig();

    protected abstract int getAcceptedCountLimit();

    protected abstract Solution_ getInitialSolution();

    @Override
    public void initSolution() {
        super.setSolution(getInitialSolution());
    }

    @Override
    public void initSolver() {
        SolverConfig solverConfig = getBaseSolverConfig();
        solverConfig.setMoveThreadCount(moveThreadCount);

        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        localSearchPhaseConfig.setMoveSelectorConfig(getMoveSelectorConfig());

        localSearchPhaseConfig.setAcceptorConfig(getAcceptorConfig());

        localSearchPhaseConfig.setForagerConfig(new LocalSearchForagerConfig());
        localSearchPhaseConfig.getForagerConfig().setAcceptedCountLimit(getAcceptedCountLimit());

        localSearchPhaseConfig.setTerminationConfig(getTerminationConfig());

        solverConfig.setPhaseConfigList(Collections.singletonList(localSearchPhaseConfig));
        SolverFactory<Solution_> solverFactory = SolverFactory.create(solverConfig);
        super.setSolver(solverFactory.buildSolver());
    }
}
