package org.jboss.qa.brms.performance.realworld.nurserostering;

import org.jboss.qa.brms.performance.AbstractPlannerBenchmark;
import org.jboss.qa.brms.performance.examples.nurserostering.NurseRostering;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Warmup;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;

@Warmup(iterations = 15)
public class NurseRosteringBenchmark extends AbstractPlannerBenchmark<NurseRoster> {

    private static final NurseRostering NURSE_ROSTERING = new NurseRostering();

    @Param({"SPRINT", "MEDIUM", "LONG"})
    private NurseRostering.DataSet dataset;

    @Override
    protected NurseRoster createInitialSolution() {
        return NURSE_ROSTERING.loadSolvingProblem(dataset);
    }

    @Override
    public Solver<NurseRoster> createSolver() {
        SolverConfig solverConfig = NURSE_ROSTERING.getSolverConfigFromXml();
        SolverFactory<NurseRoster> solverFactory = SolverFactory.create(solverConfig);
        return solverFactory.buildSolver();
    }

    @Benchmark
    public NurseRoster benchmark() {
        return runBenchmark();
    }

    // for debugging purposes
    public static void main(String[] args) {
        NurseRosteringBenchmark nurseRosteringBenchmark = new NurseRosteringBenchmark();
        Solver<NurseRoster> solver = nurseRosteringBenchmark.createSolver();
        solver.solve(NURSE_ROSTERING.loadSolvingProblem(NurseRostering.DataSet.SPRINT));
    }
}
