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
import org.drools.benchmarks.common.providers.RulesWithJoinsProvider;
import org.drools.benchmarks.model.A;
import org.drools.benchmarks.model.B;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;

/**
 * Inserts root fact and tip node fact only for first rule.
 * When firing, other rules are fired in a chain. First rule, modifies root
 * fact so next rule fires, which modifies root fact so next rule fires, etc.
 */
public class InsertChainedFireBenchmark extends AbstractBenchmark {

    @Param({"12", "48", "192", "768"})
    private int rulesNr;

    @Setup
    public void setupKieBase() {
        final String consequence = "modify($a) {setValue($a.getValue() + 1)};";
        final DrlProvider drlProvider = new RulesWithJoinsProvider( 1, false, true, false, "", consequence, " == ", ">");
        createKieBaseFromDrl(drlProvider.getDrl(rulesNr));
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() {
        createKieSession();
        // Partial match for join nodes
        kieSession.insert(new B(rulesNr + 2));
        // Match for first rule
        kieSession.insert(new A(0));
    }

    @Benchmark
    public int test() {
        return kieSession.fireAllRules();
    }
}