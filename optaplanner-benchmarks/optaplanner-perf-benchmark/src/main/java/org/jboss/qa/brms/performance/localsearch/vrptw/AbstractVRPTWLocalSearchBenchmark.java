package org.jboss.qa.brms.performance.localsearch.vrptw;

import org.jboss.qa.brms.performance.examples.vehiclerouting.VehicleRouting;
import org.jboss.qa.brms.performance.examples.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;
import org.jboss.qa.brms.performance.examples.vehiclerouting.solver.phase.VehicleRoutingSolutionInitializer;
import org.jboss.qa.brms.performance.localsearch.AbstractLocalSearchPlannerBenchmark;
import org.openjdk.jmh.annotations.Param;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchForagerConfig;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.phase.custom.CustomPhaseConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;

import java.util.Collections;

public abstract class AbstractVRPTWLocalSearchBenchmark extends AbstractLocalSearchPlannerBenchmark {

    @Param({"BELGIUM_TW_50_10", "BELGIUM_TW_500_20", "BELGIUM_TW_2750_55"})
    private VehicleRouting.DataSet dataset;

    @Override
    protected Solution createInitialSolution() {
        VehicleRouting vehicleRouting = new VehicleRouting();
        Solution solution = vehicleRouting.loadSolvingProblem(dataset);
        SolverConfig defaultConstruction = vehicleRouting.getBaseConfig();
        defaultConstruction.getEntityClassList().add(TimeWindowedCustomer.class); // diff from simple VRP
//        ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig = new ConstructionHeuristicPhaseConfig();
//        constructionHeuristicPhaseConfig.setConstructionHeuristicType(ConstructionHeuristicType.FIRST_FIT);
//        defaultConstruction.setPhaseConfigList(Collections.singletonList(((PhaseConfig) constructionHeuristicPhaseConfig)));
        CustomPhaseConfig customPhaseConfig = new CustomPhaseConfig();
        customPhaseConfig.setCustomPhaseCommandClassList(
                Collections.<Class<? extends CustomPhaseCommand>>singletonList(VehicleRoutingSolutionInitializer.class));
        defaultConstruction.setPhaseConfigList(Collections.singletonList((PhaseConfig) customPhaseConfig));
        Solver constructionSolver = defaultConstruction.buildSolver();
        constructionSolver.solve(solution);
        return constructionSolver.getBestSolution();
    }

    @Override
    public void initSolver() {
        SolverConfig config = new VehicleRouting().getBaseConfig();
        config.getEntityClassList().add(TimeWindowedCustomer.class);
        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        localSearchPhaseConfig.setMoveSelectorConfig(new UnionMoveSelectorConfig());
        ((UnionMoveSelectorConfig) localSearchPhaseConfig.getMoveSelectorConfig())
                .setMoveSelectorConfigList(createMoveSelectorConfigList());

        localSearchPhaseConfig.setAcceptorConfig(createAcceptorConfig());

        localSearchPhaseConfig.setForagerConfig(new LocalSearchForagerConfig());
        localSearchPhaseConfig.getForagerConfig().setAcceptedCountLimit(getAcceptedCountLimit());

        localSearchPhaseConfig.setTerminationConfig(getTerminationConfig());
        config.setPhaseConfigList(Collections.<PhaseConfig>singletonList(localSearchPhaseConfig));
        super.setSolver(config.buildSolver());
    }
}
