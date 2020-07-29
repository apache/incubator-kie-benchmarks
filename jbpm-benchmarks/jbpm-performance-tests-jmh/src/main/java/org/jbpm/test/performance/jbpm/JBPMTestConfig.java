package org.jbpm.test.performance.jbpm;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class JBPMTestConfig {

    protected String runtimeManagerStrategy;
    protected static JBPMTestConfig tc;
    protected boolean persistence;
    protected Properties properties;
    protected boolean pessimisticLocking;
    protected boolean humanTaskEager;
    protected String projectName;
    protected String startScriptLocation;
    protected String databaseName;

    protected JBPMTestConfig() {

    }

    public static JBPMTestConfig getInstance() {
        if (tc == null) {
            tc = new JBPMTestConfig();
            try {
                tc.loadProperties();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return tc;
    }

    public Properties loadProperties() throws Exception {
        properties = new Properties();

        projectName = System.getProperty("projectName");
        if (projectName == null || projectName.isEmpty()) {
            projectName = "Project";
        }

        startScriptLocation = System.getProperty("startScriptLocation");
        if (startScriptLocation == null) {
            startScriptLocation = "./run.sh";
        }
        properties.put("startScriptLocation", startScriptLocation);

        databaseName = JBPMController.getDatasourceProperties().getProperty("databaseName");
        properties.put("databaseName", databaseName);

        runtimeManagerStrategy = System.getProperty("jbpm.runtimeManagerStrategy");
        properties.put("jbpm.runtimeManagerStrategy", runtimeManagerStrategy);

        persistence = Boolean.parseBoolean(System.getProperty("jbpm.persistence"));
        properties.put("jbpm.persistence", persistence);

        String locking = System.getProperty("jbpm.locking");
        System.out.println(locking);
        pessimisticLocking = locking.toLowerCase().equals("pessimistic");
        properties.put("jbpm.pessimisticLocking", pessimisticLocking);

        humanTaskEager = Boolean.parseBoolean(System.getProperty("jbpm.ht.eager"));
        properties.put("jbpm.ht.eager", humanTaskEager);

        return properties;
    }

    public String getRuntimeManagerStrategy() {
        return runtimeManagerStrategy;
    }

    public boolean isPersistence() {
        return persistence;
    }

    public boolean isPessimisticLocking() {
        return pessimisticLocking;
    }

    public boolean isHumanTaskEager() {
        return humanTaskEager;
    }
}
