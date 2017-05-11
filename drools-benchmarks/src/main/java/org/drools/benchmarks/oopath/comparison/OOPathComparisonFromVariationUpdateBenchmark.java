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

import java.util.List;
import org.drools.core.common.InternalFactHandle;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;

public class OOPathComparisonFromVariationUpdateBenchmark extends AbstractOOPathComparisonBenchmark {

    @Setup
    @Override
    public void setupKieBase() {
        final String drl = MODEL_PACKAGE_IMPORT + "\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "    $man: Man( $wife: wife )\n" +
                "    $child: Child( age > 10 ) from $wife.children\n" +
                "    $toy: Toy() from $child.toys\n" +
                "then\n" +
                "    list.add( $toy.getName() );\n" +
                "end\n";
        createKieBaseFromDrl(drl);
    }

    private List<InternalFactHandle> factHandles;

    @Setup(Level.Iteration)
    public void insertFacts() {
        factHandles = insertModel(kieSession, parentFacts);
        kieSession.fireAllRules();
        globalList.clear();
    }

    @Benchmark
    public int testUpdate() {
        factsToBeModified.forEach(child -> child.setAge(11));
        factHandles.forEach(factHandle -> kieSession.update(factHandle, factHandle.getObject()));
        return kieSession.fireAllRules();
    }
}
