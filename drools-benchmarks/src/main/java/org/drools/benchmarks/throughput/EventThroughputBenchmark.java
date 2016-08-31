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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.drools.benchmarks.common.ProviderException;
import org.drools.benchmarks.domain.EventA;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.rule.FactHandle;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Threads;

@State(Scope.Benchmark)
public class EventThroughputBenchmark extends AbstractThroughputBenchmark {

    private ExecutorService executor;

    @Setup
    public void setupKieBase() throws ProviderException {
        final String drl = " import " + EventA.class.getCanonicalName() + ";\n" +
                " declare " + EventA.class.getName() + " @role( event ) @duration(duration) end\n" +
                " rule R1 \n" +
                " when \n" +
                "     EventA()\n" +
                " then \n" +
                " end";

        createKieBaseFromDrl(drl, EventProcessingOption.STREAM);
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() throws ProviderException {
        createKieSession();
        executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> kieSession.fireUntilHalt());
    }

    @TearDown(Level.Iteration)
    public void stopFireUntilHaltThread() throws InterruptedException, ExecutionException {
        executor.shutdown();
        if (!executor.awaitTermination(10, TimeUnit.MILLISECONDS)) {
            executor.shutdownNow();
        }
    }

    @Benchmark
    @Threads(Threads.MAX)
    public FactHandle insertEvent() {
        final EventA event = new EventA();
        event.setDuration(40);
        final FactHandle factHandle = kieSession.insert(event);
        return factHandle;
    }
}
