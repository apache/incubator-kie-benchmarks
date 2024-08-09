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

package org.drools.benchmarks.dmn.ast;

import org.antlr.v4.runtime.tree.ParseTree;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.FEELDialect;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.feel.lang.impl.FEELBuilder;
import org.kie.dmn.feel.parser.feel11.ASTBuilderVisitor;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;
import org.kie.dmn.feel.util.ClassLoaderUtil;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Thread)
@Warmup(iterations = 100, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 20, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public abstract class AbstractASTBenchmark {

    private BaseNode baseNode;

    @Setup()
    public void setupBaseNode() {
        baseNode = getBaseNode(getBaseNodeExpression());
    }

    protected abstract String getBaseNodeExpression();

    @Benchmark
    public Object evaluateBaseNodeBenchmark() {
        return baseNode.evaluate(newEmptyEvaluationContext());
    }

    private static EvaluationContext newEmptyEvaluationContext() {
        // Defaulting FEELDialect to FEEL
        return new EvaluationContextImpl(ClassLoaderUtil.findDefaultClassLoader(), null, FEELDialect.FEEL);
    }

    private static BaseNode getBaseNode(String baseNodeExpression) {
        Map<String, Type> inputTypes  = Collections.emptyMap();
        FEEL_1_1Parser parser = FEELParser.parse(null, baseNodeExpression, inputTypes, Collections.emptyMap(), Collections.emptyList(), Collections.emptyList(), null);

        ParseTree tree = parser.compilation_unit();

        ASTBuilderVisitor v = new ASTBuilderVisitor(inputTypes, null);
        return tree.accept(v);
    }

}
