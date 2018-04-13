package org.jboss.qa.brms.performance.examples.tsp;

import org.jboss.qa.brms.performance.examples.AbstractExample;
import org.jboss.qa.brms.performance.examples.tsp.domain.TravelingSalesmanTour;
import org.jboss.qa.brms.performance.examples.tsp.domain.Visit;
import org.jboss.qa.brms.performance.examples.tsp.persistence.TspDao;
import org.jboss.qa.brms.performance.examples.tsp.solver.score.TspIncrementalScoreCalculator;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;

import java.io.File;
import java.util.Collections;

public class TravelingSalesmanProblem extends AbstractExample<TravelingSalesmanTour> {

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

    private TspDao dao = new TspDao();

    public TravelingSalesmanTour loadSolvingProblem(DataSet dataset) {
        File dataFile = new File(dao.getDataDir(), dataset.getFilename());
        return loadSolvingProblem(dataFile);
    }

    @Override
    public SolverFactory<TravelingSalesmanTour> getDefaultSolverFactory() {
        return SolverFactory.createFromXmlInputStream(this.getClass()
                .getResourceAsStream("/org/jboss/qa/brms/performance/examples/tsp/solver/tspSolverConfig.xml"));
    }

    @Override
    public TravelingSalesmanTour loadSolvingProblem(File f) {
        return dao.readSolution(f);
    }

    @Override
    public SolverFactory<TravelingSalesmanTour> getBaseSolverFactory() {
        SolverFactory<TravelingSalesmanTour> solverFactory = SolverFactory.createEmpty();
        SolverConfig solverConfig = solverFactory.getSolverConfig();
        solverConfig.setSolutionClass(TravelingSalesmanTour.class);
        solverConfig.setEntityClassList(Collections.<Class<?>>singletonList(Visit.class));
        solverConfig.setEnvironmentMode(EnvironmentMode.REPRODUCIBLE);
        solverConfig.setScoreDirectorFactoryConfig(new ScoreDirectorFactoryConfig());
        solverConfig.getScoreDirectorFactoryConfig().setIncrementalScoreCalculatorClass(TspIncrementalScoreCalculator.class);
        solverConfig.getScoreDirectorFactoryConfig().setInitializingScoreTrend("ONLY_DOWN");
        return solverFactory;
    }

}
