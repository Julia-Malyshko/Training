package service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import atg.nucleus.GenericService;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import util.CSVUtils;
import util.CommonUtils;
import util.IConstants;

public class ExportToCSV extends GenericService implements IConstants {
	private Repository mRepository;
	private String mItemDescriptorName;

	/**
	 * columnNames - which of properties of item descriptor to export to csv
	 */
	private List<String> mColumnNames;

	private String mFilePath;
	private String mDelimeterCollection;
	private String mEmptyMapOrCollection;

	/**
	 * pageSize - number of rows in one csv file
	 */
	private int mPageSize;

	protected final RepositoryItemDescriptor getItemDescriptor() throws RepositoryException {
		return getRepository().getItemDescriptor(getItemDescriptorName());
	}

	protected RepositoryItem[] getALLItems() throws RepositoryException {
		final RepositoryItem[] resultQuery;
		final RepositoryView view = getRepository().getView(getItemDescriptor());
		final RqlStatement statement = RqlStatement.parseRqlStatement("ALL");
		final Object params[] = null;
		resultQuery = statement.executeQuery(view, params);
		return resultQuery;
	}

	protected String getItemValue(final RepositoryItem pItem) {
		final String itemValue;
		itemValue = pItem.getRepositoryId();
		return itemValue;
	}

	protected String getCollectionValues(final Collection<Object> pCollection) {
		final String collectionValue;
		if (pCollection.isEmpty()) {
			collectionValue = getEmptyMapOrCollection();
		} else {
			final StringBuffer sb = new StringBuffer("[");
			final Iterator<Object> iter = pCollection.iterator();
			Object next;
			while (iter.hasNext()) {
				next = iter.next();
				if (next instanceof RepositoryItem) {
					sb.append(getItemValue((RepositoryItem) next));
				} else {
					sb.append(next);
				}
				sb.append(getDelimeterCollection());
			}
			collectionValue = sb.replace(sb.length() - getDelimeterCollection().length(), sb.length(), "]").toString();
		}
		return collectionValue;
	}

	protected String getMapValue(final Map<String, Object> pMap) {
		final String mapValue;
		if (pMap.isEmpty()) {
			mapValue = getEmptyMapOrCollection();
		} else {
			final StringBuffer sb = new StringBuffer("[");
			Object value;
			String key;
			for (Entry<String, Object> pair : pMap.entrySet()) {
				key = pair.getKey();
				value = pair.getValue();
				sb.append(key + "=");
				if (value instanceof RepositoryItem) {
					sb.append(getItemValue((RepositoryItem) value));
				} else {
					sb.append(value);
				}
			}
			mapValue = sb.replace(sb.length() - getDelimeterCollection().length(), sb.length(), "]").toString();
		}
		return mapValue;
	}

	@SuppressWarnings("unchecked")
	protected String[] getValues(final RepositoryItem pItem) {
		final List<String> values = new LinkedList<String>();
		final String[] resultValues;
		Object value;
		String convertedValue;
		// TODO ask get()
		for (String property : getColumnNames()) {
			value = pItem.getPropertyValue(property);
			if (value instanceof Collection) {
				convertedValue = getCollectionValues((Collection<Object>) value);
			} else if (value instanceof Map) {
				convertedValue = getMapValue((Map<String, Object>) value);
			} else if (value instanceof RepositoryItem) {
				convertedValue = getItemValue((RepositoryItem) value);
			} else {
				convertedValue = String.valueOf(value);
			}
			values.add(convertedValue);
		}
		resultValues = CommonUtils.getListAsArray(values);
		return resultValues;
	}

	protected List<String[]> getData(final RepositoryItem[] pItems) {
		final List<String[]> data = new ArrayList<String[]>();
		String[] values;
		for (RepositoryItem item : pItems) {
			values = getValues(item);
			data.add(values);
		}
		vlogInfo("get data success");
		return data;
	}



	protected boolean notInProperties(final List<String> pPropNames) throws RepositoryException {
		final boolean notInProp;
		final String[] properties = getItemDescriptor().getPropertyNames();
		final List<String> propList = Arrays.asList(properties);
		notInProp = !propList.containsAll(pPropNames);
		return notInProp;
	}

	protected boolean validColumns() throws RepositoryException {
		final boolean valid;
		final List<String> columns = getColumnNames();
		if (columns == null || notInProperties(columns)) {
			valid = false;
		} else {
			valid = true;
		}
		return valid;
	}
	
	protected String generateFileName(final int startIndex, final int toIndex){
		final String filename;
		filename = getItemDescriptorName() + (startIndex + 1) + "-" + toIndex;
		return filename;
	}
	
	protected String generateWholeFilePath(final String filename){
		final String wholePath;
		wholePath = getFilePath() + filename + CSV_FILE;
		return wholePath;
	}
	
	protected String getWholeFilePath(final int startIndex, final int toIndex){
		final String filename = generateFileName(startIndex, toIndex);
		final String wholePath = generateWholeFilePath(filename);
		return wholePath;
	}


	protected void writeToCSV() throws Exception {
		if (validColumns()) {
			final RepositoryItem[] items = getALLItems();
			final List<String[]> data = getData(items);
			final String[] columnNames = CommonUtils.getListAsArray(getColumnNames());
			final int dataSize = data.size();
			String wholeFilePath;
			int toIndex;
			for (int i = 0; i < dataSize; i += getPageSize()) {
				toIndex = i + getPageSize();
				if (toIndex > dataSize) {
					toIndex = dataSize;
				}
				wholeFilePath = getWholeFilePath(i, toIndex);
				CSVUtils.writeData(columnNames, data.subList(i, toIndex), wholeFilePath);
			}
		} else {
			throw new Exception("INVALID COLUMN NAMES defined in .properties");
		}
	}

	public void exportToCSV() {
		try {
			writeToCSV();
		} catch (Exception exc) {
			vlogError("error export to csv :");
			vlogError(exc.getMessage());
		}
	}

	public final Repository getRepository() {
		return mRepository;
	}

	public final void setRepository(Repository pRepository) {
		mRepository = pRepository;
	}

	public final String getItemDescriptorName() {
		return mItemDescriptorName;
	}

	public final void setItemDescriptorName(String pItemDescriptorName) {
		mItemDescriptorName = pItemDescriptorName;
	}

	public final String getDelimeterCollection() {
		return mDelimeterCollection;
	}

	public final void setDelimeterCollection(String pDelimeterCollection) {
		mDelimeterCollection = pDelimeterCollection;
	}

	public String getEmptyMapOrCollection() {
		return mEmptyMapOrCollection;
	}

	public void setEmptyMapOrCollection(String pEmptyMapOrCollection) {
		mEmptyMapOrCollection = pEmptyMapOrCollection;
	}

	public String getFilePath() {
		return mFilePath;
	}

	public void setFilePath(String pFilePath) {
		mFilePath = pFilePath;
	}

	public final int getPageSize() {
		return mPageSize;
	}

	public final void setPageSize(int pPageSize) {
		mPageSize = pPageSize;
	}

	public final List<String> getColumnNames() {
		return mColumnNames;
	}

	public void setColumnNames(List<String> pColumnNames) {
		mColumnNames = pColumnNames;
	}
}
