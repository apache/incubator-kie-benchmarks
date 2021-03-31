/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.drools.benchmarks.dmn.example;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

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
import org.kie.dmn.core.compiler.ExecModelCompilerOption;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;

import static org.drools.benchmarks.dmn.util.DynamicTypeUtils.entry;
import static org.drools.benchmarks.dmn.util.DynamicTypeUtils.mapOf;

@Warmup(iterations = 200)
@Measurement(iterations = 100)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class DMNCH11ExampleBenchmark extends AbstractBenchmark {

    private Resource dmnResource;
    private DMNRuntime dmnRuntime;
    private DMNModel dmnModel;
    private DMNContext dmnContext;

    @Param({"true", "false"})
    private boolean useExecModelCompiler;

    @Setup
    public void setupResource() throws IOException {
        System.setProperty(ExecModelCompilerOption.PROPERTY_NAME, Boolean.toString(useExecModelCompiler));
        dmnResource = KieServices.get().getResources().newClassPathResource("dmn/ch11MODIFIED.dmn").setResourceType(ResourceType.DMN);
        dmnRuntime = DMNUtil.getDMNRuntimeWithResources(false, dmnResource);
        dmnModel = dmnRuntime.getModel("http://www.trisotech.com/definitions/_3068644b-d2c7-4b81-ab9d-64f011f81f47", "DMN Specification Chapter 11 Example");
        System.clearProperty(ExecModelCompilerOption.PROPERTY_NAME); // safe to do here, after model compilation.
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() throws ProviderException {
        dmnContext = dmnRuntime.newContext();
        dmnContext.set("Applicant data", mapOf(entry("Age", new BigDecimal(51)),
                                            entry("MaritalStatus", "M"),
                                            entry("EmploymentStatus", "EMPLOYED"),
                                            entry("ExistingCustomer", false),
                                            entry("Monthly", mapOf(entry("Income", new BigDecimal(100_000)),
                                                                   entry("Repayments", new BigDecimal(2_500)),
                                                                   entry("Expenses", new BigDecimal(10_000)))))); // DMN v1.2 spec page 181, first image: errata corrige values for Income and Expenses are likely inverted, corrected here.
        dmnContext.set("Bureau data", mapOf(entry("Bankrupt", false),
                                         entry("CreditScore", new BigDecimal(600))));
        dmnContext.set("Requested product", mapOf(entry("ProductType", "STANDARD LOAN"),
                                               entry("Rate", new BigDecimal(0.08)),
                                               entry("Term", new BigDecimal(36)),
                                               entry("Amount", new BigDecimal(100_000))));
        dmnContext.set("Supporting documents", null);
    }

    @Benchmark
    public DMNResult evaluate() {
        return dmnRuntime.evaluateAll(dmnModel, dmnContext);
    }

    public static void main(String[] args) throws Exception {
        DMNCH11ExampleBenchmark a = new DMNCH11ExampleBenchmark();
        a.useExecModelCompiler = true;
        a.setupResource();
        a.setup();
        System.out.println(ExecModelCompilerOption.PROPERTY_NAME + "=" + a.useExecModelCompiler);
        System.out.println(a.evaluate());
        a.useExecModelCompiler = false;
        a.setupResource();
        a.setup();
        System.out.println(ExecModelCompilerOption.PROPERTY_NAME + "=" + a.useExecModelCompiler);
        System.out.println(a.evaluate());
    }
}
