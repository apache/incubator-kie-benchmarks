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

import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.infra.Blackhole;

public class ExistsBenchmark extends AbstractOperatorsBenchmark {

    @Setup
    public void setupKieBase() {
        StringBuilder sb = new StringBuilder();
        sb.append("import org.drools.benchmarks.model.*;\n");
        int rulesNumber = rulesAndFactsNumber / 2;
        if (rulesNumber == 0) {
            rulesNumber = 1;
        }
        for (int i = 1; i <= rulesNumber; i = i + 2) {

            sb.append(" rule " + RULENAME_PREFIX + i + "\n" +
                    " when \n " +
                    "     exists(Account(balance > " + (i * 10000) + " || < " + ((i + 1) * 10000) + ", name == \"" + RULENAME_PREFIX + i + "\"))\n " +
                    " then\n " +
                    " end\n");

            sb.append(" rule AccountBalance" + (i + 1) + "\n" +
                    " when \n " +
                    "     exists(Account(balance >= " + ((i + 1) * 10000) + " && <= " + ((i + 2) * 10000) + ", name == \"" + RULENAME_PREFIX + (i + 1) + "\"))\n " +
                    " then\n " +
                    " end\n");
        }

        kieBase = BuildtimeUtil.createKieBaseFromDrl(sb.toString());
    }

    @Benchmark
    public int test(final Blackhole eater) {
        return runBenchmark(eater);
    }
}
