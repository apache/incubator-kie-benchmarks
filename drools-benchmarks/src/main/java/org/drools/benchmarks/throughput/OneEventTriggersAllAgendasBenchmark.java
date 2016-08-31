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
import org.drools.benchmarks.common.ProviderException;
import org.drools.benchmarks.common.providers.PartitionedCepRulesProvider;
import org.drools.benchmarks.domain.event.EventA;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.conf.MultithreadEvaluationOption;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Threads;

public class OneEventTriggersAllAgendasBenchmark extends AbstractFireUntilHaltThroughputBenchmark {

    @Param({"true", "false"})
    private boolean multithread;

    @Param({"4"})
    private int numberOfPartitions;

    @Setup
    public void setupKieBase() throws ProviderException {
        final DrlProvider drlProvider = new PartitionedCepRulesProvider(EventA.class, ">");
        createKieBaseFromDrl(
                drlProvider.getDrl(numberOfPartitions),
                EventProcessingOption.STREAM,
                multithread ? MultithreadEvaluationOption.YES : MultithreadEvaluationOption.NO);
    }

    @Benchmark
    @Threads(4)
    public FactHandle insertEvent() {
        final EventA event = new EventA(numberOfPartitions + 1, 40L);
        final FactHandle factHandle = kieSession.insert(event);
        return factHandle;
    }
}
