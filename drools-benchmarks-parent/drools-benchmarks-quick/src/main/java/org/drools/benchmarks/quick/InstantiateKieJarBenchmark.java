/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.drools.benchmarks.quick;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.drools.util.ResourceHelper;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
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
@Warmup(iterations = 15, time = 3)
@Measurement(iterations = 5, time = 3)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(3)
public class InstantiateKieJarBenchmark {

    @Param({"true", "false"})
    private boolean useCanonicalModel;
    private Resource[] resources;


    @Setup
    public void loadResources() throws IOException {
        KieResources kieResources = KieServices.get().getResources();
        Collection<File> dmns = ResourceHelper.getFileResourcesByExtension("dmn");
        Collection<File> drls = ResourceHelper.getFileResourcesByExtension("drl");
        List<Resource> resourceList  = new ArrayList<>();

        for (File dmn : dmns) {
            Resource toAdd = kieResources.newByteArrayResource(Files.readString(dmn.toPath()).getBytes(StandardCharsets.UTF_8))
                    .setResourceType(ResourceType.DMN)
                    .setSourcePath(dmn.getName());
            resourceList.add(toAdd);
        }
        for (File drl : drls) {
            Resource toAdd = kieResources.newByteArrayResource(Files.readString(drl.toPath()).getBytes(StandardCharsets.UTF_8))
                    .setResourceType(ResourceType.DRL)
                    .setSourcePath(drl.getName());
            resourceList.add(toAdd);
        }
        resources = resourceList.toArray(new Resource[0]);
    }

    @Benchmark
    public ReleaseId createKieJarFromResources() throws IOException {
        return BuildtimeUtil.createKJarFromResources(useCanonicalModel, resources);
    }


}
