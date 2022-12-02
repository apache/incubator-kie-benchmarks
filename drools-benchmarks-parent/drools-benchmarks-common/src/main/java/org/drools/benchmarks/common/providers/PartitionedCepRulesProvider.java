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

import java.util.function.Function;

import org.drools.benchmarks.common.DRLProvider;
import org.drools.benchmarks.common.model.A;
import org.drools.benchmarks.common.model.FireLogger;

/**
 * Provides rule(s) that are partitioned.
 */
public class PartitionedCepRulesProvider implements DRLProvider {

    private final Function<Object, String> constraintBuilder;
    private final int numberOfJoins;
    private final int numberOfJoinedEvents;
    private final long eventsExpirationMs;

    private final boolean countFirings;
    private final boolean logFirings;

    public PartitionedCepRulesProvider(final int numberOfJoins, final int numberOfJoinedEvents,
            final long eventsExpirationMs, final Function<Object, String> constraintBuilder,
            final boolean countFirings, final boolean logFirings) {
        this.numberOfJoins = numberOfJoins;
        this.numberOfJoinedEvents = numberOfJoinedEvents;
        this.eventsExpirationMs = eventsExpirationMs;
        this.constraintBuilder = constraintBuilder;
        this.countFirings = countFirings;
        this.logFirings = logFirings;
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

        drlBuilder.append("import " + A.class.getPackage().getName() + ".*;\n");
        appendCepHeader(drlBuilder);

        if (countFirings) {
            drlBuilder.append("global java.util.concurrent.atomic.LongAdder firings;\n");
        }
        if (logFirings) {
            drlBuilder.append( "global " + FireLogger.class.getCanonicalName() + " logger;\n" );
        }

        for (int partitionNumber = 0; partitionNumber < numberOfRules; partitionNumber++) {
            drlBuilder.append(" rule \"" + ruleNameBase + partitionNumber + "\" when\n");
            addJoins(drlBuilder, partitionNumber);
            drlBuilder.append( "then\n" );
            if (countFirings) {
                drlBuilder.append("firings.add(1);\n");
            }
            if (logFirings) {
                drlBuilder.append("logger.log(" + partitionNumber + ", " + getJoinEvents() + ");\n");
            }
            drlBuilder.append( "end\n" );
        }

        return drlBuilder.toString();
    }

    public void addJoins(final StringBuilder drlBuilder, final int partitionNumber) {
        char previousClassName = 'A';
        drlBuilder.append(getFactBinding(previousClassName) + previousClassName + "( " + constraintBuilder.apply( partitionNumber ) + "," +
                          getConstraintVariable(previousClassName) + ": " + "id )\n");
        for (int i = 0; i < numberOfJoins; i++) {
            final char nextClassName = (char) (previousClassName + 1);
            final String nextVariableName = getConstraintVariable(nextClassName);
            final String previousVariableName = getConstraintVariable(previousClassName);
            if (numberOfJoinedEvents > 0) {
                drlBuilder.append( getFactBinding(nextClassName) + nextClassName + "( (" +
                                   nextVariableName + ": id <= " + previousVariableName
                                   + ") && (id > (" + previousVariableName + " - " + numberOfJoinedEvents + " )))\n" );
            } else {
                drlBuilder.append( getFactBinding(nextClassName) + nextClassName + "( " +
                                   nextVariableName + ": id == " + previousVariableName + ")" );

            }
            previousClassName = nextClassName;
        }
    }

    private void appendCepHeader(final StringBuilder drlBuilder) {
        char domainClassName = 'A';
        // Number of joins + 1 because of first class.
        for (int i = 0; i <= numberOfJoins; i++) {
            drlBuilder.append("declare " + domainClassName + " @role( event ) ");
            if (eventsExpirationMs > 0) {
                drlBuilder.append("@expires(" + eventsExpirationMs + "ms) ");
            }
            drlBuilder.append("end\n");
            domainClassName++;
        }
    }

    private String getConstraintVariable(final char constraintClassName) {
        return "$" + constraintClassName + "id";
    }

    private String getFactBinding(final char constraintClassName) {
        return "$" + constraintClassName + ": ";
    }

    protected String getJoinEvents() {
        switch (numberOfJoins) {
            case 0:
                return "$A";
            case 1:
                return "$A, $B";
            case 2:
                return "$A, $B, $C";
            case 3:
                return "$A, $B, $C, $D";
            case 4:
                return "$A, $B, $C, $D, $E";
            default:
                throw new IllegalArgumentException("Unsupported number of joins! Maximal number of joins is 4.");
        }
    }
}
