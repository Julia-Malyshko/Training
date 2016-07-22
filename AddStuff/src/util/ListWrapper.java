package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import atg.core.util.StringUtils;

public class ListWrapper implements PropertyWrapper, IConstants{

	@Override
	public List<Object> getProperty(Map<String, Object> pParams) {
		final String propValue = (String) pParams.get(PROPERTY_VALUE);
		final String delimiter = (String) pParams.get(COLLECTION_DELIMITER);
		final List<Object> list;
		final String[] elements;
		if (propValue instanceof String && delimiter instanceof String) {
			list = new ArrayList<Object>();
			elements = ((String) propValue).split(delimiter + "\\s");
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
}
