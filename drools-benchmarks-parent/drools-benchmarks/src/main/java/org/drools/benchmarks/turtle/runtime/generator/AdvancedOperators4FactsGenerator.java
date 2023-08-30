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

package org.drools.benchmarks.turtle.runtime.generator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.drools.benchmarks.common.model.Account;
import org.drools.benchmarks.common.model.Address;
import org.drools.benchmarks.common.model.Customer;
import org.drools.benchmarks.common.model.Transaction;

public class AdvancedOperators4FactsGenerator extends FactsGenerator {

    public AdvancedOperators4FactsGenerator(final GeneratorConfiguration config) {
        super(config);
    }

    @Override
    protected List<Object> generateMatchingFacts(final int totalNumber) {
        // generate needed number of facts
        final List<Object> facts = new ArrayList<>();
        final int nrOfFactsInInnerLoop = 7;
        final int innerLoops = (config.getNumberOfRulesInDRL() / config.getNumberOfRuleTypesInDRL());
        final int outerLoops = (totalNumber / (innerLoops * nrOfFactsInInnerLoop));
        for (int j = 0; j < outerLoops; j++) {
            for (int i = 0; i < innerLoops; i++) {
                currentLoop = i;
                ///////////////////////////////////////////////////////////////
                //rule "transactionsWithCertainAccountFrom_collect"
                Account acc = new Account();
                acc.setUuid("transactionsWithCertainAccountFrom_collect_" + getPlaceHolderValue("number1"));
                acc.setName("name" + getRandomInt(1000, 100000000));
                facts.add(acc);

                Transaction tr = new Transaction();
                tr.setAccountFrom(acc);
                tr.setDescription("transactionDesc" + getRandomInt(1000, 1000000));
                facts.add(tr);

                tr = new Transaction();
                tr.setAccountFrom(acc);
                tr.setDescription("transactionDesc" + getRandomInt(1000, 1000000));
                facts.add(tr);

                tr = new Transaction();
                tr.setAccountFrom(acc);
                tr.setDescription("transactionDesc" + getRandomInt(1000, 1000000));
                facts.add(tr);

                ///////////////////////////////////////////////////////////////
                // same facts for all accumulate rules
                acc = new Account();
                acc.setUuid("totalAverageMinMaxlBalanceForCertainAccounts_accumulate_" + getPlaceHolderValue("number1"));
                acc.setBalance(getRandomInt(1, 10000000));
                facts.add(acc);

                acc = new Account();
                acc.setUuid("totalAverageMinMaxlBalanceForCertainAccounts_accumulate_" + getPlaceHolderValue("number1"));
                acc.setBalance(getRandomInt(1, 10000000));
                facts.add(acc);

                acc = new Account();
                acc.setUuid("totalAverageMinMaxlBalanceForCertainAccounts_accumulate_" + getPlaceHolderValue("number1"));
                acc.setBalance(getRandomInt(1, 10000000));
                facts.add(acc);
            }
        }
        return facts;
    }

    @Override
    protected List<Object> generateNonMatchingFacts(final int totalNumber) {
        // generate some facts that will not match on generated rules
        final List<Object> facts = new ArrayList<>();
        final int nrOfFactsInLoop = 4;
        final int loops = (totalNumber / nrOfFactsInLoop);
        for (int i = 0; i < loops; i++) {
            final Customer cust = new Customer("Delicious", "Gorgonzola" + getRandomInt(0, 100000));
            cust.setEmail("someEmail" + getRandomInt(0, 100000));
            facts.add(cust);

            final Address addr = new Address();
            addr.setCity("SomeNonMatchingCity" + i);
            addr.setUuid("SuperCoolUuid");
            facts.add(addr);

            final Transaction tr = new Transaction();
            tr.setStatus(Transaction.Status.PENDING);
            tr.setDescription("BadDescription" + getRandomInt(0, 100000));
            facts.add(tr);

            final Account acc = new Account();
            acc.setOwner(cust);
            acc.setStartDate(new Date(getRandomInt(0, 10000000)));
            facts.add(acc);

            // total of 4 facts inserted in each loop
        }
        return facts;
    }

}
