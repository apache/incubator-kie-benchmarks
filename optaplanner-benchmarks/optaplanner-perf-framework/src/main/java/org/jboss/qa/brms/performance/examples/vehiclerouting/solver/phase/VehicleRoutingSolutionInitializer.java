package org.jboss.qa.brms.performance.examples.vehiclerouting.solver.phase;

import org.jboss.qa.brms.performance.examples.vehiclerouting.domain.Customer;
import org.jboss.qa.brms.performance.examples.vehiclerouting.domain.Standstill;
import org.jboss.qa.brms.performance.examples.vehiclerouting.domain.Vehicle;
import org.jboss.qa.brms.performance.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;
import org.optaplanner.core.impl.score.director.AbstractScoreDirector;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.List;
import java.util.Map;

public class VehicleRoutingSolutionInitializer implements CustomPhaseCommand {

    @Override
    public void applyCustomProperties(Map<String, String> map) {

    }

    @Override
    public void changeWorkingSolution(ScoreDirector scoreDirector) {
        VehicleRoutingSolution solution = (VehicleRoutingSolution) scoreDirector.getWorkingSolution();

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
        scoreDirector.calculateScore();

        if (((AbstractScoreDirector) scoreDirector).countWorkingSolutionUninitializedVariables() != 0) {
            throw new IllegalStateException(
                    "The solution [VrpSchedule] was not fully initialized by CustomSolverPhase: "
                    + this.getClass().getCanonicalName());
        }
    }
}
