package org.jboss.qa.brms.performance.examples;

import org.jboss.qa.brms.performance.examples.cloudbalancing.CloudBalancing;
import org.jboss.qa.brms.performance.examples.conferencescheduling.ConferenceScheduling;
import org.jboss.qa.brms.performance.examples.nurserostering.NurseRostering;
import org.jboss.qa.brms.performance.examples.projectjobscheduling.ProjectJobScheduling;
import org.jboss.qa.brms.performance.examples.tsp.TravelingSalesmanProblem;
import org.jboss.qa.brms.performance.examples.vehiclerouting.VehicleRouting;

public class Examples {
    public static final CloudBalancing CLOUD_BALANCING = new CloudBalancing();
    public static final ConferenceScheduling CONFERENCE_SCHEDULING = new ConferenceScheduling();
    public static final NurseRostering NURSE_ROSTERING = new NurseRostering();
    public static final ProjectJobScheduling PROJECT_JOB_SCHEDULING = new ProjectJobScheduling();
    public static final TravelingSalesmanProblem TRAVELING_SALESMAN = new TravelingSalesmanProblem();
    public static final VehicleRouting VEHICLE_ROUTING = new VehicleRouting();
}
