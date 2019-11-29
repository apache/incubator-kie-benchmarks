package org.jboss.qa.brms.performance.construction.vrp;

import java.util.Collections;

import org.jboss.qa.brms.performance.construction.AbstractConstructionHeuristicBenchmark;
import org.jboss.qa.brms.performance.examples.Examples;
import org.jboss.qa.brms.performance.examples.vehiclerouting.VehicleRoutingExample;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;

public class VRPTimeWindowedConstructionBenchmark
        extends AbstractConstructionHeuristicBenchmark<VehicleRoutingSolution> {

    public VRPTimeWindowedConstructionBenchmark() {
        super(Examples.VEHICLE_ROUTING);
    }

    @Param({"FIRST_FIT", "FIRST_FIT_DECREASING"})
    private ConstructionHeuristicType constructionHeuristicType;

    @Param({"BELGIUM_TW_50_10", "BELGIUM_TW_500_20", "BELGIUM_TW_2750_55"})
    private VehicleRoutingExample.DataSet dataset;

    @Override
    protected Solver<VehicleRoutingSolution> createSolver() {
        SolverConfig solverConfig = Examples.VEHICLE_ROUTING.getBaseSolverConfig();
        ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig =
                new ConstructionHeuristicPhaseConfig().withConstructionHeuristicType(getConstructionHeuristicType());
        solverConfig.setPhaseConfigList(Collections.singletonList(constructionHeuristicPhaseConfig));
        solverConfig.getEntityClassList().add(TimeWindowedCustomer.class); // diff between normal VRP and TimeWindowed
        SolverFactory<VehicleRoutingSolution> solverFactory = SolverFactory.create(solverConfig);
        return solverFactory.buildSolver();
    }

    @Override
    protected VehicleRoutingSolution createInitialSolution() {
        return Examples.VEHICLE_ROUTING.loadSolvingProblem(dataset);
    }

    @Override
    public ConstructionHeuristicType getConstructionHeuristicType() {
        return constructionHeuristicType;
    }

    @Benchmark
    public VehicleRoutingSolution benchmark() {
        return runBenchmark();
    }
}
