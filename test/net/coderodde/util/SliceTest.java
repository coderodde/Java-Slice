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
    
    @Test(expected = IllegalArgumentException.class)
    public void testThrowsOnReversedIndices() {
        new Slice(array, 4, 3);
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
    }

    @Test
    public void testMoveLeft_0args() {
    }

    @Test
    public void testMoveRight_int() {
    }

    @Test
    public void testMoveRight_0args() {
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
            assertEquals(ints[index], it.next());
        }
        
        assertFalse(it.hasNext());
    }
}
