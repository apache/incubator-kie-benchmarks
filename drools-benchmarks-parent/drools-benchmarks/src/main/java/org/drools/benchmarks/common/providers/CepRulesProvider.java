/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.benchmarks.common.providers;

import org.drools.benchmarks.common.DRLProvider;
import org.drools.benchmarks.common.Event;
import org.drools.benchmarks.common.TemporalOperator;

/**
 * Provides rule(s) with after operator.
 */
public class CepRulesProvider implements DRLProvider {

    private Class<? extends Event> firstEventClass;
    private Class<? extends Event> secondEventClass;

    private TemporalOperator temporalOperator;
    private String temporalDistanceStart;
    private String temporalDistanceEnd;

    public CepRulesProvider(final Class<? extends Event> firstEventClass, final Class<? extends Event> secondEventClass,
            final TemporalOperator temporalOperator, final String temporalDistanceStart, final String temporalDistanceEnd) {
        this.firstEventClass = firstEventClass;
        this.secondEventClass = secondEventClass;
        this.temporalOperator = temporalOperator;
        this.temporalDistanceStart = temporalDistanceStart;
        this.temporalDistanceEnd = temporalDistanceEnd;
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

        drlBuilder.append("import " + firstEventClass.getCanonicalName() + ";\n");
        drlBuilder.append("import " + secondEventClass.getCanonicalName() + ";\n");

        drlBuilder.append("declare " + firstEventClass.getName() + " @role( event ) @duration(duration) end\n");
        drlBuilder.append("declare " + secondEventClass.getName() + " @role( event ) @duration(duration) end\n");

        final String temporalDistanceString = getTemporalDistanceString();

        for (int i = 1; i <= numberOfRules; i++) {
            drlBuilder.append(" rule \"" + ruleNameBase + i + "\" when\n");
            drlBuilder.append("   $event1: " + firstEventClass.getName() + "()\n");
            drlBuilder.append("   $event2: " + secondEventClass.getName()
                                      + "(this != $event1, this " + temporalOperator + " " + temporalDistanceString + " $event1)\n");
            drlBuilder.append( "then end\n" );
        }

        return drlBuilder.toString();
    }

    private String getTemporalDistanceString() {
        if (!"".equals(temporalDistanceStart)) {
            if (!"".equals(temporalDistanceEnd)) {
                return "[" + temporalDistanceStart + "," + temporalDistanceEnd + "]";
            } else {
                return "[" + temporalDistanceStart + "]";
            }
        }
        return "";
    }
}
