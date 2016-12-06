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

import java.util.concurrent.TimeUnit;
import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.benchmarks.common.DrlProvider;
import org.drools.benchmarks.common.providers.RulesWithJoinsProvider;
import org.drools.benchmarks.common.util.TestUtil;
import org.drools.benchmarks.domain.A;
import org.kie.api.runtime.rule.FactHandle;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Initially all rules are totally linked, so the remove/fire/insert/fire loop causes the
 * linking/unlinking of the whole rule.
 */
@Warmup(iterations = 300)
@Measurement(iterations = 400)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class RuleUnlinkingBenchmark extends AbstractBenchmark {

    @Param({"1", "10", "100"})
    private int loopCount;

    @Param({"1", "4", "16"})
    private int rulesNr;

    @Param({"1", "4", "16"})
    private int factsNr;

    @Param({"1", "2", "3"})
    private int joinsNr;

    private FactHandle aFH;

    @Setup
    public void setupKieBase() {
        final DrlProvider drlProvider = new RulesWithJoinsProvider(joinsNr, false, true);
        createKieBaseFromDrl( drlProvider.getDrl(rulesNr) );
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() {
        createKieSession();

        aFH = kieSession.insert( new A( rulesNr + 1 ) );
        for ( int i = 0; i < factsNr; i++ ) {
            TestUtil.insertJoinedFactsToSession(kieSession, joinsNr, rulesNr);
        }

        kieSession.fireAllRules();
    }

    @Benchmark
    public void test(final Blackhole eater) {
        for (int i = 0; i < loopCount; i++) {
            kieSession.delete( aFH );
            eater.consume(kieSession.fireAllRules());
            aFH = kieSession.insert( new A( rulesNr + 1 ) );
            eater.consume(kieSession.fireAllRules());
        }
    }
}
