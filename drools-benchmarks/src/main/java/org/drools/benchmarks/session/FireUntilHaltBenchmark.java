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

package org.drools.benchmarks.session;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.benchmarks.common.DrlProvider;
import org.drools.benchmarks.common.providers.RulesWithJoinsProvider;
import org.drools.benchmarks.domain.A;
import org.drools.benchmarks.domain.B;
import org.drools.benchmarks.domain.C;
import org.drools.benchmarks.domain.D;
import org.kie.api.conf.EventProcessingOption;
import org.kie.internal.conf.MultithreadEvaluationOption;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Inserts facts and fires at each insertion causing the activation of all rules.
 */
public class FireUntilHaltBenchmark extends AbstractBenchmark {

    @Param({"12", "48", "192", "768"})
    private int rulesNr;

    @Param({"10", "100", "1000"})
    private int factsNr;

    @Param({"true", "false"})
    private boolean multithread;

    @Param({"true", "false"})
    private boolean cep;

    //@Param({"1", "2", "3"})
    private int joinsNr = 1;

    private static final String GLOBAL = "global java.util.concurrent.atomic.AtomicInteger counter\n" +
            "global java.util.concurrent.CountDownLatch done";
    private static final String CONSEQUENCE_1 = "if (counter.incrementAndGet() == ";
    private static final String CONSEQUENCE_2 = " ) {\n  drools.halt();\n  done.countDown();\n}";

    private CountDownLatch done;

    @Setup
    public void setupKieBase() {
        int fireNr = rulesNr * factsNr;
        String consequence = CONSEQUENCE_1 + fireNr + CONSEQUENCE_2;
        final DrlProvider drlProvider = new RulesWithJoinsProvider(joinsNr, cep, true, false, GLOBAL, consequence);
        createKieBaseFromDrl( drlProvider.getDrl(rulesNr),
                multithread ? MultithreadEvaluationOption.YES : MultithreadEvaluationOption.NO,
                cep ? EventProcessingOption.STREAM : EventProcessingOption.CLOUD );
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() {
        createKieSession();

        done = new CountDownLatch(1);
        kieSession.setGlobal( "done", done );

        AtomicInteger counter = new AtomicInteger( 0 );
        kieSession.setGlobal( "counter", counter );

        new Thread( () -> {
            kieSession.fireUntilHalt();
        } ).start();
    }

    @Benchmark
    public void test(final Blackhole eater) {
        eater.consume(kieSession.insert( new A( rulesNr + 1 ) ));

        for ( int i = 0; i < factsNr; i++ ) {
            eater.consume(kieSession.insert( new B( rulesNr + i + 3 ) ));
            if (joinsNr > 1) {
                eater.consume(kieSession.insert( new C( rulesNr + factsNr + i + 3 ) ));
            }
            if (joinsNr > 2) {
                eater.consume(kieSession.insert( new D( rulesNr + factsNr*2 + i + 3 ) ));
            }
        }

        try {
            done.await();
        } catch (InterruptedException e) {
            throw new RuntimeException( e );
        }
    }
}