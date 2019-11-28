package org.jboss.qa.brms.performance.localsearch.nurserostering.moveselector;

import java.util.Collections;
import java.util.List;

import org.jboss.qa.brms.performance.examples.nurserostering.NurseRostering;
import org.openjdk.jmh.annotations.Benchmark;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.PillarChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.SubPillarType;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;

public class NurseRosteringSequentialPillarChangeMoveSelectorBenchmark
        extends AbstractNurseRosteringMoveSelectorBenchmark {

    @Benchmark
    @Override
    public NurseRoster benchmark() {
        return super.benchmark();
    }

    @Override
    public List<MoveSelectorConfig> createMoveSelectorConfigList() {
        PillarChangeMoveSelectorConfig pillarChangeMoveSelectorConfig = new PillarChangeMoveSelectorConfig();
        pillarChangeMoveSelectorConfig.setSubPillarType(SubPillarType.SEQUENCE);
        pillarChangeMoveSelectorConfig.setPillarSelectorConfig(createPillarSelectorConfig());
        return Collections.singletonList(pillarChangeMoveSelectorConfig);
    }

    // for debugging purposes
    public static void main(String[] args) {
        NurseRosteringSequentialPillarChangeMoveSelectorBenchmark nurseRosteringBenchmark =
                new NurseRosteringSequentialPillarChangeMoveSelectorBenchmark();
        nurseRosteringBenchmark.initSolver();
        nurseRosteringBenchmark.setDataset(NurseRostering.DataSet.SPRINT);
        nurseRosteringBenchmark.initSolution();
        nurseRosteringBenchmark.benchmark();
    }
}

