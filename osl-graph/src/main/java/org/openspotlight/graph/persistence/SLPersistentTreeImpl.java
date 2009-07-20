package org.openspotlight.graph.persistence;

import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class SLPersistentTreeImpl implements SLPersistentTree {
	
	private Repository repository;
	private Credentials credentials;
	
	public SLPersistentTreeImpl(Repository repository, Credentials credentials) {
		this.repository = repository;
		this.credentials = credentials;
	}

	@Override
	public SLPersistentTreeSession openSession() throws SLPersistentTreeException {
		try {
			Session session = repository.login(credentials);
			return new SLPersistentTreeSessionImpl(session);
		}
		catch (RepositoryException e) {
			throw new SLPersistentTreeException("Error on attempt to open persistent tree session.", e);
		}
	}

	@Override
	public void shutdown() {
	}
}
