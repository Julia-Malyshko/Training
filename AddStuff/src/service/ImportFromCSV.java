package service;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import atg.adapter.gsa.GSAPropertyDescriptor;
import atg.beans.DynamicPropertyDescriptor;
import atg.core.util.StringUtils;
import atg.nucleus.GenericService;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryPropertyDescriptor;
import tools.RepositoryMappingTools;
import util.CSVUtils;
import util.CommonUtils;
import util.IConstants;
import util.Validation;

/**
 * The Class ImportFromCSV.
 */
public class ImportFromCSV extends GenericService implements IConstants {
	// TODO factory
	/** The Repository. */
	private Repository mRepository;

	/** name of the item descriptor. */
	private String mItemDescriptorName;

	/** The whole file path. */
	private String mFilePath;

	/** The Delimiter of the list, set or map. */
	private String mDelimiterCollectionOrMap;

	/** The Empty map or collection. */
	private String mEmptyCollectionOrMap;
	private char mStartCollectionOrMap;
	private char mEndCollectionOrMap;

	/** pageSize - number of rows to read from file. */
	private int mPageSize;
	// TODO lastUpdateDate

	/** The Required properties. */
	// TODO required properties : 1) check import(all required must be here 2)
	// check custom required
	// 3) add to export required if they are not in the list of columns
	private List<String> mRequiredProperties;

	/** The Repository mapping tools. */
	private RepositoryMappingTools mRepositoryMappingTools;

	protected MutableRepositoryItem createMutableItem() throws RepositoryException {
		final MutableRepository itemRepository = (MutableRepository) getRepository();
		final MutableRepositoryItem item = itemRepository.createItem(getItemDescriptorName());
		return item;
	}

	/**
	 * Gets the list property.
	 *
	 * @param pPropValue
	 *            the prop value without {},[] and etc
	 * @param pDelimiter
	 *            the delimiter
	 * @return the list property
	 * @throws Exception
	 */
	protected List<Object> getListProperty(final Map<String, Object> pAttributes, final String pPropValue)
			throws Exception {
		final List<Object> list;
		final String delimiter = (String) pAttributes.get(COMPONENT_DELIMITER);
		final String emptyList = (String) pAttributes.get(EMPTY_COLLECTION_OR_MAP);
		if (pPropValue.equals(emptyList)) {
			list = null;
		} else {
			final String[] elements;
			Object component;
			list = new ArrayList<Object>();
			// TODO
			elements = CommonUtils.withoutWrapping(pPropValue, getStartCollectionOrMap(), getEndCollectionOrMap())
					.split("[" + delimiter + "\\s]");
			for (String element : elements) {
				if (!StringUtils.isEmpty(element)) {
					component = parseInstance(pAttributes, element);
					list.add(component);
				}
			}
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
	 * @throws Exception
	 */
	protected Set<Object> getSetProperty(final Map<String, Object> pAttributes, final String pPropValue)
			throws Exception {
		final Set<Object> set;
		final String delimiter = (String) pAttributes.get(COMPONENT_DELIMITER);
		final String emptySet = (String) pAttributes.get(EMPTY_COLLECTION_OR_MAP);
		Object component;
		if (pPropValue.equals(emptySet)) {
			set = null;
		} else {
			final String[] elements;
			set = new HashSet<Object>();
			// TODO
			elements = CommonUtils.withoutWrapping(pPropValue, getStartCollectionOrMap(), getEndCollectionOrMap()).split("[" + delimiter + "\\s]");
			for (String element : elements) {
				if (!StringUtils.isEmpty(element)) {
					component = parseInstance(pAttributes, element);
					set.add(component);
				}
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
	 * @throws Exception
	 */
	protected Map<String, Object> getMapProperty(final Map<String, Object> pAttributes, final String pPropValue)
			throws Exception {
		final Map<String, Object> map;
		final String delimiter = (String) pAttributes.get(COMPONENT_DELIMITER);
		final String emptyMap = (String) pAttributes.get(EMPTY_COLLECTION_OR_MAP);
		String key;
		Object value;
		if (pPropValue.equals(emptyMap)) {
			map = null;
		} else {
			final String[] pairs;
			String[] pair;
			map = new HashMap<String, Object>();
			// TODO
			pairs = CommonUtils.withoutWrapping(pPropValue, getStartCollectionOrMap(), getEndCollectionOrMap())
					.split("[" + delimiter + "\\s]");
			for (String pairValue : pairs) {
				if (!StringUtils.isEmpty(pairValue)) {
					pair = pairValue.split("=");
					key = pair[0].trim();
					value = parseInstance(pAttributes, pair[1].trim());
					map.put(key, value);
				}
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
	 * @throws Exception
	 */
	protected RepositoryItem getRepositoryItemProperty(final Map<String, Object> pAttributes, final String pItemId)
			throws Exception {
		final boolean isComponent = pAttributes.get(COMPONENT_TYPE) == null ? false : true;
		final String itemDescriptorName;
		final String repositoryPath;
		final Repository repository;
		if (isComponent) {
			itemDescriptorName = (String) pAttributes.get(COMPONENT_ITEM_DESCRIPTOR);
			repositoryPath = (String) pAttributes.get(COMPONENT_REPOSITORY_PATH);
			repository = getRepositoryMappingTools().getRepository(repositoryPath);
		} else {
			itemDescriptorName = (String) pAttributes.get(PROPERTY_ITEM_DESCRIPTOR);
			repositoryPath = (String) pAttributes.get(PROPERTY_REPOSITORY_PATH);
			repository = getRepositoryMappingTools().getRepository(repositoryPath);
		}
		final RepositoryItem result = repository.getItem(pItemId, itemDescriptorName);
		return result;
	}

	// TODO optimize

	protected Object parseCollectionOrMap() {
		return null;
	}

	@SuppressWarnings("rawtypes")
	protected Object parseInstance(final Map<String, Object> pAttributes, final String pPropValue) throws Exception {
		final Object propertyValue;
		//TODO optimize
		final Class type;
		final Class componentType = (Class) pAttributes.get(COMPONENT_TYPE);
		final Class propertyType = (Class) pAttributes.get(PROPERTY_TYPE);
		if (componentType == null) {
			type = propertyType;
		} else {
			type = componentType;
		}
		if (type == RepositoryItem.class) {
			propertyValue = getRepositoryItemProperty(pAttributes, pPropValue);
		} else if (type == Integer.class) {
			propertyValue = Integer.valueOf(pPropValue);
		} else if (type == Date.class) {
			// TODO check Time, Timestamp
			propertyValue = Date.valueOf(pPropValue);
		} else if (type == Double.class) {
			propertyValue = Double.valueOf(pPropValue);
		} else if (type == Boolean.class) {
			propertyValue = Boolean.valueOf(pPropValue);
		} else if (propertyType == String.class) {
			propertyValue = pPropValue;
		} else {
			propertyValue = null;
			throw new Exception("error while parsing " + pPropValue);
		}
		return propertyValue;
	}

	@SuppressWarnings("rawtypes")
	protected Object parseProperty(final Map<String, Object> pAttributes, final String pPropValue) throws Exception {
		final Object propertyValue;
		final Class propertyType = (Class) pAttributes.get(PROPERTY_TYPE);
		if (propertyType == List.class) {
			propertyValue = getListProperty(pAttributes, pPropValue);
		} else if (propertyType == Set.class) {
			propertyValue = getSetProperty(pAttributes, pPropValue);
		} else if (propertyType == Map.class) {
			propertyValue = getMapProperty(pAttributes, pPropValue);
		} else {
			propertyValue = parseInstance(pAttributes, pPropValue);
		}
		return propertyValue;
	}

	protected void setItemProperty(final MutableRepositoryItem pItem,
			final Entry<String, Map<String, Object>> pColumnAttributes, final String pPropValue) throws Exception {
		final String propName = pColumnAttributes.getKey();
		final Map<String, Object> propAttributes = pColumnAttributes.getValue();
		final Object propertyValue = parseProperty(propAttributes, pPropValue);
		pItem.setPropertyValue(propName, propertyValue);
	}

	protected RepositoryItem parseItem(final String[] pProperties,
			Map<String, Map<String, Object>> pColumnAttributesMapping) throws Exception {
		final MutableRepositoryItem item = createMutableItem();
		final Iterator<Entry<String, Map<String, Object>>> columnAttributesIterator = pColumnAttributesMapping
				.entrySet().iterator();
		if (pProperties.length == pColumnAttributesMapping.size()) {
			for (int i = 0; i < pProperties.length; i++) {
				// TODO required check
				setItemProperty(item, columnAttributesIterator.next(), pProperties[i].trim());
			}
		} else {
			throw new Exception("invalid data in csv file : number of fields doesn't equal to number of columns");
		}
		return item;
	}

	/**
	 * Read block of items.
	 *
	 * @param pFromLine
	 *            the from line
	 * @param pColumnAttributesMapping
	 *            the column types mapping
	 * @return the list
	 * @throws IOException
	 * @throws Exception
	 *             the exception
	 */
	protected List<RepositoryItem> readBlock(final int pFromLine,
			final Map<String, Map<String, Object>> pColumnAttributesMapping) throws IOException {
		final List<RepositoryItem> items = new ArrayList<RepositoryItem>();
		final List<String[]> records = CSVUtils.readCSV(getFilePath(), pFromLine, getPageSize());
		RepositoryItem item;
		// TODO transaction here
		for (final String[] record : records) {
			try {
				item = parseItem(record, pColumnAttributesMapping);
				items.add(item);
			} catch (Exception exc) {
				// TODO in file
				vlogError("error while converting " + (pFromLine + records.indexOf(record) + 1) + " row : "
						+ exc.getMessage());
			}
		}
		return items;
	}

	/**
	 * Gets the property repository path.
	 *
	 * @param pPropertyDescriptor
	 *            the property descriptor
	 * @return the property repository path
	 */
	protected String getPropertyRepositoryPath(final RepositoryPropertyDescriptor pPropertyDescriptor) {
		final String propertyRepositoryPath;
		final String foreignRepositoryPath;
		foreignRepositoryPath = (String) pPropertyDescriptor
				.getValue(RepositoryPropertyDescriptor.FOREIGN_REPOSITORY_PATH);
		if (foreignRepositoryPath == null) {
			propertyRepositoryPath = getNucleus().getAbsoluteNameOf(getRepository());
		} else {
			propertyRepositoryPath = foreignRepositoryPath;
		}
		return propertyRepositoryPath;
	}

	/**
	 * Gets the item descriptor name of the property or component of this
	 * property.
	 *
	 * @param pPropertyDescriptor
	 *            the property descriptor
	 * @param isComponent
	 *            the is component
	 * @return the property item descriptor name
	 */
	protected String getPropertyItemDescriptorName(final RepositoryPropertyDescriptor pPropertyDescriptor) {
		final String itemDescriptorName;
		final boolean isComponent = pPropertyDescriptor.isCollectionOrMap();
		final RepositoryItemDescriptor itemDescriptor;
		if (isComponent) {
			itemDescriptor = pPropertyDescriptor.getComponentItemDescriptor();
		} else {
			itemDescriptor = pPropertyDescriptor.getPropertyItemDescriptor();
		}
		itemDescriptorName = itemDescriptor.getItemDescriptorName();
		return itemDescriptorName;
	}

	/**
	 * Gets the repository item attributes.
	 *
	 * @param pPropertyDescriptor
	 *            the property descriptor
	 * @return the repository item attributes
	 */
	protected Map<String, Object> getRepositoryItemAttributes(final RepositoryPropertyDescriptor pPropertyDescriptor) {
		final Map<String, Object> attributes = new LinkedHashMap<String, Object>();
		final String repositoryPath = getPropertyRepositoryPath(pPropertyDescriptor);
		final String itemDescriptorName = getPropertyItemDescriptorName(pPropertyDescriptor);
		attributes.put(PROPERTY_REPOSITORY_PATH, repositoryPath);
		attributes.put(PROPERTY_ITEM_DESCRIPTOR, itemDescriptorName);
		return attributes;
	}

	/**
	 * Gets the collection or map attributes.
	 *
	 * @param pPropertyDescriptor
	 *            the property descriptor
	 * @return the collection or map attributes
	 */
	protected Map<String, Object> getCollectionOrMapAttributes(final RepositoryPropertyDescriptor pPropertyDescriptor) {
		final Map<String, Object> attributes = new LinkedHashMap<String, Object>();
		final String repositoryPath = getPropertyRepositoryPath(pPropertyDescriptor);
		final String itemDescriptorName = getPropertyItemDescriptorName(pPropertyDescriptor);
		attributes.put(COMPONENT_REPOSITORY_PATH, repositoryPath);
		attributes.put(COMPONENT_ITEM_DESCRIPTOR, itemDescriptorName);
		attributes.put(COMPONENT_DELIMITER, getDelimiterCollectionOrMap());
		attributes.put(EMPTY_COLLECTION_OR_MAP, getEmptyCollectionOrMap());
		return attributes;
	}

	/**
	 * Gets the repository property attributes.
	 *
	 * @param pPropertyDescriptor
	 *            the property descriptor
	 * @return the repository property attributes
	 */
	@SuppressWarnings("rawtypes")
	protected Map<String, Object> getRepositoryPropertyAttributes(
			final RepositoryPropertyDescriptor pPropertyDescriptor) {
		final Map<String, Object> attributes = new LinkedHashMap<String, Object>();
		Class propertyType = pPropertyDescriptor.getPropertyType();
		Class componentType = pPropertyDescriptor.getComponentPropertyType();
		attributes.put(PROPERTY_TYPE, propertyType);
		attributes.put(COMPONENT_TYPE, componentType);
		if (propertyType == RepositoryItem.class) {
			attributes.putAll(getRepositoryItemAttributes(pPropertyDescriptor));
		} else if (pPropertyDescriptor.isCollectionOrMap()) {
			attributes.putAll(getCollectionOrMapAttributes(pPropertyDescriptor));
		}
		return attributes;
	}

	/**
	 * Gets the column attributes.
	 *
	 * @param pColumn
	 *            the column
	 * @param pItemDescriptor
	 *            the item descriptor
	 * @return the column attributes
	 * @throws Exception
	 *             the exception
	 */
	protected Map<String, Object> getColumnAttributes(final String pColumn,
			final RepositoryItemDescriptor pItemDescriptor) throws Exception {
		final Map<String, Object> attributes;
		final DynamicPropertyDescriptor propertyDescriptor = pItemDescriptor.getPropertyDescriptor(pColumn);
		if (propertyDescriptor instanceof RepositoryPropertyDescriptor) {
			attributes = getRepositoryPropertyAttributes((RepositoryPropertyDescriptor) propertyDescriptor);
		} else {
			throw new Exception("unexpected type of propertyDescriptor " + propertyDescriptor.getClass().getName());
		}
		// TODO validation mapping
		if (propertyDescriptor instanceof GSAPropertyDescriptor) {
			GSAPropertyDescriptor gsaPropDesc = (GSAPropertyDescriptor) propertyDescriptor;
			int[] columnLength = gsaPropDesc.getJDBCColumnLengths();// array of
			if (columnLength == null) {
				// TODO
				vlogInfo(pColumn + " is transient");
			} else {
				columnLength.toString();
			}
			// class - already in map
		}
		return attributes;
	}

	/**
	 * Inits the column attributes mapping.
	 *
	 * @param pColumnNames
	 *            the column names
	 * @return the map
	 * @throws Exception
	 *             the exception
	 */
	protected Map<String, Map<String, Object>> initColumnAttributesMapping(final String[] pColumnNames)
			throws Exception {
		Map<String, Map<String, Object>> columnAttributesMapping = new LinkedHashMap<String, Map<String, Object>>();
		Map<String, Object> attributes;
		final RepositoryItemDescriptor itemDescriptor = getRepository().getItemDescriptor(getItemDescriptorName());
		for (String property : pColumnNames) {
			attributes = getColumnAttributes(property, itemDescriptor);
			columnAttributesMapping.put(property, attributes);
		}
		return columnAttributesMapping;
	}

	/**
	 * Gets all the records.
	 *
	 * @return the records
	 * @throws Exception
	 *             the exception
	 */
	protected List<RepositoryItem> getRecords() throws Exception {
		final List<RepositoryItem> items;
		int readFromLine;
		final String[] columnNames = CSVUtils.readCSVHeader(getFilePath());
		// column types
		// TODO check if all not null properties are in columns
		final Map<String, Map<String, Object>> columnAttributesMapping;
		final RepositoryItemDescriptor itemDesc = getRepository().getItemDescriptor(getItemDescriptorName());
		List<RepositoryItem> blockItems;
		if (Validation.validColumns(itemDesc, Arrays.asList(columnNames))) {
			columnAttributesMapping = initColumnAttributesMapping(columnNames);
			items = new ArrayList<RepositoryItem>();
			readFromLine = 1;
			while (!(blockItems = readBlock(readFromLine, columnAttributesMapping)).isEmpty()) {
				items.addAll(blockItems);
				readFromLine += blockItems.size();
			}
		} else {
			throw new Exception("invalid csv header for this item descriptor");
		}
		return items;
	}

	/**
	 * Import from csv.
	 */
	public void importFromCSV() {
		try {
			final List<RepositoryItem> allRecords = getRecords();
			vlogInfo("" + allRecords.size());
		} catch (Exception exc) {
			vlogError(exc.getMessage());
		}
	}

	public void checkCommonUtils() throws RepositoryException {
		// TODO end
		List<String> s = null;
		String[] a = CommonUtils.getListAsArray(s);
		s = new LinkedList<String>();
		s.add("first");
		s.add("second");
		a = CommonUtils.getListAsArray(s);
		a.toString();
		// List<RepositoryItem> r = null;
		// RepositoryItem[] rr = (RepositoryItem[])
		// CommonUtils.getListAsArray(r);
		// r = new LinkedList<>();
		// r.add(((MutableRepository) getRepository()).createItem("cat"));
		// r.add(((MutableRepository) getRepository()).createItem("cat"));
		// rr = (RepositoryItem[]) CommonUtils.getListAsArray(r);
		// rr.toString();
	}

	/**
	 * Gets the repository.
	 *
	 * @return the repository
	 */
	public final Repository getRepository() {
		return mRepository;
	}

	/**
	 * Sets the repository.
	 *
	 * @param pRepository
	 *            the new repository
	 */
	public final void setRepository(Repository pRepository) {
		mRepository = pRepository;
	}

	/**
	 * Gets the item descriptor name.
	 *
	 * @return the item descriptor name
	 */
	public final String getItemDescriptorName() {
		return mItemDescriptorName;
	}

	/**
	 * Sets the item descriptor name.
	 *
	 * @param pItemDescriptorName
	 *            the new item descriptor name
	 */
	public final void setItemDescriptorName(String pItemDescriptorName) {
		mItemDescriptorName = pItemDescriptorName;
	}

	/**
	 * Gets the file path.
	 *
	 * @return the file path
	 */
	public final String getFilePath() {
		return mFilePath;
	}

	/**
	 * Sets the file path.
	 *
	 * @param pFilePath
	 *            the new file path
	 */
	public final void setFilePath(String pFilePath) {
		mFilePath = pFilePath;
	}

	/**
	 * Gets the delimiter collection.
	 *
	 * @return the delimiter collection
	 */
	public final String getDelimiterCollectionOrMap() {
		return mDelimiterCollectionOrMap;
	}

	/**
	 * Sets the delimiter collection.
	 *
	 * @param pDelimeterCollection
	 *            the new delimiter collection
	 */
	public final void setDelimiterCollectionOrMap(String pDelimeterCollection) {
		mDelimiterCollectionOrMap = pDelimeterCollection;
	}

	/**
	 * Gets the page size.
	 *
	 * @return the page size
	 */
	public final int getPageSize() {
		return mPageSize;
	}

	/**
	 * Sets the page size.
	 *
	 * @param pPageSize
	 *            the new page size
	 */
	public final void setPageSize(int pPageSize) {
		mPageSize = pPageSize;
	}

	/**
	 * Gets the required properties.
	 *
	 * @return the required properties
	 */
	public final List<String> getRequiredProperties() {
		return mRequiredProperties;
	}

	/**
	 * Sets the required properties.
	 *
	 * @param pRequiredProperties
	 *            the new required properties
	 */
	public final void setRequiredProperties(List<String> pRequiredProperties) {
		mRequiredProperties = pRequiredProperties;
	}

	/**
	 * Gets the repository mapping tools.
	 *
	 * @return the repository mapping tools
	 */
	public final RepositoryMappingTools getRepositoryMappingTools() {
		return mRepositoryMappingTools;
	}

	/**
	 * Sets the repository mapping tools.
	 *
	 * @param pRepositoryMappingTools
	 *            the new repository mapping tools
	 */
	public final void setRepositoryMappingTools(RepositoryMappingTools pRepositoryMappingTools) {
		mRepositoryMappingTools = pRepositoryMappingTools;
	}

	public String getEmptyCollectionOrMap() {
		return mEmptyCollectionOrMap;
	}

	public void setEmptyCollectionOrMap(String pEmptyCollectionOrMap) {
		mEmptyCollectionOrMap = pEmptyCollectionOrMap;
	}

	public final char getStartCollectionOrMap() {
		return mStartCollectionOrMap;
	}

	public final void setStartCollectionOrMap(char pStartCollectionOrMap) {
		mStartCollectionOrMap = pStartCollectionOrMap;
	}

	public final char getEndCollectionOrMap() {
		return mEndCollectionOrMap;
	}

	public final void setEndCollectionOrMap(char pEndCollectionOrMap) {
		mEndCollectionOrMap = pEndCollectionOrMap;
	}
}
