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

package org.drools.benchmarks.turtle.runtime.generator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.drools.benchmarks.common.model.Account;
import org.drools.benchmarks.common.model.Address;
import org.drools.benchmarks.common.model.Customer;
import org.drools.benchmarks.common.model.Transaction;

public class AdvancedOperators2FactsGenerator extends FactsGenerator {

    public AdvancedOperators2FactsGenerator(final GeneratorConfiguration config) {
        super(config);
    }

    @Override
    protected List<Object> generateMatchingFacts(final int totalNumber) {
        // generate needed number of facts
        final List<Object> facts = new ArrayList<>();
        final int nrOfFactsInInnerLoop = 8;
        final int innerLoops = (config.getNumberOfRulesInDRL() / config.getNumberOfRuleTypesInDRL());
        final int outerLoops = (totalNumber / (innerLoops * nrOfFactsInInnerLoop));
        for (int j = 0; j < outerLoops; j++) {
            for (int i = 0; i < innerLoops; i++) {
                currentLoop = i;
                ///////////////////////////////////////////////////////////////
                // rule "twoCustomersWithSameNameInfixAnd"
                Customer cust = new Customer("Johnny" + getPlaceHolderValue("number1"), "Cheddar" + getRandomInt(0, 1000000));
                cust.setUuid("twoCustomersWithSameNameInfixAnd");
                final int randomInt = getRandomInt(0, 1000000);
                cust.setEmail("someCoolMail" + randomInt + "@somewhere.com");
                facts.add(cust);
                cust = new Customer("Johnny" + getPlaceHolderValue("number1"), "Cheddar" + getRandomInt(0, 1000000));
                cust.setUuid("twoCustomersWithSameNameInfixAnd");
                cust.setEmail("someCoolMail" + randomInt + "@somewhere.com");
                facts.add(cust);

                ///////////////////////////////////////////////////////////////
                // rule "accountWithNumberGreaterThanOrLessThanInfixOr"
                Account acc = new Account();
                acc.setNumber(getRandomInt(getPlaceHolderValue("number1"), getPlaceHolderValue("number2")));
                acc.setName("accountName" + randomInt);
                acc.setUuid("accountWithNumberGreaterThanOrLessThanInfixOr");
                acc.setInterestRate((double) getRandomInt(1000000, 2000000));
                facts.add(acc);
                acc = new Account();
                acc.setNumber(getRandomInt(getPlaceHolderValue("number1"), getPlaceHolderValue("number2")));
                acc.setName("accountName" + randomInt);
                acc.setUuid("accountWithNumberGreaterThanOrLessThanInfixOr");
                acc.setInterestRate((double) getRandomInt(1000000, 2000000));
                facts.add(acc);

                ///////////////////////////////////////////////////////////////
                // rule "transactionWithAmountGreaterThanOrLessThanPrefixOr"
                Transaction tr = new Transaction();
                tr.setUuid("transactionWithAmountGreaterThanOrLessThanPrefixOr" + getPlaceHolderValue("number1"));
                tr.setAmount(getRandomInt(getPlaceHolderValue("number1"), getPlaceHolderValue("number2")));
                tr.setDescription("someUsefulTransDescription" + randomInt);
                facts.add(tr);
                tr = new Transaction();
                tr.setUuid("transactionWithAmountGreaterThanOrLessThanPrefixOr" + getPlaceHolderValue("number1"));
                tr.setAmount(getRandomInt(getPlaceHolderValue("number1"), getPlaceHolderValue("number2")));
                tr.setDescription("someUsefulTransDescription" + randomInt);
                facts.add(tr);

                ///////////////////////////////////////////////////////////////
                // rule "transactionWithOneOfSpecifiedDescriptions"
                tr = new Transaction();
                final String[] descriptions = new String[]{"good", "great", "super"};
                tr.setDescription(descriptions[getRandomInt(0, 2)] + getPlaceHolderValue("number1"));
                tr.setUuid("transactionWithOneOfSpecifiedDescriptions");
                facts.add(tr);

                ///////////////////////////////////////////////////////////////
                // rule "cityIsBrnoOstravaPrahaZlin"
                final Address addr = new Address();
                final String[] cities = new String[]{"Brno", "Ostrava", "Praha", "Zlin"};
                addr.setCity(cities[getRandomInt(0, 2)] + getPlaceHolderValue("number1"));
                addr.setUuid("cityIsBrnoOstravaPrahaZlin");
                facts.add(addr);
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
            final Customer cust = new Customer("Mario", "Italy" + getRandomInt(0, 100000));
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

