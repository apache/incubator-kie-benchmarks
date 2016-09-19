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
import org.drools.benchmarks.common.util.ReteDumper;
import org.drools.benchmarks.domain.AbstractBean;
import org.kie.api.conf.EventProcessingOption;
import org.kie.internal.conf.MultithreadEvaluationOption;
import org.openjdk.jmh.annotations.AuxCounters;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.infra.Blackhole;

public class OneEventTriggersOneAgendaBenchmark extends AbstractFireUntilHaltThroughputBenchmark {

    private static final boolean DUMP_DRL = false;
    private static final boolean DUMP_RETE = false;

    //@Param({"true", "false"})
    private boolean multithread = true;

    //@Param({"true", "false"})
    private boolean async = true;

    //@Param({"4"})
    private int numberOfPartitions = 4;

    //@Param({"0", "1", "2", "4"})
    private int numberOfJoins = 1;

    private boolean countFirings = true;

    private AtomicInteger insertCounter;
    private static AtomicInteger firingCounter;

    @AuxCounters
    @State(Scope.Thread)
    public static class FiringsCounter {
        public int fireCount() {
            return firingCounter.get();
        }
    }

    @Setup
    public void setupKieBase() {
        final DrlProvider drlProvider = new PartitionedCepRulesProvider(numberOfJoins, "==", countFirings);
        String drl = drlProvider.getDrl(numberOfPartitions);
        if (DUMP_DRL) {
            System.out.println( drl );
        }
        createKieBaseFromDrl(
                drl,
                EventProcessingOption.STREAM,
                multithread ? MultithreadEvaluationOption.YES : MultithreadEvaluationOption.NO);
        if (DUMP_RETE) {
            ReteDumper.dumpRete( kieBase );
        }
    }

    @Setup(Level.Iteration)
    public void setupCounter() {
        if (countFirings) {
            insertCounter = new AtomicInteger(0);
            firingCounter = new AtomicInteger(0);
            kieSession.setGlobal( "firings", firingCounter );
        }
    }

    @Benchmark
    @Threads(1)
    public Integer insertEvent(final Blackhole eater, final FiringsCounter resultFirings) {
        if (countFirings) {
            final int insertCount = insertCounter.get();
            if (insertCount % 100 == 99) {
                while ( insertCount > ( getFiringsCount() * 1.1 ) ) {
                    // just wait.
                }
            }
        }

        final long id = AbstractBean.getAndIncrementIdGeneratorValue();
        insertJoinEvents(numberOfJoins, id, (int) (id % numberOfPartitions), async, eater);
        return insertCounter.incrementAndGet();
    }

    public int getFiringsCount() {
        return firingCounter.get();
    }
}
