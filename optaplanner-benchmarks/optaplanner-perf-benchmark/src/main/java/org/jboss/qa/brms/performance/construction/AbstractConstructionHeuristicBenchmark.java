package org.jboss.qa.brms.performance.construction;

import java.util.Collections;

import org.jboss.qa.brms.performance.AbstractPlannerBenchmark;
import org.jboss.qa.brms.performance.examples.AbstractExample;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.core.config.solver.SolverConfig;

public abstract class AbstractConstructionHeuristicBenchmark<Solution_> extends AbstractPlannerBenchmark<Solution_> {

    public abstract ConstructionHeuristicType getConstructionHeuristicType();

    private final AbstractExample<Solution_> example;

    protected AbstractConstructionHeuristicBenchmark(AbstractExample<Solution_> example) {
        this.example = example;
    }

    @Override
    protected Solver<Solution_> createSolver() {
        SolverConfig solverConfig = example.getBaseSolverConfig();
        ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig =
                new ConstructionHeuristicPhaseConfig().withConstructionHeuristicType(getConstructionHeuristicType());
        solverConfig.setPhaseConfigList(Collections.singletonList(constructionHeuristicPhaseConfig));

        SolverFactory<Solution_> solverFactory = SolverFactory.create(solverConfig);
        return solverFactory.buildSolver();
    }
}
