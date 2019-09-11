package org.jboss.qa.brms.performance.realworld.nurserostering;

import org.jboss.qa.brms.performance.AbstractPlannerBenchmark;
import org.jboss.qa.brms.performance.examples.nurserostering.NurseRostering;
import org.jboss.qa.brms.performance.examples.nurserostering.domain.NurseRoster;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Warmup;
import org.optaplanner.core.api.solver.SolverFactory;

@Warmup(iterations = 15)
public class NurseRosteringBenchmark extends AbstractPlannerBenchmark<NurseRoster> {

    private static final NurseRostering NURSE_ROSTERING = new NurseRostering();

    @Param({"SPRINT", "MEDIUM", "LONG"})
    private NurseRostering.DataSet dataset;

    @Override
    public void initSolution() {
        super.setSolution(NURSE_ROSTERING.loadSolvingProblem(dataset));
    }

    @Override
    public void initSolver() {
        SolverFactory<NurseRoster> nurseRosterSolverFactory = NURSE_ROSTERING.getDefaultSolverFactory();
        super.setSolver(nurseRosterSolverFactory.buildSolver());
    }

    @Benchmark
    public NurseRoster benchmark() {
        return super.benchmark();
    }

    // for debugging purposes
    public static void main(String[] args) {
        NurseRosteringBenchmark nurseRosteringBenchmark = new NurseRosteringBenchmark();
        nurseRosteringBenchmark.initSolver();
        nurseRosteringBenchmark.setSolution(NURSE_ROSTERING.loadSolvingProblem(NurseRostering.DataSet.SPRINT));
    }
}
