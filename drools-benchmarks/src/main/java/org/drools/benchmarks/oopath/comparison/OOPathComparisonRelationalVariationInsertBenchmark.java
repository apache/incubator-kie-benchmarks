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

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.infra.Blackhole;

public class OOPathComparisonRelationalVariationInsertBenchmark extends AbstractOOPathComparisonBenchmark {

    @Setup
    @Override
    public void setupKieBase() {
        final String drl = MODEL_PACKAGE_IMPORT + "\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "    $man : Man()\n" +
                "    $wife : Woman( husband == $man.name )\n" +
                "    $child : Child( mother == $wife.name, age > 10 )\n" +
                "    $toy : Toy( owner == $child.name )\n" +
                "then\n" +
                "    list.add( $toy.getName() );\n" +
                "end\n";
        createKieBaseFromDrl(drl);
    }

    @Benchmark
    public int testInsert(final Blackhole eater) {
        eater.consume(insertFullModel(kieSession, parentFacts));
        return kieSession.fireAllRules();
    }
}
