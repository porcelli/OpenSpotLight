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

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.openspotlight.common.concurrent.Lock;
import org.openspotlight.common.concurrent.LockContainer;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.graph.annotation.SLVisibility;
import org.openspotlight.graph.annotation.SLVisibility.VisibilityLevel;

/**
 * The Class SLNodeInvocationHandler.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLNodeInvocationHandler implements InvocationHandler, LockContainer {

    /** The node. */
    private final SLNode node;

    private final Lock   lock;

    /**
     * Instantiates a new sL node invocation handler.
     * 
     * @param node the node
     */
    public SLNodeInvocationHandler(
                                    final SLNode node ) {
        Assertions.checkNotNull("node", node);
        this.node = node;
        lock = node.getLockObject();
    }

    public Lock getLockObject() {
        return lock;
    }

    /**
     * Gets the node.
     * 
     * @return the node
     */
    public SLNode getNode() {
        return node;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
     * java.lang.reflect.Method, java.lang.Object[])
     */
    @SuppressWarnings( "unchecked" )
    // @Override
    public Object invoke( final Object proxy,
                          final Method method,
                          final Object[] args ) throws Throwable {
        synchronized (lock) {
            Object result = null;
            if (!method.getDeclaringClass().equals(SLNode.class) && SLNode.class.isAssignableFrom(method.getDeclaringClass())) {
                if (SLInvocationHandlerSupport.isGetter(proxy, method)) {
                    final String propName = SLInvocationHandlerSupport.getPropertyName(method);
                    final Class<? extends Serializable> typeClass = (Class<? extends Serializable>)method.getReturnType();
                    final SLNodeProperty<? extends Serializable> property = node.getProperty(typeClass, propName);
                    result = property != null ? property.getValue() : null;
                } else if (SLInvocationHandlerSupport.isSetter(proxy, method)) {
                    final Method getterMethod = method.getDeclaringClass().getMethod("get" + method.getName().substring(3));
                    VisibilityLevel visibilityLevel = VisibilityLevel.PUBLIC;
                    if (getterMethod != null) {
                        final SLVisibility visibilityAnnotation = getterMethod.getAnnotation(SLVisibility.class);
                        if (visibilityAnnotation != null) {
                            visibilityLevel = visibilityAnnotation.value();
                        }
                    }

                    final String propName = SLInvocationHandlerSupport.getPropertyName(method);
                    node.setProperty(Serializable.class, visibilityLevel, propName, (Serializable)args[0]);
                }
            } else {
                result = SLInvocationHandlerSupport.invokeMethod(node, method, args);
            }
            return result;

        }
    }

}
