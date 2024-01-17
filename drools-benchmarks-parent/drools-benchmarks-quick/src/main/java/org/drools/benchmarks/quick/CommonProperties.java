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
package org.drools.benchmarks.quick;

import org.kie.api.builder.ReleaseId;
import org.kie.util.maven.support.ReleaseIdImpl;

public class CommonProperties {

    private CommonProperties() {
        //Avoid instantiation
    }

    public static final String MODULE_GROUPID ="org.drools";
    public static final String MODULE_ARTIFACTID ="drools-benchmarks-module";
    public static final String MODULE_VERSION ="1.0-SNAPSHOT";

    public static final String MODULE_KIEBASE ="benchmarks-module";

    public static final ReleaseId MODULE_RELEASEID = new ReleaseIdImpl(MODULE_GROUPID, MODULE_ARTIFACTID, MODULE_VERSION);
}
