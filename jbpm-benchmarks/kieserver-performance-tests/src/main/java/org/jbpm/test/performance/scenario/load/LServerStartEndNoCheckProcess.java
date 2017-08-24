package org.jbpm.test.performance.scenario.load;

import org.jbpm.test.performance.kieserver.KieServerClient;
import org.jbpm.test.performance.kieserver.constant.ProcessStorage;
import org.kie.perf.scenario.IPerfTest;
import org.kie.server.client.ProcessServicesClient;

public class LServerStartEndNoCheckProcess implements IPerfTest {

    private KieServerClient client;
    
    private ProcessServicesClient processClient;

    @Override
    public void init() {
    	client = new KieServerClient();
    	processClient = client.getProcessClient();
    }

    @Override
    public void initMetrics() {
        
    }

    @Override
    public void execute() {
        processClient.startProcess(KieServerClient.containerId, ProcessStorage.StartEnd.getProcessDefinitionId());
    }

    @Override
    public void close() {

    }

}
