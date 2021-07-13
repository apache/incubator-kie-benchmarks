package org.jboss.qa.brms.performance.configuration;

import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorConfig;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorType;

import java.util.Collections;

public class AcceptorConfigurations {

    public static AcceptorConfig createHillClimbingAcceptor() {
        AcceptorConfig acceptorConfig = new AcceptorConfig();
        acceptorConfig.setAcceptorTypeList(Collections.singletonList(AcceptorType.HILL_CLIMBING));
        return acceptorConfig;
    }

    public static AcceptorConfig createLateAcceptanceAcceptor(Integer lateAcceptanceSize) {
        AcceptorConfig acceptorConfig = new AcceptorConfig();
        acceptorConfig.setLateAcceptanceSize(lateAcceptanceSize);
        return acceptorConfig;
    }

    public static AcceptorConfig createTabuSearchAcceptor(Double entityRatio) {
        AcceptorConfig acceptorConfig = new AcceptorConfig();
        acceptorConfig.setEntityTabuRatio(entityRatio);
        return acceptorConfig;
    }

    public static AcceptorConfig createSimulatedAnnealingAcceptor(String startingTemperature) {
        AcceptorConfig acceptorConfig = new AcceptorConfig();
        acceptorConfig.setSimulatedAnnealingStartingTemperature(startingTemperature);
        return acceptorConfig;
    }

    public static AcceptorConfig createStepCountHillClimbingAcceptor(Integer stepCountHillClimbingSize) {
        AcceptorConfig acceptorConfig = new AcceptorConfig();
        acceptorConfig.setStepCountingHillClimbingSize(stepCountHillClimbingSize);
        return acceptorConfig;
    }

}
