package org.jboss.qa.brms.performance.examples.projectjobscheduling;

import org.jboss.qa.brms.performance.examples.AbstractExample;
import org.jboss.qa.brms.performance.examples.common.persistence.SolutionDao;
import org.jboss.qa.brms.performance.examples.projectjobscheduling.domain.Allocation;
import org.jboss.qa.brms.performance.examples.projectjobscheduling.domain.Schedule;
import org.jboss.qa.brms.performance.examples.projectjobscheduling.persistence.ProjectJobSchedulingDao;
import org.jboss.qa.brms.performance.examples.projectjobscheduling.solver.score.ProjectJobSchedulingIncrementalScoreCalculator;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.config.score.definition.ScoreDefinitionType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;

import java.io.File;
import java.util.ArrayList;

public class ProjectJobScheduling extends AbstractExample {

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

    private SolutionDao dao = new ProjectJobSchedulingDao();

    public Solution loadSolvingProblem(DataSet dataset) {
        return loadSolvingProblem(new File(dao.getDataDir(), dataset.getFilename()));
    }

    @Override
    public Solution loadSolvingProblem(File f) {
        return dao.readSolution(f);
    }

    @Override
    public SolverConfig getDefaultConfig() {
        return null;
    }

    @Override
    public SolverConfig getBaseConfig() {
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setSolutionClass(Schedule.class);
        solverConfig.setEnvironmentMode(EnvironmentMode.REPRODUCIBLE);
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        classes.add(Allocation.class);
        solverConfig.setEntityClassList(classes);
        solverConfig.setScoreDirectorFactoryConfig(new ScoreDirectorFactoryConfig());
        solverConfig.getScoreDirectorFactoryConfig().setScoreDefinitionType(ScoreDefinitionType.BENDABLE);
        solverConfig.getScoreDirectorFactoryConfig().setBendableHardLevelsSize(1);
        solverConfig.getScoreDirectorFactoryConfig().setBendableSoftLevelsSize(2);
        solverConfig.getScoreDirectorFactoryConfig()
                .setIncrementalScoreCalculatorClass(ProjectJobSchedulingIncrementalScoreCalculator.class);
        solverConfig.getScoreDirectorFactoryConfig().setInitializingScoreTrend("ONLY_DOWN");
        return solverConfig;
    }

}
