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

package org.drools.benchmarks.turtle.runtime.common;

import java.util.List;
import org.drools.benchmarks.model.event.EventRecord;
import org.drools.benchmarks.turtle.runtime.generator.FactsGenerator;
import org.kie.api.runtime.KieSession;

/**
 * Event sender is used to send events to specified stream in stateful session.
 */
public interface EventSender {

    void init(KieSession ksession, String streamName);

    void addEvents(List<EventRecord> events);
    
    void setEventsGenerator(FactsGenerator generator);

    void generateEvents(int nrOfEventsToSend);

    void sendEvents();

    void setStreamName(String streamName);
    
    void stop();

    void clean();

    void addEventInsertedListener(EventInsertedListener listener);
}
