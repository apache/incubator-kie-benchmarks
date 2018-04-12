package org.jboss.qa.brms.performance.examples.projectjobscheduling;

import org.jboss.qa.brms.performance.examples.AbstractExample;
import org.jboss.qa.brms.performance.examples.projectjobscheduling.domain.Allocation;
import org.jboss.qa.brms.performance.examples.projectjobscheduling.domain.Schedule;
import org.jboss.qa.brms.performance.examples.projectjobscheduling.persistence.ProjectJobSchedulingDao;
import org.jboss.qa.brms.performance.examples.projectjobscheduling.solver.score.ProjectJobSchedulingIncrementalScoreCalculator;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;

import java.io.File;
import java.util.ArrayList;

public class ProjectJobScheduling extends AbstractExample<Schedule> {

    public static enum DataSet {
        A_4("A-4.xml"), A_10("A-10.xml"), B_9("B-9.xml");

        private DataSet(String file) {
            this.filename = file;
        }

        private String filename;

        public String getFilename() {
            return filename;
        }
    }

    private ProjectJobSchedulingDao dao = new ProjectJobSchedulingDao();

    public Schedule loadSolvingProblem(DataSet dataset) {
        return loadSolvingProblem(new File(dao.getDataDir(), dataset.getFilename()));
    }

    @Override
    public Schedule loadSolvingProblem(File f) {
        return dao.readSolution(f);
    }

    @Override
    public SolverFactory<Schedule> getDefaultSolverFactory() {
        return null;
    }

    @Override
    public SolverFactory<Schedule> getBaseSolverFactory() {
        SolverFactory<Schedule> solverFactory = SolverFactory.createEmpty();
        SolverConfig solverConfig = solverFactory.getSolverConfig();
        solverConfig.setSolutionClass(Schedule.class);
        solverConfig.setEnvironmentMode(EnvironmentMode.REPRODUCIBLE);
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        classes.add(Allocation.class);
        solverConfig.setEntityClassList(classes);
        solverConfig.setScoreDirectorFactoryConfig(new ScoreDirectorFactoryConfig());
        solverConfig.getScoreDirectorFactoryConfig()
                .setIncrementalScoreCalculatorClass(ProjectJobSchedulingIncrementalScoreCalculator.class);
        solverConfig.getScoreDirectorFactoryConfig().setInitializingScoreTrend("ONLY_DOWN");
        return solverFactory;
    }

}
