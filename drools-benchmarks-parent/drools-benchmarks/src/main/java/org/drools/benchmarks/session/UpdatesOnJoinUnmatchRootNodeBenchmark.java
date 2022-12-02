/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import org.kie.api.runtime.rule.FactHandle;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;

/**
 * This benchmark updates multiple times all the facts of a long join chain, but fires
 * only at the end of the update loop.
 */
@Warmup(iterations = 3000)
@Measurement(iterations = 500)
public class UpdatesOnJoinUnmatchRootNodeBenchmark extends AbstractBenchmark {

    @Param({"10", "20", "50"})
    private int loopCount;

    @Param({"4", "8", "16"})
    private int rulesNr;

    @Param({"8", "10", "12"})
    private int factsNr;

    private A[] as;
    private B[] bs;
    private C[] cs;
    private D[] ds;

    private FactHandle[] aFHs;
    private FactHandle[] bFHs;
    private FactHandle[] cFHs;
    private FactHandle[] dFHs;

    @Setup
    public void setupKieBase() {
        final DRLProvider drlProvider = new RulesWithJoinsProvider(3, false, true);
        kieBase = BuildtimeUtil.createKieBaseFromDrl(drlProvider.getDrl(rulesNr));
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() {
        kieSession = RuntimeUtil.createKieSession(kieBase);

        as = new A[factsNr];
        bs = new B[factsNr];
        cs = new C[factsNr];
        ds = new D[factsNr];

        aFHs = new FactHandle[factsNr];
        bFHs = new FactHandle[factsNr];
        cFHs = new FactHandle[factsNr];
        dFHs = new FactHandle[factsNr];
    }

    @Benchmark
    public int test() {
        for (int i = 0; i < factsNr; i++) {
            as[i] = new A(rulesNr + 1);
            aFHs[i] = kieSession.insert(as[i]);
            bs[i] = new B(rulesNr + 3);
            bFHs[i] = kieSession.insert(bs[i]);
            cs[i] = new C(rulesNr + 5);
            cFHs[i] = kieSession.insert(cs[i]);
            ds[i] = new D(rulesNr + 7);
            dFHs[i] = kieSession.insert(ds[i]);
        }

        for (int i = 0; i < loopCount; i++) {
            for (int j = 0; j < factsNr; j++) {
                as[j].setValue(as[j].getValue() + 1);
                kieSession.update(aFHs[j], as[j]);
                bs[j].setValue(bs[j].getValue() + 1);
                kieSession.update(bFHs[j], bs[j]);
                cs[j].setValue(cs[j].getValue() + 1);
                kieSession.update(cFHs[j], cs[j]);

                ds[j].setValue(ds[j].getValue() + 1);
                kieSession.update(dFHs[j], ds[j]);
            }
            // Unmatches all As, so no rule fires.
            for (int j = 0; j < factsNr; j++) {
                as[j].setValue(-1);
                kieSession.update(aFHs[j], as[j]);
            }
        }

        return kieSession.fireAllRules();
    }
}
