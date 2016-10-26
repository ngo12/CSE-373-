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


	ColorKey key0 = null;
	ColorKey key13 = null;
	ColorKey key26 = null;
	ColorKey key39 = null;







	void makeKeys() {
		try {
			blackKey = new ColorKey(0, 15);
			whiteKey = new ColorKey(255, 255, 255, 15);
			redKey   = new ColorKey(255, 0, 0, 15);
			greenKey = new ColorKey(0, 255, 0, 15);
			blueKey  = new ColorKey(0, 0, 255, 15);
		}
		catch(Exception e) {}

		try {
			key0 = new ColorKey(0, 6);
			key13 = new ColorKey(13, 6);
			key26 = new ColorKey(26, 6);
			key39 = new ColorKey(39, 6);
		} catch (Exception e) {}
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

//	@Test
//	public void testPutAndGetAt_quad() {
//		makeKeys();
//		// Instantiate ColorHash.
//		testHT = new ColorHash(10, 6, "Quadratic Probing", 0.9);
//		assertEquals(10, testHT.getTableSize());
//
//
//		ResponseItem ri89 = testHT.colorHashPut(key89, 89);
//		assertEquals(ri89.nCollisions, 0);
//		assertEquals(ri89.didRehash, false);
//		assertEquals(ri89.didUpdate, false);
//
//		ResponseItem ri18 = testHT.colorHashPut(key18, 18);
//		assertEquals(ri18.nCollisions, 0);
//		assertEquals(ri18.didRehash, false);
//		assertEquals(ri18.didUpdate, false);
//
//		ResponseItem ri49 = testHT.colorHashPut(key49, 49);
//		assertEquals(ri49.nCollisions, 1);
//		assertEquals(ri49.didRehash, false);
//		assertEquals(ri49.didUpdate, false);
//
//		ResponseItem ri58 = testHT.colorHashPut(key58, 58);
//		assertEquals(ri58.nCollisions, 2);
//		assertEquals(ri58.didRehash, false);
//		assertEquals(ri58.didUpdate, false);
//
//		ResponseItem ri69 = testHT.colorHashPut(key69, 69);
//		assertEquals(ri69.nCollisions, 2);
//		assertEquals(ri69.didRehash, false);
//		assertEquals(ri69.didUpdate, false);
//
//		for (int i=0; i<testHT.getTableSize(); i++)
//			System.out.println(testHT.getKeyAt(i));
//

//		assertEquals(ri.nCollisions, 0);
//		assertEquals(ri.didRehash, false);
//		assertEquals(ri.didUpdate, false);
//		ColorKey k = testHT.getKeyAt(0);
//		assertEquals(k, blackKey);
//		ri = testHT.colorHashPut(whiteKey, 5);
//		ri = testHT.colorHashPut(redKey, 5);
//		assertEquals(ri.didRehash, true);
//		assertEquals(ri.didUpdate, false);
//		ri = testHT.colorHashPut(redKey, 23);
//		assertEquals(ri.didRehash, false);
//		assertEquals(ri.didUpdate, true);
//		ColorKey k2 = testHT.getKeyAt(6);
//		assertEquals(k2, redKey);
//		long v = testHT.getValueAt(6);
//		assertEquals(v, 23);
//	}

	@Test
	public void testPutAndGetAt_quad() {
		makeKeys();
		// Instantiate ColorHash.
		testHT = new ColorHash(13, 6, "Quadratic Probing", 0.9);
		assertEquals(13, testHT.getTableSize());


		ResponseItem ri89 = testHT.colorHashPut(key0, 0);
		assertEquals(ri89.nCollisions, 0);
		assertEquals(ri89.didRehash, false);
		assertEquals(ri89.didUpdate, false);

		ResponseItem ri18 = testHT.colorHashPut(key13, 13);
		assertEquals(ri18.nCollisions, 1);
		assertEquals(ri18.didRehash, false);
		assertEquals(ri18.didUpdate, false);

		ResponseItem ri49 = testHT.colorHashPut(key26, 26);
		assertEquals(ri49.nCollisions, 2);
		assertEquals(ri49.didRehash, false);
		assertEquals(ri49.didUpdate, false);

		ResponseItem ri58 = testHT.colorHashPut(key39, 39);
		assertEquals(ri58.nCollisions, 3);
		assertEquals(ri58.didRehash, false);
		assertEquals(ri58.didUpdate, false);


//		for (int i=0; i<testHT.getTableSize(); i++)
//			System.out.println(testHT.getKeyAt(i));


//		assertEquals(ri.nCollisions, 0);
//		assertEquals(ri.didRehash, false);
//		assertEquals(ri.didUpdate, false);
//		ColorKey k = testHT.getKeyAt(0);
//		assertEquals(k, blackKey);
//		ri = testHT.colorHashPut(whiteKey, 5);
//		ri = testHT.colorHashPut(redKey, 5);
//		assertEquals(ri.didRehash, true);
//		assertEquals(ri.didUpdate, false);
//		ri = testHT.colorHashPut(redKey, 23);
//		assertEquals(ri.didRehash, false);
//		assertEquals(ri.didUpdate, true);
//		ColorKey k2 = testHT.getKeyAt(6);
//		assertEquals(k2, redKey);
//		long v = testHT.getValueAt(6);
//		assertEquals(v, 23);
	}
}
