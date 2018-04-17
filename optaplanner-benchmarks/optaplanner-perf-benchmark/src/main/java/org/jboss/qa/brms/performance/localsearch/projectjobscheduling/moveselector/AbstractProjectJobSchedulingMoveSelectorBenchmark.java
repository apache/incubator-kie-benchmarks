package org.jboss.qa.brms.performance.localsearch.projectjobscheduling.moveselector;

import org.jboss.qa.brms.performance.calculatecounttermination.ProjectJobSchedulingCalculateCountTermination;
import org.jboss.qa.brms.performance.configuration.AcceptorConfigurations;
import org.jboss.qa.brms.performance.localsearch.projectjobscheduling.AbstractProjectJobSchedulingLocalSearchBenchmark;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

public abstract class AbstractProjectJobSchedulingMoveSelectorBenchmark extends AbstractProjectJobSchedulingLocalSearchBenchmark {

    protected ValueSelectorConfig getExecutionModeValueSelectorConfig() {
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig();
        valueSelectorConfig.setVariableName("executionMode");
        return valueSelectorConfig;
    }

    protected ValueSelectorConfig getDelayValueSelectorConfig() {
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig();
        valueSelectorConfig.setVariableName("delay");
        return valueSelectorConfig;
    }

    @Override
    public AcceptorConfig createAcceptorConfig() {
        return AcceptorConfigurations.createSimulatedAnnealingAcceptor("0/0/0");
    }

    @Override
    public int getAcceptedCountLimit() {
        return 1000000;
    }

    @Override
    public TerminationConfig getTerminationConfig() {
        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setTerminationClass(ProjectJobSchedulingCalculateCountTermination.class);
        return terminationConfig;
    }
}
