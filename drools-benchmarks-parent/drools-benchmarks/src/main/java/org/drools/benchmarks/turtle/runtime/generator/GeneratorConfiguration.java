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

package org.drools.benchmarks.turtle.runtime.generator;

import java.util.List;

/**
 * Configuration for resources generators.
 *
 */
public class GeneratorConfiguration {

    private final int numberOfRulesInDRL;
    private final int numberOfRuleTypesInDRL;
    private final double factsMatchingRatio;

    private List<PlaceHolder> placeHolders;
    private boolean shuffleFacts = true;

    public GeneratorConfiguration(final int numberOfRulesInDRL, final int numberOfRuleTypesInDRL,
            final double factsMatchingRatio) {
        this.numberOfRulesInDRL = numberOfRulesInDRL;
        this.numberOfRuleTypesInDRL = numberOfRuleTypesInDRL;
        this.factsMatchingRatio = factsMatchingRatio;
    }

    public int getNumberOfRuleTypesInDRL() {
        return numberOfRuleTypesInDRL;
    }

    public int getNumberOfRulesInDRL() {
        return numberOfRulesInDRL;
    }

    public double getFactsMatchingRatio() {
        return factsMatchingRatio;
    }

    public List<PlaceHolder> getPlaceHolders() {
        return placeHolders;
    }

    public boolean isShuffleFacts() {
        return shuffleFacts;
    }

    public void setPlaceHolders(final List<PlaceHolder> placeHolders) {
        this.placeHolders = placeHolders;
    }

    public void setShuffleFacts(final boolean shuffleFacts) {
        this.shuffleFacts = shuffleFacts;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(format("Number of rules in DRL", numberOfRulesInDRL));
        sb.append(format("Number of different rules in DRL", numberOfRuleTypesInDRL));
        sb.append(format("Place holders", placeHolders));
        sb.append(format("Facts matching ratio", factsMatchingRatio));
        return sb.toString();
    }

    private String format(String key, Object value) {
        return "\t" + key + "='" + value.toString() + "'\n";
    }

}
