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
import java.util.List;
import org.drools.benchmarks.model.Account;
import org.drools.benchmarks.model.Address;
import org.drools.benchmarks.model.CreditCard;
import org.drools.benchmarks.model.Customer;
import org.drools.benchmarks.model.Transaction;

public class BasicRetractFactsFromWmFactsGenerator extends FactsGenerator {

    public BasicRetractFactsFromWmFactsGenerator(final GeneratorConfiguration config) {
        super(config);
    }

    @Override
    protected List<Object> generateMatchingFacts(final int totalFacts) {
        final List<Object> facts = new ArrayList<>();
        final int nrOfFactsInInnerLoop = 5;
        final int innerLoops = (config.getNumberOfRulesInDRL() / config.getNumberOfRuleTypesInDRL());
        final int outerLoops = (totalFacts / (innerLoops * nrOfFactsInInnerLoop));
        for (int j = 1; j <= outerLoops; j++) {
            for (int i = 1; i <= innerLoops; i++) {
                final Address addr = new Address("retractAddress_" + j);
                addr.setCity("SomeCity" + i + j);
                facts.add(addr);

                final Customer cust = new Customer("Delilcious" + i + j);
                cust.setUuid("retractCustomer_" + j);
                facts.add(cust);

                final Account acc = new Account("retractAccount_" + j);
                acc.setType(Account.AccountType.SAVINGS);
                facts.add(acc);

                final Transaction trans = new Transaction("retractTransaction_" + j);
                trans.setAccountFrom(new Account());
                facts.add(trans);

                final CreditCard card = new CreditCard("retractCreditCard_" + j);
                card.setOwner(cust);
                facts.add(card);
            }
        }
        return facts;
    }

    @Override
    protected List<Object> generateNonMatchingFacts(int totalFacts) {
        return new ArrayList<>();
    }

}
