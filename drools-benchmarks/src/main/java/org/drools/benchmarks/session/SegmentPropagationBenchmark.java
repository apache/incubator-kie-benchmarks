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

import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.benchmarks.domain.A;
import org.drools.benchmarks.domain.B;
import org.drools.benchmarks.domain.C;
import org.drools.benchmarks.domain.D;
import org.drools.benchmarks.domain.E;
import org.kie.api.runtime.rule.FactHandle;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;

public class SegmentPropagationBenchmark extends AbstractBenchmark {

    @Param({"10", "100", "1000"})
    private int loopCount;

    @Param({"1", "10", "100"})
    private int treesNr;

    @Param({"1", "10", "100"})
    private int factsNr;

    private FactHandle aFH;

    @Setup
    public void setupKieBase() {
        StringBuilder sb = new StringBuilder();
        sb.append( "import org.drools.benchmarks.domain.*;\n" );
        for ( int i = 0; i < treesNr; i++ ) {
            sb.append( "rule R" + i + "C when\n" +
                       "  A( $a : value > " + i + ")\n" +
                       "  B( $b : value > $a)\n" +
                       "  C( value > $b)\n" +
                       "then\n" +
                       "end\n" );
            sb.append( "rule R" + i + "D when\n" +
                       "  A( $a : value > " + i + ")\n" +
                       "  B( $b : value > $a)\n" +
                       "  D( value > $b)\n" +
                       "then\n" +
                       "end\n" );
            sb.append( "rule R" + i + "E when\n" +
                       "  A( $a : value > " + i + ")\n" +
                       "  B( $b : value > $a)\n" +
                       "  E( value > $b)\n" +
                       "then\n" +
                       "end\n" );
        }

        createKieBaseFromDrl( sb.toString() );
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() {
        createKieSession();

        aFH = kieSession.insert( new A( treesNr + 1 ) );
        for ( int i = 0; i < factsNr; i++ ) {
            kieSession.insert( new B( treesNr + 3 ) );
            kieSession.insert( new C( treesNr + 5 ) );
            kieSession.insert( new D( treesNr + 7 ) );
            kieSession.insert( new E( treesNr + 9 ) );
        }

        kieSession.fireAllRules();
    }

    @Benchmark
    public void test() {
        for (int i = 0; i < loopCount; i++) {
            kieSession.delete( aFH );
            kieSession.fireAllRules();
            kieSession.insert( new A( treesNr + 1 ) );
            kieSession.fireAllRules();
        }
    }
}
