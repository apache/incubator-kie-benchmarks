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

package org.drools.benchmarks.dmn.efesto.compilation;

import org.drools.benchmarks.common.ProviderException;
import org.drools.benchmarks.dmn.efesto.DMNEfestoAbstractBenchmark;
import org.drools.io.ClassPathResource;
import org.kie.api.io.Resource;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 15, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 15, time = 2, timeUnit = TimeUnit.SECONDS)
public class DMNCompilationComplexDMNModelBenchmark extends DMNEfestoAbstractBenchmark {

    private Resource dmnResource;

    @Param({"dmn/ch11MODIFIED.dmn"})
    private String resourceName;
    private String modelName;

    @Setup
    @Override
    public void setup() throws ProviderException {
        dmnResource = new ClassPathResource(resourceName);
        modelName = "ch11MODIFIED";
    }

    @Benchmark
    public Map<String, GeneratedResources> testGetGeneratedResourcesMap() {
        return compileModel(dmnResource, modelName);
    }
}
