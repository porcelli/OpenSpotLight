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

import org.openspotlight.graph.SLLink;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.SLPersistenceMode;
import org.openspotlight.graph.annotation.SLTransient;
import org.openspotlight.graph.event.SLAbstractGraphSessionEventListener;
import org.openspotlight.graph.event.SLGraphSessionSaveEvent;
import org.openspotlight.graph.event.SLLinkAddedEvent;
import org.openspotlight.graph.event.SLNodeAddedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;

/**
 * The listener interface for receiving SLTransientObject events. The class that is interested in processing a SLTransientObject
 * event implements this interface, and the object created with that class is registered with a component using the component's
 * <code>addSLTransientObjectListener<code> method. When
 * the SLTransientObject event occurs, that object's appropriate
 * method is invoked.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLTransientObjectListener extends SLAbstractGraphSessionEventListener {

    /** The transient links. */
    private final Set<SLLink> transientLinks;

    /** The transient nodes. */
    private final Set<SLNode> transientNodes;

    private final Logger                         logger = LoggerFactory.getLogger(getClass());

    /**
     * Instantiates a new sL transient object listener.
     */
    public SLTransientObjectListener(
                                      final LockContainer parent ) {
        super(parent);
        transientLinks = LockedCollections.createSetWithLock(parent, new HashSet<SLLink>());
        transientNodes = LockedCollections.createSetWithLock(parent, new HashSet<SLNode>());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeSave( final SLGraphSessionSaveEvent event ) {
        synchronized (lock) {
            for (final SLLink link : transientLinks) {
                link.remove();
                if (logger.isDebugEnabled()) {
                    logger.debug(" about to remove " + Arrays.toString(link.getClass().getInterfaces()) + " " + link.getID());
                }
            }
            transientLinks.clear();
            for (final SLNode node : transientNodes) {
                node.remove();
                if (logger.isDebugEnabled()) {
                    logger.debug(" about to remove " + Arrays.toString(node.getClass().getInterfaces()) + " " + node.getName()
                                 + " " + node.getID());
                }
            }
            transientNodes.clear();
        }
    }

    /**
     * Checks for transient annotation.
     * 
     * @param object the object
     * @return true, if successful
     */
    @SuppressWarnings( "unchecked" )
    private boolean hasTransientAnnotation( final Object object ) {
        synchronized (lock) {
            return object.getClass().getInterfaces()[0].getAnnotation(SLTransient.class) != null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void linkAdded( final SLLinkAddedEvent event ) {
        synchronized (lock) {
            final SLLink link = event.getLink();
            if (hasTransientAnnotation(link)) {
                transientLinks.add(link);
            } else {
                final SLPersistenceMode mode = event.getPersistenceMode();
                if (mode.equals(SLPersistenceMode.TRANSIENT)) {
                    transientLinks.add(link);
                } else if (mode.equals(SLPersistenceMode.NORMAL)) {
                    transientLinks.remove(link);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nodeAdded( final SLNodeAddedEvent event ) {
        synchronized (lock) {
            final SLNode node = event.getNode();
            if (hasTransientAnnotation(node)) {
                transientNodes.add(node);
            } else {
                final SLPersistenceMode mode = event.getPersistenceMode();
                if (mode.equals(SLPersistenceMode.TRANSIENT)) {
                    transientNodes.add(node);
                } else if (mode.equals(SLPersistenceMode.NORMAL)) {
                    transientNodes.remove(node);
                }
            }
        }
    }
}
