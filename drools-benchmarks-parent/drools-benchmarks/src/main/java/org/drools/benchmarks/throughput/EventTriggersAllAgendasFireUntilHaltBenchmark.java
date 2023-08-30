/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.drools.benchmarks.throughput;

import java.util.concurrent.TimeUnit;
import org.drools.benchmarks.common.DRLProvider;
import org.drools.benchmarks.common.providers.PartitionedCepRulesProvider;
import org.drools.benchmarks.common.model.AbstractBean;
import org.drools.core.time.SessionPseudoClock;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

public class EventTriggersAllAgendasFireUntilHaltBenchmark extends AbstractEventTriggersAgendasFireUntilHaltBenchmark {

    @Override
    protected DRLProvider getDrlProvider(long eventExpirationMs, boolean logFirings) {
        return new PartitionedCepRulesProvider(numberOfJoins,
                numberOfJoinedEvents,
                eventExpirationMs,
                i -> "value > " + i,
                true,
                logFirings);
    }

    @Override
    protected long getStartingIdGeneratorValue() {
        return numberOfRules + 1;
    }

    @Override
    protected long getExpectedFiringsPerInsert() {
        return numberOfRules * getFiringsPerRule();
    }

    @Override
    protected long getMissingFiringsOnFirstEvents() {
        final long firingsPerRule = getFiringsPerRule();
        long missingFirings = 0;
        if (numberOfJoinedEvents > 1 && numberOfJoins > 0) {
            for (int i = 1; i <= numberOfJoinedEvents; i++) {
                missingFirings += (firingsPerRule - (long) Math.pow(i, numberOfJoins));
            }
            missingFirings *= numberOfRules;
        }
        return missingFirings;
    }

    @Benchmark
    @Override
    public void insertEventBenchmark(final Blackhole eater, final FiringsCounter resultFirings) {
        if (canInsertEvent()) {
            final long id = AbstractBean.getAndIncrementIdGeneratorValue();
            insertJoinEvents(numberOfJoins, id, (int) id, eater);
            insertCounter.add(1);
            if (pseudoClock) {
                ((SessionPseudoClock) kieSession.getSessionClock()).advanceTime(EVENT_EXPIRATION_BASE_MS, TimeUnit.MILLISECONDS);
            }
        }
    }

    private long getFiringsPerRule() {
        return (long) Math.pow(Math.max(1, numberOfJoinedEvents), Math.max(1, numberOfJoins));
    }
}
