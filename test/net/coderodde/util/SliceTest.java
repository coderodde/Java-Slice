package net.coderodde.util;

import java.util.Iterator;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class SliceTest {
    
    private static final int SIZE = 20;
    
    private Integer[] array;
    private Slice s;
    
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
        
        s = new Slice(array, SIZE - 2, 1); // size 3: 18, 19, 0
        is(s, SIZE - 2, SIZE - 1, 0);
        s.moveRight(2);
        is(s, 0, 1, 2);
        s.moveRight(5);
        is(s, 5, 6, 7);
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
        Slice s = new Slice(array, 5, 8);
        is(s, 5, 6, 7);
        s.expandFront(3);
        is(s, 2, 3, 4, 5, 6, 7);
        s.expandFront(4);
        is(s, SIZE - 2, SIZE - 1, 0, 1, 2, 3, 4, 5, 6 , 7);
    }

    @Test
    public void testExpandFront_0args() {
        Slice s = new Slice(array, 2, 4);
        is(s, 2, 3);
        s.expandFront();
        is(s, 1, 2, 3);
        s.expandFront();
        is(s, 0, 1, 2, 3);
        s.expandFront();
        is(s, SIZE - 1, 0, 1, 2, 3);
        s.expandFront();
        is(s, SIZE - 2, SIZE - 1, 0, 1, 2, 3);
        
        array = new Integer[]{ 0, 1, 2 };
        s = new Slice(array, 2, 1); // 2, 0
        is(s, 2, 0);
        assertEquals(2, s.size());
        assertFalse(s.isEmpty());
        
        s.expandFront();
        is(s, 1, 2, 0);
        assertEquals(3, s.size());
        assertFalse(s.isEmpty());
    }

    @Test
    public void testContractFront_int() {
        s = new Slice(array, 1, 5);
        is(s, 1, 2, 3, 4);
        s.contractFront();
        is(s, 2, 3, 4);
        s.contractFront();
        is(s, 3, 4);
        s.contractFront();
        is(s, 4);
        s.contractFront();
        is(s);
        assertTrue(s.isEmpty());
        assertEquals(0, s.size());
        s.contractFront();
        is(s);
        assertTrue(s.isEmpty());
        assertEquals(0, s.size());
    }

    @Test
    public void testContractFront_0args() {
        s = new Slice(array, 2, 6);
        is(s, 2, 3, 4, 5);
        s.contractFront();
        is(s, 3, 4, 5);
        s.contractFront();
        is(s, 4, 5);
        assertFalse(s.isEmpty());
        assertEquals(2, s.size());
        s.contractFront();
        is(s, 5);
        
        s.contractFront();
        is(s);
        assertTrue(s.isEmpty());
        assertEquals(0, s.size());
        
        s.contractFront();
        is(s);
        assertTrue(s.isEmpty());
        assertEquals(0, s.size());
    }

    @Test
    public void testExpandBack_int() {
        s = new Slice(array, SIZE - 3); // 17, 18, 19
        is(s, 17, 18, 19);
        s.expandBack(3);
        is(s, 17, 18, 19, 0, 1, 2);
        s.expandBack(2);
        is(s, 17, 18, 19, 0, 1, 2, 3, 4);
    }

    @Test
    public void testExpandBack_0args() {
        s = new Slice(array, SIZE - 2); // 18, 19
        is(s, 18, 19);
        s.expandBack();
        is(s, 18, 19, 0);
        s.expandBack();
        is(s, 18, 19, 0, 1);
        s.expandBack();
        is(s, 18, 19, 0, 1, 2);
        assertEquals(5, s.size());
        assertFalse(s.isEmpty());
    }

    @Test
    public void testContractBack_int() {
        s = new Slice(array, SIZE - 3, 3); // 17, 18, 19, 0, 1, 2
        assertEquals(6, s.size());
        is(s, 17, 18, 19, 0, 1, 2);
        s.contractBack(2);
        is(s, 17, 18, 19, 0);
        s.contractBack(3);
        is(s, 17);
        assertEquals(1, s.size());
        
        s.contractBack(10);
        assertTrue(s.isEmpty());
        assertEquals(0, s.size());
        
        s.contractBack(10);
        assertTrue(s.isEmpty());
        assertEquals(0, s.size());
    }

    @Test
    public void testContractBack_0args() {
        s = new Slice(array, 5, 10); // 5, 6, 7, 8, 9
        is(s, 5, 6, 7, 8, 9);
        s.contractBack();
        is(s, 5, 6, 7, 8);
        s.contractBack();
        is(s, 5, 6, 7);
        s.contractBack();
        is(s, 5, 6);
        s.contractBack();
        is(s, 5);
        s.contractBack();
        is(s);
        
        assertEquals(0, s.size());
        assertTrue(s.isEmpty());
        
        s.expandFront(3);
        is(s, 2, 3, 4);
    }

    @Test
    public void testReverse() {
        s = new Slice(array, 2, 7); // 2, 3, 4, 5, 6
        is(s, 2, 3, 4, 5, 6);
        s.reverse();
        is(s, 6, 5, 4, 3, 2);
        s.reverse();
        is(s, 2, 3, 4, 5, 6);
        s.contractFront();
        s.contractBack();
        is(s, 3, 4, 5);
        s.reverse();
        is(s, 5, 4, 3);
        s.expandFront();
        s.expandBack();
        is(s, 2, 5, 4, 3, 6);
    }

    @Test
    public void testCycleLeft_int() {
        s = new Slice(array, 5, 10);
        is(s, 5, 6, 7, 8, 9);
        s.cycleLeft(2);
        is(s, 7, 8, 9, 5, 6);
        s.cycleLeft(5);
        is(s, 7, 8, 9, 5, 6);
        s.cycleLeft(0);
        is(s, 7, 8, 9, 5, 6);
        s.cycleLeft(4);
        is(s, 6, 7, 8, 9, 5);
        
        s.contractFront();
        s.contractBack();
        
        is(s, 7, 8, 9);
        s.cycleLeft(2);
        is(s, 9, 7, 8);
        s.expandFront();
        s.expandBack();
        is(s, 6, 9, 7, 8, 5);
        
        s = new Slice(array, SIZE - 2, 2); // 18, 19, 0, 1
        is(s, 18, 19, 0, 1);
        s.cycleLeft(2);
        is(s, 0, 1, 18, 19);
        s.contractFront(2);
        is(s, 18, 19);
        
        s.cycleLeft(2);
        is(s, 18, 19);
        
        s.cycleLeft(1);
        is(s, 19, 18);
        s.contractBack();
        is(s, 19);
        s.cycleLeft(3);
        is(s, 19);
        s.contractBack();
        is(s);
        s.cycleLeft(2);
        s.cycleLeft(3);
        is(s);
        assertTrue(s.isEmpty());
        
        ////
        init();
        s = new Slice(array, 4, 9); // 4, 5, 6, 7, 8
        s.cycleLeft(5);
        is(s, 4, 5, 6, 7, 8);
        s.cycleLeft(7);
        is(s, 6, 7, 8, 4, 5);
        s.cycleLeft(3);
        is(s, 4, 5, 6, 7, 8);
        s.cycleLeft(1);
        is(s, 5, 6, 7, 8, 4);
        assertFalse(s.isEmpty());
        assertEquals(5, s.size());
    }

    @Test
    public void testCycleLeft_0args() {
        s = new Slice(array, 10, 14);
        is(s, 10, 11, 12, 13);
        s.cycleLeft();
        is(s, 11, 12, 13, 10);
        s.cycleLeft();
        is(s, 12, 13, 10, 11);
        s.cycleLeft();
        is(s, 13, 10, 11, 12);
        s.cycleLeft();
        is(s, 10, 11, 12, 13);
        s.cycleLeft();
        is(s, 11, 12, 13, 10);
    }

    @Test
    public void testCycleRight_int() {
        s = new Slice(array, 4, 9); // 4, 5, 6, 7, 8
        s.cycleRight(5);
        is(s, 4, 5, 6, 7, 8);
        s.cycleRight(7);
        is(s, 7, 8, 4, 5, 6);
        s.cycleRight(3);
        is(s, 4, 5, 6, 7, 8);
        s.cycleRight(1);
        is(s, 8, 4, 5, 6, 7);
        assertFalse(s.isEmpty());
        assertEquals(5, s.size());
    }

    @Test
    public void testCycleRight_0args() {
        s = new Slice(array, 5, 8); // 5, 6, 7
        is(s, 5, 6, 7);
        s.cycleRight();
        is(s, 7, 5, 6);
        s.cycleRight();
        is(s, 6, 7, 5);
        s.cycleRight();
        is(s, 5, 6, 7);
        s.cycleRight();
        is(s, 7, 5, 6);
        s.contractBack(10);
        is(s);
        s.cycleRight();
        is(s);
        assertTrue(s.isEmpty());
    }
    
    public void is(final Slice s, final Integer... ints) {
        int index = 0;
        final Iterator<Integer> it = s.iterator();
        
        while (it.hasNext()) {
            assertEquals(ints[index++], it.next());
        }
        
        assertFalse(it.hasNext());
        assertEquals(ints.length, s.size());
        
        for (index = 0; index < s.size(); ++index) {
            assertEquals(ints[index], s.get(index));
        }
    }
}
