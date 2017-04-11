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

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Represents event record that holds event object and time of occurrence.
 * 
 * Time of occurrence is needed for Fusion.
 *
 */
public class EventRecord implements Comparable<EventRecord>, Serializable {
    private static final long serialVersionUID = 5513090531481576167L;
    private final Event event;
    private final long timeValue;
    private final TimeUnit timeUnit;

    public EventRecord(Event event, long time, TimeUnit unit) {
        super();
        this.event = event;
        timeValue = time;
        timeUnit = unit;
    }

    public Event getEvent() {
        return event;
    }

    public long getTimeValue() {
        return timeValue;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    @Override
    public int compareTo(EventRecord other) {
       long thisMilis = timeUnit.toMillis(timeValue);
       long otherMilis = other.timeUnit.toMillis(other.timeValue);
        if (thisMilis > otherMilis) {
            return 1;
        }
        if (thisMilis < otherMilis) {
            return -1;
        }
       // time of insertion is same for both events, so does not matter which is first
       return 1;
    }

    @Override
    public String toString() {
        return "EventRecord[timeValue=" + timeValue + ", timeUnit=" + timeUnit + "]";
    }
}