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

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;

import org.drools.benchmarks.common.DMNProvider;
import org.drools.benchmarks.common.ProviderException;
import org.drools.benchmarks.common.providers.dmn.BusinessKnowledgeModelDMNProvider;
import org.drools.benchmarks.common.providers.dmn.ContextDMNProvider;
import org.drools.benchmarks.common.providers.dmn.DecisionDMNProvider;
import org.drools.benchmarks.common.providers.dmn.DecisionTableDMNProvider;
import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNRuntime;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Thread)
@Warmup(iterations = 40, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 15, time = 2, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class DMNBuildKJarBenchmark {

    private KieServices kieServices;
    private ReleaseId releaseId;

    @Setup
    public void setup() throws ProviderException, IOException {
        kieServices = KieServices.Factory.get();
        releaseId = createKJar();
    }

    @Benchmark
    public DMNRuntime createDMNRuntime() {
        KieContainer container = kieServices.newKieContainer(releaseId);
        return container.newKieSession().getKieRuntime(DMNRuntime.class);
    }

    private ReleaseId createKJar() throws IOException {

        final Resource resource1 = kieServices.getResources().newClassPathResource("dmn/ch11MODIFIED.dmn");
        resource1.setResourceType(ResourceType.DMN);

        return BuildtimeUtil.createKJarFromResources(false,
                                                          resource1,
                                                          getDMNResource(new BusinessKnowledgeModelDMNProvider(), "bkm.dmn"),
                                                          getDMNResource(new DecisionTableDMNProvider(), "decisionTable.dmn"),
                                                          getDMNResource(new DecisionDMNProvider(), "decision.dmn"),
                                                          getDMNResource(new ContextDMNProvider(), "context.dmn"));
    }

    private Resource getDMNResource(final DMNProvider provider, final String sourcePath) {
        return kieServices.getResources()
                .newReaderResource(new StringReader(provider.getDMN(1000)))
                .setResourceType(ResourceType.DMN)
                .setSourcePath(sourcePath);
    }
}
