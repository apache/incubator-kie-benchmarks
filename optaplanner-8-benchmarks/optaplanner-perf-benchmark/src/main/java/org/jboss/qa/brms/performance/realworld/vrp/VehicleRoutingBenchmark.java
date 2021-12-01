package org.jboss.qa.brms.performance.realworld.vrp;

import java.util.Arrays;
import java.util.List;

import org.jboss.qa.brms.performance.AbstractPlannerBenchmark;
import org.jboss.qa.brms.performance.examples.Examples;
import org.jboss.qa.brms.performance.examples.vehiclerouting.VehicleRoutingExample;
import org.jboss.qa.brms.performance.examples.vehiclerouting.termination.HardVRPCalculateCountTermination;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.SwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.SubChainChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.SubChainSwapMoveSelectorConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.decider.acceptor.LocalSearchAcceptorConfig;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchForagerConfig;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Standstill;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;
import org.optaplanner.examples.vehiclerouting.score.VehicleRoutingConstraintProvider;

public class VehicleRoutingBenchmark extends AbstractPlannerBenchmark<VehicleRoutingSolution> {

    private static final int FORAGER_CONFIG_ACCEPTED_COUNT_LIMIT = 1;
    private static final int ACCEPTOR_CONFIG_LATE_ACCEPTANCE_SIZE = 200;

    @Param({"SMALLEST", "VRP_USA_1000_20", "VRP_USA_10000_100"})
    private VehicleRoutingExample.DataSet dataSet;

    @Override
    protected VehicleRoutingSolution createInitialSolution() {
        return Examples.VEHICLE_ROUTING.loadSolvingProblem(dataSet);
    }

    @Override
    protected Solver<VehicleRoutingSolution> createSolver() {
        // the pre-defined configuration in VehicleRouting cannot be used
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.withEntityClasses(Standstill.class, Customer.class,TimeWindowedCustomer.class);
        solverConfig.withSolutionClass(VehicleRoutingSolution.class);

        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        scoreDirectorFactoryConfig.setInitializingScoreTrend("ONLY_DOWN");
        scoreDirectorFactoryConfig.setConstraintProviderClass(VehicleRoutingConstraintProvider.class);

        solverConfig.setPhaseConfigList(getPhaseConfigList());
        solverConfig.setScoreDirectorFactoryConfig(scoreDirectorFactoryConfig);
        solverConfig.setTerminationConfig(new TerminationConfig().withTerminationClass(HardVRPCalculateCountTermination.class));

        SolverFactory<VehicleRoutingSolution> solverFactory = SolverFactory.create(solverConfig);
        return solverFactory.buildSolver();
    }

    @Benchmark
    public VehicleRoutingSolution benchmark() {
        return runBenchmark();
    }

    private List<PhaseConfig> getPhaseConfigList() {
        ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig = new ConstructionHeuristicPhaseConfig();
        constructionHeuristicPhaseConfig.setConstructionHeuristicType(ConstructionHeuristicType.FIRST_FIT_DECREASING);

        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();

        SubChainChangeMoveSelectorConfig subChainChangeMoveSelectorConfig = new SubChainChangeMoveSelectorConfig();
        subChainChangeMoveSelectorConfig.setSelectReversingMoveToo(true);

        SubChainSwapMoveSelectorConfig subChainSwapMoveSelectorConfig = new SubChainSwapMoveSelectorConfig();
        subChainSwapMoveSelectorConfig.setSelectReversingMoveToo(true);

        List<MoveSelectorConfig> moveSelectorConfigList = Arrays.asList(new ChangeMoveSelectorConfig(),
                                                                        new SwapMoveSelectorConfig(),
                                                                        subChainChangeMoveSelectorConfig,
                                                                        subChainSwapMoveSelectorConfig);
        UnionMoveSelectorConfig selectorConfig = new UnionMoveSelectorConfig(moveSelectorConfigList);

        LocalSearchForagerConfig foragerConfig = new LocalSearchForagerConfig();
        foragerConfig.setAcceptedCountLimit(FORAGER_CONFIG_ACCEPTED_COUNT_LIMIT);

        localSearchPhaseConfig.setForagerConfig(foragerConfig);
        localSearchPhaseConfig.setMoveSelectorConfig(selectorConfig);
        localSearchPhaseConfig.setAcceptorConfig(new LocalSearchAcceptorConfig().withLateAcceptanceSize(ACCEPTOR_CONFIG_LATE_ACCEPTANCE_SIZE));

        return Arrays.asList(constructionHeuristicPhaseConfig, localSearchPhaseConfig);
    }
}
