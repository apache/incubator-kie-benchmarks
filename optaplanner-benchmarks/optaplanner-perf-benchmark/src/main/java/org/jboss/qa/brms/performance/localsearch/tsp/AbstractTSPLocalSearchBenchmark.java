package org.jboss.qa.brms.performance.localsearch.tsp;

import java.util.Collections;

import org.jboss.qa.brms.performance.examples.tsp.TravelingSalesmanProblem;
import org.jboss.qa.brms.performance.examples.tsp.solution.TSPSolutionInitializer;
import org.jboss.qa.brms.performance.localsearch.AbstractLocalSearchPlannerBenchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchForagerConfig;
import org.optaplanner.core.config.phase.custom.CustomPhaseConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.examples.tsp.domain.TspSolution;

public abstract class AbstractTSPLocalSearchBenchmark
        extends AbstractLocalSearchPlannerBenchmark<TspSolution> {

    private static final TravelingSalesmanProblem TRAVELING_SALESMAN_PROBLEM = new TravelingSalesmanProblem();

    @Param({"LU_980", "USA_CA_2716", "GREECE_9882"})
    private TravelingSalesmanProblem.DataSet dataset;

    @Override
    protected TspSolution createInitialSolution() {
        CustomPhaseConfig customPhaseConfig = new CustomPhaseConfig();
        customPhaseConfig.setCustomPhaseCommandClassList(Collections.singletonList(TSPSolutionInitializer.class));

        SolverConfig solverConfig = TRAVELING_SALESMAN_PROBLEM.getBaseSolverConfig();
        solverConfig.setPhaseConfigList(Collections.singletonList(customPhaseConfig));

        SolverFactory<TspSolution> solverFactory = SolverFactory.create(solverConfig);
        Solver<TspSolution> constructionSolver = solverFactory.buildSolver();

        TspSolution solution = TRAVELING_SALESMAN_PROBLEM.loadSolvingProblem(dataset);
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

        SolverConfig solverConfig = TRAVELING_SALESMAN_PROBLEM.getBaseSolverConfig();
        solverConfig.setPhaseConfigList(Collections.singletonList(localSearchPhaseConfig));

        SolverFactory<TspSolution> solverFactory = SolverFactory.create(solverConfig);
        super.setSolver(solverFactory.buildSolver());
    }
}
