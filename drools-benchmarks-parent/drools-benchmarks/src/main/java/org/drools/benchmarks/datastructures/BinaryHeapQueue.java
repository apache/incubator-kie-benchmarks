package org.drools.benchmarks.datastructures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class BinaryHeapQueue<T extends Queue.QueueEntry> implements Queue<T> {

    /** The default capacity for a binary heap. */
    private static final int DEFAULT_CAPACITY = 13;

    /** The comparator used to order the elements */
    private Comparator<T> comparator;

    /** The number of elements currently in this heap. */
    private int size;

    /** The elements in this heap. */
    private ArrayList<T> elements;

    public BinaryHeapQueue() {

    }

    /**
     * Constructs a new <code>BinaryHeap</code> that will use the given
     * comparator to order its elements.
     *
     * @param comparator the comparator used to order the elements, null
     *                   means use natural order
     */
    public BinaryHeapQueue(final Comparator<T> comparator) {
        this(comparator,
                BinaryHeapQueue.DEFAULT_CAPACITY);
    }

    /**
     * Constructs a new <code>BinaryHeap</code>.
     *
     * @param comparator the comparator used to order the elements, null
     *                   means use natural order
     * @param capacity   the initial capacity for the heap
     * @throws IllegalArgumentException if <code>capacity</code> is &lt;= <code>0</code>
     */
    public BinaryHeapQueue(final Comparator<T> comparator,
                           final int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("invalid capacity");
        }

        //+1 as 0 is noop
        this.elements = new ArrayList<>(capacity +1);
        this.elements.add(null); // root is always null
        this.comparator = comparator;
    }

    /**
     * Clears all elements from queue.
     */
    public void clear() {
        this.elements.clear(); ; // for gc
        this.elements.add(null); // 0 must be null;
        this.size = 0;
    }

    /**
     * Tests if queue is empty.
     *
     * @return <code>true</code> if queue is empty; <code>false</code>
     *         otherwise.
     */
    public boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * Tests if queue is full.
     *
     * @return <code>true</code> if queue is full; <code>false</code>
     *         otherwise.
     */
    public  boolean isFull() {
        //+1 as Queueable 0 is noop
        return this.elements.size() == this.size + 1;
    }

    /**
     * Returns the number of elements in this heap.
     *
     * @return the number of elements in this heap
     */
    public int size() {
        return size;
    }

    public T peek() {
        return size > 0 ? this.elements.get(1) : null;
    }

    /**
     * Inserts an Queueable into queue.
     *
     * @param element the Queueable to be inserted
     */
    public void enqueue(final T element) {
        if ( isFull() ) {
            grow();
        }

        percolateUpMaxHeap( element );
    }

    /**
     * Returns the Queueable on top of heap and remove it.
     */
    public T dequeue() {
        if ( isEmpty() ) {
            return null;
        }

        final T result = this.elements.get(1);
        dequeue(result.getQueueIndex());

        return result;
    }

    public void dequeue(T activation) {
        dequeue(activation.getQueueIndex());
    }

    T dequeue(final int index) {
        if ( index < 1 || index > this.size ) {
            return null;
        }

        final T result = this.elements.get(index);

        setElement( index,
                this.elements.get(this.size) );
        this.elements.set(this.size, null);
        this.size--;
        if ( this.size != 0 && index <= this.size ) {
            int compareToParent = 0;
            if ( index > 1 ) {
                compareToParent = compare( this.elements.get(index),
                        this.elements.get(index / 2) );
            }
            if ( index > 1 && compareToParent > 0 ) {
                percolateUpMaxHeap( index );
            } else {
                percolateDownMaxHeap( index );
            }
        }

        result.setQueueIndex(-1);

        return result;
    }

    /**
     * Percolates element down heap from the position given by the index.
     * <p>
     * Assumes it is a maximum heap.
     *
     * @param index the index of the element
     */
    protected void percolateDownMaxHeap(final int index) {
        final T element = elements.get(index);
        int hole = index;

        while ((hole * 2) <= size) {
            int child = hole * 2;

            // if we have a right child and that child can not be percolated
            // up then move onto other child
            if (child != size && compare(elements.get(child + 1), elements.get(child)) > 0) {
                child++;
            }

            // if we found resting place of bubble then terminate search
            if (compare(elements.get(child), element) <= 0) {
                break;
            }

            setElement( hole, elements.get(child) );
            hole = child;
        }

        setElement( hole, element);
    }


    /**
     * Percolates element up heap from the position given by the index.
     * <p>
     * Assume it is a maximum heap.
     *
     * @param index the index of the element to be percolated up
     */
    protected void percolateUpMaxHeap(final int index) {
        int hole = index;
        T element = elements.get(hole);

        while (hole > 1 && compare(element, elements.get(hole / 2)) > 0) {
            // save element that is being pushed down
            // as the element "bubble" is percolated up
            final int next = hole / 2;
            setElement( hole, elements.get(next) );
            hole = next;
        }

        setElement( hole, element );
    }

    /**
     * Percolates a new element up heap from the bottom.
     * <p>
     * Assume it is a maximum heap.
     *
     * @param element the element
     */
    protected void percolateUpMaxHeap(final T element) {
        if (isFull()) {
            elements.add(element);
            element.setQueueIndex(++size);
        } else {
            elements.set(++size, element);
            element.setQueueIndex(size);
        }

        percolateUpMaxHeap(size);
    }

    /**
     * Compares two objects using the comparator if specified, or the
     * natural order otherwise.
     *
     * @param a the first object
     * @param b the second object
     * @return -ve if a less than b, 0 if they are equal, +ve if a greater than b
     */
    private int compare(final T a, final T b) {
        return comparator.compare( a, b );
    }

    /**
     * Increases the size of the heap to support additional elements
     */
    private void grow() {
        elements.ensureCapacity(elements.size() * 2);
    }

    private void setElement(final int index,
                            final T element) {
        elements.set(index, element);
        element.setQueueIndex(index);
    }

    public Collection<T> getAll() {
        Collection<T> subList = isEmpty() ? Collections.EMPTY_LIST : elements.subList(1, size +1); // +1 as to is exclusive (from is inclusive)
        return subList;

    }

    @Override
    public String toString() {
        return Stream.of( elements ).filter(Objects::nonNull).collect(toList() ).toString();
    }
}
