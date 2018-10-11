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

package org.drools.benchmarks.turtle.buildtime;

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;

import org.drools.benchmarks.common.DRLProvider;
import org.drools.benchmarks.common.providers.RulesWithJoinsProvider;
import org.drools.benchmarks.common.providers.SimpleRulesWithConstraintsProvider;
import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 15, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 15, time = 2, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class BuildKieBaseFromContainerBenchmark {

    @Param({"true", "false"})
    private boolean useCanonicalModel;

    @Param({"100", "500", "1000", "3000", "5000", "10000"})
    private int numberOfRules;

    @Param({SimpleRulesWithConstraintsProvider.PROVIDER_ID, RulesWithJoinsProvider.PROVIDER_ID})
    private String rulesProviderId;

    private ReleaseId releaseId;
    private KieServices kieServices;
    private KieBaseConfiguration kieBaseConfiguration;

    @Setup
    public void createKJar() throws IOException {
        kieServices = KieServices.get();
        kieBaseConfiguration = kieServices.newKieBaseConfiguration();

        final DRLProvider drlProvider = getDRLProvider();
        final Resource drlResource = KieServices.get().getResources()
                .newReaderResource(new StringReader(drlProvider.getDrl(numberOfRules)))
                .setResourceType(ResourceType.DRL)
                .setSourcePath("drlFile.drl");
        releaseId = BuildtimeUtil.createKJarFromResources(useCanonicalModel, drlResource);
    }

    @Benchmark
    public KieBase getKieBaseFromContainer() {
        return kieServices.newKieContainer(releaseId).newKieBase(kieBaseConfiguration);
    }

    private DRLProvider getDRLProvider() {
        switch (rulesProviderId) {
            case SimpleRulesWithConstraintsProvider.PROVIDER_ID:
                return new SimpleRulesWithConstraintsProvider("Integer(this == ${i})");
            case RulesWithJoinsProvider.PROVIDER_ID:
                return new RulesWithJoinsProvider(4, false, true);
            default:
                throw new IllegalArgumentException("Benchmark doesn't support rules provider with id: " + rulesProviderId + "!");
        }
    }
}
