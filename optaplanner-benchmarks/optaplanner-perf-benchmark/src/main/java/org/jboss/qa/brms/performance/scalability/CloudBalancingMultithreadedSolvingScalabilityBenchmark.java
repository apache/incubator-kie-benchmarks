package org.jboss.qa.brms.performance.scalability;

import java.util.Collections;

import org.jboss.qa.brms.performance.configuration.MoveSelectorConfigurations;
import org.jboss.qa.brms.performance.examples.cloudbalancing.CloudBalancing;
import org.jboss.qa.brms.performance.examples.cloudbalancing.domain.CloudBalance;
import org.jboss.qa.brms.performance.examples.cloudbalancing.solver.phase.CloudBalanceSolutionInitializer;
import org.jboss.qa.brms.performance.profiler.MemoryConsumptionProfiler;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorConfig;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.phase.custom.CustomPhaseConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;

public class CloudBalancingMultithreadedSolvingScalabilityBenchmark
        extends AbstractMultithreadedSolvingScalabilityBenchmark<CloudBalance> {

    private final static CloudBalancing CLOUD_BALANCING = new CloudBalancing();

    @Param({"CB_100_300", "CB_1600_4800", "CB_10000_30000"})
    private CloudBalancing.DataSet dataset;

    @Override
    protected TerminationConfig getTerminationConfig() {
        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setStepCountLimit(500);
        return terminationConfig;
    }

    @Override
    protected SolverFactory<CloudBalance> getSolverFactory() {
        return CLOUD_BALANCING.getBaseSolverFactory();
    }

    @Override
    protected MoveSelectorConfig getMoveSelectorConfig() {
        UnionMoveSelectorConfig unionMoveSelectorConfig = new UnionMoveSelectorConfig();
        unionMoveSelectorConfig.setMoveSelectorConfigList(MoveSelectorConfigurations.createAllNonChainedSelectorList());
        return unionMoveSelectorConfig;
    }

    @Override
    protected AcceptorConfig getAcceptorConfig() {
        AcceptorConfig acceptorConfig = new AcceptorConfig();
        acceptorConfig.setLateAcceptanceSize(100);
        return acceptorConfig;
    }

    @Override
    protected CloudBalance getInitialSolution() {
        CloudBalance solution = CLOUD_BALANCING.loadSolvingProblem(dataset);
        SolverFactory<CloudBalance> defaultConstruction = CLOUD_BALANCING.getBaseSolverFactory();
        CustomPhaseConfig customPhaseConfig = new CustomPhaseConfig();
        customPhaseConfig.setCustomPhaseCommandClassList(
                Collections.<Class<? extends CustomPhaseCommand>>singletonList(CloudBalanceSolutionInitializer.class));
        defaultConstruction.getSolverConfig()
                .setPhaseConfigList(Collections.singletonList((PhaseConfig) customPhaseConfig));
        Solver<CloudBalance> constructionSolver = defaultConstruction.buildSolver();
        constructionSolver.solve(solution);
        return constructionSolver.getBestSolution();
    }

    @Override
    protected int getAcceptedCountLimit() {
        return 100;
    }

    @Benchmark
    public CloudBalance benchmark() {
        return super.benchmark();
    }

    public static void main(String... args) throws Exception {
        Options opts = new OptionsBuilder()
                .addProfiler(MemoryConsumptionProfiler.class)
                .include("CloudBalancingMultithreadedSolvingScalabilityBenchmark")
                .build();
        new Runner(opts).run();
    }
}
