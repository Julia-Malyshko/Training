package util;

import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.List;

/**
 * The Class CommonUtils.
 */
public class CommonUtils {
	/**
	 * Gets the list as array.
	 * 
	 * @param <T>
	 * @param <T>
	 * 
	 * @param <T>
	 *
	 * @param pList
	 *            the list
	 * @return the list as array
	 */
	// public static <T> T[] getListAsArray(final List<T> pList) {
	// final T[] array;
	// if (pList == null) {
	// array = null;
	// } else {
	// final int size = pList.size();
	// array = pList.toArray((T[])new Object[size]);
	// }
	// return array;
	// }
	public static String[] getListAsArray(final List<String> pList) {
		final String[] array;
		if (pList == null) {
			array = null;
		} else {
			final int size = pList.size();
			array = pList.toArray(new String[size]);
		}
		return array;
	}

	/**
	 * For example : from string wrapped in [] to string without brackets.
	 *
	 * @param pValue
	 *            the value
	 * @return the string
	 */
	public static String withoutWrapping(final String pValue, final char pStartWrapping, final char pEndWrapping) {
		final String result;
		if (pValue == null) {
			result = null;
		} else {
			final int startSubstring = pValue.indexOf(pStartWrapping) + 1;
			final int endSubstring = pValue.lastIndexOf(pEndWrapping);
			result = pValue.substring(startSubstring, endSubstring);
		}
		return result;
	}
}
