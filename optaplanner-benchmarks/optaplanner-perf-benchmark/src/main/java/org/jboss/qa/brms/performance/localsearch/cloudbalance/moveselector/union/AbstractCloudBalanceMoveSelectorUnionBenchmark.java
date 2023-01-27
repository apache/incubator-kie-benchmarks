package org.jboss.qa.brms.performance.localsearch.cloudbalance.moveselector.union;

import org.jboss.qa.brms.performance.configuration.AcceptorConfigurations;
import org.jboss.qa.brms.performance.localsearch.cloudbalance.AbstractCloudBalanceLocalSearchBenchmark;
import org.optaplanner.core.config.localsearch.decider.acceptor.LocalSearchAcceptorConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

public abstract class AbstractCloudBalanceMoveSelectorUnionBenchmark extends AbstractCloudBalanceLocalSearchBenchmark {
    @Override
    public LocalSearchAcceptorConfig createAcceptorConfig() {
        return AcceptorConfigurations.createLateAcceptanceAcceptor(100);
    }
    @Override
    public int getAcceptedCountLimit() {
        return 100;
    }
    @Override
    public TerminationConfig getTerminationConfig() {
        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setStepCountLimit(100);
        return terminationConfig;
    }

}
