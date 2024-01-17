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

package org.drools.benchmarks.turtle.runtime.common;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import org.drools.benchmarks.common.model.event.BasicEvent;
import org.drools.benchmarks.common.model.event.EventRecord;
import org.drools.benchmarks.turtle.runtime.generator.FactsGenerator;
import org.drools.core.time.SessionPseudoClock;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link EventSender} created in current JVM within the same thread. The test execution is blocked
 * till the sender sends all the events.
 */
public class SameJvmEventSender implements EventSender {

    private static Logger logger = LoggerFactory.getLogger(SameJvmEventSender.class);

    protected KieSession ksession;
    protected String streamName;
    protected FactsGenerator eventsGenerator;
    protected SortedSet<EventRecord> events = new TreeSet<>();

    private Set<EventInsertedListener> eventInsertedListeners;

    @Override
    public void init(KieSession ksession, String streamName) {
        this.ksession = ksession;
        this.streamName = streamName;
    }

    @Override
    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    @Override
    public void setEventsGenerator(FactsGenerator generator) {
        this.eventsGenerator = generator;
    }

    @Override
    public void generateEvents(int nrOfEventsToSend) {
        addEvents((List<EventRecord>) (Object) eventsGenerator.generateFacts(nrOfEventsToSend));
    }

    @Override
    public void sendEvents() {
        logger.debug("Event sender in same JVM started....");
        if (streamName == null) {
            String message = "Stream name must be specified in order to send events to it!";
            logger.error(message);
            throw new RuntimeException(message);
        }

        EntryPoint stream = ksession.getEntryPoint(streamName);
        if (stream == null) {
            String message = "Stream with name '" + streamName + "' does not exist!";
            logger.error(message);
            throw new RuntimeException(message);
        }

        SessionPseudoClock clock = ksession.getSessionClock();

        // EventRecords are sorted by their timeValue attribute.
        // So timeValue is the moment in time on which the event should occur.
        // The cycle iterates through sorted EventRecords and on each iteration
        // it inserts actual Event into the entry point and moves the clock to time
        // of the event occurrence.
        long startTime = clock.getCurrentTime();
        for (EventRecord record : events) {
            long currTime = clock.getCurrentTime();
            long nextEventTime = startTime + record.getTimeUnit().toMillis(record.getTimeValue());
            clock.advanceTime(nextEventTime - currTime, TimeUnit.MILLISECONDS);
            final BasicEvent event = record.getEvent();
            stream.insert(event);
            fireEventInsertedListeners(event, startTime, currTime, nextEventTime);
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void addEvents(List<EventRecord> newEvents) {
        events.addAll(newEvents);
    }

    @Override
    public void clean() {
        events.clear();
    }

    @Override
    public void addEventInsertedListener(final EventInsertedListener listener) {
        if (eventInsertedListeners == null) {
            eventInsertedListeners = new HashSet<>();
        }
        eventInsertedListeners.add(listener);
    }

    private void fireEventInsertedListeners(final BasicEvent event, final long startTime, final long currentTime,
            final long eventTime) {
        if (eventInsertedListeners != null && !eventInsertedListeners.isEmpty()) {
            for (EventInsertedListener listener : eventInsertedListeners) {
                listener.eventWasInserted(event, startTime, currentTime, eventTime);
            }
        }
    }
}
