/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.benchmarks.dmn.feel.functions;

import java.util.concurrent.TimeUnit;

import org.drools.benchmarks.dmn.feel.AbstractFEELBenchmark;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Warmup;

@Warmup(iterations = 50, time = 200, timeUnit = TimeUnit.MILLISECONDS)
public class FEELCountFunctionBenchmark extends AbstractFEELBenchmark {

    @Param({"count( 1, 2, 3 )", "count([ 1, 2, 3 ])"})
    private String expression;

    @Benchmark
    public Object evaluateExpressionBenchmark() {
        return evaluateFEELExpression(expression);
    }
}
