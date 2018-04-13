package org.jboss.qa.brms.performance.localsearch.cloudbalance.moveselector;

import org.jboss.qa.brms.performance.calculatecounttermination.CloudBalanceCalculateCountTermination;
import org.jboss.qa.brms.performance.configuration.AcceptorConfigurations;
import org.jboss.qa.brms.performance.localsearch.cloudbalance.AbstractCloudBalanceLocalSearchBenchmark;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

public abstract class AbstractCloudBalanceMoveSelectorBenchmark extends AbstractCloudBalanceLocalSearchBenchmark {

//    public class CloudBalanceMoveSelectorCalculateCountTermination extends AbstractCalculateCountTermination {
//
//        public CloudBalanceMoveSelectorCalculateCountTermination(long calculateCountLimit) {
//            super(100000);
//        }
//    }
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
        terminationConfig.setTerminationClass(CloudBalanceCalculateCountTermination.class);
        return terminationConfig;
    }
}
