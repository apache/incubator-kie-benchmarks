package org.drools.benchmarks.turtle.runtime.generator;

import org.drools.benchmarks.model.Account;
import org.drools.benchmarks.model.Address;
import org.drools.benchmarks.model.Customer;

import java.util.ArrayList;
import java.util.List;

public class KBaseCreationFromDTable2Generator extends FactsGenerator {

    private static int from = 10000;
    private static int to = 11486;

    public KBaseCreationFromDTable2Generator(final GeneratorConfiguration config) {
        super(config);
    }

    @Override
    protected List<Object> generateMatchingFacts(int number) {
        // generate needed number of facts
        final List<Object> facts = new ArrayList<>();
        for (int i = from; i < to; i += config.getNumberOfRuleTypesInDRL()) {
            currentLoop = i;
            int currentRow = 0;

            Account account = new Account();
            account.setNumber(currentLoop + currentRow);
            facts.add(account);
            currentRow++;

            Address address = new Address();
            address.setCountry(Address.Country.US);
            List<Account> accounts = new ArrayList<>();
            account = new Account();
            account.setNumber(currentLoop + currentRow);
            accounts.add(account);
            Customer customer = new Customer();
            customer.setAddress(address);
            customer.setAccounts(accounts);
            facts.add(customer);
            currentRow++;

            address = new Address();
            address.setCountry(Address.Country.BR);
            accounts.clear();
            accounts = new ArrayList<>();
            account = new Account();
            account.setNumber(currentLoop + currentRow);
            accounts.add(account);
            customer = new Customer();
            customer.setAddress(address);
            customer.setAccounts(accounts);
            facts.add(customer);
            currentRow++;

            address = new Address();
            address.setCountry(Address.Country.SK);
            accounts.clear();
            accounts = new ArrayList<>();
            account = new Account();
            account.setNumber(currentLoop + currentRow);
            accounts.add(account);
            customer = new Customer();
            customer.setAddress(address);
            customer.setAccounts(accounts);
            facts.add(customer);
            currentRow++;

            address = new Address();
            address.setCountry(Address.Country.CZ);
            accounts.clear();
            accounts = new ArrayList<>();
            account = new Account();
            account.setNumber(currentLoop + currentRow);
            accounts.add(account);
            customer = new Customer();
            customer.setAddress(address);
            customer.setAccounts(accounts);
            facts.add(customer);
            currentRow++;
}
        return facts;
    }

    @Override
    protected List<Object> generateNonMatchingFacts(int number) {
        // generate some facts that will not match on generated rules
        final List<Object> facts = new ArrayList<>();
        for (int i = to+1; i < to + 298; i++) {
            currentLoop = i;
            int currentRow = 0;

            Account account = new Account();
            account.setNumber(currentLoop + currentRow);
            facts.add(account);
            currentRow++;

            Address address = new Address();
            address.setCountry(Address.Country.IT);
            List<Account> accounts = new ArrayList<>();
            account = new Account();
            account.setNumber(currentLoop + currentRow);
            accounts.add(account);
            Customer customer = new Customer();
            customer.setAddress(address);
            customer.setAccounts(accounts);
            facts.add(customer);
            currentRow++;
        }
        return facts;
    }
}
