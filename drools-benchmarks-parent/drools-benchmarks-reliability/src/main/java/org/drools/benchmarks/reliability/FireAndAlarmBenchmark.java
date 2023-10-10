package org.drools.benchmarks.reliability;

import org.drools.benchmarks.common.DRLProvider;
import org.drools.benchmarks.common.providers.RulesWithJoinsProvider;
import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.drools.benchmarks.common.util.RuntimeUtil;
import org.drools.benchmarks.reliability.fireandalarm.Alarm;
import org.drools.benchmarks.reliability.fireandalarm.Fire;
import org.drools.benchmarks.reliability.fireandalarm.Room;
import org.drools.benchmarks.reliability.fireandalarm.Sprinkler;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.conf.ParallelExecutionOption;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.benchmarks.reliability.AbstractReliabilityBenchmark.Mode.NONE;

@Warmup(iterations = 2000)
@Measurement(iterations = 1000)
public class FireAndAlarmBenchmark extends AbstractReliabilityBenchmark{

    public static final String FIRE_AND_ALARM =
            "import " + Alarm.class.getCanonicalName() + ";" +
                    "import " + Fire.class.getCanonicalName() + ";" +
                    "import " + Sprinkler.class.getCanonicalName() + ";" +
                    "import " + Room.class.getCanonicalName() + ";" +
                    "rule 'When there is a fire turn on the sprinkler' when\n" +
                    "  Fire($room : room) \n" +
                    "  $sprinkler: Sprinkler( room == $room, on == false ) \n" +
                    "then\n" +
                    "  modify($sprinkler) { setOn(true); } \n" +
                    "end\n" +
                    "rule 'Raise the alarm when we have one or more firs' when\n" +
                    "  exists Fire() \n" +
                    "then\n" +
                    "  insert( new Alarm() );\n" +
                    "end\n" +
                    "rule 'Cancel the alarm when all the fires have gone' when \n" +
                    "   not Fire() \n" +
                    "   $alarm : Alarm() \n" +
                    "then\n" +
                    "   delete ( $alarm ); \n" +
                    "end\n" +
                    "rule 'Status output when things are ok' when\n" +
                    "   not Alarm() \n" +
                    "   not Sprinkler ( on == true ) \n" +
                    "then \n" +
                    "   System.out.println(\"Everything is ok\"); \n" +
                    "end";

    @Param({"100"})
    private int factsNr;

    @Param({"EMBEDDED"})
    private Mode mode;

    @Param({"true", "false"})
    private boolean useObjectStoreWithReferences;

    @Param({"true", "false"})
    private boolean useSafepoints;

    @Setup
    public void setupKieBase() {
        final DRLProvider drlProvider = new RulesWithJoinsProvider(1, false, true);

        kieBase = BuildtimeUtil.createKieBaseFromDrl(true, FIRE_AND_ALARM,
                ParallelExecutionOption.SEQUENTIAL,
                EventProcessingOption.CLOUD);
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() {
        if (mode != NONE) {
            PersistedSessionOption option = PersistedSessionOption.newSession().withPersistenceStrategy(PersistedSessionOption.PersistenceStrategy.STORES_ONLY);
            if (useSafepoints) {
                option = option.withSafepointStrategy(PersistedSessionOption.SafepointStrategy.AFTER_FIRE);
            }
            if (useObjectStoreWithReferences){
                option = option.withPersistenceObjectsStrategy(PersistedSessionOption.PersistenceObjectsStrategy.OBJECT_REFERENCES);
            }
            kieSession = RuntimeUtil.createKieSession(kieBase, option);
        } else {
            kieSession = RuntimeUtil.createKieSession(kieBase);
        }

        populateKieSessionPerIteration();
    }
    @Override
    protected void populateKieSessionPerIteration() {
        // no-op
    }

    @Benchmark
    public int test() {

        // phase 1
        List<Room> rooms = new ArrayList<Room>();
        List<FactHandle> fireFactHandles = new ArrayList<FactHandle>();
        for (int i = 0; i < factsNr; i++) {
            rooms.add(new Room("room_" + i));
            kieSession.insert(rooms.get(i));
            fireFactHandles.add(kieSession.insert(new Fire(rooms.get(i))));
        }
        kieSession.fireAllRules();

        // phase 2
        Sprinkler sprinkler;
        for (int i = 0; i < factsNr; i++) {
            sprinkler = new Sprinkler(rooms.get(i));
            kieSession.insert(sprinkler);
        }
        kieSession.fireAllRules();

        // phase 3
        for (int i = 0; i < factsNr; i++) {
            kieSession.delete(fireFactHandles.get(i));
        }
        return kieSession.fireAllRules();

    }
}
