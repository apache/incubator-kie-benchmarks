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
import org.drools.benchmarks.common.Event;

/**
 * Provides rule(s) that are partitioned.
 */
public class PartitionedCepRulesProvider implements DrlProvider {

    private final Class<? extends Event> eventClass;
    private final String constraintOperator;
    private final int numberOfJoins;

    private final boolean countFirings;

    public PartitionedCepRulesProvider(final Class<? extends Event> eventClass, final String constraintOperator,
            final int numberOfJoins, boolean countFirings) {
        this.eventClass = eventClass;
        this.constraintOperator = constraintOperator;
        this.countFirings = countFirings;
        this.numberOfJoins = numberOfJoins;
    }

    @Override
    public String getDrl() {
        return getDrl(1);
    }

    @Override
    public String getDrl(final int numberOfRules) {
        final StringBuilder drlBuilder = new StringBuilder();

        drlBuilder.append("import " + eventClass.getCanonicalName() + ";\n");
        drlBuilder.append("declare " + eventClass.getName() + " @role( event ) @duration(duration) end\n");

        if (countFirings) {
            drlBuilder.append("global java.util.concurrent.atomic.AtomicInteger firings;\n");
        }

        for (int i = 0; i < numberOfRules; i++) {
            drlBuilder.append(" rule R" + i + " when\n");

            final char startVariableName = 'A';
            drlBuilder.append(eventClass.getName() + "($" + startVariableName + ": " + "id " + constraintOperator + " " + i + ")\n");
            addJoins(drlBuilder, startVariableName);
            drlBuilder.append( "then\n" );
            if (countFirings) {
                drlBuilder.append("firings.incrementAndGet();\n");
            }
            drlBuilder.append( "end\n" );
        }

        return drlBuilder.toString();
    }

    public void addJoins(final StringBuilder drlBuilder, final char startChar) {
        char previousVariableName = startChar;
        for (int i = 0; i < numberOfJoins; i++) {
            final char nextVariableName = (char) (previousVariableName + 1);
            drlBuilder.append(eventClass.getName() + "( $" + nextVariableName + ": id == $" + previousVariableName + " )\n");
            previousVariableName = nextVariableName;
        }
    }
}
