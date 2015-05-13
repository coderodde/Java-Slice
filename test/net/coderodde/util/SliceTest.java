package net.coderodde.util;

import java.util.Iterator;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static net.coderodde.util.Slice.create;

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
        s = create().withArray(array)
                    .all();
        
        Iterator<Integer> it = s.iterator();
        
        for (int i = 0; i < array.length; ++i) {
            assertEquals(array[i], it.next());
        }
        
        assertFalse(it.hasNext());
        
        s = create().withArray(array)
                    .startingFrom(array.length / 2)
                    .untilEnd(); // 10, 11, 12, ..., 19
        
        it = s.iterator();
        
        for (int i = array.length / 2; i < array.length; ++i) {
            assertEquals(array[i], it.next());
        }
        
        assertFalse(it.hasNext());
        
        s = create().withArray(array)
                    .startingFrom(2)
                    .until(array.length / 2); // 2, 3, 4, ..., 9
        
        it = s.iterator();
        
        for (int i = 2; i < array.length / 2; ++i) {
            assertEquals(array[i], it.next());
        }
        
        assertFalse(it.hasNext());
        
        s = create().withArray(array)
                    .startingFrom(3)
                    .until(3); // Empty slice.
        
        it = s.iterator();
        assertFalse(it.hasNext());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testThrowsOnNegativeFromIndex() {
        create().withArray(array)
                .startingFrom(-1)
                .untilEnd();
    }
    
    @Test
    public void testThrowsOnBadToIndex() {
        create().withArray(array)
                .startingFrom(3)
                .until(SIZE);
    }
    
    @Test(expected = NullPointerException.class)
    public void testThrowsOnNullArray() {
        create().withArray(null)
                .all();
    }

    @Test
    public void testIsEmpty() {
        s = create().withArray(array)
                    .startingFrom(3)
                    .until(3);
        
        assertTrue(s.isEmpty());
        
        s = create().withArray(array)
                    .startingFrom(0)
                    .until(0);
        
        assertTrue(s.isEmpty());
    }

    @Test
    public void testSize() {
        s = create().withArray(array).all();
        
        assertEquals(array.length, s.size());
        
        s = create().withArray(array)
                    .startingFrom(3)
                    .until(6);
        
        assertEquals(3, s.size());
        
        s = create().withArray(array)
                    .startingFrom(3)
                    .until(5);
        
        assertEquals(2, s.size());
        
        s = create().withArray(array)
                    .startingFrom(3)
                    .until(4);
        
        assertEquals(1, s.size());
        
        s = create().withArray(array)
                    .startingFrom(3)
                    .until(3);
        
        assertEquals(0, s.size());
        
        s = create().withArray(array)
                    .startingFrom(4)
                    .until(3);
        
        assertEquals(array.length - 1, s.size());
        
        s = create().withArray(array)
                    .startingFrom(4)
                    .until(2);
        
        assertEquals(array.length - 2, s.size());
        
        s = create().withArray(array)
                    .startingFrom(5)
                    .until(3);
        
        assertEquals(array.length - 2, s.size());
    }

    @Test
    public void testGet() {
        s = create().withArray(array)
                    .startingFrom(3)
                    .untilEnd();
        
        assertEquals(3, s.get(0));
        assertEquals(4, s.get(1));
        assertEquals(5, s.get(2));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetThrowsOnSmallIndex() {
        create().withArray(array)
                .all()
                .get(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetThrowsOnLargeIndex() {
        create().withArray(array)
                .all()
                .get(array.length);
    }
    
    @Test
    public void testSet() {
        s = create().withArray(array)
                    .startingFrom(5)
                    .until(10);       // 5, 6, 7, 8, 9
        
        assertEquals(5, s.get(0));
        s.set(0, 15);
        assertEquals(15, s.get(0));
        assertEquals(9, s.get(4));
        s.set(4, 29);
        assertEquals(29, s.get(4));
        assertEquals(6, s.get(1));
    }

    @Test
    public void testMoveLeft() {
        s = create().withArray(array)
                    .startingFrom(3)
                    .until(6); // 3, 4, 5
        
        is(s, 3, 4, 5);
        s.move(-2);
        is(s, 1, 2, 3);
        s.move(-3);
        is(s, 18, 19, 0);
        
        s = create().withArray(array)
                    .startingFrom(3)
                    .until(6); /// 3, 4, 5
        
        is(s, 3, 4, 5);
        s.move(-1);
        is(s, 2, 3, 4);
        s.move(-1);
        is(s, 1, 2, 3);
        s.move(-1);
        is(s, 0, 1, 2);
        s.move(-1);
        is(s, SIZE - 1, 0, 1);
        s.move(-1);
        is(s, SIZE - 2, SIZE - 1, 0);
    }

    @Test
    public void testMoveRight() {
        s = create().withArray(array)
                    .startingFrom(2)
                    .until(6); // 2, 3, 4, 5
        
        is(s, 2, 3, 4, 5);
        s.move(3);
        is(s, 5, 6, 7, 8);
        s.move(2);
        is(s, 7, 8, 9, 10);
        
        s = create().withArray(array)
                    .startingFrom(SIZE - 2)
                    .until(1); // 18, 19, 0
        
        is(s, SIZE - 2, SIZE - 1, 0);
        s.move(2);
        is(s, 0, 1, 2);
        s.move(5);
        is(s, 5, 6, 7);
        ////
        s = create().withArray(array)
                    .startingFrom(2)
                    .until(6); // 2, 3, 4, 5
        
        is(s, 2, 3, 4, 5);
        s.move(1);
        is(s, 3, 4, 5, 6);
        s.move(1);
        is(s, 4, 5, 6, 7);
        
        s = create().withArray(array)
                    .startingFrom(array.length - 3)
                    .untilEnd(); // 17, 18, 19
        
        is(s, SIZE - 3, SIZE - 2, SIZE - 1);
        s.move(1);
        is(s, SIZE - 2, SIZE - 1, 0);
        s.move(1);
        is(s, SIZE - 1, 0, 1);
        s.move(1);
        is(s, 0, 1, 2);
        s.move(1);
        is(s, 1, 2, 3);
    }

    @Test
    public void testShiftHeadPointerToLeft() {
        s = create().withArray(array)
                    .startingFrom(5)
                    .until(8); // 5, 6, 7
                
        is(s, 5, 6, 7);
        s.moveHeadPointer(-3);
        is(s, 2, 3, 4, 5, 6, 7);
        s.moveHeadPointer(-4);
        is(s, SIZE - 2, SIZE - 1, 0, 1, 2, 3, 4, 5, 6 , 7);
        
        ////

        s = create().withArray(array)
                    .startingFrom(2)
                    .until(4); // 2, 3
        
        is(s, 2, 3);
        s.moveHeadPointer(-1);
        is(s, 1, 2, 3);
        s.moveHeadPointer(-1);
        is(s, 0, 1, 2, 3);
        s.moveHeadPointer(-1);
        is(s, SIZE - 1, 0, 1, 2, 3);
        s.moveHeadPointer(-1);
        is(s, SIZE - 2, SIZE - 1, 0, 1, 2, 3);
        
        array = new Integer[]{ 0, 1, 2 };
        
        s = create().withArray(array)
                    .startingFrom(2)
                    .until(1); // 2, 0
        
        is(s, 2, 0);
        assertEquals(2, s.size());
        assertFalse(s.isEmpty());
        
        s.moveHeadPointer(-1);
        is(s, 1, 2, 0);
        assertEquals(3, s.size());
        assertFalse(s.isEmpty());
        
        init();
        
        s = create().withArray(array)
                    .startingFrom(array.length - 3)
                    .until(3); // 17, 18, 19, 0, 1, 2
        
        assertEquals(6, s.size());
        assertFalse(s.isEmpty());
        is(s, 17, 18, 19, 0, 1, 2);
        s.moveHeadPointer(-1);
        is(s, 16, 17, 18, 19, 0, 1, 2);
        
        ////
        
        s = create().withArray(array)
                    .startingFrom(1)
                    .until(5); // 1, 2, 3, 4
        
        is(s, 1, 2, 3, 4);
        s.moveHeadPointer(1);
        is(s, 2, 3, 4);
        s.moveHeadPointer(1);
        is(s, 3, 4);
        s.moveHeadPointer(1);
        is(s, 4);
        s.moveHeadPointer(1);
        is(s);
        assertTrue(s.isEmpty());
        assertEquals(0, s.size());
        s.moveHeadPointer(2);
        is(s);
        assertTrue(s.isEmpty());
        assertEquals(0, s.size());
        
        ////
        s = create().withArray(array)
                    .startingFrom(4)
                    .until(11); // 4, 5, 6, 7, 8, 9, 10
        is(s, 4, 5, 6, 7, 8, 9, 10);
        s.moveHeadPointer(3);
        is(s, 7, 8, 9, 10);
        s.moveHeadPointer(2);
        is(s, 9, 10);
        s.moveHeadPointer(40);
        is(s);
        
        assertTrue(s.isEmpty());
        assertEquals(0, s.size());
    }

    @Test
    public void testShiftHeadPointerToRight() {
        s = create().withArray(array)
                    .startingFrom(2)
                    .until(6); // 2, 3, 4, 5
        
        is(s, 2, 3, 4, 5);
        s.moveHeadPointer(1);
        is(s, 3, 4, 5);
        s.moveHeadPointer(1);
        is(s, 4, 5);
        assertFalse(s.isEmpty());
        assertEquals(2, s.size());
        s.moveHeadPointer(1);
        is(s, 5);
        
        s.moveHeadPointer(1);
        is(s);
        assertTrue(s.isEmpty());
        assertEquals(0, s.size());
        
        s.moveHeadPointer(1);
        is(s);
        assertTrue(s.isEmpty());
        assertEquals(0, s.size());
    }

    @Test
    public void testShiftTailPointerToRight() {
        s = create().withArray(array)
                    .startingFrom(array.length - 3)
                    .untilEnd(); // 17, 18, 19
        
        is(s, 17, 18, 19);
        s.moveTailPointer(3);
        is(s, 17, 18, 19, 0, 1, 2);
        s.moveTailPointer(2);
        is(s, 17, 18, 19, 0, 1, 2, 3, 4);
        
        s = create().withArray(array)
                    .startingFrom(array.length - 2)
                    .untilEnd(); // 18, 19
        
        is(s, 18, 19);
        s.moveTailPointer(1);
        is(s, 18, 19, 0);
        s.moveTailPointer(1);
        is(s, 18, 19, 0, 1);
        s.moveTailPointer(1);
        is(s, 18, 19, 0, 1, 2);
        assertEquals(5, s.size());
        assertFalse(s.isEmpty());
    }

    @Test
    public void testShiftTailPointerToLeft() {
        s = create().withArray(array)
                    .startingFrom(array.length - 3)
                    .until(3); // 17, 18, 19, 0, 1, 2
        
        assertEquals(6, s.size());
        is(s, 17, 18, 19, 0, 1, 2);
        s.moveTailPointer(-2);
        is(s, 17, 18, 19, 0);
        s.moveTailPointer(-3);
        is(s, 17);
        assertEquals(1, s.size());
        
        s.moveTailPointer(-10);
        assertTrue(s.isEmpty());
        assertEquals(0, s.size());
        
        s.moveTailPointer(-10);
        assertTrue(s.isEmpty());
        assertEquals(0, s.size());
        
        ////
        
        s = create().withArray(array)
                    .startingFrom(5)
                    .until(10); // 5, 6, 7, 8, 9
        
        is(s, 5, 6, 7, 8, 9);
        s.moveTailPointer(-1);
        is(s, 5, 6, 7, 8);
        s.moveTailPointer(-1);
        is(s, 5, 6, 7);
        s.moveTailPointer(-1);
        is(s, 5, 6);
        s.moveTailPointer(-1);
        is(s, 5);
        s.moveTailPointer(-1);
        is(s);
        
        assertEquals(0, s.size());
        assertTrue(s.isEmpty());
        
        s.moveHeadPointer(-3);
        is(s, 2, 3, 4);
    }

    @Test
    public void testReverse() {
        s = create().withArray(array)
                    .startingFrom(2)
                    .until(7); // 2, 3, 4, 5, 6
        
        is(s, 2, 3, 4, 5, 6);
        s.reverse();
        is(s, 6, 5, 4, 3, 2);
        s.reverse();
        is(s, 2, 3, 4, 5, 6);
        s.moveHeadPointer(1);
        s.moveTailPointer(-1); 
        // 3, 4, 5
        is(s, 3, 4, 5);
        s.reverse();
        is(s, 5, 4, 3);
        s.moveHeadPointer(-1);
        s.moveTailPointer(1);
        is(s, 2, 5, 4, 3, 6);
    }

    @Test
    public void testRotateLeft() {
        s = create().withArray(array)
                    .startingFrom(5)
                    .until(10); // 5, 6, 7, 8, 9
        
        is(s, 5, 6, 7, 8, 9);
        s.rotate(-2);
        is(s, 7, 8, 9, 5, 6);
        s.rotate(-5);
        is(s, 7, 8, 9, 5, 6);
        s.rotate(0);
        is(s, 7, 8, 9, 5, 6);
        s.rotate(-4);
        is(s, 6, 7, 8, 9, 5);
        
        s.moveHeadPointer(1);
        s.moveTailPointer(-1);
        
        is(s, 7, 8, 9);
        s.rotate(-2);
        is(s, 9, 7, 8);
        s.moveHeadPointer(-1);
        s.moveTailPointer(1);
        is(s, 6, 9, 7, 8, 5);
        
        s = create().withArray(array)
                    .startingFrom(array.length - 2)
                    .until(2); // 18, 19, 0, 1
        
        is(s, 18, 19, 0, 1);
        s.rotate(-2);
        is(s, 0, 1, 18, 19);
        s.moveHeadPointer(2);
        is(s, 18, 19);
        
        s.rotate(-2);
        is(s, 18, 19);
        
        s.rotate(-1);
        is(s, 19, 18);
        s.moveTailPointer(-1);
        is(s, 19);
        s.rotate(-3);
        is(s, 19);
        s.moveTailPointer(-1);
        is(s);
        s.rotate(-2);
        s.rotate(-3);
        is(s);
        assertTrue(s.isEmpty());
        
        ////
        init();
        ////
        
        s = create().withArray(array)
                    .startingFrom(4)
                    .until(9); // 4, 5, 6, 7, 8
        
        s.rotate(-5);
        is(s, 4, 5, 6, 7, 8);
        s.rotate(-7);
        is(s, 6, 7, 8, 4, 5);
        s.rotate(-3);
        is(s, 4, 5, 6, 7, 8);
        s.rotate(-1);
        is(s, 5, 6, 7, 8, 4);
        assertFalse(s.isEmpty());
        assertEquals(5, s.size());
        
        ////
        
        s = create().withArray(array)
                    .startingFrom(10)
                    .until(14); // 10, 11, 12, 13
        
        is(s, 10, 11, 12, 13);
        s.rotate(-1);
        is(s, 11, 12, 13, 10);
        s.rotate(-1);
        is(s, 12, 13, 10, 11);
        s.rotate(-1);
        is(s, 13, 10, 11, 12);
        s.rotate(-1);
        is(s, 10, 11, 12, 13);
        s.rotate(-1);
        is(s, 11, 12, 13, 10);
    }

    @Test
    public void testRotateRight() {
        s = create().withArray(array)
                    .startingFrom(4)
                    .until(9); // 4, 5, 6, 7, 8
        
        s.rotate(5);
        is(s, 4, 5, 6, 7, 8);
        s.rotate(7);
        is(s, 7, 8, 4, 5, 6);
        s.rotate(3);
        is(s, 4, 5, 6, 7, 8);
        s.rotate(1);
        is(s, 8, 4, 5, 6, 7);
        assertFalse(s.isEmpty());
        assertEquals(5, s.size());
        
        ////
        init();
        
        s = create().withArray(array)
                    .startingFrom(5)
                    .until(8); // 5, 6, 7
        
        is(s, 5, 6, 7);
        s.rotate(1);
        is(s, 7, 5, 6);
        s.rotate(1);
        is(s, 6, 7, 5);
        s.rotate(1);
        is(s, 5, 6, 7);
        s.rotate(1);
        is(s, 7, 5, 6);
        s.moveTailPointer(-10);
        is(s);
        s.rotate(1);
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
