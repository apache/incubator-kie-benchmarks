package org.jboss.qa.brms.performance.realworld.conferencescheduling;

import java.util.Arrays;

import org.jboss.qa.brms.performance.AbstractPlannerBenchmark;
import org.jboss.qa.brms.performance.examples.Examples;
import org.jboss.qa.brms.performance.examples.conferencescheduling.ConferenceSchedulingExample;
import org.jboss.qa.brms.performance.examples.conferencescheduling.termination.ConferenceSchedulingTermination;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Warmup;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.LocalSearchType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceSolution;
import org.optaplanner.examples.conferencescheduling.domain.Talk;
import org.optaplanner.examples.conferencescheduling.score.ConferenceSchedulingConstraintProvider;

@Warmup(iterations = 15)
public class ConferenceSchedulingBenchmark extends AbstractPlannerBenchmark<ConferenceSolution> {

    @Param({"TALKS_36_TIMESLOTS_12_ROOMS_5", "TALKS_108_TIMESLOTS_18_ROOMS_10", "TALKS_216_TIMESLOTS_18_ROOMS_20"})
    private ConferenceSchedulingExample.DataSet dataset;

    public ConferenceSchedulingBenchmark() {
    }

    public ConferenceSchedulingBenchmark(ConferenceSchedulingExample.DataSet dataSet) {
        this.dataset = dataSet;
    }

    @Override
    protected ConferenceSolution createInitialSolution() {
        return Examples.CONFERENCE_SCHEDULING.loadSolvingProblem(dataset);
    }

    @Override
    protected Solver<ConferenceSolution> createSolver() {
        // the pre-defined configuration in ConferenceScheduling cannot be used
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.withEntityClasses(Talk.class);
        solverConfig.withSolutionClass(ConferenceSolution.class);

        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        scoreDirectorFactoryConfig.setConstraintProviderClass(ConferenceSchedulingConstraintProvider.class);

        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        localSearchPhaseConfig.setLocalSearchType(LocalSearchType.TABU_SEARCH);

        solverConfig.setPhaseConfigList(Arrays.asList(new ConstructionHeuristicPhaseConfig(), localSearchPhaseConfig));
        solverConfig.setScoreDirectorFactoryConfig(scoreDirectorFactoryConfig);
        solverConfig.setTerminationConfig(new TerminationConfig().
                withTerminationClass(ConferenceSchedulingTermination.class));

        SolverFactory<ConferenceSolution> solverFactory = SolverFactory.create(solverConfig);
        return solverFactory.buildSolver();
    }

    @Benchmark
    public ConferenceSolution benchmark() {
        return runBenchmark();
    }
}
