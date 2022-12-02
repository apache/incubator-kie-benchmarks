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
import org.drools.benchmarks.model.Account;
import org.drools.benchmarks.model.Address;
import org.drools.benchmarks.model.Customer;
import org.drools.benchmarks.model.Transaction;

public class AdvancedOperators3FactsGenerator extends FactsGenerator {

    public AdvancedOperators3FactsGenerator(final GeneratorConfiguration config) {
        super(config);
    }

    @Override
    protected List<Object> generateMatchingFacts(final int totalNumber) {
        // generate needed number of facts
        final List<Object> facts = new ArrayList<>();
        final int nrOfFactsInInnerLoop = 4;
        final int innerLoops = (config.getNumberOfRulesInDRL() / config.getNumberOfRuleTypesInDRL());
        final int outerLoops = (totalNumber / (innerLoops * nrOfFactsInInnerLoop));
        for (int j = 0; j < outerLoops; j++) {
            for (int i = 0; i < innerLoops; i++) {
                currentLoop = i;
                ///////////////////////////////////////////////////////////////
                // rule "theseIsNotSpecifiedSomeCustomer"
                final Customer cust = new Customer("Some" + getPlaceHolderValue("number1"), "Gorgonzola" + getPlaceHolderValue("number1"));
                cust.setEmail("Some_" + getRandomInt(0, 100000) + "@cheese.galaxy");
                cust.setUuid("theseIsNotSpecifiedSomeCustomer");
                facts.add(cust);

                ///////////////////////////////////////////////////////////////
                // rule "existsVerySpecificAddress"
                final Address addr = new Address();
                addr.setUuid("existsVerySpecificAddress");
                addr.setCity("Brno" + getPlaceHolderValue("number1"));
                addr.setCountry(Address.Country.CZ);
                facts.add(addr);

                ///////////////////////////////////////////////////////////////
                // rule "accountHasNumberFromSpecifiedRange_eval"
                final Account acc = new Account();
                acc.setUuid("accountHasNumberFromSpecifiedRange_eval");
                acc.setNumber(getRandomInt(getPlaceHolderValue("number1"), getPlaceHolderValue("number2")));
                facts.add(acc);

                ///////////////////////////////////////////////////////////////
                // rule "allTransactionWithStatusPendingHasAmount_forAll"
                final Transaction tr = new Transaction();
                tr.setStatus(Transaction.Status.PENDING);
                tr.setUuid("allTransactionWithStatusPendingHasAmount_forAll");
                tr.setAmount(getRandomInt(100, 10000000));
                facts.add(tr);
            }
        }
        return facts;
    }

    @Override
    protected List<Object> generateNonMatchingFacts(final int totalNumber) {
        // generate some facts that will not match on generated rules
        final List<Object> facts = new ArrayList<Object>();
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
