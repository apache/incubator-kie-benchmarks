package org.jbpm.test.performance.jbpm.services;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.kie.services.impl.KModuleDeploymentService;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.model.DeployedUnit;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.internal.runtime.conf.MergeMode;

public class CustomKModuleDeploymentService extends KModuleDeploymentService {

    protected Map<String, Object> customEnvironmentEntries = new HashMap<>();

    public void addEnvironmentEntry(String name, Object value) {
        customEnvironmentEntries.put(name, value);
    }

    @Override
    protected RuntimeEnvironmentBuilder boostrapRuntimeEnvironmentBuilder(KModuleDeploymentUnit deploymentUnit,
                                                                          DeployedUnit deployedUnit, KieContainer kieContainer, MergeMode mode) {
        RuntimeEnvironmentBuilder builder = super.boostrapRuntimeEnvironmentBuilder(deploymentUnit, deployedUnit, kieContainer, mode);
        for (Map.Entry<String, Object> envEntry : customEnvironmentEntries.entrySet()) {
            builder.addEnvironmentEntry(envEntry.getKey(), envEntry.getValue());
        }
        return builder;
    }

}
