/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.benchmarks.common.providers;

import java.util.SortedSet;
import java.util.TreeSet;
import org.drools.benchmarks.common.Event;
import org.drools.benchmarks.common.EventProvider;
import org.drools.benchmarks.common.ProviderException;

public class BasicEventProvider implements EventProvider {

    @Override
    public <T extends Event> SortedSet<T> getEvents(Class<T> eventClass, int eventsCount,
            long startTime, long timeIncrement) throws ProviderException {
        return getEvents(eventClass, eventsCount, startTime, timeIncrement, 1);
    }

    @Override
    public <T extends Event> SortedSet<T> getEvents(final Class<T> eventClass, final int eventsCount,
            final long startTime, final long timeIncrement, long duration) throws ProviderException {
        final SortedSet<T> resultList = new TreeSet<T>();

        long actualTime = startTime;
        for (int i = 0; i < eventsCount; i++) {
            try {
                final T event = eventClass.newInstance();
                event.setTimeValue(actualTime);
                event.setDuration(duration);
                resultList.add(event);
            } catch (InstantiationException e) {
                throw new ProviderException(e.getMessage(), e);
            } catch (IllegalAccessException e) {
                throw new ProviderException(e.getMessage(), e);
            }

            actualTime = actualTime + timeIncrement;
        }
        return resultList;
    }
}
