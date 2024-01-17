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

package org.drools.benchmarks.reliability;

import org.drools.benchmarks.common.DRLProvider;
import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.drools.benchmarks.reliability.proto.complex.ComplexA;
import org.drools.benchmarks.reliability.proto.complex.ComplexB;
import org.drools.benchmarks.reliability.proto.complex.ComplexC;
import org.drools.benchmarks.reliability.proto.complex.ComplexD;
import org.drools.benchmarks.reliability.providers.ComplexRulesWithJoinsProvider;
import org.drools.reliability.infinispan.InfinispanStorageManagerFactory;
import org.kie.api.conf.EventProcessingOption;
import org.kie.internal.conf.ParallelExecutionOption;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;

import java.util.ArrayList;
import java.util.List;

import static org.drools.benchmarks.reliability.proto.complex.StringListContainerUtils.populateStringLists;

@Warmup(iterations = 3000)
@Measurement(iterations = 1000)
public class InsertAndFireComplexFactBenchmark extends AbstractReliabilityBenchmark {

    @Param({"192"})
    private int rulesNr;

    @Param({"100"})
    private int factsNr;

    @Param({"1"})
    private int joinsNr;

    private ComplexA complexA;
    private List<ComplexB> complexBList = new ArrayList<>();
    private List<ComplexC> complexCList = new ArrayList<>();
    private List<ComplexD> complexDList = new ArrayList<>();

    @Override
    protected void setupSerializationContext() {
        // marshall ComplexA, ComplexB, ComplexC and ComplexD
        System.setProperty(InfinispanStorageManagerFactory.INFINISPAN_STORAGE_SERIALIZATION_CONTEXT_INITIALIZER,
                           "org.drools.benchmarks.reliability.proto.complex.ComplexABCDProtoStreamSchemaImpl");
    }

    @Setup
    public void setupKieBase() {
        final DRLProvider drlProvider = new ComplexRulesWithJoinsProvider(joinsNr, false, true);
        kieBase = BuildtimeUtil.createKieBaseFromDrl(true, drlProvider.getDrl(rulesNr),
                ParallelExecutionOption.SEQUENTIAL,
                EventProcessingOption.CLOUD);
    }

    @Setup
    public void setupFacts() {
        complexA = new ComplexA(rulesNr + 1);
        populateStringLists(complexA);
        for (int i = 0; i < factsNr; i++) {
            ComplexB complexB = new ComplexB(rulesNr + i + 3);
            populateStringLists(complexB);
            complexBList.add(complexB);
            if (joinsNr > 1) {
                ComplexC complexC = new ComplexC(rulesNr + factsNr + i + 3);
                populateStringLists(complexC);
                complexCList.add(complexC);
            }
            if (joinsNr > 2) {
                ComplexD complexD = new ComplexD(rulesNr + factsNr * 2 + i + 3);
                populateStringLists(complexD);
                complexDList.add(complexD);
            }
        }
    }



    @Override
    public void populateKieSessionPerIteration() {
        // no op
    }

    @Benchmark
    public int test() {
        kieSession.insert(complexA);
        for (int i = 0; i < factsNr; i++) {
            kieSession.insert(complexBList.get(i));
            if (joinsNr > 1) {
                kieSession.insert(complexCList.get(i));
            }
            if (joinsNr > 2) {
                kieSession.insert(complexDList.get(i));
            }
        }
        return kieSession.fireAllRules();
    }
}