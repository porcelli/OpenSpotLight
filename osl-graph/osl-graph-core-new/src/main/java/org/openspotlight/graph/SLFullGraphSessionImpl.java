package org.openspotlight.graph;

import org.openspotlight.graph.manipulation.SLGraphWriter;
import org.openspotlight.storage.STPartitionFactory;
import org.openspotlight.storage.STStorageSession;

import com.google.inject.Provider;

public class SLFullGraphSessionImpl extends SLSimpleGraphSessionImpl implements
		SLFullGraphSession {

	private final SLGraphWriter toSync;

	public SLFullGraphSessionImpl(Provider<STStorageSession> sessionProvider,
			String artifactId, STPartitionFactory factory) {
		super(sessionProvider, factory);
		this.toSync = new SLGraphWriterImpl(factory, sessionProvider,
				artifactId, this.location(SLGraphLocation.CENTRAL));
	}

	@Override
	public SLGraphWriter toSync() {
		return toSync;
	}

}
