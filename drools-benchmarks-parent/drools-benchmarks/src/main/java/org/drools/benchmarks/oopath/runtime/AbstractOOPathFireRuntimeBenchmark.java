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

package org.drools.benchmarks.oopath.runtime;

import java.util.concurrent.TimeUnit;

import org.drools.benchmarks.common.util.RuntimeUtil;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@Warmup(iterations = 40000)
@Measurement(iterations = 10000)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public abstract class AbstractOOPathFireRuntimeBenchmark extends AbstractOOPathRuntimeBenchmark {

    @Param({"32", "64", "128"})
    private int numberOfFacts;

    @Setup(Level.Iteration)
    public void setupSessionAndInsertFacts(final Blackhole eater) {
        kieSession = RuntimeUtil.createKieSession(kieBase);
        insertFacts(getFacts(numberOfFacts), eater);
    }

    @Benchmark
    public int fireRules() {
        return kieSession.fireAllRules();
    }
}
