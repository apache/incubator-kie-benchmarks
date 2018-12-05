package org.jboss.qa.brms.performance.realworld.cloudbalancing;

import java.util.Collections;

import org.jboss.qa.brms.performance.AbstractPlannerBenchmark;
import org.jboss.qa.brms.performance.calculatecounttermination.CloudBalanceCalculateCountTermination;
import org.jboss.qa.brms.performance.examples.cloudbalancing.CloudBalancing;
import org.jboss.qa.brms.performance.examples.cloudbalancing.domain.CloudBalance;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.domain.ScanAnnotatedClassesConfig;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

public class CloudBalanceBenchmark extends AbstractPlannerBenchmark<CloudBalance> {

    @Param({"CB_100_300", "CB_1600_4800", "CB_10000_30000"})
    private CloudBalancing.DataSet dataset;

    @Override
    public void initSolution() {
        super.setSolution(new CloudBalancing().loadSolvingProblem(dataset));
    }

    @Override
    public void initSolver() {
        SolverFactory<CloudBalance> solverFactory = SolverFactory.createEmpty();
        SolverConfig config = solverFactory.getSolverConfig();

        ScanAnnotatedClassesConfig scanAnnotatedClassesConfig = new ScanAnnotatedClassesConfig();
        scanAnnotatedClassesConfig.setPackageIncludeList(Collections.singletonList("org.jboss.qa.brms.performance.examples.cloudbalancing"));

        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        scoreDirectorFactoryConfig.setInitializingScoreTrend("ONLY_DOWN");
        scoreDirectorFactoryConfig.setScoreDrlList(Collections.singletonList("org/jboss/qa/brms/performance/examples/" +
                                                                                     "cloudbalancing/solver/cloudBalancingScoreRules.drl"));
        config.setScoreDirectorFactoryConfig(scoreDirectorFactoryConfig);
        config.setTerminationConfig(new TerminationConfig().withTerminationClass(CloudBalanceCalculateCountTermination.class));
        config.setScanAnnotatedClassesConfig(scanAnnotatedClassesConfig);

        super.setSolver(solverFactory.buildSolver());
    }

    @Benchmark
    public CloudBalance benchmark() {
        return super.benchmark();
    }
}
