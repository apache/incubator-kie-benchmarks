package org.drools.benchmarks.reliability;

import org.drools.benchmarks.common.util.RuntimeUtil;
import org.drools.benchmarks.reliability.fireandalarm.Alarm;
import org.drools.benchmarks.reliability.fireandalarm.Fire;
import org.drools.benchmarks.reliability.fireandalarm.Room;
import org.drools.benchmarks.reliability.fireandalarm.Sprinkler;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;

import java.util.ArrayList;
import java.util.List;

@Warmup(iterations = 0)
@Measurement(iterations = 1)
public class InsertFailoverFireBenchmark extends AbstractReliabilityTestsBasics{

    private static final String FIRE_AND_ALARM =
            "import " + Alarm.class.getCanonicalName() + ";" +
                    "import " + Fire.class.getCanonicalName() + ";" +
                    "import " + Sprinkler.class.getCanonicalName() + ";" +
                    "import " + Room.class.getCanonicalName() + ";" +
                    "global java.util.List results;" +
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
        setupKieBase(FIRE_AND_ALARM);
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() {

        if (mode != mode.NONE) {
            PersistedSessionOption option = PersistedSessionOption.newSession().withPersistenceStrategy(PersistedSessionOption.PersistenceStrategy.STORES_ONLY);
            if (useSafepoints) {
                option = option.withSafepointStrategy(PersistedSessionOption.SafepointStrategy.AFTER_FIRE);
                safepointStrategy = PersistedSessionOption.SafepointStrategy.AFTER_FIRE;
            }
            if (useObjectStoreWithReferences){
                option = option.withPersistenceObjectsStrategy(PersistedSessionOption.PersistenceObjectsStrategy.OBJECT_REFERENCES);
            }
            //kieSession = RuntimeUtil.createKieSession(kieBase, option);
            kieSession = createSession(FIRE_AND_ALARM, option.getPersistenceStrategy(), option.getSafepointStrategy(), PersistedSessionOption.PersistenceObjectsStrategy.OBJECT_REFERENCES);

        } else {
            kieSession = RuntimeUtil.createKieSession(kieBase);
            this.sessions.add(kieSession); // not sure if we need this
            this.persistedSessionIds.put(kieSession.getIdentifier(),kieSession.getIdentifier());
        }

        /*
            insert facts
         */
        List<Room> rooms = new ArrayList<Room>();
        for (int i = 0; i < factsNr; i++) {
            rooms.add(new Room("room_" + i));
            kieSession.insert(rooms.get(i));
            kieSession.insert(new Fire(rooms.get(i)));
            kieSession.insert(new Sprinkler(rooms.get(i)));
        }

        // failover
        failover();

        populateKieSessionPerIteration();
    }

    @Override
    protected void populateKieSessionPerIteration() {
    }

    @Benchmark
    public int test() {
        kieSession = restoreSession(FIRE_AND_ALARM, PersistedSessionOption.PersistenceStrategy.STORES_ONLY,
                safepointStrategy, persistenceObjectsStrategy);
        return -1;
    }
}
