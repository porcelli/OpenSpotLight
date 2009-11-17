package org.openspotlight.common.concurrent.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.openspotlight.common.concurrent.CautiousExecutor;
import org.openspotlight.common.concurrent.CautiousExecutor.TaskListener;
import org.openspotlight.common.concurrent.CautiousExecutor.ThreadListener;

public class CautiousExecutorTest {

    public static class CustomRunnable implements Runnable {

        public void run() {

        }

    }

    private static class CustomTaskListener implements TaskListener {

        final AtomicInteger integer = new AtomicInteger();

        public void afterExecutingTask( final Runnable r,
                                        final Throwable t ) {
            if (r instanceof CustomRunnable) {
                this.integer.incrementAndGet();
            }

        }

        public void beforeExecutingTask( final Thread t,
                                         final Runnable r ) {
            // TODO Auto-generated method stub

        }

    }

    private static class CustomThreadListener implements ThreadListener {

        final AtomicInteger integer = new AtomicInteger();

        public void afterCreatingThread( final Thread t ) {
            this.integer.incrementAndGet();

        }
    };

    private final CustomThreadListener threadListener = new CustomThreadListener();
    private final CustomTaskListener   taskListener   = new CustomTaskListener();
    private CautiousExecutor           executor;

    @Test
    public void setup() throws Exception {

        this.executor = CautiousExecutor.newFixedThreadPool(4);
        this.executor.addTaskListener(this.taskListener);
        this.executor.addThreadListener(this.threadListener);
        for (int i = 0; i < 100; i++) {
            this.executor.execute(new CustomRunnable());
        }
        Thread.sleep(100);

        this.executor.shutdown();

        assertThat(this.taskListener.integer.get(), is(100));

        assertThat(this.threadListener.integer.get(), is(4));
    }

}
