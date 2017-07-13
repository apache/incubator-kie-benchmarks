/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.internal.utils.KieHelper;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 15, time = 10, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 15, time = 5, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public abstract class AbstractBuildtimeBenchmark {

    protected KieResources kieResources = KieServices.Factory.get().getResources();

    private Set<Resource> resources;

    private KieFileSystem kieFileSystem;

    public AbstractBuildtimeBenchmark() {
        resources = new HashSet<>();
    }

    protected void addClassPathResource(String path) {
        resources.add(kieResources.newClassPathResource(path));
    }

    protected void createKieFileSystemFromResources() {
        final KieServices kieServices = KieServices.get();
        kieFileSystem = kieServices.newKieFileSystem();
        resources.forEach(kieFileSystem::write);
    }

    /**
     * Creates the knowledge (KIE) base from the previously specified resources.
     *
     * @return number of packages in the kbase
     */
    protected int actuallyCreateTheKBase() {
        KieHelper kieHelper = new KieHelper();
        for (Resource resource : resources) {
            kieHelper.addResource(resource);
        }
        KieBase kieBase = kieHelper.build();
        int nrOfPackages = kieBase.getKiePackages().size();
        return nrOfPackages;
    }

    /**
     * Builds resources without creating the KieBase.
     */
    protected void buildResourcesWithoutKieBase(final Blackhole eater) {
        eater.consume(KieServices.get().newKieBuilder(kieFileSystem).buildAll());
    }

}
