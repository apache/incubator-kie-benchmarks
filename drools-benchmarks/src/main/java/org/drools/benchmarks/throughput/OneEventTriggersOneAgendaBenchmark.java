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
import org.drools.benchmarks.common.util.ReteDumper;
import org.drools.benchmarks.domain.AbstractBean;
import org.drools.core.impl.InternalKnowledgeBase;
import org.kie.api.conf.EventProcessingOption;
import org.kie.internal.conf.MultithreadEvaluationOption;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.infra.Blackhole;

public class OneEventTriggersOneAgendaBenchmark extends AbstractFireUntilHaltThroughputBenchmark {

    private static final boolean DUMP_DRL = false;
    private static final boolean DUMP_RETE = false;

    @Param({"true", "false"})
    private boolean multithread;

    //@Param({"true", "false"})
    private boolean async = true;

    @Param({"true", "false"})
    private boolean hashed;

    @Param({"4", "8"})
    private int numberOfPartitions;

    @Param({"0", "1", "2", "4"})
    private int numberOfJoins;

    private boolean countFirings = true;

    @Setup
    public void setupKieBase() {
        final DrlProvider drlProvider = new PartitionedCepRulesProvider(numberOfJoins,
                                                                        hashed ?
                                                                            i -> "value == " + i :
                                                                            i -> "boxedValue.equals(" + i + ")",
                                                                        countFirings);
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
        if (((InternalKnowledgeBase)kieBase).getConfiguration().isMultithreadEvaluation() != multithread) {
            throw new IllegalStateException();
        }
    }

    @Benchmark
    public void insertEvent(final Blackhole eater, final FiringsCounter resultFirings) {
        if (countFirings) {
            final long insertCount = insertCounter.longValue();
            if (insertCount % 100 == 99) {
                while ( insertCount > ( getFiringsCount() * 1.1 ) ) {
                    // just wait.
                }
            }
        }

        final long id = AbstractBean.getAndIncrementIdGeneratorValue();
        insertJoinEvents(numberOfJoins, id, (int) (id % numberOfPartitions), async, eater);
        insertCounter.add(1);
    }
}