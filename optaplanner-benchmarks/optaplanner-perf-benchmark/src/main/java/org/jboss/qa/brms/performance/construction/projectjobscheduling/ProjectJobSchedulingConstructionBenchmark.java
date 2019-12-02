package org.jboss.qa.brms.performance.construction.projectjobscheduling;

import org.jboss.qa.brms.performance.construction.AbstractConstructionHeuristicBenchmark;
import org.jboss.qa.brms.performance.examples.Examples;
import org.jboss.qa.brms.performance.examples.projectjobscheduling.ProjectJobSchedulingExample;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.examples.projectjobscheduling.domain.Schedule;

public class ProjectJobSchedulingConstructionBenchmark extends AbstractConstructionHeuristicBenchmark<Schedule> {

    public ProjectJobSchedulingConstructionBenchmark() {
        super(Examples.PROJECT_JOB_SCHEDULING);
    }

    @Param({"FIRST_FIT"})
    private ConstructionHeuristicType constructionHeuristicType;

    @Param({"A_4", "A_10", "B_9"})
    private ProjectJobSchedulingExample.DataSet dataset;

    @Override
    protected Schedule createInitialSolution() {
        return Examples.PROJECT_JOB_SCHEDULING.loadSolvingProblem(dataset);
    }

    @Override
    public ConstructionHeuristicType getConstructionHeuristicType() {
        return constructionHeuristicType;
    }

    @Benchmark
    public Schedule benchmark() {
        return runBenchmark();
    }
}
