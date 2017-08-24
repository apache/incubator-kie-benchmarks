package org.jbpm.test.performance.kieserver.constant;

public enum ProcessStorage {

    HumanTask("org.kie.perf.HumanTask"), GroupHumanTask("org.kie.perf.GroupHumanTask"), ParallelGatewayTwoTimes(
            "org.kie.perf.ParallelGatewayTwoTimes"), RuleTask("org.kie.perf.RuleTask"), StartEnd("org.kie.perf.StartEnd"), IntermediateTimer(
            "org.kie.perf.IntermediateTimer");

    private String processDefinitionId;

    private ProcessStorage(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

}
