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
package org.openspotlight.graph.listeners;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openspotlight.graph.SLAbstractGraphSessionEventListener;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLGraphSessionEvent;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLInvalidCredentialException;
import org.openspotlight.graph.SLLink;
import org.openspotlight.graph.SLLinkEvent;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.SLNodeEvent;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving SLObjectMark events. The class that is
 * interested in processing a SLObjectMark event implements this interface, and
 * the object created with that class is registered with a component using the
 * component's <code>addSLObjectMarkListener<code> method. When
 * the SLObjectMark event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see SLObjectMarkEvent
 * @author Vitor Hugo Chagas
 */
public class SLObjectMarkListener extends SLAbstractGraphSessionEventListener {
    
    /** The links for deletion. */
    private final Set<SLLink> linksForDeletion;
    
    /** The nodes for deletion. */
    private final Set<SLNode> nodesForDeletion;
    
    /**
     * Instantiates a new sL object mark listener.
     */
    public SLObjectMarkListener() {
        this.linksForDeletion = new HashSet<SLLink>();
        this.nodesForDeletion = new HashSet<SLNode>();
    }
    
    // @Override
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openspotlight.graph.SLAbstractGraphSessionEventListener#beforeSave
     * (org.openspotlight.graph.SLGraphSessionEvent)
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeSave(final SLGraphSessionEvent event)
            throws SLGraphSessionException, SLInvalidCredentialException {
        
        // delete links ...
        for (final SLLink link : this.linksForDeletion) {
            link.remove();
        }
        
        // delete nodes ...
        for (final SLNode node : this.nodesForDeletion) {
            node.remove();
        }
    }
    
    // @Override
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openspotlight.graph.SLAbstractGraphSessionEventListener#linkAdded
     * (org.openspotlight.graph.SLLinkEvent)
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public void linkAdded(final SLLinkEvent event)
            throws SLGraphSessionException {
        // unmark link and its sides ...
        final SLLink link = event.getLink();
        if (this.linksForDeletion.size() > 0) {
            this.linksForDeletion.remove(link);
        }
        if (this.nodesForDeletion.size() > 0) {
            final SLNode[] sides = link.getSides();
            this.nodesForDeletion.remove(sides[0]);
            this.nodesForDeletion.remove(sides[1]);
            
        }
    }
    
    // @Override
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openspotlight.graph.SLAbstractGraphSessionEventListener#nodeAdded
     * (org.openspotlight.graph.SLNodeEvent)
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public void nodeAdded(final SLNodeEvent event)
            throws SLGraphSessionException {
        
        final SLGraphSession session = event.getSession();
        final SLNode node = event.getNode();
        final Collection<Class<? extends SLLink>> linkTypesForLinkDeletion = event
                .getLinkTypesForLinkDeletion();
        final Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion = event
                .getLinkTypesForLinkedNodesDeletion();
        
        if (linkTypesForLinkDeletion != null) {
            // mark for deletion links that have the added node as side ...
            for (final Class<? extends SLLink> linkType : linkTypesForLinkDeletion) {
                final Collection<? extends SLLink> links = session.getLinks(
                        linkType, node, null, SLLink.DIRECTION_ANY);
                this.linksForDeletion.addAll(links);
            }
        }
        
        if (linkTypesForLinkedNodeDeletion != null) {
            // mark for deletion all the nodes linked to this node ...
            for (final Class<? extends SLLink> linkType : linkTypesForLinkedNodeDeletion) {
                final Collection<SLNode> nodes = session.getNodesByLink(
                        linkType, node);
                this.nodesForDeletion.addAll(nodes);
            }
        }
        
        // unmark the added node (if it's present in the set) ...
        this.nodesForDeletion.remove(node);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public void sessionCleaned() {
        // 
    }
}
