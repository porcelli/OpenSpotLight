/*
 * OpenSpotLight - Open Source IT Governance Platform
 *
 * Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA
 * or third-party contributors as indicated by the @author tags or express
 * copyright attribution statements applied by the authors.  All third-party
 * contributions are distributed under license by CARAVELATECH CONSULTORIA E
 * TECNOLOGIA EM INFORMATICA LTDA.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License  for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 * Todas as contribuições de terceiros estão distribuídas sob licença da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA.
 *
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software
 * Foundation.
 *
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 * FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 * programa; se não, escreva para:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.openspotlight.common.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The Class GossipExecutor has this name because it stay telling every listener what is going on ;-).
 */
public class GossipExecutor extends ThreadPoolExecutor {

    /**
     * A factory for creating DelegateThread objects.
     */
    private static class DelegateThreadFactory implements ThreadFactory {

        /** The listeners. */
        private final CopyOnWriteArrayList<ThreadListener> listeners = new CopyOnWriteArrayList<ThreadListener>();

        /** The wrapped. */
        private final ThreadFactory                        wrapped;

        private final String                               poolName;

        /**
         * Instantiates a new delegate thread factory.
         * 
         * @param wrapped the wrapped
         */
        public DelegateThreadFactory(
                                      final ThreadFactory wrapped,
                                      final String poolName ) {
            this.wrapped = wrapped;
            this.poolName = poolName;
        }

        /**
         * Adds the thread listener.
         * 
         * @param l the l
         */
        public void addThreadListener( final ThreadListener l ) {
            listeners.add(l);
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
         */
        public Thread newThread( final Runnable r ) {
            final Thread t = wrapped.newThread(r);
            t.setName(poolName + "_" + t.getName());
            for (final ThreadListener l : listeners) {
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
            listeners.remove(l);
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
    public static GossipExecutor newFixedThreadPool( final int nThreads,
                                                     final String poolName ) {

        final GossipExecutor ex = new GossipExecutor(nThreads, nThreads, 0L,
                                                     TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
                                                     poolName);
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
    private GossipExecutor(
                            final int corePoolSize, final int maximumPoolSize,
                            final long keepAliveTime, final TimeUnit unit,
                            final BlockingQueue<Runnable> workQueue, final String poolName ) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        delegateThreadFactory = new DelegateThreadFactory(getThreadFactory(),
                                                          poolName);
        setThreadFactory(delegateThreadFactory);

    }

    /**
     * Adds the task listener.
     * 
     * @param l the l
     */
    public void addTaskListener( final TaskListener l ) {
        listeners.add(l);
    }

    /**
     * Adds the thread listener.
     * 
     * @param l the l
     */
    public void addThreadListener( final ThreadListener l ) {
        delegateThreadFactory.addThreadListener(l);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.util.concurrent.ThreadPoolExecutor#afterExecute(java.lang.Runnable,
     * java.lang.Throwable)
     */
    @Override
    protected void afterExecute( final Runnable r,
                                 final Throwable t ) {
        for (final TaskListener l : listeners) {
            l.afterExecutingTask(r, t);
        }
        super.afterExecute(r, t);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.util.concurrent.ThreadPoolExecutor#beforeExecute(java.lang.Thread,
     * java.lang.Runnable)
     */
    @Override
    protected void beforeExecute( final Thread t,
                                  final Runnable r ) {
        for (final TaskListener l : listeners) {
            l.beforeExecutingTask(t, r);
        }
    }

    /**
     * Removes the task listener.
     * 
     * @param l the l
     */
    public void removeTaskListener( final TaskListener l ) {
        listeners.remove(l);
    }

    /**
     * Removes the thread listener.
     * 
     * @param l the l
     */
    public void removeThreadListener( final ThreadListener l ) {
        delegateThreadFactory.removeThreadListener(l);
    }

}
