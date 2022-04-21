package org.jboss.qa.brms.performance.examples.flightcrewscheduling;

import java.io.File;
import java.util.Collections;

import org.jboss.qa.brms.performance.examples.AbstractExample;
import org.jboss.qa.brms.performance.examples.Examples;
import org.jboss.qa.brms.performance.examples.flightcrewscheduling.persistence.FlightCrewSchedulingDao;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.domain.ScanAnnotatedClassesConfig;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.examples.flightcrewscheduling.domain.FlightCrewSolution;
import org.optaplanner.examples.flightcrewscheduling.optional.score.FlightCrewSchedulingConstraintProvider;

public class FlightCrewSchedulingExample extends AbstractExample<FlightCrewSolution> {

    private static final String SOLVER_CONFIG =
            "org/jboss/qa/brms/performance/examples/flightcrewscheduling/solver/flightCrewSchedulingSolverConfig.xml";

    private FlightCrewSchedulingDao dao = new FlightCrewSchedulingDao();

    public FlightCrewSolution createInitialSolution(DataSet dataSet) {
        ConstructionHeuristicPhaseConfig chPhaseConfig = new ConstructionHeuristicPhaseConfig();

        SolverConfig solverConfig = Examples.FLIGHT_CREW_SCHEDULING.getBaseSolverConfig();
        solverConfig.setPhaseConfigList(Collections.singletonList(chPhaseConfig));

        SolverFactory<FlightCrewSolution> solverFactory = SolverFactory.create(solverConfig);
        Solver<FlightCrewSolution> constructionSolver = solverFactory.buildSolver();

        FlightCrewSolution solution = Examples.FLIGHT_CREW_SCHEDULING.loadSolvingProblem(dataSet);
        constructionSolver.solve(solution);
        return constructionSolver.getBestSolution();
    }

    public FlightCrewSolution loadSolvingProblem(DataSet dataSet) {
        return loadSolvingProblem(new File(dao.getDataDir(), dataSet.getFilename()));
    }

    @Override
    public FlightCrewSolution loadSolvingProblem(File file) {
        return dao.readSolution(file);
    }

    @Override
    public SolverConfig getBaseSolverConfig() {
        SolverConfig solverConfig = new SolverConfig();

        ScanAnnotatedClassesConfig scanAnnotatedClassesConfig = new ScanAnnotatedClassesConfig();
        scanAnnotatedClassesConfig.setPackageIncludeList(
                Collections.singletonList(FlightCrewSolution.class.getPackage().getName()));
        solverConfig.setScanAnnotatedClassesConfig(scanAnnotatedClassesConfig);

        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        scoreDirectorFactoryConfig.setConstraintProviderClass(FlightCrewSchedulingConstraintProvider.class);
        scoreDirectorFactoryConfig.setInitializingScoreTrend("ONLY_DOWN");
        solverConfig.setScoreDirectorFactoryConfig(scoreDirectorFactoryConfig);

        return solverConfig;
    }

    @Override
    protected String getSolverConfigResource() {
        return SOLVER_CONFIG;
    }

    public enum DataSet {
        EUROPE_175_FLIGHTS_7_DAYS("175flights-7days-Europe.xlsx"),
        EUROPE_700_FLIGHTS_28_DAYS("700flights-28days-Europe.xlsx");

        DataSet(String file) {
            this.filename = file;
        }

        private String filename;

        public String getFilename() {
            return filename;
        }
    }
}
