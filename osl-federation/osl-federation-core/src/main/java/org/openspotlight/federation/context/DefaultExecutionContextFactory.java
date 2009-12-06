package org.openspotlight.federation.context;

import org.openspotlight.jcr.provider.JcrConnectionDescriptor;

public class DefaultExecutionContextFactory implements ExecutionContextFactory {

	public static ExecutionContextFactory createFactory() {
		return new DefaultExecutionContextFactory();
	}

	private DefaultExecutionContextFactory() {
	}

	public void closeResources() {
		// TODO Auto-generated method stub

	}

	public ExecutionContext createExecutionContext(final String username,
			final String password, final JcrConnectionDescriptor descriptor,
			final String repositoryName) {
		// TODO Auto-generated method stub
		return null;
	}

}
