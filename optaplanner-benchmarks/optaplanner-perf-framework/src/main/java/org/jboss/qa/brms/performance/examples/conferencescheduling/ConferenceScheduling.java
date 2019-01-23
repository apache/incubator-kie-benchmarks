package org.jboss.qa.brms.performance.examples.conferencescheduling;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import org.jboss.qa.brms.performance.examples.AbstractExample;
import org.jboss.qa.brms.performance.examples.conferencescheduling.domain.ConferenceSolution;
import org.jboss.qa.brms.performance.examples.conferencescheduling.persistence.ConferenceSchedulingDao;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.domain.ScanAnnotatedClassesConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.LocalSearchType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;

public class ConferenceScheduling extends AbstractExample<ConferenceSolution> {

    public enum DataSet {
        TALKS_36_TIMESLOTS_12_ROOMS_5("36talks-12timeslots-5rooms.xlsx"),
        TALKS_108_TIMESLOTS_18_ROOMS_10("108talks-18timeslots-10rooms.xlsx"),
        TALKS_216_TIMESLOTS_18_ROOMS_20("216talks-18timeslots-20rooms.xlsx");

        private String filename;

        DataSet(String file) {
            this.filename = file;
        }

        public String getFilename() {
            return filename;
        }
    }

    public static final String PATH_TO_SOLVER_CONFIG =
            "org/jboss/qa/brms/performance/examples/conferencescheduling/solver/conferenceSchedulingSolverConfig.xml";
    
    public static final String PATH_TO_DROOLS_SCORE_RULES =
            "org/jboss/qa/brms/performance/examples/conferencescheduling/solver/conferenceSchedulingScoreRules.drl";

    public static final String DOMAIN_PACKAGE_NAME =
            "org.jboss.qa.brms.performance.examples.conferencescheduling.domain";


    private ConferenceSchedulingDao dao = new ConferenceSchedulingDao();

    public ConferenceSolution loadSolvingProblem(DataSet dataset) {
        return loadSolvingProblem(new File(dao.getDataDir(), dataset.getFilename()));
    }

    @Override
    public ConferenceSolution loadSolvingProblem(File f) {
        return dao.readSolution(f);
    }

    @Override
    public SolverFactory<ConferenceSolution> getDefaultSolverFactory() {
        return SolverFactory.createFromXmlInputStream(this.getClass().
                getResourceAsStream('/' + PATH_TO_SOLVER_CONFIG));
    }

    @Override
    public SolverFactory<ConferenceSolution> getBaseSolverFactory() {
        SolverFactory<ConferenceSolution> solverFactory = SolverFactory.createEmpty();
        SolverConfig solverConfig = solverFactory.getSolverConfig();

        ScanAnnotatedClassesConfig scanAnnotatedClassesConfig = new ScanAnnotatedClassesConfig();
        scanAnnotatedClassesConfig.setPackageIncludeList(Collections.singletonList(DOMAIN_PACKAGE_NAME));

        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        scoreDirectorFactoryConfig.setScoreDrlList(Collections.singletonList(PATH_TO_DROOLS_SCORE_RULES));

        ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig = new ConstructionHeuristicPhaseConfig();
        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        localSearchPhaseConfig.setLocalSearchType(LocalSearchType.TABU_SEARCH);

        solverConfig.setPhaseConfigList(Arrays.asList(constructionHeuristicPhaseConfig,localSearchPhaseConfig));
        solverConfig.setScanAnnotatedClassesConfig(scanAnnotatedClassesConfig);
        solverConfig.setScoreDirectorFactoryConfig(scoreDirectorFactoryConfig);

        return solverFactory;
    }
}

