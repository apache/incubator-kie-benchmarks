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

package org.drools.benchmarks.pmml.compilation.tree;

import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.benchmarks.common.ProviderException;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.openjdk.jmh.annotations.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.drools.benchmarks.pmml.util.PMMLUtil.*;

@State(Scope.Benchmark)
@Warmup(iterations = 300)
@Measurement(iterations = 50)
public class PMMLEvaluateSampleMineTreeModelWithTransformationsBenchmark extends AbstractBenchmark {

    public static final String FILE_NAME_NO_SUFFIX = "SampleMineTreeModelWithTransformations";
    public static final String FILE_NAME = FILE_NAME_NO_SUFFIX + ".pmml";
    public static final String FILE_PATH = "pmml/" + FILE_NAME;

    private static final File pmmlFile;
    private static final KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;

    static {
        // Retrieve pmmlFile
        pmmlFile = getPMMLFile(FILE_PATH, FILE_NAME);

        // retrieve classloader
        memoryCompilerClassLoader = getMemoryCompilerClassLoader();
    }

    @Setup
    public void setupResource() throws IOException {
        // noop
    }

    @Override
    public void setup() throws ProviderException {
        // noop
    }

    @Benchmark
    public Map<String, GeneratedResources> evaluateCompilation() {
        return compileModel(pmmlFile, memoryCompilerClassLoader);
    }
}
