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
import org.drools.benchmarks.turtle.runtime.generator.AdvancedOperatorsFactsGenerator;
import org.drools.benchmarks.turtle.runtime.generator.GeneratorConfiguration;
import org.drools.benchmarks.turtle.runtime.generator.PlaceHolder;
import org.kie.api.runtime.KieSession;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

/**
 * Test these Expert features
 * - (not) contains
 * - (not) memberOf
 * - (not) matches
 */
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Thread)
@Warmup(iterations = 20, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 20, time = 5, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class AdvOperatorsExpertBenchmark extends AbstractSimpleRuntimeBenchmark {

    @Param({"200000"})
    int nrOfFacts;

    @Override
    protected void addResources() {
        addClassPathResource("turtle/expert-advanced-operators-100.drl");
    }

    @Override
    protected void addFactsGenerators() {
        addFactsGenerator(new AdvancedOperatorsFactsGenerator(getGeneratorConfiguration()),nrOfFacts);
    }

    @Benchmark
    public KieSession timeFactsInsertionAndRulesFiring() {
        return insertFactsAndFireAllRules();
    }

    private GeneratorConfiguration getGeneratorConfiguration() {
        final GeneratorConfiguration generatorConfiguration = new GeneratorConfiguration(
                102, 6, 0.5);
        generatorConfiguration.setPlaceHolders(getPlaceholders());
        return generatorConfiguration;
    }

    private List<PlaceHolder> getPlaceholders() {
        final List<PlaceHolder> placeHolders = new ArrayList<>();
        placeHolders.add(new PlaceHolder("number1", 100, 10000));
        placeHolders.add(new PlaceHolder("number2", 200, 10100));
        placeHolders.add(new PlaceHolder("number3", 20000, 40000));
        placeHolders.add(new PlaceHolder("number4", 21000, 41000));
        placeHolders.add(new PlaceHolder("balance1", 50000, 100000));
        placeHolders.add(new PlaceHolder("balance2", 52000, 102000));
        placeHolders.add(new PlaceHolder("balance3", 500000, 1000000));
        placeHolders.add(new PlaceHolder("balance4", 520000, 1020000));
        return placeHolders;
    }
}
