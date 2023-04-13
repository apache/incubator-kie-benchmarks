/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.benchmarks.reliability;

import java.nio.file.Path;

import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.benchmarks.common.util.RuntimeUtil;
import org.drools.reliability.core.CacheManagerFactory;
import org.drools.reliability.infinispan.EmbeddedCacheManager;
import org.drools.reliability.infinispan.InfinispanCacheManager;
import org.drools.util.FileUtils;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.server.test.core.InfinispanContainer;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;

import static org.drools.benchmarks.reliability.AbstractReliabilityBenchmark.Mode.NONE;
import static org.drools.benchmarks.reliability.AbstractReliabilityBenchmark.Mode.REMOTE;

public abstract class AbstractReliabilityBenchmark extends AbstractBenchmark {

    // The enum names have to match CacheManagerFactory.RELIABILITY_CACHE_MODE values apart from "NONE"
    public enum Mode {
        NONE,
        EMBEDDED,
        REMOTE
    }

    @Param({"NONE", "EMBEDDED", "REMOTE"})
    private Mode mode;

    private InfinispanContainer container;

    @Setup
    public void setupEnvironment() {
        if (mode != NONE) {
            FileUtils.deleteDirectory(Path.of(EmbeddedCacheManager.GLOBAL_STATE_DIR));
            System.setProperty(CacheManagerFactory.RELIABILITY_CACHE_MODE, mode.name());
            System.setProperty(CacheManagerFactory.RELIABILITY_CACHE_ALLOWED_PACKAGES, "org.drools.benchmarks.common.model");
        }

        if (mode == REMOTE) {
            container = new InfinispanContainer();
            container.start();
            InfinispanCacheManager cacheManager = (InfinispanCacheManager) CacheManagerFactory.get().getCacheManager();
            RemoteCacheManager remoteCacheManager = container.getRemoteCacheManager(cacheManager.provideAdditionalRemoteConfigurationBuilder());
            cacheManager.setRemoteCacheManager(remoteCacheManager);
        }
    }

    @TearDown
    public void tearDownEnvironment() {
        if (mode == REMOTE) {
            CacheManagerFactory.get().getCacheManager().close();
            container.stop();
        }
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() {
        if (mode != NONE) {
            kieSession = RuntimeUtil.createKieSession(kieBase, PersistedSessionOption.newSession(PersistedSessionOption.Strategy.STORES_ONLY));
        } else {
            kieSession = RuntimeUtil.createKieSession(kieBase);
        }

        populateKieSessionPerIteration();
    }

    protected abstract void populateKieSessionPerIteration();
}