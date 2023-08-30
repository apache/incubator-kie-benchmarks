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

package org.drools.benchmarks.dmn.runtime;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.benchmarks.common.ProviderException;
import org.drools.benchmarks.dmn.runtime.model.Person;
import org.drools.benchmarks.dmn.util.DMNUtil;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@Warmup(iterations = 40000)
@Measurement(iterations = 10000)
public class DMNEvaluatePojoInputBenchmark extends AbstractBenchmark {

    @Param({"dmn/pojo-input.dmn"})
    private String resourceName;

    private DMNRuntime dmnRuntime;
    private DMNModel dmnModel;
    private DMNContext dmnContext;

    @State(Scope.Benchmark)
    public static class IterationCounter {
        public static AtomicInteger value = new AtomicInteger(0);
    }

    @Setup
    public void setupResource() {
        final Resource dmnResource = KieServices.Factory.get().getResources().newClassPathResource(resourceName);
        try {
            dmnRuntime = DMNUtil.getDMNRuntimeWithResources(false, dmnResource);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        dmnModel = dmnRuntime.getModel("http://www.trisotech.com/dmn/definitions/_7a39d775-bce9-45e3-aa3b-147d6f0028c7", "20180731-pr1997");
    }

    @Setup(Level.Iteration)
    @Override
    public void setup() throws ProviderException {
        dmnContext = dmnRuntime.newContext();
        dmnContext.set("a Person", new Person("John", "Doe", IterationCounter.value.incrementAndGet()));

    }

    @Benchmark
    public DMNResult evaluateModel() {
        return dmnRuntime.evaluateAll(dmnModel, dmnContext);
    }
}
