package droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class PrintAllItems extends DynamoServlet {
	private String ITEM_DESCRIPTOR;
	private Repository repository;

	// input parameters
	// output parameters
	private static final String NUM_ITEMS = "numItems";
	private static final String ITEM = "item";
	private static final String ERROR_MESSAGE = "errorMessage";
	private static final String EMPTY_MESSAGE = "emptyMessage";
	// open parameters
	private static final String OPARAM_OUTPUT = "output";
	private static final String OPARAM_START_OUTPUT = "startOutput";
	private static final String OPARAM_ERROR = "error";
	private static final String OPARAM_EMPTY = "empty";

	public String getITEM_DESCRIPTOR() {
		return ITEM_DESCRIPTOR;
	}

	public void setITEM_DESCRIPTOR(String iTEM_DESCRIPTOR) {
		ITEM_DESCRIPTOR = iTEM_DESCRIPTOR;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	protected RepositoryItem[] searchItems() throws RepositoryException {
		RepositoryView view = repository.getView(ITEM_DESCRIPTOR);
		RqlStatement statement = RqlStatement.parseRqlStatement("ALL");
		Object params[] = null;
		RepositoryItem[] items = statement.executeQuery(view, params);
		return items;
	}

	protected void printEmptyMessage(DynamoHttpServletRequest request, DynamoHttpServletResponse response)
			throws ServletException, IOException {
		request.setParameter(EMPTY_MESSAGE, "No items were found.");
		request.serviceLocalParameter(OPARAM_EMPTY, request, response);
	}

	protected void printItemList(DynamoHttpServletRequest request, DynamoHttpServletResponse response,
			RepositoryItem[] items) throws ServletException, IOException {
		request.setParameter(NUM_ITEMS, items.length);
		request.serviceLocalParameter(OPARAM_START_OUTPUT, request, response);
		for (RepositoryItem item : items) {
			request.setParameter(ITEM, item);
			request.serviceLocalParameter(OPARAM_OUTPUT, request, response);
		}
	}

	public void service(DynamoHttpServletRequest request, DynamoHttpServletResponse response)
			throws ServletException, IOException {

		
			try {
				RepositoryItem[] items = searchItems();
				if (items == null) {
					printEmptyMessage(request, response);
				} else {
					printItemList(request, response, items);
				}
			} catch (RepositoryException exc) {
				logDebug(exc.getMessage());
				request.setParameter(ERROR_MESSAGE, "Some errors");
				request.serviceLocalParameter(OPARAM_ERROR, request, response);
			}
	}
}
