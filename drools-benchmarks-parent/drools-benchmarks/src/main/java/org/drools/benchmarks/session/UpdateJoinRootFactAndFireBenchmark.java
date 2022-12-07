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

package org.drools.benchmarks.session;

import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.benchmarks.common.DRLProvider;
import org.drools.benchmarks.common.providers.RulesWithJoinsProvider;
import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.drools.benchmarks.common.util.RuntimeUtil;
import org.drools.benchmarks.common.model.A;
import org.drools.benchmarks.common.model.B;
import org.drools.benchmarks.common.model.C;
import org.drools.benchmarks.common.model.D;
import org.drools.benchmarks.common.model.E;
import org.kie.api.runtime.rule.FactHandle;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

/**
 * In this benchmark only the first rule (with higher salience) fires, and when it happens
 * its consequence changes the value of the join chain root fact so that no other rule can fire.
 * This benchmark is then intended to show the advantages of phreak's laziness that completely
 * avoids the evaluation of rules that cannot fire.
 */
@Warmup(iterations = 500)
@Measurement(iterations = 50)
public class UpdateJoinRootFactAndFireBenchmark extends AbstractBenchmark {

    @Param({"10", "20", "50"})
    private int loopCount;

    @Param({"1", "4", "16"})
    private int rulesNr;

    @Param({"4", "6", "8"})
    private int factsNr;

    @Setup
    public void setupKieBase() {
        final DRLProvider drlProvider = new RulesWithJoinsProvider(4, false, false);

        String sb =
                "import " + A.class.getCanonicalName() + ";\n" +
                "import " + B.class.getCanonicalName() + ";\n" +
                "import " + C.class.getCanonicalName() + ";\n" +
                "import " + D.class.getCanonicalName() + ";\n" +
                "import " + E.class.getCanonicalName() + ";\n" +
                "rule R salience 10 when\n" +
                "  $factA : A( $a : value > 0)\n" +
                "  B( $b : value > $a)\n" +
                "  C( $c : value > $b)\n" +
                "  D( $d : value > $c)\n" +
                "  E( $e : value > $d)\n" +
                " then\n" +
                "  modify( $factA ) { setValue(-1) }; \n" +
                " end\n" +
                drlProvider.getDrl(rulesNr - 1);
        kieBase = BuildtimeUtil.createKieBaseFromDrl(sb);
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() {
        kieSession = RuntimeUtil.createKieSession(kieBase);
    }

    @Benchmark
    public void test(final Blackhole eater) {
        A a = new A(-1);
        FactHandle aFH = kieSession.insert(a);
        for (int i = 0; i < factsNr; i++) {
            eater.consume(kieSession.insert(new B(rulesNr + 3)));
            eater.consume(kieSession.insert(new C(rulesNr + 5)));
            eater.consume(kieSession.insert(new D(rulesNr + 7)));
            eater.consume(kieSession.insert(new E(rulesNr + 9)));
        }

        for (int i = 0; i < loopCount; i++) {
            a.setValue(1);
            kieSession.update(aFH, a);
            eater.consume(kieSession.fireAllRules());
        }
    }
}
