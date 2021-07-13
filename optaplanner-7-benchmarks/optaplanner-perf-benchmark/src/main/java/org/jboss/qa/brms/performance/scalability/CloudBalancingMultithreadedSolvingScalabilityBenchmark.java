package org.jboss.qa.brms.performance.scalability;

import java.util.Collections;

import org.jboss.qa.brms.performance.configuration.MoveSelectorConfigurations;
import org.jboss.qa.brms.performance.examples.Examples;
import org.jboss.qa.brms.performance.examples.cloudbalancing.CloudBalancingExample;
import org.jboss.qa.brms.performance.examples.cloudbalancing.solution.CloudBalanceSolutionInitializer;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorConfig;
import org.optaplanner.core.config.phase.custom.CustomPhaseConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;

public class CloudBalancingMultithreadedSolvingScalabilityBenchmark
        extends AbstractMultithreadedSolvingScalabilityBenchmark<CloudBalance> {

    @Param({"CB_100_300", "CB_1600_4800", "CB_10000_30000"})
    private CloudBalancingExample.DataSet dataset;

    @Override
    protected TerminationConfig getTerminationConfig() {
        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setStepCountLimit(500);
        return terminationConfig;
    }

    @Override
    protected SolverConfig getBaseSolverConfig() {
        return Examples.CLOUD_BALANCING.getBaseSolverConfig();
    }

    @Override
    protected MoveSelectorConfig getMoveSelectorConfig() {
        UnionMoveSelectorConfig unionMoveSelectorConfig = new UnionMoveSelectorConfig();
        unionMoveSelectorConfig.setMoveSelectorConfigList(MoveSelectorConfigurations.createAllNonChainedSelectorList());
        return unionMoveSelectorConfig;
    }

    @Override
    protected AcceptorConfig getAcceptorConfig() {
        AcceptorConfig acceptorConfig = new AcceptorConfig();
        acceptorConfig.setLateAcceptanceSize(100);
        return acceptorConfig;
    }

    @Override
    protected CloudBalance createInitialSolution() {
        CustomPhaseConfig customPhaseConfig = new CustomPhaseConfig();
        customPhaseConfig.setCustomPhaseCommandClassList(
                Collections.singletonList(CloudBalanceSolutionInitializer.class));

        SolverConfig solverConfig = getBaseSolverConfig();
        solverConfig.setPhaseConfigList(Collections.singletonList(customPhaseConfig));

        SolverFactory<CloudBalance> solverFactory = SolverFactory.create(solverConfig);
        Solver<CloudBalance> constructionSolver = solverFactory.buildSolver();

        CloudBalance solution = Examples.CLOUD_BALANCING.loadSolvingProblem(dataset);
        constructionSolver.solve(solution);
        return constructionSolver.getBestSolution();
    }

    @Override
    protected int getAcceptedCountLimit() {
        return 100;
    }

    @Benchmark
    public CloudBalance benchmark() {
        return runBenchmark();
    }
}
