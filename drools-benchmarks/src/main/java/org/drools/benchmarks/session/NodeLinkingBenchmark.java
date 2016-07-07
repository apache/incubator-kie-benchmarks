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
import org.drools.benchmarks.common.providers.RulesWithJoinsProvider;
import org.drools.benchmarks.domain.B;
import org.kie.api.runtime.rule.FactHandle;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.infra.Blackhole;

/**
 * This benchmark start from an empty working memory and inserts a single fact in an
 * insert/fire/delete/fire loop, so this is intended to measure the cost of the
 * linking/unlinking of a single node.
 */
public class NodeLinkingBenchmark extends AbstractBenchmark {

    @Param({"1", "10", "100"})
    private int loopCount;

    @Param({"1", "4", "16"})
    private int rulesNr;

    @Param({"1", "4", "16"})
    private int factsNr;

    @Setup
    public void setupKieBase() {
        final DrlProvider drlProvider = new RulesWithJoinsProvider(4, false, true);
        createKieBaseFromDrl( drlProvider.getDrl(rulesNr) );
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() {
        createKieSession();
    }

    @Benchmark
    public void test(final Blackhole eater) {
        for (int i = 0; i < loopCount; i++) {
            FactHandle bFH = kieSession.insert( new B( 1 ) );
            eater.consume(kieSession.fireAllRules());
            kieSession.delete( bFH );
            eater.consume(kieSession.fireAllRules());
        }
    }
}
