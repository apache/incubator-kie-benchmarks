package org.jboss.qa.brms.performance.scalability;

import java.util.Collections;

import org.jboss.qa.brms.performance.configuration.AcceptorConfigurations;
import org.jboss.qa.brms.performance.configuration.MoveSelectorConfigurations;
import org.jboss.qa.brms.performance.examples.vehiclerouting.VehicleRouting;
import org.jboss.qa.brms.performance.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.jboss.qa.brms.performance.examples.vehiclerouting.solver.phase.VehicleRoutingSolutionInitializer;
import org.jboss.qa.brms.performance.profiler.MemoryConsumptionProfiler;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorConfig;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.phase.custom.CustomPhaseConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;

public class VRPMultithreadedSolvingScalabilityBenchmark
        extends AbstractMultithreadedSolvingScalabilityBenchmark<VehicleRoutingSolution> {

    private static final VehicleRouting VEHICLE_ROUTING = new VehicleRouting();

    @Param({"VRP_USA_100_10", "VRP_USA_1000_20", "VRP_USA_10000_100"})
    private VehicleRouting.DataSet dataset;

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
    protected VehicleRoutingSolution getInitialSolution() {
        VehicleRoutingSolution solution = VEHICLE_ROUTING.loadSolvingProblem(dataset);
        SolverFactory<VehicleRoutingSolution> defaultConstruction = VEHICLE_ROUTING.getBaseSolverFactory();
        CustomPhaseConfig customPhaseConfig = new CustomPhaseConfig();
        customPhaseConfig.setCustomPhaseCommandClassList(
                Collections.<Class<? extends CustomPhaseCommand>>singletonList(VehicleRoutingSolutionInitializer.class));
        defaultConstruction.getSolverConfig()
                .setPhaseConfigList(Collections.singletonList((PhaseConfig) customPhaseConfig));
        Solver<VehicleRoutingSolution> constructionSolver = defaultConstruction.buildSolver();
        constructionSolver.solve(solution);
        return constructionSolver.getBestSolution();
    }

    @Override
    protected SolverFactory<VehicleRoutingSolution> getSolverFactory() {
        return VEHICLE_ROUTING.getBaseSolverFactory();
    }

    @Benchmark
    public VehicleRoutingSolution benchmark() {
        return super.benchmark();
    }
}
