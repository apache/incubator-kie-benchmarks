/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

public class UpdatesOnJoinBenchmark extends AbstractSessionBenchmark {

    @Param({"10", "100", "1000"})
    private int loopCount;

    @Param({"1", "10", "100"})
    private int rulesNr;

    @Setup(Level.Iteration)
    @Override
    public void setup() {
        StringBuilder sb = new StringBuilder();
        sb.append( "import org.drools.benchmarks.domain.*;\n" );
        for (int i = 0; i < rulesNr; i++) {
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

    }

    @Benchmark
    public void testCreateEmptySession() {
        createKieSession();
        kieSession.fireAllRules();

        A a = new A( rulesNr + 1 );
        FactHandle aFH = kieSession.insert( a );
        B b = new B( rulesNr + 3 );
        FactHandle bFH = kieSession.insert( b );
        C c = new C( rulesNr + 5 );
        FactHandle cFH = kieSession.insert( c );
        D d = new D( rulesNr + 7 );
        FactHandle dFH = kieSession.insert( d );
        E e = new E( rulesNr + 9 );
        FactHandle eFH = kieSession.insert( e );

        for (int i = 0; i < loopCount; i++) {
            a.setValue( a.getValue() + 1 );
            kieSession.update( aFH, a );
            b.setValue( b.getValue() + 1 );
            kieSession.update( bFH, b );
            c.setValue( c.getValue() + 1 );
            kieSession.update( cFH, c );
            d.setValue( d.getValue() + 1 );
            kieSession.update( dFH, d );
            e.setValue( e.getValue() + 1 );
            kieSession.update( eFH, e );
        }

        kieSession.fireAllRules();
    }
}
