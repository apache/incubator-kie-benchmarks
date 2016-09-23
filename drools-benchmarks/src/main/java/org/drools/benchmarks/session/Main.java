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

package org.drools.benchmarks.session;

import org.openjdk.jmh.infra.Blackhole;

public class Main {
    public static void main( String[] args ) {
        InsertFireLoopBenchmark benchmark = new InsertFireLoopBenchmark();
        System.out.println("setupKieBase");
        benchmark.setupKieBase();
        System.out.println("setup");
        benchmark.setup();
        System.out.println("test");
        benchmark.test(new Blackhole("Today\'s password is swordfish. I understand instantiating Blackholes directly is dangerous."));
        System.out.println("done");
    }
}
