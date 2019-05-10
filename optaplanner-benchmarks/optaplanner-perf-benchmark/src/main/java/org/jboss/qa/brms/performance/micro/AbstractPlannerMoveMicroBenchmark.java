package org.jboss.qa.brms.performance.micro;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.score.director.ScoreDirector;

@State(Scope.Thread)
@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public abstract class AbstractPlannerMoveMicroBenchmark<Solution> {

    private Move<Solution> move;
    private ScoreDirector<Solution> scoreDirector;

    public ScoreDirector<Solution> getScoreDirector() {
        return scoreDirector;
    }

    public void setScoreDirector(ScoreDirector<Solution> scoreDirector) {
        this.scoreDirector = scoreDirector;
    }

    public Move<Solution> getMove() {
        return move;
    }

    public void setMove(Move<Solution> move) {
        this.move = move;
    }

    @Setup
    public abstract void initScoreDirector();

    @Setup
    public abstract void initMove();

    public Move<Solution> benchmarkDoMove() {
        return getMove().doMove(getScoreDirector());
    }

    public Move<Solution> benchmarkRebase() {
        return getMove().rebase(getScoreDirector());
    }
}
