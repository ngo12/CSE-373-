/**
 * This class compares paintings in a variety of ways using the hash table (ColorHash)
 * and cosine similarity.
 * @author Ryan Linden ID:1571298, Brandon Ngo ID:1462375
 *
 */
public class ComparePaintings {

	// Constants
	private static final int BPP = 6;
	private static final int INITIAL_TABLE_SIZE = 3;
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

	/**
	 * Compute the similarity of two hash tables of color counts using cosine similarity
	 * @param painting1 The first painting to compare.
	 * @param painting2 The second painting to compare.
	 * @return Returns the cosine similarity value, with 1 being an optimal match.
	 */
	double compare(ColorHash painting1, ColorHash painting2) {
		FeatureVector fv1 = new FeatureVector(painting1.getBPP());
		FeatureVector fv2 = new FeatureVector(painting2.getBPP());
		fv1.getTheCounts(painting1);
		fv2.getTheCounts(painting2);
		return fv1.cosineSimilarity(fv2);
	}

	/**
	 * A basic test for the compare method: S(x,x) should be 1.0 if comparing an image with itself.
	 * @param filename The file to compare with.
	 * @throws IllegalArgumentException If countColors() has not been called and thus the feature vector is not populated
	 */
	void basicTest(String filename) {
		if (myFV == null) {
			throw new IllegalArgumentException("Must countColors() before calling this method");
		}
		ComparePaintings otherCP = new ComparePaintings();
		otherCP.countColors(filename, this.myCH.getBPP());
		System.out.print("Cosine Similarity = ");
		System.out.println(compare(myCH, otherCP.myCH));

//		if (myFV == null) {
//			throw new IllegalArgumentException("Must countColors() before calling this method");
//		}
//		ComparePaintings otherCP = new ComparePaintings();
//		myCH = otherCP.countColors(filename, BPP);
//		System.out.print("Cosine Similarity = ");
//		System.out.println(myFV.cosineSimilarity(otherCP.myFV));

	}
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
			System.out.format("%-20d", christinaCP.collisionSum);
			christinaCP.probingMethod = ColorHash.QUAD_PROBING;
			christinaCP.countColors("ChristinasWorld.jpg", bpp);
			System.out.format("%-22d%n", christinaCP.collisionSum);
		}
	}

	/**
	 * Compares similarity with the three original images
	 */
	void fullSimilarityTests(){
		ComparePaintings monaCP = new ComparePaintings();
		ComparePaintings starryCP = new ComparePaintings();
		ComparePaintings christinaCP = new ComparePaintings();

		System.out.println("Bits Per Pixel       S(Mona,Starry)    S(Mona,Christina)     S(Starry,Christina)");
		for (int bpp = 24; bpp >= 3; bpp-=3) {

			monaCP.countColors("MonaLisa.jpg", bpp);
			starryCP.countColors("StarryNight.jpg", bpp);
			christinaCP.countColors("ChristinasWorld.jpg", bpp);

			System.out.format("%-21d", bpp);
			System.out.format("%-18f", monaCP.myFV.cosineSimilarity(starryCP.myFV) );
			System.out.format("%-22f", monaCP.myFV.cosineSimilarity(christinaCP.myFV) );
			System.out.format("%-22f%n", starryCP.myFV.cosineSimilarity(christinaCP.myFV) );
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
	 * EXTRA CREDIT :
	 * Compares ten images and displays results in a table
	 */
	void tenImagesSimilarityTest(){

		int bpp = 6;
		ComparePaintings christinaCP = new ComparePaintings();
		ComparePaintings monaCP = new ComparePaintings();
		ComparePaintings starryCP = new ComparePaintings();
		ComparePaintings screamCP = new ComparePaintings();
		ComparePaintings sundayCP = new ComparePaintings();
		ComparePaintings butlerCP = new ComparePaintings();
		ComparePaintings nightCP = new ComparePaintings();
		ComparePaintings girlCP = new ComparePaintings();
		ComparePaintings dogsCP = new ComparePaintings();
		ComparePaintings persistenceCP = new ComparePaintings();
		christinaCP.countColors("ChristinasWorld.jpg", bpp);
		monaCP.countColors("MonaLisa.jpg", bpp);
		starryCP.countColors("StarryNight.jpg", bpp);
		screamCP.countColors("TheScream.jpg", bpp);
		sundayCP.countColors("SundayAfternoon.jpg", bpp);
		butlerCP.countColors("SingingButler.jpg", bpp);
		nightCP.countColors("Nighthawks.jpg", bpp);
		girlCP.countColors("GirlPearlEarring.jpg", bpp);
		dogsCP.countColors("DogsPlayingPoker.jpg", bpp);
		persistenceCP.countColors("Persistence.jpg", bpp);


		System.out.print(String.format("%-20s", ""));
		System.out.print(String.format("%-19s", "ChristinasWorld"));
		System.out.print(String.format("%-12s", "MonaLisa"));
		System.out.print(String.format("%-15s", "StarryNight"));
		System.out.print(String.format("%-13s", "TheScream"));
		System.out.print(String.format("%-19s", "SundayAfternoon"));
		System.out.print(String.format("%-17s", "SingingButler"));
		System.out.print(String.format("%-14s", "Nighthawks"));
		System.out.print(String.format("%-20s", "GirlPearlEarring"));
		System.out.print(String.format("%-20s", "DogsPlayingPoker"));
    	System.out.println(String.format("%-15s", "Persistence"));

		System.out.print(String.format("%-20s", "ChristinasWorld"));
		System.out.format("%-19f", christinaCP.myFV.cosineSimilarity(christinaCP.myFV));
		System.out.format("%-12f", christinaCP.myFV.cosineSimilarity(monaCP.myFV));
		System.out.format("%-15f", christinaCP.myFV.cosineSimilarity(starryCP.myFV));
		System.out.format("%-13f", christinaCP.myFV.cosineSimilarity(screamCP.myFV));
		System.out.format("%-19f", christinaCP.myFV.cosineSimilarity(sundayCP.myFV));
		System.out.format("%-17f", christinaCP.myFV.cosineSimilarity(butlerCP.myFV));
		System.out.format("%-14f", christinaCP.myFV.cosineSimilarity(nightCP.myFV));
		System.out.format("%-20f", christinaCP.myFV.cosineSimilarity(girlCP.myFV));
		System.out.format("%-20f", christinaCP.myFV.cosineSimilarity(dogsCP.myFV));
		System.out.format("%-15f%n", christinaCP.myFV.cosineSimilarity(persistenceCP.myFV));

		System.out.print(String.format("%-20s", "MonaLisa"));
		System.out.format("%-19f", monaCP.myFV.cosineSimilarity(christinaCP.myFV));
		System.out.format("%-12f", monaCP.myFV.cosineSimilarity(monaCP.myFV));
		System.out.format("%-15f", monaCP.myFV.cosineSimilarity(starryCP.myFV));
		System.out.format("%-13f", monaCP.myFV.cosineSimilarity(screamCP.myFV));
		System.out.format("%-19f", monaCP.myFV.cosineSimilarity(sundayCP.myFV));
		System.out.format("%-17f", monaCP.myFV.cosineSimilarity(butlerCP.myFV));
		System.out.format("%-14f", monaCP.myFV.cosineSimilarity(nightCP.myFV));
		System.out.format("%-20f", monaCP.myFV.cosineSimilarity(girlCP.myFV));
		System.out.format("%-20f", monaCP.myFV.cosineSimilarity(dogsCP.myFV));
		System.out.format("%-15f%n", monaCP.myFV.cosineSimilarity(persistenceCP.myFV));

		System.out.print(String.format("%-20s", "StarryNight"));
		System.out.format("%-19f", starryCP.myFV.cosineSimilarity(christinaCP.myFV));
		System.out.format("%-12f", starryCP.myFV.cosineSimilarity(monaCP.myFV));
		System.out.format("%-15f", starryCP.myFV.cosineSimilarity(starryCP.myFV));
		System.out.format("%-13f", starryCP.myFV.cosineSimilarity(screamCP.myFV));
		System.out.format("%-19f", starryCP.myFV.cosineSimilarity(sundayCP.myFV));
		System.out.format("%-17f", starryCP.myFV.cosineSimilarity(butlerCP.myFV));
		System.out.format("%-14f", starryCP.myFV.cosineSimilarity(nightCP.myFV));
		System.out.format("%-20f", starryCP.myFV.cosineSimilarity(girlCP.myFV));
		System.out.format("%-20f", starryCP.myFV.cosineSimilarity(dogsCP.myFV));
		System.out.format("%-15f%n", starryCP.myFV.cosineSimilarity(persistenceCP.myFV));

		System.out.print(String.format("%-20s", "TheScream"));
		System.out.format("%-19f", screamCP.myFV.cosineSimilarity(christinaCP.myFV));
		System.out.format("%-12f", screamCP.myFV.cosineSimilarity(monaCP.myFV));
		System.out.format("%-15f", screamCP.myFV.cosineSimilarity(starryCP.myFV));
		System.out.format("%-13f", screamCP.myFV.cosineSimilarity(screamCP.myFV));
		System.out.format("%-19f", screamCP.myFV.cosineSimilarity(sundayCP.myFV));
		System.out.format("%-17f", screamCP.myFV.cosineSimilarity(butlerCP.myFV));
		System.out.format("%-14f", screamCP.myFV.cosineSimilarity(nightCP.myFV));
		System.out.format("%-20f", screamCP.myFV.cosineSimilarity(girlCP.myFV));
		System.out.format("%-20f", screamCP.myFV.cosineSimilarity(dogsCP.myFV));
		System.out.format("%-15f%n", screamCP.myFV.cosineSimilarity(persistenceCP.myFV));

		System.out.print(String.format("%-20s", "SundayAfternoon"));
		System.out.format("%-19f", sundayCP.myFV.cosineSimilarity(christinaCP.myFV));
		System.out.format("%-12f", sundayCP.myFV.cosineSimilarity(monaCP.myFV));
		System.out.format("%-15f", sundayCP.myFV.cosineSimilarity(starryCP.myFV));
		System.out.format("%-13f", sundayCP.myFV.cosineSimilarity(screamCP.myFV));
		System.out.format("%-19f", sundayCP.myFV.cosineSimilarity(sundayCP.myFV));
		System.out.format("%-17f", sundayCP.myFV.cosineSimilarity(butlerCP.myFV));
		System.out.format("%-14f", sundayCP.myFV.cosineSimilarity(nightCP.myFV));
		System.out.format("%-20f", sundayCP.myFV.cosineSimilarity(girlCP.myFV));
		System.out.format("%-20f", sundayCP.myFV.cosineSimilarity(dogsCP.myFV));
		System.out.format("%-15f%n", sundayCP.myFV.cosineSimilarity(persistenceCP.myFV));

		System.out.print(String.format("%-20s", "SingingButler"));
		System.out.format("%-19f", butlerCP.myFV.cosineSimilarity(christinaCP.myFV));
		System.out.format("%-12f", butlerCP.myFV.cosineSimilarity(monaCP.myFV));
		System.out.format("%-15f", butlerCP.myFV.cosineSimilarity(starryCP.myFV));
		System.out.format("%-13f", butlerCP.myFV.cosineSimilarity(screamCP.myFV));
		System.out.format("%-19f", butlerCP.myFV.cosineSimilarity(sundayCP.myFV));
		System.out.format("%-17f", butlerCP.myFV.cosineSimilarity(butlerCP.myFV));
		System.out.format("%-14f", butlerCP.myFV.cosineSimilarity(nightCP.myFV));
		System.out.format("%-20f", butlerCP.myFV.cosineSimilarity(girlCP.myFV));
		System.out.format("%-20f", butlerCP.myFV.cosineSimilarity(dogsCP.myFV));
		System.out.format("%-15f%n", butlerCP.myFV.cosineSimilarity(persistenceCP.myFV));

		System.out.print(String.format("%-20s", "Nighthawks"));
		System.out.format("%-19f", nightCP.myFV.cosineSimilarity(christinaCP.myFV));
		System.out.format("%-12f", nightCP.myFV.cosineSimilarity(monaCP.myFV));
		System.out.format("%-15f", nightCP.myFV.cosineSimilarity(starryCP.myFV));
		System.out.format("%-13f", nightCP.myFV.cosineSimilarity(screamCP.myFV));
		System.out.format("%-19f", nightCP.myFV.cosineSimilarity(sundayCP.myFV));
		System.out.format("%-17f", nightCP.myFV.cosineSimilarity(butlerCP.myFV));
		System.out.format("%-14f", nightCP.myFV.cosineSimilarity(nightCP.myFV));
		System.out.format("%-20f", nightCP.myFV.cosineSimilarity(girlCP.myFV));
		System.out.format("%-20f", nightCP.myFV.cosineSimilarity(dogsCP.myFV));
		System.out.format("%-15f%n", nightCP.myFV.cosineSimilarity(persistenceCP.myFV));

		System.out.print(String.format("%-20s", "GirlPearlEarring"));
		System.out.format("%-19f", girlCP.myFV.cosineSimilarity(christinaCP.myFV));
		System.out.format("%-12f", girlCP.myFV.cosineSimilarity(monaCP.myFV));
		System.out.format("%-15f", girlCP.myFV.cosineSimilarity(starryCP.myFV));
		System.out.format("%-13f", girlCP.myFV.cosineSimilarity(screamCP.myFV));
		System.out.format("%-19f", girlCP.myFV.cosineSimilarity(sundayCP.myFV));
		System.out.format("%-17f", girlCP.myFV.cosineSimilarity(butlerCP.myFV));
		System.out.format("%-14f", girlCP.myFV.cosineSimilarity(nightCP.myFV));
		System.out.format("%-20f", girlCP.myFV.cosineSimilarity(girlCP.myFV));
		System.out.format("%-20f", girlCP.myFV.cosineSimilarity(dogsCP.myFV));
		System.out.format("%-15f%n", girlCP.myFV.cosineSimilarity(persistenceCP.myFV));

		System.out.print(String.format("%-20s", "DogsPlayingPoker"));
		System.out.format("%-19f", dogsCP.myFV.cosineSimilarity(christinaCP.myFV));
		System.out.format("%-12f", dogsCP.myFV.cosineSimilarity(monaCP.myFV));
		System.out.format("%-15f", dogsCP.myFV.cosineSimilarity(starryCP.myFV));
		System.out.format("%-13f", dogsCP.myFV.cosineSimilarity(screamCP.myFV));
		System.out.format("%-19f", dogsCP.myFV.cosineSimilarity(sundayCP.myFV));
		System.out.format("%-17f", dogsCP.myFV.cosineSimilarity(butlerCP.myFV));
		System.out.format("%-14f", dogsCP.myFV.cosineSimilarity(nightCP.myFV));
		System.out.format("%-20f", dogsCP.myFV.cosineSimilarity(girlCP.myFV));
		System.out.format("%-20f", dogsCP.myFV.cosineSimilarity(dogsCP.myFV));
		System.out.format("%-15f%n", dogsCP.myFV.cosineSimilarity(persistenceCP.myFV));

		System.out.print(String.format("%-20s", "Persistence"));
		System.out.format("%-19f", persistenceCP.myFV.cosineSimilarity(christinaCP.myFV));
		System.out.format("%-12f", persistenceCP.myFV.cosineSimilarity(monaCP.myFV));
		System.out.format("%-15f", persistenceCP.myFV.cosineSimilarity(starryCP.myFV));
		System.out.format("%-13f", persistenceCP.myFV.cosineSimilarity(screamCP.myFV));
		System.out.format("%-19f", persistenceCP.myFV.cosineSimilarity(sundayCP.myFV));
		System.out.format("%-17f", persistenceCP.myFV.cosineSimilarity(butlerCP.myFV));
		System.out.format("%-14f", persistenceCP.myFV.cosineSimilarity(nightCP.myFV));
		System.out.format("%-20f", persistenceCP.myFV.cosineSimilarity(girlCP.myFV));
		System.out.format("%-20f", persistenceCP.myFV.cosineSimilarity(dogsCP.myFV));
		System.out.format("%-15f%n", persistenceCP.myFV.cosineSimilarity(persistenceCP.myFV));
	}


	/**
	 * Uncomment the test you want to run
	 */
    public static void main(String[] args) {
        ComparePaintings cp = new ComparePaintings();
        cp.fullSimilarityTests();
//		cp.CollisionTests();
//		cp.tenImagesSimilarityTest();
    }


}
