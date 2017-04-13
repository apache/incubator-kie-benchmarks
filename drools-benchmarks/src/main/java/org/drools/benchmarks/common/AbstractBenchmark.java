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

import java.util.concurrent.TimeUnit;
import org.drools.benchmarks.common.util.ReteDumper;
import org.drools.benchmarks.common.util.TestUtil;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.conf.KieBaseOption;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.conf.KieSessionOption;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@BenchmarkMode(Mode.SingleShotTime)
@State(Scope.Thread)
@Warmup(iterations = 30)
@Measurement(iterations = 20)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public abstract class AbstractBenchmark {

    private Logger logger = LoggerFactory.getLogger(getClass());

    protected KieBase kieBase;
    protected KieSession kieSession;
    protected StatelessKieSession statelessKieSession;

    protected boolean isSmokeTestsRun = TestUtil.isSmokeTestsRun();

    public abstract void setup() throws ProviderException;

    @TearDown(Level.Iteration)
    public void tearDown() {
        if (kieSession != null) {
            kieSession.halt();
            kieSession.dispose();
            kieSession = null;
        }
        statelessKieSession = null;
    }

    protected void createKieSession() {
        kieSession = kieBase.newKieSession();
    }

    protected void createKieSession(final KieSessionOption... kieSessionOptions) {
        kieSession = kieBase.newKieSession(getKieSessionConfiguration(kieSessionOptions), null);
    }

    protected void createStatelessKieSession() {
        statelessKieSession = kieBase.newStatelessKieSession();
    }

    protected void createEmptyKieBase() {
        kieBase = new KieHelper().build(getKieBaseConfiguration());
    }

    protected void createKieBaseFromDrl(final String drl) {
        createKieBaseFromDrl(drl, getKieBaseConfiguration());
    }

    protected void createKieBaseFromDrl(final String drl, final KieBaseOption... kieBaseOptions) {
        createKieBaseFromDrl(drl, getKieBaseConfiguration(kieBaseOptions));
    }

    protected void createKieBaseFromDrl(final String drl, final KieBaseConfiguration kieBaseConfiguration) {
        if (TestUtil.dumpDrl()) {
            logDebug("Benchmark DRL", drl);
        }
        kieBase = new KieHelper().addContent(drl, ResourceType.DRL).build(kieBaseConfiguration);
        dumpReteIfNeeded();
    }

    protected void createKieBaseFromResource(final Resource resource, final KieBaseOption... kieBaseOptions) {
        createKieBaseFromResources(getKieBaseConfiguration(kieBaseOptions), resource);
    }

    protected void createKieBaseFromResources(final KieBaseConfiguration kieBaseConfiguration, final Resource... resources) {
        final KieHelper kieHelper = new KieHelper();
        for (final Resource resource : resources) {
            kieHelper.addResource(resource);
        }

        kieBase = kieHelper.build(kieBaseConfiguration);
        dumpReteIfNeeded();
    }

    private void dumpReteIfNeeded() {
        if (TestUtil.dumpRete()) {
            ReteDumper.dumpRete(kieBase);
        }
    }

    private KieBaseConfiguration getKieBaseConfiguration(final KieBaseOption... kieBaseOptions) {
        final KieBaseConfiguration kieBaseConfiguration = KieServices.Factory.get().newKieBaseConfiguration();
        for (final KieBaseOption kieBaseOption : kieBaseOptions) {
            kieBaseConfiguration.setOption(kieBaseOption);
        }
        return kieBaseConfiguration;
    }

    private KieSessionConfiguration getKieSessionConfiguration(final KieSessionOption... kieSessionOptions) {
        final KieSessionConfiguration kieSessionConfiguration = KieServices.Factory.get().newKieSessionConfiguration();
        for (final KieSessionOption kieSessionOption : kieSessionOptions) {
            kieSessionConfiguration.setOption(kieSessionOption);
        }
        return kieSessionConfiguration;
    }

    private void logDebug(final String caption, final String logContent) {
        logger.info("--------------------------------------------");
        logger.info(caption);
        logger.info("--------------------------------------------");
        logger.info(logContent);
        logger.info("--------------------------------------------");
    }
}
