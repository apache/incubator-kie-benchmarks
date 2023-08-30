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

package org.drools.benchmarks.manual;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.drools.benchmarks.common.DRLProvider;
import org.drools.benchmarks.common.providers.RulesWithJoinsProvider;
import org.drools.benchmarks.common.providers.SimpleRulesWithConstraintsProvider;
import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
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
import org.openjdk.jmh.util.FileUtils;

@State(Scope.Thread)
@BenchmarkMode(Mode.SingleShotTime)
@Warmup(iterations = 10, time = 2, timeUnit = TimeUnit.MINUTES)
@Measurement(iterations = 15, time = 2, timeUnit = TimeUnit.MINUTES)
@Fork(2)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class BuildKieBaseFromContainerManyRulesBenchmark {

    @Param({"true", "false"})
    private boolean useCanonicalModel;

    @Param({"200"})
    private int numberOfRulesPerFile;

    @Param({"100"})
    private int numberOfRuleFiles;

    @Param({SimpleRulesWithConstraintsProvider.PROVIDER_ID, RulesWithJoinsProvider.PROVIDER_ID})
    private String rulesProviderId;

    private ReleaseId releaseId;
    private KieServices kieServices;
    private KieBaseConfiguration kieBaseConfiguration;

    @Setup
    public void createKJar() throws IOException {
        kieServices = KieServices.get();
        kieBaseConfiguration = kieServices.newKieBaseConfiguration();
        final Collection<Resource> ruleResources = generateRuleResources();
        System.out.println("Creating KJar...");
        releaseId = BuildtimeUtil.createKJarFromResources(useCanonicalModel, ruleResources.toArray(new Resource[]{}));
    }

    @Benchmark
    public KieBase getKieBaseFromContainer() {
        return kieServices.newKieContainer(releaseId).newKieBase(kieBaseConfiguration);
    }

    private Collection<Resource> generateRuleResources() {
        final DRLProvider drlProvider = getDRLProvider();
        final Map<Integer, Resource> resourcesMap = new ConcurrentHashMap<>();
        IntStream.range(1, numberOfRuleFiles + 1).parallel().forEach(ruleFileId -> {
            final String drl = drlProvider.getDrl(numberOfRulesPerFile, "R" + ruleFileId + "-");
            final List<String> lines = Arrays.asList(drl.split("\n"));
            try {
                final String drlFilePath = FileUtils.createTempFileWithLines("drlFile-R" + ruleFileId + ".drl", lines);
                final Resource drlResource = KieServices.get().getResources().newFileSystemResource(new File(drlFilePath));
                resourcesMap.put(ruleFileId, drlResource);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return resourcesMap.values();
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
