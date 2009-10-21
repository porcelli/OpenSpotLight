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

import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;
import org.openspotlight.jcr.provider.JcrConnectionProvider;

/**
 * A factory for creating SLGraph objects.
 */
public abstract class SLGraphFactory extends AbstractFactory {

    public abstract SLGraph createGraph( JcrConnectionProvider provider ) throws SLGraphFactoryException;

    /**
     * Creates a new SLGraph object.
     * 
     * @param treeSession the tree session
     * @return the SL graph session
     * @throws SLGraphFactoryException the SL graph factory exception
     */
    abstract SLGraphSession createGraphSession( SLPersistentTreeSession treeSession ) throws SLGraphFactoryException;

    /**
     * Creates a new SLGraph object.
     * 
     * @param clazz the clazz
     * @param context the context
     * @param parent the parent
     * @param persistentNode the persistent node
     * @return the T
     * @throws SLGraphFactoryException the SL graph factory exception
     */
    abstract <T extends SLNode> T createNode( Class<T> clazz,
                                              SLContext context,
                                              SLNode parent,
                                              SLPersistentNode persistentNode ) throws SLGraphFactoryException;

    /**
     * Creates a new SLGraph object.
     * 
     * @param context the context
     * @param parent the parent
     * @param persistentNode the persistent node
     * @param eventPoster the event poster
     * @return the SL node
     * @throws SLGraphFactoryException the SL graph factory exception
     */
    abstract SLNode createNode( SLContext context,
                                SLNode parent,
                                SLPersistentNode persistentNode,
                                SLGraphSessionEventPoster eventPoster ) throws SLGraphFactoryException;

    /**
     * Creates a new SLGraph object.
     * 
     * @param context the context
     * @param persistentNode the persistent node
     * @param eventPoster the event poster
     * @return the SL node
     * @throws SLGraphFactoryException the SL graph factory exception
     */
    abstract SLNode createNode( SLContext context,
                                SLPersistentNode persistentNode,
                                SLGraphSessionEventPoster eventPoster ) throws SLGraphFactoryException;

    /**
     * Creates a new SLGraph object.
     * 
     * @param node the node
     * @param persistentProperty the persistent property
     * @return the SL node property< v>
     * @throws SLGraphFactoryException the SL graph factory exception
     */
    abstract <V extends Serializable> SLNodeProperty<V> createProperty( SLNode node,
                                                                        SLPersistentProperty<V> persistentProperty,
                                                                        SLGraphSessionEventPoster eventPoster )
        throws SLGraphFactoryException;

    /**
     * Creates a new SLGraph object.
     * 
     * @param removeExistent the remove existent
     * @return the SL graph
     * @throws SLGraphFactoryException the SL graph factory exception
     */
    public abstract SLGraph createTempGraph( boolean removeExistent ) throws SLGraphFactoryException;

    /**
     * Gets the context impl class.
     * 
     * @return the context impl class
     * @throws SLGraphFactoryException the SL graph factory exception
     */
    abstract Class<? extends SLContext> getContextImplClass() throws SLGraphFactoryException;

}
