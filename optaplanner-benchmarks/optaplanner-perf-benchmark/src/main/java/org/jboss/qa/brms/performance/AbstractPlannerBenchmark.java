package org.jboss.qa.brms.performance;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.optaplanner.core.api.solver.Solver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.SingleShotTime)
@Fork(value = 1)
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public abstract class AbstractPlannerBenchmark<Solution_> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractPlannerBenchmark.class.getName());

    private Solution_ solution;
    private Solver<Solution_> solver;

    public Solution_ getSolution() {
        return solution;
    }

    public void setSolution(Solution_ solution) {
        this.solution = solution;
    }

    public Solver<Solution_> getSolver() {
        return solver;
    }

    public void setSolver(Solver<Solution_> solver) {
        this.solver = solver;
    }

    @Setup
    public abstract void initSolution();

    @Setup
    public abstract void initSolver();

    public void benchmark() {
        getSolver().solve(getSolution());
    }

}
