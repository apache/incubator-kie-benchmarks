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

package org.drools.benchmarks.turtle.runtime.generator;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all facts generators.
 * <p>
 * Creates the concrete generator class specified by 'classname' property in
 * configuration and executes the generateFacts() method on it. This concrete
 * class is responsible for creating all needed facts. Each generator has
 * two methods, one for generating facts that will match against the rules and
 * one for generating 'noise', that means facts that will not match against rules.
 */
public abstract class FactsGenerator extends ResourceGenerator {

    protected static final Logger logger = LoggerFactory.getLogger(FactsGenerator.class);

    // contains the number of current facts generating loop, default value = 0
    protected int currentLoop = 0;

    public FactsGenerator(final GeneratorConfiguration config) {
        super(config);
    }

    public void setShuffleFacts(final boolean shuffleFacts) {
        config.setShuffleFacts(shuffleFacts);
    }

    /**
     * Generates specified number of facts based on configuration values.
     * @param number number of facts to generate
     */
    public List<Object> generateFacts(final int number) {
        logger.debug("Will generate {} facts...", number);
        final List<Object> facts = generateMatchingFacts((int) (number * config.getFactsMatchingRatio()));

        facts.addAll(generateNonMatchingFacts((int) (number * (1 - config.getFactsMatchingRatio()))));
        // shuffle the facts if specified, so that all matching facts are not at the sendEvents
        if (config.isShuffleFacts()) {
            Collections.shuffle(facts);
        }
        logger.debug("Actually generated {} facts.", facts.size());
        return facts;
    }

    /**
     * Generates matching facts. This method has to be overriden in child classes.
     */
    protected abstract List<Object> generateMatchingFacts(int number);

    /**
     * Generates non-matching facts. This method has to be overriden in child classes.
     */
    protected abstract List<Object> generateNonMatchingFacts(int number);

    /**
     * Returns random integer from specified range.
     */
    protected int getRandomInt(final int from, final int to) {
        final Random random = new Random();
        return from + random.nextInt(to - from + 1);
    }

    /**
     * Returns random integer from range specified by strings.
     */
    protected int getRandomInt(final String from, final String to) {
        return getRandomInt(Integer.valueOf(from), Integer.valueOf(to));
    }

    /**
     * Returns the current place holder value depending on current loop number.
     */
    protected String getPlaceHolderValue(final String name) {
        return String.valueOf(getActualPlaceholderValue(getPlaceHolder(name)));
    }

    /**
     * Just returns the place holder with specified name.
     */
    protected PlaceHolder getPlaceHolder(final String name) {
        for (PlaceHolder placeHolder : config.getPlaceHolders()) {
            if (placeHolder.getName().equals(name)) {
                return placeHolder;
            }
        }
        throw new IllegalArgumentException("Placeholder with name " + name + " not found!");
    }

    protected long getPlaceHolderValueMillis(final String placeHolderName) {
        final PlaceHolder placeHolder = getPlaceHolder(placeHolderName);
        final int actualValue = getActualPlaceholderValue(placeHolder);
        if (placeHolder.getTimeUnit() == null) {
            return (long) actualValue;
        } else {
            return placeHolder.getTimeUnit().toMillis(actualValue);
        }
    }

    protected long getRandomLong(long from, long to) {
        return (long) getRandomInt((int) from, (int) to);
    }

    private int getActualPlaceholderValue(final PlaceHolder placeHolder) {
        final int totalLoops = config.getNumberOfRulesInDRL() / config.getNumberOfRuleTypesInDRL();
        final int increment = Double.valueOf(Math.ceil((placeHolder.getEndValue() - placeHolder.getStartValue()) / (totalLoops - 1))).intValue();
        final int actualValue = placeHolder.getStartValue() + currentLoop * increment;
        return actualValue > placeHolder.getEndValue() ? placeHolder.getEndValue() : actualValue;
    }
}
