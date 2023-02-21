package org.drools.benchmarks.datastructures;

import java.util.Collection;

public interface Queue<T extends Queue.QueueEntry> {
    void enqueue(T queueable);

    T dequeue();
    void dequeue(T activation);

    boolean isEmpty();

    void clear();

    Collection<T> getAll();

    int size();

    T peek();

    interface QueueEntry {

        int getQueueIndex();

        void setQueueIndex(int index);
    }
}