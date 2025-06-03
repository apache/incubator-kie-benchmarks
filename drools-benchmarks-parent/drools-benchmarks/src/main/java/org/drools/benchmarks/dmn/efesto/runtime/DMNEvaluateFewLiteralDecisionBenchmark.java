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

package org.drools.benchmarks.dmn.efesto.runtime;

import org.drools.benchmarks.common.DMNProvider;
import org.drools.benchmarks.common.ProviderException;
import org.drools.benchmarks.common.providers.dmn.DecisionDMNProvider;
import org.drools.benchmarks.dmn.efesto.DMNEfestoAbstractBenchmark;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;

import java.util.Collection;

@Warmup(iterations = 5000)
@Measurement(iterations = 2000)
@SuppressWarnings({"unchecked", "rawtypes"})
public class DMNEvaluateFewLiteralDecisionBenchmark extends DMNEfestoAbstractBenchmark {

    @Param({"20", "50", "100"})
    private int numberOfDecisions;


    @Setup
    public void setupResource() {
        final DMNProvider dmnProvider = new DecisionDMNProvider();
        String dmn = dmnProvider.getDMN(numberOfDecisions);
        String modelName = dmnProvider.getModelName();
        generatedResources = compileModel(dmn, modelName);
        modelLocalUriId = getModelLocalUriId(generatedResources);
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() throws ProviderException {
        inputData = getInputData();
        inputData.put("Full Name", "John Doe");
        runtimeContext = getRuntimeContext(generatedResources, modelLocalUriId);
    }

    @Benchmark
    public Collection<EfestoOutput> evaluateDecision() {
        return evaluate(runtimeContext, modelLocalUriId, inputData);
    }
}
