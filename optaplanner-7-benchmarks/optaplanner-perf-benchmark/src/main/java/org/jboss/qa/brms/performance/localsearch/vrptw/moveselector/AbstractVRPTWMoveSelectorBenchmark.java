package org.jboss.qa.brms.performance.localsearch.vrptw.moveselector;

import org.jboss.qa.brms.performance.examples.vehiclerouting.termination.EasyVRPCalculateCountTermination;
import org.jboss.qa.brms.performance.configuration.AcceptorConfigurations;
import org.jboss.qa.brms.performance.localsearch.vrptw.AbstractVRPTWLocalSearchBenchmark;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

public abstract class AbstractVRPTWMoveSelectorBenchmark extends AbstractVRPTWLocalSearchBenchmark {

    @Override
    public AcceptorConfig createAcceptorConfig() {
        return AcceptorConfigurations.createSimulatedAnnealingAcceptor("0hard/0soft");
    }

    @Override
    public int getAcceptedCountLimit() {
        return 10000000;
    }

    @Override
    public TerminationConfig getTerminationConfig() {
        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setTerminationClass(EasyVRPCalculateCountTermination.class);
        return terminationConfig;
    }
}
