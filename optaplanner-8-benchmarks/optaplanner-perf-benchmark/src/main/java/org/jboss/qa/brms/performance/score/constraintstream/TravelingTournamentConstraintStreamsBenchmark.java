package org.jboss.qa.brms.performance.score.constraintstream;

import java.util.concurrent.TimeUnit;

import org.jboss.qa.brms.performance.AbstractPlannerBenchmark;
import org.jboss.qa.brms.performance.examples.Examples;
import org.jboss.qa.brms.performance.examples.travelingtournament.TravelingTournamentExample;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Warmup;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.travelingtournament.domain.TravelingTournament;

@Fork(value = 4)
@Warmup(iterations = 4, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
public class TravelingTournamentConstraintStreamsBenchmark extends AbstractPlannerBenchmark<TravelingTournament> {

    @Param({"SUPER_06", "SUPER_10", "SUPER_14"})
    private TravelingTournamentExample.DataSet dataset;

    @Override
    protected TravelingTournament createInitialSolution() {
        return Examples.TRAVELING_TOURNAMENT.loadSolvingProblem(TravelingTournamentExample.DataSet.SUPER_06);
    }

    @Override
    protected Solver<TravelingTournament> createSolver() {
        SolverFactory<TravelingTournament> solverFactory = SolverFactory.create(
                Examples.TRAVELING_TOURNAMENT.getSolverConfigFromXml());
        return solverFactory.buildSolver();
    }

    @Benchmark
    public TravelingTournament benchmark() {
        return runBenchmark();
    }

    public static void main(String[] args) {
        TravelingTournamentConstraintStreamsBenchmark benchmark = new TravelingTournamentConstraintStreamsBenchmark();
        Solver<TravelingTournament> solver = benchmark.createSolver();

        solver.solve(Examples.TRAVELING_TOURNAMENT.loadSolvingProblem(TravelingTournamentExample.DataSet.SUPER_06));
    }
}
