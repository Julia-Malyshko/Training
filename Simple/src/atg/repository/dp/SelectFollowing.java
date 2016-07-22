package atg.repository.dp;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atg.adapter.gsa.GSAItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryView;
import atg.repository.query.PropertyQueryExpression;
import atg.repository.rql.RqlStatement;

public class SelectFollowing extends DerivationMethodImpl {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8492493838422215917L;

	private static final String SORT_PARAM = "sortProperty";

	@SuppressWarnings("unchecked")
	@Override
	public Object derivePropertyValue(final RepositoryItemImpl paramRepositoryItemImpl) throws RepositoryException {
		final LinkedList<Exception> errors = new LinkedList<Exception>();
		final List<Object> expressions;
		Object[] result = null;
		try {
			Map<String, Object> attributes = getPropertyDescriptor().getAttributesAsMap();
			String sortProp = (String) attributes.get(SORT_PARAM);
			@SuppressWarnings("unused")
			String foreignRepository = (String) attributes.get("foreignRepositoryPath");
			// foreignRepositoryName, template.queryable, etc
			expressions = getDerivation().getExpressionList();
			if (expressions.size() > 1) {
				throw new Exception("implemented only for one expression");
			}
			Object value;
			PropertyExpression propertyExpression;
			for (Object expression : expressions) {
				propertyExpression = (PropertyExpression) expression;
				value = propertyExpression.evaluate(paramRepositoryItemImpl);
				if (value instanceof Collection) {
					result = getItems(propertyExpression, (Collection<GSAItem>) value, sortProp);
				} else {
					throw new RepositoryException(DerivedProperties.format("ERR_DP_NOT_SET_LIST_MAP"));
				}
			}
		} catch (Exception exception) {
			errors.add(exception);
			result = errors.toArray(new Exception[0]);
		}
		return result;
	}

	protected RepositoryItem[] getItems(final PropertyExpression propertyExpression, final Collection<GSAItem> items,
			final String sortPropName) throws RepositoryException {
		final RepositoryItemDescriptor propertyItemDesc = propertyExpression.getPropertyDescriptor()
				.getComponentItemDescriptor();
		final RepositoryView view = propertyItemDesc.getRepositoryView();
		final Calendar calendar = Calendar.getInstance();
		final Timestamp currentDate = new Timestamp(calendar.getTime().getTime());
		final Object[] params = { getParams(items), currentDate };
		final RqlStatement statement = RqlStatement
				.parseRqlStatement("id in { " + params[0] + " } and " + sortPropName + " > ?1");
		final RepositoryItem[] result = statement.executeQuery(view, params);
		return result;

	}

	protected String getParams(final Collection<GSAItem> items) {
		final StringBuffer params;
		if (items == null) {
			params = null;
		} else {
			params = new StringBuffer();
			for (GSAItem item : items) {
				params.append("\"" + item.getRepositoryId() + "\", ");
			}
			params.delete(params.length() - 2, params.length());
		}
		return params.toString();
	}

	protected String[] propertiesNames(final PropertyExpression propertyExpression) {
		// propNames In component descriptor
		final RepositoryItemDescriptor propertyItemDesc = propertyExpression.getPropertyDescriptor()
				.getComponentItemDescriptor();
		final String[] propNames = propertyItemDesc.getPropertyNames();
		return propNames;
	}

	protected Set<String> getReferencedItemsProperties(final RepositoryItem[] items, final String[] propNames) {
		Set<String> result;
		if (items == null) {
			result = null;
		} else {
			result = new HashSet<String>();
			StringBuffer referencedItem;
			for (RepositoryItem item : items) {
				referencedItem = new StringBuffer(); // LinkedHashMap<String,
														// Object>();
				for (String prop : propNames) {
					referencedItem.append(prop + ": " + item.getPropertyValue(prop) + " | ");
				}
				result.add(referencedItem.toString());
			}
		}
		return result;
	}

	@Override
	public Object derivePropertyValue(Object paramObject) throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isQueryable() {
		// TODO Auto-generated method stub
		return true;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Query createQuery(int paramInt1, boolean paramBoolean1, boolean paramBoolean2,
			QueryExpression paramQueryExpression1, int paramInt2, boolean paramBoolean3,
			QueryExpression paramQueryExpression2, QueryExpression paramQueryExpression3, Query paramQuery,
			QueryBuilder paramQueryBuilder, PropertyQueryExpression paramPropertyQueryExpression, List paramList)
					throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

}
