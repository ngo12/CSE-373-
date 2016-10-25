import static org.junit.Assert.assertEquals;

import org.junit.Test;


/**
 * A few initial tests for ColorHash tables, using JUnit, version 4.
 * Note: This is NOT a complete test suite.  Right now it does not
 * test collision handling much or quadratic probing at all.
 * To "turn off" any test, put double slashes ( "//" ) in front of the
 * "@Test" that precedes that method.   For example... "//@Test  ".
 * Note that some of the methods mentioned here such as setDebugging and showWholeTable
 * are not required to be supported by the ColorHash class, but you might wish
 * to create methods to help you with debugging.
 *
 * @author Steve Tanimoto
 * @version 0.02.
 *
 * Oct. 24, 2016
 *
 */
public class ColorHashTest {
    ColorKey blackKey=null, whiteKey=null, redKey=null, greenKey=null, blueKey=null;
    ColorHash testHT;

    void makeKeys() {
        try {
            blackKey = new ColorKey(0, 15);
            whiteKey = new ColorKey(255, 255, 255, 15);
            redKey   = new ColorKey(255, 0, 0, 15);
            greenKey = new ColorKey(0, 255, 0, 15);
            blueKey  = new ColorKey(0, 0, 255, 15);
        }
        catch(Exception e) {}
    }

    @Test
    public void testPutAndGetAt() {
        makeKeys();
        // Instantiate ColorHash.
        testHT = new ColorHash(3, 6, "Linear Probing", 0.9);
        assertEquals(3, testHT.getTableSize());
        ResponseItem ri = testHT.colorHashPut(blackKey, 5);
        assertEquals(ri.nCollisions, 0);
        assertEquals(ri.didRehash, false);
        assertEquals(ri.didUpdate, false);
        ColorKey k = testHT.getKeyAt(0);
        assertEquals(k, blackKey);
        ri = testHT.colorHashPut(whiteKey, 5);
        ri = testHT.colorHashPut(redKey, 5);
        assertEquals(ri.didRehash, true);
        assertEquals(ri.didUpdate, false);
        ri = testHT.colorHashPut(redKey, 23);
        assertEquals(ri.didRehash, false);
        assertEquals(ri.didUpdate, true);
        ColorKey k2 = testHT.getKeyAt(6);
        assertEquals(k2, redKey);
        long v = testHT.getValueAt(6);
        assertEquals(v, 23);
    }
    @Test
    public void testPutAndGet() {
        makeKeys();
        // Instantiates ColorHash.
        testHT = new ColorHash(3, 6, "Linear Probing", 0.9);
        assertEquals(3, testHT.getTableSize());
        //testHT.setDebugging(true);
        System.out.println(testHT.colorHashPut(blackKey, 5));
        try {
            long v = testHT.colorHashGet(blackKey).value;
            assertEquals(v, 5);
            testHT.colorHashPut(whiteKey, 5);
            testHT.colorHashPut(redKey, 5);
            testHT.colorHashPut(redKey, 23);
            v = testHT.colorHashGet(redKey).value;
            assertEquals(v, 23);
        }
        catch(Exception e) {};
    }

    @Test
    public void testIncrementing() {
        makeKeys();
        // Instantiates ColorHash.
        testHT = new ColorHash(3, 6, "Linear Probing", 0.9);
        //assertEquals(3, testHT.getTableSize());
        //testHT.setDebugging(true);
        System.out.println(testHT.increment(blackKey));
        //System.out.println(testHT.showWholeTable());
        System.out.println(testHT.increment(whiteKey));
        //System.out.println(testHT.showWholeTable());
        System.out.println(testHT.increment(blackKey));
        //System.out.println(testHT.showWholeTable());
        System.out.println(testHT.increment(redKey));
        //System.out.println(testHT.showWholeTable());
    }

    @Test
    public void testIncrementAndGet() {
        makeKeys();
        testHT = new ColorHash(3, 6, "Linear Probing", 0.9);
        System.out.println(testHT.increment(redKey));
        System.out.println(testHT.increment(redKey));
        try {
            ResponseItem ri = testHT.colorHashGet(redKey);
            assertEquals(ri.value, 2);
            System.out.println(ri);
        }
        catch(Exception e){}

    }

}