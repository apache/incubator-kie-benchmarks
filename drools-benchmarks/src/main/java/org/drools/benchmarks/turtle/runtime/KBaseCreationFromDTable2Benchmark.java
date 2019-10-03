package org.drools.benchmarks.turtle.runtime;

import org.drools.benchmarks.common.util.RuntimeUtil;
import org.drools.benchmarks.turtle.runtime.generator.GeneratorConfiguration;
import org.drools.benchmarks.turtle.runtime.generator.KBaseCreationFromDTable2Generator;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;

import java.util.ArrayList;
import java.util.List;

public class KBaseCreationFromDTable2Benchmark extends AbstractSimpleRuntimeBenchmark {

    @Setup
    public void addResources() {
        addClassPathResource("kbase-creation/dtable2-kbase-creation.xls");
    }

    @Setup(Level.Iteration)
    public void createKieSession() {
        ksession = RuntimeUtil.createKieSession(kieBase, ClockTypeOption.get("pseudo"));
    }

    @Benchmark
    public KieSession timeKBaseCreationFromOneBigAndOneSmallDTable() {
        return insertFactsAndFireAllRules();
    }

    @Override
    public KieSession insertFactsAndFireAllRules() {
        for (Object fact : facts) {
            ksession.insert(fact);
        }

        return ksession;
    }

    @Override
    protected void addFactsGenerators() {
        addFactsGenerator(new KBaseCreationFromDTable2Generator(getGeneratorConfiguration()),1490);
    }

    @Benchmark
    public KieSession timeFactsInsertionAndRulesFiring() {
        return insertFactsAndFireAllRules();
    }

    private GeneratorConfiguration getGeneratorConfiguration() {
        final GeneratorConfiguration generatorConfiguration = new GeneratorConfiguration(
                1490, 5, 0.5);
        return generatorConfiguration;
    }
}
