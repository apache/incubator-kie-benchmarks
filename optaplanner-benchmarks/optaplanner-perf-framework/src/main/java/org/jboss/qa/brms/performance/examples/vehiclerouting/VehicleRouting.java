package org.jboss.qa.brms.performance.examples.vehiclerouting;

import org.jboss.qa.brms.performance.examples.AbstractExample;
import org.jboss.qa.brms.performance.examples.common.persistence.SolutionDao;
import org.jboss.qa.brms.performance.examples.vehiclerouting.domain.Customer;
import org.jboss.qa.brms.performance.examples.vehiclerouting.domain.Standstill;
import org.jboss.qa.brms.performance.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.jboss.qa.brms.performance.examples.vehiclerouting.persistence.VehicleRoutingDao;
import org.jboss.qa.brms.performance.examples.vehiclerouting.solver.score.VehicleRoutingIncrementalScoreCalculator;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.config.score.definition.ScoreDefinitionType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;

import java.io.File;
import java.util.ArrayList;

public class VehicleRouting extends AbstractExample {

    public static enum DataSet {
        VRP_TW_400("cvrptw-400customers.xml"), VRP_ROAD_29("road-cvrp-29customers.xml"),
        VRP_TW_25("cvrptw-25customers.xml"), VRP_TW_100_B("cvrptw-100customers-B.xml"),
        VRP_USA_100_10("usa-n100-k10.xml"),
        VRP_USA_1000_20("usa-n1000-k20.xml"), VRP_USA_10000_100("usa-n10000-k100.xml"),
        BELGIUM_TW_50_10("belgium-tw-n50-k10.xml"), BELGIUM_TW_500_20("belgium-tw-n500-k20.xml"),
        BELGIUM_TW_2750_55("belgium-tw-n2750-k55.xml");

        private DataSet(String file) {
            this.filename = file;
        }

        private String filename;

        public String getFilename() {
            return filename;
        }
    }

    private SolutionDao dao = new VehicleRoutingDao();

    public Solution loadSolvingProblem(DataSet dataset) {
        return loadSolvingProblem(new File(dao.getDataDir(), dataset.getFilename()));
    }

    @Override
    public Solution loadSolvingProblem(File f) {
        return dao.readSolution(f);
    }

    @Override
    public SolverConfig getDefaultConfig() {
        return getDefaultConfig("/org/jboss/qa/brms/performance/examples/vrp/solver/vehicleRoutingSolverConfig.xml");
    }

    @Override
    public SolverConfig getBaseConfig() {
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setSolutionClass(VehicleRoutingSolution.class);
        solverConfig.setEnvironmentMode(EnvironmentMode.REPRODUCIBLE);
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        classes.add(Standstill.class);
        classes.add(Customer.class);
        solverConfig.setEntityClassList(classes);
        solverConfig.setScoreDirectorFactoryConfig(new ScoreDirectorFactoryConfig());
        solverConfig.getScoreDirectorFactoryConfig().setScoreDefinitionType(ScoreDefinitionType.HARD_SOFT_LONG);
        solverConfig.getScoreDirectorFactoryConfig().setIncrementalScoreCalculatorClass(VehicleRoutingIncrementalScoreCalculator.class);
        solverConfig.getScoreDirectorFactoryConfig().setInitializingScoreTrend("ONLY_DOWN");
        return solverConfig;
    }

}
