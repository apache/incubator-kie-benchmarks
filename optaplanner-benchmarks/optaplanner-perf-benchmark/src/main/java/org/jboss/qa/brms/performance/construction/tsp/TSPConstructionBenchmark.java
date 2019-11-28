package org.jboss.qa.brms.performance.construction.tsp;

import org.jboss.qa.brms.performance.construction.AbstractConstructionHeuristicBenchmark;
import org.jboss.qa.brms.performance.examples.tsp.TravelingSalesmanProblem;
import org.optaplanner.examples.tsp.domain.TspSolution;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;

public class TSPConstructionBenchmark extends AbstractConstructionHeuristicBenchmark<TspSolution, TravelingSalesmanProblem> {

    public TSPConstructionBenchmark() {
        super(TravelingSalesmanProblem.class);
    }

    @Param({"FIRST_FIT", "FIRST_FIT_DECREASING"})
    private ConstructionHeuristicType constructionHeuristicType;

    @Param({"LU_980", "USA_CA_2716", "GREECE_9882"})
    private TravelingSalesmanProblem.DataSet dataset;

    @Override
    public void initSolution() {
        super.setSolution(example.loadSolvingProblem(dataset));
    }

    @Override
    public ConstructionHeuristicType getConstructionHeuristicType() {
        return constructionHeuristicType;
    }

    @Benchmark
    @Override
    public TspSolution benchmark() {
        return super.benchmark();
    }
}
