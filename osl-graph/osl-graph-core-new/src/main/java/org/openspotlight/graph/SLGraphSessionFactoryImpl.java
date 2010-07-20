package org.openspotlight.graph;

import org.openspotlight.storage.STPartitionFactory;
import org.openspotlight.storage.STStorageSession;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class SLGraphSessionFactoryImpl implements SLGraphSessionFactory {

	@Inject
	public SLGraphSessionFactoryImpl(
			final Provider<STStorageSession> sessionProvider,
			STPartitionFactory factory) {
		this.sessionProvider = sessionProvider;
		this.factory = factory;
	}

	private final STPartitionFactory factory;

	private final Provider<STStorageSession> sessionProvider;

	@Override
	public SLFullGraphSession openFull() {
		return new SLFullGraphSessionImpl(sessionProvider, null, factory);
	}

	@Override
	public SLFullGraphSession openFull(String artifactId) {
		return new SLFullGraphSessionImpl(sessionProvider, artifactId, factory);
	}

	@Override
	public SLSimpleGraphSession openSimple() {
		return new SLSimpleGraphSessionImpl(sessionProvider, factory);
	}

}
