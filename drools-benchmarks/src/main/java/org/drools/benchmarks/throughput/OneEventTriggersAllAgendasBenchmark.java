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
import org.drools.core.time.SessionPseudoClock;
import org.kie.api.conf.EventProcessingOption;
import org.kie.internal.conf.MultithreadEvaluationOption;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

public class OneEventTriggersAllAgendasBenchmark extends AbstractFireUntilHaltThroughputBenchmark {

    private static final long EVENT_EXPIRATION_BASE_MS = 10;
    private static final boolean DUMP_DRL = false;
    private static final boolean DUMP_RETE = false;

    @Param({"true", "false"})
    private boolean multithread = false;

    @Param({"false"})
    private boolean async = false;

    @Param({"8"})
    private int numberOfRules = 8;

    @Param({"1"})
    private int numberOfJoins = 1;

    @Param({"2.0"})
    private double insertRatio = 2.0;

    @Param({"10"})
    private int numberOfJoinedEvents = 2;

    @Param({"true", "false"})
    private boolean eventsExpiration = true;

    @Param({"1", "2", "3"})
    private int eventsExpirationRatio = 2;

    private long firingsPerInsert;
    private long missingFiringsOnFirstEvents;

    @Setup
    @Override
    public void setupKieBase() {
        final long eventExpirationMs = eventsExpiration ?
                EVENT_EXPIRATION_BASE_MS * Math.max(1, numberOfJoinedEvents) * eventsExpirationRatio :
                -1L;

        final DrlProvider drlProvider =
                new PartitionedCepRulesProvider(numberOfJoins, numberOfJoinedEvents, eventExpirationMs, i -> "value > " + i, true);
        String drl = drlProvider.getDrl(numberOfRules);
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
        if ( ((InternalKnowledgeBase)kieBase).getConfiguration().isMultithreadEvaluation() != multithread) {
            throw new IllegalStateException();
        }

        long firingsPerRule = (long)Math.pow( Math.max(1, numberOfJoinedEvents), Math.max(1, numberOfJoins) );
        firingsPerInsert = numberOfRules * firingsPerRule;
        if (numberOfJoinedEvents > 1 && numberOfJoins > 0) {
            for (int i = 1; i <= numberOfJoinedEvents; i++) {
                missingFiringsOnFirstEvents += (firingsPerRule - (long)Math.pow( i, numberOfJoins ));
            }
            missingFiringsOnFirstEvents *= numberOfRules;
        }
    }

    @Benchmark
    @Override
    public void insertEvent(final Blackhole eater, final FiringsCounter resultFirings) {
        final long insertCount = insertCounter.longValue();
        if (insertCount % 100 == 99) {
            final long expectedFirings = (insertCount * firingsPerInsert) - missingFiringsOnFirstEvents;
            for (int i = 0; i < 10000; i++) {
                if (expectedFirings > ( firingCounter.longValue() * insertRatio )) {
                    Blackhole.consumeCPU(1024);
                } else {
                    break;
                }
            }
        }

        final long id = AbstractBean.getAndIncrementIdGeneratorValue();
        insertJoinEvents(numberOfJoins, id, (int) id, async, eater);
        insertCounter.add(1);
        if (eventsExpiration && pseudoClock) {
            ((SessionPseudoClock) kieSession.getSessionClock()).advanceTime(EVENT_EXPIRATION_BASE_MS, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void setupCounter() {
        super.setupCounter();
        // Sets the id generator to correct value so we can use the ids to fire rules. Rules have constraints (value > id)
        AbstractBean.setIdGeneratorValue(numberOfRules + 1);
    }

    public long getFiringsCount() {
        return firingCounter.longValue();
    }
}
