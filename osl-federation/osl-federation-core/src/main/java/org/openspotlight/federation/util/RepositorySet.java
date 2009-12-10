/**
 * 
 */
package org.openspotlight.federation.util;

import java.util.Set;

import org.openspotlight.federation.domain.Repository;
import org.openspotlight.persist.annotation.SimpleNodeType;

public class RepositorySet implements SimpleNodeType {

	private Set<Repository> repositories;

	public Set<Repository> getRepositories() {
		return repositories;
	}

	public void setRepositories(final Set<Repository> repositories) {
		this.repositories = repositories;
	}
}