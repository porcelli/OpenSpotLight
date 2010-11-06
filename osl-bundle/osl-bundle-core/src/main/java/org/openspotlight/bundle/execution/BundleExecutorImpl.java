package org.openspotlight.bundle.execution;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.openspotlight.bundle.context.ExecutionContextFactory;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    public BundleExecutorImpl(ExecutionContextFactory factory, @NumberOfThreads int numberOfThreads) {
        this.factory = factory;
        this.executor = Executors.newFixedThreadPool(numberOfThreads);
    }


    @Override
    public void execute(Collection<Callable<Void>> tasks) throws InterruptedException {
        this.executor.invokeAll(tasks);
    }
}
