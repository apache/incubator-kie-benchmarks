package org.jboss.qa.brms.performance;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.jboss.qa.brms.performance.construction.projectjobscheduling.ProjectJobSchedulingConstructionBenchmark;
import org.jboss.qa.brms.performance.construction.tsp.TSPConstructionBenchmark;
import org.jboss.qa.brms.performance.examples.Examples;
import org.jboss.qa.brms.performance.examples.cloudbalancing.CloudBalancingExample;
import org.jboss.qa.brms.performance.examples.conferencescheduling.ConferenceSchedulingExample;
import org.jboss.qa.brms.performance.examples.flightcrewscheduling.FlightCrewSchedulingExample;
import org.jboss.qa.brms.performance.examples.nurserostering.NurseRosteringExample;
import org.jboss.qa.brms.performance.examples.projectjobscheduling.ProjectJobSchedulingExample;
import org.jboss.qa.brms.performance.examples.travelingtournament.TravelingTournamentExample;
import org.jboss.qa.brms.performance.examples.tsp.TravelingSalesmanExample;
import org.jboss.qa.brms.performance.examples.vehiclerouting.VehicleRoutingExample;
import org.jboss.qa.brms.performance.realworld.cloudbalancing.CloudBalanceBenchmark;
import org.jboss.qa.brms.performance.realworld.conferencescheduling.ConferenceSchedulingBenchmark;
import org.jboss.qa.brms.performance.realworld.nurserostering.NurseRosteringBenchmark;
import org.jboss.qa.brms.performance.score.constraintstream.FlightCrewSchedulingConstraintStreamsBenchmark;
import org.jboss.qa.brms.performance.score.constraintstream.TravelingTournamentConstraintStreamsBenchmark;
import org.jboss.qa.brms.performance.score.constraintstream.VehicleRoutingConstraintStreamsBenchmark;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

class BenchmarksTest {

    private static Stream<Arguments> problemSetAndSolverConfigLsBenchmarks() {
        return Stream.of(
                Arguments.of(new CloudBalanceBenchmark(CloudBalancingExample.DataSet.CB_100_300), Examples.CLOUD_BALANCING.getSolverConfigFromXml()),
                 Arguments.of(new ConferenceSchedulingBenchmark(ConferenceSchedulingExample.DataSet.TALKS_36_TIMESLOTS_12_ROOMS_5), Examples.CONFERENCE_SCHEDULING.getSolverConfigFromXml()),
                 Arguments.of(new FlightCrewSchedulingConstraintStreamsBenchmark(FlightCrewSchedulingExample.DataSet.EUROPE_175_FLIGHTS_7_DAYS), Examples.FLIGHT_CREW_SCHEDULING.getSolverConfigFromXml()),
                 Arguments.of(new NurseRosteringBenchmark(NurseRosteringExample.DataSet.SPRINT), Examples.NURSE_ROSTERING.getSolverConfigFromXml()),
                 Arguments.of(new TSPConstructionBenchmark(ConstructionHeuristicType.FIRST_FIT, TravelingSalesmanExample.DataSet.LU_980), Examples.TRAVELING_SALESMAN.getSolverConfigFromXml()),
                 Arguments.of(new TravelingTournamentConstraintStreamsBenchmark(TravelingTournamentExample.DataSet.SUPER_06), Examples.TRAVELING_TOURNAMENT.getSolverConfigFromXml()),
                 Arguments.of(new VehicleRoutingConstraintStreamsBenchmark(VehicleRoutingExample.DataSet.VRP_USA_100_10), Examples.VEHICLE_ROUTING.getSolverConfigFromXml())
        );
    }

    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    @ParameterizedTest
    @MethodSource("problemSetAndSolverConfigLsBenchmarks")
    public void benchmarksLsTest(AbstractPlannerBenchmark benchmark, SolverConfig solverConfig) {
                solverConfig.getPhaseConfigList().forEach(p -> {
            if (p instanceof LocalSearchPhaseConfig) {
                p.setTerminationConfig(new TerminationConfig().withStepCountLimit(1));
            }
        });
        SolverFactory.create(solverConfig).buildSolver().solve(benchmark.createInitialSolution());
    }

    private static Stream<Arguments> problemSetAndSolverConfigCsBenchmark() {
        return Stream.of(
                Arguments.of(new ProjectJobSchedulingConstructionBenchmark(ConstructionHeuristicType.FIRST_FIT, ProjectJobSchedulingExample.DataSet.A_4),new ProjectJobSchedulingExample().getBaseSolverConfig())
        );
    }

    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    @ParameterizedTest
    @MethodSource("problemSetAndSolverConfigCsBenchmark")
    public void benchmarksChTest(AbstractPlannerBenchmark benchmark, SolverConfig solverConfig) {
        solverConfig.setTerminationConfig(new TerminationConfig().withScoreCalculationCountLimit(1L));
        SolverFactory.create(solverConfig).buildSolver().solve(benchmark.createInitialSolution());
    }

}