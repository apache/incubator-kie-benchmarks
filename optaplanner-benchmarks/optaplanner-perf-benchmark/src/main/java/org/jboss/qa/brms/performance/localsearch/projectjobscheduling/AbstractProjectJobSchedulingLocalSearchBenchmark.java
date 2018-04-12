package org.jboss.qa.brms.performance.localsearch.projectjobscheduling;

import org.jboss.qa.brms.performance.examples.projectjobscheduling.ProjectJobScheduling;
import org.jboss.qa.brms.performance.examples.projectjobscheduling.domain.Schedule;
import org.jboss.qa.brms.performance.localsearch.AbstractLocalSearchPlannerBenchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchForagerConfig;
import org.optaplanner.core.config.phase.PhaseConfig;

import java.util.Collections;

public abstract class AbstractProjectJobSchedulingLocalSearchBenchmark
        extends AbstractLocalSearchPlannerBenchmark<Schedule> {

    @Param({"A_4", "A_10", "B_9"})
    private ProjectJobScheduling.DataSet dataset;

    @Override
    protected Schedule createInitialSolution() {
        ProjectJobScheduling projectJobScheduling = new ProjectJobScheduling();
        Schedule solution = projectJobScheduling.loadSolvingProblem(dataset);
        SolverFactory<Schedule> defaultConstruction = projectJobScheduling.getBaseSolverFactory();
        ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig = new ConstructionHeuristicPhaseConfig();
        constructionHeuristicPhaseConfig.setConstructionHeuristicType(ConstructionHeuristicType.FIRST_FIT);
        defaultConstruction.getSolverConfig()
                .setPhaseConfigList(Collections.singletonList(((PhaseConfig) constructionHeuristicPhaseConfig)));
        Solver<Schedule> constructionSolver = defaultConstruction.buildSolver();
        constructionSolver.solve(solution);
        return constructionSolver.getBestSolution();
    }

    @Override
    public void initSolver() {
        SolverFactory<Schedule> solverFactory = new ProjectJobScheduling().getBaseSolverFactory();
        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        localSearchPhaseConfig.setMoveSelectorConfig(new UnionMoveSelectorConfig());
        ((UnionMoveSelectorConfig) localSearchPhaseConfig.getMoveSelectorConfig())
                .setMoveSelectorConfigList(createMoveSelectorConfigList());

        localSearchPhaseConfig.setAcceptorConfig(createAcceptorConfig());

        localSearchPhaseConfig.setForagerConfig(new LocalSearchForagerConfig());
        localSearchPhaseConfig.getForagerConfig().setAcceptedCountLimit(getAcceptedCountLimit());

        localSearchPhaseConfig.setTerminationConfig(getTerminationConfig());
        solverFactory.getSolverConfig()
                .setPhaseConfigList(Collections.<PhaseConfig>singletonList(localSearchPhaseConfig));
        super.setSolver(solverFactory.buildSolver());
    }

}
