/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.drools.benchmarks.marshaller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.kie.api.KieServices;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.server.api.marshalling.Marshaller;
import org.kie.server.api.marshalling.MarshallerFactory;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
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

@BenchmarkMode(Mode.SingleShotTime)
@State(Scope.Thread)
@Warmup(iterations = 100)
@Measurement(iterations = 100)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class JSONMarshallerDroolsCommandBenchmark {

    private static final int LIST_SIZE = 100;
    private static final int MAX_THREAD = 100;
    private static final int NUM_TASK = 100;

    @Param({"false", "true"})
    protected boolean findDeserializerFirst;

    private Marshaller marshaller;
    private String payload;
    private ExecutorService executor;

    private BatchExecutionCommand createTestCommand() {

        KieCommands commandsFactory = KieServices.Factory.get().getCommands();
        List<Command<?>> commands = new ArrayList<Command<?>>();

        List<Model2> model2List = new ArrayList<>();
        for (int i = 0; i < LIST_SIZE; i++) {
            Model3 model3 = new Model3("name" + i, i);
            model2List.add(new Model2(model3));
        }
        Model1 model1 = new Model1(model2List);

        commands.add(commandsFactory.newInsert(model1, "fact-model1"));
        commands.add(commandsFactory.newFireAllRules("fire-result"));
        BatchExecutionCommand batchExecution = commandsFactory.newBatchExecution(commands);
        return batchExecution;
    }

    private Set<Class<?>> getCustomClasses() {
        HashSet<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(Model1.class);
        classes.add(Model2.class);
        classes.add(Model3.class);
        return classes;
    }

    @Setup
    public void setup() {
        if (findDeserializerFirst) {
            System.setProperty("org.kie.server.json.findDeserializerFirst.enabled", "true");
        } else {
            System.setProperty("org.kie.server.json.findDeserializerFirst.enabled", "false");
        }

        marshaller = MarshallerFactory.getMarshaller(getCustomClasses(), MarshallingFormat.JSON, getClass().getClassLoader());
        BatchExecutionCommand inputCommand = createTestCommand();

        payload = marshaller.marshall(inputCommand);
    }

    @Setup(Level.Iteration)
    public void setupIteration() {
        executor = Executors.newFixedThreadPool(MAX_THREAD);
    }

    @Benchmark
    public boolean test(final Blackhole eater) {
        CountDownLatch latch = new CountDownLatch(MAX_THREAD);
        AtomicInteger count = new AtomicInteger(0);
        for (int n = 0; n < NUM_TASK; n++) {
            executor.execute(new Runnable() {

                public void run() {
                    latch.countDown();
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                    }
                    marshaller.unmarshall(payload, BatchExecutionCommandImpl.class);
                    count.incrementAndGet();
                }
            });
        }

        executor.shutdown();
        boolean result = false;
        try {
            result = executor.awaitTermination(300, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
        if (!result) {
            throw new RuntimeException("Timeout");
        } else {
            return true;
        }
    }
}
