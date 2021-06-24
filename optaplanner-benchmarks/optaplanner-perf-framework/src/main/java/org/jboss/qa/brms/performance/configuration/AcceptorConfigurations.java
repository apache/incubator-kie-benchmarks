package org.jboss.qa.brms.performance.configuration;

import org.optaplanner.core.config.localsearch.decider.acceptor.LocalSearchAcceptorConfig;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorType;

import java.util.Collections;

public class AcceptorConfigurations {

    public static LocalSearchAcceptorConfig createHillClimbingAcceptor() {
        LocalSearchAcceptorConfig acceptorConfig = new LocalSearchAcceptorConfig();
        acceptorConfig.setAcceptorTypeList(Collections.singletonList(AcceptorType.HILL_CLIMBING));
        return acceptorConfig;
    }

    public static LocalSearchAcceptorConfig createLateAcceptanceAcceptor(Integer lateAcceptanceSize) {
        LocalSearchAcceptorConfig acceptorConfig = new LocalSearchAcceptorConfig();
        acceptorConfig.setLateAcceptanceSize(lateAcceptanceSize);
        return acceptorConfig;
    }

    public static LocalSearchAcceptorConfig createTabuSearchAcceptor(Double entityRatio) {
        LocalSearchAcceptorConfig acceptorConfig = new LocalSearchAcceptorConfig();
        acceptorConfig.setEntityTabuRatio(entityRatio);
        return acceptorConfig;
    }

    public static LocalSearchAcceptorConfig createSimulatedAnnealingAcceptor(String startingTemperature) {
        LocalSearchAcceptorConfig acceptorConfig = new LocalSearchAcceptorConfig();
        acceptorConfig.setSimulatedAnnealingStartingTemperature(startingTemperature);
        return acceptorConfig;
    }

    public static LocalSearchAcceptorConfig createStepCountHillClimbingAcceptor(Integer stepCountHillClimbingSize) {
        LocalSearchAcceptorConfig acceptorConfig = new LocalSearchAcceptorConfig();
        acceptorConfig.setStepCountingHillClimbingSize(stepCountHillClimbingSize);
        return acceptorConfig;
    }

}
