package org.drools.benchmarks.turtle.runtime;

import org.drools.benchmarks.common.model.Account;
import org.kie.api.runtime.KieSession;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Benchmark;

import java.util.ArrayList;
import java.util.List;

@Warmup(iterations = 100000)
@Measurement(iterations = 10000)
public class DTable1Benchmark extends AbstractSimpleRuntimeBenchmark {
    @Param("FROM999_TO3102_DTABLE1")
    DTableFactRange range;

    @Setup
    public void addResources() {
        addClassPathResource(range.getFilename());
    }

    @Benchmark
    public KieSession timeFactsInsertionAndRulesFiring() {
        return insertFactsAndFireAllRules();
    }

    @Override
    protected List<Object> generateFacts() {
        List<Object> facts = new ArrayList<Object>();
        for (int i = range.getFrom(); i < range.getTo(); i++) {
            Account account = new Account();
            account.setNumber(i);
            facts.add(account);
        }
        return facts;
    }

    public enum DTableFactRange {
        MEDIUM_DTABLE1("kbase-creation/dtable1-kbase-creation.xls", 999,3102);

        DTableFactRange(String file, int from, int to) {
            this.filename = file;
            this.to = to;
            this.from = from;
        }

        private String filename;

        private int from;

        private int to;

        public String getFilename() {
            return filename;
        }

        public int getFrom() {
            return from;
        }

        public int getTo() {
            return to;
        }
    }

}
