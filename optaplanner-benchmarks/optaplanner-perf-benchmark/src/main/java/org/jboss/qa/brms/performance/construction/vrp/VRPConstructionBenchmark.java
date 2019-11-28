package org.jboss.qa.brms.performance.construction.vrp;

import org.jboss.qa.brms.performance.construction.AbstractConstructionHeuristicBenchmark;
import org.jboss.qa.brms.performance.examples.vehiclerouting.VehicleRouting;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;

public class VRPConstructionBenchmark extends AbstractConstructionHeuristicBenchmark<VehicleRoutingSolution, VehicleRouting> {

    public VRPConstructionBenchmark() {
        super(VehicleRouting.class);
    }

    @Param({"FIRST_FIT", "FIRST_FIT_DECREASING"})
    private ConstructionHeuristicType constructionHeuristicType;

    @Param({"VRP_USA_100_10", "VRP_USA_1000_20", "VRP_USA_10000_100"})
    private VehicleRouting.DataSet dataset;

    @Override
    public void initSolution() {
        super.setSolution(new VehicleRouting().loadSolvingProblem(dataset));
    }

    @Override
    public ConstructionHeuristicType getConstructionHeuristicType() {
        return constructionHeuristicType;
    }

    @Benchmark
    @Override
    public VehicleRoutingSolution benchmark() {
        return super.benchmark();
    }
}
