package org.jboss.qa.brms.performance.localsearch.tsp;

import java.util.Collections;

import org.jboss.qa.brms.performance.examples.Examples;
import org.jboss.qa.brms.performance.examples.tsp.TravelingSalesmanExample;
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

    @Param({"LU_980", "USA_CA_2716", "GREECE_9882"})
    private TravelingSalesmanExample.DataSet dataset;

    @Override
    protected TspSolution createInitialSolution() {
        CustomPhaseConfig customPhaseConfig = new CustomPhaseConfig();
        customPhaseConfig.setCustomPhaseCommandClassList(Collections.singletonList(TSPSolutionInitializer.class));

        SolverConfig solverConfig = Examples.TRAVELING_SALESMAN.getBaseSolverConfig();
        solverConfig.setPhaseConfigList(Collections.singletonList(customPhaseConfig));

        SolverFactory<TspSolution> solverFactory = SolverFactory.create(solverConfig);
        Solver<TspSolution> constructionSolver = solverFactory.buildSolver();

        TspSolution solution = Examples.TRAVELING_SALESMAN.loadSolvingProblem(dataset);
        constructionSolver.solve(solution);
        return constructionSolver.getBestSolution();
    }

    @Override
    protected Solver<TspSolution> createSolver() {
        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        localSearchPhaseConfig.setMoveSelectorConfig(new UnionMoveSelectorConfig());
        ((UnionMoveSelectorConfig) localSearchPhaseConfig.getMoveSelectorConfig())
                .setMoveSelectorConfigList(createMoveSelectorConfigList());
        localSearchPhaseConfig.setAcceptorConfig(createAcceptorConfig());
        localSearchPhaseConfig.setForagerConfig(new LocalSearchForagerConfig());
        localSearchPhaseConfig.getForagerConfig().setAcceptedCountLimit(getAcceptedCountLimit());
        localSearchPhaseConfig.setTerminationConfig(getTerminationConfig());

        SolverConfig solverConfig = Examples.TRAVELING_SALESMAN.getBaseSolverConfig();
        solverConfig.setPhaseConfigList(Collections.singletonList(localSearchPhaseConfig));

        SolverFactory<TspSolution> solverFactory = SolverFactory.create(solverConfig);
        return solverFactory.buildSolver();
    }
}
