package tools;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import atg.nucleus.GenericService;
import atg.repository.Repository;

/**
 * The Class RepositoryMappingTools which contains map {repository component path = repository} 
 * like custom cashing
 */
public class RepositoryMappingTools extends GenericService {

	/** The Repository mapping. */
	private static Map<String, Repository> mRepositoryMapping = new ConcurrentHashMap<>();

	/**
	 * Adds the repository in the repository mapping.
	 *
	 * @param pRepositoryPath
	 *            the repository path
	 * @return the repository
	 * @throws Exception
	 *             the exception
	 */
	protected Repository addRepository(final String pRepositoryPath) throws Exception {
		final Repository newRepository;
		final Object repository;
		repository = resolveName(pRepositoryPath);
		if (repository instanceof Repository) {
			newRepository = (Repository) repository;
			getRepositoryMapping().put(pRepositoryPath, newRepository);
		} else {
			throw new Exception("no such repository");
		}
		return newRepository;

	}

	/**
	 * returns true if repository already resolved.
	 *
	 * @param pRepositoryPath
	 *            the repository path
	 * @return true, if successful
	 */
	protected boolean alreadyResolved(final String pRepositoryPath) {
		final boolean resolved;
		if (getRepositoryMapping().containsKey(pRepositoryPath)) {
			resolved = true;
		} else {
			resolved = false;
		}
		return resolved;
	}

	/**
	 * Gets the repository from repository mapping.
	 *
	 * @param pRepositoryPath
	 *            the repository path
	 * @return the repository
	 * @throws Exception
	 *             the exception
	 */
	public Repository getRepository(final String pRepositoryPath) throws Exception {
		final Repository repository;
		if (alreadyResolved(pRepositoryPath)) {
			repository = getRepositoryMapping().get(pRepositoryPath);
		} else {
			repository = addRepository(pRepositoryPath);
		}
		return repository;
	}

	public static final Map<String, Repository> getRepositoryMapping() {
		return mRepositoryMapping;
	}

	public static void setRepositoryMapping(Map<String, Repository> pRepositoryMapping) {
		mRepositoryMapping = pRepositoryMapping;
	}

}
