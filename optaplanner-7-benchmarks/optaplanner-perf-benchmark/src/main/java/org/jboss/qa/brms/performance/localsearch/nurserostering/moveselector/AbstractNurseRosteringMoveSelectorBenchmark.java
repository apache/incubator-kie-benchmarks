package org.jboss.qa.brms.performance.localsearch.nurserostering.moveselector;

import java.util.Collections;

import org.jboss.qa.brms.performance.examples.Examples;
import org.jboss.qa.brms.performance.examples.nurserostering.NurseRosteringExample;
import org.jboss.qa.brms.performance.localsearch.AbstractLocalSearchPlannerBenchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.entity.pillar.PillarSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorConfig;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchForagerConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;
import org.optaplanner.examples.nurserostering.domain.solver.MovableShiftAssignmentSelectionFilter;

public abstract class AbstractNurseRosteringMoveSelectorBenchmark
        extends AbstractLocalSearchPlannerBenchmark<NurseRoster> {

    private static final int ENTITY_TABU_SIZE = 7;
    private static final int ACCEPTED_COUNT_LIMIT = 800;
    private static final long CALCULATION_COUNT_LIMIT = 10_000L;

    @Param({"SPRINT", "MEDIUM", "LONG"})
    private NurseRosteringExample.DataSet dataset;

    @Override
    protected NurseRoster createInitialSolution() {
        ConstructionHeuristicPhaseConfig chPhaseConfig = new ConstructionHeuristicPhaseConfig()
                .withConstructionHeuristicType(ConstructionHeuristicType.WEAKEST_FIT);

        SolverConfig solverConfig = Examples.NURSE_ROSTERING.getBaseSolverConfig();
        solverConfig.setPhaseConfigList(Collections.singletonList(chPhaseConfig));

        SolverFactory<NurseRoster> solverFactory = SolverFactory.create(solverConfig);
        Solver<NurseRoster> constructionSolver = solverFactory.buildSolver();

        NurseRoster nonInitializedSolution = Examples.NURSE_ROSTERING.loadSolvingProblem(dataset);
        constructionSolver.solve(nonInitializedSolution);
        return constructionSolver.getBestSolution();
    }

    protected PillarSelectorConfig createPillarSelectorConfig() {
        PillarSelectorConfig pillarSelectorConfig = new PillarSelectorConfig();
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setFilterClassList(Collections.singletonList(MovableShiftAssignmentSelectionFilter.class));
        pillarSelectorConfig.setEntitySelectorConfig(entitySelectorConfig);

        return pillarSelectorConfig;
    }

    @Override
    public AcceptorConfig createAcceptorConfig() {
        return new AcceptorConfig().withEntityTabuSize(ENTITY_TABU_SIZE);
    }

    @Override
    public int getAcceptedCountLimit() {
        return ACCEPTED_COUNT_LIMIT;
    }

    @Override
    public TerminationConfig getTerminationConfig() {
        return new TerminationConfig().withScoreCalculationCountLimit(CALCULATION_COUNT_LIMIT);
    }

    @Override
    protected Solver<NurseRoster> createSolver() {
        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        UnionMoveSelectorConfig unionMoveSelectorConfig = new UnionMoveSelectorConfig();
        unionMoveSelectorConfig.setMoveSelectorConfigList(createMoveSelectorConfigList());
        localSearchPhaseConfig.setMoveSelectorConfig(unionMoveSelectorConfig);
        localSearchPhaseConfig.setAcceptorConfig(createAcceptorConfig());
        localSearchPhaseConfig.setForagerConfig(new LocalSearchForagerConfig());
        localSearchPhaseConfig.getForagerConfig().setAcceptedCountLimit(getAcceptedCountLimit());
        localSearchPhaseConfig.setTerminationConfig(getTerminationConfig());

        SolverConfig solverConfig = Examples.NURSE_ROSTERING.getBaseSolverConfig();
        solverConfig.setPhaseConfigList(Collections.singletonList(localSearchPhaseConfig));

        SolverFactory<NurseRoster> solverFactory = SolverFactory.create(solverConfig);
        return solverFactory.buildSolver();
    }

    protected void setDataset(NurseRosteringExample.DataSet dataset) {
        this.dataset = dataset;
    }
}
