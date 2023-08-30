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

package org.drools.benchmarks.common.providers.dmn;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.drools.benchmarks.common.DMNProvider;

public class TriangularNumHardDMNProvider implements DMNProvider {

    @Override
    public String getDMN() {
        return getDMN(1);
    }

    @Override
    public String getDMN(int numberOfElements) {
        final StringBuilder dmnBuilder = new StringBuilder();

        dmnBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        dmnBuilder.append("<definitions id=\"dmn-triangular\" name=\"dmn-triangular\"\n");
        dmnBuilder.append("             namespace=\"https://github.com/kiegroup/kie-dmn\"\n");
        dmnBuilder.append("             xmlns=\"http://www.omg.org/spec/DMN/20180521/MODEL/\"\n");
        dmnBuilder.append("             xmlns:feel=\"http://www.omg.org/spec/DMN/20180521/FEEL/\">\n");

        for (int i = 0; i < numberOfElements; i++) {
            dmnBuilder.append(getSubDecision(i + 1));
        }
        dmnBuilder.append(getMainDecision(numberOfElements));

        dmnBuilder.append("</definitions>");

        return dmnBuilder.toString();
    }

    private Object getMainDecision(int numberOfElements) {
        final StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("  <decision id=\"maindecision\" name=\"maindecision\">\n");
        contextBuilder.append("    <variable name=\"maindecision\" typeRef=\"number\"/>\n");
        for (int i = 0; i < numberOfElements; i++) {
            contextBuilder.append("    <informationRequirement><requiredDecision href=\"#decision" + (i + 1) + "\"/></informationRequirement>");
        }
        String expr = IntStream.range(1, numberOfElements + 1).mapToObj(x -> "decision" + x).collect(Collectors.joining(" + "));
        contextBuilder.append("    <literalExpression typeRef=\"number\"><text>" + expr + "</text></literalExpression>\n");
        contextBuilder.append("  </decision>\n");
        return contextBuilder.toString();
    }

    private String getSubDecision(final int index) {
        final StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("  <decision id=\"decision" + index + "\" name=\"decision" + index + "\">\n");
        contextBuilder.append("    <variable name=\"decision" + index + "\" typeRef=\"number\"/>\n");
        contextBuilder.append("    <literalExpression typeRef=\"number\"><text>" + index + "</text></literalExpression>\n");
        contextBuilder.append("  </decision>\n");
        return contextBuilder.toString();
    }
}
