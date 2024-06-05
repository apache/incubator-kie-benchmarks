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

package org.drools.benchmarks.dmn.feel;

import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.lang.impl.FEELBuilder;
import org.kie.dmn.feel.parser.feel11.profiles.DoCompileFEELProfile;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Thread)
@Warmup(iterations = 100, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 20, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public abstract class AbstractFEELBenchmark {

    private FEEL feelInterpreted;
    private FEEL feelCompiled;
    private CompiledExpression compiledJavaExpression;
    private CompiledExpression compiledButInterpretedExpression;

    @Setup()
    public void setupFEEL() {
        feelInterpreted = FEELBuilder.builder().build();
        feelCompiled = FEELBuilder.builder().withProfiles(Collections.singletonList(new DoCompileFEELProfile())).build();
        compiledJavaExpression = compileExpression(getExpression());
        compiledButInterpretedExpression = compileInterpretedExpression(getExpression());
    }

    public abstract String getExpression();

    protected Object evaluateFEELExpression(final String expression) {
        return feelInterpreted.evaluate(expression);
    }

    protected Object evaluateFEELExpression(final CompiledExpression expression) {
        return feelCompiled.evaluate(expression, new HashMap<>());
    }

    protected CompiledExpression compileExpression(final String expression) {
        return feelCompiled.compile(expression, feelCompiled.newCompilerContext());
    }

    protected CompiledExpression compileInterpretedExpression(String expression) {
        return feelInterpreted.compile(expression, feelInterpreted.newCompilerContext());
    }

    @Benchmark
    public Object evaluateExpressionBenchmark() {
        return evaluateFEELExpression(getExpression());
    }

    @Benchmark
    public Object evaluateCompiledJavaExpressionBenchmark() {
        return evaluateFEELExpression(compiledJavaExpression);
    }

    @Benchmark
    public Object evaluateCompiledButInterpretedExpression() {
        return evaluateFEELExpression(compiledButInterpretedExpression);
    }
}
