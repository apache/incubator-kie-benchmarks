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
import org.drools.benchmarks.model.AbstractBean;
import org.drools.core.time.SessionPseudoClock;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.infra.Blackhole;

public class EventTriggersOneAgendaFireAllRulesBenchmark extends AbstractEventTriggersAgendaThroughputBenchmark {

    @Param({"1", "10", "100"})
    private int fireAllRulesRatio = 1;

    @Param({"true", "false"})
    private boolean hashed;

    private long fireAllRulesCounter;

    @Override
    protected DrlProvider getDrlProvider(long eventExpirationMs, boolean logFirings) {
        return new PartitionedCepRulesProvider(numberOfJoins,
                numberOfJoinedEvents,
                Long.MAX_VALUE,
                hashed ?
                        i -> "value == " + i :
                        i -> "boxedValue.equals(" + i + ")",
                true,
                logFirings);
    }

    @Setup(Level.Iteration)
    public void prepareBenchmark() {
        fireAllRulesCounter = 0;
    }

    @Benchmark
    @Override
    public void insertEventBenchmark(final Blackhole eater, final FiringsCounter resultFirings) {
        final long id = AbstractBean.getAndIncrementIdGeneratorValue();
        insertJoinEvents(numberOfJoins, id,(int) (id % numberOfRules), async, eater);
        if (fireAllRulesCounter % fireAllRulesRatio == 0) {
            kieSession.fireAllRules();
        }
        fireAllRulesCounter++;
        if (pseudoClock) {
            ((SessionPseudoClock) kieSession.getSessionClock()).advanceTime(EVENT_EXPIRATION_BASE_MS, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    protected long getStartingIdGeneratorValue() {
        return 0;
    }
}
