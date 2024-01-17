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

import java.util.BitSet;

import org.drools.benchmarks.common.DMNProvider;

public class DTNoGapsNoOverlapsDMNProvider implements DMNProvider {

    @Override
    public String getDMN() {
        return getDMN(1);
    }

    @Override
    public String getDMN(int param) {
        final StringBuilder dmnBuilder = new StringBuilder();

        dmnBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        dmnBuilder.append("<definitions id=\"dt-nogapsnooverlaps\" name=\"dt-nogapsnooverlaps\"\n");
        dmnBuilder.append("             namespace=\"https://github.com/kiegroup/kie-dmn\"\n");
        dmnBuilder.append("             xmlns=\"http://www.omg.org/spec/DMN/20180521/MODEL/\"\n");
        dmnBuilder.append("             xmlns:feel=\"http://www.omg.org/spec/DMN/20180521/FEEL/\">\n");

        for (int i = 0; i < param; i++) {
            dmnBuilder.append(getInputData(i));
        }

        dmnBuilder.append("<decision id=\"_266fa9de-5c8e-40b4-8c5e-775ddb7c910e\" name=\"decision\">\n" +
                          "        <variable name=\"decision\" id=\"_06bfb08f-2a56-4c18-a159-6b96bba97e30\" typeRef=\"string\"/>");
        for (int i = 0; i < param; i++) {
            dmnBuilder.append(getIR(i));
        }
        dmnBuilder.append("        <decisionTable hitPolicy=\"UNIQUE\" outputLabel=\"decision\" typeRef=\"string\" >");
        for (int i = 0; i < param; i++) {
            dmnBuilder.append(getDTInput(i));
        }
        dmnBuilder.append("            <output id=\"_a9b77f43-e2e6-4c59-a441-65501da1a45b\" />\n" +
                          "            <annotation name=\"Description\"/>\n");
        double totalRows = Math.pow(2, param);
        for (int i = 0; i < totalRows; i++) {
            dmnBuilder.append(getRule(i, param));
        }

        dmnBuilder.append("        </decisionTable>\n" +
                          "    </decision>");
        dmnBuilder.append("</definitions>");

        return dmnBuilder.toString();
    }

    private Object getRule(int i, int param) {
        final StringBuilder sb = new StringBuilder();
        BitSet bs = BitSet.valueOf(new long[]{i});

        sb.append("<rule>\n");
        for (int ri = 0; ri < param; ri++) {
            sb.append("<inputEntry><text>");
            sb.append(bs.get(ri) ? "&gt;=" : "&lt;");
            sb.append("0</text></inputEntry>\n");
        }
        sb.append("<outputEntry><text>\"");
        for (int ri = 0; ri < param; ri++) {
            sb.append(bs.get(ri) ? "M" : "m");
        }
        sb.append("\"</text></outputEntry>\n");
        sb.append("<annotationEntry><text/></annotationEntry>\n");
        sb.append("</rule>\n");

        return sb.toString();
    }

    private Object getDTInput(int i) {
        return "            <input id=\"dtinput" + i + "\" label=\"i" + i + "\">\n" +
               "                <inputExpression typeRef=\"number\">\n" +
               "                    <text>i" + i + "</text>\n" +
               "                </inputExpression>\n" +
               "            </input>";
    }

    private Object getIR(int i) {
        return "        <informationRequirement id=\"_ir" + i + "\">\n" +
               "            <requiredInput href=\"#i" + i + "\"/>\n" +
               "        </informationRequirement>";
    }

    private Object getInputData(int i) {
        return "    <inputData id=\"i" + i + "\" name=\"i" + i + "\">\n" +
               "        <variable name=\"i" + i + "\" id=\"i" + i + "var\" typeRef=\"number\"/>\n" +
               "    </inputData>";
    }

}
