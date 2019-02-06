package org.jboss.qa.brms.performance.realworld.conferencescheduling;

import java.util.Arrays;
import java.util.Collections;

import org.jboss.qa.brms.performance.AbstractPlannerBenchmark;
import org.jboss.qa.brms.performance.calculatecounttermination.ConferenceSchedulingTermination;
import org.jboss.qa.brms.performance.examples.conferencescheduling.ConferenceScheduling;
import org.jboss.qa.brms.performance.examples.conferencescheduling.domain.ConferenceSolution;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.domain.ScanAnnotatedClassesConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.LocalSearchType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

public class ConferenceSchedulingBenchmark extends AbstractPlannerBenchmark<ConferenceSolution> {

    private static final String CONFERENCE_SCHEDULING_DOMAIN_PACKAGE =
            "org.jboss.qa.brms.performance.examples.conferencescheduling";

    private static final String CONFERENCE_SCHEDULING_DROOLS_SCORE_RULES_FILE =
            "org/jboss/qa/brms/performance/examples/conferencescheduling/solver/conferenceSchedulingScoreRules.drl";

    @Param({"TALKS_36_TIMESLOTS_12_ROOMS_5", "TALKS_108_TIMESLOTS_18_ROOMS_10", "TALKS_216_TIMESLOTS_18_ROOMS_20"})
    private ConferenceScheduling.DataSet dataSet;

    @Override
    public void initSolution() {
        super.setSolution(new ConferenceScheduling().loadSolvingProblem(dataSet));
    }

    @Override
    public void initSolver() {
        SolverFactory<ConferenceSolution> solverFactory = SolverFactory.createEmpty();
        SolverConfig solverConfig = solverFactory.getSolverConfig();

        ScanAnnotatedClassesConfig scanAnnotatedClassesConfig = new ScanAnnotatedClassesConfig();
        scanAnnotatedClassesConfig.setPackageIncludeList(Collections.
                singletonList(CONFERENCE_SCHEDULING_DOMAIN_PACKAGE));

        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        scoreDirectorFactoryConfig.setScoreDrlList(Collections.
                singletonList(CONFERENCE_SCHEDULING_DROOLS_SCORE_RULES_FILE));

        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        localSearchPhaseConfig.setLocalSearchType(LocalSearchType.TABU_SEARCH);

        solverConfig.setPhaseConfigList(Arrays.asList(new ConstructionHeuristicPhaseConfig(), localSearchPhaseConfig));
        solverConfig.setScoreDirectorFactoryConfig(scoreDirectorFactoryConfig);
        solverConfig.setScanAnnotatedClassesConfig(scanAnnotatedClassesConfig);
        solverConfig.setTerminationConfig(new TerminationConfig().
                withTerminationClass(ConferenceSchedulingTermination.class));

        super.setSolver(solverFactory.buildSolver());
    }

    @Benchmark
    public ConferenceSolution benchmark() {
        return super.benchmark();
    }
}
