package org.jboss.qa.brms.performance.localsearch.nurserostering.moveselector;

import java.util.Collections;
import java.util.List;

import org.jboss.qa.brms.performance.examples.nurserostering.NurseRostering;
import org.jboss.qa.brms.performance.examples.nurserostering.domain.NurseRoster;
import org.jboss.qa.brms.performance.examples.nurserostering.domain.ShiftAssignmentComparator;
import org.openjdk.jmh.annotations.Benchmark;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.PillarSwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.SubPillarType;

public class NurseRosteringSequentialPillarSwapMoveSelectorBenchmark
        extends AbstractNurseRosteringMoveSelectorBenchmark {

    @Benchmark
    @Override
    public NurseRoster benchmark() {
        return super.benchmark();
    }

    @Override
    public List<MoveSelectorConfig> createMoveSelectorConfigList() {
        PillarSwapMoveSelectorConfig pillarSwapMoveSelectorConfig = new PillarSwapMoveSelectorConfig();
        pillarSwapMoveSelectorConfig.setSubPillarType(SubPillarType.SEQUENCE);
        pillarSwapMoveSelectorConfig.setSubPillarSequenceComparatorClass(ShiftAssignmentComparator.class);
        pillarSwapMoveSelectorConfig.setPillarSelectorConfig(createPillarSelectorConfig());
        return Collections.singletonList(pillarSwapMoveSelectorConfig);
    }

    // for debugging purposes
    public static void main(String[] args) {
        NurseRosteringSequentialPillarSwapMoveSelectorBenchmark nurseRosteringBenchmark =
                new NurseRosteringSequentialPillarSwapMoveSelectorBenchmark();
        nurseRosteringBenchmark.initSolver();
        nurseRosteringBenchmark.setDataset(NurseRostering.DataSet.SPRINT);
        nurseRosteringBenchmark.initSolution();
        nurseRosteringBenchmark.benchmark();
    }
}
