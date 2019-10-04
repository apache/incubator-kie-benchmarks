package org.drools.benchmarks.turtle.runtime.generator;

import org.drools.benchmarks.model.Account;
import org.drools.benchmarks.model.Address;
import org.drools.benchmarks.model.Customer;

import java.util.ArrayList;
import java.util.List;

public class KBaseCreationFromDTable2Generator extends FactsGenerator {

    private static int from = 10000;
    private static int to = 11490;

    public KBaseCreationFromDTable2Generator(final GeneratorConfiguration config) {
        super(config);
    }

    private Account getAccount(int number){
        Account account = new Account();
        account.setNumber(number);

        return account;
    }

    private Customer getCustomer(String planet) {
        Address address = new Address();
        address.setPlanet(planet);
        Customer customer = new Customer();
        customer.setAddress(address);

        return customer;
    }

    @Override
    protected List<Object> generateMatchingFacts(int number) {
        // generate needed number of facts
        final List<Object> facts = new ArrayList<>();
        int currentGroup = 1;
        for (int i = from; i < to; i += config.getNumberOfRuleTypesInDRL()) {
            int accountNumber = i;

            facts.add(getAccount(accountNumber));
            facts.add(getAccount(accountNumber++));
            facts.add(getCustomer("DeathStar"+currentGroup));
            facts.add(getAccount(accountNumber++));
            facts.add(getCustomer("Naboo"+currentGroup));
            facts.add(getAccount(accountNumber++));
            facts.add(getCustomer("Tatoooine"+currentGroup));
            facts.add(getAccount(accountNumber++));
            facts.add(getCustomer("Kamino"+currentGroup));

            currentGroup++;
        }
        return facts;
    }

    @Override
    protected List<Object> generateNonMatchingFacts(int number) {
        // generate some facts that will not match on generated rules
        final List<Object> facts = new ArrayList<>();
        for (int i = to + 1; i < to + 298; i++) {
            currentLoop = i;

            facts.add(getAccount(currentLoop));
            facts.add(getCustomer("Coruscant"+currentLoop));
        }
        return facts;
    }
}
