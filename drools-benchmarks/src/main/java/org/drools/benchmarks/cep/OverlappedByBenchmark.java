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

package org.drools.benchmarks.cep;

import java.util.SortedSet;
import java.util.TreeSet;

import org.drools.benchmarks.common.DRLProvider;
import org.drools.benchmarks.common.Event;
import org.drools.benchmarks.common.ProviderException;
import org.drools.benchmarks.common.TemporalOperator;
import org.drools.benchmarks.common.providers.BasicEventProvider;
import org.drools.benchmarks.common.providers.CepRulesProvider;
import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.drools.benchmarks.common.util.RuntimeUtil;
import org.drools.benchmarks.model.event.EventA;
import org.drools.benchmarks.model.event.EventB;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;

/**
 * Benchmarks "before" CEP operator.
 */
public class OverlappedByBenchmark extends AbstractCEPBenchmark {

    @Param({"8", "16", "32"})
    private int rulesAndEventsNumber;

    private SortedSet<Event> events;

    @Setup
    public void setupKieBase() {
        final DRLProvider drlProvider =
                new CepRulesProvider(EventA.class, EventB.class, TemporalOperator.OVERLAPPED_BY, "1", "10");
        kieBase = BuildtimeUtil.createKieBaseFromDrl(drlProvider.getDrl(rulesAndEventsNumber), EventProcessingOption.STREAM);
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() throws ProviderException {
        final BasicEventProvider eventProvider = new BasicEventProvider();
        events = new TreeSet<Event>();
        events.addAll(eventProvider.getEvents(EventA.class, rulesAndEventsNumber / 2, 2, 100, 10));
        events.addAll(eventProvider.getEvents(EventB.class, rulesAndEventsNumber / 2, 5, 100, 10));
        kieSession = RuntimeUtil.createKieSession(kieBase, ClockTypeOption.get("pseudo"));
    }

    @Benchmark
    public int testOverlappedByOperator() {
        return insertEventsAndFire(events);
    }
}
