package org.jboss.qa.brms.performance.localsearch.tsp;

import org.jboss.qa.brms.performance.examples.tsp.TravelingSalesmanProblem;
import org.jboss.qa.brms.performance.examples.tsp.domain.TravelingSalesmanTour;
import org.jboss.qa.brms.performance.examples.tsp.solver.phase.TSPSolutionInitializer;
import org.jboss.qa.brms.performance.localsearch.AbstractLocalSearchPlannerBenchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchForagerConfig;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.phase.custom.CustomPhaseConfig;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;

import java.util.Collections;

public abstract class AbstractTSPLocalSearchBenchmark
        extends AbstractLocalSearchPlannerBenchmark<TravelingSalesmanTour> {

    @Param({"LU_980", "USA_CA_2716", "GREECE_9882"})
    private TravelingSalesmanProblem.DataSet dataset;

    @Override
    protected TravelingSalesmanTour createInitialSolution() {
        TravelingSalesmanProblem travelingSalesmanProblem = new TravelingSalesmanProblem();
        TravelingSalesmanTour solution = travelingSalesmanProblem.loadSolvingProblem(dataset);
        SolverFactory<TravelingSalesmanTour> defaultConstruction = travelingSalesmanProblem.getBaseSolverFactory();
        CustomPhaseConfig customPhaseConfig = new CustomPhaseConfig();
        customPhaseConfig.setCustomPhaseCommandClassList(
                Collections.<Class<? extends CustomPhaseCommand>>singletonList(TSPSolutionInitializer.class));
        defaultConstruction.getSolverConfig()
                .setPhaseConfigList(Collections.singletonList((PhaseConfig) customPhaseConfig));
        Solver<TravelingSalesmanTour> constructionSolver = defaultConstruction.buildSolver();
        constructionSolver.solve(solution);
        return constructionSolver.getBestSolution();
    }

    @Override
    public void initSolver() {
        SolverFactory<TravelingSalesmanTour> solverFactory = new TravelingSalesmanProblem().getBaseSolverFactory();
        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        localSearchPhaseConfig.setMoveSelectorConfig(new UnionMoveSelectorConfig());
        ((UnionMoveSelectorConfig) localSearchPhaseConfig.getMoveSelectorConfig())
                .setMoveSelectorConfigList(createMoveSelectorConfigList());

        localSearchPhaseConfig.setAcceptorConfig(createAcceptorConfig());

        localSearchPhaseConfig.setForagerConfig(new LocalSearchForagerConfig());
        localSearchPhaseConfig.getForagerConfig().setAcceptedCountLimit(getAcceptedCountLimit());

        localSearchPhaseConfig.setTerminationConfig(getTerminationConfig());
        solverFactory.getSolverConfig()
                .setPhaseConfigList(Collections.<PhaseConfig>singletonList(localSearchPhaseConfig));
        super.setSolver(solverFactory.buildSolver());
    }

}
