package org.openspotlight.federation.processing.internal;

public interface RunnableWithBundleContext extends Runnable {

    public CurrentProcessorContextImpl getCurrentContext();

    public void setBundleContext( BundleProcessorContextImpl context );

}
