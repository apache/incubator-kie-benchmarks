package org.jbpm.test.performance.kieserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class KieServerTestConfig {
    protected static KieServerTestConfig tc;

    protected Properties properties;

    protected List<String> tags = new ArrayList<String>();

    protected String username;
    protected String password;
    protected String host;
    protected int port;
    protected String name;
    protected String remoteAPI;
    protected String dataSourceJndi;

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
        return tc;
    }

    public Properties loadProperties() {
        properties = new Properties();

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

    public String getApplicationUrl() {
        return "http://" + host + ":" + port + "/" + name + "/services/rest/server";
    }

    public String getDataSourceJndi() {
        return dataSourceJndi;
    }

    protected void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }
}
