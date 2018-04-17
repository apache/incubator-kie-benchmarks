package org.jboss.qa.brms.performance.localsearch.tsp;

import org.jboss.qa.brms.performance.examples.tsp.TravelingSalesmanProblem;
import org.jboss.qa.brms.performance.examples.tsp.solver.phase.TSPSolutionInitializer;
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

public abstract class AbstractTSPLocalSearchBenchmark extends AbstractLocalSearchPlannerBenchmark {

    @Param({"LU_980", "USA_CA_2716", "GREECE_9882"})
    private TravelingSalesmanProblem.DataSet dataset;

    @Override
    protected Solution createInitialSolution() {
        TravelingSalesmanProblem travelingSalesmanProblem = new TravelingSalesmanProblem();
        Solution solution = travelingSalesmanProblem.loadSolvingProblem(dataset);
        SolverConfig defaultConstruction = travelingSalesmanProblem.getBaseConfig();
//        ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig = new ConstructionHeuristicPhaseConfig();
//        constructionHeuristicPhaseConfig.setConstructionHeuristicType(ConstructionHeuristicType.FIRST_FIT);
//        defaultConstruction.setPhaseConfigList(Collections.singletonList(((PhaseConfig) constructionHeuristicPhaseConfig)));
        CustomPhaseConfig customPhaseConfig = new CustomPhaseConfig();
        customPhaseConfig.setCustomPhaseCommandClassList(
                Collections.<Class<? extends CustomPhaseCommand>>singletonList(TSPSolutionInitializer.class));
        defaultConstruction.setPhaseConfigList(Collections.singletonList((PhaseConfig) customPhaseConfig));
        Solver constructionSolver = defaultConstruction.buildSolver();
        constructionSolver.solve(solution);
        return constructionSolver.getBestSolution();
    }

    @Override
    public void initSolver() {
        SolverConfig config = new TravelingSalesmanProblem().getBaseConfig();
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
