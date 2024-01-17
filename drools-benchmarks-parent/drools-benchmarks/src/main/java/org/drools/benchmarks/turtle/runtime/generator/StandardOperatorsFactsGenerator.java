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

public class StandardOperatorsFactsGenerator extends FactsGenerator {

    public StandardOperatorsFactsGenerator(final GeneratorConfiguration config) {
        super(config);
    }

    @Override
    protected List<Object> generateMatchingFacts(final int totalNumber) {
        // generate needed number of facts
        final List<Object> facts = new ArrayList<>();
        final int nrOfFactsInInnerLoop = 13;
        final int innerLoops = (config.getNumberOfRulesInDRL() / config.getNumberOfRuleTypesInDRL());
        final int outerLoops = (totalNumber / (innerLoops * nrOfFactsInInnerLoop));
        for (int j = 0; j < outerLoops; j++) {
            for (int i = 0; i < innerLoops; i++) {
                currentLoop = i;
                // rule "CustomerIsThatOrStrange"
                Customer cust = new Customer("That" + getPlaceHolderValue("number1"), "Eidam" + getPlaceHolderValue("number1"));
                cust.setUuid("CustomerIsThatOrStrange");
                cust.setEmail("mail" + getRandomInt(1, 10000));
                facts.add(cust);

                cust = new Customer("Strange" + getPlaceHolderValue("number1"), "Eidam" + getPlaceHolderValue("number1"));
                cust.setUuid("CustomerIsThatOrStrange");
                cust.setEmail("mail" + getRandomInt(1, 10000));
                facts.add(cust);

                //////////////////////////////////////////////////
                // rule "SomeCityInCz"
                Address addr = new Address();
                addr.setCity("CityWontMatch" + getPlaceHolderValue("number1"));
                addr.setCountry(Address.Country.CZ);
                addr.setUuid("SomeCityInCz" + getPlaceHolderValue("number1"));
                facts.add(addr);

                ///////////////////////////////////////////////
                // rule "CustomerWithCertainEmail"
                cust = new Customer("That" + getRandomInt(0, 100000));
                cust.setEmail("mail" + getPlaceHolderValue("number1"));
                cust.setUuid("CustomerWithCertainEmail");
                facts.add(cust);

                ///////////////////////////////////////////////
                // rule "NonEmptyStreetInCertainCity"
                addr = new Address();
                addr.setCity("City" + getPlaceHolderValue("number1"));
                addr.setStreet("SomeStreet" + getRandomInt(0, 100000));
                addr.setUuid("NonEmptyStreetInCertainCity");
                facts.add(addr);

                ///////////////////////////////////////////////
                // rule "AccountBalance"
                Account acc = new Account();
                acc.setBalance(getRandomInt(getPlaceHolderValue("balance1"), getPlaceHolderValue("balance2")));
                acc.setUuid("AccountBalance" + getPlaceHolderValue("number1"));
                facts.add(acc);

                ///////////////////////////////////////////////
                // rule "AccountBalance2"
                acc = new Account();
                acc.setBalance(getRandomInt(getPlaceHolderValue("balance2"), getPlaceHolderValue("balance3")));
                acc.setUuid("AccountBalance2");
                facts.add(acc);

                ///////////////////////////////////////////////
                // rule "TransactionWithCertainAmount"
                Transaction tr = new Transaction();
                tr.setAmount(getRandomInt(getPlaceHolderValue("amount1"), getPlaceHolderValue("amount2")));
                tr.setUuid("TransactionWithCertainAmount" + getPlaceHolderValue("number1"));
                facts.add(tr);

                ///////////////////////////////////////////////
                // rule "TwoTransactionsHaveSameDescription"
                tr = new Transaction();
                final int value = getRandomInt(0, 100000);
                tr.setAmount(getRandomInt(1000, 100000));
                tr.setDescription("SomeSpecialDescription" + value);
                tr.setUuid("TwoTransactionsHaveSameDescription_" + getPlaceHolderValue("number1"));
                facts.add(tr);

                tr = new Transaction();
                tr.setAmount(getRandomInt(0, 1000));
                tr.setDescription("SomeSpecialDescription" + value);
                tr.setUuid("TwoTransactionsHaveSameDescription_" + getPlaceHolderValue("number1"));
                facts.add(tr);

                ///////////////////////////////////////////////
                // rule "TransactionWithCertainDescription"
                tr = new Transaction();
                tr.setDescription("evilTransaction_" + getPlaceHolderValue("number1"));
                tr.setUuid("TransactionWithCertainDescription");
                facts.add(tr);

                tr = new Transaction();
                tr.setDescription("superEvilTransaction_" + getPlaceHolderValue("number2"));
                tr.setUuid("TransactionWithCertainDescription");
                facts.add(tr);

                ///////////////////////////////////////////////
                // rule "AccountInterestRate"
                acc = new Account();
                acc.setInterestRate(getRandomInt(getPlaceHolderValue("irate1"), getPlaceHolderValue("irate2")));
                acc.setUuid("AccountInterestRate");
                facts.add(acc);

                // total of 13 facts inserted in each inner loop
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
            tr.setDescription("someGreatDescription" + getRandomInt(0, 100000));
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
