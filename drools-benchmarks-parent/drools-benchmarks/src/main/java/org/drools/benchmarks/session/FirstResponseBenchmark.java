/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.benchmarks.session;

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;

import org.drools.benchmarks.common.DRLProvider;
import org.drools.benchmarks.common.providers.RulesWithJoinsProvider;
import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.drools.benchmarks.common.util.RuntimeUtil;
import org.drools.benchmarks.common.model.A;
import org.drools.benchmarks.common.model.B;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
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
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

/**
 * the first response time after a kbase is created
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.SingleShotTime)
@Warmup(iterations = 0)
@Measurement(iterations = 1)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(100)
public class FirstResponseBenchmark {

    @Param({"64"})
    private int rulesNr;

    @Param({"10"})
    private int factsNr;

    @Param({"true", "false"})
    private boolean useCanonicalModel;

    private ReleaseId releaseId;
    private KieServices kieServices;
    private KieBaseConfiguration kieBaseConfiguration;
    private KieBase kieBase;
    private KieSession kieSession;

    @Setup
    public void createKJar() throws IOException {
        kieServices = KieServices.get();
        kieBaseConfiguration = kieServices.newKieBaseConfiguration();

        final DRLProvider drlProvider = new RulesWithJoinsProvider(1, false, true);
        final Resource drlResource = KieServices.get().getResources()
                .newReaderResource(new StringReader(drlProvider.getDrl(rulesNr)))
                .setResourceType(ResourceType.DRL)
                .setSourcePath("drlFile.drl");

        releaseId = BuildtimeUtil.createKJarFromResources(useCanonicalModel, drlResource);

        kieBase = kieServices.newKieContainer(releaseId).newKieBase(kieBaseConfiguration);
    }

    @Setup(Level.Iteration)
    public void setup() {
        kieSession = RuntimeUtil.createKieSession(kieBase);
    }

    @TearDown(Level.Iteration)
    public void tearDown() {
        if (kieSession != null) {
            kieSession.dispose();
            kieSession = null;
        }
    }

    @Benchmark
    public int test(final Blackhole eater) {
        kieSession.insert(new A(rulesNr + 1));
        for (int i = 0; i < factsNr; i++) {
            kieSession.insert(new B(rulesNr + i + 3));
        }
        return kieSession.fireAllRules();
    }
}
