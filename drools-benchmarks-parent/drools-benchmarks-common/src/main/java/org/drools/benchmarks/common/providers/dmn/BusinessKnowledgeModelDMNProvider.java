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

import org.drools.benchmarks.common.DMNProvider;

public class BusinessKnowledgeModelDMNProvider implements DMNProvider {

    @Override
    public String getDMN() {
        return getDMN(1);
    }

    @Override
    public String getDMN(int numberOfElements) {
        final StringBuilder dmnBuilder = new StringBuilder();

        dmnBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        dmnBuilder.append("<definitions id=\"business-knowledge-model\" name=\"business-knowledge-model\"\n");
        dmnBuilder.append("             namespace=\"https://github.com/kiegroup/kie-dmn\"\n");
        dmnBuilder.append("             xmlns=\"http://www.omg.org/spec/DMN/20151101/dmn.xsd\"\n");
        dmnBuilder.append("             xmlns:feel=\"http://www.omg.org/spec/FEEL/20140401\">\n");

        for (int i = 0; i < numberOfElements; i++) {
            dmnBuilder.append(getDecision(i));
            dmnBuilder.append(getBusinessKnowledgeModel(i));
        }
        dmnBuilder.append("</definitions>");

        return dmnBuilder.toString();
    }

    private String getDecision(final int index) {
        final StringBuilder decisionBuilder = new StringBuilder();
        decisionBuilder.append("  <decision id=\"decision" + index + "\" name=\"decision" + index + "\">\n");
        decisionBuilder.append("    <variable name=\"decisionVariable\" typeRef=\"feel:number\"/>\n");
        decisionBuilder.append("    <knowledgeRequirement>\n");
        decisionBuilder.append("      <requiredKnowledge href=\"#bkm" + index + "\"/>\n");
        decisionBuilder.append("    </knowledgeRequirement>\n");
        decisionBuilder.append("    <literalExpression>\n");
        decisionBuilder.append("      <text>bkm" + index + "()</text>\n");
        decisionBuilder.append("    </literalExpression>\n");
        decisionBuilder.append("  </decision>\n");
        return decisionBuilder.toString();
    }

    private String getBusinessKnowledgeModel(final int index) {
        final StringBuilder bkmBuilder = new StringBuilder();
        bkmBuilder.append("  <businessKnowledgeModel id=\"bkm" + index + "\" name=\"bkm" + index + "\">\n");
        bkmBuilder.append("    <encapsulatedLogic>\n");
        bkmBuilder.append("      <literalExpression expressionLanguage=\"http://www.omg.org/spec/FEEL/20140401\">\n");
        bkmBuilder.append("        <text>" + index + "</text>\n");
        bkmBuilder.append("      </literalExpression>\n");
        bkmBuilder.append("    </encapsulatedLogic>\n");
        bkmBuilder.append("    <variable name=\"bkm" + index + "\" typeRef=\"feel:number\"/>\n");
        bkmBuilder.append("  </businessKnowledgeModel>\n");
        return bkmBuilder.toString();
    }
}
