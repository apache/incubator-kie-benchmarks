/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.drools.benchmarks.operators;

import java.util.HashSet;
import java.util.Set;

import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.benchmarks.common.model.Account;
import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.drools.benchmarks.common.util.RuntimeUtil;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.infra.Blackhole;

public class MatchesBenchmark extends AbstractBenchmark {

    @Param({"16", "32"})
    private int _rulesNumber;

    @Param({"32", "256"})
    private int _factsNumber;

    @Param({"true", "false"})
    private boolean cacheEnabled;

    private Set<Account> accounts;

    @Setup
    public void setupKieBase() {

        if (cacheEnabled) {
            System.setProperty("drools.matches.compiled.cache.count", "100");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("import " + Account.class.getCanonicalName() + ";\n");

        for (int i = 1; i <= _rulesNumber; i++) {

            sb.append(" rule NameMatches" + i + "\n" +
                    " when \n " +
                    "     $account : Account(name matches \"A.*t" + i + "\")\n " +
                    " then\n " +
                    " end\n" );
        }

        // use canonical model
        kieBase = BuildtimeUtil.createKieBaseFromDrl(true, sb.toString());
    }

    @Setup
    public void generateFacts() {
        accounts = new HashSet<>();
        for (int i = 1; i <= _factsNumber; i++) {
            final Account account = new Account();
            account.setName("Account" + i);
            accounts.add(account);
        }
    }

    @Setup(Level.Iteration)
    public void setup() {
        kieSession = RuntimeUtil.createKieSession(kieBase);
    }

    @Benchmark
    public int test(final Blackhole eater) {
        for (Account account : accounts) {
            eater.consume(kieSession.insert(account));
        }
        return kieSession.fireAllRules();
    }
}
