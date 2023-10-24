/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.drools.benchmarks.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.benchmarks.common.model.A;
import org.drools.benchmarks.common.model.B;
import org.drools.benchmarks.common.model.C;
import org.drools.benchmarks.common.model.D;
import org.drools.benchmarks.common.providers.RulesWithJoinsProvider;
import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.drools.benchmarks.common.util.RuntimeUtil;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieSession;
import org.kie.internal.conf.ParallelExecutionOption;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

@Warmup(iterations = 20)
@Measurement(iterations = 20)
public class ConcurrentUpdateLeftTupleBenchmark extends AbstractBenchmark {

    @Param({"32"})
    private int rulesNr;

    @Param({"15"})
    private int factsNr;

    @Param({"3"})
    private int joinsNr;

    private static final int SESSIONS_NR = 20;

    private List<KieSession> kieSessions = new ArrayList<>(); // Do not use kieSession in AbstractBenchmark

    @Setup
    public void setupKieBase() {
        final RulesWithJoinsProvider drlProvider = new RulesWithJoinsProvider(joinsNr, false, true)
                .withNot(true)
                .withGeneratedConsequence(false)
                .withConsequence("    $a.setValue2($a.getValue2() + 1);\n" +
                                         "    update($a);\n"); // this update triggers LeftInputAdapterNode.modifyObject -> LeftTuple.getTupleSink
        //System.out.println(drlProvider.getDrl(rulesNr));
        kieBase = BuildtimeUtil.createKieBaseFromDrl(drlProvider.getDrl(rulesNr),
                                                     ParallelExecutionOption.SEQUENTIAL,
                                                     EventProcessingOption.CLOUD);
        //ReteDumper.dumpRete(kieBase);
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() {
        for (int n = 0; n < SESSIONS_NR; n++) {
            StatefulKnowledgeSessionImpl session = (StatefulKnowledgeSessionImpl) RuntimeUtil.createKieSession(kieBase);
            A a = new A(rulesNr + 1);

            session.insert(a);

            for (int i = 0; i < factsNr; i++) {

                session.insert(new B(rulesNr + i + 3));
                if (joinsNr > 1) {
                    session.insert(new C(rulesNr + factsNr + i + 3));
                }
                if (joinsNr > 2) {
                    session.insert(new D(rulesNr + factsNr * 2 + i + 3));
                }
            }
            kieSessions.add(session);
        }
    }

    @TearDown(Level.Iteration)
    public void tearDoneIter() {
        for (int n = 0; n < SESSIONS_NR; n++) {
            kieSessions.get(n).dispose();
        }
        kieSessions.clear();
    }

    @Benchmark
    public int test() {
        ExecutorService executor = Executors.newFixedThreadPool(SESSIONS_NR);

        for (int n = 0; n < SESSIONS_NR; n++) {
            final int index = n;
            executor.execute(new Runnable() {

                public void run() {
                    int fired = kieSessions.get(index).fireAllRules();
//                    System.out.println(fired);
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(300, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return kieSessions.size();
    }

    public static void main(String[] args) {
        ConcurrentUpdateLeftTupleBenchmark benchmark = new ConcurrentUpdateLeftTupleBenchmark();

        benchmark.rulesNr = 32;
        benchmark.factsNr = 15;
        benchmark.joinsNr = 3;

        benchmark.setupKieBase();
        benchmark.setup();
        benchmark.test();
    }
}