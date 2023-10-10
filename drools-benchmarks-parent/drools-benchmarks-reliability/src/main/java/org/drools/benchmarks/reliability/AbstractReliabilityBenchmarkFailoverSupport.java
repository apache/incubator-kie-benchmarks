package org.drools.benchmarks.reliability;

import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.drools.benchmarks.common.util.RuntimeUtil;
import org.drools.reliability.core.ReliableKieSession;
import org.drools.reliability.core.ReliableRuntimeComponentFactoryImpl;
import org.drools.reliability.core.StorageManagerFactory;
import org.drools.reliability.core.TestableStorageManager;
import org.drools.reliability.infinispan.InfinispanStorageManager;
import org.drools.reliability.infinispan.InfinispanStorageManagerFactory;
import org.drools.util.FileUtils;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.server.test.core.InfinispanContainer;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.conf.KieBaseOption;
import org.kie.api.conf.Option;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.KieSessionOption;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.kie.internal.conf.ParallelExecutionOption;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static org.drools.benchmarks.reliability.AbstractReliabilityBenchmark.Mode.EMBEDDED;
import static org.drools.benchmarks.reliability.AbstractReliabilityBenchmark.Mode.NONE;
import static org.drools.benchmarks.reliability.AbstractReliabilityBenchmark.Mode.REMOTE;
import static org.drools.benchmarks.reliability.AbstractReliabilityBenchmark.Mode.REMOTEPROTO;
import static org.drools.reliability.infinispan.EmbeddedStorageManager.GLOBAL_STATE_DIR;
import static org.drools.reliability.infinispan.InfinispanStorageManagerFactory.INFINISPAN_STORAGE_ALLOWED_PACKAGES;
import static org.drools.reliability.infinispan.InfinispanStorageManagerFactory.INFINISPAN_STORAGE_MARSHALLER;
import static org.drools.reliability.infinispan.InfinispanStorageManagerFactory.INFINISPAN_STORAGE_MODE;

public abstract class AbstractReliabilityBenchmarkFailoverSupport extends AbstractReliabilityBenchmark {

    protected Long persistedSessionId;
    protected PersistedSessionOption.PersistenceStrategy persistenceStrategy; // used strategy throughout the benchmark
    protected PersistedSessionOption.SafepointStrategy safepointStrategy;
    protected PersistedSessionOption.PersistenceObjectsStrategy persistenceObjectsStrategy;

    @Setup(Level.Iteration)
    @Override
    public void setup() {
        // overriding setup() because failover scenario requires more complicated iteration setup
    }

    protected abstract void setupKieBase();

    protected abstract void populateKieSessionPerIteration();

    protected KieSession createKieSession(PersistedSessionOption persistedSessionOption) {
        KieSessionConfiguration conf = KieServices.get().newKieSessionConfiguration();
        if (persistedSessionOption != null) {
            conf.setOption(persistedSessionOption);
        }
        KieSession session =  kieBase.newKieSession(conf, null);
        this.persistedSessionId = persistedSessionOption == null || persistedSessionOption.isNewSession() ? session.getIdentifier() : persistedSessionOption.getSessionId();
        return session;
    }

    protected KieSession restoreSession() {
        return createKieSession(PersistedSessionOption.fromSession(persistedSessionId)
                .withPersistenceStrategy(persistenceStrategy)
                .withSafepointStrategy(safepointStrategy)
                .withPersistenceObjectsStrategy(persistenceObjectsStrategy));
    }

    public void failover() {
        // EXPLICIT is not used for now, because if useSafepoints then strategy is always SafepointStrategy.AFTER_FIRE
        if (safepointStrategy == PersistedSessionOption.SafepointStrategy.EXPLICIT) {
            ((ReliableKieSession)kieSession).safepoint();
        }

        if (((TestableStorageManager) StorageManagerFactory.get().getStorageManager()).isRemote()) {
            // fail-over means restarting Drools instance. Assuming remote infinispan keeps alive
            StorageManagerFactory.get().getStorageManager().close(); // close remoteCacheManager
            // Reclaim RemoteCacheManager
            InfinispanStorageManager cacheManager = (InfinispanStorageManager) StorageManagerFactory.get().getStorageManager();
            RemoteCacheManager remoteCacheManager = container.getRemoteCacheManager(cacheManager.provideAdditionalRemoteConfigurationBuilder());
            cacheManager.setRemoteCacheManager(remoteCacheManager);
        } else {
            ((TestableStorageManager) StorageManagerFactory.get().getStorageManager()).restart(); // restart embedded infinispan cacheManager. GlobalState and FireStore are kept
        }
        ReliableRuntimeComponentFactoryImpl.refreshCounterUsingStorage();
    }

}
