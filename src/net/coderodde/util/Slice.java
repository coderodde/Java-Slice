package net.coderodde.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * This utility class implements <b>cyclic</b> array slices. If you move the 
 * slice to the right long enough, the "head" of the slice will wrap around and
 * emerge at the beginning of the array being sliced. Same applies to movement
 * to the left.
 * 
 * @author Rodion "rodde" Efremov
 * @param <E> the actual array component type.
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
     * The size of this slice. 
     */
    private int size;
    
    /**
     * Constructs a slice representing the entire array.
     * 
     * @param array the array being sliced.
     */
    public Slice(final E[] array) {
        this(array, 0, array.length);
    }
    
    /**
     * Constructs a slice representing everything starting at index
     * <code>fromIndex</code>.
     * 
     * @param array     the array being sliced.
     * @param fromIndex the starting index.
     */
    public Slice(final E[] array, final int fromIndex) {
        this(array, fromIndex, array.length);
    }
    
    /**
     * Constructs a new slice for <code>array</code> starting at 
     * <code>fromIndex</code> and ending at <code>toIndex - 1</code>.
     * 
     * @param array     the array being sliced.
     * @param fromIndex the starting (inclusive) index.
     * @param toIndex   the ending (exclusive) index.
     */
    public Slice(final E[] array, 
                 final int fromIndex, 
                 final int toIndex) {
        checkArray(array);
        checkFromAndToIndices(fromIndex, toIndex, array.length);
        this.array = array;
        this.fromIndex = fromIndex;
        this.size = toIndex - fromIndex;
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
        checkIndex(index);
        return array[(fromIndex + index) % array.length];
    }
    
    /**
     * Sets a new value at slice index <code>index</code>.
     * 
     * @param index the target component index.
     * @param value the new value to set.
     */
    public void set(final int index, final E value) {
        checkIndex(index);
        array[(fromIndex + index) % array.length] = value;
    }
    
    /**
     * Moves this slice <code>steps</code> to the right. If the head of this
     * slice, while moving to the left, leaves the beginning of the underlying
     * array, it reappears at the right end of the array.
     * 
     * @param steps the amount of steps to move.
     */
    public void moveLeft(final int steps) {
        if (array.length == 0) {
            return;
        }
        
        fromIndex -= steps % array.length;
        
        if (fromIndex < 0) {
            fromIndex += array.length;
        }
    }
    
    /**
     * Moves this slice one step to the left.
     */
    public void moveLeft() {
        moveLeft(1);
    }
    
    /**
     * Moves this slice <code>steps</code> amount of steps to the right. If the 
     * tail of this slice, while moving to the right, leaves the tail of the
     * underlying array, it reappears at the beginning of the array.
     * 
     * @param steps the amount of steps to move.
     */
    public void moveRight(final int steps) {
        if (array.length == 0) {
            return;
        }
        
        fromIndex += steps % array.length;
        
        if (fromIndex >= array.length) {
            fromIndex -= array.length;
        }
    }
    
    /**
     * Moves this slice one step to the right.
     */
    public void moveRight() {
        moveRight(1);
    }
    
    /**
     * Expands the front of this slice by at <code>amount</code> array
     * components. This slice may "cycle" the same way as at motion to the left
     * or right.
     * 
     * @param amount the expansion length.
     */
    public void expandFront(final int amount) {
        checkNotNegative(amount);
        final int actualAmount = Math.min(amount, array.length - size());
        fromIndex -= actualAmount;
        size += actualAmount;
        
        if (fromIndex < 0) {
            fromIndex += array.length;
        }
    }
    
    /**
     * Expands the front of this slice by one array component.
     */
    public void expandFront() {
        expandFront(1);
    }
    
    /**
     * Contracts the front of this slice by at <code>amount</code> array
     * components.
     * 
     * @param amount the contraction length.
     */
    public void contractFront(final int amount) {
        checkNotNegative(amount);
        final int actualAmount = Math.min(amount, size());
        fromIndex += actualAmount;
        size -= actualAmount;
        
        if (fromIndex >= array.length) {
            fromIndex -= array.length;
        }
    }
    
    /**
     * Contracts the front of this slice by one array component.
     */
    public void contractFront() {
        contractFront(1);
    }
    
    /**
     * Expands the back of this slice by at <code>amount</code> array 
     * components.
     * 
     * @param amount the expansion length.
     */
    public void expandBack(final int amount) {
        checkNotNegative(amount);
        size += Math.min(amount, array.length - size());
    }
    
    /**
     * Expands the back of this slice by one array component.
     */
    public void expandBack() {
        expandBack(1);
    }
    
    /**
     * Contracts the back of this slice by <code>amount</code> array components.
     * 
     * @param amount the contraction length.
     */
    public void contractBack(final int amount) {
        checkNotNegative(amount);
        size -= Math.min(amount, size());
    }
    
    /**
     * Contracts the back of this slice by one array component.
     */
    public void contractBack() {
        contractBack(1);
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
     * Cycles the array range covered by this slice <code>steps</code> steps to
     * the left.
     * 
     * @param steps the amount of steps to cycle.
     */
    public void cycleLeft(final int steps) {
        if (size() < 2) {
            // Trivially cycled.
            return;
        }
        
        final int actualSteps = steps % size();
        
        if (actualSteps == 0) {
            return;
        }
        
        if (steps <= size() - steps) {
            cycleImplLeft(steps);
        } else {
            cycleImplRight(size() - steps);
        }
    }
    
    /**
     * Cycles the array range covered by this slice one step to the leftt.
     */
    public void cycleLeft() {
        cycleLeft(1);
    }
    
    /**
     * Cycles the array range covered by this slice <code>steps</code> steps to
     * the right.
     * 
     * @param steps the amount of steps to cycle.
     */
    public void cycleRight(final int steps) {
        if (size() < 2) {
            // Trivially cycled.
            return;
        }
        
        final int actualSteps = steps % size();
        
        if (actualSteps == 0) {
            return;
        }
        
        if (steps <= size() - steps) {
            cycleImplRight(steps);
        } else {
            cycleImplLeft(size() - steps);
        }
    }
    
    /**
     * Cycles the array range covered by this slice one step to the right.
     */
    public void cycleRight() {
        cycleRight(1);
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
     * Implements the rotation of a slice to the left.
     * 
     * @param steps the amount of steps.
     */
    private void cycleImplLeft(final int steps) {
        final Object[] buffer = new Object[steps];
        
        int index = 0;
        
        // Load the buffer.
        for (; index < steps; ++index) {
            buffer[index] = get(index);
        }
        
        for (int j = 0; index < size; ++index, ++j) {
            set(j, get(index));
        }
        
        index -= steps;
        
        for (int j = 0; index < size; ++index, ++j) {
            set(index, (E) buffer[j]);
        }
    }
    
    /**
     * Implements the rotation of a slice to the right.
     * 
     * @param steps the amount of steps.
     */
    private void cycleImplRight(final int steps) {
        final Object[] buffer = new Object[steps];
        
        for (int i = 0, j = size - steps; i < steps; ++i, ++j) {
            buffer[i] = get(j);
        }
        
        for (int i = size - steps - 1; i >= 0; --i) {
            set(i + steps, get(i));
        }
        
        for (int i = 0; i < buffer.length; ++i) {
            set(i, (E) buffer[i]);
        }
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
     * Checks the slice starting index.
     * 
     * @param fromIndex   the starting (inclusive) index of a slice.
     * @param arrayLength the length of an array.
     */
    private static void checkFromIndex(final int fromIndex, 
                                       final int arrayLength) {
        if (fromIndex < 0 || fromIndex >= arrayLength) {
            throw new IllegalArgumentException(
                    "Bad 'fromIndex': " + fromIndex + ", array length: " +
                    arrayLength);
        }
    }
    
    /**
     * Checks the ending (exclusive) index.
     * 
     * @param toIndex     the ending (exclusive) index of a slice.
     * @param arrayLength the length of an array.
     */
    private static void checkToIndex(final int toIndex, final int arrayLength) {
        if (toIndex < 0 || toIndex > arrayLength) {
            throw new IllegalArgumentException(
                    "Bad 'toIndex': " + toIndex + ", array length: " + 
                    arrayLength);
        }
    }
    
    /**
     * Checks both slice indices.
     * 
     * @param fromIndex   the starting (inclusive) index of a slice.
     * @param toIndex     the ending (exclusive) index of a slice.
     * @param arrayLength the length of an array.
     */
    private static void checkFromAndToIndices(final int fromIndex, 
                                              final int toIndex,
                                              final int arrayLength) {
        checkFromIndex(fromIndex, arrayLength);
        checkToIndex(toIndex, arrayLength);
        
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException(
                    "'fromIndex' (" + fromIndex + ") is larger than " +
                    "'toIndex' (" + toIndex + ").");
        }
    }
    
    /**
     * Checks the access indices.
     * 
     * @param index the index to check.
     */
    private void checkIndex(final int index) {
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
    
    /**
     * The entry point into a program.

     * @param args the command line arguments.
     */
    public static void main(final String... args) {
        final Character[] array = new Character[26];
        
        for (char c = 'A'; c <= 'Z'; ++c) {
            array[c - 'A'] = c;
        }
        
        final Slice<Character> slice = new Slice<>(array);
        final Scanner scanner = new Scanner(System.in);
        
        System.out.println(slice);
        
        while (scanner.hasNext()) {
            
            final String line = scanner.nextLine().trim().toLowerCase();
            final String[] parts = line.split("\\s+");
            
            if (parts.length == 0) {
                continue;
            }
            
            switch (parts[0]) {
                case "left":
                    if (parts.length > 1) {
                        int steps = Integer.parseInt(parts[1]);
                        slice.moveLeft(steps);
                    } else {
                        slice.moveLeft();
                    }
                    
                break;
                    
                case "right":
                    if (parts.length > 1) {
                        int steps = Integer.parseInt(parts[1]);
                        slice.moveRight(steps);
                    } else {
                        slice.moveRight();
                    }
                
                break;
                    
                case "exfront":
                    if (parts.length > 1) {
                        int steps = Integer.parseInt(parts[1]);
                        slice.expandFront(steps);
                    } else {
                        slice.expandFront();
                    }
                
                break;
                    
                case "exback":
                    if (parts.length > 1) {
                        int steps = Integer.parseInt(parts[1]);
                        slice.expandBack(steps);
                    } else {
                        slice.expandBack();
                    }
                
                break;
                    
                case "confront":
                    if (parts.length > 1) {
                        int steps = Integer.parseInt(parts[1]);
                        slice.contractFront(steps);
                    } else {
                        slice.contractFront();
                    }
                
                break;
                    
                case "conback":
                    if (parts.length > 1) {
                        int steps = Integer.parseInt(parts[1]);
                        slice.contractBack(steps);
                    } else {
                        slice.contractBack();
                    }
                
                break;
                    
                case "lcycle":
                    if (parts.length > 1) {
                        int steps = Integer.parseInt(parts[1]);
                        slice.cycleLeft(steps);
                    } else {
                        slice.cycleLeft();
                    }
                    
                break;
                    
                case "rcycle":
                    if (parts.length > 1) {
                        int steps = Integer.parseInt(parts[1]);
                        slice.cycleRight(steps);
                    } else {
                        slice.cycleRight();
                    }
                    
                break;
                    
                case "help":
                    printHelp();
                    break;
                    
                case "quit":
                    System.exit(0);
            }
            
            System.out.println(slice);
        }
    }
    
    private static void printHelp() {
        System.out.println(
                "----------------------------------------------\n" +
                "quit         - Quit the demonstration.\n" +
                "help         - Print this help list.\n" +
                "left [N]     - Move the slice to the left.\n" +
                "right [N]    - Move the slice to the right.\n" +
                "exfront [N]  - Expand the front.\n" + 
                "exback [N]   - Expand the back.\n" +
                "confront [N] - Contract the front.\n" +
                "conback [N]  - Contract the back.\n" +
                "lcycle [N]   - Cycle the slice to the left.\n" +
                "rcycle [N]   - Cycle the slice to the right.\n" +
                "----------------------------------------------\n");
    }
}
