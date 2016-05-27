/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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
import org.drools.benchmarks.common.DrlProvider;
import org.drools.benchmarks.common.providers.RulesWithJoins;
import org.drools.benchmarks.domain.*;
import org.kie.api.runtime.rule.FactHandle;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;

/**
 * This benchmark updates multiple times all the facts of a long join chain, but fires
 * only at the end of the update loop.
 */
public class UpdatesOnJoinBenchmark extends AbstractBenchmark {

    @Param({"1", "10", "100"})
    private int loopCount;

    @Param({"1", "4", "16"})
    private int rulesNr;

    @Param({"1", "4", "16"})
    private int factsNr;

    @Param({"true", "false"})
    private boolean insertLastJoinItem;

    @Param({"true", "false"})
    private boolean resetA;

    private A[] as;
    private B[] bs;
    private C[] cs;
    private D[] ds;
    private E[] es;

    private FactHandle[] aFHs;
    private FactHandle[] bFHs;
    private FactHandle[] cFHs;
    private FactHandle[] dFHs;
    private FactHandle[] eFHs;

    @Setup
    public void setupKieBase() {
        final int numberOfJoins = isSmokeTestsRun ? 3 : 4;
        final DrlProvider drlProvider = new RulesWithJoins(numberOfJoins, false, true);
        createKieBaseFromDrl(drlProvider.getDrl(rulesNr));
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() {
        createKieSession();

        as = new A[factsNr];
        bs = new B[factsNr];
        cs = new C[factsNr];
        ds = new D[factsNr];

        aFHs = new FactHandle[factsNr];
        bFHs = new FactHandle[factsNr];
        cFHs = new FactHandle[factsNr];
        dFHs = new FactHandle[factsNr];

        if (!isSmokeTestsRun) {
            es = new E[factsNr];
            eFHs = new FactHandle[factsNr];
        }
    }

    @Benchmark
    public void test() {
        for (int i = 0; i < factsNr; i++) {
            as[i] = new A( rulesNr + 1 );
            aFHs[i] = kieSession.insert( as[i] );
            bs[i] = new B( rulesNr + 3 );
            bFHs[i] = kieSession.insert( bs[i] );
            cs[i] = new C( rulesNr + 5 );
            cFHs[i] = kieSession.insert( cs[i] );

            if (isSmokeTestsRun) {
                if (insertLastJoinItem) {
                    ds[i] = new D( rulesNr + 7 );
                    dFHs[i] = kieSession.insert( ds[i] );
                }
            } else {
                ds[i] = new D( rulesNr + 7 );
                dFHs[i] = kieSession.insert( ds[i] );
                if (insertLastJoinItem) {
                    es[i] = new E( rulesNr + 9 );
                    eFHs[i] = kieSession.insert( es[i] );
                }
            }
        }

        for (int i = 0; i < loopCount; i++) {
            for (int j = 0; j < factsNr; j++) {
                as[j].setValue( as[j].getValue() + 1 );
                kieSession.update( aFHs[j], as[j] );
                bs[j].setValue( bs[j].getValue() + 1 );
                kieSession.update( bFHs[j], bs[j] );
                cs[j].setValue( cs[j].getValue() + 1 );
                kieSession.update( cFHs[j], cs[j] );

                if (isSmokeTestsRun) {
                    if (insertLastJoinItem) {
                        ds[j].setValue( ds[j].getValue() + 1 );
                        kieSession.update( dFHs[j], ds[j] );
                    }
                } else {
                    ds[j].setValue( ds[j].getValue() + 1 );
                    kieSession.update( dFHs[j], ds[j] );
                    if (insertLastJoinItem) {
                        es[j].setValue( es[j].getValue() + 1 );
                        kieSession.update( eFHs[j], es[j] );
                    }
                }
            }
            for (int j = 0; j < factsNr; j++) {
                if ( resetA ) {
                    as[j].setValue( -1 );
                    kieSession.update( aFHs[j], as[j] );
                }
            }
        }

        kieSession.fireAllRules();
    }
}
