package org.openspotlight.federation.context;

import java.util.concurrent.CopyOnWriteArrayList;

import org.openspotlight.common.DisposingListener;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;

public class DefaultExecutionContextFactory implements ExecutionContextFactory,
		DisposingListener<DefaultExecutionContext> {

	public static ExecutionContextFactory createFactory() {
		return new DefaultExecutionContextFactory();
	}

	private final CopyOnWriteArrayList<DefaultExecutionContext> openedContexts = new CopyOnWriteArrayList<DefaultExecutionContext>();

	private DefaultExecutionContextFactory() {
	}

	public void closeResources() {
		for (final DefaultExecutionContext openedContext : openedContexts) {
			openedContext.closeResources();
		}
	}

	public ExecutionContext createExecutionContext(final String username,
			final String password, final JcrConnectionDescriptor descriptor,
			final String repositoryName) {
		final DefaultExecutionContext newContext = new DefaultExecutionContext(
				username, password, descriptor, repositoryName, this);
		openedContexts.add(newContext);
		return newContext;
	}

	public void didCloseResource(final DefaultExecutionContext context) {
		openedContexts.remove(context);
	}

}
