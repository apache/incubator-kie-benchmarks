package org.jboss.qa.brms.performance.construction.vrp;

import org.jboss.qa.brms.performance.construction.AbstractConstructionHeuristicPlannerBenchmark;
import org.jboss.qa.brms.performance.examples.vehiclerouting.VehicleRouting;
import org.jboss.qa.brms.performance.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.jboss.qa.brms.performance.examples.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.core.config.phase.PhaseConfig;

import java.util.Collections;

public class VRPTimeWindowedConstructionBenchmark
        extends AbstractConstructionHeuristicPlannerBenchmark<VehicleRoutingSolution> {

    @Param({"FIRST_FIT", "FIRST_FIT_DECREASING"})
    private ConstructionHeuristicType constructionHeuristicType;

    @Param({"BELGIUM_TW_50_10", "BELGIUM_TW_500_20", "BELGIUM_TW_2750_55"})
    private VehicleRouting.DataSet dataset;

    @Override
    public void initSolver() {
        SolverFactory<VehicleRoutingSolution> solverFactory = new VehicleRouting().getBaseSolverFactory();
        solverFactory.getSolverConfig()
                .getEntityClassList().add(TimeWindowedCustomer.class); // diff between normal VRP and TimeWindowed
        ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig = new ConstructionHeuristicPhaseConfig();
        constructionHeuristicPhaseConfig.setConstructionHeuristicType(getConstructionHeuristicType());
        solverFactory.getSolverConfig()
                .setPhaseConfigList(Collections.singletonList(((PhaseConfig) constructionHeuristicPhaseConfig)));
        super.setSolver(solverFactory.buildSolver());
    }

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
    public void benchmark() {
        super.benchmark();
    }
}
