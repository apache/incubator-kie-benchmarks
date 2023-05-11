package org.jbpm.test.performance.kieserver.constant;

public enum ProcessStorage {

    HumanTask("org.kie.perf.HumanTask"), HumanTaskTimer("org.kie.perf.HumanTaskTimer"), GroupHumanTask("org.kie.perf.GroupHumanTask"), ParallelGatewayTwoTimes(
            "org.kie.perf.ParallelGatewayTwoTimes"), RuleTask("org.kie.perf.RuleTask"), StartEnd("org.kie.perf.StartEnd"), IntermediateTimer(
            "org.kie.perf.IntermediateTimer"), HumanTaskNotification("org.kie.perf.HumanTaskNotification");

    private String processDefinitionId;

    private ProcessStorage(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

}
