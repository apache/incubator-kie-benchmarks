package org.jboss.qa.brms.performance.score.constraintstream;

import org.jboss.qa.brms.performance.AbstractPlannerBenchmark;
import org.jboss.qa.brms.performance.examples.Examples;
import org.jboss.qa.brms.performance.examples.flightcrewscheduling.FlightCrewSchedulingExample;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.flightcrewscheduling.domain.FlightCrewSolution;

public class FlightCrewSchedulingConstraintStreamsBenchmark extends AbstractPlannerBenchmark<FlightCrewSolution> {

    @Param({"EUROPE_175_FLIGHTS_7_DAYS", "EUROPE_700_FLIGHTS_28_DAYS"})
    private FlightCrewSchedulingExample.DataSet dataSet;

    @Override
    protected FlightCrewSolution createInitialSolution() {
        return Examples.FLIGHT_CREW_SCHEDULING.createInitialSolution(dataSet);
    }

    @Override
    protected Solver<FlightCrewSolution> createSolver() {
        SolverFactory<FlightCrewSolution> solverFactory = SolverFactory.create(
                Examples.FLIGHT_CREW_SCHEDULING.getSolverConfigFromXml());
        return solverFactory.buildSolver();
    }

    @Benchmark
    public FlightCrewSolution benchmark() {
        return runBenchmark();
    }

    public static void main(String[] args) {
        FlightCrewSchedulingConstraintStreamsBenchmark benchmark = new FlightCrewSchedulingConstraintStreamsBenchmark();
        Solver<FlightCrewSolution> solver = benchmark.createSolver();
        FlightCrewSolution initialSolution = Examples.FLIGHT_CREW_SCHEDULING.createInitialSolution(
                FlightCrewSchedulingExample.DataSet.EUROPE_175_FLIGHTS_7_DAYS);
        FlightCrewSolution solution = solver.solve(initialSolution);
    }
}
