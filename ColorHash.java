/**
 * TODO Describe class!!!
 * @author Ngo, Linden
 *
 */
public class ColorHash {

	// Constants
	static final String LINEAR_PROBING = "Linear Probing";
	static final String QUAD_PROBING = "Quadratic Probing";

	// Initialized in constructor
	private HashEntry[] hashTable;     // The hash table itself, made up of dictionaries {key, value}
	private String collisionMethod; // Chooses which method of probing we use to handle collisions
	private int currentSize;           // Number of elements currently in hash table
	private int bpp;                // bitsPerPixel
	private double rhLoadFactor;    // The rehash load factor

	/**
	 * Constructs a ColorHash object
	 *
	 * A ColorHash object contains information relevant to the hash table itself and provides methods to manipulate
	 * values in the table.
	 *
	 * @param tableSize The initial size of the hash table.
	 * @param bitsPerPixel The number of bits per pixel: one of 3, 6, 9, 12, 15, 18, 21, or 24.
	 * @param collisionResolutionMethod The type of probing to use upon collisions (Linear or Quadratic in our case)
	 * @param rehashLoadFactor The threshold that determines when to rehash the table (# elements / table size)
	 * @throws Exception If the resolution method is invalid
	 */
	public ColorHash(int tableSize, int bitsPerPixel, String collisionResolutionMethod, double rehashLoadFactor){

		// Initialize members
		hashTable = new HashEntry[tableSize];
		rhLoadFactor = rehashLoadFactor;
		bpp = bitsPerPixel;
		currentSize = 0;

		// Check for valid resolution method
		if (!collisionResolutionMethod.equals(LINEAR_PROBING) || !collisionResolutionMethod.equals(QUAD_PROBING)){
			// TODO throw exception
		}
		collisionMethod = collisionResolutionMethod;

	}

	/**
	 * Inner class that holds a member of the hash table. It is a pair of a key and corresponding value in one object
	 */
	class HashEntry {
		private ColorKey myKey;
		private long myValue;

		HashEntry(ColorKey key, long value){
			myKey = key;
			myValue = value;
		}

		ColorKey getKey(){
			return myKey;
		}

		long getValue(){
			return myValue;
		}
	}

//	// TODO finish implementing
//	private int[] probing(ColorKey key) {
//
//		int index = key.hashCode() % hashTable.length; // Get the first target index
//		int collisions = 0;
//		int offset = 1;
//
//		int[] ixAndColl = {index, collisions};
//
//		boolean keyFound = false; // True if we found a place to insert/update
//		while (!keyFound) {
//
//			// Get the current hashTable item to see if it is available
//			HashEntry currentHash = hashTable[index];
//
//			if (currentHash.equals(key) || currentHash == null){
//				keyFound = true;
//
//			} else {  // Otherwise we have a collision, and will probe for a new spot using specified collision method
//				if (collisionMethod.equals(LINEAR_PROBING)) {
//					index++;
//					if (index == getTableSize()) {
//						index = 0;
//					} // Wrap around array if needed
//				} else if (collisionMethod.equals(QUAD_PROBING)) {
//					index += offset;
//					offset += 2;
//					if (index >= getTableSize()) {
//						index -= getTableSize();
//					}
//				}
//				collisions++;
//			}
//		}
//		return ixAndColl;
//
//	}


//	/**
//	 * Inserts key into hash table with associated value
//	 * If entry already exists for key, overwrite the value
//	 * @param key The key to insert/update in hash table.
//	 * @param value The value associated with the key
//	 * @return Returns a ResponseItem that contains information about the task.
//	 */
//	public ResponseItem colorHashPut(ColorKey key, long value){
//
//
//		boolean didRehash = false;
//		boolean didUpdate = false;
//
//		int[] indexAndCollisions = probing(key);
//
//		int hashIndex = indexAndCollisions[0];
//		int nCollisions = indexAndCollisions[1];
//
//		if (hashTable[hashIndex] == null){
//			hashTable[hashIndex] = new HashEntry(key, value);
//			currentSize++;
//			return new ResponseItem(value, nCollisions, didRehash, didUpdate);
//		} else if (hashTable[hashIndex].equals(key)){
//			hashTable[hashIndex].myValue = value;
//			didUpdate = true;
//			return new ResponseItem(value, nCollisions, didRehash, didUpdate);
//		}
//
//
//
//		// Check if rehashing is necessary
//		didRehash = checkRehashing();
//
//		return new ResponseItem(value, nCollisions, didRehash, didUpdate);
//	}



	/**
	 * Inserts key into hash table with associated value
	 * If entry already exists for key, overwrite the value
	 * @param key The key to insert/update in hash table.
	 * @param value The value associated with the key
	 * @return Returns a ResponseItem that contains information about the task.
	 */
	public ResponseItem colorHashPut(ColorKey key, long value){


		int offset = 1; // Used for quad probing
		int nCollisions = 0;
		boolean didRehash = false;
		boolean didUpdate = false;

		int hashIndex = key.hashCode() % hashTable.length; // Get the first target index

		boolean keyFound = false; // True if we found a place to insert/update
		while (!keyFound){

			// Get the current hashTable item to see if it is available
			HashEntry currentHash = hashTable[hashIndex];

			if (currentHash == null) { // Nothing found at target index, insert entry
				hashTable[hashIndex] = new HashEntry(key, value);
				currentSize++;
				keyFound = true;

			} else if (currentHash.myKey.equals(key)) { // Duplicate key found, update value
				hashTable[hashIndex].myValue = value;
				didUpdate = true;
				keyFound = true;

			} else {  // Otherwise we have a collision, and will probe for a new spot using specified collision method
				if (collisionMethod.equals(LINEAR_PROBING)){
					hashIndex++;
					if (hashIndex == getTableSize()){hashIndex = 0;} // Wrap around array if needed
				} else if (collisionMethod.equals(QUAD_PROBING)){
					hashIndex += offset;
					offset += 2;
					while(hashIndex >= getTableSize()){
						hashIndex -= getTableSize();
					}
				}
				nCollisions++;
			}
		}

		// Check if rehashing is necessary
		didRehash = checkRehashing();

		return new ResponseItem(value, nCollisions, didRehash, didUpdate);
	}

	// * make sure find portion of operation only is performed once
	// * this cuts # of collisions to half the number that would be required if performed with
	// * separate get/put operations
	// The returned ResponseItem should contain the number of collisions involved in the initial find operation.
	/**
	 * Increment value of a key if it already exists. If it doesn't exist insert it and store a value 1 with it.
	 * @param key The key to increment or insert.
	 * @return Return a ResponseItem with the relevant procedural info.
	 */
	public ResponseItem increment(ColorKey key){

		long value = 1;
		int offset = 1; // Used for quad probing
		int nCollisions = 0;
		boolean didRehash = false;
		boolean didUpdate = false;

		int hashIndex = key.hashCode() % hashTable.length; // Get the first target index

		boolean keyFound = false; // True if we found a place to insert/update
		while (!keyFound){

			// Get the current hashTable item to see if it is available
			HashEntry currentHash = hashTable[hashIndex];

			if (currentHash == null) { // Nothing found at target index, insert entry
				hashTable[hashIndex] = new HashEntry(key, value);
				currentSize++;
				keyFound = true;

			} else if (currentHash.myKey.equals(key)) { // Duplicate key found, increment value
				value = hashTable[hashIndex].myValue += 1;
				didUpdate = true;
				keyFound = true;

			} else {  // Otherwise we have a collision, and will probe for a new spot using specified collision method
				if (collisionMethod.equals(LINEAR_PROBING)){
					hashIndex++;
					if (hashIndex == getTableSize()){hashIndex = 0;} // Wrap around array if needed
				} else if (collisionMethod.equals(QUAD_PROBING)){
					hashIndex += offset;
					offset += 2;
					while(hashIndex >= getTableSize()){
						hashIndex -= getTableSize();
					}
				}
				nCollisions++;
			}
		}

		// Check if rehashing is necessary
		didRehash = checkRehashing();

		return new ResponseItem(value, nCollisions, didRehash, didUpdate);
	}

	/**
	 * Look up a specified key and return the value in a ResponseItem format.
	 * @param key The key to look up.
	 * @return Returns a ResponseItem that contains the associated value.
	 * @throws MissingColorKeyException If the key does not exist
	 */
	public ResponseItem colorHashGet(ColorKey key)  throws Exception{

		long value = -1L;
		int offset = 1; // Used for quad probing
		int nCollisions = 0;
		boolean didRehash = false;
		boolean didUpdate = false;

		int hashIndex = key.hashCode() % hashTable.length; // Get the first target index

		boolean keyFound = false; // True if we found a place to insert/update
		while (!keyFound){

			// Get the current hashTable item to see if it is available
			HashEntry currentHash = hashTable[hashIndex];

			if (currentHash == null) { // Nothing found at target index, throw exception
				throw new MissingColorKeyException("Key not found");

			} else if (currentHash.myKey.equals(key)) { // Key found, get value
				value = hashTable[hashIndex].myValue;
				keyFound = true;

			} else {  // Otherwise we have a collision, and will probe for a new spot using specified collision method
				if (collisionMethod.equals(LINEAR_PROBING)){
					hashIndex++;
					if (hashIndex == getTableSize()){hashIndex = 0;} // Wrap around array if needed
				} else if (collisionMethod.equals(QUAD_PROBING)){
					hashIndex = nCollisions^2 + key.hashCode() % hashTable.length;
//					int newIndex += offset;
//					offset += 2;
					while(hashIndex >= getTableSize()){
						hashIndex -= getTableSize();
					}
				}
				nCollisions++;
			}
		}

		return new ResponseItem(value, nCollisions, didRehash, didUpdate);

	}

	/**
	 * Looks for a specified key in the table and returns its associated value.
	 * @param key The key to look for.
	 * @return Returns the value associated with the key, if non is found return 0.
	 */
	public long getCount(ColorKey key){

		long value = -1L;
		int offset = 1; // Used for quad probing

		int hashIndex = key.hashCode() % hashTable.length; // Get the first target index

		boolean keyFound = false; // True if we found a place to insert/update
		while (!keyFound){

			// Get the current hashTable item to see if it is available
			HashEntry currentHash = hashTable[hashIndex];

			if (currentHash == null) { // Nothing found at target index, return 0
				value = 0L;
				keyFound = true;

			} else if (currentHash.myKey.equals(key)) { // Key found, get value
				value = hashTable[hashIndex].myValue;
				keyFound = true;

			} else {  // Otherwise we have a collision, and will probe for a new spot using specified collision method
				if (collisionMethod.equals(LINEAR_PROBING)){
					hashIndex++;
					if (hashIndex == getTableSize()){hashIndex = 0;} // Wrap around array if needed
				} else if (collisionMethod.equals(QUAD_PROBING)){
					hashIndex += offset;
					offset += 2;
					while(hashIndex >= getTableSize()){
						hashIndex -= getTableSize();
					}
				}
			}
		}

		return value;
	}

	// TODO Is it okay to use ints for the currentSize, etc.
	// TODO throw exception if index is null? what about out of index range?
	/**
	 * Gets the Key at the specified index.
	 * @param tableIndex The index of the hash table.
	 * @return Returns a key in the hash table.
	 */
	public ColorKey getKeyAt(int tableIndex){ return hashTable[tableIndex].myKey; }

	// TODO throw exception or return 0 if no value exists
	/**
	 * Gets the value stored at the index location specified.
	 * @param tableIndex The index of the hash table value to return.
	 * @return Returns the value specified by the index.
	 */
	public long getValueAt(int tableIndex){
		if (hashTable[tableIndex] == null){
			return 0L;
		} else {
			return hashTable[tableIndex].myValue;
		}
	}

	/**
	 * Gets the current value of the hash table load factor.
	 * @return Returns the current load factor.
	 */
	public double getLoadFactor(){ return currentSize/hashTable.length; }

	/**
	 * Gets the current size of the hash table. Since resizing can happen this is not a constant.
	 * @return Returns the current size of the hash table, including empty spots.
	 */
	public int getTableSize(){ return hashTable.length; }

	/**
	 * Resizes the hash table to the next prime number that is at least double the old size.
	 */
	public void resize(){

		// new table size must be at least double the old size
		int newTableSize = getTableSize() * 2;
		// new table size must be a prime number
		while (!IsPrime.isPrime(newTableSize)){ newTableSize++; }

		// Create temps
		ColorHash tempCH = new ColorHash(newTableSize, bpp, collisionMethod, rhLoadFactor);
		HashEntry[] tempHT = tempCH.getHashTable();

		// TODO should i use alternate constructor from ColorKey.java?
		HashEntry currentHash;
		for (int i = 0; i < getTableSize(); i++) {
			currentHash = hashTable[i];
			if (currentHash != null){
				tempCH.colorHashPut(currentHash.getKey(), currentHash.getValue());
			}
		}
		hashTable = tempHT;
	}

	/**
	 * Gets the current hash table.
	 * @return Returns the hash table
	 */
	HashEntry[] getHashTable(){
		return hashTable;
	}


	// TODO Right now checking for rehashing AFTER each entry, but the assignment says to do so before inserting
	// TODO if we do so it should be getLoadFactor()+1
	/**
	 * Check if we are over the load factor and must rehash. If we are, then go ahead and rehash and set flag.
	 */
	private boolean checkRehashing(){
		if (getLoadFactor() >= rhLoadFactor) {
			resize();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * New Exception, called when trying to find a ColorKey that doesn't exist.
	 */
	class MissingColorKeyException extends Exception
	{
		public MissingColorKeyException() {}
		public MissingColorKeyException(String message)
		{
			super(message);
		}
	}

	//////////////////////////////////////////////////////////////////////////////

	/**
	 * Only for testing purposes
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ColorKey black = new ColorKey(0, 0, 0, 6);
			System.out.println("black's color key is: "+black);
			ColorKey white = new ColorKey(255, 255, 255, 6);
			System.out.println("white color key is: "+white);
			ColorKey green = new ColorKey(0, 255, 0, 6);
			System.out.println("green color key is: "+green);
			ColorKey other1 = new ColorKey(122, 255, 0, 6);
			System.out.println("other1 color key is: "+other1);
			ColorKey other2 = new ColorKey(255, 122, 0, 6);
			System.out.println("other2 color key is: "+other2);
			ColorKey other3 = new ColorKey(0, 255, 122, 6);
			System.out.println("other1 color key is: "+other3);
			ColorKey other4 = new ColorKey(0, 255, 255, 6);
			System.out.println("other2 color key is: "+other4);
			ColorKey number17 = new ColorKey(17, 6); // Call to the alternative constructor
			System.out.println("number17 color key is: "+number17);
//            ColorKey blah = new ColorKey(255, 255, 255, 10);
//            System.out.println("blah color key is: "+blah);
			ColorHash testCH = new ColorHash(3, 6, "Quadtrad", 0.9);
			testCH.increment(white);
			testCH.increment(black);
			testCH.increment(black);
			testCH.increment(green);
			testCH.increment(green);
			testCH.increment(green);
//			testCH.increment(other1);
//			testCH.increment(other2);
//			testCH.increment(other3);
//			testCH.increment(other4);
//			testCH.increment(number17);
//            System.out.println(testCH.getKeyAt(0));
			for (int i = 0; i < testCH.getTableSize(); i++) {
//                if (testCH.getKeyAt(i) != null) {
//                    System.out.println(testCH.getKeyAt(i));
//                }
				System.out.println(testCH.getValueAt(i));
			}
			System.out.println("TEST");
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}

}
