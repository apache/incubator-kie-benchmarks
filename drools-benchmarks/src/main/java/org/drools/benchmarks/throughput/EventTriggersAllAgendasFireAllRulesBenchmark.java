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

import java.util.concurrent.TimeUnit;
import org.drools.benchmarks.common.DrlProvider;
import org.drools.benchmarks.common.providers.PartitionedCepRulesProvider;
import org.drools.benchmarks.domain.AbstractBean;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.time.SessionPseudoClock;
import org.kie.api.conf.EventProcessingOption;
import org.kie.internal.conf.MultithreadEvaluationOption;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

public class EventTriggersAllAgendasFireAllRulesBenchmark extends AbstractEventTriggersAgendaThroughputBenchmark {

    private static final long EVENT_EXPIRATION_BASE_MS = 10;
    private static final boolean LOG_FIRINGS = false;
    private static final FireLogger LOGGER = LOG_FIRINGS ? new FireLogger() : null;

    @Param({"true", "false"})
    private boolean multithread = true;

    @Param({"false"})
    private boolean async = false;

    @Param({"8"})
    private int numberOfRules = 8;

    @Param({"1", "2", "4"})
    private int numberOfJoins = 1;

    @Param({"1", "2", "4", "8"})
    private int numberOfJoinedEvents = 1;

    @Param({"1"})
    private int eventsExpirationRatio = 1;

    @Setup
    @Override
    public void setupKieBase() {
        final long eventExpirationMs = EVENT_EXPIRATION_BASE_MS * Math.max(1, numberOfJoinedEvents) * eventsExpirationRatio;

        final DrlProvider drlProvider =
                new PartitionedCepRulesProvider(numberOfJoins,
                        numberOfJoinedEvents,
                        eventExpirationMs,
                        i -> "value > " + i,
                        true,
                        LOG_FIRINGS);
        final String drl = drlProvider.getDrl(numberOfRules);

        createKieBaseFromDrl(
                drl,
                EventProcessingOption.STREAM,
                multithread ? MultithreadEvaluationOption.YES : MultithreadEvaluationOption.NO);

        if (((InternalKnowledgeBase) kieBase).getConfiguration().isMultithreadEvaluation() != multithread) {
            throw new IllegalStateException();
        }
    }

    @Benchmark
    @Override
    public void insertEvent(final Blackhole eater, final FiringsCounter resultFirings) {
        final long id = AbstractBean.getAndIncrementIdGeneratorValue();
        insertJoinEvents(numberOfJoins, id, (int) id, async, eater);
        kieSession.fireAllRules();
        if (pseudoClock) {
            ((SessionPseudoClock) kieSession.getSessionClock()).advanceTime(EVENT_EXPIRATION_BASE_MS, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void setupCounter() {
        super.setupCounter();
        // Sets the id generator to correct value so we can use the ids to fire rules. Rules have constraints (value > id)
        AbstractBean.setIdGeneratorValue(numberOfRules + 1);
        if (LOG_FIRINGS) {
            kieSession.setGlobal("logger", LOGGER);
        }
    }

    @TearDown(Level.Iteration)
    public void cleanup() {
        if (LOG_FIRINGS) {
            LOGGER.flush();
        }
    }
}
