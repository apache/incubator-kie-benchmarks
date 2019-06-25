/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

public class DecisionTablesDMNProvider implements DMNProvider {

    @Override
    public String getDMN() {
        return getDMN(1);
    }

    @Override
    public String getDMN(int numberOfElements) {
        final StringBuilder dmnBuilder = new StringBuilder();

        dmnBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        dmnBuilder.append("<definitions id=\"decision-table-id\" name=\"decision-table-name\"\n");
        dmnBuilder.append("             namespace=\"https://github.com/kiegroup/kie-dmn\"\n");
        dmnBuilder.append("xmlns=\"http://www.omg.org/spec/DMN/20180521/MODEL/\"  ");
        dmnBuilder.append("xmlns:triso=\"http://www.trisotech.com/2015/triso/modeling\"  ");
        dmnBuilder.append("xmlns:dmndi=\"http://www.omg.org/spec/DMN/20180521/DMNDI/\"  ");
        dmnBuilder.append("xmlns:di=\"http://www.omg.org/spec/DMN/20180521/DI/\"  ");
        dmnBuilder.append("xmlns:dc=\"http://www.omg.org/spec/DMN/20180521/DC/\"  ");
        dmnBuilder.append("xmlns:trisodmn=\"http://www.trisotech.com/2016/triso/dmn\"  ");
        dmnBuilder.append("xmlns:feel=\"http://www.omg.org/spec/DMN/20180521/FEEL/\"  ");
        dmnBuilder.append("xmlns:tc=\"http://www.omg.org/spec/DMN/20160719/testcase\"  ");
        dmnBuilder.append("xmlns:drools=\"http://www.drools.org/kie/dmn/1.1\"  ");
        dmnBuilder.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  ");
        dmnBuilder.append("xmlns:rss=\"http://purl.org/rss/2.0/\"  ");
        dmnBuilder.append(" > \n");


        for (int i = 0; i < numberOfElements; i++) {
            dmnBuilder.append("    <!-- " + i + " -->\n");
            dmnBuilder.append("    <inputData id=\"leftInput_" + i + "\" name=\"leftInput_" + i + "\">\n");
            dmnBuilder.append("        <variable name=\"leftInput_" + i + "\" id=\"leftInput_" + i + "var\" typeRef=\"string\"/>\n");
            dmnBuilder.append("    </inputData>\n");
            dmnBuilder.append("    <inputData id=\"rightInput_" + i + "\" name=\"rightInput_" + i + "\">\n");
            dmnBuilder.append("        <variable name=\"rightInput_" + i + "\" id=\"rightInput_" + i + "var\" typeRef=\"string\"/>\n");
            dmnBuilder.append("    </inputData>\n");
            dmnBuilder.append("    <decision id=\"myDecision_" + i + "\" name=\"myDecision_" + i + "\">\n");
            dmnBuilder.append("        <variable name=\"myDecision_" + i + "\" id=\"myDecision_" + i + "var\" typeRef=\"string\"/>\n");
            dmnBuilder.append("        <informationRequirement >\n");
            dmnBuilder.append("            <requiredInput href=\"#leftInput_" + i + "\"/>\n");
            dmnBuilder.append("        </informationRequirement>\n");
            dmnBuilder.append("        <informationRequirement >\n");
            dmnBuilder.append("            <requiredInput href=\"#rightInput_" + i + "\"/>\n");
            dmnBuilder.append("        </informationRequirement>\n");
            dmnBuilder.append("        <decisionTable hitPolicy=\"UNIQUE\" outputLabel=\"myDecision_" + i + "\" typeRef=\"string\">\n");
            dmnBuilder.append("            <input >\n");
            dmnBuilder.append("                <inputExpression typeRef=\"string\">\n");
            dmnBuilder.append("                    <text>leftInput_" + i + "</text>\n");
            dmnBuilder.append("                </inputExpression>\n");
            dmnBuilder.append("            </input>\n");
            dmnBuilder.append("            <input >\n");
            dmnBuilder.append("                <inputExpression typeRef=\"string\">\n");
            dmnBuilder.append("                    <text>rightInput_" + i + "</text>\n");
            dmnBuilder.append("                </inputExpression>\n");
            dmnBuilder.append("            </input>\n");
            dmnBuilder.append("            <output />\n");
            dmnBuilder.append("            <annotation name=\"Description\"/>\n");
            dmnBuilder.append("            <rule >\n");
            dmnBuilder.append("                <inputEntry >\n");
            dmnBuilder.append("                    <text>\"a\"</text>\n");
            dmnBuilder.append("                </inputEntry>\n");
            dmnBuilder.append("                <inputEntry >\n");
            dmnBuilder.append("                    <text>-</text>\n");
            dmnBuilder.append("                </inputEntry>\n");
            dmnBuilder.append("                <outputEntry >\n");
            dmnBuilder.append("                    <text>\"left A\"</text>\n");
            dmnBuilder.append("                </outputEntry>\n");
            dmnBuilder.append("                <annotationEntry>\n");
            dmnBuilder.append("                    <text/>\n");
            dmnBuilder.append("                </annotationEntry>\n");
            dmnBuilder.append("            </rule>\n");
            dmnBuilder.append("            <rule >\n");
            dmnBuilder.append("                <inputEntry >\n");
            dmnBuilder.append("                    <text>-</text>\n");
            dmnBuilder.append("                </inputEntry>\n");
            dmnBuilder.append("                <inputEntry >\n");
            dmnBuilder.append("                    <text>\"a\"</text>\n");
            dmnBuilder.append("                </inputEntry>\n");
            dmnBuilder.append("                <outputEntry >\n");
            dmnBuilder.append("                    <text>\"right A\"</text>\n");
            dmnBuilder.append("                </outputEntry>\n");
            dmnBuilder.append("                <annotationEntry>\n");
            dmnBuilder.append("                    <text/>\n");
            dmnBuilder.append("                </annotationEntry>\n");
            dmnBuilder.append("            </rule>\n");
            dmnBuilder.append("            <rule >\n");
            dmnBuilder.append("                <inputEntry >\n");
            dmnBuilder.append("                    <text>not(\"a\")</text>\n");
            dmnBuilder.append("                </inputEntry>\n");
            dmnBuilder.append("                <inputEntry >\n");
            dmnBuilder.append("                    <text>not(\"a\")</text>\n");
            dmnBuilder.append("                </inputEntry>\n");
            dmnBuilder.append("                <outputEntry >\n");
            dmnBuilder.append("                    <text>\"A not found\"</text>\n");
            dmnBuilder.append("                </outputEntry>\n");
            dmnBuilder.append("                <annotationEntry>\n");
            dmnBuilder.append("                    <text/>\n");
            dmnBuilder.append("                </annotationEntry>\n");
            dmnBuilder.append("            </rule>\n");
            dmnBuilder.append("        </decisionTable>\n");
            dmnBuilder.append("    </decision>\n");

            dmnBuilder.append("    <decision id=\"layer_myDecision_" + i + "\" name=\"layer_myDecision_" + i + "\">\n");
            dmnBuilder.append("        <variable name=\"layer_myDecision_" + i + "\" id=\"layer_myDecision_" + i + "var\" typeRef=\"string\"/>\n");
            dmnBuilder.append("        <informationRequirement >\n");
            dmnBuilder.append("            <requiredDecision href=\"#myDecision_" + i + "\"/>\n");
            dmnBuilder.append("        </informationRequirement>\n");
            dmnBuilder.append("        <literalExpression typeRef=\"string\" >\n");
            dmnBuilder.append("            <text>\"decision was: \" + myDecision_" + i + "</text>\n");
            dmnBuilder.append("        </literalExpression>\n");
            dmnBuilder.append("    </decision>");

        }
        dmnBuilder.append("</definitions>");

        return dmnBuilder.toString();
    }

}
