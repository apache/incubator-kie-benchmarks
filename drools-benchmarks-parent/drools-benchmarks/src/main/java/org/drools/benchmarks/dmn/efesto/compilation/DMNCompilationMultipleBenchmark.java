/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.drools.benchmarks.dmn.efesto.compilation;

import org.drools.benchmarks.common.ProviderException;
import org.drools.benchmarks.common.providers.dmn.BusinessKnowledgeModelDMNProvider;
import org.drools.benchmarks.common.providers.dmn.ContextDMNProvider;
import org.drools.benchmarks.common.providers.dmn.DecisionDMNProvider;
import org.drools.benchmarks.common.providers.dmn.DecisionTableDMNProvider;
import org.drools.benchmarks.dmn.efesto.DMNEfestoAbstractBenchmark;
import org.drools.io.ClassPathResource;
import org.drools.io.ReaderResource;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Thread)
@Warmup(iterations = 40, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 15, time = 2, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class DMNCompilationMultipleBenchmark extends DMNEfestoAbstractBenchmark {

    private Resource resource1;

    private String dmn1;
    private String dmn2;
    private String dmn3;
    private String dmn4;

    @Setup
    public void setup() throws ProviderException {
        resource1 = new ClassPathResource("dmn/ch11MODIFIED.dmn");
        resource1.setResourceType(ResourceType.DMN);

        dmn1 = new BusinessKnowledgeModelDMNProvider().getDMN(1000);
        dmn2 = new DecisionTableDMNProvider().getDMN(1000);
        dmn3 = new DecisionDMNProvider().getDMN(1000);
        dmn4 = new ContextDMNProvider().getDMN(1000);
    }

    @Benchmark
    public DMNRuntime createDMNRuntime() {
        List<Resource> dmnResources = Arrays.asList(resource1,
                getDMNResource(dmn1, "bkm.dmn"),
                getDMNResource(dmn2, "decisionTable.dmn"),
                getDMNResource(dmn3, "decision.dmn"),
                getDMNResource(dmn4, "context.dmn"));
        return DMNRuntimeBuilder.fromDefaults()
                .buildConfiguration()
                .fromResources(dmnResources)
                .getOrElseThrow(e -> new RuntimeException("Error initializing DMNRuntime", e));
    }

    private Resource getDMNResource(String dmn, final String sourcePath) {
        return new ReaderResource(new StringReader(dmn))
                .setResourceType(ResourceType.DMN)
                .setSourcePath(sourcePath);
    }
}
