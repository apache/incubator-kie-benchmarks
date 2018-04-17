package org.jboss.qa.brms.performance.examples.cloudbalancing.solver.phase;

import org.jboss.qa.brms.performance.examples.cloudbalancing.domain.CloudBalance;
import org.jboss.qa.brms.performance.examples.cloudbalancing.domain.CloudProcess;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;
import org.optaplanner.core.impl.score.director.AbstractScoreDirector;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.Map;

public class CloudBalanceSolutionInitializer implements CustomPhaseCommand {

    public CloudBalanceSolutionInitializer() {
    }

    @Override
    public void applyCustomProperties(Map<String, String> map) {

    }

    @Override
    public void changeWorkingSolution(ScoreDirector scoreDirector) {
        CloudBalance cb = (CloudBalance) scoreDirector.getWorkingSolution();
        int i = 0;
        for (CloudProcess p : cb.getProcessList()) {
            scoreDirector.beforeVariableChanged(p, "computer");
            p.setComputer(cb.getComputerList().get(i % cb.getComputerList().size()));
            scoreDirector.afterVariableChanged(p, "computer");
            i++;
        }
        scoreDirector.triggerVariableListeners();
        scoreDirector.calculateScore();

        if (((AbstractScoreDirector) scoreDirector).countWorkingSolutionUninitializedVariables() != 0) {
            throw new IllegalStateException(
                    "The solution [CloudBalance] was not fully initialized by CustomSolverPhase: "
                    + this.getClass().getCanonicalName());
        }
    }
}
