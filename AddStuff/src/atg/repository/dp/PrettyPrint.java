package atg.repository.dp;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import atg.repository.Repository;
import atg.adapter.gsa.ChangeAwareSet;
import atg.core.util.ResourceUtils;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor;
import atg.repository.RepositoryView;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.query.PropertyQueryExpression;
import atg.repository.rql.RqlStatement;

public class PrettyPrint extends DerivationMethodImpl {

	@Override
	public Object derivePropertyValue(final RepositoryItemImpl paramRepositoryItemImpl) {
		final LinkedList<Exception> errors = new LinkedList<Exception>();
		final List expressions;
		final StringBuffer sb = new StringBuffer("pretty cat ");
		String valueAppend;
		try {
			expressions = getDerivation().getExpressionList();
			Object value;
			for (Object expression : expressions) {
				PropertyExpression propertyExpression = (PropertyExpression) expression;
				value = propertyExpression.evaluate(paramRepositoryItemImpl);
				if (value == null) {
					valueAppend = " | " + expression.toString() + ": null :(";
				} else {
					valueAppend = " | " + expression.toString() + ": " + value.toString();
				}
				sb.append(valueAppend);
			}
		} catch (Exception exception) {
			errors.add(exception);
			return errors;
		}

		return sb.toString();

	}
	

	@Override
	public Object derivePropertyValue(Object paramObject) throws RepositoryException {
		// TODO Auto-generated method stub
		return "cat :3";
	}

	@Override
	public boolean isQueryable() {
		// TODO Auto-generated method stub
		return true;
	}

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
