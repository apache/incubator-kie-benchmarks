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
public abstract class AbstractOperatorsBenchmark extends AbstractBenchmark {

    protected static final String RULENAME_PREFIX = "AccountBalance";

    @Param({"2", "8", "32"})
    protected int rulesAndFactsNumber;

    private Set<Account> accounts;

    @Setup
    public void generateFacts() {
        accounts = new HashSet<>();
        for (int i = 1; i <= rulesAndFactsNumber; i++) {
            final Account account = new Account();
            account.setBalance(i * 10000);
            account.setName(RULENAME_PREFIX + i);
            accounts.add(account);
        }
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() {
        createKieSession();
    }

    protected int runBenchmark(final Blackhole eater) {
        for (Account account : accounts) {
            eater.consume(kieSession.insert(account));
        }
        return kieSession.fireAllRules();
    }
}
