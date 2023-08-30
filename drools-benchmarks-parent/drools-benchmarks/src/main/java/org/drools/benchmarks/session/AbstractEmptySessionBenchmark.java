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

package org.drools.benchmarks.session;

import java.util.concurrent.TimeUnit;

import org.drools.benchmarks.common.AbstractBenchmark;
import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;

@Warmup(iterations = 200000)
@Measurement(iterations = 100000)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public abstract class AbstractEmptySessionBenchmark extends AbstractBenchmark {

    @Setup
    @Override
    public void setup() {
        kieBase = BuildtimeUtil.createEmptyKieBase();
    }
}
