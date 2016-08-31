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

package org.drools.benchmarks.throughput;

import org.drools.benchmarks.common.ProviderException;
import org.drools.benchmarks.domain.event.EventA;
import org.drools.benchmarks.domain.event.EventB;
import org.drools.benchmarks.domain.event.EventC;
import org.drools.benchmarks.domain.event.EventD;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.conf.MultithreadEvaluationOption;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.infra.Blackhole;

// TODO - think about merging benchmarks with different number of partitions into one parametrized (how it will influence benchmark time).
public class EventThroughput4PartitionsBenchmark extends AbstractFireUntilHaltThroughputBenchmark {

    @Param({"true", "false"})
    private boolean multithread;

    @Setup
    public void setupKieBase() throws ProviderException {
        final String drl = " import " + EventA.class.getCanonicalName() + ";\n" +
                " import " + EventB.class.getCanonicalName() + ";\n" +
                " import " + EventC.class.getCanonicalName() + ";\n" +
                " import " + EventD.class.getCanonicalName() + ";\n" +
                " declare " + EventA.class.getName() + " @role( event ) @duration(duration) end\n" +
                " declare " + EventB.class.getName() + " @role( event ) @duration(duration) end\n" +
                " declare " + EventC.class.getName() + " @role( event ) @duration(duration) end\n" +
                " declare " + EventD.class.getName() + " @role( event ) @duration(duration) end\n" +
                " rule R1 \n" +
                " when \n" +
                "     EventA()\n" +
                " then \n" +
                " end" +

                " rule R2 \n" +
                " when \n" +
                "     EventB()\n" +
                " then \n" +
                " end" +

                " rule R3 \n" +
                " when \n" +
                "     EventC()\n" +
                " then \n" +
                " end" +

                " rule R4 \n" +
                " when \n" +
                "     EventD()\n" +
                " then \n" +
                " end";

        createKieBaseFromDrl(
                drl,
                EventProcessingOption.STREAM,
                multithread ? MultithreadEvaluationOption.YES : MultithreadEvaluationOption.NO);
    }

    @Benchmark
    @Threads(1)
    public void insertEvent(final Blackhole eater) {
        eater.consume(kieSession.insert(new EventA(40)));
        eater.consume(kieSession.insert(new EventB(40)));
        eater.consume(kieSession.insert(new EventC(40)));
        eater.consume(kieSession.insert(new EventD(40)));
    }
}
