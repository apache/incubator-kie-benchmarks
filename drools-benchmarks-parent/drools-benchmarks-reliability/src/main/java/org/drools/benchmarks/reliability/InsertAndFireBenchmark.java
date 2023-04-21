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
import org.drools.benchmarks.common.model.C;
import org.drools.benchmarks.common.model.D;
import org.drools.benchmarks.common.providers.RulesWithJoinsProvider;
import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.kie.api.conf.EventProcessingOption;
import org.kie.internal.conf.MultithreadEvaluationOption;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@Warmup(iterations = 3000)
@Measurement(iterations = 1000)
public class InsertAndFireBenchmark extends AbstractReliabilityBenchmark {

    @Param({"192"})
    private int rulesNr;

    @Param({"100"})
    private int factsNr;

    @Param({"1"})
    private int joinsNr;

    @Setup
    public void setupKieBase() {
        final DRLProvider drlProvider = new RulesWithJoinsProvider(joinsNr, false, true);
        kieBase = BuildtimeUtil.createKieBaseFromDrl(true, drlProvider.getDrl(rulesNr),
                MultithreadEvaluationOption.NO,
                EventProcessingOption.CLOUD);
    }

    @Override
    public void populateKieSessionPerIteration() {
        // no op
    }

    @Benchmark
    public int test() {
        kieSession.insert(new A(rulesNr + 1));
        for (int i = 0; i < factsNr; i++) {
            kieSession.insert(new B(rulesNr + i + 3));
            if (joinsNr > 1) {
                kieSession.insert(new C(rulesNr + factsNr + i + 3));
            }
            if (joinsNr > 2) {
                kieSession.insert(new D(rulesNr + factsNr * 2 + i + 3));
            }
        }
        return kieSession.fireAllRules();
    }
}