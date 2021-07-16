package org.jboss.qa.brms.performance.configuration;

import org.optaplanner.core.config.localsearch.decider.acceptor.LocalSearchAcceptorConfig;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorType;

import java.util.Collections;

public class AcceptorConfigurations {

    public static LocalSearchAcceptorConfig createHillClimbingAcceptor() {
        LocalSearchAcceptorConfig localSearchAcceptorConfig = new LocalSearchAcceptorConfig();
        localSearchAcceptorConfig.setAcceptorTypeList(Collections.singletonList(AcceptorType.HILL_CLIMBING));
        return localSearchAcceptorConfig;
    }

    public static LocalSearchAcceptorConfig createLateAcceptanceAcceptor(Integer lateAcceptanceSize) {
        LocalSearchAcceptorConfig localSearchAcceptorConfig = new LocalSearchAcceptorConfig();
        localSearchAcceptorConfig.setLateAcceptanceSize(lateAcceptanceSize);
        return localSearchAcceptorConfig;
    }

    public static LocalSearchAcceptorConfig createTabuSearchAcceptor(Double entityRatio) {
        LocalSearchAcceptorConfig localSearchAcceptorConfig = new LocalSearchAcceptorConfig();
        localSearchAcceptorConfig.setEntityTabuRatio(entityRatio);
        return localSearchAcceptorConfig;
    }

    public static LocalSearchAcceptorConfig createSimulatedAnnealingAcceptor(String startingTemperature) {
        LocalSearchAcceptorConfig localSearchAcceptorConfig = new LocalSearchAcceptorConfig();
        localSearchAcceptorConfig.setSimulatedAnnealingStartingTemperature(startingTemperature);
        return localSearchAcceptorConfig;
    }

    public static LocalSearchAcceptorConfig createStepCountHillClimbingAcceptor(Integer stepCountHillClimbingSize) {
        LocalSearchAcceptorConfig localSearchAcceptorConfig = new LocalSearchAcceptorConfig();
        localSearchAcceptorConfig.setStepCountingHillClimbingSize(stepCountHillClimbingSize);
        return localSearchAcceptorConfig;
    }

}
