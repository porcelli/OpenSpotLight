/**
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

package org.openspotlight.storage.domain;

import org.openspotlight.storage.Partition;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.key.NodeKey;
import org.openspotlight.storage.domain.key.NodeKey.CompositeKey;

/**
 * The StorageNode is the base data structure that enables store any information. Any information can be modeled as a StorageNode
 * using its unique identifiers or properties. <br>
 * A StorageNode is uniquely identified by a {@link NodeKey}.
 * <p>
 * To secure the data consistency its not possible change the unique identifiers of a StorageNode. If you need so, you'll have to
 * delete it and create a new one.
 * </p>
 * <p>
 * A StorageNode defines any kind of information, to relate this data with other you'll have to create {@link StorageLink} to
 * connect those nodes. <br>
 * StorageNodes should be created using the {@link NodeBuilder}.
 * </p>
 * <p>
 * Along with {@link StorageLink}, nodes are the core of persistence data model.
 * </p>
 * 
 * @author feuteston
 * @author porcelli
 */
public interface StorageNode extends StorageDataMarker, NodeFactory, PropertyContainer {

    /**
     * Returns the {@link NodeKey}, wich defines uniquely the node.
     * 
     * @return the node key
     * @see NodeKey
     */
    NodeKey getKey();

    /**
     * Returns the parent node
     * 
     * @param session the storage session
     * @return the parent node, or null if node there is no parent
     */
    StorageNode getParent(StorageSession session);

    /**
     * Returns the node type, in fact this is just a sugar method for {@link CompositeKey#getNodeType()}.
     * 
     * @return the node type
     */
    String getType();

    /**
     * Returns an iterable of children nodes stored into specific partition.
     * 
     * @param partition the partion to lookup for children nodes
     * @param session the storage session
     * @return an iterable of children nodes, or empty if not found
     */
    Iterable<StorageNode> getChildren(Partition partition, StorageSession session);

    /**
     * Returns an iterable of children nodes of a given type stored into specific partition.
     * 
     * @param partition the partion to lookup for children nodes
     * @param session the storage session
     * @param type the node type filter
     * @return an iterable of children nodes, or empty if not found
     */
    Iterable<StorageNode> getChildren(Partition partition, StorageSession session, String type);

    /**
     * Returns an iterable of children nodes stored into specific partition. <br>
     * <b>Note</b> that this operation invalidates the internal cache and reload its result into it.
     * 
     * @param partition the partion to lookup for children nodes
     * @param session the storage session
     * @return an iterable of children nodes, or empty if not found
     */
    Iterable<StorageNode> getChildrenForcingReload(Partition partition, StorageSession session);

    /**
     * Returns an iterable of children nodes of a given type stored into specific partition.<br>
     * <b>Note</b> that this operation invalidates the internal cache and reload its result into it.
     * 
     * @param partition the partion to lookup for children nodes
     * @param session the storage session
     * @param type the node type
     * @return an iterable of children nodes, or empty if not found
     */
    Iterable<StorageNode> getChildrenForcingReload(Partition partition, StorageSession session, String type);

    /**
     * Removes the node and all its children and any link that its associated. <br>
     * <p>
     * <b>Important Note:</b> this is just a sugar method that, in fact, executes the
     * {@link StorageSession#removeNode(StorageNode)}.
     * 
     * @param session the storage session
     */
    void remove(StorageSession session);

}
