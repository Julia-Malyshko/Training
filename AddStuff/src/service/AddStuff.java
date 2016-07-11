package service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import atg.commerce.CommerceException;
import atg.commerce.catalog.CatalogTools;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemManager;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.userprofiling.ProfileTools;
import util.IConstants;
import util.RandomUtils;

public class AddStuff extends GenericService implements IConstants {

	private ProfileTools mProfileTools;
	private OrderManager mOrderManager;
	private CatalogTools mCatalogTools;
	private CommerceItemManager mCommerceItemManager;
	private TransactionManager mTransactionManager;

	private String mCategoryId;

	private String mUserLogin;
	private String mDefaultPassword;
	private String mUserLoginSuffix;
	private int mUsersOffset;
	private int mUsersCount;

	private int mOrdersOffset;
	private int mOrdersCount;
	private int mMaxQuantity;
	private int mMinQuantity;

	protected void addUser(final String pLogin, final String pPassword) throws RepositoryException {
		final MutableRepository profileRepository = getProfileTools().getProfileRepository();
		final MutableRepositoryItem newUser;
		newUser = profileRepository.createItem(USER_ITEM_DESC);
		newUser.setPropertyValue(USER_LOGIN, pLogin);
		newUser.setPropertyValue(USER_EMAIL, pLogin);
		newUser.setPropertyValue(USER_PASSWORD, pPassword);
		profileRepository.addItem(newUser);
	}

	protected void deleteUser(final RepositoryItem pUser) throws RepositoryException {
		final MutableRepository profileRepository = getProfileTools().getProfileRepository();
		profileRepository.removeItem(pUser.getRepositoryId(), USER_ITEM_DESC);
	}

	// protected List<RepositoryItem> getAllSkusInCatalog() throws
	// RepositoryException {
	// final List<RepositoryItem> result = new LinkedList<RepositoryItem>();
	// final Repository catalog = getCatalogTools().getCatalog();
	//
	// final RepositoryView view = catalog.getView(SKU_ITEM_DESC);
	// final RqlStatement statement = RqlStatement.parseRqlStatement("ALL");
	// final Object params[] = null;
	// final RepositoryItem[] resultQuery = statement.executeQuery(view,
	// params);
	// for (RepositoryItem item : resultQuery) {
	// result.add(item);
	// }
	// return result;
	// }

	protected RepositoryItem findCategory(final String pCategoryId) throws RepositoryException {
		final RepositoryItem category;
		final Repository catalog = getCatalogTools().getCatalog();
		final RepositoryView view = catalog.getView(CATEGORY_ITEM_DESCRIPTOR);
		final RqlStatement statement = RqlStatement.parseRqlStatement("id = ?0");
		final Object params[] = { pCategoryId };
		category = statement.executeQuery(view, params)[0];
		return category;
	}

	@SuppressWarnings("unchecked")
	protected Map<String, List<RepositoryItem>> getChildSKUs(final RepositoryItem pProduct) {
		final Map<String, List<RepositoryItem>> result = new HashMap<String, List<RepositoryItem>>();
		final List<RepositoryItem> childSKUs;
		final String productId;
		childSKUs = (List<RepositoryItem>) pProduct.getPropertyValue(PRODUCT_CHILD_SKUS);
		productId = pProduct.getRepositoryId();
		if (childSKUs != null) {
			result.put(productId, childSKUs);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	protected Map<String, List<RepositoryItem>> getAllSkusInCategory(final String pCategoryId)
			throws RepositoryException {
		final Map<String, List<RepositoryItem>> result = new HashMap<String, List<RepositoryItem>>();
		final RepositoryItem category = findCategory(pCategoryId);
		final List<RepositoryItem> childCategories = (List<RepositoryItem>) category
				.getPropertyValue(CATEGORY_CHILD_CATEGORIES);
		final List<RepositoryItem> childProducts = (List<RepositoryItem>) category
				.getPropertyValue(CATEGORY_CHILD_PRODUCTS);
		if (childCategories == null) {
			for (final RepositoryItem product : childProducts) {
				result.putAll(getChildSKUs(product));
			}
		} else {
			String childCategoryId;
			for (final RepositoryItem childCategory : childCategories) {
				childCategoryId = childCategory.getRepositoryId();
				result.putAll(getAllSkusInCategory(childCategoryId));
			}
		}
		return result;
	}

	// @SuppressWarnings("unchecked")
	// protected List<CommerceItem> getRandomCommerceItems(final
	// List<RepositoryItem> pSKUs) throws CommerceException {
	// final List<CommerceItem> commerceItems;
	// final Random rand = new Random();
	// final double probability = 0.5;
	// if (pSKUs == null) {
	// commerceItems = null;
	// } else {
	// commerceItems = new LinkedList<CommerceItem>();
	// for (RepositoryItem sku : pSKUs) {
	// if (rand.nextDouble() > probability) {
	// String skuId = sku.getRepositoryId();
	// Set<Object> parentProducts = (Set<Object>)
	// sku.getPropertyValue(SKU_PARENT_PRODUCTS);
	// for (Object parentProduct : parentProducts) {
	// String productId = ((RepositoryItem) parentProduct).getRepositoryId();
	// long quantity = RandomUtils.randomUniform(getMaxQuantity(),
	// getMinQuantity());
	// CommerceItem commerceSKU =
	// getCommerceItemManager().createCommerceItem(skuId, productId,
	// quantity);
	// commerceItems.add(commerceSKU);
	// }
	// }
	// }
	// }
	// return commerceItems;
	// }

	protected List<CommerceItem> getRandomCommerceItemsByProduct(final List<RepositoryItem> pProductSKUs,
			final String pProductId) throws CommerceException {
		final List<CommerceItem> commerceItems = new LinkedList<CommerceItem>();
		CommerceItem commerceSKU;
		String skuId;
		long quantity;
		for (final RepositoryItem sku : pProductSKUs) {
			if (RandomUtils.random.nextDouble() > PROBABILITY_ADD_SKU) {
				skuId = sku.getRepositoryId();
				quantity = RandomUtils.randomUniform(getMaxQuantity(), getMinQuantity());
				commerceSKU = getCommerceItemManager().createCommerceItem(skuId, pProductId, quantity);
				commerceItems.add(commerceSKU);
			}
		}
		return commerceItems;
	}

	protected List<CommerceItem> getRandomCommerceItems(final Map<String, List<RepositoryItem>> pSKUs)
			throws CommerceException {
		final List<CommerceItem> commerceItems;
		if (pSKUs == null) {
			commerceItems = null;
		} else {
			commerceItems = new LinkedList<CommerceItem>();
			for (final String productId : pSKUs.keySet()) {
				commerceItems.addAll(getRandomCommerceItemsByProduct(pSKUs.get(productId), productId));
			}
		}
		return commerceItems;
	}

	protected void createRandomOrder(final String pUserId) throws CommerceException, RepositoryException {
		final Order order = getOrderManager().createOrder(pUserId);
		final Map<String, List<RepositoryItem>> skus = getAllSkusInCategory(getCategoryId());
		final List<CommerceItem> commerceItems = getRandomCommerceItems(skus);
		for (final CommerceItem commerceItem : commerceItems) {
			order.addCommerceItem(commerceItem);
		}
		getOrderManager().addOrder(order);
	}

	protected List<String> getIds(final RepositoryItem[] pItems) {
		final List<String> ids;
		if (pItems == null) {
			ids = null;
		} else {
			ids = new LinkedList<String>();
			for (RepositoryItem item : pItems) {
				ids.add(item.getRepositoryId());
			}
		}
		return ids;
	}

	protected RepositoryItem[] getMyUsers() throws RepositoryException {
		final RepositoryItem[] myUsers;
		final Repository profileRepository = getProfileTools().getProfileRepository();
		final RepositoryView view = profileRepository.getView(USER_ITEM_DESC);
		final RqlStatement statement = RqlStatement
				.parseRqlStatement(USER_LOGIN + " starts with ?0 and " + USER_LOGIN + " ends with ?1");
		final Object params[] = { getUserLogin(), getUserLoginSuffix() };
		myUsers = statement.executeQuery(view, params);
		return myUsers;
	}

	protected void endTransaction(final boolean rollback, final TransactionManager tm, final TransactionDemarcation td)
			throws TransactionDemarcationException, IllegalStateException, SecurityException, SystemException {
		if (rollback) {
			tm.rollback();
		}
		td.end();
	}

	@SuppressWarnings("static-access")
	public void addUsers() {
		boolean rollback = false;
		final TransactionManager tm = getTransactionManager();
		final TransactionDemarcation td = new TransactionDemarcation();
		try {
			td.begin(tm, td.REQUIRED);
			try {
				for (int i = getUsersOffset(); i < getUsersCount(); i++) {
					addUser(getUserLogin() + i + getUserLoginSuffix(), getDefaultPassword());
				}
				try {
					tm.commit();
				} catch (SecurityException | IllegalStateException | RollbackException | HeuristicMixedException
						| HeuristicRollbackException | SystemException e) {
					rollback = true;
				}
			} catch (RepositoryException e) {
				rollback = true;
			} finally {
				try {
					endTransaction(rollback, tm, td);
				} catch (IllegalStateException | SecurityException | SystemException exc) {
					vlogError("rollback error");
				}
			}
		} catch (TransactionDemarcationException exc) {
			vlogError("TransactionDemarcationException");
		}
		vlogInfo("users were successfully added");
	}

	public void deleteMyUsers() {
		try {
			final RepositoryItem[] myUsers = getMyUsers();
			for (final RepositoryItem myUser : myUsers) {
				deleteUser(myUser);
			}
		} catch (RepositoryException e) {
			vlogError("can't delete users");
		}
		vlogInfo("users were successfully deleted");
	}

	public void createRandomOrdersToMyUsers() {
		try {
			int numOrders;
			final RepositoryItem[] myUsers = getMyUsers();
			final List<String> myUserIds = getIds(myUsers);
			for (final String userId : myUserIds) {
				numOrders = RandomUtils.randomUniform(getOrdersCount(), getOrdersOffset());
				for (int i = 0; i < numOrders; i++) {
					createRandomOrder(userId);
				}
			}
		} catch (CommerceException | RepositoryException exc) {
			vlogError("create random orders crashed");
		}
		vlogInfo("random orders were succesfully added");
	}

	public void doAddStuff() {
		addUsers();
		createRandomOrdersToMyUsers();
	}

	@Override
	public void doStartService() throws ServiceException {
		vlogInfo("do start add stuff");
		super.doStartService();
	}

	public final ProfileTools getProfileTools() {
		return mProfileTools;
	}

	public final void setProfileTools(ProfileTools pProfileTools) {
		mProfileTools = pProfileTools;
	}

	public final OrderManager getOrderManager() {
		return mOrderManager;
	}

	public final void setOrderManager(OrderManager pOrderManager) {
		mOrderManager = pOrderManager;
	}

	public final CatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	public final void setCatalogTools(CatalogTools pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}

	public final CommerceItemManager getCommerceItemManager() {
		return mCommerceItemManager;
	}

	public final void setCommerceItemManager(CommerceItemManager pCommerceItemManager) {
		mCommerceItemManager = pCommerceItemManager;
	}

	public final TransactionManager getTransactionManager() {
		return mTransactionManager;
	}

	public final void setTransactionManager(TransactionManager pTransactionManager) {
		mTransactionManager = pTransactionManager;
	}

	public final String getUserLogin() {
		return mUserLogin;
	}

	public final void setUserLogin(String pUserLogin) {
		mUserLogin = pUserLogin;
	}

	public final String getDefaultPassword() {
		return mDefaultPassword;
	}

	public final void setDefaultPassword(String pDefaultPassword) {
		mDefaultPassword = pDefaultPassword;
	}

	public final int getUsersOffset() {
		return mUsersOffset;
	}

	public final void setUsersOffset(int pUsersOffset) {
		mUsersOffset = pUsersOffset;
	}

	public final int getUsersCount() {
		return mUsersCount;
	}

	public final void setUsersCount(int pUsersCount) {
		mUsersCount = pUsersCount;
	}

	public final int getOrdersOffset() {
		return mOrdersOffset;
	}

	public final void setOrdersOffset(int pOrdersOffset) {
		mOrdersOffset = pOrdersOffset;
	}

	public final int getOrdersCount() {
		return mOrdersCount;
	}

	public final void setOrdersCount(int pOrdersCount) {
		mOrdersCount = pOrdersCount;
	}

	public final String getUserLoginSuffix() {
		return mUserLoginSuffix;
	}

	public final void setUserLoginSuffix(String pUserLoginSuffix) {
		mUserLoginSuffix = pUserLoginSuffix;
	}

	public final String getCategoryId() {
		return mCategoryId;
	}

	public final void setCategoryId(String pCategoryId) {
		mCategoryId = pCategoryId;
	}

	public final int getMaxQuantity() {
		return mMaxQuantity;
	}

	public final void setMaxQuantity(int pMaxQuantity) {
		mMaxQuantity = pMaxQuantity;
	}

	public final int getMinQuantity() {
		return mMinQuantity;
	}

	public final void setMinQuantity(int pMinQuantity) {
		mMinQuantity = pMinQuantity;
	}

}
