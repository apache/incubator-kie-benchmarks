package org.jboss.qa.brms.performance.examples.nurserostering;

import java.io.File;

import org.jboss.qa.brms.performance.examples.AbstractExample;
import org.jboss.qa.brms.performance.examples.nurserostering.persistence.NurseRosteringDao;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;
import org.optaplanner.examples.nurserostering.domain.ShiftAssignment;
import org.optaplanner.examples.nurserostering.score.NurseRosteringConstraintProvider;

public final class NurseRosteringExample extends AbstractExample<NurseRoster> {

    private static final String SOLVER_CONFIG =
            "org/jboss/qa/brms/performance/examples/nurserostering/solver/nurseRosteringSolverConfig.xml";

    private final NurseRosteringDao dao = new NurseRosteringDao();

    public NurseRoster loadSolvingProblem(DataSet dataset) {
        return loadSolvingProblem(new File(dao.getDataDir(), dataset.getFilename()));
    }

    @Override
    public NurseRoster loadSolvingProblem(File file) {
        return dao.readSolution(file);
    }

    @Override
    public SolverConfig getBaseSolverConfig() {
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.withEntityClasses(ShiftAssignment.class);
        solverConfig.withSolutionClass(NurseRoster.class);
        solverConfig.setScoreDirectorFactoryConfig(new ScoreDirectorFactoryConfig());
        solverConfig.getScoreDirectorFactoryConfig().setConstraintProviderClass(NurseRosteringConstraintProvider.class);
        solverConfig.getScoreDirectorFactoryConfig().setInitializingScoreTrend("ONLY_DOWN");
        return solverConfig;
    }

    @Override
    protected String getSolverConfigResource() {
        return SOLVER_CONFIG;
    }

    public enum DataSet {
        LONG("long01.json"),
        MEDIUM("medium01.json"),
        SPRINT("sprint01.json");

        private String filename;

        DataSet(String file) {
            this.filename = file;
        }

        public String getFilename() {
            return filename;
        }
    }
}
