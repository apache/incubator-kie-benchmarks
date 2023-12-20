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

package org.drools.benchmarks.dmn.feel;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

@Warmup(iterations = 30, time = 200, timeUnit = TimeUnit.MILLISECONDS)
public class FEELInfixOpBenchmark extends AbstractFEELBenchmark {

    @Param({"30 >= 20 * 10", "16 / 5 * ( 7 )", "9 + 5 ** 3", "10 * 32", "34 < 10 and 43 = \"32\" or 23"})
    private String expression;

    @Override
    public String getExpression() {
        return expression;
    }

}
