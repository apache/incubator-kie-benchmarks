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
import org.drools.benchmarks.domain.Account;
import org.drools.benchmarks.domain.CreditCard;
import org.drools.benchmarks.domain.Customer;

public class BasicWmManipulationFactsGenerator extends FactsGenerator {

    public BasicWmManipulationFactsGenerator(final GeneratorConfiguration config) {
        super(config);
    }

    @Override
    protected List<Object> generateMatchingFacts(final int totalFacts) {
        final List<Object> facts = new ArrayList<>();
        final int nrOfFactsInInnerLoop = 2;
        final int innerLoops = (config.getNumberOfRulesInDRL() / config.getNumberOfRuleTypesInDRL());
        final int outerLoops = (totalFacts / (innerLoops * nrOfFactsInInnerLoop));
        for (int j = 1; j <= outerLoops; j++) {
            for (int i = 1; i <= innerLoops; i++) {
                final Customer cust = new Customer("Delicious" + i + j, "Gorgonzola" + i + j);
                cust.setUuid("WmManipulation_" + j);
                final List<Account> accounts = new ArrayList<>();
                for (int k = 0; k < 100; k++) {
                    accounts.add(new Account("WmManipulation_" + j));
                }
                cust.setAccounts(accounts);
                facts.add(cust);

                final CreditCard card = new CreditCard();
                card.setUuid("WmManipulation_" + j);
                facts.add(card);
            }
        }
        return facts;
    }

    @Override
    protected List<Object> generateNonMatchingFacts(final int totalFacts) {
        return new ArrayList<>();
    }
}
