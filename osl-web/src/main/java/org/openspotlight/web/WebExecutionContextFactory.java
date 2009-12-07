package org.openspotlight.web;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.openspotlight.federation.context.DefaultExecutionContextFactory;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.context.ExecutionContextFactory;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;

public enum WebExecutionContextFactory {
	INSTANCE;

	private ExecutionContextFactory factory;

	public synchronized void contextStarted() {
		factory = DefaultExecutionContextFactory.createFactory();
	}

	public synchronized void contextStopped() {
		factory.closeResources();
		factory = null;
	}

	public ExecutionContext createExecutionContext(final ServletContext ctx,
			final HttpServletRequest request) {
		final String repositoryName = OslServletDataSupport
				.getCurrentRepository(ctx, request);
		final JcrConnectionDescriptor descriptor = OslServletDataSupport
				.getJcrDescriptor(ctx, request);
		final String password = OslServletDataSupport.getPassword(ctx, request);
		final String username = OslServletDataSupport.getUserName(ctx, request);
		final ExecutionContext newContext = factory.createExecutionContext(
				username, password, descriptor, repositoryName);
		return newContext;
	}

}
