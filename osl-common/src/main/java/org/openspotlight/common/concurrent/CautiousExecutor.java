package org.openspotlight.common.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The Class CautiousExecutor.
 */
public class CautiousExecutor extends ThreadPoolExecutor {

    /**
     * A factory for creating DelegateThread objects.
     */
    private static class DelegateThreadFactory implements ThreadFactory {

        /** The listeners. */
        private final CopyOnWriteArrayList<ThreadListener> listeners = new CopyOnWriteArrayList<ThreadListener>();

        /** The wrapped. */
        private final ThreadFactory                        wrapped;

        /**
         * Instantiates a new delegate thread factory.
         *
         * @param wrapped the wrapped
         */
        public DelegateThreadFactory(
                                      final ThreadFactory wrapped ) {
            this.wrapped = wrapped;
        }

        /**
         * Adds the thread listener.
         *
         * @param l the l
         */
        public void addThreadListener( final ThreadListener l ) {
            this.listeners.add(l);
        }

        /* (non-Javadoc)
         * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
         */
        public Thread newThread( final Runnable r ) {
            final Thread t = this.wrapped.newThread(r);
            for (final ThreadListener l : this.listeners) {
                l.afterCreatingThread(t);
            }
            return t;
        }

        /**
         * Removes the thread listener.
         *
         * @param l the l
         */
        public void removeThreadListener( final ThreadListener l ) {
            this.listeners.remove(l);
        }
    }

    /**
     * The listener interface for receiving task events. The class that is interested in processing a task event implements this
     * interface, and the object created with that class is registered with a component using the component's
     * <code>addTaskListener<code> method. When
     * the task event occurs, that object's appropriate
     * method is invoked.
     *
     * @see TaskEvent
     */
    public static interface TaskListener {

        /**
         * After executing task.
         *
         * @param r the r
         * @param t the t
         */
        public void afterExecutingTask( Runnable r,
                                        Throwable t );

        /**
         * Before executing task.
         *
         * @param t the t
         * @param r the r
         */
        public void beforeExecutingTask( Thread t,
                                         Runnable r );
    }

    /**
     * The listener interface for receiving thread events. The class that is interested in processing a thread event implements
     * this interface, and the object created with that class is registered with a component using the component's
     * <code>addThreadListener<code> method. When
     * the thread event occurs, that object's appropriate
     * method is invoked.
     *
     * @see ThreadEvent
     */
    public static interface ThreadListener {

        /**
         * After creating thread.
         *
         * @param t the t
         */
        public void afterCreatingThread( Thread t );
    }

    /**
     * New fixed thread pool.
     *
     * @param nThreads the n threads
     * @return the cautious executor
     */
    public static CautiousExecutor newFixedThreadPool( final int nThreads ) {

        final CautiousExecutor ex = new CautiousExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
                                                         new LinkedBlockingQueue<Runnable>());
        return ex;

    }

    /** The listeners. */
    private final CopyOnWriteArrayList<TaskListener> listeners = new CopyOnWriteArrayList<TaskListener>();

    /** The delegate thread factory. */
    private final DelegateThreadFactory              delegateThreadFactory;

    /**
     * Instantiates a new cautious executor.
     *
     * @param corePoolSize the core pool size
     * @param maximumPoolSize the maximum pool size
     * @param keepAliveTime the keep alive time
     * @param unit the unit
     * @param workQueue the work queue
     */
    private CautiousExecutor(
                              final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit,
                              final BlockingQueue<Runnable> workQueue ) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.delegateThreadFactory = new DelegateThreadFactory(this.getThreadFactory());
        this.setThreadFactory(this.delegateThreadFactory);

    }

    /**
     * Adds the task listener.
     *
     * @param l the l
     */
    public void addTaskListener( final TaskListener l ) {
        this.listeners.add(l);
    }

    /**
     * Adds the thread listener.
     *
     * @param l the l
     */
    public void addThreadListener( final ThreadListener l ) {
        this.delegateThreadFactory.addThreadListener(l);
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.ThreadPoolExecutor#afterExecute(java.lang.Runnable, java.lang.Throwable)
     */
    @Override
    protected void afterExecute( final Runnable r,
                                 final Throwable t ) {
        for (final TaskListener l : this.listeners) {
            l.afterExecutingTask(r, t);
        }
        super.afterExecute(r, t);
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.ThreadPoolExecutor#beforeExecute(java.lang.Thread, java.lang.Runnable)
     */
    @Override
    protected void beforeExecute( final Thread t,
                                  final Runnable r ) {
        for (final TaskListener l : this.listeners) {
            l.beforeExecutingTask(t, r);
        }
    }

    /**
     * Removes the task listener.
     *
     * @param l the l
     */
    public void removeTaskListener( final TaskListener l ) {
        this.listeners.remove(l);
    }

    /**
     * Removes the thread listener.
     *
     * @param l the l
     */
    public void removeThreadListener( final ThreadListener l ) {
        this.delegateThreadFactory.removeThreadListener(l);
    }

}
