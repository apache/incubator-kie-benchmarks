/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import org.drools.benchmarks.domain.A;
import org.drools.benchmarks.domain.B;
import org.kie.api.runtime.rule.FactHandle;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;

public class RuleUnlikingBenchmark extends AbstractSessionBenchmark {

    @Param({"10", "100", "1000"})
    private int loopCount;

    @Param({"1", "10", "100"})
    private int rulesNr;

    @Param({"1", "10", "100"})
    private int factsNr;

    @Setup
    public void setupKieBase() {
        StringBuilder sb = new StringBuilder();
        sb.append( "import org.drools.benchmarks.domain.*;\n" );
        for ( int i = 0; i < rulesNr; i++ ) {
            sb.append( "rule R" + i + " when\n" +
                       "  A( $a : value > " + i + ")\n" +
                       "  B( $b : value > $a)\n" +
                       "then\n" +
                       "end\n" );
        }

        createKieBaseFromDrl( sb.toString() );
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() {
        createKieSession();
    }

    @Benchmark
    public void test() {
        FactHandle aFH = kieSession.insert( new A( rulesNr + 1 ) );
        for ( int i = 0; i < factsNr; i++ ) {
            kieSession.insert( new B( rulesNr + 3 ) );
        }

        kieSession.fireAllRules();

        for (int i = 0; i < loopCount; i++) {
            kieSession.delete( aFH );
            kieSession.fireAllRules();
            kieSession.insert( new A( rulesNr + 1 ) );
            kieSession.fireAllRules();
        }
    }
}
