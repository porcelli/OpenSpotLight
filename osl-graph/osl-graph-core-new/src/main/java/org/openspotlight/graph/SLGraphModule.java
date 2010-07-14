package org.openspotlight.graph;

import com.google.inject.AbstractModule;

public class SLGraphModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(SLGraphSessionFactory.class).to(SLGraphSessionFactoryImpl.class);
	}

}
