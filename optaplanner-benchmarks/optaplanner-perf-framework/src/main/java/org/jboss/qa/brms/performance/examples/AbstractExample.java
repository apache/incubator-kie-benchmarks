package org.jboss.qa.brms.performance.examples;

import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;

import java.io.File;
import java.io.InputStream;

public abstract class AbstractExample {

    public abstract Solution loadSolvingProblem(File f);

    public abstract SolverConfig getDefaultConfig();

    public abstract SolverConfig getBaseConfig();

    @Deprecated
    protected SolverConfig getDefaultConfig(String resource) {
        InputStream configFileStream = getClass().getResourceAsStream(resource);
        assert configFileStream != null;
        SolverFactory solverFactory = SolverFactory.createFromXmlInputStream(configFileStream);
        return solverFactory.getSolverConfig();
    }
}
