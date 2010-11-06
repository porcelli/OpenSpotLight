package org.openspotlight.bundle.execution;

import com.google.inject.AbstractModule;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Nov 6, 2010
 * Time: 2:57:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class BundleModule extends AbstractModule{
    private final int numberOfThreads;

    public BundleModule(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }


    @Override
    protected void configure() {
        bind(int.class).annotatedWith(NumberOfThreads.class).toInstance(numberOfThreads);
        bind(BundleExecutor.class).to(BundleExecutorImpl.class);
    }
}
