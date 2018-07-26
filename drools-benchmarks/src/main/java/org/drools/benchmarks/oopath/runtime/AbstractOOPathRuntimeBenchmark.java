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

package org.drools.benchmarks.oopath.runtime;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.benchmarks.common.ProviderException;
import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.drools.benchmarks.model.reactive.Child;
import org.drools.benchmarks.model.reactive.Man;
import org.drools.benchmarks.model.reactive.Woman;
import org.openjdk.jmh.annotations.Setup;

public abstract class AbstractOOPathRuntimeBenchmark extends AbstractBenchmark {

    protected abstract Collection<Object> getFacts(int numberOfFacts);

    protected abstract String getDrl();

    @Setup
    @Override
    public void setup() throws ProviderException {
        kieBase = BuildtimeUtil.createKieBaseFromDrl(getDrl());
    }

    protected Collection<Object> generateMenFacts(final int numberOfFacts, final boolean withWife,
            final boolean withChildren) {
        final Collection<Object> facts = new ArrayList<>();
        for (int i = 0; i < numberOfFacts; i++) {
            final Man man = new Man("Gizmo", i);
            if (withChildren) {
                generateChildrenOfMan(man);
            }
            if (withWife) {
                generateWifeOfMan(man);
            }
            facts.add(man);
        }
        return facts;
    }

    private void generateChildrenOfMan(final Man man) {
        man.addChild(new Child("Tandi", 20));
        man.addChild(new Child("VaultDweller", 25));
    }

    private void generateWifeOfMan(final Man man) {
        final Woman wife = new Woman("Beth", 45);
        wife.addChildren(man.getChildren());
        man.setWife(wife);
    }
}
