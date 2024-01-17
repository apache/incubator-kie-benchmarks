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

package org.drools.benchmarks.cep;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.benchmarks.common.ProviderException;
import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.drools.benchmarks.common.util.RuntimeUtil;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.time.SessionPseudoClock;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.infra.Blackhole;

public class SlidingTimeWindowBenchmark extends AbstractBenchmark {

    private static final String drl =
            "package org.drools.benchmark\n" +
            "import " + StockTick.class.getCanonicalName() + ";\n" +
            "global " + AtomicReference.class.getCanonicalName() + " result;\n" +
            "\n" +
            "declare StockTick \n" +
            "    @role( event )\n" +
            "    @expires( 15s )\n" +
            "end\n" +
            "\n" +
            "rule \"TestEventReceived\"\n" +
            "no-loop\n" +
            "when\n" +
            "   accumulate( StockTick( company == \"ACME\", $price : price ) over window:time( 10s ), $sum : sum( $price ) )" +
            "then\n" +
            "   result.set( $sum );\n" +
            "end\n";

    @Setup
    public void setupKieBase() {
        kieBase = BuildtimeUtil.createKieBaseFromDrl(drl, EventProcessingOption.STREAM);
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() throws ProviderException {
        kieSession = RuntimeUtil.createKieSession(kieBase, ClockTypeOption.get("pseudo"));
        kieSession.setGlobal("result", new AtomicReference<>());
    }


    @Benchmark
    public void benchmark(Blackhole bh) {
        SessionPseudoClock clock = kieSession.getSessionClock();
        for (int i = 0; i < 1_000_000; i++) {
            kieSession.insert(new StockTick("ACME", 1));
            clock.advanceTime(10, TimeUnit.MILLISECONDS);
            if (i % 100 == 0) {
                bh.consume(kieSession.fireAllRules());
            }
        }
    }

    public static class StockTick {
        private final String company;
        private final double price;

        public StockTick(String company, double price) {
            this.company = company;
            this.price = price;
        }

        public String getCompany() {
            return company;
        }

        public double getPrice() {
            return price;
        }
    }
}
