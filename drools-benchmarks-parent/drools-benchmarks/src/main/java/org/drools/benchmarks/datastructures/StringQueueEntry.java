package org.drools.benchmarks.datastructures;

import java.util.Comparator;
import java.util.Objects;

public class StringQueueEntry implements Queue.QueueEntry {

    private final String value;

    private int queueIndex;

    public StringQueueEntry(String value) {
        this.value = value;
    }

    @Override
    public int getQueueIndex() {
        return queueIndex;
    }

    @Override
    public void setQueueIndex(int queueIndex) {
        this.queueIndex = queueIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringQueueEntry that = (StringQueueEntry) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public static class StringQueueEntryComparator implements Comparator<StringQueueEntry> {

        public static final StringQueueEntryComparator INSTANCE = new StringQueueEntryComparator();

        @Override
        public int compare(StringQueueEntry o1, StringQueueEntry o2) {
            return o1.value.compareTo(o2.value);
        }
    }
}
