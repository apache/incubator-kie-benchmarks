package org.jboss.qa.brms.performance.localsearch.nurserostering.moveselector;

import java.util.Collections;
import java.util.List;

import org.jboss.qa.brms.performance.examples.nurserostering.NurseRosteringExample;
import org.openjdk.jmh.annotations.Benchmark;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.PillarSwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.SubPillarType;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;

public class NurseRosteringSequentialPillarSwapMoveSelectorBenchmark
        extends AbstractNurseRosteringMoveSelectorBenchmark {

    @Benchmark
    public NurseRoster benchmark() {
        return runBenchmark();
    }

    @Override
    public List<MoveSelectorConfig> createMoveSelectorConfigList() {
        PillarSwapMoveSelectorConfig pillarSwapMoveSelectorConfig = new PillarSwapMoveSelectorConfig();
        pillarSwapMoveSelectorConfig.setSubPillarType(SubPillarType.SEQUENCE);
        pillarSwapMoveSelectorConfig.setPillarSelectorConfig(createPillarSelectorConfig());
        return Collections.singletonList(pillarSwapMoveSelectorConfig);
    }

    // for debugging purposes
    public static void main(String[] args) {
        NurseRosteringSequentialPillarSwapMoveSelectorBenchmark nurseRosteringBenchmark =
                new NurseRosteringSequentialPillarSwapMoveSelectorBenchmark();
        nurseRosteringBenchmark.initSolver();
        nurseRosteringBenchmark.setDataset(NurseRosteringExample.DataSet.SPRINT);
        nurseRosteringBenchmark.initSolution();
        nurseRosteringBenchmark.benchmark();
    }
}
