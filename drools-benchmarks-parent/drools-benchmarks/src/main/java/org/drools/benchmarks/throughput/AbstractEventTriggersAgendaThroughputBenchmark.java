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

import java.util.concurrent.atomic.LongAdder;

import org.drools.benchmarks.common.DRLProvider;
import org.drools.benchmarks.common.model.A;
import org.drools.benchmarks.common.model.AbstractBean;
import org.drools.benchmarks.common.model.B;
import org.drools.benchmarks.common.model.C;
import org.drools.benchmarks.common.model.D;
import org.drools.benchmarks.common.model.E;
import org.drools.benchmarks.common.model.FireLogger;
import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.drools.benchmarks.common.util.RuntimeUtil;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.conf.MultithreadEvaluationOption;
import org.openjdk.jmh.annotations.AuxCounters;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
public abstract class AbstractEventTriggersAgendaThroughputBenchmark extends AbstractThroughputBenchmark {

    protected static final long EVENT_EXPIRATION_BASE_MS = 10;

    private static final boolean LOG_FIRINGS = false;
    private static final FireLogger LOGGER = LOG_FIRINGS ? new FireLogger() : null;

    @Param({"true"})
    protected boolean pseudoClock = true;

    @Param({"true", "false"})
    protected boolean multithread = true;

    @Param({"false"})
    protected boolean async = false;

    @Param({"8"})
    protected int numberOfRules = 8;

    @Param({"1", "2", "4"})
    protected int numberOfJoins = 1;

    @Param({"1", "2", "4", "8"})
    protected int numberOfJoinedEvents = 1;

    @Param({"true"})
    protected boolean eventsExpiration = true;

    @Param({"1"})
    protected int eventsExpirationRatio = 1;

    private boolean countFirings = true;

    protected static LongAdder insertCounter;
    protected static LongAdder firingCounter;

    protected abstract DRLProvider getDrlProvider(final long eventExpirationMs, final boolean logFirings);
    public abstract void insertEventBenchmark(final Blackhole eater, final FiringsCounter resultFirings );
    protected abstract long getStartingIdGeneratorValue();

    @Setup
    public void setupKieBase() {
        final long eventExpirationMs = eventsExpiration ?
                EVENT_EXPIRATION_BASE_MS * Math.max(1, numberOfJoinedEvents) * eventsExpirationRatio + 1L :
                -1L;
        final DRLProvider drlProvider = getDrlProvider(eventExpirationMs, LOG_FIRINGS);
        final String drl = drlProvider.getDrl(numberOfRules);

        kieBase = BuildtimeUtil.createKieBaseFromDrl(
                drl,
                EventProcessingOption.STREAM,
                multithread ? MultithreadEvaluationOption.YES : MultithreadEvaluationOption.NO);

        if (((InternalKnowledgeBase) kieBase).getRuleBaseConfiguration().isMultithreadEvaluation() != multithread) {
            throw new IllegalStateException();
        }
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() {
        if (pseudoClock) {
            kieSession = RuntimeUtil.createKieSession(kieBase, ClockTypeOption.get("pseudo"));
        } else {
            kieSession = RuntimeUtil.createKieSession(kieBase);
        }
        AbstractBean.setIdGeneratorValue(getStartingIdGeneratorValue());
        setupCounter();
    }

    @TearDown(Level.Iteration)
    public void cleanup() {
        if (LOG_FIRINGS) {
            LOGGER.flush();
        }
    }

    @AuxCounters
    @State(Scope.Thread)
    public static class FiringsCounter {
        public long fireCount() {
            return firingCounter.longValue();
        }
        public long insertCount() { return insertCounter.longValue(); }
    }

    public void setupCounter() {
        if (countFirings) {
            insertCounter = new LongAdder();
            firingCounter = new LongAdder();
            kieSession.setGlobal( "firings", firingCounter );
        }
        if (LOG_FIRINGS) {
            kieSession.setGlobal("logger", LOGGER);
        }
    }

    public long getFiringsCount() {
        return firingCounter.longValue();
    }

    protected void insertJoinEvents(final int numberOfJoins, final long eventId, final int eventValue,
            final boolean async, final Blackhole eater) {
        switch (numberOfJoins) {
            case 0:
                insertJoinEvent(new A(eventId, eventValue), async, eater);
                break;
            case 1:
                insertJoinEvent(new A(eventId, eventValue), async, eater);
                insertJoinEvent(new B(eventId, eventValue), async, eater);
                break;
            case 2:
                insertJoinEvent(new A(eventId, eventValue), async, eater);
                insertJoinEvent(new B(eventId, eventValue), async, eater);
                insertJoinEvent(new C(eventId, eventValue), async, eater);
                break;
            case 3:
                insertJoinEvent(new A(eventId, eventValue), async, eater);
                insertJoinEvent(new B(eventId, eventValue), async, eater);
                insertJoinEvent(new C(eventId, eventValue), async, eater);
                insertJoinEvent(new D(eventId, eventValue), async, eater);
                break;
            case 4:
                insertJoinEvent(new A(eventId, eventValue), async, eater);
                insertJoinEvent(new B(eventId, eventValue), async, eater);
                insertJoinEvent(new C(eventId, eventValue), async, eater);
                insertJoinEvent(new D(eventId, eventValue), async, eater);
                insertJoinEvent(new E(eventId, eventValue), async, eater);
                break;
            default:
                throw new IllegalArgumentException("Unsupported number of joins! Maximal number of joins is 4.");
        }
    }

    private void insertJoinEvent(final AbstractBean event, final boolean async, final Blackhole eater) {
        if (async) {
            insertEventAsync(event, eater);
        } else {
            insertEvent(event, eater);
        }
    }

    private void insertEvent(final AbstractBean event, final Blackhole eater) {
        if (eater != null) {
            eater.consume(kieSession.insert(event));
        } else {
            kieSession.insert(event);
        }
    }

    private void insertEventAsync(final AbstractBean event, final Blackhole eater) {
        if (eater != null) {
            eater.consume(((StatefulKnowledgeSessionImpl) kieSession).insertAsync(event));
        } else {
            ((StatefulKnowledgeSessionImpl) kieSession).insertAsync(event);
        }
    }
}
