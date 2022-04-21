package org.jboss.qa.brms.performance.construction.tsp;

import org.jboss.qa.brms.performance.construction.AbstractConstructionHeuristicBenchmark;
import org.jboss.qa.brms.performance.examples.Examples;
import org.jboss.qa.brms.performance.examples.tsp.TravelingSalesmanExample;
import org.optaplanner.examples.tsp.domain.TspSolution;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;

public class TSPConstructionBenchmark extends AbstractConstructionHeuristicBenchmark<TspSolution> {

    public TSPConstructionBenchmark() {
        super(Examples.TRAVELING_SALESMAN);
    }

    @Param({"FIRST_FIT", "FIRST_FIT_DECREASING"})
    private ConstructionHeuristicType constructionHeuristicType;

    @Param({"LU_980", "USA_CA_2716", "GREECE_9882"})
    private TravelingSalesmanExample.DataSet dataset;

    @Override
    protected TspSolution createInitialSolution() {
        return Examples.TRAVELING_SALESMAN.loadSolvingProblem(dataset);
    }

    @Override
    public ConstructionHeuristicType getConstructionHeuristicType() {
        return constructionHeuristicType;
    }

    @Benchmark
    public TspSolution benchmark() {
        return runBenchmark();
    }
}
