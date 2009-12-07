package org.openspotlight.federation.processing.internal;

import org.openspotlight.federation.context.ExecutionContext;

public interface RunnableWithBundleContext extends Runnable {
	public void setBundleContext(ExecutionContext context);
}
