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

import org.drools.benchmarks.common.ConstraintPattern;
import org.drools.benchmarks.common.DRLProvider;
import org.drools.benchmarks.model.A;

/**
 * Provides rule(s) with JoinNodes. Each join is of different type in different rules, so joins are not shared.
 */
public class RulesWithJoinsTreeProvider implements DRLProvider {

    private final int numberOfJoins;
    private final boolean appendDrlHeader;
    private final boolean sharedRoot;
    private final String global;
    private final String consequence;

    public RulesWithJoinsTreeProvider(final int numberOfJoins, final boolean appendDrlHeader) {
        this(numberOfJoins, appendDrlHeader, false, "", "");
    }

    /**
     * Constructor.
     * @param numberOfJoins Required number of joins that each rule in provided DRL will contain.
     * This number is limited to maximum number of 4 joins in each rule.
     * @param appendDrlHeader True, if DRL header should be appended to provided DRL, else false.
     * @param sharedRoot If true, the root constraint is shared between rules.
     * @param global DRL global.
     * @param consequence Rule consequence.
     */
    public RulesWithJoinsTreeProvider(final int numberOfJoins, final boolean appendDrlHeader, final boolean sharedRoot,
            final String global, final String consequence) {
        if (numberOfJoins > 4) {
            throw new IllegalArgumentException(
                    "Unsupported number of joins! Maximal allowed number of joins is 4, actual is " + numberOfJoins);
        }
        this.numberOfJoins = numberOfJoins;
        this.appendDrlHeader = appendDrlHeader;
        this.sharedRoot = sharedRoot;
        this.global = global;
        this.consequence = consequence;
    }

    @Override
    public String getDrl() {
        return getDrl(1);
    }

    @Override
    public String getDrl(int numberOfRules) {
        return getDrl(numberOfRules, "R");
    }

    @Override
    public String getDrl(int numberOfRules, String ruleNameBase) {
        final StringBuilder drlBuilder = new StringBuilder();

        if (appendDrlHeader) {
            drlBuilder.append("import " + A.class.getPackage().getName() + ".*;\n");
        }
        drlBuilder.append( global + "\n" );
        for ( int i = 0; i < numberOfRules; i++ ) {
            drlBuilder.append( "rule \"" + ruleNameBase + i + "\" \n");
            drlBuilder.append( " when\n");
            appendJoins(drlBuilder, i);
            drlBuilder.append( "then\n" );
            drlBuilder.append( consequence + "\n" );
            drlBuilder.append( "end\n" );
        }
        return drlBuilder.toString();
    }

    private void appendJoins(final StringBuilder drlBuilder, final int ruleNumber) {
        final ConstraintPattern[] joinConstraints = getJoinConstraints();
        String variableNameForJoinConstraint = "$a";
        if (sharedRoot) {
            drlBuilder.append("  " + variableNameForJoinConstraint + " : A( value == 0 )\n");
        } else {
            drlBuilder.append("  " + variableNameForJoinConstraint + " : A( value > " + ruleNumber + " )\n");
        }
        for (int i = 0; i < numberOfJoins; i++) {
            final ConstraintPattern joinConstraint = joinConstraints[getJoinConstraintIndex(ruleNumber, i, joinConstraints.length)];
            drlBuilder.append(joinConstraint.getConstraint().replace("${variable}", variableNameForJoinConstraint));
            variableNameForJoinConstraint = joinConstraint.getVariableName();
        }
    }

    private int getJoinConstraintIndex(final int indexOffset, final int requiredIndex, final int constraintsSize) {
        return (requiredIndex + indexOffset) % constraintsSize;
    }

    private ConstraintPattern[] getJoinConstraints() {
        return new ConstraintPattern[] {
                new ConstraintPattern("$b", "  $b : B( value > ${variable}.value )\n"),
                new ConstraintPattern("$c", "  $c : C( value > ${variable}.value )\n"),
                new ConstraintPattern("$d", "  $d : D( value > ${variable}.value )\n"),
                new ConstraintPattern("$e", "  $e : E( value > ${variable}.value )\n")
        };
    }
}
