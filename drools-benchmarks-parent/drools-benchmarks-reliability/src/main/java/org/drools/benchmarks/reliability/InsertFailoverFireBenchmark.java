package org.drools.benchmarks.reliability;

import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.drools.benchmarks.common.util.RuntimeUtil;
import org.drools.benchmarks.reliability.fireandalarm.Fire;
import org.drools.benchmarks.reliability.fireandalarm.Room;
import org.drools.benchmarks.reliability.fireandalarm.Sprinkler;
import org.drools.reliability.core.ReliableRuntimeComponentFactoryImpl;
import org.drools.reliability.core.StorageManagerFactory;
import org.drools.reliability.core.TestableStorageManager;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.kie.internal.conf.ParallelExecutionOption;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

import java.util.ArrayList;
import java.util.List;

import static org.drools.benchmarks.reliability.AbstractReliabilityBenchmark.Mode.REMOTE;
import static org.drools.benchmarks.reliability.AbstractReliabilityBenchmark.Mode.REMOTEPROTO;
import static org.drools.benchmarks.reliability.FireAndAlarmBenchmark.FIRE_AND_ALARM;

@Warmup(iterations = 0)
@Measurement(iterations = 1)
public class InsertFailoverFireBenchmark extends AbstractReliabilityBenchmarkFailoverSupport {

    @Param({"100"})
    private int factsNr;

    @Param({"EMBEDDED"})
    private Mode mode;

    @Param({"true", "false"})
    private boolean useObjectStoreWithReferences;

    @Param({"true", "false"})
    private boolean useSafepoints;

    @Setup
    @Override
    public void setupKieBase() {
        kieBase = BuildtimeUtil.createKieBaseFromDrl(true, FIRE_AND_ALARM,
                ParallelExecutionOption.SEQUENTIAL,
                EventProcessingOption.CLOUD);
    }


    @Setup(Level.Iteration)
    public void setupAndFailover() {
        System.out.println("setupAndFailover!!");
        if (mode != Mode.NONE) {
            persistenceStrategy = PersistedSessionOption.PersistenceStrategy.STORES_ONLY;
            safepointStrategy = useSafepoints ? PersistedSessionOption.SafepointStrategy.AFTER_FIRE : PersistedSessionOption.SafepointStrategy.ALWAYS;
            persistenceObjectsStrategy = useObjectStoreWithReferences ? PersistedSessionOption.PersistenceObjectsStrategy.OBJECT_REFERENCES : PersistedSessionOption.PersistenceObjectsStrategy.SIMPLE;

            // These 3 strategies will not change during the benchmark
            PersistedSessionOption persistedSessionOption = PersistedSessionOption.newSession()
                    .withPersistenceStrategy(persistenceStrategy)
                    .withSafepointStrategy(safepointStrategy)
                    .withPersistenceObjectsStrategy(persistenceObjectsStrategy);
            kieSession = createKieSession(persistedSessionOption);
        } else {
            kieSession = RuntimeUtil.createKieSession(kieBase);
        }
        populateKieSessionPerIteration();

        kieSession.fireAllRules();

        // failover
        failover();

        // recreate kieBase
        setupKieBase();
    }

    @Override
    protected void populateKieSessionPerIteration() {
        List<Room> rooms = new ArrayList<Room>();
        for (int i = 0; i < factsNr; i++) {
            rooms.add(new Room("room_" + i));
            kieSession.insert(rooms.get(i));
            kieSession.insert(new Fire(rooms.get(i)));
            kieSession.insert(new Sprinkler(rooms.get(i)));
        }
    }

    @Benchmark
    public long test() {
        kieSession = restoreSession();
        System.out.println("restored : facts size = " + kieSession.getFactHandles().size());
        return kieSession.getIdentifier();
    }

    public static void main(String[] args) {
        InsertFailoverFireBenchmark benchmark = new InsertFailoverFireBenchmark();
        benchmark.factsNr = 10;
        benchmark.mode = Mode.EMBEDDED;
        benchmark.useObjectStoreWithReferences = true;
        benchmark.useSafepoints = true;

        benchmark.setupKieBase();
        benchmark.setupAndFailover();
        benchmark.test();
        benchmark.tearDown();

        benchmark.setupKieBase();
        benchmark.setupAndFailover();
        benchmark.test();
        benchmark.tearDown();

    }
}
