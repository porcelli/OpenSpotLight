package org.openspotlight.federation.processing.internal;

public interface RunnableWithBundleContext extends Runnable {

    public void setBundleContext( BundleProcessorContextImpl context );

}
