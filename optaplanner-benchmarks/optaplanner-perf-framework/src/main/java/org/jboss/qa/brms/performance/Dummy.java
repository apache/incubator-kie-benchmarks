package org.jboss.qa.brms.performance;

import org.jboss.qa.brms.performance.examples.cloudbalancing.CloudBalancing;
import org.jboss.qa.brms.performance.examples.tsp.TravelingSalesmanProblem;
import org.jboss.qa.brms.performance.examples.vehiclerouting.VehicleRouting;
import org.optaplanner.core.api.solver.Solver;

public class Dummy {

    public static void main(String[] args) {
        CloudBalancing cloudBalancing = new CloudBalancing();
        Solver solver = cloudBalancing.getDefaultConfig().buildSolver();
        solver.solve(cloudBalancing.loadSolvingProblem(CloudBalancing.DataSet.CB_400_1200));

        System.out.println("next");
        VehicleRouting vehicleRouting = new VehicleRouting();
        solver = vehicleRouting.getDefaultConfig().buildSolver();
        solver.solve(vehicleRouting.loadSolvingProblem(VehicleRouting.DataSet.VRP_ROAD_29));

        System.out.println("next");
        TravelingSalesmanProblem travelingSalesmanProblem = new TravelingSalesmanProblem();
        solver = travelingSalesmanProblem.getDefaultConfig().buildSolver();
        solver.solve(travelingSalesmanProblem.loadSolvingProblem(TravelingSalesmanProblem.DataSet.USA_CA_2716));
    }

}
