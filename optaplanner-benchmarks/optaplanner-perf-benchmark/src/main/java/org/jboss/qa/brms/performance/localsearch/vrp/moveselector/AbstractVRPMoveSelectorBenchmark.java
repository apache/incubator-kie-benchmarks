package org.jboss.qa.brms.performance.localsearch.vrp.moveselector;

import org.jboss.qa.brms.performance.calculatecounttermination.HardVRPCalculateCountTermination;
import org.jboss.qa.brms.performance.configuration.AcceptorConfigurations;
import org.jboss.qa.brms.performance.localsearch.vrp.AbstractVRPLocalSearchBenchmark;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

public abstract class AbstractVRPMoveSelectorBenchmark extends AbstractVRPLocalSearchBenchmark {

    @Override
    public AcceptorConfig createAcceptorConfig() {
        return AcceptorConfigurations.createSimulatedAnnealingAcceptor("0hard/0soft");
    }

    @Override
    public int getAcceptedCountLimit() {
        return 1000000;
    }

    @Override
    public TerminationConfig getTerminationConfig() {
        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setTerminationClass(HardVRPCalculateCountTermination.class);
        return terminationConfig;
    }
}
