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

package org.drools.benchmarks.session;

import java.util.Date;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Benchmarks creation of a ksession from an empty kbase, insertion of 10 facts of 10 different classes
 * and firing. On 5.x this caused the creation of a new ObjectTypeNode for each insertion.
 */
@Warmup(iterations = 100000)
@Measurement(iterations = 10000)
public class EmptySessionWithInsertionsAndFireBenchmark extends AbstractEmptySessionBenchmark {

    @Benchmark
    public int testCreateEmptySession(final Blackhole eater) {
        kieSession = kieBase.newKieSession();
        eater.consume(kieSession.insert( "1" ));
        eater.consume(kieSession.insert(1));
        eater.consume(kieSession.insert(1L));
        eater.consume(kieSession.insert((short) 1));
        eater.consume(kieSession.insert(1.0));
        eater.consume(kieSession.insert(1.0f));
        eater.consume(kieSession.insert('1'));
        eater.consume(kieSession.insert( Boolean.TRUE ));
        eater.consume(kieSession.insert( String.class ));
        eater.consume(kieSession.insert( new Date() ));
        return kieSession.fireAllRules();
    }
}
