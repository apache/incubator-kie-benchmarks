package org.jboss.qa.brms.performance.examples;

import java.io.File;
import org.optaplanner.core.config.solver.SolverConfig;

public abstract class AbstractExample<Solution_> {

    public abstract Solution_ loadSolvingProblem(File file);

    public SolverConfig getSolverConfigFromXml() {
        return SolverConfig.createFromXmlResource(getSolverConfigResource());
    }

    public abstract SolverConfig getBaseSolverConfig();

    protected abstract String getSolverConfigResource();
}
