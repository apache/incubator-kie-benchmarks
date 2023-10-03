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

public abstract class AbstractReliabilityTestsBasics extends AbstractBenchmark {

    protected KieBase kieBase;
    protected final List<KieSession> sessions = new ArrayList<>();
    protected final HashMap<Long,Long> persistedSessionIds = new HashMap<>();

    protected PersistedSessionOption.SafepointStrategy safepointStrategy;
    protected PersistedSessionOption.PersistenceObjectsStrategy persistenceObjectsStrategy;

    // infinispanStorageMode has to match InfinispanStorageManagerFactory.INFINISPAN_STORAGE_MODE
    public enum Mode {
        NONE(null),
        EMBEDDED("EMBEDDED"),
        REMOTE("REMOTE"),
        REMOTEPROTO("REMOTE");

        private String infinispanStorageMode;

        private Mode(String infinispanStorageMode) {
            this.infinispanStorageMode = infinispanStorageMode;
        }

        public String getInfinispanStorageMode() {
            return infinispanStorageMode;
        }
    }

    @Param({"EMBEDDED"})
    private AbstractReliabilityBenchmark.Mode mode;

    @Param({"true", "false"})
    private boolean useSafepoints;

    @Param({"true", "false"})
    private boolean useObjectStoreWithReferences;

    private InfinispanContainer container;

    @Setup
    public void setupEnvironment() {
        FileUtils.deleteDirectory(Path.of(GLOBAL_STATE_DIR));

        if (mode != NONE) {
            System.setProperty(INFINISPAN_STORAGE_MODE, mode.getInfinispanStorageMode());
        }

        if (mode == EMBEDDED || mode == REMOTE) {
            System.setProperty(INFINISPAN_STORAGE_ALLOWED_PACKAGES, "org.drools.benchmarks.common.model");
        }

        if (mode == REMOTEPROTO) {
            System.setProperty(INFINISPAN_STORAGE_MARSHALLER, "PROTOSTREAM");
            setupSerializationContext();
        }

        if (mode == REMOTE || mode == REMOTEPROTO) {
            container = new InfinispanContainer();
            container.start();
            InfinispanStorageManager storageManager = (InfinispanStorageManager) StorageManagerFactory.get().getStorageManager();
            RemoteCacheManager remoteCacheManager = container.getRemoteCacheManager(storageManager.provideAdditionalRemoteConfigurationBuilder());
            storageManager.setRemoteCacheManager(remoteCacheManager);
        }
    }

    protected void setupSerializationContext() {
        // Default to marshall A, B, C and D
        System.setProperty(InfinispanStorageManagerFactory.INFINISPAN_STORAGE_SERIALIZATION_CONTEXT_INITIALIZER,
                "org.drools.benchmarks.reliability.proto.ABCDProtoStreamSchemaImpl");
    }

    @TearDown
    public void tearDownEnvironment() {
        if (mode == REMOTE || mode == REMOTEPROTO) {
            StorageManagerFactory.get().getStorageManager().close();
            container.stop();
        }
    }

    protected abstract void populateKieSessionPerIteration();

    /*
        kiebase
     */
    protected void setupKieBase(String drl) {
        kieBase = BuildtimeUtil.createKieBaseFromDrl(true, drl,
                ParallelExecutionOption.SEQUENTIAL,
                EventProcessingOption.CLOUD);
    }

    /*
       create session
    */
    protected KieSession createSession(String drl, PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy,
                                       PersistedSessionOption.PersistenceObjectsStrategy persistenceObjectsStrategy, Option... options) {
        return getKieSession(drl, persistenceStrategy != null ? PersistedSessionOption.newSession().withPersistenceStrategy(persistenceStrategy)
                .withSafepointStrategy(safepointStrategy).withPersistenceObjectsStrategy(persistenceObjectsStrategy) : null, options);
    }

    //
    protected KieSession getKieSession(String drl, PersistedSessionOption persistedSessionOption, Option... options) {
        OptionsFilter optionsFilter = new OptionsFilter(options);
        setupKieBase(drl);
        return getKieSessionFromKieBase(kieBase, persistedSessionOption, optionsFilter);
    }

    //
    protected KieSession getKieSessionFromKieBase(KieBase kbase, PersistedSessionOption persistedSessionOption, OptionsFilter optionsFilter) {
        KieSessionConfiguration conf = KieServices.get().newKieSessionConfiguration();
        if (persistedSessionOption != null) {
            conf.setOption(persistedSessionOption);
            //safepointStrategy = persistedSessionOption.getSafepointStrategy();
        }
        Stream.of(optionsFilter.getKieSessionOption()).forEach(conf::setOption);
        KieSession session =  RuntimeUtil.createKieSession(kieBase, optionsFilter.getKieSessionOption());
        sessions.add(session);
        persistedSessionIds.put(session.getIdentifier(),persistedSessionOption == null || persistedSessionOption.isNewSession() ? session.getIdentifier() : persistedSessionOption.getSessionId());
        return session;
    }

    /*
        failover
     */

    public void failover() {
        // do we need lines 169-171, if useSafepoints then strategy is always PersistedSessionOption.SafepointStrategy.AFTER_FIRE
        if (safepointStrategy == PersistedSessionOption.SafepointStrategy.EXPLICIT) {
            this.sessions.stream().map(ReliableKieSession.class::cast).forEach(ReliableKieSession::safepoint);
        }
        sessions.clear();

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

    /*
        restore seesion
    */
    protected KieSession restoreSession(String drl, PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy,
                                        PersistedSessionOption.PersistenceObjectsStrategy persistenceObjectsStrategy, Option... options) {
        Long sessionIdToRestoreFrom = (Long) this.persistedSessionIds.values().toArray()[0];
        return restoreSession(sessionIdToRestoreFrom, drl, persistenceStrategy, safepointStrategy, persistenceObjectsStrategy, options);
    }

    protected KieSession restoreSession(Long sessionId, String drl, PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy,
                                        PersistedSessionOption.PersistenceObjectsStrategy persistenceObjectsStrategy, Option... options) {
        Long sessionIdToRestoreFrom = this.persistedSessionIds.get(sessionId);
        return getKieSession(drl, PersistedSessionOption.fromSession(sessionIdToRestoreFrom).withPersistenceStrategy(persistenceStrategy)
                .withSafepointStrategy(safepointStrategy).withPersistenceObjectsStrategy(persistenceObjectsStrategy), options);
    }

    /*
        other
     */
    private static class OptionsFilter {
        private final Option[] options;

        OptionsFilter(Option[] options) {
            this.options = options;
        }

        KieBaseOption[] getKieBaseOptions() {
            return options == null ? new KieBaseOption[0] : Stream.of(options).filter(KieBaseOption.class::isInstance).toArray(KieBaseOption[]::new);
        }

        KieSessionOption[] getKieSessionOption() {
            return options == null ? new KieSessionOption[0] : Stream.of(options).filter(KieSessionOption.class::isInstance).toArray(KieSessionOption[]::new);
        }
    }
}
