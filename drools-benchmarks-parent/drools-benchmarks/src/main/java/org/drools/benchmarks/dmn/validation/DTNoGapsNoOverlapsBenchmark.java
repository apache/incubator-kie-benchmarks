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

package org.drools.benchmarks.dmn.validation;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.benchmarks.common.DMNProvider;
import org.drools.benchmarks.common.ProviderException;
import org.drools.benchmarks.common.providers.dmn.DTNoGapsNoOverlapsDMNProvider;
import org.drools.benchmarks.dmn.util.DMNUtil;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.validation.DMNValidator.Validation;
import org.kie.dmn.validation.dtanalysis.DMNDTAnalyser;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;
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
public class DTNoGapsNoOverlapsBenchmark extends AbstractBenchmark {

    private static final Logger LOG = LoggerFactory.getLogger(DTNoGapsNoOverlapsBenchmark.class);
    private static final Set<Validation> FLAGS = new HashSet<>(Arrays.asList(Validation.ANALYZE_DECISION_TABLE));

    /**
     * DMN with param=9 means
     * 9 InputData
     * decision table of 512 rows.
     * Will perform the full analysis of the hypercube of 9 dimensions, complete, with no gaps and overlaps
     */
    @Param({"9"})
    private int param; // careful: this will generate 2^param combinations.

    private Resource dmnResource;
    private DMNRuntime dmnRuntime;
    private DMNModel dmnModel;
    private DMNDTAnalyser dmndtAnalyser;

    public static void main(String[] args) throws Exception {
        try (Scanner scanner = new Scanner(System.in)) {
            DTNoGapsNoOverlapsBenchmark instance = new DTNoGapsNoOverlapsBenchmark();
            instance.param = 7;
            instance.setupResource();
            instance.setup();
            //            System.out.println("Press ENTER to continue... ");
            //            scanner.nextLine();
            instance.performAnalysis();
        }
    }

    @Setup
    public void setupResource() throws IOException {
        final DMNProvider dmnProvider = new DTNoGapsNoOverlapsDMNProvider();
        String dmnContent = dmnProvider.getDMN(param);
        LOG.debug("{}", dmnContent);
        dmnResource = KieServices.get().getResources()
                                 .newReaderResource(new StringReader(dmnContent))
                                 .setResourceType(ResourceType.DMN)
                                 .setSourcePath("dmnFile.dmn");
        dmnRuntime = DMNUtil.getDMNRuntimeWithResources(false, dmnResource);
        dmnModel = dmnRuntime.getModel("https://github.com/kiegroup/kie-dmn", "dt-nogapsnooverlaps");
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() throws ProviderException {
        dmndtAnalyser = new DMNDTAnalyser(Collections.emptyList());
    }

    @Benchmark
    public List<DTAnalysis> performAnalysis() {
        List<DTAnalysis> analyse = dmndtAnalyser.analyse(dmnModel, FLAGS);
        if (LOG.isDebugEnabled()) {
            LOG.debug("{}", analyse.get(0).asDMNMessages()); // full analysis no gaps no overlaps, U policy: DMN: Decision Table Analysis of table 'decision' finished with no messages to be reported.
        }
        return analyse;
    }

}
