package service;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.transaction.TransactionManager;

import atg.commerce.CommerceException;
import atg.commerce.catalog.CatalogTools;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.GenericService;

import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.userprofiling.ProfileTools;
import util.IConstants;

public class AddStuff extends GenericService implements IConstants {
	private ProfileTools mProfileTools;
	private OrderManager mOrderManager;
	private CatalogTools mCatalogTools;
	//private TransactionManager mTransactionManager;
	//private TransactionDemarcation mTransactionDemocration;

	private String mUserLogin = "user";
	private String mUserPassword = "12345";

	protected void addUser(final String pLogin, final String pPassword) {
		final MutableRepository profileRepository = mProfileTools.getProfileRepository();
		final MutableRepositoryItem newUser;
		// TO DO transaction here
		try {
			newUser = profileRepository.createItem(USER_ITEM_DESC);
			newUser.setPropertyValue(USER_LOGIN, pLogin);
			newUser.setPropertyValue(USER_EMAIL, pLogin);
			newUser.setPropertyValue(USER_PASSWORD, pPassword);
			profileRepository.addItem(newUser);
		} catch (RepositoryException e) {
			vlogError("can't create user " + pLogin);
		}

	}
	
/*	protected void addUser2(final String pLogin, final String pPassword) {
		final MutableRepository profileRepository = mProfileTools.getProfileRepository();
		final MutableRepositoryItem newUser;
		// TO DO transaction here
		final TransactionManager tm = getTransactionManager();
		final TransactionDemarcation td = getTransactionDemocration();
		try {
			if(tm != null){
				td.begin(tm);
			}
			newUser = profileRepository.createItem(USER_ITEM_DESC);
			newUser.setPropertyValue(USER_LOGIN, pLogin);
			newUser.setPropertyValue(USER_EMAIL, pLogin);
			newUser.setPropertyValue(USER_PASSWORD, pPassword);
			profileRepository.addItem(newUser);
		} catch (TransactionDemarcationException exc) {
			vlogError("add user with login " + pLogin + " rollback");
		} catch (RepositoryException e) {
			vlogError("can't create user " + pLogin);
		}

	}*/

	protected void deleteUser(final RepositoryItem pUser) {
		final MutableRepository profileRepository = mProfileTools.getProfileRepository();
		try {
			profileRepository.removeItem(pUser.getRepositoryId(), USER_ITEM_DESC);
		} catch (RepositoryException e) {
			vlogError("can't delete user with id " + pUser.getRepositoryId());
		}
	}

	protected RepositoryItem[] getAllSkusInCatalog() {
		RepositoryItem[] result; // ???
		final Repository catalog = mCatalogTools.getCatalog();
		//
		String[] viewNames = catalog.getViewNames();
		//
		try {
			final RepositoryView view = catalog.getView(SKU_ITEM_DESC);
			final RqlStatement statement = RqlStatement.parseRqlStatement("ALL");
			final Object params[] = null;
			result = statement.executeQuery(view, params);
		} catch (RepositoryException e) {
			result = null;
			vlogError("getAllSkusInCatalog crashes");
		}
		return result;
	}

	protected List<CommerceItem> getRandomCommerceItems(final RepositoryItem[] pSKUs) {
		final List<CommerceItem> commerceItems;
		final Random rand = new Random();
		final double probability = 0.5;
		if (pSKUs == null) {
			commerceItems = null;
		} else {
			commerceItems = new LinkedList<CommerceItem>();
			for (RepositoryItem sku : pSKUs) {
				if (rand.nextDouble() > probability) {
					commerceItems.add((CommerceItem) sku);
				}
			}
		}
		return commerceItems;
	}

	protected void createRandomOrder(final String pUserId) {
		try {
			final Order order = mOrderManager.createOrder(pUserId);
			final RepositoryItem[] skus = getAllSkusInCatalog();
			final List<CommerceItem> commerceItems = getRandomCommerceItems(skus);
			for (CommerceItem commerceItem : commerceItems) {
				order.addCommerceItem(commerceItem);
			}
			mOrderManager.addOrder(order);
		} catch (CommerceException e) {
			vlogError("can't create order for user with id = " + pUserId);
		}
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

	protected RepositoryItem[] getMyUsers(final String pUserLogin) {
		RepositoryItem[] myUsers;
		Repository profileRepository = mProfileTools.getProfileRepository();
		try {
			RepositoryView view = profileRepository.getView(USER_ITEM_DESC);
			final RqlStatement statement = RqlStatement.parseRqlStatement(USER_LOGIN + " contains ?0");
			final Object params[] = { pUserLogin };
			myUsers = statement.executeQuery(view, params);
		} catch (RepositoryException e) {
			myUsers = null;
			vlogError("getMyUsers crashes");
		}
		return myUsers;
	}

	public void addUsers() {
		int numUsers = 10;
		for (int i = 0; i < numUsers; i++) {
			addUser(mUserLogin + i, mUserPassword);
		}
		vlogInfo("users were successfully added");
	}

	public void deleteMyUsers() {
		final RepositoryItem[] myUsers = getMyUsers(mUserLogin);
		for (RepositoryItem myUser : myUsers) {
			deleteUser(myUser);
		}
		vlogInfo("users were successfully deleted");
	}

	public void createRandomOrdersToMyUsers() {
		RepositoryItem[] myUsers = getMyUsers(mUserLogin);
		List<String> myUserIds = getIds(myUsers);
		for (String userId : myUserIds) {
			createRandomOrder(userId);
		}
		vlogInfo("random orders were succesfully added");
	}

	public void doAddStuff() {
		addUsers();
		createRandomOrdersToMyUsers();
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
}
