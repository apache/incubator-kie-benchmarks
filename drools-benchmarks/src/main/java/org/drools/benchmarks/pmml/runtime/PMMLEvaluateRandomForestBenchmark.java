/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.benchmarks.pmml.runtime;

import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.benchmarks.common.ProviderException;
import org.drools.benchmarks.pmml.util.PMMLUtil;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.pmml.api.runtime.PMMLContext;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.evaluator.core.PMMLContextImpl;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Warmup(iterations = 300)
@Measurement(iterations = 50)
public class PMMLEvaluateRandomForestBenchmark extends AbstractBenchmark {

    public static final String MODEL_NAME = "RandomForest";
    public static final String FILE_NAME = "RandomForest.pmml";
    public static final String FILE_PATH = "pmml/" + FILE_NAME;

    private Resource pmmlResource;
    private PMMLRuntime pmmlRuntime;
    private PMMLContext pmmlContext;
    private PMMLRequestData pmmlRequestData;

    private static final Map<String, Object> INPUT_DATA;

    static {
        INPUT_DATA = new HashMap<>();
        INPUT_DATA.put("Age", 40.83);
        INPUT_DATA.put("MonthlySalary", 3.5);
        INPUT_DATA.put("TotalAsset", 0.04);
        INPUT_DATA.put("TotalRequired", 10.04);
        INPUT_DATA.put("NumberInstallments", 93.2);
    }

    @Setup
    public void setupResource() throws IOException {
        pmmlResource = KieServices.get().getResources()
                .newClassPathResource(FILE_PATH);
        System.out.println("pmmlResource " + pmmlResource.getSourcePath());
        pmmlRuntime = PMMLUtil.getPMMLRuntimeWithResources(true, pmmlResource);
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() throws ProviderException {
        pmmlRequestData = new PMMLRequestData("123", MODEL_NAME);
        INPUT_DATA.forEach(pmmlRequestData::addRequestParam);
        pmmlContext = new PMMLContextImpl(pmmlRequestData);
    }

    @Benchmark
    public PMML4Result evaluateDecision() {
        return pmmlRuntime.evaluate(MODEL_NAME, pmmlContext);
    }
}
