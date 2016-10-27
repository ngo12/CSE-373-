/**
 * // TODO describe class!!
 * @author
 *
 */
public class ComparePaintings {

	// Constants
	private static final int BPP = 6;
	private static final int INITIAL_TABLE_SIZE = 11;
	private static final double REHASH_LOAD_FACTOR = 0.4;

	// Declare members
	private ColorHash myCH;
	private FeatureVector myFV;
	private long collisionSum;
	private String probingMethod;

	/**
	 * Constructor for ComparePaintings. Simply initializes relevant values.
	 */
	public ComparePaintings(){
		collisionSum = 0; // Used to sum complete number of collisions during a hashing process
		probingMethod = ColorHash.LINEAR_PROBING; // Set default to linear probing. This can be changed.
	}

	/**
	 * Loads the image, constructs the hash table, and counts the colors of the image.
	 * @param filename The image to operate on.
	 * @param bitsPerPixel Used to set our keyspace.
	 * @return Returns a constructed ColorHash object using the loaded image.
	 */
	ColorHash countColors(String filename, int bitsPerPixel) {

		ImageLoader il = new ImageLoader(filename);

		// Construct hash table and populate it while counting total collisions
		ResponseItem currentResponse;
		collisionSum = 0;
		try{
			myCH = new ColorHash(INITIAL_TABLE_SIZE, bitsPerPixel, probingMethod, REHASH_LOAD_FACTOR);
		}
		catch (Exception InvalidLoadFactor){
			System.out.println(InvalidLoadFactor);
		}
		for (int x = 0; x < il.getWidth(); x++) {
			for (int y = 0; y < il.getHeight(); y++) {
				currentResponse = myCH.increment(il.getColorKey(x, y, bitsPerPixel));
				collisionSum += currentResponse.nCollisions;
			}
		}

		// Count colors and organize them in the FeatureVector object.
		myFV = new FeatureVector(bitsPerPixel);
		myFV.getTheCounts(myCH);

		return myCH;
	}

	// TODO What should bpp be? what if bpp is different for each?
	/**
	 * Compute the similarity of two hash tables of color counts using cosine similarity
	 * @param painting1 The first painting to compare.
	 * @param painting2 The second painting to compare.
	 * @return Returns the cosine similarity value, with 1 being an optimal match.
	 */
	double compare(ColorHash painting1, ColorHash painting2) {
		FeatureVector fv1 = new FeatureVector(BPP);
		FeatureVector fv2 = new FeatureVector(BPP);
		fv1.getTheCounts(painting1);
		fv2.getTheCounts(painting2);
		return fv1.cosineSimilarity(fv2);
	}

	// TODO do i need to countColors here? or can we assume its been done? maybe use a flag?
	// TODO or is this supposed to be for any file or just the same one?
	/**
	 * A basic test for the compare method: S(x,x) should be 1.0 if comparing an image with itself.
	 * @param filename The file to compare with.
	 */
	void basicTest(String filename) {
		ComparePaintings otherCP = new ComparePaintings();
		otherCP.countColors(filename, BPP);
		System.out.print("Cosine Similarity = ");
		System.out.println(myFV.cosineSimilarity(otherCP.myFV));

	}

	// TODO Finish quad probing and uncomment it
	/**
	 * using the three painting images and their bits-per-pixel values, compute and print out a table of collision counts
	 */
	void CollisionTests() {
		ComparePaintings monaCP = new ComparePaintings();
		ComparePaintings starryCP = new ComparePaintings();
		ComparePaintings christinaCP = new ComparePaintings();

		// Print the table
		System.out.println("Bits Per Pixel   C(Mona,linear)  C(Mona,quadratic)  C(Starry,linear) "
				+ "C(Starry,quadratic) C(Christina,linear) C(Christina,quadratic)");
		for (int bpp = 24; bpp >= 3; bpp-=3) {
			System.out.format("%-17d", bpp);
			monaCP.probingMethod = ColorHash.LINEAR_PROBING;
			monaCP.countColors("MonaLisa.jpg", bpp);
			System.out.format("%-16d", monaCP.collisionSum);
			monaCP.probingMethod = ColorHash.QUAD_PROBING;
			monaCP.countColors("MonaLisa.jpg", bpp);
			System.out.format("%-19d", monaCP.collisionSum);
			starryCP.probingMethod = ColorHash.LINEAR_PROBING;
			starryCP.countColors("StarryNight.jpg", bpp);
			System.out.format("%-17d", starryCP.collisionSum);
			starryCP.probingMethod = ColorHash.QUAD_PROBING;
			starryCP.countColors("StarryNight.jpg", bpp);
			System.out.format("%-20d", starryCP.collisionSum);
			christinaCP.probingMethod = ColorHash.LINEAR_PROBING;
			christinaCP.countColors("ChristinasWorld.jpg", bpp);
			System.out.format("%-20d", starryCP.collisionSum);
			christinaCP.probingMethod = ColorHash.QUAD_PROBING;
			christinaCP.countColors("ChristinasWorld.jpg", bpp);
			System.out.format("%-22d%n", starryCP.collisionSum);
		}
	}

	/**
	 * Compares similarity with ... TODO not sure exactly how this test works
	 */
	void fullSimilarityTests(){
		ComparePaintings monaCP = new ComparePaintings();
		ComparePaintings starryCP = new ComparePaintings();
		ComparePaintings christinaCP = new ComparePaintings();

		System.out.println("Bits Per Pixel       S(Mona,Starry)    S(Mona,Christina)     S(Starry,Christina)");
		for (int bpp = 24; bpp >= 3; bpp-=3) {
			System.out.format("%-21d", bpp);

			monaCP.countColors("MonaLisa.jpg", bpp);
			System.out.format("%-18f", monaCP.myFV.cosineSimilarity(monaCP.myFV) );

			starryCP.countColors("StarryNight.jpg", bpp);
			System.out.format("%-22f", starryCP.myFV.cosineSimilarity(starryCP.myFV) );

			christinaCP.countColors("ChristinasWorld.jpg", bpp);
			System.out.format("%-22f%n", christinaCP.myFV.cosineSimilarity(christinaCP.myFV) );
		}

	}

	/**
	 * Checks that the images can be loaded so missing file/bad path exceptions do not arise
	 */
	void imageLoadingTest() {
		ImageLoader mona = new ImageLoader("MonaLisa.jpg");
		ImageLoader starry = new ImageLoader("StarryNight.jpg");
		ImageLoader christina = new ImageLoader("ChristinasWorld.jpg");
		System.out.println("It looks like we have successfully loaded all three test images.");
	}

	/**
	 * This is a basic testing function, and can be changed.
	 */

//    public static void main(String[] args) {
//        ComparePaintings cp = new ComparePaintings();
//        cp.countColors("MonaLisa.jpg", BPP);
//        cp.fullSimilarityTests();
//    }

	public static void main(String[] args) {
		ComparePaintings cp = new ComparePaintings();
		cp.CollisionTests();
	}

//	public static void main(String[] args) {
//		ComparePaintings cp = new ComparePaintings();
//		cp.imageLoadingTest();
//        cp.countColors("MonaLisa.jpg", BPP);
////		cp.countColors("ChristinasWorld.jpg", 6);
//		long sum = 0;
//		for (int i = 0; i < 37; i++) {
//			System.out.println(cp.myCH.getValueAt(i));
//            sum+=cp.myCH.getValueAt(i);
//		}
//		System.out.println(sum);
//		System.out.println("COLOR COUNTS:");
//		for (int i = 0; i < cp.myFV.colorCounts.length; i++) {
//			System.out.print("Color Count " + i + ": ");
//			System.out.println(cp.myFV.colorCounts[i]);
//		}
//
//		cp.basicTest("ChristinasWorld.jpg");
//
//	}

}
