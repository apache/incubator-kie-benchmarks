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

/*
BASELINE
Result "org.drools.benchmarks.dmn.runtime.DMNEvaluateDecisionTablesSparseBenchmark.evaluateDecision":
  N = 250
  mean =     18.447 ±(99.9%) 0.633 ms/op

  Histogram, ms/op:
    [10.000, 12.500) = 0 
    [12.500, 15.000) = 13 
    [15.000, 17.500) = 105 
    [17.500, 20.000) = 78 
    [20.000, 22.500) = 35 
    [22.500, 25.000) = 12 
    [25.000, 27.500) = 3 
    [27.500, 30.000) = 2 
    [30.000, 32.500) = 0 
    [32.500, 35.000) = 1 
    [35.000, 37.500) = 1 

  Percentiles, ms/op:
      p(0.0000) =     14.240 ms/op
     p(50.0000) =     17.667 ms/op
     p(90.0000) =     21.888 ms/op
     p(95.0000) =     23.687 ms/op
     p(99.0000) =     31.485 ms/op
     p(99.9000) =     35.549 ms/op
     p(99.9900) =     35.549 ms/op
     p(99.9990) =     35.549 ms/op
     p(99.9999) =     35.549 ms/op
    p(100.0000) =     35.549 ms/op


# Run complete. Total time: 00:03:30

REMEMBER: The numbers below are just data. To gain reusable insights, you need to follow up on
why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
experiments, perform baseline and negative tests that provide experimental control, make sure
the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
Do not assume the numbers tell you what you want them to tell.

Benchmark                                                  (numberOfElements)  (sparseness)  Mode  Cnt   Score   Error  Units
DMNEvaluateDecisionTablesSparseBenchmark.evaluateDecision                 100            10    ss  250  18.447 ± 0.633  ms/op

AFTER
Result "org.drools.benchmarks.dmn.runtime.DMNEvaluateDecisionTablesSparseBenchmark.evaluateDecision":
  N = 250
  mean =      2.744 ±(99.9%) 0.180 ms/op

  Histogram, ms/op:
    [1.000, 1.500) = 0 
    [1.500, 2.000) = 4 
    [2.000, 2.500) = 136 
    [2.500, 3.000) = 50 
    [3.000, 3.500) = 22 
    [3.500, 4.000) = 17 
    [4.000, 4.500) = 10 
    [4.500, 5.000) = 4 
    [5.000, 5.500) = 3 
    [5.500, 6.000) = 1 
    [6.000, 6.500) = 1 
    [6.500, 7.000) = 1 
    [7.000, 7.500) = 0 

  Percentiles, ms/op:
      p(0.0000) =      1.965 ms/op
     p(50.0000) =      2.438 ms/op
     p(90.0000) =      3.921 ms/op
     p(95.0000) =      4.450 ms/op
     p(99.0000) =      6.486 ms/op
     p(99.9000) =      7.875 ms/op
     p(99.9900) =      7.875 ms/op
     p(99.9990) =      7.875 ms/op
     p(99.9999) =      7.875 ms/op
    p(100.0000) =      7.875 ms/op


# Run complete. Total time: 00:03:12

REMEMBER: The numbers below are just data. To gain reusable insights, you need to follow up on
why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
experiments, perform baseline and negative tests that provide experimental control, make sure
the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
Do not assume the numbers tell you what you want them to tell.

Benchmark                                                  (numberOfElements)  (sparseness)  Mode  Cnt  Score   Error  Units
DMNEvaluateDecisionTablesSparseBenchmark.evaluateDecision                 100            10    ss  250  2.744 ± 0.180  ms/op

 */
