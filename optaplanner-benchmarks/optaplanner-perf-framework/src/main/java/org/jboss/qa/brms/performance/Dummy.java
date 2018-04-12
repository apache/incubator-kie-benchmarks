package org.jboss.qa.brms.performance;

import org.jboss.qa.brms.performance.examples.cloudbalancing.CloudBalancing;
import org.jboss.qa.brms.performance.examples.cloudbalancing.domain.CloudBalance;
import org.jboss.qa.brms.performance.examples.tsp.TravelingSalesmanProblem;
import org.jboss.qa.brms.performance.examples.tsp.domain.TravelingSalesmanTour;
import org.jboss.qa.brms.performance.examples.vehiclerouting.VehicleRouting;
import org.jboss.qa.brms.performance.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.core.api.solver.Solver;

public class Dummy {

    public static void main(String[] args) {
        CloudBalancing cloudBalancing = new CloudBalancing();
        Solver<CloudBalance> cbSolver = cloudBalancing.getDefaultSolverFactory().buildSolver();
        cbSolver.solve(cloudBalancing.loadSolvingProblem(CloudBalancing.DataSet.CB_400_1200));

        System.out.println("next");
        VehicleRouting vehicleRouting = new VehicleRouting();
        Solver<VehicleRoutingSolution> vrpSolver = vehicleRouting.getDefaultSolverFactory().buildSolver();
        vrpSolver.solve(vehicleRouting.loadSolvingProblem(VehicleRouting.DataSet.VRP_ROAD_29));

        System.out.println("next");
        TravelingSalesmanProblem travelingSalesmanProblem = new TravelingSalesmanProblem();
        Solver<TravelingSalesmanTour> tspSolver = travelingSalesmanProblem.getDefaultSolverFactory().buildSolver();
        tspSolver.solve(travelingSalesmanProblem.loadSolvingProblem(TravelingSalesmanProblem.DataSet.USA_CA_2716));
    }

}
