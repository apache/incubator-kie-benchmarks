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
import java.util.concurrent.TimeUnit;

import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.drools.benchmarks.common.util.RuntimeUtil;
import org.drools.benchmarks.common.model.Account;
import org.kie.internal.conf.AlphaRangeIndexThresholdOption;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@Warmup(iterations = 100000)
@Measurement(iterations = 10000)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class AlphaNodeRangeIndexingBenchmark extends AbstractBenchmark {

    protected static final String RULENAME_PREFIX = "AccountBalance";

    @Param({"12", "24", "48"})
    protected int rulesAndFactsNumber;

    @Param({"false", "true"})
    protected boolean rangeIndexingEnabled;

    private Set<Account> accounts;

    @Setup
    public void setupKieBase() {

        StringBuilder sb = new StringBuilder();
        sb.append("import " + Account.class.getCanonicalName() + ";\n");
        for (int i = 1; i <= rulesAndFactsNumber; i++) {
            sb.append(" rule " + RULENAME_PREFIX + i + "\n" +
                    " when \n " +
                    "     $account : Account(balance >= " + (i * 10000) + ")\n " +
                    " then\n " +
                    " end\n" );
        }

        if (rangeIndexingEnabled) {
            kieBase = BuildtimeUtil.createKieBaseFromDrl(sb.toString()); // AlphaRangeIndexThresholdOption.DEFAULT_VALUE = 9
        } else {
            kieBase = BuildtimeUtil.createKieBaseFromDrl(sb.toString(), AlphaRangeIndexThresholdOption.get(0));
        }
    }

    @Setup
    public void generateFacts() {
        accounts = new HashSet<>();
        for (int i = 1; i <= rulesAndFactsNumber; i++) {
            final Account account = new Account();
            account.setBalance(i * 10000 + 5000d); // not boundary value
            account.setName(RULENAME_PREFIX + i);
            accounts.add(account);
        }
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() {
        kieSession = RuntimeUtil.createKieSession(kieBase);
    }

    @Benchmark
    public int test(final Blackhole eater) {
        for (Account account : accounts) {
            kieSession.insert(account);
        }
        return  kieSession.fireAllRules();
    }
}
