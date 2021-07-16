package org.jboss.qa.brms.performance.scalability;

import java.util.Collections;

import org.jboss.qa.brms.performance.configuration.AcceptorConfigurations;
import org.jboss.qa.brms.performance.configuration.MoveSelectorConfigurations;
import org.jboss.qa.brms.performance.examples.Examples;
import org.jboss.qa.brms.performance.examples.vehiclerouting.VehicleRoutingExample;
import org.jboss.qa.brms.performance.examples.vehiclerouting.solution.VehicleRoutingSolutionInitializer;
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
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;

public class VRPMultithreadedSolvingScalabilityBenchmark
        extends AbstractMultithreadedSolvingScalabilityBenchmark<VehicleRoutingSolution> {

    @Param({"VRP_USA_100_10", "VRP_USA_1000_20", "VRP_USA_10000_100"})
    private VehicleRoutingExample.DataSet dataset;

    @Override
    protected TerminationConfig getTerminationConfig() {
        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setStepCountLimit(100);
        return terminationConfig;
    }

    @Override
    protected MoveSelectorConfig getMoveSelectorConfig() {
        UnionMoveSelectorConfig unionMoveSelectorConfig = new UnionMoveSelectorConfig();
        unionMoveSelectorConfig.setMoveSelectorConfigList(MoveSelectorConfigurations.createAllChainedSelectorList());
        return unionMoveSelectorConfig;
    }

    @Override
    protected AcceptorConfig getAcceptorConfig() {
        return AcceptorConfigurations.createLateAcceptanceAcceptor(50);
    }

    @Override
    protected int getAcceptedCountLimit() {
        return 100;
    }

    @Override
    protected VehicleRoutingSolution createInitialSolution() {
        CustomPhaseConfig customPhaseConfig = new CustomPhaseConfig();
        customPhaseConfig.setCustomPhaseCommandClassList(
                Collections.singletonList(VehicleRoutingSolutionInitializer.class));

        SolverConfig solverConfig = getBaseSolverConfig();
        solverConfig.setPhaseConfigList(Collections.singletonList(customPhaseConfig));

        SolverFactory<VehicleRoutingSolution> solverFactory = SolverFactory.create(solverConfig);
        Solver<VehicleRoutingSolution> constructionSolver = solverFactory.buildSolver();

        VehicleRoutingSolution solution = Examples.VEHICLE_ROUTING.loadSolvingProblem(dataset);
        constructionSolver.solve(solution);
        return constructionSolver.getBestSolution();
    }

    @Override
    protected SolverConfig getBaseSolverConfig() {
        return Examples.VEHICLE_ROUTING.getBaseSolverConfig();
    }

    @Benchmark
    public VehicleRoutingSolution benchmark() {
        return runBenchmark();
    }
}
