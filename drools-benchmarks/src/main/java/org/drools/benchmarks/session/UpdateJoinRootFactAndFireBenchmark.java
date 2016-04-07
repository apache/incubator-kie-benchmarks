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
import org.drools.benchmarks.util.TestUtil;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.utils.KieHelper;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;

public class UpdateJoinRootFactAndFireBenchmark extends AbstractSessionBenchmark {

    @Param({"10", "100", "1000"})
    private int loopCount;

    @Param({"1", "10", "100"})
    private int rulesNr;

    @Setup(Level.Iteration)
    @Override
    public void setup() {
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

        String drl = sb.toString();

        kieBase = new KieHelper().addContent( drl, ResourceType.DRL )
                                 .build( TestUtil.getKieBaseConfiguration() );

        createKieSession();
    }

    @Benchmark
    public void testCreateEmptySession() {
        A a = new A( -1 );
        FactHandle aFH = kieSession.insert( a );
        B b = new B( 3 );
        FactHandle bFH = kieSession.insert( b );
        C c = new C( 5 );
        FactHandle cFH = kieSession.insert( c );
        D d = new D( 7 );
        FactHandle dFH = kieSession.insert( d );
        E e = new E( 9 );
        FactHandle eFH = kieSession.insert( e );

        for (int i = 0; i < loopCount; i++) {
            a.setValue( 1 );
            kieSession.update( aFH, a );
            kieSession.fireAllRules();
        }
    }
}
