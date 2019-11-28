package org.jboss.qa.brms.performance.localsearch.projectjobscheduling;

import java.util.Collections;

import org.jboss.qa.brms.performance.examples.projectjobscheduling.ProjectJobScheduling;
import org.jboss.qa.brms.performance.localsearch.AbstractLocalSearchPlannerBenchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchForagerConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.examples.projectjobscheduling.domain.Schedule;

public abstract class AbstractProjectJobSchedulingLocalSearchBenchmark
        extends AbstractLocalSearchPlannerBenchmark<Schedule> {

    private static final ProjectJobScheduling PROJECT_JOB_SCHEDULING = new ProjectJobScheduling();

    @Param({"A_4", "A_10", "B_9"})
    private ProjectJobScheduling.DataSet dataset;

    @Override
    protected Schedule createInitialSolution() {
        ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig = new ConstructionHeuristicPhaseConfig();
        constructionHeuristicPhaseConfig.setConstructionHeuristicType(ConstructionHeuristicType.FIRST_FIT);

        SolverConfig solverConfig = PROJECT_JOB_SCHEDULING.getBaseSolverConfig();
        solverConfig.setPhaseConfigList(Collections.singletonList(constructionHeuristicPhaseConfig));
        SolverFactory<Schedule> solverFactory = SolverFactory.create(solverConfig);
        Solver<Schedule> constructionSolver = solverFactory.buildSolver();

        Schedule solution = PROJECT_JOB_SCHEDULING.loadSolvingProblem(dataset);
        constructionSolver.solve(solution);
        return constructionSolver.getBestSolution();
    }

    @Override
    public void initSolver() {
        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        localSearchPhaseConfig.setMoveSelectorConfig(new UnionMoveSelectorConfig());
        ((UnionMoveSelectorConfig) localSearchPhaseConfig.getMoveSelectorConfig())
                .setMoveSelectorConfigList(createMoveSelectorConfigList());

        localSearchPhaseConfig.setAcceptorConfig(createAcceptorConfig());

        localSearchPhaseConfig.setForagerConfig(new LocalSearchForagerConfig());
        localSearchPhaseConfig.getForagerConfig().setAcceptedCountLimit(getAcceptedCountLimit());

        localSearchPhaseConfig.setTerminationConfig(getTerminationConfig());

        SolverConfig solverConfig = PROJECT_JOB_SCHEDULING.getBaseSolverConfig();
        solverConfig.setPhaseConfigList(Collections.singletonList(localSearchPhaseConfig));

        SolverFactory<Schedule> solverFactory = SolverFactory.create(solverConfig);
        super.setSolver(solverFactory.buildSolver());
    }
}
