/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.benchmarks.throughput;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.drools.benchmarks.common.DrlProvider;
import org.drools.benchmarks.common.providers.PartitionedCepRulesProvider;
import org.drools.benchmarks.domain.AbstractBean;
import org.drools.core.time.SessionPseudoClock;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

public abstract class AbstractEventTriggersAgendasFireUntilHaltBenchmark extends AbstractEventTriggersAgendaThroughputBenchmark {

    @Param({"1.1"})
    private double insertRatio = 1.1;

    private ExecutorService executor;

    private long firingsPerInsert;
    private long missingFiringsOnFirstEvents;
    private int maxWait;

    protected abstract long getExpectedFiringsPerInsert();
    protected abstract long getMissingFiringsOnFirstEvents();

    @Setup
    public void initVariables() {
        firingsPerInsert = getExpectedFiringsPerInsert();
        missingFiringsOnFirstEvents = getMissingFiringsOnFirstEvents();
    }

    @Setup(Level.Iteration)
    public void prepareBenchmark() {
        maxWait = 1000;

        executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> kieSession.fireUntilHalt());
    }

    @TearDown(Level.Iteration)
    public void stopFireUntilHaltThread() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void waitIfNeeded() {
        final long insertCount = insertCounter.longValue();
        final long expectedFirings = (insertCount * firingsPerInsert) - missingFiringsOnFirstEvents;

        int i = 0;
        while (i < maxWait) {
            if (expectedFirings > ( firingCounter.longValue() * insertRatio )) {
                Blackhole.consumeCPU(1024);
                i++;
            } else {
                break;
            }
        }

        if (i == maxWait && maxWait < 1_000_000_000) {
            maxWait *= 2;
        }
    }
}
