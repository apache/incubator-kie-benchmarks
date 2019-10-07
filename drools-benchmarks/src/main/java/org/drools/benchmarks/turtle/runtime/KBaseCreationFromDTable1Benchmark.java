package org.drools.benchmarks.turtle.runtime;

import org.drools.benchmarks.model.Account;
import org.kie.api.runtime.KieSession;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Benchmark;

import java.util.ArrayList;
import java.util.List;

@Warmup(iterations = 100000)
@Measurement(iterations = 10000)
public class KBaseCreationFromDTable1Benchmark extends AbstractSimpleRuntimeBenchmark {

    private static int from = 999;
    private static int to = 3102;

    @Setup
    public void addResources() {
        addClassPathResource("kbase-creation/dtable1-kbase-creation.xls");
    }

    @Benchmark
    public KieSession timeKBaseCreationFromOneBigAndOneSmallDTable() {
        return insertFactsAndFireAllRules();
    }

    @Override
    protected List<Object> generateFacts() {
        List<Object> facts = new ArrayList<Object>();
        for (int i = from; i < to; i++) {
            Account account = new Account();
            account.setNumber(i);
            facts.add(account);
        }
        return facts;
    }

}
