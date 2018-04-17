package org.jboss.qa.brms.performance.examples.cloudbalancing;

import org.jboss.qa.brms.performance.examples.AbstractExample;
import org.jboss.qa.brms.performance.examples.cloudbalancing.domain.CloudBalance;
import org.jboss.qa.brms.performance.examples.cloudbalancing.domain.CloudProcess;
import org.jboss.qa.brms.performance.examples.cloudbalancing.persistence.CloudBalancingDao;
import org.jboss.qa.brms.performance.examples.cloudbalancing.solver.score.CloudBalancingIncrementalScoreCalculator;
import org.jboss.qa.brms.performance.examples.common.persistence.SolutionDao;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.config.score.definition.ScoreDefinitionType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;

import java.io.File;
import java.util.Collections;

public class CloudBalancing extends AbstractExample {

    /**
     * Predefined datasets.
     */
    public static enum DataSet {
        CB_400_1200("400computers-1200processes.xml"), CB_800_2400("800computers-2400processes.xml"),
        CB_1600_4800("1600computers-4800processes.xml"), CB_100_300("100computers-300processes.xml"),
        CB_10000_30000("10000computers-30000processes.xml");

        private DataSet(String file) {
            this.filename = file;
        }

        private String filename;

        public String getFilename() {
            return filename;
        }
    }

    private SolutionDao dao = new CloudBalancingDao();

    @Override
    public Solution loadSolvingProblem(File f) {
        return dao.readSolution(f);
    }

    public Solution loadSolvingProblem(DataSet dataset) {
        File dataFile = new File(dao.getDataDir(), dataset.getFilename());
        return loadSolvingProblem(dataFile);
    }

    @Override
    public SolverConfig getDefaultConfig() {
        return getDefaultConfig("/org/jboss/qa/brms/performance/examples/cloudbalancing/"
                + "solver/cloudBalancingSolverConfig.xml");
    }

    @Override
    public SolverConfig getBaseConfig() {
        SolverConfig config = new SolverConfig();
        config.setEntityClassList(Collections.<Class<?>>singletonList(CloudProcess.class));
        config.setSolutionClass(CloudBalance.class);
        config.setEnvironmentMode(EnvironmentMode.REPRODUCIBLE);
        config.setScoreDirectorFactoryConfig(new ScoreDirectorFactoryConfig());
        config.getScoreDirectorFactoryConfig().setScoreDefinitionType(ScoreDefinitionType.HARD_SOFT);
        config.getScoreDirectorFactoryConfig().setIncrementalScoreCalculatorClass(CloudBalancingIncrementalScoreCalculator.class);
        config.getScoreDirectorFactoryConfig().setInitializingScoreTrend("ONLY_DOWN");
        return config;
    }
}
