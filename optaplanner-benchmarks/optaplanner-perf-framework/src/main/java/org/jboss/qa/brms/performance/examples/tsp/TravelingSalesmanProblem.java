package org.jboss.qa.brms.performance.examples.tsp;

import org.jboss.qa.brms.performance.examples.AbstractExample;
import org.jboss.qa.brms.performance.examples.common.persistence.SolutionDao;
import org.jboss.qa.brms.performance.examples.tsp.domain.TravelingSalesmanTour;
import org.jboss.qa.brms.performance.examples.tsp.domain.Visit;
import org.jboss.qa.brms.performance.examples.tsp.persistence.TspDao;
import org.jboss.qa.brms.performance.examples.tsp.solver.score.TspIncrementalScoreCalculator;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.score.definition.ScoreDefinitionType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;

import java.io.File;
import java.util.Collections;

public class TravelingSalesmanProblem extends AbstractExample {

    public static enum DataSet {
        USA_CA_2716("usa_ca_2716.xml"), USA_NY_2281("usa_ny_2281.xml"), USA_TX_2743("usa_tx_2743.xml"),
        CHINA_71009("ch71009.xml"), GREECE_9882("gr9882.xml"), LU_980("lu980.xml");

        private DataSet(String file) {
            this.filename = file;
        }

        private String filename;

        public String getFilename() {
            return filename;
        }
    }

    private SolutionDao dao = new TspDao();

    public TravelingSalesmanTour loadSolvingProblem(DataSet dataset) {
        File dataFile = new File(dao.getDataDir(), dataset.getFilename());
        return (TravelingSalesmanTour) loadSolvingProblem(dataFile);
    }

    @Override
    public SolverConfig getDefaultConfig() {
        return (SolverFactory.createFromXmlInputStream(this.getClass()
                .getResourceAsStream("/org/jboss/qa/brms/performance/examples/tsp/solver/tspSolverConfig.xml")))
                .getSolverConfig();
    }

    @Override
    public Solution loadSolvingProblem(File f) {
        return dao.readSolution(f);
    }

    @Override
    public SolverConfig getBaseConfig() {
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setSolutionClass(TravelingSalesmanTour.class);
        solverConfig.setEntityClassList(Collections.<Class<?>>singletonList(Visit.class));
        solverConfig.setEnvironmentMode(EnvironmentMode.REPRODUCIBLE);
        solverConfig.setScoreDirectorFactoryConfig(new ScoreDirectorFactoryConfig());
        solverConfig.getScoreDirectorFactoryConfig().setScoreDefinitionType(ScoreDefinitionType.SIMPLE_LONG);
        solverConfig.getScoreDirectorFactoryConfig().setIncrementalScoreCalculatorClass(TspIncrementalScoreCalculator.class);
        solverConfig.getScoreDirectorFactoryConfig().setInitializingScoreTrend("ONLY_DOWN");
        return solverConfig;
    }

}
