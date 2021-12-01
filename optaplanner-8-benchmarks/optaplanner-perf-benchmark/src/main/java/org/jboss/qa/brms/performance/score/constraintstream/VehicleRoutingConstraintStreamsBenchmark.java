package org.jboss.qa.brms.performance.score.constraintstream;

import org.jboss.qa.brms.performance.AbstractPlannerBenchmark;
import org.jboss.qa.brms.performance.examples.Examples;
import org.jboss.qa.brms.performance.examples.vehiclerouting.VehicleRoutingExample;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;

public class VehicleRoutingConstraintStreamsBenchmark extends AbstractPlannerBenchmark<VehicleRoutingSolution> {

    @Param({"SMALLEST", "VRP_USA_1000_20", "VRP_USA_10000_100"})
    private VehicleRoutingExample.DataSet dataSet;

    @Override
    protected VehicleRoutingSolution createInitialSolution() {
        return Examples.VEHICLE_ROUTING.createInitialSolution(dataSet);
    }

    @Override
    protected Solver<VehicleRoutingSolution> createSolver() {
        SolverFactory<VehicleRoutingSolution> solverFactory = SolverFactory.create(
                Examples.VEHICLE_ROUTING.getSolverConfigFromXml());
        return solverFactory.buildSolver();
    }

    @Benchmark
    public VehicleRoutingSolution benchmark() {
        return runBenchmark();
    }

    public static void main(String[] args) {
        VehicleRoutingConstraintStreamsBenchmark benchmark = new VehicleRoutingConstraintStreamsBenchmark();
        Solver<VehicleRoutingSolution> solver = benchmark.createSolver();
        VehicleRoutingSolution initialSolution = Examples.VEHICLE_ROUTING.createInitialSolution(
                VehicleRoutingExample.DataSet.SMALLEST);
        VehicleRoutingSolution solution = solver.solve(initialSolution);
    }
}
