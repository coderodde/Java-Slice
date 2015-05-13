package net.coderodde.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This utility class implements <b>cyclic</b> array slices. If you move the 
 * slice to the right long enough, the "head" of the slice will wrap around and
 * emerge at the beginning of the array being sliced. Same applies to movement
 * to the left.
 * 
 * @author Rodion "rodde" Efremov
 * @param <E> the actual array component type.
 * @version 1.61
 */
public class Slice<E> implements Iterable<E> {

    /**
     * The actual array being sliced.
     */
    private final E[] array;

    /**
     * The starting index of this slice within <code>array</code>.
     */
    private int fromIndex;

    /**
     * The size of this slice. Another possibility is to maintain an 
     * 'toIndex'. This does not work as well as wanted: if the user wants a
     * slice covering the entire array, we will have 'fromIndex == toIndex', 
     * which will denote also an empty slice. Basically, we are forced to cache
     * the size (length) of any slice. Another arrangement would have been 
     * keeping a boolean value indicating that the slice is full, yet it is 
     * pretty ad-hoc.
     */
    private int size;

    /**
     * Constructs a new slice for <code>array</code> starting at 
     * <code>fromIndex</code> and ending at <code>toIndex - 1</code>.
     * 
     * @param array     the array being sliced.
     * @param fromIndex the starting (inclusive) index.
     * @param toIndex   the ending (exclusive) index.
     */
    private Slice(E[] array, 
                  int fromIndex, 
                  int toIndex) {
        checkArray(array);
        checkIndexForArray(array, fromIndex);
        checkIndexForArray(array, toIndex);
        this.array = array;
        this.fromIndex = fromIndex;
        // 100 10 9
        this.size = fromIndex <= toIndex ? 
                    toIndex - fromIndex :
                    array.length - fromIndex + toIndex;
    }

    /**
     * Initiates the fluent API for constructing aliases.
     * 
     * @param  <E> the array component type.
     * @return array selector.
     */
    public static <E> ArraySelector<E> create() {
        return new ArraySelector<>();
    }
    
    /**
     * Implements an array selector.
     * 
     * @param <E> the array component type.
     */
    public static class ArraySelector<E> {
        
        /**
         * Creates a start index selector.
         * 
         * @param  array the chosen array.
         * @return start index selector.
         */
        public StartIndexSelector<E> withArray(E[] array) {
            return new StartIndexSelector<>(array);
        }
    }
    
    /**
     * Implements a start index selector.
     * 
     * @param <E> the array component type.
     */
    public static class StartIndexSelector<E> {
        
        /**
         * The array being sliced.
         */
        private final E[] array;
        
        /**
         * Constructs this start index selector.
         * 
         * @param array the array being sliced.
         */
        public StartIndexSelector(E[] array) {
            this.array = array;
        }
        
        /**
         * Returns a slice covering the entire array in the same order as 
         * components appear in the array.
         * 
         * @return a slice.
         */
        public Slice<E> all() {
            return new Slice<>(array, 0, array.length);
        }
        
        /**
         * Chooses a particular starting index of a slice being constructed.
         * 
         * @param  fromIndex the starting index.
         * @return second index selector.
         */
        public SecondIndexSelector<E> startingFrom(int fromIndex) {
            return new SecondIndexSelector(array, fromIndex);
        }
    }
    
    /**
     * Implements second index selector.
     * 
     * @param <E> the array component type.
     */
    public static class SecondIndexSelector<E> {
        
        /**
         * The array being sliced.
         */
        private final E[] array;
        
        /**
         * The starting index of a slice being constructed.
         */
        private final int fromIndex;
        
        /**
         * Constructs this second index selector.
         * 
         * @param array     the array being sliced.
         * @param fromIndex the starting index of the slice.
         */
        public SecondIndexSelector(E[] array, int fromIndex) {
            this.array = array;
            this.fromIndex = fromIndex;
        }
        
        /**
         * Returns a slice covering everything starting from 
         * <code>fromIndex</code>.
         * 
         * @return a slice.
         */
        public Slice<E> untilEnd() {
            return new Slice<>(array, fromIndex, array.length);
        }
        
        /**
         * Returns a slice starting at <code>fromIndex</code> and ending at
         * <code>toIndex</code>. If <code>fromIndex</code> is larger than
         * <code>toIndex</code>, the slice wraps around the tail of the array
         * being sliced. 
         * 
         * @param  toIndex the end index (exclusive).
         * @return a slice.
         */
        public Slice<E> until(int toIndex) {
            return new Slice<>(array, fromIndex, toIndex);
        }
    }
    
    /**
     * Returns <code>true</code> if this slice is empty.
     * 
     * @return a boolean value.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the current size of this slice.
     * 
     * @return the current size.
     */
    public int size() {
        return size;
    }

    /**
     * Accesses an element. The indices wrap around to the beginning of the 
     * underlying array.
     * 
     * @param index the target index element.
     * @return the element at the specified index.
     */
    public E get(final int index) {
        checkAccessIndex(index);
        return array[(fromIndex + index) % array.length];
    }

    /**
     * Sets a new value at slice index <code>index</code>.
     * 
     * @param index the target component index.
     * @param value the new value to set.
     */
    public void set(final int index, final E value) {
        checkAccessIndex(index);
        array[(fromIndex + index) % array.length] = value;
    }

    /**
     * Moves this slice. If <code>delta</code> is negative, moves this slice to 
     * the left <code>-delta</code> steps. Otherwise, moves this slice 
     * <code>delta</code> steps to the right. In any case, this slice may wrap
     * around and reappear at the opposite end of the covered array.
     * 
     * @param delta the movement delta.
     */
    public void move(int delta) {
        if (delta < 0) {
            moveLeft(-delta);
        } else {
            moveRight(delta);
        }
    }
    
    /**
     * Shifts the head of this slice. If <code>delta</code> is negative, expands
     * the head of this slice <code>-delta</code> amount of array components.
     * 
     * @param delta the shift delta.
     */
    public void moveHeadPointer(int delta) {
        if (delta < 0) {
            expandHead(-delta);
        } else {
            contractHead(delta);
        }
    }

    /**
     * Shifts the tail of this slice. If <code>delta</code> is negative, 
     * this operations contracts the tail of this slice by <code>-delta<code> 
     * array components. Otherwise, expands tail of this slice 
     * <code>delta</code> array components.
     * 
     * @param delta the shift delta.
     */
    public void moveTailPointer(int delta) {
        if (delta < 0) {
            contractTail(-delta);
        } else {
            expandTail(delta);
        }
    }
    
    /**
     * Reverses the array range covered by this slice.
     */
    public void reverse() {
        for (int l = 0, r = size() - 1; l < r; ++l, --r) {
            final E tmp = get(l);
            set(l, get(r));
            set(r, tmp);
        }
    }

    /**
     * Rotates this slice. If <code>delta</code> is negative, rotates to the
     * left <code>-delta</code> array components. Otherwise, rotates to the 
     * right <code>delta</code> array components.
     * 
     * @param delta rotation delta.
     */
    public void rotate(int delta) {
        if (delta < 0) {
            rotateLeft(-delta);
        } else {
            rotateRight(delta);
        }
    }
    
    /**
     * Returns the iterator over this slice.
     * 
     * @return the iterator.
     */
    @Override
    public Iterator<E> iterator() {
        return new SliceIterator();
    }

    /**
     * Returns the textual representation of this slice.
     * 
     * @return a string.
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        int left = size();

        for (final E element : this) {
            sb.append(element);

            if (--left > 0) {
                sb.append(' ');
            }
        }

        return sb.toString();
    }

    /**
     * Checks that the input array is not <code>null</code>.
     * 
     * @param <E>   the array component type.
     * @param array the array.
     */
    private static <E> void checkArray(final E[] array) {
        if (array == null) {
            throw new NullPointerException("Input array is null.");
        }
    }

    /**
     * Checks that <code>index</code> is legal for an <code>array</code>.
     * 
     * @param <E>   the actual array component type.
     * @param array the array.
     * @param index the index.
     */
    private static <E> void checkIndexForArray(final E[] array,
                                               final int index) {
        if (index < 0) {
            throw new IllegalArgumentException(
                    "The index (" + index + ") may not be negative.");
        }

        if (index > array.length) {
            throw new IllegalArgumentException(
                    "The index (" + index + ") is too large. Should be at " +
                    "most " + array.length);
        }
    }

    /**
     * Checks the access indices.
     * 
     * @param index the index to check.
     */
    private void checkAccessIndex(final int index) {
        final int size = size();

        if (size == 0) {
            throw new NoSuchElementException("Reading from an empty slice.");
        }

        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    "The input index is invalid: " + index + ". Should be " +
                    "in range [0, " + (size - 1) + "].");
        }
    }

    /**
     * Expands the front of this slice by <code>amount</code> array components.
     * This slice may "cycle" the same way as at motion to the left or right.
     * 
     * @param amount the expansion length.
     */
    private void expandHead(final int amount) {
        checkNotNegative(amount);
        final int actualAmount = Math.min(amount, array.length - size());
        fromIndex -= actualAmount;
        size += actualAmount;

        if (fromIndex < 0) {
            fromIndex += array.length;
        }
    }

    /**
     * Contracts the front of this slice by <code>amount</code> array
     * components.
     * 
     * @param amount the contraction length.
     */
    private void contractHead(final int amount) {
        checkNotNegative(amount);
        final int actualAmount = Math.min(amount, size());
        fromIndex += actualAmount;
        size -= actualAmount;

        if (fromIndex >= array.length) {
            fromIndex -= array.length;
        }
    }
    
    /**
     * Expands the back of this slice by <code>amount</code> array components.
     * 
     * @param amount the expansion length.
     */
    private void expandTail(final int amount) {
        checkNotNegative(amount);
        size += Math.min(amount, array.length - size());
    }

    /**
     * Contracts the back of this slice by <code>amount</code> array components.
     * 
     * @param amount the contraction length.
     */
    private void contractTail(final int amount) {
        checkNotNegative(amount);
        size -= Math.min(amount, size());
    }
    
    /**
     * Moves this slice <code>steps</code> to the left. If the head of this
     * slice, while moving to the left, leaves the beginning of the underlying
     * array, it reappears at the right end of the array.
     * 
     * @param steps the amount of steps to move.
     */
    private void moveLeft(int steps) {
        checkNotNegative(steps);
        
        if (array.length == 0) {
            return;
        }

        fromIndex -= steps % array.length;

        if (fromIndex < 0) {
            fromIndex += array.length;
        }
    }

    /**
     * Moves this slice <code>steps</code> amount of steps to the right. If the 
     * tail of this slice, while moving to the right, leaves the tail of the
     * underlying array, it reappears at the beginning of the array.
     * 
     * @param steps the amount of steps to move.
     */
    private void moveRight(final int steps) {
        checkNotNegative(steps);
        
        if (array.length == 0) {
            return;
        }

        fromIndex += steps % array.length;

        if (fromIndex >= array.length) {
            fromIndex -= array.length;
        }
    }
    
    /**
     * Cycles the array range covered by this slice <code>steps</code> steps to
     * the left.
     * 
     * @param steps the amount of steps to cycle.
     */
    private void rotateLeft(int steps) {
        if (size() < 2) {
            // Trivially cycled.
            return;
        }

        final int actualSteps = steps % size();

        if (actualSteps == 0) {
            return;
        }

        if (actualSteps <= size() - actualSteps) {
            rotateLeftImpl(actualSteps);
        } else {
            rotateRightImpl(size() - actualSteps);
        }
    }

    /**
     * Cycles the array range covered by this slice <code>steps</code> steps to
     * the right.
     * 
     * @param steps the amount of steps to cycle.
     */
    private void rotateRight(int steps) {
        if (size() < 2) {
            // Trivially cycled.
            return;
        }

        final int actualSteps = steps % size();

        if (actualSteps == 0) {
            return;
        }

        if (actualSteps <= size() - actualSteps) {
            rotateRightImpl(actualSteps);
        } else {
            rotateLeftImpl(size() - actualSteps);
        }
    }
    
    /**
     * Implements the rotation of a slice to the left.
     * 
     * @param steps the amount of steps.
     */
    private void rotateLeftImpl(int steps) {
        checkNotNegative(steps);
        final Object[] buffer = new Object[steps];

        int index = 0;

        // Load the buffer.
        for (; index < steps; ++index) {
            buffer[index] = get(index);
        }

        // Rotate.
        for (int j = 0; index < size; ++index, ++j) {
            set(j, get(index));
        }

        index -= steps;

        // Dump the buffer.
        for (int j = 0; index < size; ++index, ++j) {
            set(index, (E) buffer[j]);
        }
    }

    /**
     * Implements the rotation of a slice to the right.
     * 
     * @param steps the amount of steps.
     */
    private void rotateRightImpl(int steps) {
        checkNotNegative(steps);
        final Object[] buffer = new Object[steps];

        // Load the buffer.
        for (int i = 0, j = size - steps; i < steps; ++i, ++j) {
            buffer[i] = get(j);
        }

        // Rotate.
        for (int i = size - steps - 1; i >= 0; --i) {
            set(i + steps, get(i));
        }

        // Dump the buffer.
        for (int i = 0; i < buffer.length; ++i) {
            set(i, (E) buffer[i]);
        }
    }
    
    /**
     * Checks that <code>number</code> is not negative.
     * 
     * @param number the number to check.
     */
    private static void checkNotNegative(final int number) {
        if (number < 0) {
            throw new IllegalArgumentException(
                    "The input number is negative: " + number);
        }
    }

    /**
     * This class implements an iterator over this slice's array components.
     */
    private class SliceIterator implements Iterator<E> {

        /**
         * The index of the next slice component to return.
         */
        private int index;

        /**
         * The number of components yet to iterate.
         */
        private int toIterateLeft;

        /**
         * Constructs a new slice iterator.
         */
        SliceIterator() {
            toIterateLeft = Slice.this.size;
        }

        /**
         * Returns <code>true</code> if there is components yet to iterate.
         * 
         * @return a boolean value.
         */
        @Override
        public boolean hasNext() {
            return toIterateLeft > 0;
        }

        /**
         * Returns the next slice component.
         * 
         * @return a component.
         */
        @Override
        public E next() {
            if (toIterateLeft == 0) {
                throw new NoSuchElementException("Iterator exceeded.");
            }

            --toIterateLeft;
            return Slice.this.get(index++);
        }
    }
}
