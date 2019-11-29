package org.jboss.qa.brms.performance.realworld.cloudbalancing;

import java.util.Collections;

import org.jboss.qa.brms.performance.AbstractPlannerBenchmark;
import org.jboss.qa.brms.performance.examples.Examples;
import org.jboss.qa.brms.performance.examples.cloudbalancing.CloudBalancingExample;
import org.jboss.qa.brms.performance.examples.cloudbalancing.termination.CloudBalanceCalculateCountTermination;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.domain.ScanAnnotatedClassesConfig;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;

public class CloudBalanceBenchmark extends AbstractPlannerBenchmark<CloudBalance> {

    private static final String CLOUD_BALANCING_DROOLS_SCORE_RULES_FILE =
            "org/optaplanner/examples/cloudbalancing/solver/cloudBalancingScoreRules.drl";

    @Param({"CB_100_300", "CB_1600_4800", "CB_10000_30000"})
    private CloudBalancingExample.DataSet dataset;

    @Override
    protected CloudBalance createInitialSolution() {
        return Examples.CLOUD_BALANCING.loadSolvingProblem(dataset);
    }

    @Override
    protected Solver<CloudBalance> createSolver() {
        // the pre-defined configuration in CloudBalancing cannot be used
        SolverConfig solverConfig = new SolverConfig();

        ScanAnnotatedClassesConfig scanAnnotatedClassesConfig = new ScanAnnotatedClassesConfig();
        scanAnnotatedClassesConfig.setPackageIncludeList(Collections.singletonList(
                CloudBalance.class.getPackage().getName()));

        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        scoreDirectorFactoryConfig.setInitializingScoreTrend("ONLY_DOWN");
        scoreDirectorFactoryConfig.setScoreDrlList(Collections.singletonList(CLOUD_BALANCING_DROOLS_SCORE_RULES_FILE));
        solverConfig.setScoreDirectorFactoryConfig(scoreDirectorFactoryConfig);
        solverConfig.setTerminationConfig(new TerminationConfig().withTerminationClass(
                CloudBalanceCalculateCountTermination.class));
        solverConfig.setScanAnnotatedClassesConfig(scanAnnotatedClassesConfig);

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
