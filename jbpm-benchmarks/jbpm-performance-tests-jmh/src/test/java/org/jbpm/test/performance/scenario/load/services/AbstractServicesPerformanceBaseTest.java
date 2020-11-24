package org.jbpm.test.performance.scenario.load.services;

import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.test.performance.jbpm.JBPMKieServicesController;
import org.jbpm.test.performance.scenario.load.AbstractPerformanceBaseTest;
import org.kie.internal.task.api.InternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class AbstractServicesPerformanceBaseTest extends AbstractPerformanceBaseTest {

    private static final Logger log = LoggerFactory.getLogger(AbstractServicesPerformanceBaseTest.class);

    protected static JBPMKieServicesController jc;
    protected static ProcessService processService;
    protected static RuntimeDataService runtimeDataService;
    protected static UserTaskService userTaskService;
    protected static DefinitionService definitionService;
    protected static DeploymentUnit deploymentUnit;
    protected static InternalTaskService internalTaskService;
    protected static String puName = "org.jbpm.domain";

    protected static void initServices(List<String> processesDefinition) throws Exception {
        jc = JBPMKieServicesController.getInstance(processesDefinition, getPuName());
        processService = jc.getProcessService();
        runtimeDataService = jc.getRuntimeDataService();
        userTaskService = jc.getUserTaskService();
        definitionService = jc.getDefinitionService();
        deploymentUnit = jc.getDeploymentUnit();
        internalTaskService = jc.getInternalTaskService();
    }

    protected static String getPuName() {
        return puName;
    }
}
