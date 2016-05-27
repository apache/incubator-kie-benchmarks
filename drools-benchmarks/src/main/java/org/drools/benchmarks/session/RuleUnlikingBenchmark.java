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
import org.drools.benchmarks.domain.A;
import org.drools.benchmarks.domain.B;
import org.kie.api.runtime.rule.FactHandle;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;

/**
 * Initially all rules are totally linked, so the remove/fire/insert/fire loop causes the
 * linking/unlinking of the whole rule.
 */
public class RuleUnlikingBenchmark extends AbstractBenchmark {

    @Param({"1", "10", "100"})
    private int loopCount;

    @Param({"1", "4", "16"})
    private int rulesNr;

    @Param({"1", "4", "16"})
    private int factsNr;

    private FactHandle aFH;

    @Setup
    public void setupKieBase() {
        final DrlProvider drlProvider = new RulesWithJoins(1, false, true);
        createKieBaseFromDrl( drlProvider.getDrl(rulesNr) );
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() {
        createKieSession();

        aFH = kieSession.insert( new A( rulesNr + 1 ) );
        for ( int i = 0; i < factsNr; i++ ) {
            kieSession.insert( new B( rulesNr + 3 ) );
        }

        kieSession.fireAllRules();
    }

    @Benchmark
    public void test() {
        for (int i = 0; i < loopCount; i++) {
            kieSession.delete( aFH );
            kieSession.fireAllRules();
            aFH = kieSession.insert( new A( rulesNr + 1 ) );
            kieSession.fireAllRules();
        }
    }
}
