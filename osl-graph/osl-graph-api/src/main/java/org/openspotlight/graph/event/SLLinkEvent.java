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
package org.openspotlight.graph.event;

import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.graph.SLLink;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.SLPersistenceMode;
import org.openspotlight.graph.persistence.SLPersistentNode;

/**
 * The Class SLLinkEvent.
 * 
 * @author Vitor Hugo Chagas
 */
public abstract class SLLinkEvent extends SLGraphSessionEvent {

    /** The link. */
    private final SLLink      link;

    /** The link node. */
    private SLPersistentNode  linkNode;

    /** The persistence mode. */
    private SLPersistenceMode persistenceMode;

    /** The new link. */
    private boolean           newLink;

    /** The changed to bidirectional. */
    private boolean           changedToBidirectional;

    /** The bidirectional. */
    private boolean           bidirectional;

    /** The source. */
    private final SLNode      source;

    /** The target. */
    private final SLNode      target;

    /** The sides. */
    private final SLNode[]    sides;

    /**
     * Instantiates a new sL link event.
     */
    public SLLinkEvent(
                        final SLLink link ) {
        super(link.getSession());
        this.link = link;
        try {
            if (link.isBidirectional()) {
                sides = link.getSides();
                source = null;
                target = null;
            } else {
                sides = null;
                source = link.getSource();
                target = link.getTarget();
            }
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    /**
     * Instantiates a new sL link event.
     * 
     * @param type the type
     * @param link the link
     * @param linkNode the link node
     * @param persistenceMode the persistence mode
     */
    public SLLinkEvent(
                        final SLLink link, final SLPersistentNode linkNode,
                        final SLPersistenceMode persistenceMode ) {
        super(link.getSession());
        this.link = link;
        this.linkNode = linkNode;
        this.persistenceMode = persistenceMode;
        try {
            if (link.isBidirectional()) {
                sides = link.getSides();
                Assertions.checkNotNull("sides", sides);
                Assertions.checkNotNull("sides[0]", sides[0]);
                Assertions.checkNotNull("sides[1]", sides[1]);
                source = null;
                target = null;
            } else {
                sides = null;
                source = link.getSource();
                target = link.getTarget();
                Assertions.checkNotNull("source", source);
                Assertions.checkNotNull("target", target);
            }
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    /**
     * Gets the link.
     * 
     * @return the link
     */
    public SLLink getLink() {
        return link;
    }

    /**
     * Gets the link node.
     * 
     * @return the link node
     */
    public SLPersistentNode getLinkNode() {
        return linkNode;
    }

    /**
     * Gets the persistence mode.
     * 
     * @return the persistence mode
     */
    public SLPersistenceMode getPersistenceMode() {
        return persistenceMode;
    }

    /**
     * Gets the sides.
     * 
     * @return the sides
     */
    public SLNode[] getSides() {
        return sides;
    }

    /**
     * Gets the source.
     * 
     * @return the source
     */
    public SLNode getSource() {
        return source;
    }

    /**
     * Gets the target.
     * 
     * @return the target
     */
    public SLNode getTarget() {
        return target;
    }

    /**
     * Checks if is bidirectional.
     * 
     * @return true, if is bidirectional
     */
    public boolean isBidirectional() {
        return bidirectional;
    }

    /**
     * Checks if is changed to bidirectional.
     * 
     * @return true, if is changed to bidirectional
     */
    public boolean isChangedToBidirectional() {
        return changedToBidirectional;
    }

    /**
     * Checks if is new link.
     * 
     * @return true, if is new link
     */
    public boolean isNewLink() {
        return newLink;
    }

    /**
     * Sets the bidirectional.
     * 
     * @param bidirectional the new bidirectional
     */
    public void setBidirectional( final boolean bidirectional ) {
        this.bidirectional = bidirectional;
    }

    /**
     * Sets the changed to bidirectional.
     * 
     * @param allowsMultiple the new changed to bidirectional
     */
    public void setChangedToBidirectional( final boolean allowsMultiple ) {
        changedToBidirectional = allowsMultiple;
    }

    /**
     * Sets the new link.
     * 
     * @param newLink the new new link
     */
    public void setNewLink( final boolean newLink ) {
        this.newLink = newLink;
    }
}
