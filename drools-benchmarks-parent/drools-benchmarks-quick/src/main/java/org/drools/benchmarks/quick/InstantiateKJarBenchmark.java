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

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
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
@Warmup(iterations = 9, time = 3)
@Measurement(iterations = 4, time = 3)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(3)
public class InstantiateKJarBenchmark {

    @Param({"true", "false"})
    private boolean useCanonicalModel;
    private Resource[] resources;

    @Setup(Level.Trial)
    public void loadResources() {
        URL dmnResource = Thread.currentThread().getContextClassLoader().getResource("dmn/Traffic Violation.dmn");
        if (dmnResource == null) {
            throw new RuntimeException("Failed to find dmn file");
        }
        String dmnContent = readStringFromURL(dmnResource);
        URL drlResource = Thread.currentThread().getContextClassLoader().getResource("drl/rules.drl");
        if (drlResource == null) {
            throw new RuntimeException("Failed to find drl file");
        }
        String drlContent = readStringFromURL(drlResource);
        KieResources kieResources = KieServices.get().getResources();
        List<Resource> resourceList  = new ArrayList<>();
        Resource toAdd = kieResources.newByteArrayResource(dmnContent.getBytes())
                .setResourceType(ResourceType.DMN)
                .setSourcePath("Traffic Violation.dmn");
        resourceList.add(toAdd);
        toAdd = kieResources.newByteArrayResource(drlContent.getBytes())
                .setResourceType(ResourceType.DRL)
                .setSourcePath("rules.drl");
        resourceList.add(toAdd);
        resources = resourceList.toArray(new Resource[0]);
    }

    @Benchmark
    public ReleaseId createKieJarFromResources() throws IOException {
        return BuildtimeUtil.createKJarFromResources(useCanonicalModel, resources);
    }

    private static String readStringFromURL(URL url) {
        try (Scanner scanner = new Scanner(url.openStream(),
                                           StandardCharsets.UTF_8.toString()))
        {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
