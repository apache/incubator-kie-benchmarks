/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.benchmarks.oopath.buildtime;

import java.util.concurrent.TimeUnit;

import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.benchmarks.common.DRLProvider;
import org.drools.benchmarks.common.ProviderException;
import org.drools.benchmarks.common.providers.SimpleRulesWithConstraintProvider;
import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.kie.api.KieBase;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Thread)
@Warmup(iterations = 50, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 15, time = 500, timeUnit = TimeUnit.MILLISECONDS)
public class OOPathBuildDRLBenchmark extends AbstractBenchmark {

    @Param({"32", "64", "128"})
    private int numberOfRules;

    private String drl;

    @Setup
    @Override
    public void setup() throws ProviderException {
        final DRLProvider drlProvider = new SimpleRulesWithConstraintProvider("  Man( $toy: /wife/children[age > ${i} ]/toys )\n");
        drl = drlProvider.getDrl(numberOfRules);
    }

    @Benchmark
    public KieBase testBuildKieBase() {
        return BuildtimeUtil.createKieBaseFromDrl(drl);
    }
}
