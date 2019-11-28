package org.jboss.qa.brms.performance.construction.projectjobscheduling;

import org.jboss.qa.brms.performance.construction.AbstractConstructionHeuristicBenchmark;
import org.jboss.qa.brms.performance.examples.projectjobscheduling.ProjectJobScheduling;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.examples.projectjobscheduling.domain.Schedule;

public class ProjectJobSchedulingConstructionBenchmark extends AbstractConstructionHeuristicBenchmark<Schedule, ProjectJobScheduling> {

    public ProjectJobSchedulingConstructionBenchmark() {
        super(ProjectJobScheduling.class);
    }

    @Param({"FIRST_FIT"})
    private ConstructionHeuristicType constructionHeuristicType;

    @Param({"A_4", "A_10", "B_9"})
    private ProjectJobScheduling.DataSet dataset;

    @Override
    public void initSolution() {
        super.setSolution(example.loadSolvingProblem(dataset));
    }

    @Override
    public ConstructionHeuristicType getConstructionHeuristicType() {
        return constructionHeuristicType;
    }

    @Benchmark
    @Override
    public Schedule benchmark() {
        return super.benchmark();
    }
}
