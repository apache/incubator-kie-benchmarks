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

import java.util.Collection;

import org.drools.core.event.DefaultAgendaEventListener;
import org.kie.api.runtime.KieSession;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@Warmup(iterations = 30000)
@Measurement(iterations = 5000)
public class DisposeSessionBenchmark extends AbstractSessionsPoolBenchmark {

    @Param({"true", "false"})
    private boolean usePool;

    private KieSession kieSession;
    private Collection<Object> factsForSession;

    @Setup
    public void generateFactsForSessions() {
        factsForSession = generateFacts();
    }

    @Setup(Level.Iteration)
    public void setupKieSessions(final Blackhole eater) {
        kieSession = usePool ? sessionsPool.newKieSession() : kieContainer.newKieSession();
        kieSession.addEventListener(new DefaultAgendaEventListener());
        exerciseSession(kieSession, factsForSession, eater);
    }

    @Benchmark
    public void getKieSessionFromContainer() {
        kieSession.dispose();
    }
}
