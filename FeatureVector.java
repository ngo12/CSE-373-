/**
 * FeatureVector is a class for storing the results of
 * counting the occurrences of colors.
 * <p>
 * Unlike the hash table, where the information can be
 * almost anyplace with the array(s) (due to hashing), in the FeatureVector,
 * the colors are in their numerical order and each count
 * is in the array position for its color.
 * <p>
 * Besides storing the information, the class provides methods
 * for getting the information (getTheCounts) and for computing
 * the similarity between two vectors (cosineSimilarity).
 * @author Ryan Linden ID:1571298, Brandon Ngo ID:1462375
 */
public class FeatureVector {

	long[] colorCounts;
	int bitsPerPixel;
	int keySpaceSize;

	/**
	 * Constructor of a FeatureVector.
	 *
	 * This creates a FeatureVector instance with an array of the
	 * proper size to hold a count for every possible element in the key space.
	 *
	 * @param bpp	Bits per pixel. This controls the size of the vector.
	 * 				The keySpace Size is 2^k where k is bpp.
	 *
	 */
	public FeatureVector(int bpp) {
		keySpaceSize = 1 << bpp;
		colorCounts = new long[keySpaceSize];
	}

	/**
	 * Goes through all possible key values in order and counts the corresponding hash table entries.
	 * The counts are set in the class member array in order.
	 * @param ch The ColorHash object to be counted.
	 */
	public void getTheCounts(ColorHash ch) {
		for (int i = 0; i < ch.getTableSize(); i++) {
			if (ch.getHashTable()[i] != null){ // if entry is found in hash table, add it.
				ColorKey key = ch.getHashTable()[i].getKey();
				long value = ch.getHashTable()[i].getValue();
				colorCounts[key.hashCode()] = value;
			}
		}
	}

	/**
	 * Compares two FeatureVectors using cosine similarity.
	 * @param other The other FeatureVector to compare to this one.
	 * @return Returns the cosine similarity with a max value of 1 (1 means perfect similarity)
	 */
	public double cosineSimilarity(FeatureVector other) {
		double dotProductResult = dotProduct(this.colorCounts, other.colorCounts);
		double vectorMagnitudeProductResult = vectorMagnitudeProduct(this.colorCounts, other.colorCounts);

		return dotProductResult / vectorMagnitudeProductResult;
	}

	/**
	 * Compute the dot product of two vectors
	 * @param A The first vector for the dot product.
	 * @param B The second vector for the dot product.
	 * @return The dot product of vectors A and B.
	 * @throws IllegalArgumentException If trying to perform dot product on two vectors of different length
	 */
	private double dotProduct(long[] A, long[] B){
		if (A.length != B.length) {
			throw new IllegalArgumentException("Dot product vectors must be of same length");
		}
		double sum = 0;
		for (int i = 0; i < A.length; i++) {
			sum += (double)A[i] * (double)B[i];
		}
		return sum;
	}

	/**
	 * Compute the product of the magnitude of two vectors.
	 * @param A The first vector operand.
	 * @param B The second vector operand.
	 * @return Returns the product of the magnitude of two vectors.
	 */
	private double vectorMagnitudeProduct(long[] A, long[] B){
		double sumA = 0;
		double sumB = 0;

		for (int i = 0; i < A.length; i++) {
			sumA += Math.pow((double)A[i], 2.0);
			sumB += Math.pow((double)B[i], 2.0);
		}

		sumA = Math.sqrt(sumA);
		sumB = Math.sqrt(sumB);
		return sumA * sumB;
	}

}
