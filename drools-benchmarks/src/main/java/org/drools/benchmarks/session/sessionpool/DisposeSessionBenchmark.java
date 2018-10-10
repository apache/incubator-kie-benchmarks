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

import org.kie.api.runtime.KieSession;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;

// TODO - warmup and measurement iteration count
@BenchmarkMode(Mode.SingleShotTime)
@Warmup(iterations = 50000)
@Measurement(iterations = 10000)
public class DisposeSessionBenchmark extends AbstractSessionsPoolBenchmark {

    private KieSession kieSessionFromPool;
    private KieSession kieSessionFromContainer;

    private Collection<Object> factsForSession;

    @Setup
    public void generateFactsForSessions() {
        factsForSession = generateFacts();
    }

    @Setup(Level.Iteration)
    public void setupKieSessions() {
        kieSessionFromPool = sessionsPool.newKieSession();
        kieSessionFromContainer = kieContainer.newKieSession();
        doSomethingWithSessions();
    }

    @Benchmark
    public void getKieSessionFromPool() {
        kieSessionFromPool.dispose();
    }

    @Benchmark
    public void getKieSessionFromContainer() {
        kieSessionFromContainer.dispose();
    }

    private void doSomethingWithSessions() {
        insertFactsIntoSession(kieSessionFromPool, factsForSession, null);
        kieSessionFromPool.fireAllRules();

        insertFactsIntoSession(kieSessionFromContainer, factsForSession, null);
        kieSessionFromContainer.fireAllRules();
    }
}
