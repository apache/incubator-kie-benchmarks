package org.jboss.qa.brms.performance.examples.tsp.termination;

import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.solver.termination.AbstractTermination;

public class HardTSPCalculateCountTermination extends AbstractTermination {

    protected long calculateCountLimit;

    public HardTSPCalculateCountTermination() {
        this.calculateCountLimit = 10000;
    }

    // ************************************************************************
    // Terminated methods
    // ************************************************************************

    @Override
    public boolean isSolverTerminated(SolverScope solverScope) {
        return isTerminated(solverScope.getScoreDirector());
    }

    @Override
    public boolean isPhaseTerminated(AbstractPhaseScope phaseScope) {
        return isTerminated(phaseScope.getScoreDirector());
    }

    protected boolean isTerminated(InnerScoreDirector scoreDirector) {
        long calculateCount = scoreDirector.getCalculationCount();
        return calculateCount >= calculateCountLimit;
    }

    // ************************************************************************
    // Time gradient methods
    // ************************************************************************

    @Override
    public double calculateSolverTimeGradient(SolverScope solverScope) {
        return calculateTimeGradient(solverScope.getScoreDirector());
    }

    @Override
    public double calculatePhaseTimeGradient(AbstractPhaseScope phaseScope) {
        return calculateTimeGradient(phaseScope.getScoreDirector());
    }

    protected double calculateTimeGradient(InnerScoreDirector scoreDirector) {
        long calculateCount = scoreDirector.getCalculationCount();
        double timeGradient = ((double) calculateCount) / ((double) calculateCountLimit);
        return Math.min(timeGradient, 1.0);
    }

}
