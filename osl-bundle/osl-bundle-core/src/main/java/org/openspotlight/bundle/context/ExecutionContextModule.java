package org.openspotlight.bundle.context;

import com.google.inject.AbstractModule;

public class ExecutionContextModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ExecutionContext.class).to(DefaultExecutionContext.class);
	}

}
