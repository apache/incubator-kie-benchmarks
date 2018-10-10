/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.benchmarks.session.sessionpool;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.drools.benchmarks.common.DRLProvider;
import org.drools.benchmarks.common.providers.SimpleRulesWithConstraintsProvider;
import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.drools.benchmarks.model.A;
import org.drools.benchmarks.model.B;
import org.drools.benchmarks.model.C;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieContainerSessionsPool;
import org.kie.api.runtime.KieSession;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Thread)
@Warmup(iterations = 20, time = 50, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 50, timeUnit = TimeUnit.MILLISECONDS)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(5)
public abstract class AbstractSessionsPoolBenchmark {

    @Param({"10"})
    private int numberOfRules;

    @Param({"1"})
    private int initialSessionPoolSize;

    @Param({"10"})
    private int numberOfFacts;

    KieContainer kieContainer;
    KieContainerSessionsPool sessionsPool;

    @Setup
    public void setupKieContainer() throws IOException {
        final Resource drlResource = KieServices.get().getResources()
                .newReaderResource(new StringReader(getDRLProvider().getDrl(numberOfRules)))
                .setResourceType(ResourceType.DRL)
                .setSourcePath("drlFile.drl");
        kieContainer = BuildtimeUtil.createKieContainerFromResources(false, drlResource);
    }

    @Setup(Level.Iteration)
    public void setupSessionsPool() {
        sessionsPool = kieContainer.newKieSessionsPool(initialSessionPoolSize);
    }

    private DRLProvider getDRLProvider() {
        // First 2 nodes are shared
        final String constraints =
                "A(value == 0) \n"
                + "B(value == 1) \n"
                + "C(value > ${i})";
        return new SimpleRulesWithConstraintsProvider(constraints);
    }

    Collection<Object> generateFacts() {
        final List<Object> facts = new ArrayList<>();
        facts.add(new A(0));
        facts.add(new B(1));
        for (int i = 2; i < numberOfFacts; i++) {
            facts.add(new C(i));
        }
        return facts;
    }

    void insertFactsIntoSession(final KieSession kieSession, final Collection<Object> facts, final Blackhole eater) {
        if (eater == null) {
            facts.forEach(kieSession::insert);
        } else {
            facts.forEach(fact -> eater.consume(kieSession.insert(fact)));
        }
    }
}
