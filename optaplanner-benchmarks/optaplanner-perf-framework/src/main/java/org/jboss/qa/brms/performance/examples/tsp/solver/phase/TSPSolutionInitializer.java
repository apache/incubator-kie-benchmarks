package org.jboss.qa.brms.performance.examples.tsp.solver.phase;

import org.jboss.qa.brms.performance.examples.tsp.domain.TravelingSalesmanTour;
import org.jboss.qa.brms.performance.examples.tsp.domain.Visit;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;
import org.optaplanner.core.impl.score.director.AbstractScoreDirector;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.List;
import java.util.Map;

public class TSPSolutionInitializer implements CustomPhaseCommand {

    @Override
    public void applyCustomProperties(Map<String, String> map) {

    }

    @Override
    public void changeWorkingSolution(ScoreDirector scoreDirector) {
        TravelingSalesmanTour solution = (TravelingSalesmanTour) scoreDirector.getWorkingSolution();

        List<Visit> visitList = solution.getVisitList();

        for (int i = 0; i < visitList.size(); i++) {
            Visit visit = visitList.get(i);
            scoreDirector.beforeVariableChanged(visit, "previousStandstill");
            visit.setPreviousStandstill((i == 0)
                    ? solution.getDomicile() : visitList.get(i - 1));
            scoreDirector.afterVariableChanged(visit, "previousStandstill");
        }
        scoreDirector.triggerVariableListeners();
        scoreDirector.calculateScore();

        if (((AbstractScoreDirector) scoreDirector).countWorkingSolutionUninitializedVariables() != 0) {
            throw new IllegalStateException(
                    "The solution [VrpSchedule] was not fully initialized by CustomSolverPhase: "
                    + this.getClass().getCanonicalName());
        }
    }
}
