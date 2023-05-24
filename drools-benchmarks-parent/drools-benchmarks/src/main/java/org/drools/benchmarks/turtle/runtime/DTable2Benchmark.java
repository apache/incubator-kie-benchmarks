package org.drools.benchmarks.turtle.runtime;

import org.drools.benchmarks.turtle.runtime.generator.GeneratorConfiguration;
import org.drools.benchmarks.turtle.runtime.generator.KBaseCreationFromDTable2Generator;
import org.kie.api.runtime.KieSession;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Benchmark;

@Warmup(iterations = 2000)
@Measurement(iterations = 200)
public class DTable2Benchmark extends AbstractSimpleRuntimeBenchmark {

    @Param({"20000"})
    int nrOfFacts;

    @Setup
    public void addResources() {
        addClassPathResource("kbase-creation/dtable2-kbase-creation.xls");
    }

    @Override
    protected void addFactsGenerators() {
        addFactsGenerator(new KBaseCreationFromDTable2Generator(getGeneratorConfiguration()),nrOfFacts);
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
