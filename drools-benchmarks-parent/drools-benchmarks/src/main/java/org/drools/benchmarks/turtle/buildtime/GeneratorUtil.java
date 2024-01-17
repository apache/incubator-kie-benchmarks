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

package org.drools.benchmarks.turtle.buildtime;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.openjdk.jmh.util.FileUtils;

public class GeneratorUtil {

    public static void main(String[] args) throws IOException {
        final StringBuilder sb = new StringBuilder();
        sb.append("package org.drools.benchmarks.bre;\n");
        sb.append("\n");
        sb.append("import org.drools.benchmarks.common.model.Account;\n");
        sb.append("import org.drools.benchmarks.common.model.Address;\n");
        sb.append("import org.drools.benchmarks.common.model.Customer;\n");
        sb.append("\n");

        for (int i = 1; i <= 20; i++) {
            sb.append("rule \"accountBalance" + i + "\"\n"
                    + "when \n"
                    + "    $account : Account(balance == " + i + ")\n"
                    + "then\n"
                    + "    modify ($account) { setBalance(" + (-i) + ") };\n"
                    + "end\n");
            sb.append("\n");

            sb.append("rule \"postalCode" + i + "\"\n"
                    + "when \n"
                    + "    $address : Address(postCode != \"" + i + "\")\n"
                    + "then\n"
                    + "    modify ($address) { setPostCode(\"" + i + "\") };\n"
                    + "end\n");
            sb.append("\n");

            sb.append("rule \"accountOwner" + i + "\"\n"
                    + "when \n"
                    + "    $account : Account(balance == " + i + ")\n"
                    + "    $customer : Customer (this == $account.owner)\n"
                    + "then\n"
                    + "    modify ($account) { setBalance(" + (-i) + ") };\n"
                    + "end\n");
            sb.append("\n");

            sb.append("rule \"BrnoPrahaOstrava" + i + "\"\n"
                    + "when \n"
                    + "    $address : Address(city in (\"Brno\", \"Praha\", \"Ostrava\", \"" + i + "\"))\n"
                    + "then\n"
                    + "    modify ($address) { setCity(\"" + i + "\") };\n"
                    + "end\n");
            sb.append("\n");

            sb.append("rule \"exists" + i + "\"\n"
                    + "when \n"
                    + "    $customer: Customer(firstName == \"Jake" + i + "\")\n"
                    + "then\n"
                    + "    modify ($customer) {setFirstName(\"Jackie" + i + "\")} \n"
                    + "end\n");
            sb.append("\n");
        }

        final List<String> lines = Arrays.asList(sb.toString().split("\n"));
        FileUtils.writeLines(new File("drools-benchmarks/src/main/resources/kbase-creation/rules.drl"), lines);
    }

}
