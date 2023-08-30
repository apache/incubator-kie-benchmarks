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
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.drools.benchmarks.common.model.event.EventRecord;
import org.drools.benchmarks.common.model.event.NewCustomerEvent;
import org.drools.benchmarks.common.model.event.TransactionCreatedEvent;

public class FinishesFinishedbyEventsGenerator extends FactsGenerator {

    public FinishesFinishedbyEventsGenerator(final GeneratorConfiguration config) {
        super(config);
    }

    @Override
    protected List<Object> generateMatchingFacts(final int totalNumber) {
        // generate needed number of facts
        final List<Object> events = new ArrayList<>();
        final int nrOfFactsInInnerLoop = 4;
        final int innerLoops = (config.getNumberOfRulesInDRL() / config.getNumberOfRuleTypesInDRL());
        final int outerLoops = (totalNumber / (innerLoops * nrOfFactsInInnerLoop));

        int id = 0;
        for (int j = 0; j < outerLoops; j++) {
            for (int i = 0; i < innerLoops; i++) {
                currentLoop = i;
                long currentTime = getPlaceHolderValueMillis("time1") + 2;
                id = id + 1;

                ///////////////////////////////////////////////////////////////
                // rule "FinishesFinishedby_TransactionCreatedFinishesNewCustomerCreated"
                TransactionCreatedEvent tcEvent = new TransactionCreatedEvent(id, 2);
                tcEvent.setDescription("FinishesFinishedby_TransactionCreatedFinishesNewCustomerCreated" + getPlaceHolderValueMillis("time1") + "ms");
                EventRecord eventRecord = new EventRecord(tcEvent, currentTime, TimeUnit.MILLISECONDS);
                events.add(eventRecord);
                // advance time
                currentTime = getPlaceHolderValueMillis("time1");

                NewCustomerEvent ncEvent = new NewCustomerEvent(id, 4);
                ncEvent.setDescription("FinishesFinishedby_TransactionCreatedFinishesNewCustomerCreated" + getPlaceHolderValueMillis("time1") + "ms");
                eventRecord = new EventRecord(ncEvent, currentTime, TimeUnit.MILLISECONDS);
                events.add(eventRecord);

                ///////////////////////////////////////////////////////////////
                // rule "FinishesFinishedby_TransactionCreatedFinishedbyNewCustomerCreated"
                id = id + 1;
                currentTime = getPlaceHolderValueMillis("time2") + 2;
                ncEvent = new NewCustomerEvent(id, 2);
                ncEvent.setDescription("FinishesFinishedby_TransactionCreatedFinishedbyNewCustomerCreated" + getPlaceHolderValueMillis("time2") + "ms");
                eventRecord = new EventRecord(ncEvent, currentTime, TimeUnit.MILLISECONDS);
                events.add(eventRecord);
                // advance time
                currentTime = getPlaceHolderValueMillis("time2");
                tcEvent = new TransactionCreatedEvent(id, 4);
                tcEvent.setDescription("FinishesFinishedby_TransactionCreatedFinishedbyNewCustomerCreated" + getPlaceHolderValueMillis("time2") + "ms");
                eventRecord = new EventRecord(tcEvent, currentTime, TimeUnit.MILLISECONDS);
                events.add(eventRecord);
                // total of 4 events inserted in each loop
            }
        }
        return events;
    }

    @Override
    protected List<Object> generateNonMatchingFacts(final int totalNumber) {
        // generate some facts that will not match on generated rules
        final List<Object> events = new ArrayList<>();
        final int nrOfFactsInLoop = 2;
        final int loops = (totalNumber / nrOfFactsInLoop);
        for (int i = 0; i < loops; i++) {
            final NewCustomerEvent ncEvent = new NewCustomerEvent(getRandomInt(0, 10000000), 3);
            ncEvent.setDescription("FinishesFinishedby_SomeGreateDescription" + i * getRandomInt(0, 10000));
            EventRecord eventRecord = new EventRecord(ncEvent, getRandomLong(0, 100), TimeUnit.MILLISECONDS);
            events.add(eventRecord);

            final TransactionCreatedEvent tcEvent = new TransactionCreatedEvent(getRandomInt(0, 10000000), 3);
            tcEvent.setDescription("FinishesFinishedby_SomeGreateDescription2" + i * getRandomInt(0, 10000));
            eventRecord = new EventRecord(tcEvent, getRandomLong(0, 100), TimeUnit.MILLISECONDS);
            events.add(eventRecord);
            // total of 2 events inserted in each loop
        }
        return events;
    }

}
