package org.jboss.qa.brms.performance.localsearch.vrptw.heuristics;

import org.jboss.qa.brms.performance.configuration.AcceptorConfigurations;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.openjdk.jmh.annotations.Benchmark;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorConfig;

public class VRPTWLateAcceptanceBenchmark extends AbstractVRPTWHeuristicBenchmark {

    @Override
    public AcceptorConfig createAcceptorConfig() {
        return AcceptorConfigurations.createLateAcceptanceAcceptor(50);
    }

    @Benchmark
    public VehicleRoutingSolution benchmark() {
        return runBenchmark();
    }
}
