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

import java.util.ArrayList;
import java.util.List;
import org.kie.api.runtime.KieSession;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;

public class SimpleDummyFactsMatchRatioExpertBenchmark extends AbstractSimpleRuntimeBenchmark {

    @Param({"400000"})
    private int nrOfFacts;

    @Override
    protected void addResources() {
        addClassPathResource("turtle/expert-basic-match-ratio-100.drl");
    }

    @Override
    protected List<Object> generateFacts() {
        List<Object> facts = new ArrayList<Object>();
        int loops = nrOfFacts / 2;
        for (int i = 0; i < loops; i++) {
            facts.add(new String("someString" + i));
            facts.add(new Integer(i));
        }
        return facts;
    }

    @Benchmark
    public KieSession timeFactsInsertionAndRulesFiring() {
        return insertFactsAndFireAllRules();
    }

}
