package org.jboss.qa.brms.performance.examples.vehiclerouting;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import org.jboss.qa.brms.performance.examples.AbstractExample;
import org.jboss.qa.brms.performance.examples.Examples;
import org.jboss.qa.brms.performance.examples.vehiclerouting.persistence.VehicleRoutingDao;
import org.jboss.qa.brms.performance.examples.vehiclerouting.solution.VehicleRoutingSolutionInitializer;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.phase.custom.CustomPhaseConfig;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Standstill;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.solver.score.VehicleRoutingIncrementalScoreCalculator;

public final class VehicleRoutingExample extends AbstractExample<VehicleRoutingSolution> {

    private static final String SOLVER_CONFIG =
            "org/jboss/qa/brms/performance/examples/vrp/solver/vehicleRoutingSolverConfig.xml";

    private final VehicleRoutingDao dao = new VehicleRoutingDao();

    public VehicleRoutingSolution loadSolvingProblem(DataSet dataset) {
        return loadSolvingProblem(new File(dao.getDataDir(), dataset.getFilename()));
    }

    public VehicleRoutingSolution createInitialSolution(DataSet dataSet) {
        CustomPhaseConfig customPhaseConfig = new CustomPhaseConfig();
        customPhaseConfig.setCustomPhaseCommandClassList(
                Collections.singletonList(VehicleRoutingSolutionInitializer.class));
        SolverConfig solverConfig = Examples.VEHICLE_ROUTING.getBaseSolverConfig();
        solverConfig.setPhaseConfigList(Collections.singletonList(customPhaseConfig));

        SolverFactory<VehicleRoutingSolution> solverFactory = SolverFactory.create(solverConfig);
        Solver<VehicleRoutingSolution> constructionSolver = solverFactory.buildSolver();

        VehicleRoutingSolution solution = Examples.VEHICLE_ROUTING.loadSolvingProblem(dataSet);
        constructionSolver.solve(solution);
        return constructionSolver.getBestSolution();
    }

    @Override
    public VehicleRoutingSolution loadSolvingProblem(File file) {
        return dao.readSolution(file);
    }

    @Override
    public SolverConfig getBaseSolverConfig() {
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setSolutionClass(VehicleRoutingSolution.class);
        solverConfig.setEnvironmentMode(EnvironmentMode.REPRODUCIBLE);
        ArrayList<Class<?>> classes = new ArrayList<>();
        classes.add(Standstill.class);
        classes.add(Customer.class);
        solverConfig.setEntityClassList(classes);
        solverConfig.setScoreDirectorFactoryConfig(new ScoreDirectorFactoryConfig());
        solverConfig.getScoreDirectorFactoryConfig().setIncrementalScoreCalculatorClass(VehicleRoutingIncrementalScoreCalculator.class);
        solverConfig.getScoreDirectorFactoryConfig().setInitializingScoreTrend("ONLY_DOWN");

        return solverConfig;
    }

    @Override
    protected String getSolverConfigResource() {
        return SOLVER_CONFIG;
    }

    public enum DataSet {
        VRP_TW_400("cvrptw-400customers.xml"),
        VRP_ROAD_29("road-cvrp-29customers.xml"),
        VRP_TW_25("cvrptw-25customers.xml"),
        VRP_TW_100_B("cvrptw-100customers-B.xml"),
        VRP_USA_100_10("usa-n100-k10.xml"),
        VRP_USA_1000_20("usa-n1000-k20.xml"),
        VRP_USA_10000_100("usa-n10000-k100.xml"),
        BELGIUM_TW_50_10("belgium-tw-n50-k10.xml"),
        BELGIUM_TW_500_20("belgium-tw-n500-k20.xml"),
        BELGIUM_TW_2750_55("belgium-tw-n2750-k55.xml");

        DataSet(String file) {
            this.filename = file;
        }

        private String filename;

        public String getFilename() {
            return filename;
        }
    }
}
