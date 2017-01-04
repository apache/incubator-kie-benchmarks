/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.benchmarks.operators;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.infra.Blackhole;

public class EvalBenchmark extends AbstractOperatorsBenchmark {

    @Setup
    public void setupKieBase() {
        StringBuilder sb = new StringBuilder();
        sb.append("import org.drools.benchmarks.domain.*;\n");
        for (int i = 1; i <= rulesAndFactsNumber; i++) {
            sb.append(" rule " + RULENAME_PREFIX + i + "\n" +
                    " when \n " +
                    "     $acc: Account(balance == " + (i * 10000) + ")\n " +
                    "     eval($acc.getBalance() == " + (i * 10000) + ") \n" +
                    " then\n " +
                    " end\n");
        }

        createKieBaseFromDrl(sb.toString());
    }

    @Benchmark
    public int test(final Blackhole eater) {
        return runBenchmark(eater);
    }
}
