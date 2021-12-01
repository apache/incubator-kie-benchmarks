package org.jboss.qa.brms.performance.realworld.cloudbalancing;

import java.util.ArrayList;
import java.util.Collections;

import org.jboss.qa.brms.performance.AbstractPlannerBenchmark;
import org.jboss.qa.brms.performance.examples.Examples;
import org.jboss.qa.brms.performance.examples.cloudbalancing.CloudBalancingExample;
import org.jboss.qa.brms.performance.examples.cloudbalancing.termination.CloudBalanceCalculateCountTermination;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;
import org.optaplanner.examples.cloudbalancing.score.CloudBalancingConstraintProvider;

public class CloudBalanceBenchmark extends AbstractPlannerBenchmark<CloudBalance> {


    @Param({"SMALLEST", "CB_1600_4800", "CB_10000_30000"})
    private CloudBalancingExample.DataSet dataSet;

    @Override
    protected CloudBalance createInitialSolution() {
        return Examples.CLOUD_BALANCING.loadSolvingProblem(dataSet);
    }

    @Override
    protected Solver<CloudBalance> createSolver() {
        // the pre-defined configuration in CloudBalancing cannot be used
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.withEntityClasses(CloudProcess.class);
        solverConfig.withSolutionClass(CloudBalance.class);

        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        scoreDirectorFactoryConfig.setInitializingScoreTrend("ONLY_DOWN");
        scoreDirectorFactoryConfig.setConstraintProviderClass(CloudBalancingConstraintProvider.class);

        solverConfig.setScoreDirectorFactoryConfig(scoreDirectorFactoryConfig);
        solverConfig.setTerminationConfig(new TerminationConfig().withTerminationClass(
                CloudBalanceCalculateCountTermination.class));

        SolverFactory<CloudBalance> solverFactory = SolverFactory.create(solverConfig);
        return solverFactory.buildSolver();
    }

    @Benchmark
    public CloudBalance benchmark() {
        return runBenchmark();
    }

    public static void main(String[] args) {
        CloudBalanceBenchmark benchmark = new CloudBalanceBenchmark();
        Solver<CloudBalance> solver = benchmark.createSolver();
        solver.solve(benchmark.createInitialSolution());
    }
}
