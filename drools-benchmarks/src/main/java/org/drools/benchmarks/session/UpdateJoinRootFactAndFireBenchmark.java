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
import org.drools.benchmarks.common.DrlProvider;
import org.drools.benchmarks.common.providers.RulesWithJoins;
import org.drools.benchmarks.domain.*;
import org.kie.api.runtime.rule.FactHandle;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;

/**
 * In this benchmark only the first rule (with higher salience) fires, and when it happens
 * its consequence changes the value of the join chain root fact so that no other rule can fire.
 * This benchmark is then intended to show the advantages of phreak's laziness that completely
 * avoids the evaluation of rules that cannot fire.
 */
public class UpdateJoinRootFactAndFireBenchmark extends AbstractBenchmark {

    @Param({"1", "10", "100"})
    private int loopCount;

    @Param({"1", "4", "16"})
    private int rulesNr;

    @Param({"1", "4", "16"})
    private int factsNr;

    @Param({"true", "false"})
    private boolean insertLastJoinItem;

    @Setup
    public void setupKieBase() {
        final StringBuilder sb = new StringBuilder();
        sb.append( "import org.drools.benchmarks.domain.*;\n" );
        sb.append( "rule R salience 10 when\n" +
                "  $factA : A( $a : value > 0)\n" +
                "  B( $b : value > $a)\n" +
                "  C( $c : value > $b)\n" +
                "  D( $d : value > $c)\n");
        if (!isSmokeTestsRun) {
            sb.append("  E( $e : value > $d)\n");
        }
        sb.append(" then\n" +
                "  modify( $factA ) { setValue(-1) }; \n" +
                " end\n" );

        final int numberOfJoins = isSmokeTestsRun ? 3 : 4;
        final DrlProvider drlProvider = new RulesWithJoins(numberOfJoins, false, false);
        sb.append(drlProvider.getDrl(rulesNr - 1));

        createKieBaseFromDrl(sb.toString());
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() {
        createKieSession();
    }

    @Benchmark
    public void test() {
        A a = new A( -1 );
        FactHandle aFH = kieSession.insert( a );
        for (int i = 0; i < factsNr; i++) {
            kieSession.insert( new B( rulesNr + 3 ) );
            kieSession.insert( new C( rulesNr + 5 ) );

            if (isSmokeTestsRun) {
                if (insertLastJoinItem) {
                    kieSession.insert( new D( rulesNr + 7 ) );
                }
            } else {
                kieSession.insert( new D( rulesNr + 7 ) );
                if (insertLastJoinItem) {
                    kieSession.insert( new E( rulesNr + 9 ) );
                }
            }
        }

        for (int i = 0; i < loopCount; i++) {
            a.setValue( 1 );
            kieSession.update( aFH, a );
            kieSession.fireAllRules();
        }
    }
}
