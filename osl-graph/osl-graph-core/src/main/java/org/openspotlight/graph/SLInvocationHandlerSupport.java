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
package org.openspotlight.graph;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.graph.annotation.SLProperty;

/**
 * The Class SLInvocationHandlerSupport.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLInvocationHandlerSupport {

    /**
     * Gets the property name.
     * 
     * @param method the method
     * @return the property name
     */
    static String getPropertyName( final Method method ) {
        return method.getName().substring(3, 4).toLowerCase().concat(
                                                                     method.getName().substring(4));
    }

    /**
     * Invoke method.
     * 
     * @param object the object
     * @param method the method
     * @param args the args
     * @return the object
     * @throws Throwable the throwable
     */
    static Object invokeMethod( final Object object,
                                final Method method,
                                final Object[] args ) throws Throwable {
        try {
            return method.invoke(object, args);
        } catch (final InvocationTargetException e) {
            throw e.getTargetException();
        } catch (final Exception e) {
            Exceptions.catchAndLog(e);
            throw new SLRuntimeException("Error on node proxy.", e);
        }
    }

    /**
     * Checks if is getter.
     * 
     * @param proxy the proxy
     * @param method the method
     * @return true, if is getter
     */
    static boolean isGetter( final Object proxy,
                             final Method method ) {
        try {
            boolean status = false;
            if (method.getName().startsWith("get")
                    && !method.getReturnType().equals(void.class)
                    && method.getParameterTypes().length == 0) {
                final SLProperty propertyAnnotation = method
                                                            .getAnnotation(SLProperty.class);
                if (propertyAnnotation == null) {
                    try {
                        final String setterName = "set".concat(method.getName()
                                                                     .substring(3));
                        final Class<?> iFace = proxy.getClass().getInterfaces()[0];
                        final Method setterMethod = iFace.getMethod(setterName,
                                                                    new Class<?>[] {method.getReturnType()});
                        status = setterMethod.getAnnotation(SLProperty.class) != null
                                 && setterMethod.getReturnType().equals(
                                                                        void.class);
                    } catch (final NoSuchMethodException e) {
                    }
                } else {
                    status = true;
                }
            }
            return status;
        } catch (final Exception e) {
            throw new SLRuntimeException(
                                         "Error on attempt to verify if method is getter.", e);
        }
    }

    /**
     * Checks if is setter.
     * 
     * @param proxy the proxy
     * @param method the method
     * @return true, if is setter
     */
    static boolean isSetter( final Object proxy,
                             final Method method ) {
        try {
            boolean status = false;
            if (method.getName().startsWith("set")
                    && method.getReturnType().equals(void.class)
                    && method.getParameterTypes().length == 1) {
                final SLProperty propertyAnnotation = method
                                                            .getAnnotation(SLProperty.class);
                if (propertyAnnotation == null) {
                    try {
                        final String getterName = "get".concat(method.getName()
                                                                     .substring(3));
                        final Class<?> iFace = proxy.getClass().getInterfaces()[0];
                        final Method getterMethod = iFace.getMethod(getterName,
                                                                    new Class<?>[] {});
                        status = getterMethod.getAnnotation(SLProperty.class) != null
                                 && getterMethod.getReturnType().equals(
                                                                        method.getParameterTypes()[0]);
                    } catch (final NoSuchMethodException e) {
                    }
                } else {
                    status = true;
                }
            }
            return status;
        } catch (final Exception e) {
            throw new SLRuntimeException(
                                         "Error on attempt to verify if method is setter.", e);
        }
    }

}
