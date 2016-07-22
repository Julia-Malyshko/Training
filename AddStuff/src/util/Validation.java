package util;

import java.util.Arrays;
import java.util.List;

import atg.repository.RepositoryException;
import atg.repository.RepositoryItemDescriptor;

/**
 * The Class Validation.
 */
public class Validation {

	/**
	 * Valid properties checks if some properties is a subclass of item
	 * descriptor properties
	 *
	 * @param pPropNames
	 *            the prop names
	 * @param pItemPropNames
	 *            the item prop names
	 * @return true, if successful
	 * @throws RepositoryException
	 *             the repository exception
	 */
	public static boolean validProperties(final List<String> pPropNames, final List<String> pItemPropNames)
			throws RepositoryException {
		final boolean valid;
		if (pPropNames == null || pItemPropNames == null) {
			valid = false;
		} else {
			valid = pItemPropNames.containsAll(pPropNames);
		}
		return valid;
	}

	/**
	 * Valid columns checks if all of column names is properties of item
	 * descriptor
	 *
	 * @param pItemDescriptor
	 *            the item descriptor
	 * @param pColumns
	 *            the columns
	 * @return true, if successful
	 * @throws RepositoryException
	 *             the repository exception
	 */
	public static boolean validColumns(final RepositoryItemDescriptor pItemDescriptor, final List<String> pColumns)
			throws RepositoryException {
		final boolean valid;
		if (pColumns == null || pItemDescriptor == null) {
			valid = false;
		} else {
			final String[] properties = pItemDescriptor.getPropertyNames();
			final List<String> propList = Arrays.asList(properties);
			valid = Validation.validProperties(pColumns, propList);
		}
		return valid;
	}

}
