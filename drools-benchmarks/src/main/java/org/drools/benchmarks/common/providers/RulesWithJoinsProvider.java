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

import org.drools.benchmarks.common.DrlProvider;
import org.drools.benchmarks.domain.A;

/**
 * Provides rule(s) with simple JoinNodes. Can provide rules with JoinNodes also for event processing.
 */
public class RulesWithJoinsProvider implements DrlProvider {

    private final int numberOfJoins;
    private final boolean withCep;
    private final boolean appendDrlHeader;
    private final String global;
    private final String consequence;

    public RulesWithJoinsProvider(final int numberOfJoins, final boolean withCep, final boolean appendDrlHeader) {
        this(numberOfJoins, withCep, appendDrlHeader, "", "");
    }

    /**
     * Constructor.
     *
     * @param numberOfJoins Required number of joins that each rule in provided DRL will contain.
     *                      This number is limited to maximum number of 4 joins in each rule.
     * @param withCep True, if rules for event processing should be generated, else false.
     * @param appendDrlHeader True, if DRL header should be appended to provided DRL, else false.
     * @param global DRL global.
     * @param consequence Rule consequence.
     */
    public RulesWithJoinsProvider(final int numberOfJoins, final boolean withCep, final boolean appendDrlHeader,
            final String global, final String consequence) {
        if (numberOfJoins > 4) {
            throw new IllegalArgumentException(
                    "Unsupported number of joins! Maximal allowed number of joins is 4, actual is " + numberOfJoins);
        }
        this.numberOfJoins = numberOfJoins;
        this.withCep = withCep;
        this.appendDrlHeader = appendDrlHeader;
        this.global = global;
        this.consequence = consequence;
    }

    @Override
    public String getDrl() {
        return getDrl(1);
    }

    @Override
    public String getDrl(int numberOfRules) {
        final StringBuilder drlBuilder = new StringBuilder();

        if (appendDrlHeader) {
            drlBuilder.append("import " + A.class.getPackage().getName() + ".*;\n");
        }
        drlBuilder.append( global + "\n" );
        if (withCep) {
            appendCepHeader(drlBuilder);
        }
        for ( int i = 0; i < numberOfRules; i++ ) {
            drlBuilder.append( "rule R" + i + " when\n");
            appendJoins(drlBuilder, i);
            drlBuilder.append( "then\n" );
            drlBuilder.append( consequence + "\n" );
            drlBuilder.append( "end\n" );
        }
        return drlBuilder.toString();
    }

    private void appendCepHeader(final StringBuilder drlBuilder) {
        final String[] domainClassNames = new String[] {"B", "C", "D", "E"};
        drlBuilder.append("declare A @role( event ) @timestamp( value ) end\n");
        for (int i = 0; i < numberOfJoins; i++) {
            drlBuilder.append("declare " + domainClassNames[i] + " @role( event ) @timestamp( value ) end\n");
        }
    }

    private void appendJoins(final StringBuilder drlBuilder, final int valueInConstraint) {
        final String[] joinConstraints = withCep ? getJoinConstraintsCep() : getJoinConstraints();
        drlBuilder.append("  $a : A( value > " + valueInConstraint + ")\n");
        for (int i = 0; i < numberOfJoins; i++) {
            drlBuilder.append(joinConstraints[i]);
        }
    }

    private String[] getJoinConstraintsCep() {
        return new String[]{
                "  $b : B( this after $a )\n",
                "  $c : C( this after $b )\n",
                "  $d : D( this after $c )\n",
                "  $e : E( this after $d )\n"
        };
    }

    private String[] getJoinConstraints() {
        return new String[] {
                "  $b : B( value > $a.value )\n",
                "  $c : C( value > $b.value )\n",
                "  $d : D( value > $c.value )\n",
                "  $e : E( value > $d.value )\n"
        };
    }
}
