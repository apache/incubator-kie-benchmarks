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

import org.drools.benchmarks.common.DrlProvider;
import org.drools.benchmarks.common.providers.PartitionedCepRulesProvider;
import org.drools.benchmarks.domain.event.EventA;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.conf.MultithreadEvaluationOption;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Threads;

import java.util.concurrent.atomic.AtomicInteger;

public class OneEventTriggersOneAgendaBenchmark extends AbstractFireUntilHaltThroughputBenchmark {

    private AtomicInteger counter;

    //@Param({"true", "false"})
    private boolean multithread = true;

    //@Param({"4"})
    private int numberOfPartitions = 4;

    private boolean countFirings = true;

    @Setup
    public void setupKieBase() {
        final DrlProvider drlProvider = new PartitionedCepRulesProvider(EventA.class, "==", countFirings);
        createKieBaseFromDrl(
                drlProvider.getDrl(numberOfPartitions),
                EventProcessingOption.STREAM,
                multithread ? MultithreadEvaluationOption.YES : MultithreadEvaluationOption.NO);
    }

    @Setup(Level.Iteration)
    public void setupCounter() {
        counter = new AtomicInteger(0);
        if (countFirings) {
            kieSession.setGlobal( "firings", new AtomicInteger(0) );
        }
    }

    @Benchmark
    @Threads(1)
    public FactHandle insertEvent() {
        final EventA event = new EventA((counter.incrementAndGet() % numberOfPartitions), 40L);
        return kieSession.insert(event);
    }

    public int getFiringsCount() {
        return ( (AtomicInteger) kieSession.getGlobal( "firings" ) ).get();
    }
}
