/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.benchmarks.dmn.feel.infixexecutors;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.ast.infixexecutors.GtExecutor;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.feel.util.ClassLoaderUtil;
import org.openjdk.jmh.annotations.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.drools.benchmarks.dmn.feel.infixexecutors.FEELInfixExecutorBenchmarkUtils.getBooleanMap;

@Warmup(iterations = 10)
@Measurement(iterations = 50)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@State(Scope.Thread)
public class FEELGtExecutorBenchmark {

    private GtExecutor executor;
    private EvaluationContext ctx;

    private Map<String, Object> map;

    @Param({"String String", "Duration Duration", "int int", "ChronoPeriod ChronoPeriod"})
    private String args;


    @Setup
    public void setup() {
        executor = GtExecutor.instance();
        ctx = new EvaluationContextImpl(ClassLoaderUtil.findDefaultClassLoader(), null);
        map = getBooleanMap(args);
    }

    @Benchmark
    public Object evaluate() {
        return executor.evaluate(map.get("a"), map.get("b"), ctx);
    }
}
