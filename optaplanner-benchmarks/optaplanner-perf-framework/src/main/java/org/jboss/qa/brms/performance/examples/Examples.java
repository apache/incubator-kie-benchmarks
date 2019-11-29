package org.jboss.qa.brms.performance.examples;

import org.jboss.qa.brms.performance.examples.cloudbalancing.CloudBalancingExample;
import org.jboss.qa.brms.performance.examples.conferencescheduling.ConferenceSchedulingExample;
import org.jboss.qa.brms.performance.examples.nurserostering.NurseRosteringExample;
import org.jboss.qa.brms.performance.examples.projectjobscheduling.ProjectJobSchedulingExample;
import org.jboss.qa.brms.performance.examples.tsp.TravelingSalesmanExample;
import org.jboss.qa.brms.performance.examples.vehiclerouting.VehicleRoutingExample;

public class Examples {
    public static final CloudBalancingExample CLOUD_BALANCING = new CloudBalancingExample();
    public static final ConferenceSchedulingExample CONFERENCE_SCHEDULING = new ConferenceSchedulingExample();
    public static final NurseRosteringExample NURSE_ROSTERING = new NurseRosteringExample();
    public static final ProjectJobSchedulingExample PROJECT_JOB_SCHEDULING = new ProjectJobSchedulingExample();
    public static final TravelingSalesmanExample TRAVELING_SALESMAN = new TravelingSalesmanExample();
    public static final VehicleRoutingExample VEHICLE_ROUTING = new VehicleRoutingExample();
}
