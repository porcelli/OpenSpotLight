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

package org.openspotlight.storage.engine;

import java.util.Set;

import org.openspotlight.common.Disposable;
import org.openspotlight.storage.NodeCriteria;
import org.openspotlight.storage.Partition;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.Property;
import org.openspotlight.storage.domain.PropertyContainer;
import org.openspotlight.storage.domain.StorageLink;
import org.openspotlight.storage.domain.StorageNode;

/**
 * Interface that defines engine speficic operations. This type is is injected into {@link StorageSession}.<br>
 * Those types that implements this type does not have to care about caching or related operations, once this is already cared by
 * {@link StorageSession}. <br>
 * 
 * @author porcelli
 * @author feuteston
 * @param <RN> engine specific type that represents a node
 * @param <RL> engine specific type that represents a link
 */
public interface StorageEngineBind<RN, RL> extends Disposable {

    /**
     * Creates an engine specific type that represents the input param.
     * 
     * @param link the input link
     * @return engine specific representation of the input link
     * @throws IllegalArgumentException if input param is null
     */
    RL createLinkReference(StorageLink link)
        throws IllegalStateException;

    /**
     * Creates an engine specific type that represents the input param.
     * 
     * @param node the input node
     * @return engine specific representation of the input node
     * @throws IllegalArgumentException if input param is null
     */
    RN createNodeReference(StorageNode node)
        throws IllegalStateException;

    /**
     * Persist the input node into storage engine
     * 
     * @param reference the engine specific node reference
     * @param node the node to be persisted
     * @throws Exception if there is some problem regarding storage engine operation
     * @throws IllegalArgumentException if any input param is null
     */
    void persistNode(RN reference, StorageNode node)
        throws Exception, IllegalStateException;

    /**
     * Deletes the input node from storage engine.
     * 
     * @param node the node to be removed
     * @throws Exception if there is some problem regarding storage engine operation
     * @throws IllegalArgumentException if input param is null
     */
    void deleteNode(StorageNode node)
        throws Exception, IllegalStateException;

    /**
     * Persist the input link into storage engine
     * 
     * @param link the link to be persisted
     * @throws Exception if there is some problem regarding storage engine operation
     * @throws IllegalArgumentException if input param is null
     */
    void persistLink(StorageLink link)
        throws Exception, IllegalStateException;

    /**
     * Deletes the input link from storage engine.
     * 
     * @param link the link to be removed
     * @throws Exception if there is some problem regarding storage engine operation
     * @throws IllegalArgumentException if input param is null
     */
    void deleteLink(StorageLink link)
        throws Exception, IllegalStateException;

    /**
     * Search for nodes that matches the search criteria.
     * 
     * @param criteria the search criteria
     * @return an iterable found of nodes, empty if not found
     * @throws Exception if there is some problem regarding storage engine operation
     * @throws IllegalArgumentException if input param is null
     */
    Iterable<StorageNode> search(NodeCriteria criteria)
        throws Exception, IllegalStateException;

    /**
     * Returns an iterable of nodes of a given type from input partition.
     * 
     * @param partition the partition to be scanned
     * @param type the node type
     * @return an iterable of nodes, empty if not found
     * @throws Exception if there is any exception during this operation
     * @throws IllegalArgumentException if any input param is null or empty
     */
    Iterable<StorageNode> getNodes(Partition partition, String type)
        throws Exception, IllegalStateException;

    /**
     * Returns an iterable of link instances that matches the input param.
     * 
     * @param source the source node
     * @param target the target node, can be null
     * @param type the link type, can be null
     * @return an iterable of matched links, empty if not found
     * @throws Exception if there is any exception during this operation
     * @throws IllegalArgumentException if source param is null
     */
    Iterable<StorageLink> getLinks(StorageNode source, StorageNode target, String type)
        throws Exception, IllegalStateException;

    /**
     * Sets (or creates if does not exists) the property for the given reference node. <br>
     * Null is an accepted property value. <br>
     * 
     * @param nodeRef the engine specific node reference
     * @param property the property to be setted
     * @throws Exception if there is any exception during this operation
     * @throws IllegalArgumentException if property param is null
     */
    void setNodeProperty(RN nodeRef, Property property)
        throws Exception, IllegalStateException;

    /**
     * Sets (or creates if does not exists) the property for the given reference link. <br>
     * Null is an accepted property value. <br>
     * 
     * @param linkRef the engine specific link reference
     * @param property the property to be setted
     * @throws Exception if there is any exception during this operation
     * @throws IllegalArgumentException if property param is null
     */
    void setLinkProperty(RL linkRef, Property property)
        throws Exception, IllegalStateException;

    /**
     * Returns an iterable of all node types stored into given partition.
     * 
     * @param partition the partion to lookup for node types
     * @return an iterable of all node types of the given partition, empty if not found
     * @throws Exception if there is any exception during this operation
     * @throws IllegalArgumentException if input param is null
     */
    Iterable<String> getAllNodeTypes(Partition partition)
        throws Exception, IllegalStateException;

    /**
     * Returns, if exists, a node based on its key.
     * 
     * @param key the node key
     * @return the node, or null if not found
     * @throws Exception if there is any exception during this operation
     * @throws IllegalArgumentException if input param is null or empty
     */
    StorageNode getNode(String key)
        throws Exception, IllegalStateException;

    /**
     * Returns an iterable of children nodes of the input node stored into specific partition.
     * 
     * @param partition the partion to lookup for children nodes
     * @param node the node to get its children
     * @return an iterable of children nodes, or empty if not found
     * @throws Exception if there is any exception during this operation
     * @throws IllegalArgumentException if any input param is null
     */
    Iterable<StorageNode> getChildren(Partition partition, StorageNode node)
        throws Exception, IllegalStateException;

    /**
     * Returns an iterable of children nodes of the input node restricted by a given type and stored into specific partition.
     * 
     * @param partition the partion to lookup for children nodes
     * @param node the node to get its children
     * @param type the node type filter
     * @return an iterable of children nodes, or empty if not found
     * @throws Exception if there is any exception during this operation
     * @throws IllegalArgumentException if any input param is null
     */
    Iterable<StorageNode> getChildren(Partition partition, StorageNode node, String type)
        throws Exception, IllegalStateException;

    /**
     * Returns the parent node of the input.
     * 
     * @param node the input node to get parent from
     * @return parent node, or null if there is no parent
     * @throws Exception if there is any exception during this operation
     * @throws IllegalArgumentException if input param is null
     */
    StorageNode getParent(StorageNode node)
        throws Exception, IllegalStateException;

    /**
     * Returns all existing properties, or an empty {@link Set}, of the input element. <br>
     * 
     * @param element the input element to get properties from
     * @return all properties of this element
     * @throws Exception if there is any exception during this operation
     * @throws IllegalArgumentException if input param is null
     */
    Set<Property> getProperties(PropertyContainer element)
        throws Exception, IllegalStateException;

    /**
     * Returns the property value as byte array.
     * 
     * @param property the input property to get value from
     * @return the value as byte array
     * @throws Exception if there is any exception during this operation
     * @throws IllegalArgumentException if input param is null
     */
    byte[] getPropertyValue(Property property)
        throws Exception, IllegalStateException;

    /**
     * Persist any data that is not yet persisted.
     * 
     * @param partitions patitions to be saved
     * @throws Exception if there is any exception during this operation
     */
    void save(Partition... partitions)
        throws Exception;

}
