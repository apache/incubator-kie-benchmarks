package org.jboss.qa.brms.performance.profiler;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.IterationParams;
import org.openjdk.jmh.profile.InternalProfiler;
import org.openjdk.jmh.results.AggregationPolicy;
import org.openjdk.jmh.results.IterationResult;
import org.openjdk.jmh.results.Result;
import org.openjdk.jmh.results.ScalarResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryConsumptionProfiler implements InternalProfiler {

    private static final Logger logger = LoggerFactory.getLogger(MemoryConsumptionProfiler.class);

    private static final long MAX_WAIT_MSEC = 20 * 1000;

    private final AtomicBoolean proceedWithMetrics = new AtomicBoolean(true);

    private GcCounter gcCounter;

    public MemoryConsumptionProfiler() {
        try {
            gcCounter = new GcCounter();
        } catch (IllegalStateException ex) {
            logger.error("{0} deactivated due to initialization error.", getClass().getSimpleName(), ex);
            proceedWithMetrics.set(false);
        }
    }

    @Override
    public void beforeIteration(BenchmarkParams benchmarkParams, IterationParams iterationParams) {
        // nothing to do
    }

    @Override
    public Collection<? extends Result> afterIteration(BenchmarkParams benchmarkParams,
                                                       IterationParams iterationParams,
                                                       IterationResult result) {
        if (!proceedWithMetrics.get()) {
            return Collections.EMPTY_LIST;
        }

        final UsedMemory usedMemory;
        try {
            usedMemory = getUsedMemoryAfterSettling();
        } catch (IllegalStateException ex) {
            logger.error("{0} deactivated due to error during metrics recording.", getClass().getSimpleName(), ex);
            return Collections.EMPTY_LIST;
        }
        List<Result> results = new ArrayList<>();
        double usedHeap = bytesToKB(usedMemory.usedHeapMemory);
        double usedNonHeap = bytesToKB(usedMemory.usedNonHeapMemory);
        double total = bytesToKB(usedMemory.totalCommittedMemory);
        results.add(new ScalarResult("mem.used.heap", usedHeap, "kB", AggregationPolicy.AVG));
        results.add(new ScalarResult("mem.used.nonheap", usedNonHeap, "kB", AggregationPolicy.AVG));
        results.add(new ScalarResult("mem.total", total, "kB", AggregationPolicy.AVG));

        return results;
    }

    private double bytesToKB(long bytes) {
        return bytes / 1024;
    }

    private UsedMemory getUsedMemoryAfterGc() {
        long beforeGcCount = gcCounter.count();
        long startMsec = System.currentTimeMillis();

        System.gc();
        while (System.currentTimeMillis() - startMsec < MAX_WAIT_MSEC) {
            try {
                Thread.sleep(234);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if ((gcCounter.count() > beforeGcCount)) {
                return getUsedMemorySimply();
            }
        }
        throw new IllegalStateException("GS hasn't been detected after calling System.gc()");
    }

    private UsedMemory getUsedMemorySimply() {
        MemoryUsage heapUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        final long total = heapUsage.getCommitted() + nonHeapUsage.getCommitted();
        final long usedHeap = heapUsage.getUsed();
        final long usedNonHeap = nonHeapUsage.getUsed();

        return new UsedMemory(total, usedHeap, usedNonHeap);
    }

    private UsedMemory getUsedMemoryAfterSettling() {
        UsedMemory previousTotal;
        UsedMemory settledTotal = getUsedMemoryAfterGc();
        do {
            try {
                Thread.sleep(123);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            previousTotal = settledTotal;
            settledTotal = getUsedMemoryAfterGc();
        } while (previousTotal.totalCommittedMemory > settledTotal.totalCommittedMemory);
        return settledTotal;
    }

    @Override
    public String getDescription() {
        return "JVM memory consumption profiler using MBeans";
    }

    private static class UsedMemory {
        final long totalCommittedMemory;
        final long usedHeapMemory;
        final long usedNonHeapMemory;

        UsedMemory(final long totalCommittedMemory, final long usedHeapMemory, final long usedNonHeapMemory) {
            this.totalCommittedMemory = totalCommittedMemory;
            this.usedHeapMemory = usedHeapMemory;
            this.usedNonHeapMemory = usedNonHeapMemory;
        }
    }

    private static class GcCounter {
        private final List<GarbageCollectorMXBean> enabledBeans = new ArrayList<>();

        private GcCounter() {
            enabledBeans.addAll(ManagementFactory.getGarbageCollectorMXBeans());
            if (enabledBeans.isEmpty()) {
                throw new IllegalStateException("MXBeans do not provide GC info. Reliable metrics are not available.");
            }
        }

        long count() {
            return enabledBeans.stream().mapToLong(a -> a.getCollectionCount()).sum();
        }
    }
}
