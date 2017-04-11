/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
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
import org.drools.benchmarks.common.providers.RulesWithJoinsTreeProvider;
import org.drools.benchmarks.model.A;
import org.drools.benchmarks.model.B;
import org.drools.benchmarks.model.C;
import org.drools.benchmarks.model.D;
import org.drools.benchmarks.model.E;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;

/**
 * Two rules, both match in the beginning. During fire, first rule modifies
 * knowledge so the second rule doesn't fire.
 */
public class InsertFireCancelSecondRuleBenchmark extends AbstractBenchmark {

    @Param({"1", "2", "3", "4"})
    private int joinsNr;

    @Param({"1", "4", "16"})
    private int factsNr;

    private int numberOfRules = 2;

    @Setup
    public void setupKieBase() {
        final DrlProvider drlProvider = new RulesWithJoinsTreeProvider(joinsNr, true, true,
                "", "modify($a) {setValue(-1)};");
        createKieBaseFromDrl(drlProvider.getDrl(numberOfRules));
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() {
        createKieSession();
        kieSession.insert( new A( 0) );
        for ( int i = 0; i < factsNr; i++ ) {
            // All fact types must be inserted because each rule contains different type joined.
            kieSession.insert( new B( numberOfRules + 3 ) );
            kieSession.insert( new C( numberOfRules + 4 ) );
            kieSession.insert( new D( numberOfRules + 5 ) );
            kieSession.insert( new E( numberOfRules + 6 ) );
        }
    }

    @Benchmark
    public int test() {
        return kieSession.fireAllRules();
    }
}