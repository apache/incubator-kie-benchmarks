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
import org.drools.benchmarks.domain.C;
import org.drools.benchmarks.domain.D;
import org.drools.benchmarks.domain.E;
import org.kie.api.runtime.rule.FactHandle;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;

public class UpdateJoinRootFactAndFireBenchmark extends AbstractSessionBenchmark {

    @Param({"10", "100", "1000"})
    private int loopCount;

    @Param({"1", "10", "100"})
    private int rulesNr;

    @Param({"1", "10", "100"})
    private int factsNr;

    @Param({"true", "false"})
    private boolean insertLastJoinItem;

    private B[] bs;
    private C[] cs;
    private D[] ds;
    private E[] es;

    private FactHandle[] bFHs;
    private FactHandle[] cFHs;
    private FactHandle[] dFHs;
    private FactHandle[] eFHs;

    @Setup
    public void setupKieBase() {
        StringBuilder sb = new StringBuilder();
        sb.append( "import org.drools.benchmarks.domain.*;\n" );
        sb.append( "rule R0 salience 10 when\n" +
                "  $factA : A( $a : value > 0)\n" +
                "  B( $b : value > $a)\n" +
                "  C( $c : value > $b)\n" +
                "  D( $d : value > $c)\n" +
                "  E( $e : value > $d)\n" +
                "then\n" +
                "  modify( $factA ) { setValue(-1) }; \n" +
                "end\n" );

        for (int i = 1; i < rulesNr; i++) {
            sb.append( "rule R" + i + " when\n" +
                    "  A( $a : value > " + i + ")\n" +
                    "  B( $b : value > $a)\n" +
                    "  C( $c : value > $b)\n" +
                    "  D( $d : value > $c)\n" +
                    "  E( $e : value > $d)\n" +
                    "then\n" +
                    "end\n" );
        }

        createKieBaseFromDrl(sb.toString());
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() {
        createKieSession();

        bs = new B[factsNr];
        cs = new C[factsNr];
        ds = new D[factsNr];
        es = new E[factsNr];

        bFHs = new FactHandle[factsNr];
        cFHs = new FactHandle[factsNr];
        dFHs = new FactHandle[factsNr];
        eFHs = new FactHandle[factsNr];
    }

    @Benchmark
    public void test() {
        A a = new A( -1 );
        FactHandle aFH = kieSession.insert( a );
        for (int i = 0; i < factsNr; i++) {
            bs[i] = new B( rulesNr + 3 );
            bFHs[i] = kieSession.insert( bs[i] );
            cs[i] = new C( rulesNr + 5 );
            cFHs[i] = kieSession.insert( cs[i] );
            ds[i] = new D( rulesNr + 7 );
            dFHs[i] = kieSession.insert( ds[i] );
            if (insertLastJoinItem) {
                es[i] = new E( rulesNr + 9 );
                eFHs[i] = kieSession.insert( es[i] );
            }
        }

        for (int i = 0; i < loopCount; i++) {
            a.setValue( 1 );
            kieSession.update( aFH, a );
            kieSession.fireAllRules();
        }
    }
}
