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

import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.drools.benchmarks.common.util.RuntimeUtil;
import org.drools.benchmarks.common.model.A;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;

@Warmup(iterations = 2000)
@Measurement(iterations = 500)
public class FireOnlyWithAlphasBenchmark extends AbstractBenchmark {

    @Param({"32"})
    private int rulesNr;

    @Param({"1000"})
    private int factsNr;

    @Param({"true", "false"})
    private boolean skipBetaPropagation;

    @Setup
    public void setupKieBase() {
        kieBase = BuildtimeUtil.createKieBaseFromDrl(getDrl(rulesNr));
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() {
        kieSession = RuntimeUtil.createKieSession(kieBase);

        for (int i = 0; i < factsNr; i++) {
            kieSession.insert( new A( (i*2) % rulesNr ) );
        }
    }

    @Benchmark
    public int test() {
        int firings = kieSession.fireAllRules();
//        if (firings != factsNr * 2) {
//            throw new IllegalStateException("wrong firing number");
//        }
//        if (!kieSession.getObjects().isEmpty()) {
//            throw new IllegalStateException("session is expected to be empty");
//        }
        return firings;
    }

    private String getDrl(int rulesNr) {
        StringBuilder drlBuilder = new StringBuilder();
        drlBuilder.append("import " + A.class.getCanonicalName() + ";\n");
        for (int i = 0; i < rulesNr; i += 2) {
            drlBuilder.append( "rule R" + i + (skipBetaPropagation ? "" : " @Propagation(LAZY)") +
                    " when $a : A( value == " + i + " ) then modify($a) { setValue($a.getValue()+1) }; end\n" );
            drlBuilder.append( "rule R" + (i+1) + (skipBetaPropagation ? "" : " @Propagation(LAZY)") +
                    " when $a : A( value == " + (i+1) + " ) then delete($a); end\n" );
        }
        return drlBuilder.toString();
    }
}