/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.drools.benchmarks.reliability;

import java.nio.file.Path;

import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.benchmarks.common.util.RuntimeUtil;
import org.drools.reliability.core.ReliableGlobalResolverFactory;
import org.drools.reliability.core.SimpleReliableObjectStoreFactory;
import org.drools.reliability.core.StorageManagerFactory;
import org.drools.reliability.h2mvstore.H2MVStoreStorageManager;
import org.drools.reliability.infinispan.InfinispanStorageManager;
import org.drools.reliability.infinispan.InfinispanStorageManagerFactory;
import org.drools.util.FileUtils;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.server.test.core.InfinispanContainer;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;

import static org.drools.benchmarks.reliability.AbstractReliabilityBenchmark.Mode.INFINISPAN_EMBEDDED;
import static org.drools.benchmarks.reliability.AbstractReliabilityBenchmark.Mode.NONE;
import static org.drools.benchmarks.reliability.AbstractReliabilityBenchmark.Mode.INFINISPAN_REMOTE;
import static org.drools.benchmarks.reliability.AbstractReliabilityBenchmark.Mode.INFINISPAN_REMOTEPROTO;
import static org.drools.benchmarks.reliability.AbstractReliabilityBenchmark.Mode.H2MVSTORE;
import static org.drools.reliability.infinispan.EmbeddedStorageManager.GLOBAL_STATE_DIR;
import static org.drools.reliability.infinispan.InfinispanStorageManagerFactory.INFINISPAN_STORAGE_ALLOWED_PACKAGES;
import static org.drools.reliability.infinispan.InfinispanStorageManagerFactory.INFINISPAN_STORAGE_MARSHALLER;
import static org.drools.reliability.infinispan.InfinispanStorageManagerFactory.INFINISPAN_STORAGE_MODE;
import static org.drools.util.Config.getConfig;

public abstract class AbstractReliabilityBenchmark extends AbstractBenchmark {

    // infinispanStorageMode has to match InfinispanStorageManagerFactory.INFINISPAN_STORAGE_MODE
    public enum Mode {
        NONE(null),
        INFINISPAN_EMBEDDED("EMBEDDED"),
        INFINISPAN_REMOTE("REMOTE"),
        INFINISPAN_REMOTEPROTO("REMOTE"),
        H2MVSTORE("H2MVSTORE");

        private String infinispanStorageMode;

        private Mode(String infinispanStorageMode) {
            this.infinispanStorageMode = infinispanStorageMode;
        }

        public String getInfinispanStorageMode() {
            return infinispanStorageMode;
        }
    }

    public enum Module {
        INFINISPAN,
        H2MVSTORE
    }

    public static final String DROOLS_RELIABILITY_MODULE_TEST = "drools.reliability.module.test";

    @Param({"NONE", "INFINISPAN_EMBEDDED", "INFINISPAN_REMOTE", "INFINISPAN_REMOTEPROTO", "H2MVSTORE"})
    protected Mode mode;

    @Param({"true", "false"})
    protected boolean useSafepoints;

    protected InfinispanContainer container;

    @Setup
    public void setupEnvironment() {
        FileUtils.deleteDirectory(Path.of(GLOBAL_STATE_DIR));

        if (mode == H2MVSTORE){
            H2MVStoreStorageManager.cleanUpDatabase();
            System.setProperty(DROOLS_RELIABILITY_MODULE_TEST, "H2MVSTORE");
            configureServicePriorities();
        }else {
            System.setProperty(DROOLS_RELIABILITY_MODULE_TEST, "INFINISPAN");
            if (mode != NONE) {
                System.setProperty(INFINISPAN_STORAGE_MODE, mode.getInfinispanStorageMode());
            }

            if (mode == INFINISPAN_EMBEDDED || mode == INFINISPAN_REMOTE) {
                System.setProperty(INFINISPAN_STORAGE_ALLOWED_PACKAGES, "org.drools.benchmarks.common.model");
            }

            if (mode == INFINISPAN_REMOTEPROTO) {
                System.setProperty(INFINISPAN_STORAGE_MARSHALLER, "PROTOSTREAM");
                setupSerializationContext();
            }

            configureServicePriorities();

            if (mode == INFINISPAN_REMOTE || mode == INFINISPAN_REMOTEPROTO) {
                container = new InfinispanContainer();
                container.start();
                InfinispanStorageManager storageManager = (InfinispanStorageManager) StorageManagerFactory.get().getStorageManager();
                RemoteCacheManager remoteCacheManager = container.getRemoteCacheManager(storageManager.provideAdditionalRemoteConfigurationBuilder());
                storageManager.setRemoteCacheManager(remoteCacheManager);
            }
        }
    }

    protected void setupSerializationContext() {
        // Default to marshall A, B, C and D
        System.setProperty(InfinispanStorageManagerFactory.INFINISPAN_STORAGE_SERIALIZATION_CONTEXT_INITIALIZER,
                           "org.drools.benchmarks.reliability.proto.ABCDProtoStreamSchemaImpl");
    }

    @TearDown
    public void tearDownEnvironment() {
        if (mode == Mode.INFINISPAN_REMOTE || mode == Mode.INFINISPAN_REMOTEPROTO) {
            StorageManagerFactory.get().getStorageManager().close();
            container.stop();
        }
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() {
        if (mode != NONE) {
            PersistedSessionOption option = PersistedSessionOption.newSession().withPersistenceStrategy(PersistedSessionOption.PersistenceStrategy.STORES_ONLY);
            if (useSafepoints) {
                option = option.withSafepointStrategy(PersistedSessionOption.SafepointStrategy.AFTER_FIRE);
            }
            kieSession = RuntimeUtil.createKieSession(kieBase, option);
        } else {
            kieSession = RuntimeUtil.createKieSession(kieBase);
        }

        populateKieSessionPerIteration();
    }

    protected abstract void populateKieSessionPerIteration();


    public static void configureServicePriorities() {
        Module module = Module.valueOf(getConfig(DROOLS_RELIABILITY_MODULE_TEST, Module.INFINISPAN.name()));
        if (module == Module.INFINISPAN) {
            prioritizeInfinispanServices();
        } else if (module == Module.H2MVSTORE) {
            prioritizeH2MVStoreServices();
        } else {
            throw new IllegalStateException("Unknown module: " + module);
        }
    }

    private static void prioritizeInfinispanServices() {
        ReliableGlobalResolverFactory.get("infinispan");
        SimpleReliableObjectStoreFactory.get("infinispan");
        StorageManagerFactory.get("infinispan");
    }

    private static void prioritizeH2MVStoreServices() {
        ReliableGlobalResolverFactory.get("core");
        SimpleReliableObjectStoreFactory.get("core");
        StorageManagerFactory.get("h2mvstore");
    }
}