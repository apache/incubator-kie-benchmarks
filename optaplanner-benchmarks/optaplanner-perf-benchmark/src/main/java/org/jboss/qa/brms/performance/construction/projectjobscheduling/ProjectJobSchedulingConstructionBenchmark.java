package org.jboss.qa.brms.performance.construction.projectjobscheduling;

import org.jboss.qa.brms.performance.construction.AbstractConstructionHeuristicPlannerBenchmark;
import org.jboss.qa.brms.performance.examples.projectjobscheduling.ProjectJobScheduling;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.solver.SolverConfig;

import java.util.Collections;

public class ProjectJobSchedulingConstructionBenchmark extends AbstractConstructionHeuristicPlannerBenchmark {

    @Param({"FIRST_FIT"})
    private ConstructionHeuristicType constructionHeuristicType;

    @Param({"A_4", "A_10", "B_9"})
    private ProjectJobScheduling.DataSet dataset;

    @Override
    public void initSolver() {
        SolverConfig config = new ProjectJobScheduling().getBaseConfig();
        ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig = new ConstructionHeuristicPhaseConfig();
        constructionHeuristicPhaseConfig.setConstructionHeuristicType(getConstructionHeuristicType());
        config.setPhaseConfigList(Collections.singletonList(((PhaseConfig) constructionHeuristicPhaseConfig)));
        super.setSolver(config.buildSolver());
    }

    @Override
    public void initSolution() {
        super.setSolution(new ProjectJobScheduling().loadSolvingProblem(dataset));
    }

    @Override
    public ConstructionHeuristicType getConstructionHeuristicType() {
        return constructionHeuristicType;
    }

    @Benchmark
    @Override
    public void benchmark() {
        super.benchmark();
    }

}
