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
package org.openspotlight.jcr.util;

import java.io.InputStream;
import java.io.Serializable;
import java.util.StringTokenizer;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;

import org.openspotlight.common.exception.JCRUtilException;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.SerializationUtil;
import org.openspotlight.jcr.provider.SessionWithLock;

/**
 * The Class JCRUtil.
 * 
 * @author Vitor Hugo Chagas
 */
public class JCRUtil {

    /**
     * Creates the value.
     * 
     * @param session the session
     * @param value the value
     * @return the value
     * @throws JCRUtilException the JCR util exception
     */
    public static Value createValue( final Session session,
                                     final Object value ) throws JCRUtilException {
        try {
            Value jcrValue = null;
            final ValueFactory factory = session.getValueFactory();
            if (value.getClass().equals(Integer.class) || value.getClass().equals(Long.class)) {
                final Number number = Number.class.cast(value);
                jcrValue = factory.createValue(number.longValue());
            } else if (value.getClass().equals(Float.class) || value.getClass().equals(Double.class)) {
                final Number number = Number.class.cast(value);
                jcrValue = factory.createValue(number.doubleValue());
            } else if (value.getClass().equals(String.class)) {
                jcrValue = factory.createValue(String.class.cast(value));
            } else if (value.getClass().equals(Boolean.class)) {
                jcrValue = factory.createValue(Boolean.class.cast(value));
            } else {
                final InputStream inputStream = SerializationUtil.serialize(value);
                jcrValue = factory.createValue(inputStream);
            }
            return jcrValue;
        } catch (final Exception e) {
            throw new JCRUtilException("Error on attempt to newPair value.", e);
        }
    }

    /**
     * Creates the values.
     * 
     * @param session the session
     * @param value the value
     * @return the value[]
     * @throws JCRUtilException the JCR util exception
     */
    public static Value[] createValues( final Session session,
                                        final Object value ) throws JCRUtilException {
        try {
            Value[] jcrValues = null;
            final ValueFactory factory = session.getValueFactory();
            if (value.getClass().equals(Integer[].class) || value.getClass().equals(Long[].class)) {
                final Long[] arr = (Long[])value;
                jcrValues = new Value[arr.length];
                for (int i = 0; i < arr.length; i++) {
                    jcrValues[i] = factory.createValue(arr[i]);
                }
            } else if (value.getClass().equals(Float[].class) || value.getClass().equals(Double[].class)) {
                final Float[] arr = (Float[])value;
                jcrValues = new Value[arr.length];
                for (int i = 0; i < arr.length; i++) {
                    jcrValues[i] = factory.createValue(arr[i]);
                }
            } else if (value.getClass().equals(String[].class)) {
                final String[] arr = (String[])value;
                jcrValues = new Value[arr.length];
                for (int i = 0; i < jcrValues.length; i++) {
                    jcrValues[i] = factory.createValue(arr[i]);
                }
            } else if (value.getClass().equals(Boolean[].class)) {
                final Boolean[] arr = (Boolean[])value;
                jcrValues = new Value[arr.length];
                for (int i = 0; i < jcrValues.length; i++) {
                    jcrValues[i] = factory.createValue(arr[i]);
                }
            } else {
                final Serializable[] arr = (Serializable[])value;
                jcrValues = new Value[arr.length];
                for (int i = 0; i < jcrValues.length; i++) {
                    final InputStream inputStream = SerializationUtil.serialize(arr[i]);
                    jcrValues[i] = factory.createValue(inputStream);
                }
            }
            return jcrValues;

        } catch (final Exception e) {
            throw new JCRUtilException("Error on attempt to newPair value array.", e);
        }
    }

    /**
     * Gets the child node.
     * 
     * @param node the node
     * @param name the name
     * @return the child node
     * @throws RepositoryException the repository exception
     */
    public static Node getChildNode( final Node node,
                                     final String name ) throws RepositoryException {
        try {
            return node.getNode(name);
        } catch (final PathNotFoundException e) {
        }
        return null;
    }

    public static Node getOrCreateByPath( final Session session,
                                          final Node parentNode,
                                          final String pathWithSlashes ) {
        if (session instanceof SessionWithLock) {
            final SessionWithLock sessionWithLock = (SessionWithLock)session;
            synchronized (sessionWithLock.getLockObject()) {
                return internalGetOrCreateByPath(sessionWithLock, parentNode, pathWithSlashes);
            }
        }
        return internalGetOrCreateByPath(session, parentNode, pathWithSlashes);

    }

    /**
     * Gets the parent.
     * 
     * @param node the node
     * @return the parent
     * @throws RepositoryException the repository exception
     */
    public static Node getParent( final Node node ) throws RepositoryException {
        Node parent = null;
        try {
            parent = node.getParent();
        } catch (final ItemNotFoundException e) {
        }
        return parent;
    }

    private static Node internalGetOrCreateByPath( final Session session,
                                                   final Node parentNode,
                                                   final String pathWithSlashes ) {
        try {
            Node newParent = parentNode;
            final StringTokenizer tok = new StringTokenizer(pathWithSlashes, "/");
            while (tok.hasMoreTokens()) {
                final String currentToken = tok.nextToken();
                if (currentToken.length() == 0) {
                    continue;
                }
                try {
                    newParent = newParent.getNode(currentToken);
                } catch (final PathNotFoundException e) {
                    newParent = newParent.addNode(currentToken);
                }

            }
            return newParent;
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    /**
     * Make referenceable.
     * 
     * @param node the node
     * @throws RepositoryException the repository exception
     */
    public static void makeReferenceable( final Node node ) throws RepositoryException {
        node.addMixin("mix:referenceable");
    }

    /**
     * Make versionable.
     * 
     * @param node the node
     * @throws RepositoryException the repository exception
     */
    public static void makeVersionable( final Node node ) throws RepositoryException {
        node.addMixin("mix:versionable");
    }
}
