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

package org.drools.benchmarks.turtle.buildtime;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;

/**
 * Time how long it takes to create knowledge base from rules saved in DRL files.
 * <p/>
 * There are 3 separate tests using 1k, 5k and 10k rules.
 */
public class KBaseCreationFromDrlBenchmark extends AbstractBuildtimeBenchmark {

    @Param({"1k", "5k", "10k"})
    private String nrOfRules;

    @Setup
    public void addResources() {
        addClassPathResource("kbase-creation/drl-kbase-creation-" + nrOfRules + ".drl");
    }

    @Benchmark
    public int timeKBaseCreationFromDrl() {
        return actuallyCreateTheKBase();
    }

}
