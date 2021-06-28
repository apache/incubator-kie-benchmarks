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

package org.drools.benchmarks.pmml.runtime.clustering;

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

@State(Scope.Benchmark)
@Warmup(iterations = 300)
@Measurement(iterations = 50)
public class PMMLEvaluateSingleIrisKMeansClusteringBenchmark extends AbstractBenchmark {

    public static final String MODEL_NAME = "SingleIrisKMeansClustering";
    public static final String FILE_NAME = "SingleIrisKMeansClustering.pmml";
    public static final String FILE_PATH = "pmml/" + FILE_NAME;

    private Resource pmmlResource;
    private PMMLRuntime pmmlRuntime;

    private static final Map<String, Object> INPUT_DATA;
    private static final PMMLContext pmmlContext;

    static {
        INPUT_DATA = new HashMap<>();
        INPUT_DATA.put("sepal_length", 4.4);
        INPUT_DATA.put("sepal_width", 3.0);
        INPUT_DATA.put("petal_length", 1.3);
        INPUT_DATA.put("petal_width",  0.2);
        PMMLRequestData pmmlRequestData = new PMMLRequestData("123", MODEL_NAME);
        INPUT_DATA.forEach(pmmlRequestData::addRequestParam);
        pmmlContext = new PMMLContextImpl(pmmlRequestData);
    }

    @Setup
    public void setupResource() throws IOException {
        pmmlResource = KieServices.get().getResources()
                .newClassPathResource(FILE_PATH);
        pmmlRuntime = PMMLUtil.getPMMLRuntimeWithResources(true, pmmlResource);
    }

    @Override
    public void setup() throws ProviderException {
        // noop
    }

    @Benchmark
    public PMML4Result evaluatePrediction() {
        return pmmlRuntime.evaluate(MODEL_NAME, pmmlContext);
    }
}
