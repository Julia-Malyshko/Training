package util;

import java.util.Random;

/**
 * The Class RandomUtils.
 */
public class RandomUtils {
	
	/** The random. */
	public static Random random = new Random();

	/**
	 * Random uniform variable from [low, high].
	 *
	 * @param pLow
	 *            the low
	 * @param pHigh
	 *            the high
	 * @return the int
	 * @throws IllegalArgumentException
	 *             the illegal argument exception
	 */
	public static int randomUniform(final int pLow, final int pHigh) throws IllegalArgumentException {
		final int uniformNum;
		if (pLow > pHigh) {
			throw new IllegalArgumentException("illegal section bounds");
		}
		uniformNum = random.nextInt(pHigh - pLow) + pLow;
		return uniformNum;
	}
}
