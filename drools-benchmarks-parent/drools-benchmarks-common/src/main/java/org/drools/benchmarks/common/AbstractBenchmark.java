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

package org.drools.benchmarks.common;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@BenchmarkMode(Mode.SingleShotTime)
@State(Scope.Thread)
@Warmup(iterations = 30)
@Measurement(iterations = 20)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public abstract class AbstractBenchmark {

    protected KieBase kieBase;
    protected KieSession kieSession;

    public abstract void setup() throws ProviderException;

    @TearDown(Level.Iteration)
    public void tearDown() {
        if (kieSession != null) {
            kieSession.halt();
            kieSession.dispose();
            kieSession = null;
        }
    }

    protected void insertFacts(final Collection<Object> facts, final Blackhole eater) {
        facts.forEach(fact -> eater.consume(kieSession.insert(fact)));
    }
}
