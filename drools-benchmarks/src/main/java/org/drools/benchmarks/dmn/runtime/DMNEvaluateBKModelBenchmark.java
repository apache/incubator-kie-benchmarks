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

package org.drools.benchmarks.dmn.runtime;

import java.io.IOException;
import java.io.StringReader;

import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.benchmarks.common.DMNProvider;
import org.drools.benchmarks.common.ProviderException;
import org.drools.benchmarks.common.providers.dmn.BusinessKnowledgeModelDMNProvider;
import org.drools.benchmarks.dmn.util.DMNUtil;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;

public class DMNEvaluateBKModelBenchmark extends AbstractBenchmark {

    @Param({"3000"})
    private int numberOfDecisionsWithBKM;

    private DMNRuntime dmnRuntime;
    private DMNModel dmnModel;
    private DMNContext dmnContext;

    @Setup(Level.Iteration)
    @Override
    public void setup() throws ProviderException {
        final DMNProvider dmnProvider = new BusinessKnowledgeModelDMNProvider();
        final Resource dmnResource = KieServices.get().getResources()
                .newReaderResource(new StringReader(dmnProvider.getDMN(numberOfDecisionsWithBKM)))
                .setResourceType(ResourceType.DMN)
                .setSourcePath("dmnFile.dmn");
        try {
            dmnRuntime = DMNUtil.getDMNRuntimeWithResources(false, dmnResource);
            dmnModel = dmnRuntime.getModel("https://github.com/kiegroup/kie-dmn", "business-knowledge-model");
            dmnContext = dmnRuntime.newContext();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public DMNResult evaluateBusinessKnowledgeModel() {
        return dmnRuntime.evaluateAll(dmnModel, dmnContext);
    }
}
