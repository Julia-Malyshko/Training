package util;

import java.util.List;

public class CommonUtils {

	public static String[] getListAsArray(List<String> list) {
		final String[] array;
		array = list.toArray(new String[0]);
		return array;
	}

}
