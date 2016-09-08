/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import org.drools.benchmarks.common.DrlProvider;
import org.drools.benchmarks.domain.A;

/**
 * Provides rule(s) that are partitioned.
 */
public class PartitionedCepRulesProvider implements DrlProvider {

    private final String constraintOperator;
    private final int numberOfJoins;

    private final boolean countFirings;

    public PartitionedCepRulesProvider(final int numberOfJoins, final String constraintOperator,
            final boolean countFirings) {
        this.numberOfJoins = numberOfJoins;
        this.constraintOperator = constraintOperator;
        this.countFirings = countFirings;
    }

    @Override
    public String getDrl() {
        return getDrl(1);
    }

    @Override
    public String getDrl(final int numberOfRules) {
        final StringBuilder drlBuilder = new StringBuilder();

        drlBuilder.append("import " + A.class.getPackage().getName() + ".*;\n");
        appendCepHeader(drlBuilder);

        if (countFirings) {
            drlBuilder.append("global java.util.concurrent.atomic.AtomicInteger firings;\n");
        }

        for (int partitionNumber = 0; partitionNumber < numberOfRules; partitionNumber++) {
            drlBuilder.append(" rule R" + partitionNumber + " when\n");
            addJoins(drlBuilder, partitionNumber);
            drlBuilder.append( "then\n" );
            if (countFirings) {
                drlBuilder.append("firings.incrementAndGet();\n");
            }
            drlBuilder.append( "end\n" );
        }

        return drlBuilder.toString();
    }

    public void addJoins(final StringBuilder drlBuilder, final int partitionNumber) {
        char previousClassName = 'A';
        drlBuilder.append(previousClassName + "( value " + constraintOperator + " " + partitionNumber + ","
                + getConstraintVariable(previousClassName) + ": " + "id )\n");
        for (int i = 0; i < numberOfJoins; i++) {
            final char nextClassName = (char) (previousClassName + 1);
            drlBuilder.append(nextClassName + "( " +
                    getConstraintVariable(nextClassName) + ": id == " + getConstraintVariable(previousClassName) + " )\n");
            previousClassName = nextClassName;
        }
    }

    private void appendCepHeader(final StringBuilder drlBuilder) {
        char domainClassName = 'A';
        // Number of joins + 1 because of first class.
        for (int i = 0; i <= numberOfJoins; i++) {
            drlBuilder.append("declare " + domainClassName + " @role( event ) end\n");
            domainClassName++;
        }
    }

    private String getConstraintVariable(final char constraintClassName) {
        return "$" + constraintClassName + "id";
    }
}
