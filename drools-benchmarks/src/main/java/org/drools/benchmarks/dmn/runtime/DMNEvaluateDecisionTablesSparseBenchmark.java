/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import java.util.Scanner;

import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.benchmarks.common.DMNProvider;
import org.drools.benchmarks.common.ProviderException;
import org.drools.benchmarks.common.providers.dmn.DecisionTablesDMNProvider;
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
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Warmup(iterations = 100)
@Measurement(iterations = 50)
public class DMNEvaluateDecisionTablesSparseBenchmark extends AbstractBenchmark {

    private static final Logger LOG = LoggerFactory.getLogger(DMNEvaluateDecisionTablesSparseBenchmark.class);

    @Param({"100"})
    private int numberOfElements;
    @Param({"10"})
    private int sparseness;

    private Resource dmnResource;
    private DMNRuntime dmnRuntime;
    private DMNModel dmnModel;
    private DMNContext dmnContext;

    public static void main(String[] args) throws Exception {
        try (Scanner scanner = new Scanner(System.in)) {
            DMNEvaluateDecisionTablesSparseBenchmark instance = new DMNEvaluateDecisionTablesSparseBenchmark();
            instance.numberOfElements = 100;
            instance.sparseness = 10;
            instance.setupResource();
            instance.setup();
            System.out.println("Press ENTER to continue... ");
            scanner.nextLine();
            for (int i = 0; i < 1_000; i++) {
                instance.evaluateDecision();
            }
        }
    }

    @Setup
    public void setupResource() {
        final DMNProvider dmnProvider = new DecisionTablesDMNProvider();
        String dmnContent = dmnProvider.getDMN(numberOfElements);
        LOG.debug("{}", dmnContent);
        dmnResource = KieServices.get().getResources()
                                 .newReaderResource(new StringReader(dmnContent))
                                 .setResourceType(ResourceType.DMN)
                                 .setSourcePath("dmnFile.dmn");
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() throws ProviderException {
        try {
            dmnRuntime = DMNUtil.getDMNRuntimeWithResources(false, dmnResource);
            dmnModel = dmnRuntime.getModel("https://github.com/kiegroup/kie-dmn", "decision-table-name");
            dmnContext = dmnRuntime.newContext();
            for (int i = 0; i < numberOfElements; i++) {
                if (i % sparseness == 0) {
                    dmnContext.set("leftInput_" + i, "a");
                    dmnContext.set("rightInput_" + i, "x");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public DMNResult evaluateDecision() {
        DMNResult evaluateAll = dmnRuntime.evaluateAll(dmnModel, dmnContext);
        LOG.debug("{}", evaluateAll);
        return evaluateAll;
    }
}

