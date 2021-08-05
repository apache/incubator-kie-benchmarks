package org.jboss.qa.brms.performance.localsearch.vrptw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.qa.brms.performance.examples.Examples;
import org.jboss.qa.brms.performance.examples.vehiclerouting.VehicleRoutingExample;
import org.jboss.qa.brms.performance.examples.vehiclerouting.solution.VehicleRoutingSolutionInitializer;
import org.jboss.qa.brms.performance.localsearch.AbstractLocalSearchPlannerBenchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchForagerConfig;
import org.optaplanner.core.config.phase.custom.CustomPhaseConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;

public abstract class AbstractVRPTWLocalSearchBenchmark
        extends AbstractLocalSearchPlannerBenchmark<VehicleRoutingSolution> {

    @Param({"BELGIUM_TW_50_10", "BELGIUM_TW_500_20", "BELGIUM_TW_2750_55"})
    private VehicleRoutingExample.DataSet dataset;

    @Override
    protected VehicleRoutingSolution createInitialSolution() {
        SolverConfig solverConfig = Examples.VEHICLE_ROUTING.getBaseSolverConfig();
        // different from simple VRP
        List<Class<?>> classes = new ArrayList<>(solverConfig.getEntityClassList());
        classes.add(TimeWindowedCustomer.class);
        solverConfig.setEntityClassList(classes);

        CustomPhaseConfig customPhaseConfig = new CustomPhaseConfig();
        customPhaseConfig.setCustomPhaseCommandClassList(
                Collections.singletonList(VehicleRoutingSolutionInitializer.class));
        solverConfig.setPhaseConfigList(Collections.singletonList(customPhaseConfig));

        SolverFactory<VehicleRoutingSolution> solverFactory = SolverFactory.create(solverConfig);
        Solver<VehicleRoutingSolution> constructionSolver = solverFactory.buildSolver();

        VehicleRoutingSolution solution = Examples.VEHICLE_ROUTING.loadSolvingProblem(dataset);
        return constructionSolver.solve(solution);
    }

    @Override
    protected Solver<VehicleRoutingSolution> createSolver() {
        SolverConfig solverConfig = Examples.VEHICLE_ROUTING.getBaseSolverConfig();

        List<Class<?>> classes = new ArrayList<>(solverConfig.getEntityClassList());
        classes.add(TimeWindowedCustomer.class);
        solverConfig.setEntityClassList(classes);
        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        localSearchPhaseConfig.setMoveSelectorConfig(new UnionMoveSelectorConfig());
        ((UnionMoveSelectorConfig) localSearchPhaseConfig.getMoveSelectorConfig())
                .setMoveSelectorConfigList(createMoveSelectorConfigList());

        localSearchPhaseConfig.setAcceptorConfig(createAcceptorConfig());
        localSearchPhaseConfig.setForagerConfig(new LocalSearchForagerConfig());
        localSearchPhaseConfig.getForagerConfig().setAcceptedCountLimit(getAcceptedCountLimit());
        localSearchPhaseConfig.setTerminationConfig(getTerminationConfig());
        solverConfig.setPhaseConfigList(Collections.singletonList(localSearchPhaseConfig));

        SolverFactory<VehicleRoutingSolution> solverFactory = SolverFactory.create(solverConfig);
        return solverFactory.buildSolver();
    }
}
