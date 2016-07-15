package service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import atg.nucleus.GenericService;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import util.CSVUtils;
import util.Validation;

public class ImportFromCSV extends GenericService {
	private Repository mRepository;
	private String mItemDescriptorName;

	private String mFilePath;
	private String mDelimeterCollection;
	private String mEmptyMapOrCollection;

	/**
	 * pageSize - number of rows to read from file
	 */
	private int mPageSize;
	//TODO lastUpdateDate

	protected void setItemProperty(final MutableRepositoryItem pItem, final String pPropName, final String pPropValue) {
		// TODO if Collection if Map else
		String s;
		final Object propValue = pItem.getPropertyValue(pPropName);
		if (propValue instanceof Collection) {
			// parse collection
			s = pPropValue;
		} else if (propValue instanceof Map) {
			// parse map
			s = pPropValue;
		} else {
			pItem.setPropertyValue(pPropName, pPropValue);
		}
	}

	protected void setProperties(MutableRepositoryItem pItem, final String[] pProperties, final String[] pPropNames)
			throws Exception {
		if (pProperties.length == pPropNames.length) {
			for (int i = 0; i < pProperties.length; i++) {
				setItemProperty(pItem, pPropNames[i], pProperties[i]);
			}
		} else {
			throw new Exception("invalid data in csv file");
		}

	}

	protected List<RepositoryItem> readBlock(final int fromLine, final String[] columnNames) throws Exception {
		final List<RepositoryItem> items = new ArrayList<RepositoryItem>();
		final List<String[]> records = CSVUtils.readCSV(getFilePath(), fromLine, getPageSize());
		final MutableRepository itemRepository = (MutableRepository) getRepository();
		MutableRepositoryItem item;
		// TODO transaction here
		for (final String[] record : records) {
			item = itemRepository.createItem(getItemDescriptorName());
			setProperties(item, record, columnNames);
			items.add(item);
		}
		return items;
	}

	protected List<RepositoryItem> getRecords() throws Exception {
		final List<RepositoryItem> items;
		int readFromLine;
		final String[] columnNames = CSVUtils.readCSVHeader(getFilePath());
		final RepositoryItemDescriptor itemDesc = getRepository().getItemDescriptor(getItemDescriptorName());
		List<RepositoryItem> blockItems;
		if (Validation.validColumns(itemDesc, Arrays.asList(columnNames))) {
			items = new ArrayList<RepositoryItem>();
			readFromLine = 1;
			while (!(blockItems = readBlock(readFromLine, columnNames)).isEmpty()) {
				items.addAll(blockItems);
				readFromLine += blockItems.size();
			}

		} else {
			throw new Exception("invalid csv header for this item descriptor");
		}
		return items;
	}

	public void importFromCSV() {
		try {
			final List<RepositoryItem> allRecords = getRecords();
			vlogInfo("" + allRecords.size());
		} catch (Exception exc) {
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

	public final String getFilePath() {
		return mFilePath;
	}

	public final void setFilePath(String pFilePath) {
		mFilePath = pFilePath;
	}

	public final String getDelimeterCollection() {
		return mDelimeterCollection;
	}

	public final void setDelimeterCollection(String pDelimeterCollection) {
		mDelimeterCollection = pDelimeterCollection;
	}

	public final String getEmptyMapOrCollection() {
		return mEmptyMapOrCollection;
	}

	public final void setEmptyMapOrCollection(String pEmptyMapOrCollection) {
		mEmptyMapOrCollection = pEmptyMapOrCollection;
	}

	public final int getPageSize() {
		return mPageSize;
	}

	public final void setPageSize(int pPageSize) {
		mPageSize = pPageSize;
	}

}
