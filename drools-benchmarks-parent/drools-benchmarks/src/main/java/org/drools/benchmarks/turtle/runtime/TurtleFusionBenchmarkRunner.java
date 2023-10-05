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

package org.drools.benchmarks.turtle.runtime;

import java.io.IOException;

public class TurtleFusionBenchmarkRunner {
    /**
     * When run, JVM option -Ddrools.parallelExecution=fully_parallel must be set.
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // Choose your benchmark here
        // Any benchmark that extends AbstractSimpleFusionRuntimeBenchmark can be put here
        final StartsStartedbyFusionBenchmark benchmark = new StartsStartedbyFusionBenchmark();
        benchmark.nrOfEvents = 200000;
        benchmark.createKBase();
        benchmark.initFactsGenerators();
        benchmark.initEventSenders();
        for (int i = 1; i <= 100; i++) {
            benchmark.gatherFacts();
            benchmark.createKieSession();
            try {
                System.out.println("Iteration " + i + "...");
                benchmark.timeEventsInsertionAndRulesFiring();
                System.out.println("Iteration " + i + " done.");
                System.out.println("------------------------");
            } finally {
                benchmark.dispose();
                benchmark.disposeKSession();
            }
        }
    }
}
