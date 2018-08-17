package org.jboss.qa.brms.performance.scalability;

import java.util.Collections;

import org.jboss.qa.brms.performance.AbstractPlannerBenchmark;
import org.jboss.qa.brms.performance.profiler.MemoryConsumptionProfiler;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorConfig;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchForagerConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

public abstract class AbstractMultithreadedSolvingScalabilityBenchmark<Solution_> extends AbstractPlannerBenchmark<Solution_> {

    @Param({"NONE", "2", "4", "8"})
    private String moveThreadCount;

    protected abstract SolverFactory<Solution_> getSolverFactory();

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
        SolverFactory<Solution_> solverFactory = getSolverFactory();
        solverFactory.getSolverConfig().setMoveThreadCount(moveThreadCount);

        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        localSearchPhaseConfig.setMoveSelectorConfig(getMoveSelectorConfig());

        localSearchPhaseConfig.setAcceptorConfig(getAcceptorConfig());

        localSearchPhaseConfig.setForagerConfig(new LocalSearchForagerConfig());
        localSearchPhaseConfig.getForagerConfig().setAcceptedCountLimit(getAcceptedCountLimit());

        localSearchPhaseConfig.setTerminationConfig(getTerminationConfig());

        solverFactory.getSolverConfig().setPhaseConfigList(Collections.singletonList(localSearchPhaseConfig));

        super.setSolver(solverFactory.buildSolver());
    }

    public static void main(String... args) throws Exception {
        Options opts = new OptionsBuilder()
                .addProfiler(MemoryConsumptionProfiler.class)
                .include(".+ScalabilityBenchmark")
                .build();
        new Runner(opts).run();
    }
}
