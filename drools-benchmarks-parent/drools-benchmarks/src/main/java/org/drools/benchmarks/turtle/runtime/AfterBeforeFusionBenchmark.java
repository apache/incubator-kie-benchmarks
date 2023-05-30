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
import java.util.concurrent.TimeUnit;
import org.drools.benchmarks.turtle.runtime.generator.AfterBeforeEventsGenerator;
import org.drools.benchmarks.turtle.runtime.generator.GeneratorConfiguration;
import org.drools.benchmarks.turtle.runtime.generator.PlaceHolder;
import org.kie.api.runtime.KieSession;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;

public class AfterBeforeFusionBenchmark extends AbstractSimpleFusionRuntimeBenchmark {

    @Param({"200000"})
    int nrOfFacts;
    
    @Override
    public void addResources() {
        addClassPathResource("turtle/fusion-after-before-operators-100.drl");
    }

    @Override
    public void addEventSenders() {
        addEventSender(new AfterBeforeEventsGenerator(getGeneratorConfiguration()),nrOfFacts);
    }

    @Benchmark
    public KieSession timeEventsInsertionAndRulesFiring() {
        return insertEventsAndFireAllRules();
    }

    private GeneratorConfiguration getGeneratorConfiguration() {
        final GeneratorConfiguration generatorConfiguration = new GeneratorConfiguration(
                100, 2, 0.1);
        generatorConfiguration.setPlaceHolders(getPlaceholders());
        return generatorConfiguration;
    }

    private List<PlaceHolder> getPlaceholders() {
        final List<PlaceHolder> placeHolders = new ArrayList<>();
        placeHolders.add(new PlaceHolder("time1", 0, 147, TimeUnit.MILLISECONDS));
        placeHolders.add(new PlaceHolder("time2", 3, 149, TimeUnit.MILLISECONDS));
        placeHolders.add(new PlaceHolder("time3", 0, 147, TimeUnit.MILLISECONDS));
        placeHolders.add(new PlaceHolder("time4", 3, 149, TimeUnit.MILLISECONDS));
        return placeHolders;
    }
}
