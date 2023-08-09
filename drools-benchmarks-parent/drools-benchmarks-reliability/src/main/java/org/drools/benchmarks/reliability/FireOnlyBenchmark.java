/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.drools.benchmarks.reliability;

import org.drools.benchmarks.common.DRLProvider;
import org.drools.benchmarks.common.model.A;
import org.drools.benchmarks.common.model.B;
import org.drools.benchmarks.common.providers.RulesWithJoinsProvider;
import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.kie.api.conf.EventProcessingOption;
import org.kie.internal.conf.ParallelExecutionOption;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;

@Warmup(iterations = 2000)
@Measurement(iterations = 1000)
public class FireOnlyBenchmark extends AbstractReliabilityBenchmark {

    @Param({"192"})
    private int rulesNr;

    @Param({"10", "100", "1000"})
    private int factsNr;

    @Setup
    public void setupKieBase() {
        final DRLProvider drlProvider = new RulesWithJoinsProvider(1, false, true);
        kieBase = BuildtimeUtil.createKieBaseFromDrl(true, drlProvider.getDrl(rulesNr),
                                                     ParallelExecutionOption.SEQUENTIAL,
                                                     EventProcessingOption.CLOUD);
    }

    @Override
    public void populateKieSessionPerIteration() {
        StatefulKnowledgeSessionImpl session = (StatefulKnowledgeSessionImpl) kieSession;
        A a = new A(rulesNr + 1);

        session.insert(a);

        for (int i = 0; i < factsNr; i++) {
            session.insert(new B(rulesNr + i + 3));
        }
    }

    @Benchmark
    public int test() {
        return kieSession.fireAllRules();
    }
}