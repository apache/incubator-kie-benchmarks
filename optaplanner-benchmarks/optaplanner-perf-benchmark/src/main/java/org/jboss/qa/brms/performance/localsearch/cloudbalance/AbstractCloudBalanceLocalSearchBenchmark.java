package org.jboss.qa.brms.performance.localsearch.cloudbalance;

import org.jboss.qa.brms.performance.examples.cloudbalancing.CloudBalancing;
import org.jboss.qa.brms.performance.examples.cloudbalancing.solver.phase.CloudBalanceSolutionInitializer;
import org.jboss.qa.brms.performance.localsearch.AbstractLocalSearchPlannerBenchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchForagerConfig;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.phase.custom.CustomPhaseConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;

import java.util.Collections;

public abstract class AbstractCloudBalanceLocalSearchBenchmark extends AbstractLocalSearchPlannerBenchmark {

    @Param({"CB_100_300", "CB_1600_4800", "CB_10000_30000"})
    private CloudBalancing.DataSet dataset;

    @Override
    protected Solution createInitialSolution() {
        CloudBalancing cloudBalancing = new CloudBalancing();
        Solution solution = cloudBalancing.loadSolvingProblem(dataset);
        SolverConfig defaultConstruction = cloudBalancing.getBaseConfig();
//        ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig = new ConstructionHeuristicPhaseConfig();
//        constructionHeuristicPhaseConfig.setConstructionHeuristicType(ConstructionHeuristicType.FIRST_FIT);
//        defaultConstruction.setPhaseConfigList(Collections.singletonList(((PhaseConfig) constructionHeuristicPhaseConfig)));
        CustomPhaseConfig customPhaseConfig = new CustomPhaseConfig();
        customPhaseConfig.setCustomPhaseCommandClassList(
                Collections.<Class<? extends CustomPhaseCommand>>singletonList(CloudBalanceSolutionInitializer.class));
        defaultConstruction.setPhaseConfigList(Collections.singletonList((PhaseConfig) customPhaseConfig));
        Solver constructionSolver = defaultConstruction.buildSolver();
        constructionSolver.solve(solution);
        return constructionSolver.getBestSolution();
    }

    @Override
    public void initSolver() {
        SolverConfig config = new CloudBalancing().getBaseConfig();
        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        localSearchPhaseConfig.setMoveSelectorConfig(new UnionMoveSelectorConfig());
        ((UnionMoveSelectorConfig) localSearchPhaseConfig.getMoveSelectorConfig())
                .setMoveSelectorConfigList(createMoveSelectorConfigList());

        localSearchPhaseConfig.setAcceptorConfig(createAcceptorConfig());

        localSearchPhaseConfig.setForagerConfig(new LocalSearchForagerConfig());
        localSearchPhaseConfig.getForagerConfig().setAcceptedCountLimit(getAcceptedCountLimit());

        localSearchPhaseConfig.setTerminationConfig(getTerminationConfig());
        config.setPhaseConfigList(Collections.<PhaseConfig>singletonList(localSearchPhaseConfig));
        super.setSolver(config.buildSolver());
    }

}
