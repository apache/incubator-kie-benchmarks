/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import org.drools.benchmarks.common.util.BuildtimeUtil;
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
@BenchmarkMode(Mode.SingleShotTime)
@Warmup(iterations = 10)
@Measurement(iterations = 10)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class BuildKJarFromResourceStoreFirstBenchmark {

    @Param({"true"})
    private boolean useCanonicalModel;

    @Param({"true", "false"})
    private boolean storeFirst;

    @Param({"100", "200"})
    private int numberOfRules;

    private Resource drlResource;

    @Setup
    public void createResource() {
        if (storeFirst) {
            System.setProperty("drools.projectClassLoader.enableStoreFirst", "true");
        } else {
            System.setProperty("drools.projectClassLoader.enableStoreFirst", "false");
        }
        final DRLProvider drlProvider = new RulesWithJoinsProvider(4, false, true);
        drlResource = KieServices.get().getResources()
                                 .newReaderResource(new StringReader(drlProvider.getDrl(numberOfRules)))
                                 .setResourceType(ResourceType.DRL)
                                 .setSourcePath("drlFile.drl");
    }

    @Benchmark
    public ReleaseId createKJarFromResource() throws IOException {
        return BuildtimeUtil.createKJarFromResources(useCanonicalModel, drlResource);
    }

}
