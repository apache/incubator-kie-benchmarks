package org.jboss.qa.brms.performance.construction.cloudbalance;

import org.jboss.qa.brms.performance.construction.AbstractConstructionHeuristicBenchmark;
import org.jboss.qa.brms.performance.examples.Examples;
import org.jboss.qa.brms.performance.examples.cloudbalancing.CloudBalancingExample;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;

public class CloudBalanceConstructionBenchmark extends AbstractConstructionHeuristicBenchmark<CloudBalance> {

    @Param({"FIRST_FIT", "FIRST_FIT_DECREASING"})
    private ConstructionHeuristicType constructionHeuristicType;

    @Param({"CB_100_300", "CB_1600_4800", "CB_10000_30000"})
    private CloudBalancingExample.DataSet dataset;

    public CloudBalanceConstructionBenchmark() {
        super(Examples.CLOUD_BALANCING);
    }

    @Override
    protected CloudBalance createInitialSolution() {
        return Examples.CLOUD_BALANCING.loadSolvingProblem(dataset);
    }

    @Override
    public ConstructionHeuristicType getConstructionHeuristicType() {
        return constructionHeuristicType;
    }

    @Benchmark
    public CloudBalance benchmark() {
        return runBenchmark();
    }
}
