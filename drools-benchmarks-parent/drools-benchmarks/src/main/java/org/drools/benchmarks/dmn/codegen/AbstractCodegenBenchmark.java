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

import org.kie.dmn.api.core.DMNCompiler;
import org.kie.dmn.api.core.DMNCompilerConfiguration;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.compiler.DMNCompilerConfigurationImpl;
import org.kie.dmn.core.compiler.DMNCompilerImpl;
import org.kie.dmn.core.compiler.RuntimeTypeCheckOption;
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.dmn.feel.parser.feel11.profiles.DoCompileFEELProfile;
import org.kie.dmn.feel.util.Either;
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
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.drools.benchmarks.dmn.util.DynamicTypeUtils.entry;
import static org.drools.benchmarks.dmn.util.DynamicTypeUtils.prototype;

public abstract class AbstractCodegenBenchmark {

    private DMNRuntime dmnRuntime;
    private DMNModel dmnModel;
    private DMNContext dmnContext;

    protected abstract String getResource();
    protected abstract List<String> getAdditionalResources();
    protected abstract String getNameSpace();
    protected abstract String getModelName();
    protected abstract Map<String, Object> getInputData();

    protected void setupModelAndContext() {
        Function<DMNCompilerConfiguration, DMNCompiler> dmnCompilerFn = dmnCompilerConfiguration -> {
            ((DMNCompilerConfigurationImpl) dmnCompilerConfiguration).addFEELProfile(new DoCompileFEELProfile());
            return new DMNCompilerImpl(dmnCompilerConfiguration);
        };
        DMNRuntimeBuilder.DMNRuntimeBuilderConfigured dmnRuntimeBuilderConfigured = DMNRuntimeBuilder.fromDefaults()
                .buildConfigurationUsingCustomCompiler(dmnCompilerFn);
        Either<Exception, DMNRuntime> exceptionDMNRuntimeEither;
        if (getAdditionalResources() != null && !getAdditionalResources().isEmpty()) {
            exceptionDMNRuntimeEither = dmnRuntimeBuilderConfigured
                    .fromClasspathResources(getResource(), this.getClass(), getAdditionalResources().toArray(new String[0]));
        } else {
            exceptionDMNRuntimeEither = dmnRuntimeBuilderConfigured
                    .fromClasspathResource(getResource(), this.getClass());
        }
        dmnRuntime = exceptionDMNRuntimeEither
                .getOrElseThrow(e -> new RuntimeException("Error initializing DMNRuntime", e));
        ((DMNRuntimeImpl) dmnRuntime).setOption(new RuntimeTypeCheckOption(true));
        dmnModel = dmnRuntime.getModel(
                getNameSpace(),
                getModelName());
        if (dmnModel == null) {
            throw new RuntimeException("Model " + getNameSpace() + "." + getModelName() + " not found");
        }
        dmnContext = dmnRuntime.newContext();
        getInputData().forEach((key, value) -> dmnContext.set(key, value));
    }

    protected Object evaluateModelBenchmark() {
        return dmnRuntime.evaluateAll(dmnModel, dmnContext);
    }



}
