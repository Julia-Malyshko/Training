package util;

public interface IConstants {
	static final String USER_ITEM_DESC = "user";
	static final String USER_LOGIN = "login";
	static final String USER_EMAIL = "email";
	static final String USER_PASSWORD = "password";
	
	static final String SKU_ITEM_DESC = "sku";
	static final String SKU_PARENT_PRODUCTS = "parentProducts";
	
	static final String CATEGORY_ITEM_DESCRIPTOR = "category";
	static final String CATEGORY_CHILD_CATEGORIES = "childCategories";
	static final String CATEGORY_CHILD_PRODUCTS = "childProducts";
	static final String PRODUCT_CHILD_SKUS = "childSKUs";
	
	static final double PROBABILITY_ADD_SKU = 0.5;
	
	static final String CSV_FILE = ".csv";
	static final String TXT_FILE = ".txt";
	static final String NEW_LINE = "\n";
	
	static final String PROPERTY_VALUE = "propertyValue";
	static final String COLLECTION_DELIMITER = "collectionDelimiter";
	static final String REPOSITORY_ITEM = "repositoryItem";
	//for Import map attributes
	static final String PROPERTY_TYPE = "propertyType"; // all have
	//if repository item
	static final String PROPERTY_REPOSITORY_PATH = "propertyRepositoryPath";
	static final String PROPERTY_ITEM_DESCRIPTOR = "propertyItemDescriptor";
	//if map or collection -> not null
	static final String COMPONENT_TYPE = "componentType"; // all have
	//if repository item
	static final String COMPONENT_REPOSITORY_PATH = "componentRepositoryPath";
	static final String COMPONENT_ITEM_DESCRIPTOR = "componentItemDescriptor";
	static final String COMPONENT_DELIMITER = "componentDelimiter";
	static final String EMPTY_COLLECTION_OR_MAP = "emptyCollectionOrMap";
}
