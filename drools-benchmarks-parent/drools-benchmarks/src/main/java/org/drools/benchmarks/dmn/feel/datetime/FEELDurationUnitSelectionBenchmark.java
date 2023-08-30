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

package org.drools.benchmarks.dmn.feel.datetime;

import org.drools.benchmarks.dmn.feel.AbstractFEELBenchmark;
import org.openjdk.jmh.annotations.Param;

public class FEELDurationUnitSelectionBenchmark extends AbstractFEELBenchmark {

    @Param({"duration( \"P2DT20H14M5S\" ).days",
            "duration( \"P2DT20H14M5S\" ).hours",
            "duration( \"P2DT20H14M5S\" ).minutes",
            "duration( \"P2DT20H14M5S\" ).seconds",
            "years and months duration( date(\"2011-12-22\"), date(\"2013-08-24\") ).years",
            "years and months duration( date(\"2011-12-22\"), date(\"2013-08-24\") ).months"})
    private String expression;

    @Override
    public String getExpression() {
        return expression;
    }
}
