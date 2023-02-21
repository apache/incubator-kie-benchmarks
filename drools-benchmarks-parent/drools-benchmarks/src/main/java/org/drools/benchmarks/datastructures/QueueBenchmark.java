package org.drools.benchmarks.datastructures;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@State(Scope.Benchmark)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)
public class QueueBenchmark {

    @Param({"true", "false"})
    private boolean useTreeSet;

    @Param({"1000"})
    private int size;

    private Queue<StringQueueEntry> queue;

    private StringQueueEntry[] values;

    @Setup
    public void setup() {
        values = new StringQueueEntry[size];
        for (int i = 0; i < size; i++) {
            values[i] = new StringQueueEntry(UUID.randomUUID().toString());
        }

        queue = useTreeSet ?
                new TreeSetQueue<>(StringQueueEntry.StringQueueEntryComparator.INSTANCE) :
                new BinaryHeapQueue<>(StringQueueEntry.StringQueueEntryComparator.INSTANCE);
    }

    @Benchmark
    public int benchmark() {
        for (int i = 0; i < size; i++) {
            queue.enqueue(values[i]);
        }

        while (true) {
            // remove and entry in the middle ...
            StringQueueEntry entry = values[queue.size() / 2];
            queue.dequeue(entry);

            // ... and one at the end
            queue.dequeue();

            // if the queue is empty terminates the benchmark
            if (queue.isEmpty()) {
                break;
            }

            // otherwise enqueue again the item that was in the middle
            queue.enqueue(entry);
        }

        return queue.size();
    }
}
