/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.benchmarks.common.providers.dmn;

import org.drools.benchmarks.common.DMNProvider;

public class DecisionTableDMNProvider implements DMNProvider {

    @Override
    public String getDMN() {
        return getDMN(1);
    }

    @Override
    public String getDMN(int numberOfTableRules) {
        final StringBuilder dmnBuilder = new StringBuilder();

        dmnBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        dmnBuilder.append("<definitions id=\"decision-table-id\" name=\"decision-table-name\"\n");
        dmnBuilder.append("             namespace=\"https://github.com/kiegroup/kie-dmn\"\n");
        dmnBuilder.append("             xmlns=\"http://www.omg.org/spec/DMN/20151101/dmn.xsd\"\n");
        dmnBuilder.append("             xmlns:feel=\"http://www.omg.org/spec/FEEL/20140401\"\n");
        dmnBuilder.append("             xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        dmnBuilder.append("             xsi:schemaLocation=\"http://www.omg.org/spec/DMN/20151101/dmn.xsd\">\n");

        dmnBuilder.append("  <decision id=\"decision-table\" name=\"decision-table\">\n");
        dmnBuilder.append("    <variable name=\"Approval Status\" typeRef=\"feel:string\"/>\n");
        dmnBuilder.append("    <informationRequirement>\n");
        dmnBuilder.append("      <requiredInput href=\"#_Age\"/>\n");
        dmnBuilder.append("    </informationRequirement>\n");
        dmnBuilder.append("    <informationRequirement>\n");
        dmnBuilder.append("      <requiredInput href=\"#_RiskCategory\"/>\n");
        dmnBuilder.append("    </informationRequirement>\n");
        dmnBuilder.append("    <informationRequirement>\n");
        dmnBuilder.append("      <requiredInput href=\"#_isAffordable\"/>\n");
        dmnBuilder.append("    </informationRequirement>\n");
        dmnBuilder.append("    <decisionTable hitPolicy=\"FIRST\" outputLabel=\"Approval Status\" preferredOrientation=\"Rule-as-Row\">\n");
        dmnBuilder.append("      <input id=\"_iAge\" label=\"Age\">\n");
        dmnBuilder.append("        <inputExpression typeRef=\"feel:number\">\n");
        dmnBuilder.append("          <text>Age</text>\n");
        dmnBuilder.append("        </inputExpression>\n");
        dmnBuilder.append("      </input>\n");
        dmnBuilder.append("      <input id=\"_iRiskCategory\" label=\"RiskCategory\">\n");
        dmnBuilder.append("        <inputExpression typeRef=\"feel:string\">\n");
        dmnBuilder.append("          <text>RiskCategory</text>\n");
        dmnBuilder.append("        </inputExpression>\n");
        dmnBuilder.append("        <inputValues>\n");
        dmnBuilder.append("          <text>\"High\", \"Low\", \"Medium\"</text>\n");
        dmnBuilder.append("        </inputValues>\n");
        dmnBuilder.append("      </input>\n");
        dmnBuilder.append("      <input id=\"_iIsAffordable\" label=\"isAffordable\">\n");
        dmnBuilder.append("        <inputExpression typeRef=\"feel:boolean\">\n");
        dmnBuilder.append("          <text>isAffordable</text>");
        dmnBuilder.append("        </inputExpression>\n");
        dmnBuilder.append("      </input>\n");
        dmnBuilder.append("      <output id=\"_oApprovalStatus\">\n");
        dmnBuilder.append("        <outputValues>\n");
        dmnBuilder.append("          <text>\"Approved\", \"Declined\"</text>\n");
        dmnBuilder.append("        </outputValues>\n");
        dmnBuilder.append("      </output>\n");

        for (int i = 0; i < numberOfTableRules; i++) {
            dmnBuilder.append(getDecisionTableRules(i));
        }

        dmnBuilder.append("    </decisionTable>\n");
        dmnBuilder.append("  </decision>\n");

        dmnBuilder.append("  <inputData id=\"_Age\" name=\"Age\">\n");
        dmnBuilder.append("    <variable name=\"Age\" typeRef=\"feel:number\"/>\n");
        dmnBuilder.append("  </inputData>\n");
        dmnBuilder.append("  <inputData id=\"_RiskCategory\" name=\"RiskCategory\">\n");
        dmnBuilder.append("    <variable name=\"RiskCategory\" typeRef=\"feel:string\"/>\n");
        dmnBuilder.append("  </inputData>\n");
        dmnBuilder.append("  <inputData id=\"_isAffordable\" name=\"isAffordable\">\n");
        dmnBuilder.append("    <variable name=\"isAffordable\" typeRef=\"feel:boolean\"/>\n");
        dmnBuilder.append("  </inputData>\n");
        dmnBuilder.append("</definitions>");

        return dmnBuilder.toString();
    }

    private String getDecisionTableRules(final int index) {
        final StringBuilder ruleBuilder = new StringBuilder();
        ruleBuilder.append("      <rule id=\"rule" + index + "\">\n");
        ruleBuilder.append("        <inputEntry id=\"inputEntry" + index + "-1\">\n");
        ruleBuilder.append("          <text>>=" + index + "</text>\n");
        ruleBuilder.append("        </inputEntry>\n");
        ruleBuilder.append("        <inputEntry id=\"inputEntry" + index + "-2\">\n");
        ruleBuilder.append("          <text>\"Medium\",\"Low\"</text>\n");
        ruleBuilder.append("        </inputEntry>\n");
        ruleBuilder.append("        <inputEntry id=\"inputEntry" + index + "-3\">\n");
        ruleBuilder.append("          <text>true</text>\n");
        ruleBuilder.append("        </inputEntry>\n");
        ruleBuilder.append("        <outputEntry id=\"outputEntry" + index + "\">\n");
        if (index % 3 == 0) {
            ruleBuilder.append("          <text>\"Declined\"</text>");
        } else {
            ruleBuilder.append("          <text>\"Approved\"</text>");
        }
        ruleBuilder.append("        </outputEntry>\n");
        ruleBuilder.append("      </rule>\n");
        return ruleBuilder.toString();
    }
}
