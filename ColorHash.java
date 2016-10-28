/**
 * ColorHash is a hash table for the ColorKey object.
 * @author Brandon Ngo, Ryan Linden
 *
 */
public class ColorHash {

	// Constants
	static final String LINEAR_PROBING = "Linear Probing";
	static final String QUAD_PROBING   = "Quadratic Probing";

	// Members
	private HashEntry[] hashTable;  // The hash table itself, made up of dictionaries {key, value}
	private String collisionMethod; // Chooses which method of probing we use to handle collisions
	private int currentSize;        // Number of elements currently in hash table
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
	public ColorHash(int tableSize, int bitsPerPixel, String collisionResolutionMethod, double rehashLoadFactor) throws Exception {

		// Initialize members
		hashTable       = new HashEntry[tableSize];
		rhLoadFactor    = rehashLoadFactor;
		bpp             = bitsPerPixel;
		currentSize     = 0;
		collisionMethod = collisionResolutionMethod;

		// Check for valid resolution method
		if (!collisionResolutionMethod.equals(LINEAR_PROBING) && !collisionResolutionMethod.equals(QUAD_PROBING)){
			throw new IllegalArgumentException("Only supports 'Linear Probing' or 'Quadratic Probing'.");
		}

		// Check for valid load factor
		if (collisionResolutionMethod.equals(LINEAR_PROBING)){
			if (rhLoadFactor >= 1 || rhLoadFactor <= 0){
				throw new InvalidLoadFactorException("Invalid Load Factor for Linear Probing");
			}
		} else {
			if (rhLoadFactor >= .5 || rhLoadFactor <= 0){
				throw new InvalidLoadFactorException("Invalid Load Factor for Quadratic Probing");
			}
		}
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

	/**
	 * Probes the hash table for the insert/update location of the ColorKey, counting collisions along the way.
	 * It does linear/quadratic probing based on the constructor input.
	 *
	 * @param key The key to insert/update in the hash table.
	 * @return An array of type int[size=2] where array[0] = the index of insert/update point
	 * and array[1] = the number of collisions during the probing.
	 */
	private int[] probing(ColorKey key){

		int nCollisions = 0;

		int[] indexAndCollisions = new int[2];

		int hashIndex = key.hashCode() % hashTable.length; // Get the first target index

		boolean keyFound = false; // True if we found a place to insert/update
		while (!keyFound){

			HashEntry currentHash = hashTable[hashIndex];

			if (currentHash == null || currentHash.myKey.equals(key)) { // Empty/duplicate spot found
				keyFound = true;
			} else {  // Otherwise we have a collision, and will probe for a new spot using specified collision method
				nCollisions++;
				if (collisionMethod.equals(LINEAR_PROBING)){
					hashIndex++;
					if (hashIndex == getTableSize()){hashIndex = 0;} // Wrap around array if needed
				} else if (collisionMethod.equals(QUAD_PROBING)){
					hashIndex = nCollisions * nCollisions + key.hashCode() % hashTable.length;
					while(hashIndex >= getTableSize()){
						hashIndex -= getTableSize();
					}
				}
			}
		}
		indexAndCollisions[0] = hashIndex;
		indexAndCollisions[1] = nCollisions;
		return indexAndCollisions;
	}

	/**
	 * Inserts key into hash table with associated value
	 * If entry already exists for key, overwrite the value
	 * @param key The key to insert/update in hash table.
	 * @param value The value associated with the key
	 * @return Returns a ResponseItem that contains information about the task.
	 */
	public ResponseItem colorHashPut(ColorKey key, long value){

		int nCollisions   = 0;
		boolean didRehash = false;
		boolean didUpdate = false;


		int[] ixAndCols = probing(key); // Get insert/update position and numCollisions
		int hashIndex = ixAndCols[0];
		nCollisions = ixAndCols[1];

		HashEntry currentHash = hashTable[hashIndex];

		if (currentHash == null) {  // Empty spot found
			didRehash = checkRehashing();
			if (didRehash){
				ixAndCols = probing(key);  // Probe in newly created hash table for a spot
				nCollisions += ixAndCols[1];  // Add collisions from probing new table
				nCollisions += rehashCollisions;
				hashIndex = ixAndCols[0];  // Save the newly found spot in the rehashed table
			}
			hashTable[hashIndex] = new HashEntry(key, value); // Insert the key
			currentSize++;

		} else if (currentHash.myKey.equals(key)) { // Duplicate key found, update value
			hashTable[hashIndex].myValue = value;
			didUpdate = true;
		}

		return new ResponseItem(value, nCollisions, didRehash, didUpdate);
	}

	/**
	 * Increment value of a key if it already exists. If it doesn't exist insert it and store a value 1 with it.
	 * @param key The key to increment or insert.
	 * @return Return a ResponseItem with the relevant procedural info.
	 */
	public ResponseItem increment(ColorKey key){

		long value        = 1;
		int nCollisions   = 0;
		boolean didRehash = false;
		boolean didUpdate = false;

		int[] ixAndCols = probing(key); // Get insert/update position and numCollisions
		int hashIndex = ixAndCols[0];
		nCollisions = ixAndCols[1];

		HashEntry currentHash = hashTable[hashIndex];

		if (currentHash == null) { // Empty spot found
			didRehash = checkRehashing();
			if (didRehash){
				ixAndCols = probing(key);
				nCollisions += rehashCollisions;
				nCollisions += ixAndCols[1];
				hashIndex = ixAndCols[0];
			}
			hashTable[hashIndex] = new HashEntry(key, value);
			currentSize++;

		} else if (currentHash.myKey.equals(key)) { // Duplicate key found, increment value
			value = hashTable[hashIndex].myValue += 1;
			didUpdate = true;
		}
		return new ResponseItem(value, nCollisions, didRehash, didUpdate);
	}

	/**
	 * Look up a specified key and return the value in a ResponseItem format.
	 * @param key The key to look up.
	 * @return Returns a ResponseItem that contains the associated value.
	 * @throws MissingColorKeyException If the key does not exist
	 */
	public ResponseItem colorHashGet(ColorKey key)  throws Exception{

		long value        = -1L;
		int nCollisions   = 0;
		boolean didRehash = false;
		boolean didUpdate = false;


		int[] ixAndCols = probing(key); // Get insert/update position and numCollisions
		int hashIndex = ixAndCols[0];
		nCollisions = ixAndCols[1];

		HashEntry currentHash = hashTable[hashIndex];

		if (currentHash == null) { // Empty spot
			throw new MissingColorKeyException("Key not found");
		} else if (currentHash.myKey.equals(key)) { // Key found, return the value
			value = hashTable[hashIndex].myValue;
		}

		return new ResponseItem(value, nCollisions, didRehash, didUpdate);

	}

	/**
	 * Looks for a specified key in the table and returns its associated value.
	 * @param key The key to look for.
	 * @return Returns the value associated with the key, if non is found return 0.
	 */
	public long getCount(ColorKey key){

		long value      = -1L;
		int nCollisions = 0;

		int[] ixAndCols = probing(key); // Get insert/update position and numCollisions
		int hashIndex = ixAndCols[0];
		nCollisions = ixAndCols[1];

		HashEntry currentHash = hashTable[hashIndex];

		if (currentHash == null) { // Key not found, so we return 0
			value = 0L;
		} else if (currentHash.myKey.equals(key)) { // Key found, return associated value
			value = hashTable[hashIndex].myValue;
		}

		return value;
	}

	// TODO what if the key is null?
	/**
	 * Gets the Key at the specified index.
	 * @param tableIndex The index of the hash table.
	 * @return Returns a key in the hash table.
	 * @throws IndexOutOfBoundsException If index is not in the table.
	 */
	public ColorKey getKeyAt(int tableIndex){
		if (tableIndex >= getTableSize() || tableIndex < 0){
			throw new IndexOutOfBoundsException();
		}
		return hashTable[tableIndex].myKey;
	}

	/**
	 * Gets the value stored at the index location.
	 * @param tableIndex The index of the hash table value to return.
	 * @return Returns the value specified by the index.
	 */
	public long getValueAt(int tableIndex){
		if (tableIndex >= getTableSize() || tableIndex < 0){
			throw new IndexOutOfBoundsException();
		}

		if (hashTable[tableIndex] == null){
			return -1L;
		} else {
			return hashTable[tableIndex].myValue;
		}
	}

	/**
	 * Updated with one more than current size
	 * Gets the current value of the hash table load factor.
	 * @return Returns the current load factor.
	 */
	public double getLoadFactor(){ return (currentSize + 1.0) /hashTable.length; }

	/**
	 * Gets the current size of the hash table. Since resizing can happen this is not a constant.
	 * @return Returns the current size of the hash table, including empty spots.
	 */
	public int getTableSize(){ return hashTable.length; }

	/**
	 * Resizes the hash table to the next prime number that is at least double the old size.
	 * It also counts the collisions during rehashing and saves it into a private variable.
	 */
	private int rehashCollisions = 0;
	public void resize(){

		rehashCollisions = 0;

		// new table size must be at least double the old size
		int newTableSize = getTableSize() * 2;
		// new table size must be a prime number
		while (!IsPrime.isPrime(newTableSize)){ newTableSize++; }

		// Create temps
		try{
			ColorHash tempCH = new ColorHash(newTableSize, bpp, collisionMethod, rhLoadFactor);
			HashEntry[] tempHT = tempCH.getHashTable();

			HashEntry currentHash;
			for (int i = 0; i < getTableSize(); i++) {
				currentHash = hashTable[i];
				if (currentHash != null){
					ResponseItem ri = tempCH.colorHashPut(currentHash.getKey(), currentHash.getValue());
					rehashCollisions += ri.nCollisions;
				}
			}
			hashTable = tempHT;
		} catch (Exception InvalidLoadFactor) {
			System.out.println(InvalidLoadFactor);
		}

	}

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
	 * Gets the current hash table.
	 * @return Returns the hash table
	 */
	HashEntry[] getHashTable(){
		return hashTable;
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

	/**
	 * New Exception, called when trying to use invalid Load Factor for Collision Probing .
	 */
	class InvalidLoadFactorException extends Exception
	{
		public InvalidLoadFactorException() {}
		public InvalidLoadFactorException(String message)
		{
			super(message);
		}
	}

}

