/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.benchmarks.throughput;

public class ThroughputBenchmarkMain {

    private static final boolean oneTriggerOne = false;

    private static final int BENCHMARK_DURATION_IN_SECONDS = 10;
    private static final int BENCHMARK_DURATION_IN_EVENTS = 10;

    private static final boolean DO_WARMUP = false;
    private static final int WARMUP_INSERTS = 2_000_000;

    public static void main( String[] args ) {
        AbstractFireUntilHaltThroughputBenchmark benchmark = oneTriggerOne ?
                                                             new OneEventTriggersOneAgendaBenchmark() :
                                                             new OneEventTriggersAllAgendasBenchmark();
        benchmark.setupKieBase();
        benchmark.setup();
        benchmark.setupCounter();

        if (DO_WARMUP) {
            for ( int i = 0; i < WARMUP_INSERTS; i++ ) {
                benchmark.insertEvent( null , null);
            }

            terminate( benchmark );
            System.gc();
            benchmark.setup();
            benchmark.setupCounter();
        }

        long start = System.nanoTime();
        long end = start + ( BENCHMARK_DURATION_IN_SECONDS * 1_000_000_000L );

        int i = 0;
        while (BENCHMARK_DURATION_IN_EVENTS < 0 || i < BENCHMARK_DURATION_IN_EVENTS) {
            benchmark.insertEvent(null, null);
            i++;
            if (System.nanoTime() > end) {
                break;
            }
        }

        try {
            Thread.sleep( 1000L );
        } catch (InterruptedException e) {
            throw new RuntimeException( e );
        }

        System.out.println("inserts = " + i);
        System.out.println("firings = " + benchmark.getFiringsCount());

        terminate( benchmark );
    }

    private static void terminate( AbstractFireUntilHaltThroughputBenchmark benchmark ) {
        benchmark.tearDown();
        benchmark.stopFireUntilHaltThread();
    }
}
