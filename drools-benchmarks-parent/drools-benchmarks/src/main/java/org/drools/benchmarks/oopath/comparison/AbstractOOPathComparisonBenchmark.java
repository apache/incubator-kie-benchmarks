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

package org.drools.benchmarks.oopath.comparison;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.benchmarks.common.ProviderException;
import org.drools.benchmarks.common.util.RuntimeUtil;
import org.drools.benchmarks.common.model.reactive.Child;
import org.drools.benchmarks.common.model.reactive.Man;
import org.drools.benchmarks.common.model.reactive.Toy;
import org.drools.benchmarks.common.model.reactive.Woman;
import org.drools.core.common.InternalFactHandle;
import org.kie.api.runtime.KieSession;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;

@Warmup(iterations = 40000)
@Measurement(iterations = 10000)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public abstract class AbstractOOPathComparisonBenchmark extends AbstractBenchmark {

    protected static final String MODEL_PACKAGE_IMPORT = "import org.drools.benchmarks.common.model.reactive.*;";

    @Param({"10"})
    protected int numberOfParentFacts;

    protected List<Man> parentFacts;
    protected List<Child> factsToBeModified;
    protected List<String> globalList;

    public abstract void setupKieBase();

    @Setup(Level.Iteration)
    @Override
    public void setup() throws ProviderException {
        kieSession = RuntimeUtil.createKieSession(kieBase);
        globalList = new ArrayList<>();
        kieSession.setGlobal("list", globalList);
        prepareFacts();
    }

    private void prepareFacts() {
        parentFacts = generateModel(numberOfParentFacts);
        factsToBeModified = getChildToBeModified(parentFacts);
    }

    private static List<Man> generateModel(final int nr) {
        final List<Man> model = new ArrayList<>();
        for (int i = 0; i < nr; i++) {
            final Man man = new Man("m" + i, 40);
            model.add(man);
            final Woman woman = new Woman("w" + i, 35);
            man.setWife(woman);
            woman.setHusband(man.getName());

            final Child childA = new Child("cA" + i, 12);
            woman.addChild(childA);
            childA.setMother(woman.getName());
            final Child childB = new Child("cB" + i, 10);
            woman.addChild(childB);
            childB.setMother(woman.getName());

            final Toy toyA = new Toy("tA" + i);
            toyA.setOwner(childA.getName());
            childA.addToy(toyA);
            final Toy toyB = new Toy("tB" + i);
            toyB.setOwner(childA.getName());
            childA.addToy(toyB);
            final Toy toyC = new Toy("tC" + i);
            toyC.setOwner(childB.getName());
            childB.addToy(toyC);
        }
        return model;
    }

    private static List<Child> getChildToBeModified(final List<Man> model) {
        final List<Child> toBeModified = new ArrayList<>();
        for (final Man man : model) {
            for (final Child child : man.getWife().getChildren()) {
                if (child.getAge() == 10) {
                    toBeModified.add(child);
                }
            }
        }
        return toBeModified;
    }

    protected static List<InternalFactHandle> insertModel(final KieSession ksession, final List<Man> model) {
        final List<InternalFactHandle> fhs = new ArrayList<>();
        for (final Man man : model) {
            fhs.add((InternalFactHandle)ksession.insert(man));
        }
        return fhs;
    }

    protected static List<InternalFactHandle> insertFullModel(final KieSession ksession, final List<Man> model) {
        final List<InternalFactHandle> toBeModified = new ArrayList<>();
        for (final Man man : model) {
            ksession.insert(man);
            ksession.insert(man.getWife());
            for (final Child child : man.getWife().getChildren()) {
                final InternalFactHandle fh = (InternalFactHandle)ksession.insert(child);
                if (child.getAge() == 10) {
                    toBeModified.add(fh);
                }
                for (final Toy toy : child.getToys()) {
                    ksession.insert(toy);
                }
            }
        }
        return toBeModified;
    }
}
