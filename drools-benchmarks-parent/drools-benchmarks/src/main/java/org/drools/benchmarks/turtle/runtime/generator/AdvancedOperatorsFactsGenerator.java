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

public class AdvancedOperatorsFactsGenerator extends FactsGenerator {

    public AdvancedOperatorsFactsGenerator(final GeneratorConfiguration config) {
        super(config);
    }

    @Override
    protected List<Object> generateMatchingFacts(final int totalNumber) {
        // generate needed number of facts
        final List<Object> facts = new ArrayList<>();
        final int nrOfFactsInInnerLoop = 12;
        final int innerLoops = (config.getNumberOfRulesInDRL() / config.getNumberOfRuleTypesInDRL());
        final int outerLoops = (totalNumber / (innerLoops * nrOfFactsInInnerLoop));
        for (int j = 0; j < outerLoops; j++) {
            for (int i = 0; i < innerLoops; i++) {
                currentLoop = i;
                // rule "customerHasSpecificAccount"
                Customer cust = new Customer("That" + getRandomInt(0, 100000), "Eidam");
                cust.setUuid("customerHasSpecifiedAccount");
                cust.setEmail("mail" + getRandomInt(1, 100000));

                Account acc = new Account();
                acc.setNumber(getRandomInt(getPlaceHolderValue("number1"), getPlaceHolderValue("number2")));
                acc.setUuid("customerHasSpecifiedAccount");
                acc.setOwner(cust);

                List<Account> accounts = new ArrayList<>();
                accounts.add(acc);
                for (int k = 0; k < 5; k++) {
                    final Account otherAcc = new Account();
                    otherAcc.setNumber(getRandomInt(0, 100000));
                    otherAcc.setUuid("SomeOtherUuid" + getRandomInt(0, 100000));
                    acc.setOwner(cust);
                    accounts.add(otherAcc);
                }
                cust.setAccounts(accounts);
                facts.add(acc);
                facts.add(cust);

                //////////////////////////////////////////////////
                // rule "customerDoesNotHaveSpecifiedAccount"
                cust = new Customer("Delicious" + getRandomInt(0, 100000), "Gorgonzola");
                cust.setUuid("customerDoesNotHaveSpecifiedAccount");
                cust.setEmail("mail" + getRandomInt(1, 100000));

                acc = new Account();
                acc.setNumber(getRandomInt(getPlaceHolderValue("number3"), getPlaceHolderValue("number4")));
                acc.setUuid("customerDoesNotHaveSpecifiedAccount");
                acc.setOwner(cust);

                Account acc1 = new Account();
                acc1.setNumber(getRandomInt(0, 100000));
                acc1.setOwner(cust);

                accounts = new ArrayList<>();
                accounts.add(acc1);
                for (int k = 0; k < 5; k++) {
                    final Account otherAcc = new Account();
                    otherAcc.setNumber(getRandomInt(0, 100000));
                    otherAcc.setUuid("SomeOtherUuid" + getRandomInt(0, 100000));
                    acc.setOwner(cust);
                    accounts.add(otherAcc);
                }
                cust.setAccounts(accounts);
                facts.add(acc);
                facts.add(cust);
                facts.add(acc1);

                /////////////////////////////////////////////
                // rule "memberOfCustomersAccounts"
                cust = new Customer("That" + i + getRandomInt(0, 100000));
                cust.setUuid("memberOfCustomersAccounts");

                acc = new Account();
                acc.setUuid("memberOfCustomersAccounts");
                acc.setBalance(getRandomInt(getPlaceHolderValue("balance1"), getPlaceHolderValue("balance2")));
                acc.setNumber(getRandomInt(0, 100000));
                acc.setOwner(cust);

                accounts = new ArrayList<>();
                for (int k = 0; k < 5; k++) {
                    final Account otherAcc = new Account();
                    otherAcc.setNumber(getRandomInt(0, 100000));
                    otherAcc.setUuid("SomeOtherUuid" + getRandomInt(0, 100000));
                    otherAcc.setOwner(cust);
                    accounts.add(otherAcc);
                }
                accounts.add(acc);
                cust.setAccounts(accounts);
                facts.add(acc);
                facts.add(cust);

                /////////////////////////////////////////////
                // rule "notMemberOfCustomersAccounts"
                cust = new Customer("Strange" + i + getRandomInt(0, 100000));
                cust.setUuid("notMemberOfCustomersAccounts");

                acc = new Account();
                acc.setUuid("notMemberOfCustomersAccounts");
                acc.setBalance(getRandomInt(getPlaceHolderValue("balance3"), getPlaceHolderValue("balance4")));
                acc.setNumber(getRandomInt(0, 100000));
                acc.setOwner(cust);

                acc1 = new Account();
                acc1.setNumber(getRandomInt(0, 1000000));

                accounts = new ArrayList<>();
                for (int k = 0; k < 5; k++) {
                    final Account otherAcc = new Account();
                    otherAcc.setNumber(getRandomInt(0, 100000));
                    otherAcc.setUuid("SomeOtherUuid" + getRandomInt(0, 100000));
                    otherAcc.setOwner(cust);
                    accounts.add(otherAcc);
                }
                accounts.add(acc1);
                cust.setAccounts(accounts);
                facts.add(acc);
                facts.add(acc1);
                facts.add(cust);

                /////////////////////////////////////////////
                // rule "matchesCityWithPrefixBr"
                final Address addr = new Address();
                addr.setUuid("matchesCityWithPrefixBr" + getPlaceHolderValue("number1"));
                addr.setPostalCode(String.valueOf(getRandomInt(0, 1000000)));
                addr.setCity("Br" + getPlaceHolderValue("number1") + getRandomInt(0, 1000000));
                facts.add(addr);

                /////////////////////////////////////////////
                // rule "notMatchesTransactionDescription"
                final Transaction tr = new Transaction();
                tr.setDescription("BadDescriptionNotMatch" + getPlaceHolderValue("number2") + "someOtherStaff");
                tr.setUuid("notMatchesTransactionDescription" + getPlaceHolderValue("number2"));
                facts.add(tr);
                // total of 32 facts inserted in each inner loop
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
