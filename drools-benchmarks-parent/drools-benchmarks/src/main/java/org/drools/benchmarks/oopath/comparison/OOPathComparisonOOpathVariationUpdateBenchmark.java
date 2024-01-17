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

package org.drools.benchmarks.oopath.comparison;

import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;

public class OOPathComparisonOOpathVariationUpdateBenchmark extends AbstractOOPathComparisonBenchmark {

    @Setup
    @Override
    public void setupKieBase() {
        final String drl = MODEL_PACKAGE_IMPORT + "\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Man( $toy: /wife/children[age > 10]/toys )\n" +
                "then\n" +
                "  list.add( $toy.getName() );\n" +
                "end\n";
        kieBase = BuildtimeUtil.createKieBaseFromDrl(drl);
    }

    @Setup(Level.Iteration)
    public void insertFacts() {
        insertModel(kieSession, parentFacts);
        kieSession.fireAllRules();
        globalList.clear();
    }

    @Benchmark
    public int testUpdate() {
        factsToBeModified.forEach(child -> child.setAge(11));
        return kieSession.fireAllRules();
    }
}
