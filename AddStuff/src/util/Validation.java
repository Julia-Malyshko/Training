package util;

import java.util.Arrays;
import java.util.List;

import atg.repository.RepositoryException;
import atg.repository.RepositoryItemDescriptor;

public class Validation {

	public static boolean validProperties(final List<String> pPropNames, final List<String> pItemPropNames)
			throws RepositoryException {
		final boolean valid;
		valid = pItemPropNames.containsAll(pPropNames);
		return valid;
	}
	

	public static boolean validColumns(final RepositoryItemDescriptor pItemDescriptor, final List<String> pColumns) throws RepositoryException {
		final boolean valid;
		final String[] properties = pItemDescriptor.getPropertyNames();
		final List<String> propList = Arrays.asList(properties);
		if (pColumns == null) {
			valid = false;
		} else {
			valid = Validation.validProperties(pColumns, propList);
		}
		return valid;
	}
	
}
