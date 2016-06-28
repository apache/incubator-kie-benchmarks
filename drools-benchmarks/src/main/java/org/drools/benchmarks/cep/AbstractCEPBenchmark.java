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

package org.drools.benchmarks.cep;

import java.util.SortedSet;
import java.util.concurrent.TimeUnit;
import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.benchmarks.common.Event;
import org.drools.core.time.SessionPseudoClock;

public abstract class AbstractCEPBenchmark extends AbstractBenchmark {

    protected int insertEventsAndFire(final SortedSet<Event> events) {
        final SessionPseudoClock sessionClock = kieSession.getSessionClock();

        final long startTime = sessionClock.getCurrentTime();
        int fireCount = 0;
        for (Event event : events) {
            final long eventTime = startTime + event.getTimeValue();
            sessionClock.advanceTime(eventTime - sessionClock.getCurrentTime(), TimeUnit.MILLISECONDS);
            kieSession.insert(event);
            fireCount += kieSession.fireAllRules();
        }
        return fireCount;
    }
}
