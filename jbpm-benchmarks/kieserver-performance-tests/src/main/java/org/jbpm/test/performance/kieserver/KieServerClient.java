package org.jbpm.test.performance.kieserver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.api.model.ServiceResponse.ResponseType;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;
import org.kie.server.client.UserTaskServicesClient;

public class KieServerClient {
	
	public static String containerId = "kieserver-assets";
	
    private static ReleaseId releaseId = new ReleaseId("org.jbpm.test.performance", "kieserver-assets",
            "7.3.0-SNAPSHOT");

    private ProcessServicesClient processClient;
    private UserTaskServicesClient taskClient;
    private QueryServicesClient queryClient;
    
    public KieServerClient(Class<?>... classes) {
    	this(KieServerTestConfig.getInstance().getUsername(), KieServerTestConfig.getInstance().getPassword(), classes);
    }
    
    public KieServerClient(String username, String password, Class<?>... classes) {
    	init(username, password, classes);
    }
    
    public ProcessServicesClient getProcessClient() {
		return processClient;
	}
    
    public UserTaskServicesClient getTaskClient() {
		return taskClient;
	}
    
    public QueryServicesClient getQueryClient() {
		return queryClient;
	}
    
    private void init(String username, String password, Class<?>... classes) {
    	KieServerTestConfig config = KieServerTestConfig.getInstance();
    	KieServicesConfiguration kconfig = null;
    	if (KieServerTestConfig.getInstance().getRemoteAPI().equals("JMS")) {
    		kconfig = KieServicesFactory.newJMSConfiguration(getInitialRemoteContext(), username, password);
    	} else {
    		kconfig = KieServicesFactory.newRestConfiguration(config.getApplicationUrl(), username, password);
    	}
    	if (classes != null) {
    		Set<Class<?>> jaxbClasses = new HashSet<Class<?>>(Arrays.asList(classes));
    		kconfig.addExtraClasses(jaxbClasses);
    	}
    	KieServicesClient client = KieServicesFactory.newKieServicesClient(kconfig);
    	// create container if not exist
    	ServiceResponse<KieContainerResource> container = client.getContainerInfo(containerId);
    	if (container.getType() == ResponseType.FAILURE) {
    		container = client.createContainer(containerId, new KieContainerResource(containerId, releaseId));
    		if (container.getType() == ResponseType.FAILURE) {
    			throw new RuntimeException(container.getMsg());
    		}
    	}
    	this.processClient = client.getServicesClient(ProcessServicesClient.class);
        this.taskClient = client.getServicesClient(UserTaskServicesClient.class);
        this.queryClient = client.getServicesClient(QueryServicesClient.class);
    }
    
    private static InitialContext getInitialRemoteContext() {
        InitialContext context = null;
        try {
            final Properties env = new Properties();
        	KieServerTestConfig config = KieServerTestConfig.getInstance();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
            env.put(Context.PROVIDER_URL, String.format("http-remoting://%s:%s", config.getHost(), config.getPort()));
            env.put(Context.SECURITY_PRINCIPAL, config.getUsername());
            env.put(Context.SECURITY_CREDENTIALS, config.getPassword());
            context = new InitialContext(env);
        } catch (NamingException e) {
            throw new RuntimeException("Failed to create initial context!", e);
        }
        return context;
    }
    
}
