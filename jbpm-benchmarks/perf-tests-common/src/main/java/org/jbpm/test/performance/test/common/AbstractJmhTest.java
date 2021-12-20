package org.jbpm.test.performance.test.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.openjdk.jmh.annotations.TearDown;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class AbstractJmhTest {
    static final Logger log = LoggerFactory.getLogger(AbstractJmhTest.class);

    @TearDown
    public void cleandb() throws MojoExecutionException, MavenInvocationException, IOException {
        if (!Objects.equals(getProperties().getProperty("databaseName"), "H2")){
            log.info("DB Cleaning");
            PerfdbClean.cleandb();
        }
    }

    private Properties getProperties() throws IOException, RuntimeException {
        String DATASOURCE_PROPERTIES = "/datasource.properties";
        InputStream propsInputStream = AbstractJmhTest.class.getResourceAsStream(DATASOURCE_PROPERTIES);
        if (propsInputStream == null) {
            throw new RuntimeException("Unable to load datasource properties [" + DATASOURCE_PROPERTIES + "]");
        }
        Properties props = new Properties();
        try {
            props.load(propsInputStream);
        } catch (IOException ioe) {
            log.error("Unable to load properties");
            log.error("Stacktrace:", ioe);
            throw ioe;
        }
        return props;
    }
}
