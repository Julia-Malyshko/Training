package util;

import java.util.Random;

public class RandomUtils {
	public static Random random = new Random();
	
	public static int randomUniform(int b, int a) {
		final int uniformNum;
		uniformNum = random.nextInt(b - a) + a;
		return uniformNum;
	}

}
