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

package org.drools.benchmarks.turtle.runtime;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.drools.benchmarks.turtle.runtime.generator.FactsGenerator;
import org.drools.benchmarks.turtle.runtime.generator.ResourceGenerator;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.conf.KieBaseOption;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.utils.KieHelper;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.SingleShotTime)
@State(Scope.Thread)
@Warmup(iterations = 30)
@Measurement(iterations = 20)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public abstract class AbstractSimpleRuntimeBenchmark {

    protected KieServices kieServices = KieServices.Factory.get();
    protected KieResources kieResources = KieServices.Factory.get().getResources();

    protected Set<Resource> resources;
    protected List<Object> facts;
    /**
     * Generator to number of facts to generate mapping
     */
    protected Map<ResourceGenerator, Integer> factsGenerators;

    protected volatile KieBase kieBase;
    protected KieSession ksession;
    protected StatelessKieSession statelessKieSession;

    public AbstractSimpleRuntimeBenchmark() {
        resources = new HashSet<>();
        facts = new ArrayList<>();
        factsGenerators = new HashMap<>();
    }

    @Setup
    public void createKBase() {
        addResources();
        KieHelper kieHelper = new KieHelper();
        for (Resource resource : resources) {
            kieHelper.addResource(resource);
        }
        kieBase = kieHelper.build(getKieBaseOptions());
    }

    protected KieBaseOption[] getKieBaseOptions() {
        return new KieBaseOption[]{};
    }

    @Setup
    public void initFactsGenerators() throws IOException {
        addFactsGenerators();
    }

    @Setup(Level.Iteration)
    public void gatherFacts() {
        facts.clear();
        generateFactsUsingGenerators();
        facts.addAll(generateFacts());
    }

    @TearDown(Level.Iteration)
    public void disposeKSession() {
        if (ksession != null) {
            ksession.dispose();
        }
    }

    protected void addResources() {
        // Intentionally blank.
    }
    protected void addFactsGenerators() {
        // Intentionally blank.
    }

    protected List<Object> generateFacts() {
        return new ArrayList<Object>();
    }

    protected void generateFactsUsingGenerators() {
        for (Map.Entry<ResourceGenerator, Integer> entry : factsGenerators.entrySet()) {
            ResourceGenerator generator = entry.getKey();
            int nrOfFacts = entry.getValue();
            if (generator instanceof FactsGenerator) {
                facts.addAll(((FactsGenerator) generator).generateFacts(nrOfFacts));
            }
        }
    }

    protected void addClassPathResource(String path) {
        resources.add(kieResources.newClassPathResource(path));
    }

    protected void addDrlResource(String drl) {
        resources.add(kieResources.newReaderResource(new StringReader(drl)).setSourcePath(generateResourceName(ResourceType.DRL)));
    }

    private int counter = 0;
    private String generateResourceName(ResourceType type) {
        return "src/main/resources/file" + counter++ + "." + type.getDefaultExtension();
    }

    protected void addFactsGenerator(ResourceGenerator generator, int nrOfFacts) {
        factsGenerators.put(generator, nrOfFacts);
    }

    public KieSession insertFactsAndFireAllRules() {
        ksession = kieBase.newKieSession();
        for (Object fact : facts) {
            ksession.insert(fact);
        }
        ksession.fireAllRules();
        ksession.dispose();
        return ksession;
    }

    public StatelessKieSession insertFactsAndExecuteStateless() {
        statelessKieSession = kieBase.newStatelessKieSession();
        statelessKieSession.execute(facts);
        return statelessKieSession;
    }

}
