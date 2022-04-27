package thuonghth.utils;

import java.util.Random;

/**
 * @author thuonghth
 */

public class RandomGeneratorUtil {
	public static String generateActivateCode() {
		Random rnd = new Random();
		int number = rnd.nextInt(999999);

		// this will convert any number sequence into 6 character.
		return String.format("%06d", number);
	}
}
