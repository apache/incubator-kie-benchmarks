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

public class FEELContextBenchmark extends AbstractFEELBenchmark {

    @Param({"{ first name : \"Bob\", birthday : date(\"1978-09-12\"), salutation : \"Hello \"+first name }",
            "{ full name : { first name: \"Bob\", last name : \"Doe\" }, birthday : date(\"1978-09-12\"), salutation : \"Hello \"+full name.first name }"})
    private String expression;

    @Override
    public String getExpression() {
        return expression;
    }
}
