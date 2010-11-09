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

package org.openspotlight.storage;

import org.openspotlight.common.Disposable;
import org.openspotlight.storage.NodeCriteria.NodeCriteriaBuilder;
import org.openspotlight.storage.domain.NodeFactory;
import org.openspotlight.storage.domain.StorageLink;
import org.openspotlight.storage.domain.StorageNode;
import org.openspotlight.storage.domain.key.NodeKey;
import org.openspotlight.storage.domain.key.NodeKey.CompositeKey.SimpleKey;

/**
 * This class is an abstraction of a current state of storage session. The implementation classes must not store any kind of
 * connection state. This implementation must not be shared between threads.
 * 
 * @author feuteston
 * @author porcelli
 */
public interface StorageSession extends Disposable {

    /**
     * Defines the {@link StorageSession} flush behavior.
     * 
     * @author feuteston
     * @author porcelli
     */
    static enum FlushMode {
        /**
         * Data are automatically flushed into storage, wich means that its not necessary execute
         * {@link StorageSession#flushTransient()} method.
         */
        AUTO,
        /**
         * Its mandatory execute the {@link StorageSession#flushTransient()} method to flush data into storage.
         */
        EXPLICIT
    }

    /**
     * Builder that creates {@link NodeKey} instances.
     * 
     * @author feuteston
     * @author porcelli
     */
    interface NodeKeyBuilder {

        /**
         * Creates the {@link NodeKey} instance based on builder data stack.
         * 
         * @return the node key instance
         */
        NodeKey andCreate();

        /**
         * Adds a new {@link SimpleKey} into stack.
         * 
         * @param keyName the key name
         * @param value the key value
         * @return self builder
         */
        NodeKeyBuilder withSimpleKey(String keyName, String value);

        /**
         * Sets the parent's key of the current {@link NodeKey}.
         * 
         * @param parentKey the parent key
         * @return self builder
         */
        NodeKeyBuilder withParent(String parentKey);

        /**
         * Pushes a new {@link NodeKey} into builder stack to define the parent characteristics. <br>
         * <b>Note:</b> use this method once you already defined all data related to the node, once there is no way to pop the
         * stack.
         * 
         * @param partition the parent partition
         * @param nodeType the parent node type
         * @return the pushed
         */
        NodeKeyBuilder withParent(Partition partition, String nodeType);
    }

    /**
     * Interface that defines a set of operations available to execute into {@link Partition}s.
     * 
     * @author feuteston
     * @author porcelli
     */
    interface PartitionMethods extends NodeFactory {

        /**
         * Start creating a search criteria using a builder pattern.
         * 
         * @return teh search criteria builder
         */
        NodeCriteriaBuilder createCriteria();

        /**
         * Start creating a {@link NodeKey} using a builder pattern with a node type setted.
         * 
         * @param nodeType the node type
         * @return the node key builder
         */
        NodeKeyBuilder createNodeKeyWithType(String nodeType);

        /**
         * Start creating a {@link StorageNode} using a builder pattern with a node type setted.
         * 
         * @param nodeType the node type
         * @return the node builder
         */
        NodeBuilder createNodeWithType(String nodeType);

        /**
         * Creates, if not exists, a node hierarchy using the parameter input for each node as a type into partition. <br>
         * The hierarchy is based on input order, the first is higher.
         * 
         * @param nodeTypes node type for each element of hierarchy
         * @return the last created node on hierarchy
         */
        StorageNode createNewSimpleNode(String... nodeTypes);

        /**
         * Returns an iterable of nodes of a given type from partition.
         * 
         * @param nodeType the node type
         * @return an iterable of nodes, empty if not found
         */
        Iterable<StorageNode> getNodes(String nodeType);

        /**
         * Search for nodes, stored into partition, that matches the seacrh criteria.
         * 
         * @param criteria the search criteria
         * @return an iterable found of nodes, empty if not found
         */
        Iterable<StorageNode> search(NodeCriteria criteria);

        /**
         * Sugar method that executes the search and returns the first found node, or null if not found.
         * 
         * @param criteria the search criteria
         * @return the found node, or null if not found
         */
        StorageNode searchUnique(NodeCriteria criteria);

        /**
         * Returns an iterable of all node types stored into partition.
         * 
         * @return an iterable of all node types of partition, empty if not found
         */
        Iterable<String> getAllNodeTypes();
    }

    /**
     * Defines the partition to be manipulated
     * 
     * @param partition the chosen partition
     * @return partition manipulation methods
     */
    PartitionMethods withPartition(Partition partition);

    /**
     * Returns the session's flush mode behavior.
     * 
     * @return the flush mode behavior
     * @see FlushMode
     */
    FlushMode getFlushMode();

    /**
     * Flush into storage the transient (not yet stored) data.<br>
     * <b>Note</b> that this method just makes sense if session is running with {@link FlushMode#EXPLICIT} mode.
     */
    void flushTransient();

    /**
     * This method discard all transiente (not yet stored) data.<br>
     * <b>Important Notes:</b><br>
     * <ul>
     * <li>this method has no undo, so be carefull
     * <li>this method just makes sense if session is running with {@link FlushMode#EXPLICIT} mode.
     * </ul>
     */
    void discardTransient();

    /**
     * Adds an unidirectional link between the source and target nodes with the specified link type. <br>
     * <b>Note</b> that if link already exists its not duplicated.
     * <p>
     * <b>Important Note:</b> If session is running in {@link FlushMode#EXPLICIT} mode, its necessary to execute the
     * {@link #flushTransient()} method to effectivelly store it, otherwise its automatically stored.
     * 
     * @param source the source node
     * @param target the target node
     * @param type the link type
     * @return the new, or already existing, link
     */
    StorageLink addLink(StorageNode source, StorageNode target, String type);

    /**
     * Removes, if exists, the paramenter link from storage.
     * <p>
     * <b>Important Note:</b> If session is running in {@link FlushMode#EXPLICIT} mode, its necessary to execute the
     * {@link #flushTransient()} method to effectivelly store it, otherwise its automatically removed.
     * 
     * @param link the link to be removed
     */
    void removeLink(StorageLink link);

    /**
     * Removes, if exists, the link instance defined by input parameters from storage.
     * <p>
     * <b>Important Note:</b> If session is running in {@link FlushMode#EXPLICIT} mode, its necessary to execute the
     * {@link #flushTransient()} method to effectivelly store it, otherwise its automatically removed.
     * 
     * @param source the source node
     * @param target the target node
     * @param type the link type
     */
    void removeLink(StorageNode source, StorageNode target, String type);

    /**
     * Returns a unique link instance defined by input parameters.
     * 
     * @param source the source node
     * @param target the target node
     * @param type the link type
     * @return the link, or null if not found
     */
    StorageLink getLink(StorageNode source, StorageNode target, String type);

    /**
     * Returns an iterable of link instances that matches the given source node.
     * 
     * @param source the source node
     * @return an iterable of matched links, empty if not found
     */
    Iterable<StorageLink> getLinks(StorageNode source);

    /**
     * Returns an iterable of link instances that matches the link type of a given source node.
     * 
     * @param source the source node
     * @param type the link type
     * @return an iterable of matched links, empty if not found
     */
    Iterable<StorageLink> getLinks(StorageNode source, String type);

    /**
     * Returns an iterable of link instances of any type that matches the given source and target nodes.
     * 
     * @param source the source node
     * @param target the target node
     * @return an iterable of matched links, empty if not found
     */
    Iterable<StorageLink> getLinks(StorageNode source, StorageNode target);

    /**
     * Returns, if exists, a node based on its key.
     * 
     * @param key the node key
     * @return the node, or null if not found
     */
    StorageNode getNode(String key);

    /**
     * Removes the node and all its children and any link that its associated. <br>
     * <p>
     * <b>Important Note:</b> If session is running in {@link FlushMode#EXPLICIT} mode, its necessary to execute the
     * {@link #flushTransient()} method to effectivelly store it, otherwise its automatically removed.
     * 
     * @param node the node to be removed
     */
    void removeNode(StorageNode node);

}
