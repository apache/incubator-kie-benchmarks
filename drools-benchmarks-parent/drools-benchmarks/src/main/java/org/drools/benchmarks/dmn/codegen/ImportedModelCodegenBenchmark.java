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

package org.drools.benchmarks.dmn.codegen;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.drools.benchmarks.dmn.util.DynamicTypeUtils.entry;
import static org.drools.benchmarks.dmn.util.DynamicTypeUtils.prototype;

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Thread)
@Warmup(iterations = 100, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 20, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class ImportedModelCodegenBenchmark extends AbstractCodegenBenchmark {

    @Override
    protected String getResource() {
        return "dmn/Importing_EmptyNamed_Model_With_Href_Namespace.dmn";
    }

    @Override
    protected List<String> getAdditionalResources() {
        return List.of("dmn/Imported_Model_Unamed.dmn");
    }

    @Override
    protected String getNameSpace() {
        return "http://www.trisotech.com/dmn/definitions/_f79aa7a4-f9a3-410a-ac95-bea496edabgc";
    }

    @Override
    protected String getModelName() {
        return "Importing empty-named Model";
    }

    @Override
    protected Map<String, Object> getInputData() {
        Map<String, Object> aPerson = prototype(entry("name", "John"), entry("age", 20));
        Map<String, Object> anImportedPerson = prototype(entry("name", "Luke"), entry("age", 35));
        return prototype(entry("A Person", aPerson), entry("An Imported Person", anImportedPerson));
    }

    @Setup()
    public void setupModelAndContext() {
        super.setupModelAndContext();
    }

    @Benchmark
    public Object evaluateModelBenchmark() {
        return super.evaluateModelBenchmark();
    }

    public static void main(String[] args) throws Exception {
        ImportedModelCodegenBenchmark a = new ImportedModelCodegenBenchmark();
        a.setupModelAndContext();
        System.out.println(a.evaluateModelBenchmark());
    }


}
