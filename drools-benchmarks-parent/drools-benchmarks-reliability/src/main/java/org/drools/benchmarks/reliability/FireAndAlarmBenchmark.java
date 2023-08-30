package org.drools.benchmarks.reliability;

import org.drools.benchmarks.common.DRLProvider;
import org.drools.benchmarks.common.providers.RulesWithJoinsProvider;
import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.drools.benchmarks.reliability.fireandalarm.Alarm;
import org.drools.benchmarks.reliability.fireandalarm.Fire;
import org.drools.benchmarks.reliability.fireandalarm.Room;
import org.drools.benchmarks.reliability.fireandalarm.Sprinkler;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.kie.api.conf.EventProcessingOption;
import org.kie.internal.conf.ParallelExecutionOption;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;

@Warmup(iterations = 2000)
@Measurement(iterations = 1000)
public class FireAndAlarmBenchmark extends AbstractReliabilityBenchmark{

    @Param({"100"})
    private int factsNr;

    @Setup
    public void setupKieBase() {
        final DRLProvider drlProvider = new RulesWithJoinsProvider(1, false, true);

        kieBase = BuildtimeUtil.createKieBaseFromDrl(true, drlProvider.getDrl(), //FIRE_AND_ALARM
                ParallelExecutionOption.SEQUENTIAL,
                EventProcessingOption.CLOUD);
    }

    @Override
    protected void populateKieSessionPerIteration() {
        StatefulKnowledgeSessionImpl session = (StatefulKnowledgeSessionImpl) kieSession;

        for (int i = 0; i < factsNr; i++) {
            Room room = new Room("room_" + i);
            session.insert(room);
            session.insert(new Fire(room));
            session.insert(new Sprinkler(room));
            session.insert(new Alarm());
        }
    }

    @Benchmark
    public int test() {
        return kieSession.fireAllRules();
    }
}
