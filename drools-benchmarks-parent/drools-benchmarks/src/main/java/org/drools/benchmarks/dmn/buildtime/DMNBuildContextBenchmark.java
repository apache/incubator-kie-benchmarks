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

package org.drools.benchmarks.dmn.buildtime;

import java.io.StringReader;
import java.util.concurrent.TimeUnit;

import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.benchmarks.common.DMNProvider;
import org.drools.benchmarks.common.ProviderException;
import org.drools.benchmarks.common.providers.dmn.ContextDMNProvider;
import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 40, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 15, time = 2, timeUnit = TimeUnit.SECONDS)
public class DMNBuildContextBenchmark extends AbstractBenchmark {

    private String dmn;

    @Param({"3000"})
    private int numberOfDecisionsWithContext;

    @Setup
    @Override
    public void setup() throws ProviderException {
        DMNProvider dmnProvider = new ContextDMNProvider();
        dmn = dmnProvider.getDMN(numberOfDecisionsWithContext);
    }

    @Benchmark
    public KieBase testBuildKieBase() {
        Resource dmnResource = KieServices.get().getResources()
                .newReaderResource(new StringReader(dmn))
                .setResourceType(ResourceType.DMN)
                .setSourcePath("dmnFile.dmn");
        return BuildtimeUtil.createKieBaseFromResource(dmnResource);
    }
}
