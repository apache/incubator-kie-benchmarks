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
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;

public abstract class AbstractEventTriggersAgendasFireUntilHaltBenchmark extends AbstractEventTriggersAgendaThroughputBenchmark {

    @Param({"1.1"})
    private double insertRatio = 1.1;

    private ExecutorService executor;

    private long firingsPerInsert;
    private long missingFiringsOnFirstEvents;

    protected abstract long getExpectedFiringsPerInsert();

    protected abstract long getMissingFiringsOnFirstEvents();

    @Setup
    public void initVariables() {
        firingsPerInsert = getExpectedFiringsPerInsert();
        missingFiringsOnFirstEvents = getMissingFiringsOnFirstEvents();
    }

    @Setup(Level.Iteration)
    public void prepareBenchmark() {
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

    protected boolean canInsertEvent() {
        final long expectedFirings = (insertCounter.longValue() * firingsPerInsert) - missingFiringsOnFirstEvents;
        return (expectedFirings <= (firingCounter.longValue() * insertRatio));
    }
}
