/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.benchmarks.datastructures;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toList;

public class TreeSetQueue<T extends Queue.QueueEntry> implements Queue<T> {

    /** The elements in this heap. */
    private TreeSet<T> elements;

    public TreeSetQueue() {

    }

    /**
     * Constructs a new <code>BinaryHeap</code>.
     *
     * @param comparator the comparator used to order the elements, null
     *                   means use natural order
     */
    public TreeSetQueue(final Comparator<T> comparator) {
        this.elements = new TreeSet<>(comparator);
    }

    /**
     * Clears all elements from queue.
     */
    @Override
    public void clear() {
        this.elements.clear();
    }

    @Override
    public Collection<T> getAll() {
        return elements;
    }

    /**
     * Tests if queue is empty.
     *
     * @return <code>true</code> if queue is empty; <code>false</code>
     *         otherwise.
     */
    @Override
    public boolean isEmpty() {
        return this.elements.isEmpty();
    }

    /**
     * Returns the number of elements in this heap.
     *
     * @return the number of elements in this heap
     */
    @Override
    public int size() {
        return this.elements.size();
    }

    @Override
    public T peek() {
        return isEmpty() ? null : elements.last();
    }

    /**
     * Inserts an Queueable into queue.
     *
     * @param element the Queueable to be inserted
     */
    @Override
    public void enqueue(final T element) {
        elements.add( element );
    }

    /**
     * Returns the Queueable on top of heap and remove it.
     *
     * @return the Queueable at top of heap
     */
    @Override
    public T dequeue() {
        return elements.pollLast();
    }

    @Override
    public void dequeue(T activation) {
        elements.remove(activation);
    }

    @Override
    public String toString() {
        return Stream.of( elements ).filter(Objects::nonNull).collect(toList() ).toString();
    }
}
