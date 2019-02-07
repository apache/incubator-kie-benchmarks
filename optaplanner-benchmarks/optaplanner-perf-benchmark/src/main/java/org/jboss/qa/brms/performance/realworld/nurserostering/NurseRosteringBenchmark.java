package org.jboss.qa.brms.performance.realworld.nurserostering;

import java.util.Arrays;
import java.util.Collections;

import org.jboss.qa.brms.performance.AbstractPlannerBenchmark;
import org.jboss.qa.brms.performance.calculatecounttermination.NurseRosterHardSoftCalculateCountTermination;
import org.jboss.qa.brms.performance.examples.nurserostering.NurseRostering;
import org.jboss.qa.brms.performance.examples.nurserostering.domain.NurseRoster;
import org.jboss.qa.brms.performance.examples.nurserostering.solver.move.factory.EmployeeChangeMoveFactory;
import org.jboss.qa.brms.performance.examples.nurserostering.solver.move.factory.ShiftAssignmentPillarPartSwapMoveFactory;
import org.jboss.qa.brms.performance.examples.nurserostering.solver.move.factory.ShiftAssignmentSwapMoveFactory;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Warmup;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.core.config.domain.ScanAnnotatedClassesConfig;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.factory.MoveListFactoryConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorConfig;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchForagerConfig;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

@Warmup(iterations = 15)
public class NurseRosteringBenchmark extends AbstractPlannerBenchmark<NurseRoster> {

    private static final String NURSE_ROSTERING_DOMAIN_PACKAGE =
            "org.jboss.qa.brms.performance.examples.nurserostering";

    private static final String NURSE_ROSTERING_PATH_TO_SCORE_RULES_FILE =
            "org/jboss/qa/brms/performance/examples/nurserostering/solver/nurseRosteringScoreRules.drl";

    private static final int ACCEPTOR_CONFIG_ENTITY_TABU_SIZE = 7;
    private static final int FORAGER_CONFIG_ACCEPTED_COUNT_LIMIT = 700;
    @Param({"SPRINT", "MEDIUM", "LONG"})
    private NurseRostering.DataSet dataset;

    @Override
    public void initSolution() {
        super.setSolution(new NurseRostering().loadSolvingProblem(dataset));
    }

    @Override
    public void initSolver() {
        SolverFactory<NurseRoster> nurseRosterSolverFactory = SolverFactory.createEmpty();
        SolverConfig solverConfig = nurseRosterSolverFactory.getSolverConfig();

        ScanAnnotatedClassesConfig scanAnnotatedClassesConfig = new ScanAnnotatedClassesConfig();
        scanAnnotatedClassesConfig.setPackageIncludeList(Collections.singletonList(NURSE_ROSTERING_DOMAIN_PACKAGE));

        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        scoreDirectorFactoryConfig.setScoreDrlList(Collections.singletonList(NURSE_ROSTERING_PATH_TO_SCORE_RULES_FILE));

        solverConfig.setTerminationConfig(new TerminationConfig().
                withTerminationClass(NurseRosterHardSoftCalculateCountTermination.class));

        ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig = new ConstructionHeuristicPhaseConfig();
        constructionHeuristicPhaseConfig.setConstructionHeuristicType(ConstructionHeuristicType.WEAKEST_FIT);

        LocalSearchPhaseConfig localSearchPhaseConfig = getLocalSearchPhaseConfig();

        solverConfig.setPhaseConfigList(Arrays.asList(constructionHeuristicPhaseConfig, localSearchPhaseConfig));
        solverConfig.setScoreDirectorFactoryConfig(scoreDirectorFactoryConfig);
        solverConfig.setScanAnnotatedClassesConfig(scanAnnotatedClassesConfig);

        super.setSolver(nurseRosterSolverFactory.buildSolver());
    }

    private LocalSearchPhaseConfig getLocalSearchPhaseConfig() {
        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();

        UnionMoveSelectorConfig moveSelectorConfig = new UnionMoveSelectorConfig();

        MoveListFactoryConfig employeeChangeMoveFactoryConfig = new MoveListFactoryConfig();
        employeeChangeMoveFactoryConfig.setCacheType(SelectionCacheType.PHASE);
        employeeChangeMoveFactoryConfig.setMoveListFactoryClass(EmployeeChangeMoveFactory.class);

        MoveListFactoryConfig shiftAssignmentSwapMoveFactoryConfig = new MoveListFactoryConfig();
        shiftAssignmentSwapMoveFactoryConfig.setCacheType(SelectionCacheType.PHASE);
        shiftAssignmentSwapMoveFactoryConfig.setMoveListFactoryClass(ShiftAssignmentSwapMoveFactory.class);

        MoveListFactoryConfig shiftAssignmentPillarPartSwapMoveFactoryConfig = new MoveListFactoryConfig();
        shiftAssignmentPillarPartSwapMoveFactoryConfig.setCacheType(SelectionCacheType.STEP);
        shiftAssignmentPillarPartSwapMoveFactoryConfig.
                setMoveListFactoryClass(ShiftAssignmentPillarPartSwapMoveFactory.class);

        moveSelectorConfig.setMoveSelectorConfigList(Arrays.asList(employeeChangeMoveFactoryConfig,
                                                                   shiftAssignmentSwapMoveFactoryConfig,
                                                                   shiftAssignmentPillarPartSwapMoveFactoryConfig));

        LocalSearchForagerConfig foragerConfig = new LocalSearchForagerConfig();
        foragerConfig.setAcceptedCountLimit(FORAGER_CONFIG_ACCEPTED_COUNT_LIMIT);

        localSearchPhaseConfig.setMoveSelectorConfig(moveSelectorConfig);
        localSearchPhaseConfig.setAcceptorConfig(new AcceptorConfig().
                withEntityTabuSize(ACCEPTOR_CONFIG_ENTITY_TABU_SIZE));
        localSearchPhaseConfig.setForagerConfig(foragerConfig);
        return localSearchPhaseConfig;
    }

    @Benchmark
    public NurseRoster benchmark() {
        return super.benchmark();
    }
}
