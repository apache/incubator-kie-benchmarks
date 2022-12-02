/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.benchmarks.common.providers;

import org.drools.benchmarks.common.DRLProvider;

public class SimpleRulesWithConstraintsProvider implements DRLProvider {

    public static final String PROVIDER_ID = "SimpleRulesWithConstraintsProvider";

    private final String constraints;

    public SimpleRulesWithConstraintsProvider(final String constraints) {
        this.constraints = constraints;
    }

    @Override
    public String getDrl() {
        return getDrl(1);
    }

    @Override
    public String getDrl(final int numberOfRules) {
        return getDrl(numberOfRules, "R");
    }

    @Override
    public String getDrl(int numberOfRules, String ruleNameBase) {
        final StringBuilder drlBuilder = new StringBuilder();
        drlBuilder.append("import org.drools.benchmarks.model.reactive.*;\n");
        drlBuilder.append("import org.drools.benchmarks.model.*;\n");
        drlBuilder.append("\n");
        for (int i = 0; i < numberOfRules; i++) {
            drlBuilder.append("rule \"" + ruleNameBase + i + "\" when\n");
            drlBuilder.append(constraints.replace("${i}", String.valueOf(i)));
            drlBuilder.append("\n");
            drlBuilder.append("then\n");
            drlBuilder.append("end\n");
        }
        return drlBuilder.toString();
    }
}
