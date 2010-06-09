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
package org.openspotlight.common.util;

import static org.openspotlight.common.util.reflection.MethodIdentificationSupport.getMethodUniqueName;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.openspotlight.common.util.reflection.MethodIdentificationSupport.MethodWithParametersKey;
import org.openspotlight.common.util.reflection.MethodIdentificationSupport.UseEnhanced;

/**
 * This factory is used to newPair lazy behavior on method invocations. For the first method invocation with some of the
 * parameters, the target method is called, and its response is cached. So, on the next invocations with the same parameters, the
 * result is returned from the cache. It is possible to instantiate the new cached instance if there's a default constructor. Also
 * it's possible to call any constructor. The only restriction is: the target class MUST BE NON FINAL. It caches returned objects
 * and also thrown exceptions. It caches only public method invocations.
 * 
 * @author feu
 */
public final class InvocationCacheFactory {

    /**
     * Internal cache factory class. It uses CGLib inside to subclass the target class.
     * 
     * @author feu
     */
    private static class CachedInterceptor implements MethodInterceptor {

        /**
         * This class is used to wrap thrown exceptions.
         */
        private static final class ThrowableWrapped {

            /** The throwable. */
            final Throwable throwable;

            /**
             * Instantiates a new throwable wrapped.
             * 
             * @param toWrap the to wrap
             */
            public ThrowableWrapped(
                                     final Throwable toWrap ) {
                this.throwable = toWrap;

            }
        }

        /** The use enhanced method. */
        private final UseEnhanced                          useEnhancedMethod;

        /** The source. */
        private Object                                     source;

        /** The cache. */
        private final Map<MethodWithParametersKey, Object> cache      = new HashMap<MethodWithParametersKey, Object>();

        /** The Constant NULL_VALUE. */
        private static final Object                        NULL_VALUE = new Object();

        /** The Constant VOID_VALUE. */
        private static final Object                        VOID_VALUE = new Object();

        /**
         * Instantiates a new cached interceptor using the behavior described on {@link UseEnhanced}.
         * 
         * @param useEnhancedMethod the use enhanced method
         */
        public CachedInterceptor(
                                  final UseEnhanced useEnhancedMethod ) {
            this.useEnhancedMethod = useEnhancedMethod;
        }

        public Object intercept( final Object enhancedTarget,
                                 final Method method,
                                 final Object[] parameters,
                                 final MethodProxy proxy ) throws Throwable {
            if (Modifier.isPublic(method.getModifiers())) {
                final String uniqueName = getMethodUniqueName(method);
                final MethodWithParametersKey key = new MethodWithParametersKey(uniqueName, parameters);
                Object value = this.cache.get(key);
                if (value == null) {
                    boolean invocationOk = true;
                    try {
                        value = this.invoke(method, parameters, proxy);
                    } catch (final InvocationTargetException e) {
                        invocationOk = false;
                        final Throwable toWrap = e.getTargetException();
                        this.cache.put(key, new ThrowableWrapped(toWrap));
                    } catch (final Throwable t) {
                        invocationOk = false;
                        this.cache.put(key, new ThrowableWrapped(t));
                    }
                    if (invocationOk) {
                        if (method.getReturnType().equals(Void.TYPE)) {
                            this.cache.put(key, VOID_VALUE);
                        } else if (value == null) {
                            this.cache.put(key, NULL_VALUE);
                        } else {
                            this.cache.put(key, value);
                        }
                    }
                    value = this.cache.get(key);
                }

                if (value == VOID_VALUE || value == NULL_VALUE) {
                    return null;
                } else if (value instanceof ThrowableWrapped) {
                    final ThrowableWrapped wrapped = (ThrowableWrapped)value;
                    throw wrapped.throwable;
                }
                return value;
            }
            try {
                final Object value = this.invoke(method, parameters, proxy);

                return value;
            } catch (final InvocationTargetException e) {
                throw e.getTargetException();
            }

        }

        /**
         * Invoke the method itself.
         * 
         * @param method the method
         * @param parameters the parameters
         * @param proxy the proxy
         * @return the object
         * @throws Throwable the throwable
         * @throws IllegalAccessException the illegal access exception
         * @throws InvocationTargetException the invocation target exception
         */
        private Object invoke( final Method method,
                               final Object[] parameters,
                               final MethodProxy proxy ) throws Throwable, IllegalAccessException, InvocationTargetException {
            Object value = null;
            switch (this.useEnhancedMethod) {
                case USE_ENHANCED:
                    value = proxy.invokeSuper(this.source, parameters);
                    break;
                case USE_WRAPPED:
                    value = method.invoke(this.source, parameters);
                    break;
            }
            return value;
        }

        /**
         * Sets the source.
         * 
         * @param source the new source
         */
        public void setSource( final Object source ) {
            this.source = source;
        }

    }

    /**
     * Creates a new InvocationCache object.
     * 
     * @param <T> the type been subclassed.
     * @param superClass the super class
     * @param argumentTypes the argument types
     * @param arguments the arguments
     * @return the T
     */
    public static <T> T createIntoCached( final Class<T> superClass,
                                          final Class<?>[] argumentTypes,
                                          final Object[] arguments ) {
        final CachedInterceptor interceptor = new CachedInterceptor(UseEnhanced.USE_ENHANCED);
        final Enhancer e = new Enhancer();
        e.setSuperclass(superClass);
        e.setCallback(interceptor);
        @SuppressWarnings( "unchecked" )
        final T wrapped = (T)e.create(argumentTypes, arguments);
        interceptor.setSource(wrapped);
        return wrapped;
    }

    /**
     * Wrap into cached.
     * 
     * @param <T> the type been subclassed.
     * @param toWrap the to wrap
     * @return the t
     */
    public static <T> T wrapIntoCached( final T toWrap ) {
        final CachedInterceptor interceptor = new CachedInterceptor(UseEnhanced.USE_WRAPPED);
        interceptor.setSource(toWrap);
        final Enhancer e = new Enhancer();
        e.setSuperclass(toWrap.getClass());
        e.setCallback(interceptor);
        @SuppressWarnings( "unchecked" )
        final T wrapped = (T)e.create();
        return wrapped;
    }

    /**
     * do not newPair a new cache factory.
     */
    private InvocationCacheFactory() {
        throw new UnsupportedOperationException();
    }

}
