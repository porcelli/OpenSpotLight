package org.openspotlight.federation.processing.internal;

import org.openspotlight.federation.processing.internal.domain.BundleProcessorContextImpl;

public interface RunnableWithBundleContext extends Runnable {
    public void setBundleContext( BundleProcessorContextImpl context );
}
