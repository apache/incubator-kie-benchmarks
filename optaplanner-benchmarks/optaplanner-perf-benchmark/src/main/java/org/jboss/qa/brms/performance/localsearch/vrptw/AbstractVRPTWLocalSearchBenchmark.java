package org.jboss.qa.brms.performance.localsearch.vrptw;

import org.jboss.qa.brms.performance.examples.vehiclerouting.VehicleRouting;
import org.jboss.qa.brms.performance.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.jboss.qa.brms.performance.examples.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;
import org.jboss.qa.brms.performance.examples.vehiclerouting.solver.phase.VehicleRoutingSolutionInitializer;
import org.jboss.qa.brms.performance.localsearch.AbstractLocalSearchPlannerBenchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchForagerConfig;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.phase.custom.CustomPhaseConfig;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;

import java.util.Collections;

public abstract class AbstractVRPTWLocalSearchBenchmark
        extends AbstractLocalSearchPlannerBenchmark<VehicleRoutingSolution> {

    @Param({"BELGIUM_TW_50_10", "BELGIUM_TW_500_20", "BELGIUM_TW_2750_55"})
    private VehicleRouting.DataSet dataset;

    @Override
    protected VehicleRoutingSolution createInitialSolution() {
        VehicleRouting vehicleRouting = new VehicleRouting();
        VehicleRoutingSolution solution = vehicleRouting.loadSolvingProblem(dataset);
        SolverFactory<VehicleRoutingSolution> defaultConstruction = vehicleRouting.getBaseSolverFactory();
        // different from simple VRP
        defaultConstruction.getSolverConfig().getEntityClassList().add(TimeWindowedCustomer.class);
        CustomPhaseConfig customPhaseConfig = new CustomPhaseConfig();
        customPhaseConfig.setCustomPhaseCommandClassList(
                Collections.<Class<? extends CustomPhaseCommand>>singletonList(VehicleRoutingSolutionInitializer.class));
        defaultConstruction.getSolverConfig()
                .setPhaseConfigList(Collections.singletonList((PhaseConfig) customPhaseConfig));
        Solver<VehicleRoutingSolution> constructionSolver = defaultConstruction.buildSolver();
        constructionSolver.solve(solution);
        return constructionSolver.getBestSolution();
    }

    @Override
    public void initSolver() {
        SolverFactory<VehicleRoutingSolution> solverFactory = new VehicleRouting().getBaseSolverFactory();
        solverFactory.getSolverConfig().getEntityClassList().add(TimeWindowedCustomer.class);
        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        localSearchPhaseConfig.setMoveSelectorConfig(new UnionMoveSelectorConfig());
        ((UnionMoveSelectorConfig) localSearchPhaseConfig.getMoveSelectorConfig())
                .setMoveSelectorConfigList(createMoveSelectorConfigList());

        localSearchPhaseConfig.setAcceptorConfig(createAcceptorConfig());

        localSearchPhaseConfig.setForagerConfig(new LocalSearchForagerConfig());
        localSearchPhaseConfig.getForagerConfig().setAcceptedCountLimit(getAcceptedCountLimit());

        localSearchPhaseConfig.setTerminationConfig(getTerminationConfig());
        solverFactory.getSolverConfig()
                .setPhaseConfigList(Collections.<PhaseConfig>singletonList(localSearchPhaseConfig));
        super.setSolver(solverFactory.buildSolver());
    }
}
