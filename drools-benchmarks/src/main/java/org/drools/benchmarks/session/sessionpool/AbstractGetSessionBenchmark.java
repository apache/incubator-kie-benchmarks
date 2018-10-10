/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.benchmarks.session.sessionpool;

import java.util.Collection;

import org.kie.api.runtime.KieSession;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;

public abstract class AbstractGetSessionBenchmark extends AbstractSessionsPoolBenchmark {

    @Param({"true", "false"})
    private boolean exerciseSession;

    private Collection<Object> factsForSession;

    @Setup
    public void generateFactsForSessions() {
        if (exerciseSession) {
            factsForSession = generateFacts();
        }
    }

    protected KieSession handleKieSession(final KieSession kieSession) {
        if (exerciseSession) {
            doSomethingWithSessions(kieSession, factsForSession);
        }
        return kieSession;
    }
}
