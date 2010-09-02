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
package org.openspotlight.graph.query;

import java.util.Collection;

import org.openspotlight.graph.metadata.Metadata;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.StorageNode;

/**
 * The Class SLSelectCommandDO.
 *
 * @author Vitor Hugo Chagas
 */
public class SLSelectCommandDO {

    /** The metadata. */
    private Metadata               metadata;

    /** The node wrappers. */
    private Collection<StorageNode> nodeWrappers;

    /** The previous node wrappers. */
    private Collection<StorageNode> previousNodeWrappers;

    /** The tree session. */
    private StorageSession  treeSession;

    /** The collator strength. */
    private int                      collatorStrength;

    /**
     * Gets the metadata.
     *
     * @return the metadata
     */
    public Metadata getMetadata() {
        return metadata;
    }

    /**
     * Sets the metadata.
     *
     * @param metadata the new metadata
     */
    public void setMetadata( Metadata metadata ) {
        this.metadata = metadata;
    }

    /**
     * Gets the node wrappers.
     *
     * @return the node wrappers
     */
    public Collection<StorageNode> getNodeWrappers() {
        return nodeWrappers;
    }

    /**
     * Sets the node wrappers.
     *
     * @param nodeWrappers the new node wrappers
     */
    public void setNodeWrappers( Collection<StorageNode> nodeWrappers ) {
        this.nodeWrappers = nodeWrappers;
    }

    /**
     * Gets the tree session.
     *
     * @return the tree session
     */
    public StorageSession getTreeSession() {
        return treeSession;
    }

    /**
     * Sets the tree session.
     *
     * @param treeSession the new tree session
     */
    public void setTreeSession( StorageSession treeSession ) {
        this.treeSession = treeSession;
    }

    /**
     * Gets the previous node wrappers.
     *
     * @return the previous node wrappers
     */
    public Collection<StorageNode> getPreviousNodeWrappers() {
        return previousNodeWrappers;
    }

    /**
     * Sets the previous node wrappers.
     *
     * @param previousNodeWrappers the new previous node wrappers
     */
    public void setPreviousNodeWrappers( Collection<StorageNode> previousNodeWrappers ) {
        this.previousNodeWrappers = previousNodeWrappers;
    }

    /**
     * Gets the collator strength.
     *
     * @return the collator strength
     */
    public int getCollatorStrength() {
        return collatorStrength;
    }

    /**
     * Sets the collator strength.
     *
     * @param collatorStrength the new collator strength
     */
    public void setCollatorStrength( int collatorStrength ) {
        this.collatorStrength = collatorStrength;
    }
}
