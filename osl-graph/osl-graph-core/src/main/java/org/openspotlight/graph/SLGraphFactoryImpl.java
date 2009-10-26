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

import static org.openspotlight.common.util.Exceptions.logAndReturnNew;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openspotlight.common.exception.AbstractFactoryException;
import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentTree;
import org.openspotlight.graph.persistence.SLPersistentTreeFactory;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;

/**
 * The Class SLGraphFactoryImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLGraphFactoryImpl extends SLGraphFactory {

    private SLGraph                                     graph;

    private final Map<JcrConnectionDescriptor, SLGraph> cache = new ConcurrentHashMap<JcrConnectionDescriptor, SLGraph>();

    @Override
    public synchronized SLGraph createGraph( final JcrConnectionProvider provider ) throws SLGraphFactoryException {
        SLGraph cached = this.cache.get(provider.getData());
        if (cached == null) {
            try {
                SLPersistentTreeFactory factory;
                factory = AbstractFactory.getDefaultInstance(SLPersistentTreeFactory.class);
                final SLPersistentTree tree = factory.createPersistentTree(provider);
                cached = new SLGraphImpl(tree);
                this.cache.put(provider.getData(), cached);
            } catch (final AbstractFactoryException e) {
                throw logAndReturnNew(e, ConfigurationException.class);
            }

        }
        return cached;
    }

    //@Override
    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLGraphFactory#createGraphSession(org.openspotlight.graph.persistence.SLPersistentTreeSession)
     */
    /**
     * Creates the graph session.
     * 
     * @param treeSession the tree session
     * @return the sL graph session
     */
    @Override
    SLGraphSession createGraphSession( final SLPersistentTreeSession treeSession ) {
        return new SLGraphSessionImpl(treeSession);
    }

    //@Override
    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLGraphFactory#createNode(java.lang.Class, org.openspotlight.graph.SLContext, org.openspotlight.graph.SLNode, org.openspotlight.graph.persistence.SLPersistentNode)
     */
    /**
     * Creates the node.
     * 
     * @param clazz the clazz
     * @param context the context
     * @param parent the parent
     * @param persistentNode the persistent node
     * @return the t
     * @throws SLGraphFactoryException the SL graph factory exception
     */
    @Override
    <T extends SLNode> T createNode( final Class<T> clazz,
                                     final SLContext context,
                                     final SLNode parent,
                                     final SLPersistentNode persistentNode ) throws SLGraphFactoryException {
        try {
            final Constructor<T> constructor = clazz.getConstructor(SLContext.class, SLNode.class, SLPersistentNode.class);
            return constructor.newInstance(context, parent, persistentNode);
        } catch (final Exception e) {
            throw new SLGraphFactoryException("Couldn't instantiate node type " + clazz.getName(), e);
        }
    }

    //@Override
    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLGraphFactory#createNode(org.openspotlight.graph.SLContext, org.openspotlight.graph.SLNode, org.openspotlight.graph.persistence.SLPersistentNode, org.openspotlight.graph.SLGraphSessionEventPoster)
     */
    /**
     * Creates the node.
     * 
     * @param context the context
     * @param parent the parent
     * @param persistentNode the persistent node
     * @param eventPoster the event poster
     * @return the sL node
     * @throws SLGraphFactoryException the SL graph factory exception
     */
    @Override
    SLNode createNode( final SLContext context,
                       final SLNode parent,
                       final SLPersistentNode persistentNode,
                       final SLGraphSessionEventPoster eventPoster ) throws SLGraphFactoryException {
        return new SLNodeImpl(context, parent, persistentNode, eventPoster);
    }

    //@Override
    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLGraphFactory#createNode(org.openspotlight.graph.SLContext, org.openspotlight.graph.persistence.SLPersistentNode, org.openspotlight.graph.SLGraphSessionEventPoster)
     */
    /**
     * Creates the node.
     * 
     * @param context the context
     * @param persistentNode the persistent node
     * @param eventPoster the event poster
     * @return the sL node
     * @throws SLGraphFactoryException the SL graph factory exception
     */
    @Override
    SLNode createNode( final SLContext context,
                       final SLPersistentNode persistentNode,
                       final SLGraphSessionEventPoster eventPoster ) throws SLGraphFactoryException {
        return new SLNodeImpl(context, null, persistentNode, eventPoster);
    }

    //@Override
    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLGraphFactory#createProperty(org.openspotlight.graph.SLNode, org.openspotlight.graph.persistence.SLPersistentProperty)
     */
    /**
     * Creates the property.
     * 
     * @param node the node
     * @param persistentProperty the persistent property
     * @return the sL node property< v>
     * @throws SLGraphFactoryException the SL graph factory exception
     */
    @Override
    <V extends Serializable> SLNodeProperty<V> createProperty( final SLNode node,
                                                               final SLPersistentProperty<V> persistentProperty,
                                                               final SLGraphSessionEventPoster eventPoster )
        throws SLGraphFactoryException {
        return new SLNodePropertyImpl<V>(node, persistentProperty, eventPoster);
    }

    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLGraphFactory#createTempGraph(boolean)
     */
    @Override
    public SLGraph createTempGraph( final boolean removeExistent ) throws SLGraphFactoryException {
        try {
            if (this.graph == null || removeExistent) {
                final SLPersistentTreeFactory factory = AbstractFactory.getDefaultInstance(SLPersistentTreeFactory.class);
                final SLPersistentTree tree = factory.createTempPersistentTree(removeExistent);
                this.graph = new SLGraphImpl(tree);
            }
            return this.graph;
        } catch (final Exception e) {
            throw new SLGraphFactoryException("Couldn't create SL graph.", e);
        }
    }

    //@Override
    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLGraphFactory#getContextImplClass()
     */
    /**
     * Gets the context impl class.
     * 
     * @return the context impl class
     * @throws SLGraphFactoryException the SL graph factory exception
     */
    @Override
    Class<? extends SLContext> getContextImplClass() throws SLGraphFactoryException {
        return SLContextImpl.class;
    }
}
