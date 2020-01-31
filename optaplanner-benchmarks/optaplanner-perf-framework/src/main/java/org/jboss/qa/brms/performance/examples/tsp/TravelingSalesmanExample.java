package org.jboss.qa.brms.performance.examples.tsp;

import java.io.File;
import java.util.Collections;

import org.jboss.qa.brms.performance.examples.AbstractExample;
import org.jboss.qa.brms.performance.examples.tsp.persistence.TspDao;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.examples.tsp.domain.TspSolution;
import org.optaplanner.examples.tsp.domain.Visit;
import org.optaplanner.examples.tsp.solver.score.TspIncrementalScoreCalculator;

public final class TravelingSalesmanExample extends AbstractExample<TspSolution> {

    private static final String SOLVER_CONFIG = "/org/jboss/qa/brms/performance/examples/tsp/solver/tspSolverConfig.xml";

    private final TspDao dao = new TspDao();

    public TspSolution loadSolvingProblem(DataSet dataset) {
        File dataFile = new File(dao.getDataDir(), dataset.getFilename());
        return loadSolvingProblem(dataFile);
    }

    @Override
    public TspSolution loadSolvingProblem(File file) {
        return dao.readSolution(file);
    }

    @Override
    public SolverConfig getBaseSolverConfig() {
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setSolutionClass(TspSolution.class);
        solverConfig.setEntityClassList(Collections.singletonList(Visit.class));
        solverConfig.setEnvironmentMode(EnvironmentMode.REPRODUCIBLE);
        solverConfig.setScoreDirectorFactoryConfig(new ScoreDirectorFactoryConfig());
        solverConfig.getScoreDirectorFactoryConfig().setIncrementalScoreCalculatorClass(
                TspIncrementalScoreCalculator.class);
        solverConfig.getScoreDirectorFactoryConfig().setInitializingScoreTrend("ONLY_DOWN");
        return solverConfig;
    }

    @Override
    protected String getSolverConfigResource() {
        return SOLVER_CONFIG;
    }

    public enum DataSet {
        USA_CA_2716("usa_ca_2716.xml"),
        USA_NY_2281("usa_ny_2281.xml"),
        USA_TX_2743("usa_tx_2743.xml"),
        CHINA_71009("ch71009.xml"),
        GREECE_9882("gr9882.xml"),
        LU_980("lu980.xml");

        DataSet(String file) {
            this.filename = file;
        }

        private String filename;

        public String getFilename() {
            return filename;
        }
    }
}
