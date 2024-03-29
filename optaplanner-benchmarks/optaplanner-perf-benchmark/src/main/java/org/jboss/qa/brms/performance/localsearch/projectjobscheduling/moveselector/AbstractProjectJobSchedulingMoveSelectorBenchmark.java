package org.jboss.qa.brms.performance.localsearch.projectjobscheduling.moveselector;

import org.jboss.qa.brms.performance.examples.projectjobscheduling.termination.ProjectJobSchedulingCalculateCountTermination;
import org.jboss.qa.brms.performance.configuration.AcceptorConfigurations;
import org.jboss.qa.brms.performance.localsearch.projectjobscheduling.AbstractProjectJobSchedulingLocalSearchBenchmark;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.localsearch.decider.acceptor.LocalSearchAcceptorConfig;
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
    public LocalSearchAcceptorConfig createAcceptorConfig() {
        return AcceptorConfigurations.createSimulatedAnnealingAcceptor("0hard/0medium/0soft");
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
