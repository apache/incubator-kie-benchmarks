package org.jbpm.test.performance.scenario.load;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.jbpm.test.performance.jbpm.util.JbpmJmhPerformanceUtil.readProperties;

public abstract class AbstractPerformanceBaseTest {

    private static final Logger log = LoggerFactory.getLogger(AbstractPerformanceBaseTest.class);

    private static final String JHM_CONFIG_FILE_LOCATION = "classpath:/jmh-config.properties";

    protected static int processes;
    protected static int threads;
    protected static boolean failOnError;
    protected static boolean doGc;
    protected static int measurementIterations;
    protected static int forks;
    protected static List<String> jvmArgsAppend;
    protected static int timeout;
    protected static int warmupTime;
    protected static int iterationTime;

    protected static final String JMH_PREFIX = "jmh";
    protected static final String JMH_JBPM_PREFIX = ".jbpm";
    protected static final String JMH_JBPM_PROCESSES = JMH_PREFIX + JMH_JBPM_PREFIX + ".processes";
    protected static final String JMH_THREADS = JMH_PREFIX + ".threads";
    protected static final String JMH_FOE = JMH_PREFIX + ".failOnError";
    protected static final String JMH_GC = JMH_PREFIX + ".doGc";
    protected static final String JMH_MEASUREMENT_ITERATIONS = JMH_PREFIX + ".measurementIterations";
    protected static final String JMH_FORKS = JMH_PREFIX + ".forks";
    protected static final String JMH_JVM_ARGS_APPEND = JMH_PREFIX + ".jvmArgsAppend";
    protected static final String JMH_TIMEOUT = JMH_PREFIX + ".timeout";
    protected static final String JMH_WARMUP_TIME = JMH_PREFIX + ".warmupTime";
    protected static final String JMH_ITERATION_TIME = JMH_PREFIX + ".iterationTime";


    @BeforeClass
    public static void setup() {
        Properties props = readProperties(JHM_CONFIG_FILE_LOCATION);
        processes = Integer.parseInt(props.getProperty(JMH_JBPM_PROCESSES, "1"));
        threads = Integer.parseInt(props.getProperty(JMH_THREADS, "1"));
        failOnError = Boolean.parseBoolean(props.getProperty(JMH_FOE, "true"));
        doGc = Boolean.parseBoolean(props.getProperty(JMH_GC, "true"));
        measurementIterations = Integer.parseInt(props.getProperty(JMH_MEASUREMENT_ITERATIONS, "1"));
        forks = Integer.parseInt(props.getProperty(JMH_FORKS,"1"));
        jvmArgsAppend = new ArrayList<>(Arrays.asList(props.getProperty(JMH_JVM_ARGS_APPEND, "").split(" ")));
        jvmArgsAppend.add("-Djava.io.tmpdir="+System.getProperty("java.io.tmpdir")); // temp folder might be different from one jvm to another (specially when going through Jenkins)
        jvmArgsAppend.stream()
                .filter(s -> s.contains("="))
                .map(s -> s.split("="))
                .forEach(s -> System.setProperty(s[0].replace("-D", ""), s[1]));
        timeout = Integer.parseInt(props.getProperty(JMH_TIMEOUT, "30"));
        warmupTime = Integer.parseInt(props.getProperty(JMH_WARMUP_TIME, "30"));
        iterationTime = Integer.parseInt(props.getProperty(JMH_ITERATION_TIME, "300"));
    }

    @AfterClass
    public static void tearDown() {
        jvmArgsAppend.stream()
                .filter(s -> s.contains("="))
                .map(s -> s.split("="))
                .forEach(s -> System.clearProperty(s[0].replace("-D", "")));
    }
}
