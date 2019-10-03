package org.drools.benchmarks.turtle.runtime;

import org.drools.benchmarks.common.util.RuntimeUtil;
import org.drools.benchmarks.model.Account;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;

import java.util.ArrayList;
import java.util.List;

public class KBaseCreationFromDTable1Benchmark extends AbstractSimpleRuntimeBenchmark {

    private static int from = 999;
    private static int to = 3102;

    @Setup
    public void addResources() {
        addClassPathResource("kbase-creation/dtable1-kbase-creation.xls");
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
        ksession.fireAllRules();
        return ksession;
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
