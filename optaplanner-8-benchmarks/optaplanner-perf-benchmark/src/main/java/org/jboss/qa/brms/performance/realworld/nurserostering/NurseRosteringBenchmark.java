package org.jboss.qa.brms.performance.realworld.nurserostering;

import org.jboss.qa.brms.performance.AbstractPlannerBenchmark;
import org.jboss.qa.brms.performance.examples.Examples;
import org.jboss.qa.brms.performance.examples.nurserostering.NurseRosteringExample;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Warmup;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;

@Warmup(iterations = 15)
public class NurseRosteringBenchmark extends AbstractPlannerBenchmark<NurseRoster> {

    @Param({"SPRINT", "MEDIUM", "LONG"})
    private NurseRosteringExample.DataSet dataset;

    @Override
    protected NurseRoster createInitialSolution() {
        return Examples.NURSE_ROSTERING.loadSolvingProblem(dataset);
    }

    @Override
    public Solver<NurseRoster> createSolver() {
        SolverConfig solverConfig = Examples.NURSE_ROSTERING.getSolverConfigFromXml();
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
        solver.solve(Examples.NURSE_ROSTERING.loadSolvingProblem(NurseRosteringExample.DataSet.SPRINT));
    }
}
