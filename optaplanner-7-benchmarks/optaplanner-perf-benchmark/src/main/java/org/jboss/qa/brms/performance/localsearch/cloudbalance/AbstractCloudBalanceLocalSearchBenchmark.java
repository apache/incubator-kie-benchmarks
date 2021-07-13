package org.jboss.qa.brms.performance.localsearch.cloudbalance;

import java.util.Collections;

import org.jboss.qa.brms.performance.examples.Examples;
import org.jboss.qa.brms.performance.examples.cloudbalancing.CloudBalancingExample;
import org.jboss.qa.brms.performance.examples.cloudbalancing.solution.CloudBalanceSolutionInitializer;
import org.jboss.qa.brms.performance.localsearch.AbstractLocalSearchPlannerBenchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchForagerConfig;
import org.optaplanner.core.config.phase.custom.CustomPhaseConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;

public abstract class AbstractCloudBalanceLocalSearchBenchmark extends AbstractLocalSearchPlannerBenchmark<CloudBalance> {

    @Param({"CB_100_300", "CB_1600_4800", "CB_10000_30000"})
    private CloudBalancingExample.DataSet dataset;

    @Override
    protected CloudBalance createInitialSolution() {
        CustomPhaseConfig customPhaseConfig = new CustomPhaseConfig();
        customPhaseConfig.setCustomPhaseCommandClassList(
                Collections.singletonList(CloudBalanceSolutionInitializer.class));

        SolverConfig solverConfig = Examples.CLOUD_BALANCING.getBaseSolverConfig();
        solverConfig.setPhaseConfigList(Collections.singletonList(customPhaseConfig));

        SolverFactory<CloudBalance> solverFactory = SolverFactory.create(solverConfig);
        Solver<CloudBalance> constructionSolver = solverFactory.buildSolver();

        CloudBalance solution = Examples.CLOUD_BALANCING.loadSolvingProblem(dataset);
        constructionSolver.solve(solution);
        return constructionSolver.getBestSolution();
    }

    @Override
    protected Solver<CloudBalance> createSolver() {
        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        localSearchPhaseConfig.setMoveSelectorConfig(new UnionMoveSelectorConfig());
        ((UnionMoveSelectorConfig) localSearchPhaseConfig.getMoveSelectorConfig())
                .setMoveSelectorConfigList(createMoveSelectorConfigList());
        localSearchPhaseConfig.setAcceptorConfig(createAcceptorConfig());
        localSearchPhaseConfig.setForagerConfig(new LocalSearchForagerConfig());
        localSearchPhaseConfig.getForagerConfig().setAcceptedCountLimit(getAcceptedCountLimit());
        localSearchPhaseConfig.setTerminationConfig(getTerminationConfig());

        SolverConfig solverConfig = Examples.CLOUD_BALANCING.getBaseSolverConfig();
        solverConfig.setPhaseConfigList(Collections.singletonList(localSearchPhaseConfig));

        SolverFactory<CloudBalance> solverFactory = SolverFactory.create(solverConfig);
        return solverFactory.buildSolver();
    }
}
