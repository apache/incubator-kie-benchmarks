/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.drools.benchmarks.turtle.runtime;

import java.io.IOException;

public class TurtleBenchmarkRunner {
    /**
     * When run, JVM option -Ddrools.parallelExecution=fully_parallel must be set.
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // Choose your benchmark here
        // Any benchmark that extends AbstractSimpleRuntimeBenchmark can be put here
        // (except CEP benchmarks, see TurtleFusionBenchmarkRunner).
        final RetractFactsFromWmExpertBenchmark benchmark = new RetractFactsFromWmExpertBenchmark();
//        final AdvOperators3ExpertBenchmark benchmark = new AdvOperators3ExpertBenchmark();
        // final UpdateFactsInWmExpertBenchmark benchmark = new UpdateFactsInWmExpertBenchmark();
        benchmark.createKBase();
        benchmark.initFactsGenerators();
        for (int i = 1; i <= 100; i++) {
            benchmark.gatherFacts();
            try {
                System.out.println("Iteration " + i + "...");
                benchmark.timeFactsInsertionAndRulesFiring();
                System.out.println("Iteration " + i + " done.");
                System.out.println("------------------------");
            } finally {
                benchmark.disposeKSession();
            }
        }
    }
}
