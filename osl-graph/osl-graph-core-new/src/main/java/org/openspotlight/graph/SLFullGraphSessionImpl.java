package org.openspotlight.graph;

import org.openspotlight.graph.manipulation.SLGraphWriter;
import org.openspotlight.storage.STStorageSession;

import com.google.inject.Provider;

public class SLFullGraphSessionImpl extends SLSimpleGraphSessionImpl implements SLFullGraphSession {

	private final SLGraphWriter toSync;
	
	public SLFullGraphSessionImpl(Provider<STStorageSession> sessionProvider, String artifactId) {
		super(sessionProvider);
		this.toSync = new SLGraphWriterImpl(sessionProvider,artifactId);
	}

	@Override
	public SLGraphWriter toSync() {
		return toSync;
	}

	
}
