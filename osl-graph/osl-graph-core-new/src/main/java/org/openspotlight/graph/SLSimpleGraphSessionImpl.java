package org.openspotlight.graph;

import java.util.HashMap;
import java.util.Map;

import org.openspotlight.graph.manipulation.SLGraphReader;
import org.openspotlight.graph.manipulation.SLGraphTransientWriter;
import org.openspotlight.security.authz.PolicyEnforcement;
import org.openspotlight.security.idm.User;
import org.openspotlight.storage.STStorageSession;

import com.google.inject.Provider;

public class SLSimpleGraphSessionImpl implements SLSimpleGraphSession {

	public SLSimpleGraphSessionImpl(Provider<STStorageSession> sessionProvider) {
		this.transientWriter = new SLGraphTransientWriterImpl();
		this.sessionProvider = sessionProvider;
	}

	private final SLGraphTransientWriter transientWriter;
	protected final Provider<STStorageSession> sessionProvider;

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public void flushChangedProperties(SLNode node) {
		if(node.)// TODO Auto-generated method stub

	}

	@Override
	public PolicyEnforcement getPolicyEnforcement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User getUser() {
		// TODO Auto-generated method stub
		return null;
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
			reader = new SLGraphReaderImpl(sessionProvider, location);
			readerCache.put(location, reader);
		}
		return reader;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

}
