package org.openspotlight.graph;

import org.openspotlight.storage.STStorageSession;

import com.google.inject.Provider;

public class SLGraphSessionFactoryImpl implements SLGraphSessionFactory{
	
	public SLGraphSessionFactoryImpl(final Provider<STStorageSession> sessionProvider){
		this.sessionProvider = sessionProvider;
	}
	
	private final Provider<STStorageSession> sessionProvider;

	@Override
	public SLFullGraphSession openFull() {
		return new SLFullGraphSessionImpl(sessionProvider.get(),null);
	}

	@Override
	public SLFullGraphSession openFull(String artifactId) {
		return new SLFullGraphSessionImpl(sessionProvider.get(),artifactId);
	}

	@Override
	public SLSimpleGraphSession openSimple() {
		return new SLSimpleGraphSessionImpl(sessionProvider.get());
	}

}
