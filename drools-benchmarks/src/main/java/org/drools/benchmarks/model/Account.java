/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.benchmarks.model;

import java.util.Currency;
import java.util.Date;

public class Account {
    public enum AccountType {
        STANDARD, SAVINGS, STUDENT
    }

    public enum AccountStatus {
        OK, BLOCKED, CANCELED
    }

    private int number;
    private String name;
    private double balance;
    private Currency currency;
    private Date startDate;
    private Date endDate;
    private AccountType type;
    private String uuid;
    private double interestRate;
    private AccountStatus status;
    private Customer owner;

    public Account() {}

    public Account(double balance) {
        this.balance = balance;
    }

    public Account(String uuid) {
        this.uuid = uuid;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public Customer getOwner() {
        return owner;
    }

    public void setOwner(Customer owner) {
        this.owner = owner;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    @Override
    public String toString() {
        return "Account [number=" + number + ", name=" + name + ", balance=" + balance + ", currency=" +
                currency + ", startDate=" + startDate + ", endDate=" + endDate + ", type=" + type + ", uuid=" +
                uuid + ", interestRate=" + interestRate + ", status=" + status + ", owner=" + owner.getUuid() + "]";
    }
}