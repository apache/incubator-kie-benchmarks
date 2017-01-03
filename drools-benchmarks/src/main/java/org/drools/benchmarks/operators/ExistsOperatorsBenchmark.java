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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.benchmarks.domain.Account;
import org.kie.internal.builder.conf.RuleEngineOption;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 20, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class ExistsOperatorsBenchmark extends AbstractBenchmark {

    private static final String RULENAME_PREFIX = "AccountBalance";

    @Param({"128"})
    private int rulesAndFactsNumber;

    private Set<Account> accounts;

    @Setup
    public void setupKieBase() {
        StringBuilder sb = new StringBuilder();
        sb.append( "import org.drools.benchmarks.domain.*;\n" );
        int rulesNumber = rulesAndFactsNumber / 2;
        if (rulesNumber == 0) {
            rulesNumber = 1;
        }
        for (int i = 1; i <= rulesNumber; i = i + 2) {

            sb.append(" rule " + RULENAME_PREFIX + i + "\n" +
                    " when \n " +
                    "     exists(Account(balance > " + (i * 10000) + " || < " + ((i + 1) * 10000) + ", name == \"" + RULENAME_PREFIX + i + "\"))\n " +
                    " then\n " +
                    " end\n" );

            sb.append(" rule AccountBalance" + (i + 1) + "\n" +
                    " when \n " +
                    "     exists(Account(balance >= " + ((i + 1) * 10000) + " && <= " + ((i + 2) * 10000) + ", name == \"" + RULENAME_PREFIX + (i + 1) + "\"))\n " +
                    " then\n " +
                    " end\n" );
        }

        createKieBaseFromDrl(sb.toString(), RuleEngineOption.PHREAK);
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() {
        generateFacts();
    }

    @Benchmark
    public int test(final Blackhole eater) {
        createKieSession();
        try {
            for (Account account : accounts) {
                eater.consume(kieSession.insert(account));
            }
            return kieSession.fireAllRules();
        } finally {
            kieSession.dispose();
        }
    }

    private void generateFacts() {
        accounts = new HashSet<Account>();
        for (int i = 1; i <= rulesAndFactsNumber; i++) {
            final Account account = new Account();
            account.setBalance(i * 10000);
            account.setName(RULENAME_PREFIX + i);
            accounts.add(account);
        }
    }
}
