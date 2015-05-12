package net.coderodde.util;

import java.util.Iterator;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class SliceTest {
    
    private static final int SIZE = 20;
    
    private Integer[] array;
    
    @Before
    public void init() {
        array = new Integer[SIZE];
        
        for (int i = 0; i < SIZE; ++i) {
            array[i] = i;
        }
    }
    
    @Test
    public void testIterator() {
        Slice s = new Slice(array);
        Iterator<Integer> it = s.iterator();
        
        for (int i = 0; i < array.length; ++i) {
            assertEquals(array[i], it.next());
        }
        
        assertFalse(it.hasNext());
        
        s = new Slice(array, array.length / 2);
        it = s.iterator();
        
        for (int i = array.length / 2; i < array.length; ++i) {
            assertEquals(array[i], it.next());
        }
        
        assertFalse(it.hasNext());
        
        s = new Slice(array, 2, array.length / 2);
        it = s.iterator();
        
        for (int i = 2; i < array.length / 2; ++i) {
            assertEquals(array[i], it.next());
        }
        
        assertFalse(it.hasNext());
        
        s = new Slice(array, 3, 3);
        it = s.iterator();
        assertFalse(it.hasNext());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testThrowsOnNegativeFromIndex() {
        new Slice(array, -1);
    }
    
    @Test
    public void testThrowsOnBadToIndex() {
        new Slice(array, 3, SIZE);
    }
    
    @Test(expected = NullPointerException.class)
    public void testThrowsOnNullArray() {
        new Slice(null);
    }

    @Test
    public void testIsEmpty() {
        assertTrue(new Slice(array, 3, 3).isEmpty());
        assertTrue(new Slice(array, 0, 0).isEmpty());
    }

    @Test
    public void testSize() {
        assertEquals(array.length, new Slice(array).size());
        assertEquals(3, new Slice(array, 3, 6).size());
        assertEquals(2, new Slice(array, 3, 5).size());
        assertEquals(1, new Slice(array, 3, 4).size());
        assertEquals(0, new Slice(array, 3, 3).size());
    }

    @Test
    public void testGet() {
        Slice s = new Slice(array, 3);
        
        assertEquals(3, s.get(0));
        assertEquals(4, s.get(1));
        assertEquals(5, s.get(2));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetThrowsOnSmallIndex() {
        new Slice(array).get(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetThrowsOnLargeIndex() {
        new Slice(array).get(array.length);
    }
    
    @Test
    public void testSet() {
        Slice s = new Slice(array, 5, 10);
        assertEquals(5, s.get(0));
        s.set(0, 15);
        assertEquals(15, s.get(0));
        assertEquals(9, s.get(4));
        s.set(4, 29);
        assertEquals(29, s.get(4));
        assertEquals(6, s.get(1));
    }

    @Test
    public void testMoveLeft_int() {
        Slice s = new Slice(array, 3, 6); // size 3
        is(s, 3, 4, 5);
        s.moveLeft(2);
        is(s, 1, 2, 3);
    }

    @Test
    public void testMoveLeft_0args() {
        Slice s = new Slice(array, 3, 6); // size 3
        is(s, 3, 4, 5);
        s.moveLeft();
        is(s, 2, 3, 4);
        s.moveLeft();
        is(s, 1, 2, 3);
        s.moveLeft();
        is(s, 0, 1, 2);
        s.moveLeft();
        is(s, SIZE - 1, 0, 1);
        s.moveLeft();
        is(s, SIZE - 2, SIZE - 1, 0);
    }

    @Test
    public void testMoveRight_int() {
        Slice s = new Slice(array, 2, 6); // size 4
        is(s, 2, 3, 4, 5);
        s.moveRight(3);
        is(s, 5, 6, 7, 8);
        s.moveRight(2);
        is(s, 7, 8, 9, 10);
        
        s = new Slice(array, SIZE - 2, 1); // size 4
    }

    @Test
    public void testMoveRight_0args() {
        Slice s = new Slice(array, 2, 6);
        is(s, 2, 3, 4, 5);
        s.moveRight();
        is(s, 3, 4, 5, 6);
        s.moveRight();
        is(s, 4, 5, 6, 7);
        
        s = new Slice(array, array.length - 3); // size 3
        is(s, SIZE - 3, SIZE - 2, SIZE - 1);
        s.moveRight();
        is(s, SIZE - 2, SIZE - 1, 0);
        s.moveRight();
        is(s, SIZE - 1, 0, 1);
        s.moveRight();
        is(s, 0, 1, 2);
        s.moveRight();
        is(s, 1, 2, 3);
    }

    @Test
    public void testExpandFront_int() {
    }

    @Test
    public void testExpandFront_0args() {
    }

    @Test
    public void testContractFront_int() {
    }

    @Test
    public void testContractFront_0args() {
    }

    @Test
    public void testExpandBack_int() {
    }

    @Test
    public void testExpandBack_0args() {
    }

    @Test
    public void testContractBack_int() {
    }

    @Test
    public void testContractBack_0args() {
    }

    @Test
    public void testReverse() {
    }

    @Test
    public void testCycleLeft_int() {
    }

    @Test
    public void testCycleLeft_0args() {
    }

    @Test
    public void testCycleRight_int() {
    }

    @Test
    public void testCycleRight_0args() {
    }
    
    public void is(final Slice s, final Integer... ints) {
        int index = 0;
        final Iterator<Integer> it = s.iterator();
        
        while (it.hasNext()) {
            assertEquals(ints[index++], it.next());
        }
        
        assertFalse(it.hasNext());
    }
}
