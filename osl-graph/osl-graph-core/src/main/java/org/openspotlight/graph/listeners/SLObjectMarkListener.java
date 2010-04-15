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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openspotlight.common.concurrent.LockContainer;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLLink;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.event.SLAbstractGraphSessionEventListener;
import org.openspotlight.graph.event.SLGraphSessionSaveEvent;
import org.openspotlight.graph.event.SLLinkAddedEvent;
import org.openspotlight.graph.event.SLNodeAddedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving SLObjectMark events. The class that is interested in processing a SLObjectMark event
 * implements this interface, and the object created with that class is registered with a component using the component's
 * <code>addSLObjectMarkListener<code> method. When
 * the SLObjectMark event occurs, that object's appropriate
 * method is invoked.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLObjectMarkListener extends SLAbstractGraphSessionEventListener {

    /** The links for deletion. */
    private final Set<SLLink> linksForDeletion;

    /** The nodes for deletion. */
    private final Set<SLNode> nodesForDeletion;

    private final Logger      logger = LoggerFactory.getLogger(getClass());

    /**
     * Instantiates a new sL object mark listener.
     */
    public SLObjectMarkListener(
                                 final LockContainer parent ) {
        super(parent);
        linksForDeletion = Collections.synchronizedSet(new HashSet<SLLink>());
        nodesForDeletion = Collections.synchronizedSet(new HashSet<SLNode>());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeSave( final SLGraphSessionSaveEvent event ) {
        synchronized (lock) {

            // delete links ...
            for (final SLLink link : linksForDeletion) {
                logLinkRemoval(link);
                link.remove();
            }
            linksForDeletion.clear();

            // delete nodes ...
            for (final SLNode node : nodesForDeletion) {
                logNodeRemoval(node);
                node.remove();
            }
            nodesForDeletion.clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void linkAdded( final SLLinkAddedEvent event ) {
        synchronized (lock) {
            // unmark link and its sides ...
            final SLLink link = event.getLink();
            if (linksForDeletion.size() > 0) {
                linksForDeletion.remove(link);
            }
            if (nodesForDeletion.size() > 0) {
                final SLNode[] sides = link.getSides();
                nodesForDeletion.remove(sides[0]);
                nodesForDeletion.remove(sides[1]);
            }
        }
    }

    private void logLinkRemoval( final SLLink link ) {
        try {
            if (link != null && logger.isDebugEnabled()) {

                logger.debug(" about to remove link "
                             + Arrays.toString(link.getClass().getInterfaces())
                             + " " + link.getID());
            }
        } catch (final Exception e) {
            Exceptions.catchAndLog(e);
        }
    }

    private void logNodeRemoval( final SLNode node ) {
        try {
            if (node != null && logger.isDebugEnabled()) {
                logger.debug(" about to remove node "
                             + Arrays.toString(node.getClass().getInterfaces())
                             + " " + node.getName() + " " + node.getID());
            }
        } catch (final Exception e) {
            Exceptions.catchAndLog(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nodeAdded( final SLNodeAddedEvent event ) {
        synchronized (lock) {

            final SLGraphSession session = event.getSession();
            final SLNode node = event.getNode();
            final Collection<Class<? extends SLLink>> linkTypesForLinkDeletion = event
                                                                                      .getLinkTypesForLinkDeletion();
            final Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion = event
                                                                                            .getLinkTypesForLinkedNodesDeletion();

            if (linkTypesForLinkDeletion != null) {
                // mark for deletion links that have the added node as side ...
                for (final Class<? extends SLLink> linkType : linkTypesForLinkDeletion) {
                    final Collection<? extends SLLink> links = session
                                                                      .getLinks(linkType, node, null,
                                                                                SLLink.DIRECTION_ANY);
                    linksForDeletion.addAll(links);
                }
            }

            if (linkTypesForLinkedNodeDeletion != null) {
                // mark for deletion all the nodes linked to this node ...
                for (final Class<? extends SLLink> linkType : linkTypesForLinkedNodeDeletion) {
                    final Collection<SLNode> nodes = session.getNodesByLink(
                                                                            linkType, node);
                    nodesForDeletion.addAll(nodes);
                }
            }

            // unmark the added node (if it's present in the set) ...
            nodesForDeletion.remove(node);
        }
    }
}
