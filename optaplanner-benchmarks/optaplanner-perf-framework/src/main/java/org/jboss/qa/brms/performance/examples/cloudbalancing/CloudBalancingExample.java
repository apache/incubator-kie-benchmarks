package org.jboss.qa.brms.performance.examples.cloudbalancing;

import org.jboss.qa.brms.performance.examples.AbstractExample;
import org.jboss.qa.brms.performance.examples.cloudbalancing.persistence.CloudBalancingDao;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;
import org.optaplanner.examples.cloudbalancing.optional.score.CloudBalancingIncrementalScoreCalculator;

import java.io.File;
import java.util.Collections;

public final class CloudBalancingExample extends AbstractExample<CloudBalance> {

    private static final String SOLVER_CONFIG =
            "/org/jboss/qa/brms/performance/examples/cloudbalancing/solver/cloudBalancingSolverConfig.xml";

    private final CloudBalancingDao dao = new CloudBalancingDao();

    @Override
    public CloudBalance loadSolvingProblem(File file) {
        return dao.readSolution(file);
    }

    public CloudBalance loadSolvingProblem(DataSet dataset) {
        File dataFile = new File(dao.getDataDir(), dataset.getFilename());
        return loadSolvingProblem(dataFile);
    }

    @Override
    public SolverConfig getBaseSolverConfig() {
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setEntityClassList(Collections.singletonList(CloudProcess.class));
        solverConfig.setSolutionClass(CloudBalance.class);
        solverConfig.setEnvironmentMode(EnvironmentMode.REPRODUCIBLE);
        solverConfig.setScoreDirectorFactoryConfig(new ScoreDirectorFactoryConfig());
        solverConfig.getScoreDirectorFactoryConfig().setIncrementalScoreCalculatorClass(
                CloudBalancingIncrementalScoreCalculator.class);
        solverConfig.getScoreDirectorFactoryConfig().setInitializingScoreTrend("ONLY_DOWN");
        return solverConfig;
    }

    @Override
    protected String getSolverConfigResource() {
        return SOLVER_CONFIG;
    }

    /**
     * Predefined datasets.
     */
    public enum DataSet {
        CB_400_1200("400computers-1200processes.xml"), CB_800_2400("800computers-2400processes.xml"),
        CB_1600_4800("1600computers-4800processes.xml"), CB_100_300("100computers-300processes.xml"),
        CB_10000_30000("10000computers-30000processes.xml");

        DataSet(String file) {
            this.filename = file;
        }

        private String filename;

        public String getFilename() {
            return filename;
        }
    }
}
