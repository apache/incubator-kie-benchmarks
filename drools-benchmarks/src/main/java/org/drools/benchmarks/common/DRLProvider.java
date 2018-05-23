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

package org.drools.benchmarks.common;

/**
 * Interface for providing DRL files.
 */
public interface DRLProvider {

    /**
     * Returns DRL.
     * @return String representation of DRL.
     */
    String getDrl();

    /**
     * Returns DRL that contains specified number of rules.
     * @param numberOfRules Required number of rules in resulting DRL.
     * @return String representation of DRL that contains specified number of rules.
     */
    String getDrl(int numberOfRules);

    /**
     * Returns DRL that contains specified number of rules.
     * @param numberOfRules Required number of rules in resulting DRL.
     * @return String representation of DRL that contains specified number of rules.
     */
    String getDrl(int numberOfRules, final String ruleNameBase);
}
