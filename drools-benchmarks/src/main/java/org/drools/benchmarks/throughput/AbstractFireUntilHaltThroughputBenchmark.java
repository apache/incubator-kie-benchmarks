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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.drools.benchmarks.domain.A;
import org.drools.benchmarks.domain.AbstractBean;
import org.drools.benchmarks.domain.B;
import org.drools.benchmarks.domain.C;
import org.drools.benchmarks.domain.D;
import org.drools.benchmarks.domain.E;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
public abstract class AbstractFireUntilHaltThroughputBenchmark extends AbstractThroughputBenchmark {

    private ExecutorService executor;

    @Setup(Level.Iteration)
    @Override
    public void setup() {
        createKieSession();
        executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> kieSession.fireUntilHalt());
    }

    @TearDown(Level.Iteration)
    public void stopFireUntilHaltThread() throws InterruptedException, ExecutionException {
        executor.shutdown();
        if (!executor.awaitTermination(10, TimeUnit.MILLISECONDS)) {
            executor.shutdownNow();
        }
    }

    protected void insertJoinEvents(final int numberOfJoins, final long eventId, final int eventValue,
            final boolean async, final Blackhole eater) {
        switch (numberOfJoins) {
            case 0:
                insertJoinEvents(new AbstractBean[]{new A(eventId, eventValue)}, async, eater);
                break;
            case 1:
                insertJoinEvents(
                        new AbstractBean[]{
                            new A(eventId, eventValue), new B(eventId, eventValue)},
                        async, eater);
                break;
            case 2:
                insertJoinEvents(
                        new AbstractBean[]{
                                new A(eventId, eventValue), new B(eventId, eventValue),
                                new C(eventId, eventValue)},
                        async, eater);
                break;
            case 3:
                insertJoinEvents(
                        new AbstractBean[]{
                                new A(eventId, eventValue), new B(eventId, eventValue),
                                new C(eventId, eventValue), new D(eventId, eventValue)},
                        async, eater);
                break;
            case 4:
                insertJoinEvents(
                        new AbstractBean[]{
                                new A(eventId, eventValue), new B(eventId, eventValue),
                                new C(eventId, eventValue), new D(eventId, eventValue),
                                new E(eventId, eventValue)},
                        async, eater);
                break;
            default:
                throw new IllegalArgumentException("Unsupported number of joins! Maximal number of joins is 4.");
        }
    }

    private void insertJoinEvents(final AbstractBean[] events, final boolean async, final Blackhole eater) {
        for (AbstractBean event : events) {
            if (async) {
                insertEventAsync(event, eater);
            } else {
                insertEvent(event, eater);
            }
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
