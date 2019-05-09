package org.jboss.qa.brms.performance.micro.changemove;

import java.util.Collections;

import org.jboss.qa.brms.performance.examples.cloudbalancing.domain.CloudBalance;
import org.jboss.qa.brms.performance.examples.cloudbalancing.domain.CloudComputer;
import org.jboss.qa.brms.performance.examples.cloudbalancing.domain.CloudProcess;
import org.jboss.qa.brms.performance.examples.cloudbalancing.solver.score.CloudBalancingEasyScoreCalculator;
import org.jboss.qa.brms.performance.micro.AbstractPlannerMoveMicroBenchmark;
import org.openjdk.jmh.annotations.Benchmark;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.score.director.easy.EasyScoreDirector;
import org.optaplanner.core.impl.score.director.easy.EasyScoreDirectorFactory;

public class ChangeMoveBenchmark extends AbstractPlannerMoveMicroBenchmark<CloudBalance> {

    private CloudComputer cloudComputer = getCloudComputer();
    private CloudProcess cloudProcess = getCloudProcess();
    private SolutionDescriptor<CloudBalance> solutionDescriptor = SolutionDescriptor.
            buildSolutionDescriptor(CloudBalance.class, CloudProcess.class);

    private static CloudComputer getCloudComputer() {
        CloudComputer cloudComputer = new CloudComputer();
        cloudComputer.setId(1L);
        cloudComputer.setCost(200);
        cloudComputer.setCpuPower(2000);
        cloudComputer.setNetworkBandwidth(200);
        cloudComputer.setMemory(200);
        return cloudComputer;
    }

    private static CloudProcess getCloudProcess() {
        CloudProcess cloudProcess = new CloudProcess();
        cloudProcess.setId(1L);
        cloudProcess.setRequiredCpuPower(3000);
        cloudProcess.setRequiredMemory(300);
        cloudProcess.setRequiredNetworkBandwidth(300);
        return cloudProcess;
    }

    @Override
    public void initMove() {
        EntityDescriptor<CloudBalance> entityDescriptor = solutionDescriptor.findEntityDescriptorOrFail(CloudProcess.class);
        GenuineVariableDescriptor<CloudBalance> genuineVariableDescriptor = entityDescriptor.getGenuineVariableDescriptor("computer");
        ChangeMove<CloudBalance> cloudBalanceChangeMove = new ChangeMove<>(cloudProcess, genuineVariableDescriptor, cloudComputer);
        super.setMove(cloudBalanceChangeMove);
    }

    @Override
    public void initScoreDirector() {
        CloudBalancingEasyScoreCalculator cloudBalancingEasyScoreCalculator = new CloudBalancingEasyScoreCalculator();
        EasyScoreDirector<CloudBalance> cloudBalanceScoreDirector = new EasyScoreDirector<>
                (new EasyScoreDirectorFactory<>(solutionDescriptor,
                                                cloudBalancingEasyScoreCalculator),
                 true,
                 true,
                 cloudBalancingEasyScoreCalculator);
        CloudBalance workingSolution = new CloudBalance();
        workingSolution.setComputerList(Collections.singletonList(cloudComputer));
        workingSolution.setProcessList(Collections.singletonList(cloudProcess));
        cloudBalanceScoreDirector.setWorkingSolution(workingSolution);
        super.setScoreDirector(cloudBalanceScoreDirector);
    }

    @Benchmark
    public Move<CloudBalance> benchmarkDoMove() {
        return super.benchmarkDoMove();
    }

    @Benchmark
    public Move<CloudBalance> benchmarkRebase() {
        return super.benchmarkRebase();
    }
}
