package org.openspotlight.federation.processing.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openspotlight.common.concurrent.GossipExecutor.TaskListener;
import org.openspotlight.common.concurrent.GossipExecutor.ThreadListener;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.context.ExecutionContextFactory;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;

public class BundleContextThreadInjector implements ThreadListener,
		TaskListener {

	private final ConcurrentHashMap<Thread, Map<String, ExecutionContext>> contextsPerThread = new ConcurrentHashMap<Thread, Map<String, ExecutionContext>>();

	private final ExecutionContextFactory factory;

	private final String[] repositoryNames;

	private final String username;
	private final String password;
	private final JcrConnectionDescriptor descriptor;

	public BundleContextThreadInjector(final ExecutionContextFactory factory,
			final String[] repositoryNames, final String username,
			final String password, final JcrConnectionDescriptor descriptor) {
		this.factory = factory;
		this.repositoryNames = repositoryNames;
		this.descriptor = descriptor;
		this.username = username;
		this.password = password;
	}

	public void afterCreatingThread(final Thread t) {
		try {
			final Map<String, ExecutionContext> executionContextMap = new HashMap<String, ExecutionContext>();
			for (final String repositoryName : repositoryNames) {
				final ExecutionContext executionContext = factory
						.createExecutionContext(username, password, descriptor,
								repositoryName);
				executionContextMap.put(repositoryName, executionContext);
			}
			contextsPerThread.put(t, executionContextMap);
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	public void afterExecutingTask(final Runnable r, final Throwable t) {
		// thats ok. lets see gc do its job ;-)
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openspotlight.common.concurrent.CautiousExecutor.TaskListener#
	 * beforeExecutingTask(java.lang.Thread, java.lang.Runnable)
	 */
	public void beforeExecutingTask(final Thread t, final Runnable r) {
		if (r instanceof RunnableWithBundleContext) {
			try {
				final RunnableWithBundleContext rwbc = (RunnableWithBundleContext) r;
				final Map<String, ExecutionContext> ctx = contextsPerThread
						.get(t);
				rwbc.setBundleContext(ctx);
			} catch (final Exception e) {
				throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
			}
		}

	}

}
