package org.openspotlight.federation.processing.internal;

import java.util.concurrent.ConcurrentHashMap;

import org.openspotlight.common.concurrent.CautiousExecutor.TaskListener;
import org.openspotlight.common.concurrent.CautiousExecutor.ThreadListener;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;

public class BundleContextThreadInjector implements ThreadListener, TaskListener {

    private final ConcurrentHashMap<Thread, BundleProcessorContextImpl> contextsPerThread = new ConcurrentHashMap<Thread, BundleProcessorContextImpl>();

    private final BundleProcessorContextFactory                         factory;

    public BundleContextThreadInjector(
                                        final BundleProcessorContextFactory factory ) {
        this.factory = factory;
    }

    public void afterCreatingThread( final Thread t ) {
        try {
            this.contextsPerThread.put(t, this.factory.createBundleContext());
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    public void afterExecutingTask( final Runnable r,
                                    final Throwable t ) {
        //thats ok. lets see gc do its job ;-)
    }

    public void beforeExecutingTask( final Thread t,
                                     final Runnable r ) {
        if (r instanceof RunnableWithBundleContext) {
            final RunnableWithBundleContext rwbc = (RunnableWithBundleContext)r;
            ((RunnableWithBundleContext)r).setBundleContext(this.contextsPerThread.get(t));
        }

    }

}
