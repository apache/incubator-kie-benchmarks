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

package org.drools.benchmarks.operators;

import java.util.HashSet;
import java.util.Set;

import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.benchmarks.domain.Account;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;

public class BasicOperatorsBenchmark extends AbstractBenchmark {

    private static final String RULENAME_PREFIX = "AccountBalance";

    @Param({"1", "4", "16"})
    private int rulesNr;

    @Param({"1", "4", "16"})
    private int factsNr;

    private Set<Account> accounts;

    @Setup
    public void setupKieBase() {
        StringBuilder sb = new StringBuilder();
        sb.append( "import org.drools.benchmarks.domain.*;\n" );
        int rulesNumber = rulesNr / 2;
        if (rulesNumber == 0) {
            rulesNumber = 1;
        }
        for (int i = 1; i <= rulesNumber; i = i + 2) {

            sb.append(" rule " + RULENAME_PREFIX + i + "\n" +
                    " when \n " +
                    "     $account : Account(balance > " + (i * 10000) + " || < " + ((i + 1) * 10000) + ", name == \"" + RULENAME_PREFIX + i + "\")\n " +
                    " then\n " +
                    " end\n" );

            sb.append(" rule AccountBalance" + (i + 1) + "\n" +
                    " when \n " +
                    "     $account : Account(balance >= " + ((i + 1) * 10000) + " && <= " + ((i + 2) * 10000) + ", name == \"" + RULENAME_PREFIX + (i + 1) + "\")\n " +
                    " then\n " +
                    " end\n" );
        }

        createKieBaseFromDrl(sb.toString());
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() {
        generateFacts();
        createKieSession();
    }

    @Benchmark
    public void test() {
        for (Account account : accounts) {
            kieSession.insert(account);
        }
        kieSession.fireAllRules();
    }

    private void generateFacts() {
        accounts = new HashSet<Account>();
        for (int i = 1; i <= factsNr; i++) {
            final Account account = new Account();
            account.setBalance(i * 10000);
            account.setName(RULENAME_PREFIX + i);
            accounts.add(account);
        }
    }
}
