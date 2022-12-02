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

package org.drools.benchmarks.session;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@Fork(1)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class FactTypeBenchmark {

    @Param({"true", "false"})
    private boolean useCanonicalModel;

    @Param({"10"})
    private int typesNr;

    @Param({"true", "false"})
    private boolean useNotExistingField;

    private final KieServices kieServices = KieServices.get();
    private final KieBaseConfiguration kieBaseConfiguration = kieServices.newKieBaseConfiguration();

    private ReleaseId releaseId;
    private KieBase kieBase;
    private KieSession kieSession;

    @Setup
    public void createKBase() throws IOException {
        Resource ruleResource = kieServices.getResources().newByteArrayResource( generateDrl( typesNr ).getBytes() )
                .setResourceType( ResourceType.DRL ).setSourcePath( "src/main/resouces/rules.drl" );
        releaseId = BuildtimeUtil.createKJarFromResources(useCanonicalModel, ruleResource);
        kieBase = kieServices.newKieContainer(releaseId).newKieBase(kieBaseConfiguration);
    }

    @Setup(Level.Invocation)
    public void createKSession() {
        kieSession = kieBase.newKieSession();
    }

    @TearDown(Level.Invocation)
    public void disposeKSession() {
        kieSession.dispose();
    }

    @Benchmark
    public int fireAllRules() throws IllegalAccessException, InstantiationException {
        for (int i = 0; i < typesNr; i++) {
            FactType factType = kieBase.getFactType( "org.test", "Fact" + i );
            Object fact = factType.newInstance();
            factType.set(fact, "name", "x");
            factType.set(fact, "value", 1);
            if (useNotExistingField) {
                factType.set(fact, "notExisting", 2);
            }
            kieSession.insert(fact);
        }

        int result = kieSession.fireAllRules();
        if (result != typesNr) {
            throw new RuntimeException( "Wrong number of fired rules, expected: " + typesNr + ", actual: " + result );
        }
        return result;
    }

    private String generateDrl(int typesNr) {
        StringBuilder sb = new StringBuilder();
        sb.append( "package org.test;\n" );
        for (int i = 0; i < typesNr; i++) {
            sb.append( "declare Fact" ).append( i ).append("\nname: String\nvalue: int\nend\n");
            sb.append( "rule R" ).append( i ).append(" when\n");
            sb.append( "Fact").append( i ).append("( name == \"x\", value == 1)\n");
            sb.append( "then end\n" );
        }
        return sb.toString();
    }


}
