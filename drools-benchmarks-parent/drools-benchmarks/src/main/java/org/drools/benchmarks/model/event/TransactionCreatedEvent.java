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

package org.drools.benchmarks.model.event;

import java.util.Date;
import org.drools.benchmarks.model.Transaction;

public class TransactionCreatedEvent extends Event {
    protected Transaction transaction;

    public TransactionCreatedEvent() {
        super();
    }

    public TransactionCreatedEvent(int id) {
        super(id);
    }

    public TransactionCreatedEvent(int id, long duration) {
        super(id, duration);
    }

    public TransactionCreatedEvent(int id, String description) {
        super(id, description);
    }

    public TransactionCreatedEvent(int id, long duration, String description) {
        super(id, duration, description);
    }

    public Date getCreationDate() {
        //return transaction.getDate();
        return new Date();
    }

    public void setCreationDate(Date date) {
        transaction.setDate(date);
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
