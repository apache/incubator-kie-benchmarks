package org.jboss.qa.brms.performance.localsearch.tsp.moveselector;

import org.jboss.qa.brms.performance.examples.tsp.termination.HardTSPCalculateCountTermination;
import org.jboss.qa.brms.performance.configuration.AcceptorConfigurations;
import org.jboss.qa.brms.performance.localsearch.tsp.AbstractTSPLocalSearchBenchmark;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

public abstract class AbstractTSPMoveSelectorBenchmark extends AbstractTSPLocalSearchBenchmark {

    @Override
    public AcceptorConfig createAcceptorConfig() {
        return AcceptorConfigurations.createTabuSearchAcceptor(0.1);
    }

    @Override
    public int getAcceptedCountLimit() {
        return 10000000;
    }

    @Override
    public TerminationConfig getTerminationConfig() {
        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setTerminationClass(HardTSPCalculateCountTermination.class);
        return terminationConfig;
    }
}
