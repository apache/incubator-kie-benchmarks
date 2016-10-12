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
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@Fork(1)
public class OneEventTriggersAllAgendasBenchmark extends AbstractFireUntilHaltThroughputBenchmark {

    private static final long EVENT_EXPIRATION_BASE_MS = 10;
    private static final boolean DUMP_DRL = false;
    private static final boolean DUMP_RETE = false;

    private static final boolean LOG_FIRINGS = false;
    private static final FireLogger LOGGER = LOG_FIRINGS ? new FireLogger() : null;

    @Param({"true", "false"})
    private boolean multithread = false;

    @Param({"false"})
    private boolean async = false;

    @Param({"8"})
    private int numberOfRules = 8;

//    @Param({"1", "2", "4"})
    @Param({"1"})
    private int numberOfJoins = 1;

    @Param({"1.1"})
    private double insertRatio = 1.1;

//    @Param({"1", "2", "4", "8"})
    @Param({"8"})
    private int numberOfJoinedEvents = 8;

    @Param({"true"})
    private boolean eventsExpiration = true;

    @Param({"2"})
    private int eventsExpirationRatio = 2;

    private long firingsPerInsert;
    private long missingFiringsOnFirstEvents;

    private int maxWait;

    @Setup
    @Override
    public void setupKieBase() {
        final long eventExpirationMs = eventsExpiration ?
                EVENT_EXPIRATION_BASE_MS * Math.max(1, numberOfJoinedEvents) * eventsExpirationRatio :
                -1L;

        final DrlProvider drlProvider =
                new PartitionedCepRulesProvider(numberOfJoins,
                                                numberOfJoinedEvents,
                                                eventExpirationMs,
                                                i -> "value > " + i,
                                                true,
                                                LOG_FIRINGS);
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

        maxWait = 1000;

        if (LOG_FIRINGS) {
            kieSession.setGlobal( "logger", LOGGER );
        }
    }

    @Override
    public void stopFireUntilHaltThread() {
        super.stopFireUntilHaltThread();
        LOGGER.flush();
    }

    public long getFiringsCount() {
        return firingCounter.longValue();
    }
}
