package org.jboss.qa.brms.performance.realworld.vrp;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jboss.qa.brms.performance.AbstractPlannerBenchmark;
import org.jboss.qa.brms.performance.calculatecounttermination.HardVRPCalculateCountTermination;
import org.jboss.qa.brms.performance.examples.vehiclerouting.VehicleRouting;
import org.jboss.qa.brms.performance.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.core.config.domain.ScanAnnotatedClassesConfig;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.SwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.SubChainChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.SubChainSwapMoveSelectorConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorConfig;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchForagerConfig;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

public class VehicleRoutingBenchmark extends AbstractPlannerBenchmark<VehicleRoutingSolution> {

    private static final String VEHCILE_ROUTING_DOMAIN_PACKAGE = "org.jboss.qa.brms.performance.examples.vehiclerouting";
    private static final String VEHCILE_ROUTING_DROOLS_SCORE_RULES_FILE = "org/jboss/qa/brms/performance/examples/vrp/solver/vehicleRoutingScoreRules.drl";
    private static final int FORAGER_CONFIG_ACCEPTED_COUNT_LIMIT = 1;
    private static final int ACCEPTOR_CONFIG_LATE_ACCEPTANCE_SIZE = 200;

    @Param({"VRP_USA_100_10", "VRP_USA_1000_20", "VRP_USA_10000_100"})
    private VehicleRouting.DataSet dataset;

    @Override
    public void initSolution() {
        super.setSolution(new VehicleRouting().loadSolvingProblem(dataset));
    }

    @Override
    public void initSolver() {
        SolverFactory<VehicleRoutingSolution> solverFactory = SolverFactory.createEmpty();
        SolverConfig config = solverFactory.getSolverConfig();

        ScanAnnotatedClassesConfig scanAnnotatedClassesConfig = new ScanAnnotatedClassesConfig();
        scanAnnotatedClassesConfig.setPackageIncludeList(Collections.singletonList(VEHCILE_ROUTING_DOMAIN_PACKAGE));

        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        scoreDirectorFactoryConfig.setInitializingScoreTrend("ONLY_DOWN");
        scoreDirectorFactoryConfig.setScoreDrlList(Collections.singletonList(VEHCILE_ROUTING_DROOLS_SCORE_RULES_FILE));

        config.setPhaseConfigList(getPhaseConfigList());
        config.setScoreDirectorFactoryConfig(scoreDirectorFactoryConfig);
        config.setTerminationConfig(new TerminationConfig().withTerminationClass(HardVRPCalculateCountTermination.class));
        config.setScanAnnotatedClassesConfig(scanAnnotatedClassesConfig);

        super.setSolver(solverFactory.buildSolver());
    }

    @Benchmark
    public VehicleRoutingSolution benchmark() {
        return super.benchmark();
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
        localSearchPhaseConfig.setAcceptorConfig(new AcceptorConfig().withLateAcceptanceSize(ACCEPTOR_CONFIG_LATE_ACCEPTANCE_SIZE));

        return Arrays.asList(constructionHeuristicPhaseConfig, localSearchPhaseConfig);
    }
}
