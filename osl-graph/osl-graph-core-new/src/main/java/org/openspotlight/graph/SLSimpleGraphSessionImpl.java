package org.openspotlight.graph;

import java.util.HashMap;
import java.util.Map;

import org.openspotlight.graph.manipulation.SLGraphReader;
import org.openspotlight.graph.manipulation.SLGraphTransientWriter;
import org.openspotlight.security.authz.PolicyEnforcement;
import org.openspotlight.security.idm.User;
import org.openspotlight.storage.STPartitionFactory;
import org.openspotlight.storage.STStorageSession;

import com.google.inject.Provider;

public class SLSimpleGraphSessionImpl implements SLSimpleGraphSession {

	public SLSimpleGraphSessionImpl(Provider<STStorageSession> sessionProvider,
			STPartitionFactory factory) {
		this.transientWriter = new SLGraphTransientWriterImpl();
		this.sessionProvider = sessionProvider;
		this.factory = factory;
	}

	private final STPartitionFactory factory;
	private final SLGraphTransientWriter transientWriter;
	protected final Provider<STStorageSession> sessionProvider;

	@Override
	public void close() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void flushChangedProperties(SLNode node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public PolicyEnforcement getPolicyEnforcement() {
		throw new UnsupportedOperationException();
	}

	@Override
	public User getUser() {
		throw new UnsupportedOperationException();
	}

	@Override
	public SLGraphTransientWriter local() {
		return transientWriter;
	}

	private final Map<SLGraphLocation, SLGraphReader> readerCache = new HashMap<SLGraphLocation, SLGraphReader>();

	@Override
	public SLGraphReader location(SLGraphLocation location) {
		SLGraphReader reader = readerCache.get(location);
		if (reader == null) {
			reader = new SLGraphReaderImpl(sessionProvider, location, factory);
			readerCache.put(location, reader);
		}
		return reader;
	}

	@Override
	public void shutdown() {
		throw new UnsupportedOperationException();

	}

}
