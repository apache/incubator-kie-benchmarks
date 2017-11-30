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

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.drools.benchmarks.common.ProviderException;
import org.drools.compiler.kie.builder.impl.ZipKieModule;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
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
import org.openjdk.jmh.util.FileUtils;

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
        releaseId = kieServices.newReleaseId("org.drools", "test-dmn-project", "1.0");
        prepareRepository();
    }

    @Benchmark
    public DMNRuntime createDMNRuntime() {
        KieContainer container = kieServices.newKieContainer(releaseId);
        return container.newKieSession().getKieRuntime(DMNRuntime.class);
    }

    private void prepareRepository() throws IOException {
        final File kJarFile = FileUtils.extractFromResource("/dmn/test-dmn-project-1.0.jar");
        final ZipKieModule zipKieModule = new ZipKieModule(releaseId, getKieModuleModel(), kJarFile);
        kieServices.getRepository().addKieModule(zipKieModule);
    }

    private KieModuleModel getKieModuleModel() {
        final KieModuleModel kieModuleModel = kieServices.newKieModuleModel();
        kieModuleModel
                .newKieBaseModel("default-kiebase")
                .addPackage("*")
                .setDefault(true)
                .newKieSessionModel("default-kiesession")
                .setDefault(true);
        return kieModuleModel;
    }
}
