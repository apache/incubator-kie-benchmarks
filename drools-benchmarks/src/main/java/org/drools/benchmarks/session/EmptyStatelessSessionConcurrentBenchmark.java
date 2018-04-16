/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.benchmarks.session;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Benchmarks the creation of a stateless ksession from an empty kbase
 */
@State(Scope.Benchmark)
@Warmup(iterations = 10000)
@Measurement(iterations = 10000)
public class EmptyStatelessSessionConcurrentBenchmark extends AbstractEmptySessionBenchmark {

    private static final int THREAD_COUNT = 20;

    private ExecutorService executorService;
    private Future[] futures;

    @Setup(Level.Iteration)
    public void setupExecutorService() {
        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        futures = new Future[THREAD_COUNT];
    }

    @TearDown(Level.Iteration)
    public void stopExecutor() {
        executorService.shutdownNow();
    }

    @Benchmark
    public void testCreateEmptySession(final Blackhole eater) throws ExecutionException, InterruptedException {
        for (int i = 0; i < THREAD_COUNT; i++) {
            futures[i] = executorService.submit(() -> eater.consume(kieBase.newStatelessKieSession()));
        }
        for (int i = 0; i < THREAD_COUNT; i++) {
            futures[i].get();
        }
    }
}
