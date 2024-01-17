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

package org.drools.benchmarks.turtle.reproducers;

import java.util.concurrent.TimeUnit;

import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.benchmarks.common.ProviderException;
import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.drools.benchmarks.common.util.RuntimeUtil;
import org.drools.benchmarks.common.model.MyFact1;
import org.drools.benchmarks.common.model.MyFact2;
import org.drools.benchmarks.common.model.MyFact3;
import org.drools.benchmarks.common.model.MyFact4;
import org.drools.benchmarks.common.model.MyFact5;
import org.kie.api.KieServices;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Benchmark reproducing DROOLS-1387
 */
@Warmup(iterations = 100000)
@Measurement(iterations = 10000)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class Drools1387ExistsBenchmark extends AbstractBenchmark {

    @Setup
    @Override
    public void setup() throws ProviderException {
        kieBase = BuildtimeUtil.createKieBaseFromResource(KieServices.Factory.get().getResources().newClassPathResource("reproducers/drools1387/exists.drl"));
    }

    @Setup(Level.Iteration)
    public void setupKieSession() {
        kieSession = RuntimeUtil.createKieSession(kieBase);
    }

    @Benchmark
    public void test(final Blackhole eater) {
        MyFact1 fact1 = new MyFact1();
        fact1.setId("hoge");
        fact1.setValue1("hoge1");
        fact1.setValue2("hoge2");
        fact1.setValue3("hoge3");
        fact1.setValue4("hoge4");
        fact1.setValue5("hoge5");

        MyFact2 fact2 = new MyFact2();
        fact2.setId("hoge");
        fact2.setValue1("hoge1");
        fact2.setValue2("hoge2");
        fact2.setValue3("hoge3");
        fact2.setValue4("hoge4");
        fact2.setValue5("hoge5");

        MyFact3 fact3 = new MyFact3();
        fact3.setId("hoge");
        fact3.setValue1("hoge1");
        fact3.setValue2("hoge2");
        fact3.setValue3("hoge3");
        fact3.setValue4("hoge4");
        fact3.setValue5("hoge5");

        MyFact4 fact4 = new MyFact4();
        fact4.setId("hoge");
        fact4.setValue1("hoge1");
        fact4.setValue2("hoge2");
        fact4.setValue3("hoge3");
        fact4.setValue4("hoge4");
        fact4.setValue5("hoge5");

        MyFact5 fact5 = new MyFact5();
        fact5.setId("hoge");
        fact5.setValue1("hoge1");
        fact5.setValue2("hoge2");
        fact5.setValue3("hoge3");
        fact5.setValue4("hoge4");
        fact5.setValue5("hoge5");

        eater.consume(kieSession.insert(fact1));
        eater.consume(kieSession.insert(fact2));
        eater.consume(kieSession.insert(fact3));
        eater.consume(kieSession.insert(fact4));
        eater.consume(kieSession.insert(fact5));

        eater.consume(kieSession.fireAllRules());
    }
}
