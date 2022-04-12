package org.jboss.qa.brms.performance.examples.vehiclerouting.solution;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Standstill;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;

import java.util.List;

public class VehicleRoutingSolutionInitializer implements CustomPhaseCommand<VehicleRoutingSolution> {

    @Override
    public void changeWorkingSolution(ScoreDirector<VehicleRoutingSolution> scoreDirector) {
        VehicleRoutingSolution solution = scoreDirector.getWorkingSolution();

        List<Customer> customers = solution.getCustomerList();
        List<Vehicle> vehicles = solution.getVehicleList();

        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            Standstill tmpAppearance = (i == 0) ? vehicles.get(0) : customers.get(i - 1);
            scoreDirector.beforeVariableChanged(customer, "previousStandstill");
            customer.setPreviousStandstill(tmpAppearance);
            scoreDirector.afterVariableChanged(customer, "previousStandstill");
        }
        scoreDirector.triggerVariableListeners();

        InnerScoreDirector<VehicleRoutingSolution, ?> innerScoreDirector =
                (InnerScoreDirector<VehicleRoutingSolution, ?>) scoreDirector;
        Score<?> score = innerScoreDirector.calculateScore();

        if (!score.isSolutionInitialized()) {
            throw new IllegalStateException(
                    "The solution [" + VehicleRoutingSolution.class.getSimpleName()
                    + "] was not fully initialized by CustomSolverPhase: "
                    + this.getClass().getCanonicalName());
        }
    }
}
