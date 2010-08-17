/**
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

package org.openspotlight.common.util;

import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class wraps object instances from a given interface inside a timeout object. This timeout object will run a background
 * thread to monitor how many time the wrapped object wasn't invoked. If this time is so long, it will finalize this object,
 * calling a method decribed inside an interface called {@link TaskFinalizer}.
 * 
 * @author feu
 */
public final class TimeOutFactory {

    /**
     * This interface will be used when the monitored object reaches the time out.
     * 
     * @param <T> object type
     * @author feu
     */
    public static interface TaskFinalizer<T> {

        /**
         * Finalize task.
         * 
         * @param target the target
         */
        public void finalizeTask(T target);
    }

    /**
     * This Java Proxy {@link InvocationHandler} will start a background thread to monitor if the wrapped object reaches the time
     * out or not. And this invocation handler will update the {@link #lastInvocation} attribute each time it is invoked.
     * 
     * @author feu
     * @param <T> object type
     */
    private static class TimeOutCacheInvocationHandler<T> implements InvocationHandler {

        /**
         * The Class TimerRunnable will watch to see if the time out was reached.
         */
        class TimerRunnable implements Runnable {

            @Override
            public void run() {
                if (TimeOutCacheInvocationHandler.this.invalid.get()) { return; }
                while (true) {
                    if (TimeOutCacheInvocationHandler.this.lastInvocation.get() < System.currentTimeMillis()
                                                                                  - TimeOutCacheInvocationHandler.this.intervalInMilliseconds) {
                        TimeOutCacheInvocationHandler.this.taskFinalizer.finalizeTask(TimeOutCacheInvocationHandler.this.wrapped);
                        TimeOutCacheInvocationHandler.this.invalid.set(true);
                        return;
                    }
                    try {
                        Thread.sleep(TimeOutCacheInvocationHandler.this.intervalInMilliseconds / 2);
                    } catch (final InterruptedException e) {
                        // ok, nothing to do here
                    }
                }

            }

        }

        /** The invalid. */
        final AtomicBoolean    invalid        = new AtomicBoolean(false);

        /** The interval in milliseconds. */
        final long             intervalInMilliseconds;

        /** The last invocation. */
        final AtomicLong       lastInvocation = new AtomicLong();

        /** The task finalizer. */
        final TaskFinalizer<T> taskFinalizer;

        /** The timer runnable. */
        final Runnable         timerRunnable  = new TimerRunnable();

        /** The wrapped. */
        final T                wrapped;

        /**
         * Instantiates a new time out cache interceptor.
         * 
         * @param intervalInMilliseconds the interval in milliseconds
         * @param wrapped the wrapped
         * @param taskFinalizer the task finalizer
         */
        TimeOutCacheInvocationHandler(
                                       final long intervalInMilliseconds, final T wrapped, final TaskFinalizer<T> taskFinalizer) {
            this.wrapped = wrapped;
            this.intervalInMilliseconds = intervalInMilliseconds;
            this.taskFinalizer = taskFinalizer;
            this.lastInvocation.set(System.currentTimeMillis());
            new Thread(this.timerRunnable).start();
        }

        @Override
        public Object invoke(final Object proxy,
                              final Method method,
                              final Object[] args)
            throws Throwable {
            if (this.invalid.get()) { throw new IllegalStateException(); }
            this.lastInvocation.set(System.currentTimeMillis());
            try {
                return method.invoke(this.wrapped, args);
            } catch (final InvocationTargetException e) {
                throw e;
            } catch (final Exception e) {
                throw e;
            } finally {
                this.lastInvocation.set(System.currentTimeMillis());
            }
        }

    }

    /**
     * Creates a new timed out object.
     * 
     * @param <T> Object type
     * @param interfaceType the interface type
     * @param intervalInMilliseconds the interval in milliseconds
     * @param wrapped the wrapped
     * @param taskFinalizer the task finalizer
     * @return the T
     */
    @SuppressWarnings("unchecked")
    public static <T> T createTimedOutObject(final Class<T> interfaceType,
                                              final long intervalInMilliseconds,
                                              final T wrapped,
                                              final TaskFinalizer<T> taskFinalizer) {

        checkNotNull("interfaceType", interfaceType);
        checkNotNull("wrapped", wrapped);
        checkNotNull("taskFinalizer", taskFinalizer);
        checkCondition("typeIsInterface", interfaceType.isInterface());
        checkCondition("intervalBiggerThanZero", intervalInMilliseconds > 0l);

        final TimeOutCacheInvocationHandler<T> interceptor = new TimeOutCacheInvocationHandler<T>(intervalInMilliseconds,
                                                                                                  wrapped, taskFinalizer);

        final T proxy = (T) Proxy.newProxyInstance(wrapped.getClass().getClassLoader(), new Class<?>[] {interfaceType},
                                                  interceptor);

        return proxy;
    }

}
