/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.benchmarks.common;

import org.drools.benchmarks.common.util.TestUtil;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.conf.KieBaseOption;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.utils.KieHelper;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SingleShotTime)
@State(Scope.Thread)
@Warmup(iterations = 30)
@Measurement(iterations = 20)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public abstract class AbstractBenchmark {

    protected KieBase kieBase;
    protected KieSession kieSession;
    protected StatelessKieSession statelessKieSession;

    protected boolean isSmokeTestsRun = TestUtil.isSmokeTestsRun();

    public abstract void setup();

    @TearDown(Level.Iteration)
    public void tearDown() {
        if (kieSession != null) {
            kieSession.dispose();
            kieSession = null;
        }
        statelessKieSession = null;
    }

    protected void createKieSession() {
        kieSession = kieBase.newKieSession();
    }

    protected void createStatelessKieSession() {
        statelessKieSession = kieBase.newStatelessKieSession();
    }

    protected void createEmptyKieBase() {
        kieBase = new KieHelper().build(TestUtil.getKieBaseConfiguration());
    }

    protected void createKieBaseFromDrl(String drl, KieBaseOption... options) {
        KieBaseConfiguration conf = TestUtil.getKieBaseConfiguration();
        if (options != null) {
            for (KieBaseOption option : options) {
                conf.setOption(option);
            }
        }
        kieBase = new KieHelper().addContent(drl, ResourceType.DRL).build(conf);
    }
}
