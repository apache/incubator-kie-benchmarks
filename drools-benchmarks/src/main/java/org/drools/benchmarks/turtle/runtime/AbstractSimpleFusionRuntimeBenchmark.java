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

package org.drools.benchmarks.turtle.runtime;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.drools.benchmarks.turtle.runtime.common.EventSender;
import org.drools.benchmarks.turtle.runtime.common.SameJvmEventSender;
import org.drools.benchmarks.turtle.runtime.generator.FactsGenerator;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.conf.KieBaseOption;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;


public abstract class AbstractSimpleFusionRuntimeBenchmark extends AbstractSimpleRuntimeBenchmark {

    /**
     * The param "anchor" is necessary to upload the performance results to (<a href="https://horreum.corp.redhat.com">Horreum</a>).
     * When one of the tests uses parameters, Horreum expects at least one parameter per test.
     * When a test has no parameter, there will be the dummy "anchor" which will make the update possible.
     */

    @Param({"true"})
    private boolean anchor;

    protected Map<EventSender, Integer> eventSenders;

    public AbstractSimpleFusionRuntimeBenchmark() {
        super();
        eventSenders = new HashMap<EventSender, Integer>();
    }

    @Setup
    public void initEventSenders() throws IOException {
        addEventSenders();
    }

    protected void addEventSenders() {
        // intentionally lef blank
    }

    protected void addEventSender(FactsGenerator generator, int nrOfEvents) {
        EventSender sender = new SameJvmEventSender();
        sender.setEventsGenerator(generator);
        addEventSender(sender, nrOfEvents);
    }

    protected void addEventSender(EventSender eventSender, int nrOfEvents) {
        eventSenders.put(eventSender, nrOfEvents);
    }

    @Override
    protected KieBaseOption[] getKieBaseOptions() {
        return new KieBaseOption[]{EventProcessingOption.STREAM};
    }

    @Setup(Level.Iteration)
    public void createKieSession() {
        KieSessionConfiguration kieSessionConfiguration = kieServices.newKieSessionConfiguration();
        kieSessionConfiguration.setOption(ClockTypeOption.get("pseudo"));
        ksession = kieBase.newKieSession(kieSessionConfiguration, null);
        for (Map.Entry<EventSender, Integer> entry : eventSenders.entrySet()) {
            EventSender sender = entry.getKey();
            sender.init(ksession, getStreamName());
            int nrOfEvents = entry.getValue();
            sender.generateEvents(nrOfEvents);
        }
    }

    @TearDown(Level.Iteration)
    public void dispose() {
        for (Map.Entry<EventSender, Integer> entry : eventSenders.entrySet()) {
            EventSender sender = entry.getKey();
            // need to clean the sender after the invocation so the events used can be GCed
            sender.clean();
        }
    }

    protected KieSession insertEventsAndFireAllRules() {
        for (Map.Entry<EventSender, Integer> entry : eventSenders.entrySet()) {
            EventSender sender = entry.getKey();
            sender.sendEvents();
        }
        ksession.fireAllRules();
        return ksession;
    }

    protected String getStreamName() {
        return "Master Stream";
    }

}
