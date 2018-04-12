package org.jboss.qa.brms.performance.examples.vehiclerouting.solver.phase;

import org.jboss.qa.brms.performance.examples.vehiclerouting.domain.Customer;
import org.jboss.qa.brms.performance.examples.vehiclerouting.domain.Standstill;
import org.jboss.qa.brms.performance.examples.vehiclerouting.domain.Vehicle;
import org.jboss.qa.brms.performance.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;
import org.optaplanner.core.impl.score.director.ScoreDirector;

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
        Score<?> score = scoreDirector.calculateScore();

        if (!score.isSolutionInitialized()) {
            throw new IllegalStateException(
                    "The solution [" + VehicleRoutingSolution.class.getSimpleName()
                    + "] was not fully initialized by CustomSolverPhase: "
                    + this.getClass().getCanonicalName());
        }
    }
}
