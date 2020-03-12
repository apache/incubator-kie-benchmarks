package org.jbpm.test.performance.jbpm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TestConfig {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected static TestConfig tc;

    protected Properties properties;

    protected String projectName;
    protected String suite;
    protected String scenario;
    protected String startScriptLocation;

    protected String databaseName;

    protected int duration;
    protected int iterations;
    protected int expectedRate;

    protected ReporterType reporterType;
    protected int periodicity;
    protected String reportDataLocation;

    protected int threads;

    protected boolean warmUp;
    protected int warmUpCount;
    protected int warmUpTime;

    protected String perfRepoHost;
    protected String perfRepoUrlPath;
    protected String perfRepoUsername;
    protected String perfRepoPassword;


    protected List<Measure> measure;
    protected List<String> tags = new ArrayList<String>();

    protected TestConfig() {

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

//        duration = Integer.valueOf(System.getProperty("duration"));
//        iterations = Integer.valueOf(System.getProperty("iterations"));


//        properties.put("duration", duration);
//        properties.put("iterations", iterations);


//        threads = Integer.valueOf(System.getProperty("threads"));
//        properties.put("threads", threads);
//        if (suite.equals(ConcurrentLoadSuite.class.getSimpleName())) {
//            addTag("thread-" + threads);
//        }



        perfRepoHost = System.getProperty("perfRepo.host");
        if (perfRepoHost != null) {
            properties.put("perfRepo.host", perfRepoHost);
        }
        perfRepoUrlPath = System.getProperty("perfRepo.urlPath");
        if (perfRepoUrlPath != null) {
            properties.put("perfRepo.urlPath", perfRepoUrlPath);
        }
        perfRepoUsername = System.getProperty("perfRepo.username");
        if (perfRepoUsername != null) {
            properties.put("perfRepo.username", perfRepoUsername);
        }
        perfRepoPassword = System.getProperty("perfRepo.password");
        if (perfRepoPassword != null) {
            properties.put("perfRepo.password", perfRepoPassword);
        }

        return properties;
    }

    public static TestConfig getInstance() {
        if (tc == null) {
            tc = new TestConfig();
            try {
                tc.loadProperties();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return tc;
    }

    protected void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }

    public Properties getProperties() {
        return properties;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getSuite() {
        return suite;
    }

    public String getScenario() {
        return scenario;
    }

    public String getStartScriptLocation() {
        return startScriptLocation;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public int getDuration() {
        return duration;
    }

    public int getIterations() {
        return iterations;
    }

    public int getExpectedRate() {
        return expectedRate;
    }

    public ReporterType getReporterType() {
        return reporterType;
    }

    public int getPeriodicity() {
        return periodicity;
    }

    public String getReportDataLocation() {
        return reportDataLocation;
    }

    public int getThreads() {
        return threads;
    }

    public boolean isWarmUp() {
        return warmUp;
    }

    public int getWarmUpCount() {
        return warmUpCount;
    }

    public int getWarmUpTime() {
        return warmUpTime;
    }

    public List<Measure> getMeasure() {
        return measure;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getPerfRepoHost() {
        return perfRepoHost;
    }

    public String getPerfRepoUrlPath() {
        return perfRepoUrlPath;
    }

    public String getPerfRepoUsername() {
        return perfRepoUsername;
    }

    public String getPerfRepoPassword() {
        return perfRepoPassword;
    }

    public static enum ReporterType {
        CONSOLE, CSV, CSVSINGLE, PERFREPO
    }

    public static enum Measure {
        MEMORYUSAGE, FILEDESCRIPTORS, THREADSTATES, CPUUSAGE
    }

}