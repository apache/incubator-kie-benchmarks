package org.jboss.qa.brms.performance.construction.tsp;

import org.jboss.qa.brms.performance.construction.AbstractConstructionHeuristicPlannerBenchmark;
import org.jboss.qa.brms.performance.examples.tsp.TravelingSalesmanProblem;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.solver.SolverConfig;

import java.util.Collections;

public class TSPConstructionBenchmark extends AbstractConstructionHeuristicPlannerBenchmark {

    @Param({"FIRST_FIT", "FIRST_FIT_DECREASING"})
    private ConstructionHeuristicType constructionHeuristicType;

    @Param({"LU_980", "USA_CA_2716", "GREECE_9882"})
    private TravelingSalesmanProblem.DataSet dataset;

    @Override
    public void initSolver() {
        SolverConfig config = new TravelingSalesmanProblem().getBaseConfig();
        ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig = new ConstructionHeuristicPhaseConfig();
        constructionHeuristicPhaseConfig.setConstructionHeuristicType(getConstructionHeuristicType());
        config.setPhaseConfigList(Collections.singletonList(((PhaseConfig) constructionHeuristicPhaseConfig)));
        super.setSolver(config.buildSolver());
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
