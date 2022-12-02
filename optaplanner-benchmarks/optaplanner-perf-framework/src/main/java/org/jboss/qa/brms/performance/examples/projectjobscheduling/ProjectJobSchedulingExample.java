package org.jboss.qa.brms.performance.examples.projectjobscheduling;

import java.io.File;

import org.jboss.qa.brms.performance.examples.AbstractExample;
import org.jboss.qa.brms.performance.examples.projectjobscheduling.persistence.ProjectJobSchedulingDao;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.examples.projectjobscheduling.domain.Allocation;
import org.optaplanner.examples.projectjobscheduling.domain.Schedule;
import org.optaplanner.examples.projectjobscheduling.optional.score.ProjectJobSchedulingIncrementalScoreCalculator;

public final class ProjectJobSchedulingExample extends AbstractExample<Schedule> {

    private final ProjectJobSchedulingDao dao = new ProjectJobSchedulingDao();

    public Schedule loadSolvingProblem(DataSet dataset) {
        return loadSolvingProblem(new File(dao.getDataDir(), dataset.getFilename()));
    }

    @Override
    public Schedule loadSolvingProblem(File file) {
        return dao.readSolution(file);
    }

    @Override
    public SolverConfig getBaseSolverConfig() {
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setEnvironmentMode(EnvironmentMode.REPRODUCIBLE);
        solverConfig.withEntityClasses(Allocation.class);
        solverConfig.withSolutionClass(Schedule.class);
        solverConfig.setScoreDirectorFactoryConfig(new ScoreDirectorFactoryConfig());
        solverConfig.getScoreDirectorFactoryConfig()
                .setIncrementalScoreCalculatorClass(ProjectJobSchedulingIncrementalScoreCalculator.class);
        solverConfig.getScoreDirectorFactoryConfig().setInitializingScoreTrend("ONLY_DOWN");

        return solverConfig;
    }

    @Override
    protected String getSolverConfigResource() {
        throw new UnsupportedOperationException("Solver configuration XML not available.");
    }

    public enum DataSet {
        A_4("A-4.json"),
        A_10("A-10.json"),
        B_9("B-9.json");

        DataSet(String file) {
            this.filename = file;
        }

        private String filename;

        public String getFilename() {
            return filename;
        }
    }
}
