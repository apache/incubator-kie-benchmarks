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
 * Tests how long it takes to create knowledge base from few quite big
 * (several thousands of rules) decision tables.
 */
public class KBaseCreationFromDTablesBenchmark extends AbstractBuildtimeBenchmark {


    @Param({"298r"})
    private String dataset;

    @Setup
    public void addResources() {
        addClassPathResource("kbase-creation/dtable1-kbase-creation.xls");
        addClassPathResource("kbase-creation/dtable2-kbase-creation.xls");
    }

    @Benchmark
    public int timeKBaseCreationFromOneBigAndOneSmallDTable() {
        return actuallyCreateTheKBase();
    }

}
