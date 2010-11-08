package org.openspotlight.bundle.execution;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openspotlight.bundle.context.ExecutionContextFactory;
import org.openspotlight.federation.loader.ConfigurationManagerFactory;
import org.openspotlight.federation.loader.ImmutableConfigurationManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Nov 6, 2010
 * Time: 2:37:46 PM
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class BundleExecutorImpl implements BundleExecutor {

    private final ExecutionContextFactory factory;

    private final ExecutorService executor;


    @Inject
    public BundleExecutorImpl(ExecutionContextFactory factory, ConfigurationManagerFactory configurationManagerFactory) {
        this.factory = factory;
        ImmutableConfigurationManager configurationManager = configurationManagerFactory.createImmutable();
        int numberOfThreads = configurationManager.getGlobalSettings().getParallelThreads();
        configurationManager.closeResources();
        this.executor = Executors.newFixedThreadPool(numberOfThreads);
    }


    @Override
    public void execute(Collection<Callable<Void>> tasks) throws InterruptedException {
        this.executor.invokeAll(tasks);
    }
}
