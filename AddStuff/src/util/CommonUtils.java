package util;

import java.util.List;

public class CommonUtils {

	public static String[] getListAsArray(List<String> pList) {
		final String[] array;
		array = pList.toArray(new String[0]);
		return array;
	}

}
