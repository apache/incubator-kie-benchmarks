package org.jboss.qa.brms.performance.examples.tsp.solver.phase;

import org.jboss.qa.brms.performance.examples.tsp.domain.TravelingSalesmanTour;
import org.jboss.qa.brms.performance.examples.tsp.domain.Visit;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.List;

public class TSPSolutionInitializer implements CustomPhaseCommand<TravelingSalesmanTour> {

    @Override
    public void changeWorkingSolution(ScoreDirector<TravelingSalesmanTour> scoreDirector) {
        TravelingSalesmanTour solution = scoreDirector.getWorkingSolution();

        List<Visit> visitList = solution.getVisitList();

        for (int i = 0; i < visitList.size(); i++) {
            Visit visit = visitList.get(i);
            scoreDirector.beforeVariableChanged(visit, "previousStandstill");
            visit.setPreviousStandstill((i == 0)
                    ? solution.getDomicile() : visitList.get(i - 1));
            scoreDirector.afterVariableChanged(visit, "previousStandstill");
        }
        scoreDirector.triggerVariableListeners();
        Score<?> score = scoreDirector.calculateScore();

        if (!score.isSolutionInitialized()) {
            throw new IllegalStateException(
                    "The solution [" + TravelingSalesmanTour.class.getSimpleName()
                    + "] was not fully initialized by CustomSolverPhase: "
                    + this.getClass().getCanonicalName());
        }
    }
}
