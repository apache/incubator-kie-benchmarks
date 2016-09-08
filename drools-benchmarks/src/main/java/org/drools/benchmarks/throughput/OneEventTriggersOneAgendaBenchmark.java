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

import java.util.concurrent.atomic.AtomicInteger;
import org.drools.benchmarks.common.DrlProvider;
import org.drools.benchmarks.common.providers.PartitionedCepRulesProvider;
import org.drools.benchmarks.domain.AbstractBean;
import org.kie.api.conf.EventProcessingOption;
import org.kie.internal.conf.MultithreadEvaluationOption;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.infra.Blackhole;

public class OneEventTriggersOneAgendaBenchmark extends AbstractFireUntilHaltThroughputBenchmark {

    //@Param({"true", "false"})
    private boolean multithread = true;

    //@Param({"true", "false"})
    private boolean async = true;

    //@Param({"4"})
    private int numberOfPartitions = 4;

    //@Param({"0", "1", "2", "4"})
    private int numberOfJoins = 0;

    private boolean countFirings = true;

    @Setup
    public void setupKieBase() {
        final DrlProvider drlProvider = new PartitionedCepRulesProvider(numberOfJoins, "==", countFirings);
        System.out.println(drlProvider.getDrl(numberOfPartitions));
        createKieBaseFromDrl(
                drlProvider.getDrl(numberOfPartitions),
                EventProcessingOption.STREAM,
                multithread ? MultithreadEvaluationOption.YES : MultithreadEvaluationOption.NO);
    }

    @Setup(Level.Iteration)
    public void setupCounter() {
        if (countFirings) {
            kieSession.setGlobal( "firings", new AtomicInteger(0) );
        }
    }

    @Benchmark
    @Threads(1)
    public void insertEvent(final Blackhole eater) {
        final long id = AbstractBean.getAndIncrementIdGeneratorValue();
        insertJoinEvents(numberOfJoins, id, (int) (id % numberOfPartitions), async, eater);
    }

    public int getFiringsCount() {
        return ( (AtomicInteger) kieSession.getGlobal( "firings" ) ).get();
    }

    public int getNumberOfPartitions() {
        return numberOfPartitions;
    }

    public int getNumberOfJoins() {
        return numberOfJoins;
    }

    public boolean isAsync() {
        return async;
    }
}
