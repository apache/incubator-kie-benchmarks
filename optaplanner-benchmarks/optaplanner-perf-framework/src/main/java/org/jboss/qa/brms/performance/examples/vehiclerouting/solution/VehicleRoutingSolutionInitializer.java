package org.jboss.qa.brms.performance.examples.vehiclerouting.solution;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;

public class VehicleRoutingSolutionInitializer implements CustomPhaseCommand<VehicleRoutingSolution> {

    @Override
    public void changeWorkingSolution(ScoreDirector<VehicleRoutingSolution> scoreDirector) {
        VehicleRoutingSolution solution = scoreDirector.getWorkingSolution();

        Vehicle vehicle = solution.getVehicleList().get(0);

        int i = 0;
        scoreDirector.beforeListVariableChanged(vehicle, "customers", 0, 0);
        for (Customer customer : solution.getCustomerList()) {
            scoreDirector.beforeListVariableElementAssigned(vehicle, "customers", customer);
            vehicle.getCustomers().add(customer);
            scoreDirector.afterListVariableElementAssigned(vehicle, "customers", customer);
            i++;
        }
        scoreDirector.afterListVariableChanged(vehicle, "customers", 0, vehicle.getCustomers().size());
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
