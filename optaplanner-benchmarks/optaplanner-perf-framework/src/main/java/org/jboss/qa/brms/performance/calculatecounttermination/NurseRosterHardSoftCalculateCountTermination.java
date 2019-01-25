package org.jboss.qa.brms.performance.calculatecounttermination;

import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.solver.termination.AbstractTermination;

public class NurseRosterHardSoftCalculateCountTermination extends AbstractTermination {

    protected long calculateCountLimit;

    public NurseRosterHardSoftCalculateCountTermination() {
        this.calculateCountLimit = 10000;
    }

    @Override
    public boolean isSolverTerminated(DefaultSolverScope defaultSolverScope) {
        return isTerminated(defaultSolverScope.getScoreDirector());
    }

    @Override
    public boolean isPhaseTerminated(AbstractPhaseScope abstractPhaseScope) {
        return isTerminated(abstractPhaseScope.getScoreDirector());
    }

    @Override
    public double calculateSolverTimeGradient(DefaultSolverScope defaultSolverScope) {
        return calculateTimeGradient(defaultSolverScope.getScoreDirector());
    }

    @Override
    public double calculatePhaseTimeGradient(AbstractPhaseScope abstractPhaseScope) {
        return calculateTimeGradient(abstractPhaseScope.getScoreDirector());
    }

    protected double calculateTimeGradient(InnerScoreDirector scoreDirector) {
        long calculateCount = scoreDirector.getCalculationCount();
        double timeGradient = ((double) calculateCount) / ((double) calculateCountLimit);
        return Math.min(timeGradient, 1.0);
    }

    protected boolean isTerminated(InnerScoreDirector scoreDirector) {
        long calculateCount = scoreDirector.getCalculationCount();
        return calculateCount >= calculateCountLimit;
    }
}
