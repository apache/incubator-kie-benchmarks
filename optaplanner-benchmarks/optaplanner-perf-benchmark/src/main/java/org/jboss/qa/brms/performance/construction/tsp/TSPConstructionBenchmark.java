package org.jboss.qa.brms.performance.construction.tsp;

import org.jboss.qa.brms.performance.construction.AbstractConstructionHeuristicPlannerBenchmark;
import org.jboss.qa.brms.performance.examples.tsp.TravelingSalesmanProblem;
import org.jboss.qa.brms.performance.examples.tsp.domain.TravelingSalesmanTour;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.core.config.phase.PhaseConfig;

import java.util.Collections;

public class TSPConstructionBenchmark extends AbstractConstructionHeuristicPlannerBenchmark<TravelingSalesmanTour> {

    @Param({"FIRST_FIT", "FIRST_FIT_DECREASING"})
    private ConstructionHeuristicType constructionHeuristicType;

    @Param({"LU_980", "USA_CA_2716", "GREECE_9882"})
    private TravelingSalesmanProblem.DataSet dataset;

    @Override
    public void initSolver() {
        SolverFactory<TravelingSalesmanTour> solverFactory = new TravelingSalesmanProblem().getBaseSolverFactory();
        ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig = new ConstructionHeuristicPhaseConfig();
        constructionHeuristicPhaseConfig.setConstructionHeuristicType(getConstructionHeuristicType());
        solverFactory.getSolverConfig()
                .setPhaseConfigList(Collections.singletonList(((PhaseConfig) constructionHeuristicPhaseConfig)));
        super.setSolver(solverFactory.buildSolver());
    }

    @Override
    public void initSolution() {
        super.setSolution(new TravelingSalesmanProblem().loadSolvingProblem(dataset));
    }

    @Override
    public ConstructionHeuristicType getConstructionHeuristicType() {
        return constructionHeuristicType;
    }

    @Benchmark
    @Override
    public void benchmark() {
        super.benchmark();
    }

}
