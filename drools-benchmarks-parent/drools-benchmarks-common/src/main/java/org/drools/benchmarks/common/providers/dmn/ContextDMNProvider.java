/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

public class ContextDMNProvider implements DMNProvider {

    @Override
    public String getDMN() {
        return getDMN(1);
    }

    @Override
    public String getDMN(int numberOfElements) {
        final StringBuilder dmnBuilder = new StringBuilder();

        dmnBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        dmnBuilder.append("<definitions id=\"dmn-context\" name=\"dmn-context\"\n");
        dmnBuilder.append("             namespace=\"https://github.com/kiegroup/kie-dmn\"\n");
        dmnBuilder.append("             xmlns=\"http://www.omg.org/spec/DMN/20151101/dmn.xsd\"\n");
        dmnBuilder.append("             xmlns:feel=\"http://www.omg.org/spec/FEEL/20140401\">\n");
        dmnBuilder.append("             xsi:schemaLocation=\"http://www.omg.org/spec/DMN/20151101/dmn.xsd http://www.omg.org/spec/DMN/20151101/dmn.xsd \"\n");

        for (int i = 0; i < numberOfElements; i++) {
            dmnBuilder.append(getContext(i));
        }

        dmnBuilder.append("</definitions>");

        return dmnBuilder.toString();
    }

    private String getContext(final int index) {
        final StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("  <decision id=\"decision" + index + "\" name=\"decision" + index + "\">\n");
        contextBuilder.append("    <variable name=\"DecisionVar" + index + "\" typeRef=\"feel:context\"/>\n");
        contextBuilder.append("    <context>\n");
        contextBuilder.append("      <contextEntry>\n");
        contextBuilder.append("        <variable name=\"entry" + index + "-1\" typeRef=\"feel:string\"/>\n");
        contextBuilder.append("        <literalExpression>\n");
        contextBuilder.append("          <text>\"foo" + index + "-1\"</text>\n");
        contextBuilder.append("        </literalExpression>\n");
        contextBuilder.append("      </contextEntry>\n");
        contextBuilder.append("      <contextEntry>\n");
        contextBuilder.append("        <variable name=\"entry" + index + "-2\" typeRef=\"feel:string\"/>\n");
        contextBuilder.append("        <literalExpression>\n");
        contextBuilder.append("          <text>\"foo" + index + "-2\"</text>\n");
        contextBuilder.append("        </literalExpression>\n");
        contextBuilder.append("      </contextEntry>\n");
        contextBuilder.append("      <contextEntry>\n");
        contextBuilder.append("        <variable name=\"entry" + index + "-3\" typeRef=\"feel:string\"/>\n");
        contextBuilder.append("        <literalExpression>\n");
        contextBuilder.append("          <text>\"foo" + index + "-3\"</text>\n");
        contextBuilder.append("        </literalExpression>\n");
        contextBuilder.append("      </contextEntry>\n");
        contextBuilder.append("    </context>\n");
        contextBuilder.append("  </decision>\n");
        return contextBuilder.toString();
    }
}
