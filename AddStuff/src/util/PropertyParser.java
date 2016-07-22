package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atg.core.util.StringUtils;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;

// TODO: Auto-generated Javadoc
/**
 * The Class PropertyParser.
 */
public class PropertyParser {
	/**
	 * Gets the list property.
	 *
	 * @param pPropValue
	 *            the prop value without {},[] and etc
	 * @param pDelimiter
	 *            the delimiter
	 * @return the list property
	 */
	public static List<Object> getListProperty(final Object pPropValue, final Object pDelimiter) {
		// string string
		final List<Object> list;
		final String[] elements;
		if (pPropValue instanceof String && pDelimiter instanceof String) {
			list = new ArrayList<Object>();
			elements = ((String) pPropValue).split(pDelimiter + "\\s");
			for (String element : elements) {
				if (!StringUtils.isEmpty(element)) {
					list.add(element);
				}
			}
		} else {
			list = null;
		}
		return list;
	}

	/**
	 * Gets the set property.
	 *
	 * @param pPropValue
	 *            the prop value - without {},[] and etc
	 * @param pDelimiter
	 *            the delimiter
	 * @return the sets the property
	 */
	public static Set<Object> getSetProperty(final String pPropValue, final String pDelimiter) {
		final Set<Object> set;
		final String[] elements;
		set = new HashSet<Object>();
		elements = pPropValue.split(pDelimiter + "\\s");
		for (String element : elements) {
			if (!StringUtils.isEmpty(element)) {
				set.add(element);
			}
		}
		return set;
	}

	/**
	 * Gets the map property.
	 *
	 * @param pPropValue
	 *            the prop value without {},[] and etc
	 * @param pDelimiter
	 *            the delimiter
	 * @return the map property
	 */
	public static Map<String, Object> getMapProperty(final String pPropValue, final String pDelimiter) {
		final Map<String, Object> map;
		final String[] pairs;
		String[] pair;
		map = new HashMap<String, Object>();
		pairs = pPropValue.split(pDelimiter + "\\s");
		for (String pairValue : pairs) {
			if (!StringUtils.isEmpty(pairValue)) {
				pair = pairValue.split("=");
				map.put(pair[0].trim(), pair[1].trim());
			}
		}
		return map;
	}

	/**
	 * Gets the repository item property.
	 *
	 * @param pItem
	 *            the item
	 * @param pItemId
	 *            the item id
	 * @return the repository item property
	 * @throws RepositoryException
	 *             the repository exception
	 */
	public static RepositoryItem getRepositoryItemProperty(final Object pPropValue, final String pItemId)
			throws RepositoryException {
		final RepositoryItem pItem = (RepositoryItem) pPropValue;
		final RepositoryItemDescriptor itemDescriptor = pItem.getItemDescriptor();
		final String itemDescriptorName = itemDescriptor.getItemDescriptorName();
		final Repository repository = pItem.getRepository();
		final RepositoryItem result = repository.getItem(pItemId, itemDescriptorName);
		return result;
	}

}
