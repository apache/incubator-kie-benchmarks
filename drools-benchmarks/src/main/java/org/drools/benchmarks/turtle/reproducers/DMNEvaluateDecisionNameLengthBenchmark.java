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

package org.drools.benchmarks.turtle.reproducers;

import java.io.IOException;

import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.benchmarks.common.ProviderException;
import org.drools.benchmarks.dmn.util.DMNUtil;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.core.api.DMNFactory;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;

@Warmup(iterations = 30)
@Measurement(iterations = 15)
@Fork(3)
public class DMNEvaluateDecisionNameLengthBenchmark extends AbstractBenchmark {

    @Param({"example_short_names.xml", "example_long_names.xml"})
    private String dmnFileName;

    private Resource dmnResource;
    private DMNRuntime dmnRuntime;
    private DMNModel dmnModel;
    private DMNContext dmnContext;

    @Setup
    public void setupResource() throws IOException {
        dmnResource = KieServices.get().getResources().newClassPathResource("dmn/" + dmnFileName).setResourceType(ResourceType.DMN);
        dmnRuntime = DMNUtil.getDMNRuntimeWithResources(false, dmnResource);
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() throws ProviderException {
        dmnModel = dmnRuntime.getModels().get(0);
        dmnContext = getTestContext();
    }

    private DMNContext getTestContext() {
        final DMNContext context = DMNFactory.newContext();
        for (InputDataNode input : dmnModel.getInputs()) {
            context.set(input.getName(), Boolean.TRUE);
        }
        return context;
    }

    @Benchmark
    public DMNResult evaluateDecision() {
        return dmnRuntime.evaluateAll(dmnModel, dmnContext);
    }
}
