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
import org.drools.benchmarks.turtle.runtime.generator.GeneratorConfiguration;
import org.drools.benchmarks.turtle.runtime.generator.PlaceHolder;
import org.drools.benchmarks.turtle.runtime.generator.StandardOperatorsFactsGenerator;
import org.kie.api.runtime.KieSession;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;

/**
 * Test the standard Expert operators like <,>,<=,>=,&&,||
 */
public class StandardOperatorsExpertBenchmark extends AbstractSimpleRuntimeBenchmark {

    @Param({"200000"})
    int nrOfFacts;

    @Override
    protected void addResources() {
        addClassPathResource("turtle/expert-standard-operators-100.drl");
    }

    @Override
    protected void addFactsGenerators() {
        addFactsGenerator(new StandardOperatorsFactsGenerator(getGeneratorConfiguration()),nrOfFacts);
    }

    @Benchmark
    public KieSession timeFactsInsertionAndRulesFiring() {
        return insertFactsAndFireAllRules();
    }

    private GeneratorConfiguration getGeneratorConfiguration() {
        final GeneratorConfiguration generatorConfiguration = new GeneratorConfiguration(
                100, 10, 0.25);
        generatorConfiguration.setPlaceHolders(getPlaceholders());
        return generatorConfiguration;
    }

    private List<PlaceHolder> getPlaceholders() {
        final List<PlaceHolder> placeHolders = new ArrayList<>();
        placeHolders.add(new PlaceHolder("number1", 100, 10000));
        placeHolders.add(new PlaceHolder("number2", 200, 30000));
        placeHolders.add(new PlaceHolder("balance1", 100, 10000));
        placeHolders.add(new PlaceHolder("balance2", 200, 20000));
        placeHolders.add(new PlaceHolder("balance3", 2000, 21800));
        placeHolders.add(new PlaceHolder("amount1", 1000, 100000));
        placeHolders.add(new PlaceHolder("amount2", 2000, 300000));
        placeHolders.add(new PlaceHolder("irate1", 200, 20000));
        placeHolders.add(new PlaceHolder("irate2", 2000, 21800));
        return placeHolders;
    }

}
