package org.jboss.qa.brms.performance.construction.cloudbalance;

import org.jboss.qa.brms.performance.construction.AbstractConstructionHeuristicPlannerBenchmark;
import org.jboss.qa.brms.performance.examples.cloudbalancing.CloudBalancing;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.solver.SolverConfig;

import java.util.Collections;

public class CloudBalanceConstructionBenchmark extends AbstractConstructionHeuristicPlannerBenchmark {

    @Param({"FIRST_FIT", "FIRST_FIT_DECREASING"})
    private ConstructionHeuristicType constructionHeuristicType;

    @Param({"CB_100_300", "CB_1600_4800", "CB_10000_30000"})
    private CloudBalancing.DataSet dataset;

    @Override
    public void initSolver() {
        SolverConfig config = new CloudBalancing().getBaseConfig();
        ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig = new ConstructionHeuristicPhaseConfig();
        constructionHeuristicPhaseConfig.setConstructionHeuristicType(getConstructionHeuristicType());
        config.setPhaseConfigList(Collections.singletonList(((PhaseConfig) constructionHeuristicPhaseConfig)));
        super.setSolver(config.buildSolver());
    }

    @Override
    public void initSolution() {
        super.setSolution(new CloudBalancing().loadSolvingProblem(dataset));
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
