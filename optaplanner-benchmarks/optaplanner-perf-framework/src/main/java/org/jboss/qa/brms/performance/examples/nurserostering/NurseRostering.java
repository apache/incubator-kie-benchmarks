package org.jboss.qa.brms.performance.examples.nurserostering;

import java.io.File;
import java.util.Collections;

import org.jboss.qa.brms.performance.examples.AbstractExample;
import org.jboss.qa.brms.performance.examples.nurserostering.domain.NurseRoster;
import org.jboss.qa.brms.performance.examples.nurserostering.domain.ShiftAssignment;
import org.jboss.qa.brms.performance.examples.nurserostering.persistence.NurseRosteringDao;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;

public class NurseRostering extends AbstractExample<NurseRoster> {

    public enum DataSet {
        LONG("long01.xml"),
        LONG_HINT("long_hint01.xml"),
        MEDIUM("medium01.xml"),
        MEDIUM_HINT("medium_hint01.xml"),
        MEDIUM_LATE_INITIALIZED("medium_late01_initialized.xml"),
        MEDIUM_LATE("medium_late02.xml"),
        SPRINT("sprint01.xml"),
        SPRINT_HINT("sprint_hint01.xml");

        private String filename;

        DataSet(String file) {
            this.filename = file;
        }

        public String getFilename() {
            return filename;
        }
    }

    private static final String PATH_TO_SOLVER_CONFIG = "/org/jboss/qa/brms/performance/examples/nurserostering/solver/nurseRosteringSolverConfig.xml";
    private static final String PATH_TO_DRL_FILE = "/org/jboss/qa/brms/performance/examples/nurserostering/solver/nurseRosteringScoreRules.drl";
    private NurseRosteringDao dao = new NurseRosteringDao();

    public NurseRoster loadSolvingProblem(DataSet dataset) {
        return loadSolvingProblem(new File(dao.getDataDir(), dataset.getFilename()));
    }

    @Override
    public NurseRoster loadSolvingProblem(File f) {
        return dao.readSolution(f);
    }

    @Override
    public SolverFactory<NurseRoster> getDefaultSolverFactory() {
        return SolverFactory.createFromXmlInputStream(getClass().getResourceAsStream(PATH_TO_SOLVER_CONFIG));
    }

    @Override
    public SolverFactory<NurseRoster> getBaseSolverFactory() {
        SolverFactory<NurseRoster> solverFactory = SolverFactory.createEmpty();
        SolverConfig config = solverFactory.getSolverConfig();
        config.setEntityClassList(Collections.singletonList(ShiftAssignment.class));
        config.setSolutionClass(NurseRoster.class);
        config.setScoreDirectorFactoryConfig(new ScoreDirectorFactoryConfig());
        config.getScoreDirectorFactoryConfig().setScoreDrlList(Collections.singletonList(PATH_TO_DRL_FILE));
        config.getScoreDirectorFactoryConfig().setInitializingScoreTrend("ONLY_DOWN");
        return solverFactory;
    }
}
