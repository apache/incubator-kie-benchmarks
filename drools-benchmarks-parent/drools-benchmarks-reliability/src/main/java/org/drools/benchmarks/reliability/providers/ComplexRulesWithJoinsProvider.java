/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.drools.benchmarks.reliability.providers;

import org.drools.benchmarks.common.DRLProvider;
import org.drools.benchmarks.common.model.ConsequenceBlackhole;
import org.drools.benchmarks.reliability.proto.complex.ComplexA;

/**
 * Provides rule(s) with simple JoinNodes. Can provide rules with JoinNodes also for event processing.
 */
public class ComplexRulesWithJoinsProvider implements DRLProvider {

    public static final String PROVIDER_ID = "ComplexRulesWithJoinsProvider";

    private int numberOfJoins = 0;
    private boolean withCep = false;
    private boolean withImports = true;
    private boolean withGeneratedConsequence = true;
    private boolean prioritizedBySalience = false;
    private String global = "";
    private String consequence = "";
    private String rootConstraintValueOperator = ">";
    private String joinConstraintValueOperator = ">";

    public ComplexRulesWithJoinsProvider() { }

    public ComplexRulesWithJoinsProvider(final int numberOfJoins, final boolean withCep, final boolean withImports ) {
        this(numberOfJoins, withCep, withImports, "", "");
    }

    public ComplexRulesWithJoinsProvider(int numberOfJoins, boolean withCep, boolean withImports, String global, String consequence) {
        this(numberOfJoins, withCep, withImports, false, global, consequence, ">", ">");
    }

    /**
     * Constructor.
     * @param numberOfJoins Required number of joins that each rule in provided DRL will contain.
     * This number is limited to maximum number of 4 joins in each rule.
     * @param withCep True, if rules for event processing should be generated, else false.
     * @param withImports True, if DRL header should be appended to provided DRL, else false.
     * @param prioritizedBySalience If true, the rules are generated with salience and ordered by it.
     * Each rule gets higher salience than previous one.
     * @param global DRL global.
     * @param consequence Rule consequence.
     * @param rootConstraintValueOperator Operator for matching value in root constraint.
     * @param joinConstraintValueOperator Operator for matching value in join constraint.
     */
    public ComplexRulesWithJoinsProvider(final int numberOfJoins, final boolean withCep, final boolean withImports,
                                         final boolean prioritizedBySalience, final String global, final String consequence,
                                         final String rootConstraintValueOperator, final String joinConstraintValueOperator) {
        if (numberOfJoins > 4) {
            throw new IllegalArgumentException(
                    "Unsupported number of joins! Maximal allowed number of joins is 4, actual is " + numberOfJoins);
        }
        this.numberOfJoins = numberOfJoins;
        this.withCep = withCep;
        this.withImports = withImports;
        this.prioritizedBySalience = prioritizedBySalience;
        this.global = global;
        this.consequence = consequence;
        this.rootConstraintValueOperator = rootConstraintValueOperator;
        this.joinConstraintValueOperator = joinConstraintValueOperator;
    }

    public ComplexRulesWithJoinsProvider withNumberOfJoins(int numberOfJoins) {
        this.numberOfJoins = numberOfJoins;
        return this;
    }

    public ComplexRulesWithJoinsProvider withCep(boolean withCep) {
        this.withCep = withCep;
        return this;
    }

    public ComplexRulesWithJoinsProvider withImports(boolean withImports) {
        this.withImports = withImports;
        return this;
    }

    public ComplexRulesWithJoinsProvider withGeneratedConsequence(boolean withGeneratedConsequence) {
        this.withGeneratedConsequence = withGeneratedConsequence;
        return this;
    }

    public ComplexRulesWithJoinsProvider withPrioritizedBySalience(boolean prioritizedBySalience) {
        this.prioritizedBySalience = prioritizedBySalience;
        return this;
    }

    public ComplexRulesWithJoinsProvider withGlobal(String global) {
        this.global = global;
        return this;
    }

    public ComplexRulesWithJoinsProvider withConsequence(String consequence) {
        this.consequence = consequence;
        return this;
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
        if (withGeneratedConsequence) {
            this.consequence = generateConsequence();
        }

        final StringBuilder drlBuilder = new StringBuilder();

        if ( withImports ) {
            drlBuilder.append("import " + ComplexA.class.getPackage().getName() + ".*;\n");
        }
        drlBuilder.append( global + "\n" );
        if (withCep) {
            appendCepHeader(drlBuilder);
        }
        for ( int i = 0; i < numberOfRules; i++ ) {
            drlBuilder.append( "rule \"" + ruleNameBase + i + "\" \n");
            if (prioritizedBySalience) {
                drlBuilder.append("salience " + i + " \n");
            }
            drlBuilder.append( " when\n");
            appendJoins(drlBuilder, i);
            drlBuilder.append( "then\n" );
            drlBuilder.append( consequence + "\n" );
            drlBuilder.append( "end\n" );
        }
        //System.out.println(drlBuilder.toString());
        return drlBuilder.toString();
    }

    private void appendCepHeader(final StringBuilder drlBuilder) {
        final String[] domainClassNames = new String[] {"ComplexB", "ComplexC", "ComplexD"};
        drlBuilder.append("declare ComplexA @role( event ) @timestamp( value ) end\n");
        for (int i = 0; i < numberOfJoins; i++) {
            drlBuilder.append("declare " + domainClassNames[i] + " @role( event ) @timestamp( value ) end\n");
        }
    }

    private void appendJoins(final StringBuilder drlBuilder, final int valueInConstraint) {
        drlBuilder.append("  $a : ComplexA( value " + rootConstraintValueOperator + " " + valueInConstraint + ")\n");
        for (int i = 0; i < numberOfJoins; i++) {
            drlBuilder.append(withCep ? getJoinConstraintsCep(i) : getJoinConstraints(i));
        }
    }

    private String getJoinConstraintsCep(int index) {
        return "  $" + (char)('b'+index) + " : Complex" + (char)('B'+index) + "( this after $" + (char)('a'+index) + " )\n";
    }

    private String getJoinConstraints(int index) {
        return "  $" + (char)('b'+index) + " : Complex" + (char)('B'+index) + "( value " + joinConstraintValueOperator + " $" + (char)('a'+index) + ".value )\n";
    }

    private String generateConsequence() {
        StringBuilder consequence = new StringBuilder("    long result = $a.getId()");
        for (int i = 0; i < numberOfJoins; i++) {
            consequence.append( " + $" ).append( (char)('b'+i) ).append( ".getId()" );
        }
        consequence.append( ";\n" );
        consequence.append( "    " + ConsequenceBlackhole.class.getCanonicalName() + ".consume( result );" );
        return consequence.toString();
    }
}
