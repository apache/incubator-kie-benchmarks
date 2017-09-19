package org.jbpm.test.performance.kieserver;

import java.util.Properties;

import org.kie.perf.TestConfig;
import org.kie.server.client.KieServicesClient;

public class KieServerTestConfig extends TestConfig {

    protected String clientVersion = KieServicesClient.class.getPackage().getImplementationVersion();

    protected String username;

    protected String password;

    protected String host;

    protected int port;

    protected String name;

    protected String remoteAPI;

    protected KieServerTestConfig() {

    }

    public static KieServerTestConfig getInstance() {
        if (tc == null || !(tc instanceof KieServerTestConfig)) {
            tc = new KieServerTestConfig();
            try {
                tc.loadProperties();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return (KieServerTestConfig) tc;
    }

    @Override
    public Properties loadProperties() throws Exception {
        super.loadProperties();

        username = System.getProperty("kieserver.username");
        properties.put("kieserver.username", username);

        password = System.getProperty("kieserver.password");
        properties.put("kieserver.password", password);

        host = System.getProperty("kieserver.host");
        properties.put("kieserver.host", host);

        port = Integer.valueOf(System.getProperty("kieserver.port"));
        properties.put("kieserver.port", port);

        name = System.getProperty("kieserver.name");
        properties.put("kieserver.name", name);

        remoteAPI = System.getProperty("remoteAPI");
        properties.put("remoteAPI", remoteAPI);
        addTag(remoteAPI);

        properties.put("client.version", clientVersion);
        addTag(clientVersion);

        return properties;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getRemoteAPI() {
        return remoteAPI;
    }

    public void setRemoteAPI(String remoteAPI) {
        this.remoteAPI = remoteAPI;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApplicationUrl() {
        return "http://" + host + ":" + port + "/" + name + "/services/rest/server";
    }

}
