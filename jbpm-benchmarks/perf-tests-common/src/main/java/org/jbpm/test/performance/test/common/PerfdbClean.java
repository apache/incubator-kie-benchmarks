package org.jbpm.test.performance.test.common;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;

public abstract class PerfdbClean {

    public static void cleandb() throws MavenInvocationException, MojoExecutionException {
        InvocationRequest request = getInvocationRequestForDbClean();
        Invoker invoker = new DefaultInvoker();
        invoker.setWorkingDirectory(new File(System.getProperty("user.dir")));
        invoker.setMavenHome(new File(System.getProperty("mvn.home")));
        invoker.getLogger().setThreshold(4);
        InvocationResult result = invoker.execute(request);
        if (result.getExitCode() != 0) {
            throw new MojoExecutionException("Error during request invocation. See previous errors in log.", result.getExecutionException());
        }
    }

    private static InvocationRequest getInvocationRequestForDbClean() {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setUserSettingsFile(new File(System.getenv("MVN_SETTINGS")));
        Properties properties = new Properties();
        properties.setProperty("perfdb", "true");
        request.setProperties(properties);
        request.setGoals(Arrays.asList("process-resources"));
        return request;
    }
}
