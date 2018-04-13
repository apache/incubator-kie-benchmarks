package org.jboss.qa.brms.performance.examples;

import org.optaplanner.core.api.solver.SolverFactory;

import java.io.File;
import java.io.InputStream;

public abstract class AbstractExample<S> {

    public abstract S loadSolvingProblem(File f);

    public abstract SolverFactory<S> getDefaultSolverFactory();

    public abstract SolverFactory<S> getBaseSolverFactory();

    @Deprecated
    protected SolverFactory<S> getSolverFactory(String resource) {
        InputStream configFileStream = getClass().getResourceAsStream(resource);
        assert configFileStream != null;
        return SolverFactory.createFromXmlInputStream(configFileStream);
    }
}
