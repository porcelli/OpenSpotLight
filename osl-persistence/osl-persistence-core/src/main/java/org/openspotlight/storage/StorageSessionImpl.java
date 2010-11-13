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

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static org.openspotlight.common.Pair.newPair;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openspotlight.common.Pair;
import org.openspotlight.common.util.SLCollections;
import org.openspotlight.storage.NodeCriteria.NodeCriteriaBuilder;
import org.openspotlight.storage.NodeCriteria.NodeCriteriaItem;
import org.openspotlight.storage.NodeCriteria.NodeCriteriaItem.CompositeKeyCriteriaItem;
import org.openspotlight.storage.NodeCriteria.NodeCriteriaItem.NodeKeyCriteriaItem;
import org.openspotlight.storage.NodeCriteria.NodeCriteriaItem.PropertyContainsString;
import org.openspotlight.storage.NodeCriteria.NodeCriteriaItem.PropertyCriteriaItem;
import org.openspotlight.storage.NodeCriteria.NodeCriteriaItem.PropertyEndsWithString;
import org.openspotlight.storage.NodeCriteria.NodeCriteriaItem.PropertyStartsWithString;
import org.openspotlight.storage.NodeCriteriaImpl.CriteriaBuilderImpl;
import org.openspotlight.storage.domain.NodeFactory;
import org.openspotlight.storage.domain.NodeFactory.NodeBuilder;
import org.openspotlight.storage.domain.Property;
import org.openspotlight.storage.domain.PropertyContainer;
import org.openspotlight.storage.domain.StorageLink;
import org.openspotlight.storage.domain.StorageLinkImpl;
import org.openspotlight.storage.domain.StorageNode;
import org.openspotlight.storage.domain.StorageNodeImpl;
import org.openspotlight.storage.domain.key.NodeKey;
import org.openspotlight.storage.domain.key.NodeKey.CompositeKey.SimpleKey;
import org.openspotlight.storage.domain.key.NodeKeyImpl;
import org.openspotlight.storage.domain.key.NodeKeyImpl.CompositeKeyImpl;
import org.openspotlight.storage.domain.key.NodeKeyImpl.CompositeKeyImpl.SimpleKeyImpl;
import org.openspotlight.storage.engine.StorageEngineBind;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;

/**
 * Internal (default) implementation of {@link StorageSession}.
 * 
 * @author feuteston
 * @author porcelli
 * @param <RN> storage engine specific type that represents a node
 * @param <RL> storage engine specific type that represents a link
 */
public class StorageSessionImpl<RN, RL> implements StorageSession {

    /**
     * Internal (default) implementation of {@link NodeFactory.NodeBuilder}.
     * 
     * @author feuteston
     * @author porcelli
     */
    private final class NodeBuilderImpl implements NodeBuilder {

        private final Set<String>    keyNames  = newHashSet();

        private final Set<SimpleKey> keys      = newHashSet();

        private String               parentKey = null;

        private final Partition      partition;

        private final String         type;

        private NodeBuilderImpl(final String type, final Partition partition) {
            this.type = type;
            this.partition = partition;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StorageNode andCreate()
            throws RuntimeException {
            final CompositeKeyImpl localKey = new CompositeKeyImpl(keys, type);

            final NodeKeyImpl uniqueKey = new NodeKeyImpl(localKey, parentKey, partition);
            final StorageNodeImpl result = new StorageNodeImpl(uniqueKey, false);
            if (getFlushMode().equals(FlushMode.AUTO)) {
                StorageSessionImpl.this.persistNode(result);
            } else {
                final RN ref = storageEngine.createNodeReference(result);
                final Pair<StorageNode, RN> pair = newPair((StorageNode) result, ref);
                StorageSessionImpl.this.newNodes.add(pair);
            }
            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public NodeFactory.NodeBuilder withParent(final StorageNode parent)
            throws IllegalArgumentException, IllegalStateException {
            checkNotNull("parent", parent);

            return withParent(parent.getKeyAsString());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public NodeBuilder withParent(final String key) {
            checkNotEmpty("key", key);

            if (this.parentKey != null) { throw new IllegalStateException(); }
            this.parentKey = key;
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public NodeFactory.NodeBuilder withSimpleKey(final String name, final String value)
            throws IllegalArgumentException, IllegalStateException {
            checkNotEmpty("name", name);
            checkNotEmpty("value", value);

            if (keyNames.contains(name)) { throw new IllegalStateException("key name already inserted"); }
            this.keys.add(new SimpleKeyImpl(name, value));
            this.keyNames.add(name);
            return this;
        }
    }

    /**
     * Internal (default) implementation of {@link PartitionMethods}.
     * 
     * @author feuteston
     * @author porcelli
     */
    public class PartitionMethodsImpl implements PartitionMethods {

        private final Partition partition;

        private PartitionMethodsImpl(final Partition partition) {
            this.partition = partition;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public NodeCriteriaBuilder createCriteria() {
            return new CriteriaBuilderImpl(partition);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StorageNode createNewSimpleNode(final String... nodeTypes)
            throws RuntimeException, IllegalStateException {
            checkNotEmpty("nodeTypes", nodeTypes);

            StorageNode parent = null;
            NodeKey parentKey = null;
            for (final String nodeType: nodeTypes) {
                parentKey =
                    new NodeKeyImpl(new CompositeKeyImpl(Collections.<SimpleKey>emptySet(), nodeType),
                        parentKey != null ? parentKey.getKeyAsString() : null,
                        partition);
                parent = new StorageNodeImpl(parentKey, false);
                persistNode(parent);
            }

            return parent;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public NodeKeyBuilder createNodeKeyWithType(final String nodeType)
            throws IllegalArgumentException {
            checkNotEmpty("nodeType", nodeType);

            return new NodeKeyBuilderImpl(nodeType, partition);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public NodeFactory.NodeBuilder createNodeWithType(final String type)
            throws IllegalArgumentException {
            checkNotEmpty("type", type);

            return this.createWithType(StorageSessionImpl.this, type);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public NodeFactory.NodeBuilder createWithType(final StorageSession session, final String type)
            throws IllegalArgumentException {
            checkNotNull("session", session);
            checkNotEmpty("type", type);

            return new NodeBuilderImpl(type, partition);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterable<String> getAllNodeTypes()
            throws RuntimeException {
            try {
                return storageEngine.getAllNodeTypes(partition);
            } catch (final Exception e) {
                handleException(e);
                return null;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterable<StorageNode> getNodes(final String type)
            throws RuntimeException, IllegalArgumentException {
            checkNotEmpty("type", type);

            try {
                return storageEngine.getNodes(partition, type);
            } catch (final Exception e) {
                handleException(e);
                return null;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterable<StorageNode> search(final NodeCriteria criteria)
            throws RuntimeException, IllegalArgumentException, IllegalStateException {
            checkNotNull("criteria", criteria);

            try {
                if (!criteria.getPartition().equals(partition)) { throw new IllegalStateException(); }
                boolean hasGlobal = false;
                boolean hasOther = false;
                for (final NodeCriteriaItem item: criteria.getCriteriaItems()) {
                    if (item instanceof PropertyCriteriaItem) {
                        hasOther = true;
                    } else if (item instanceof CompositeKeyCriteriaItem) {
                        hasOther = true;
                    } else if (item instanceof PropertyContainsString) {
                        hasOther = true;
                    } else if (item instanceof PropertyStartsWithString) {
                        hasOther = true;
                    } else if (item instanceof PropertyEndsWithString) {
                        hasOther = true;
                    } else if (item instanceof NodeKeyCriteriaItem) {
                        hasGlobal = true;
                    }
                    if (hasOther && hasGlobal) { throw new IllegalStateException(); }
                }
                return storageEngine.search(criteria);
            } catch (final Exception e) {
                handleException(e);
                return null;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StorageNode searchUnique(final NodeCriteria criteria)
            throws RuntimeException, IllegalArgumentException, IllegalStateException {
            checkNotNull("criteria", criteria);
            try {
                final Iterable<StorageNode> result = search(criteria);
                if (result == null) { return null; }
                final Iterator<StorageNode> it = result.iterator();
                if (!it.hasNext()) { return null; }
                return it.next();
            } catch (final Exception e) {
                handleException(e);
                return null;
            }
        }

    }

    private final FlushMode                        flushMode;

    private final PartitionFactory                 partitionFactory;

    private final StorageEngineBind<RN, RL>        storageEngine;

    private final Map<Partition, PartitionMethods> partitionMethods    = newHashMap();

    private final Multimap<StorageNode, Property>  dirtyNodeProperties = ArrayListMultimap.create();

    private final Multimap<StorageLink, Property>  dirtyLinkProperties = ArrayListMultimap.create();

    private final Set<Pair<StorageLink, RL>>       newLinks            = newLinkedHashSet();

    private final Set<Pair<StorageNode, RN>>       newNodes            = newLinkedHashSet();

    private final Set<StorageLink>                 removedLinks        = newLinkedHashSet();

    private final Set<StorageNode>                 removedNodes        = newLinkedHashSet();

    @Inject
    public StorageSessionImpl(final FlushMode flushMode, final PartitionFactory partitionFactory,
                              final StorageEngineBind<RN, RL> storageEngine) {
        if (flushMode == null) { throw new NullPointerException(); }
        if (partitionFactory == null) { throw new NullPointerException(); }
        this.flushMode = flushMode;
        this.partitionFactory = partitionFactory;
        this.storageEngine = storageEngine;
    }

    /**
     * Utility method that trows the input as a {@link RuntimeException}. If input is not an instance of {@link RuntimeException}
     * it wraps the input into a new one.
     * 
     * @param e parameter exception
     * @throws RuntimeException always throwed
     */
    private void handleException(final Exception e)
        throws RuntimeException {
        if (e instanceof RuntimeException) { throw (RuntimeException) e; }
        throw new RuntimeException(e);
    }

    /**
     * Serializes the property invoking storage engine {@link StorageEngineBind#setNodeProperty(Object, Property)} operation.
     * 
     * @param reference the engine specific representation of the node
     * @param property the property to be serialized
     * @throws RuntimeException if there is any exception on storage engine during this operation
     * @throws IllegalArgumentException if property param is null
     */
    private void serializeNodeProperty(final RN reference, final Property property)
        throws RuntimeException, IllegalArgumentException {
        try {
            checkNotNull("property", property);
            storageEngine.setNodeProperty(reference, property);
        } catch (final Exception e) {
            handleException(e);
        }
    }

    /**
     * Serializes the property invoking storage engine {@link StorageEngineBind#setLinkProperty(Object, Property)} operation.
     * 
     * @param reference the engine specific representation of the link
     * @param property the property to be serialized
     * @throws RuntimeException if there is any exception on storage engine during this operation
     * @throws IllegalArgumentException if property param is null
     */
    private void serializeLinkProperty(final RL reference, final Property property)
        throws RuntimeException, IllegalArgumentException {
        checkNotNull("property", property);
        try {
            storageEngine.setLinkProperty(reference, property);
        } catch (final Exception e) {
            handleException(e);
        }
    }

    /**
     * Persists the input node.<br>
     * If this session operates in {@link FlushMode#AUTO} mode, it persists the node directly into storage engine, otherwise it
     * keep it in memory.
     * 
     * @param node the node to be persisted
     * @throws RuntimeException if there is any exception on storage engine during this operation
     * @throws IllegalArgumentException if input param is null
     */
    private void persistNode(final StorageNode node)
        throws RuntimeException, IllegalArgumentException {
        checkNotNull("node", node);
        try {
            RN reference;
            switch (getFlushMode()) {
                case AUTO:
                    reference = storageEngine.createNodeReference(node);
                    storageEngine.persistNode(reference, node);
                    break;
                case EXPLICIT:
                    reference = storageEngine.createNodeReference(node);
                    newNodes.add(newPair(node, reference));
                    break;
            }
        } catch (final Exception e) {
            handleException(e);
        }
    }

    /**
     * Deletes the input node.<br>
     * If this session operates in {@link FlushMode#AUTO} mode, it deletes the node directly from storage engine, otherwise it
     * keep this operation to be executed later (on {@link #flushTransient()}).
     * 
     * @param node the node to be deleted
     * @throws RuntimeException if there is any exception on storage engine during this operation
     * @throws IllegalArgumentException if input param is null
     */
    private void deleteNode(final StorageNode node)
        throws RuntimeException, IllegalArgumentException {
        checkNotNull("node", node);
        try {
            switch (getFlushMode()) {
                case AUTO:
                    storageEngine.deleteNode(node);
                    break;
                case EXPLICIT:
                    removedNodes.add(node);
                    break;
            }
        } catch (final Exception e) {
            handleException(e);
        }
    }

    /**
     * Collects children from input node and store into parameter list.
     * 
     * @param node the node to collect its children
     * @param collectedChildrenList list that all collected children references will be added
     * @throws IllegalArgumentException if any input param is null
     */
    private void collectChildren(final StorageNode node, final List<StorageNode> collectedChildrenList)
        throws IllegalArgumentException {
        checkNotNull("node", node);
        checkNotNull("removedItems", collectedChildrenList);

        collectedChildrenList.add(node);
        for (final Partition p: partitionFactory.getValues()) {
            final Iterable<StorageNode> children = node.getChildren(p, this);
            for (final StorageNode e: children) {
                collectChildren(e, collectedChildrenList);
            }
        }
    }

    /**
     * Creates a new {@link NodeBuilder} based on input params.
     * 
     * @param partition the partition where node should be stored
     * @param parent the parent node
     * @param type the node type to be created
     * @return a node builder filled with input params
     * @throws IllegalArgumentException if any input param is null or empty
     */
    public NodeBuilder createNode(final Partition partition, final StorageNode parent, final String type)
        throws IllegalArgumentException {
        checkNotNull("partition", partition);
        checkNotNull("parent", parent);
        checkNotEmpty("type", type);
        return withPartition(partition).createWithType(StorageSessionImpl.this, type).withParent(parent);
    }

    /**
     * Query storage engine and returns an iterable of children nodes of the input node stored into specific partition.
     * 
     * @param partition the partion to lookup for children nodes
     * @param node the node to get its children
     * @return an iterable of children nodes, or empty if not found
     * @throws RuntimeException if there is any exception on storage engine during this operation
     * @throws IllegalArgumentException if any input param is null
     */
    public Iterable<StorageNode> getChildren(final Partition partition, final StorageNode node)
        throws RuntimeException, IllegalArgumentException {
        checkNotNull("partition", partition);
        checkNotNull("node", node);
        try {
            return storageEngine.getChildren(partition, node);
        } catch (final Exception e) {
            handleException(e);
        }
        return null;
    }

    /**
     * Query storage engine and returns an iterable of children nodes of the input node restricted by a given type and stored into
     * specific partition.
     * 
     * @param partition the partion to lookup for children nodes
     * @param node the node to get its children
     * @param type the node type filter
     * @return an iterable of children nodes, or empty if not found
     * @throws RuntimeException if there is any exception on storage engine during this operation
     * @throws IllegalArgumentException if any input param is null or empty
     */
    public Iterable<StorageNode> getChildren(final Partition partition, final StorageNode node, final String type)
        throws RuntimeException, IllegalArgumentException {
        checkNotNull("partition", partition);
        checkNotNull("node", node);
        checkNotEmpty("type", type);
        try {
            return storageEngine.getChildren(partition, node, type);
        } catch (final Exception e) {
            handleException(e);
        }
        return null;
    }

    /**
     * Query storage engine and returns the parent node of the input.
     * 
     * @param node the input node to get parent from
     * @return parent node, or null if there is no parent
     * @throws RuntimeException if there is any exception on storage engine during this operation
     * @throws IllegalArgumentException if input param is null
     */
    public StorageNode getParent(final StorageNode node)
        throws RuntimeException, IllegalArgumentException {
        checkNotNull("node", node);
        try {
            return storageEngine.getParent(node);
        } catch (final Exception e) {
            handleException(e);
        }
        return null;
    }

    /**
     * Query storage engine and returns all existing properties, or an empty {@link Set}, of the input element. <br>
     * 
     * @param element element to get properties from
     * @return all properties of the input element
     * @throws RuntimeException if there is any exception on storage engine during this operation
     * @throws IllegalArgumentException if input param is null
     */
    public Set<Property> getProperties(final PropertyContainer element)
        throws RuntimeException, IllegalArgumentException {
        checkNotNull("element", element);
        try {
            return storageEngine.getProperties(element);
        } catch (final Exception e) {
            handleException(e);
        }
        return null;
    }

    /**
     * Query storage engine to return the property value as byte array.
     * 
     * @param property the input property to get value from
     * @return the value as byte array
     * @throws RuntimeException if there is any exception on storage engine during this operation
     * @throws IllegalArgumentException if input param is null
     */
    public byte[] getPropertyValue(final Property property)
        throws RuntimeException {
        checkNotNull("property", property);
        try {
            return storageEngine.getPropertyValue(property);
        } catch (final Exception e) {
            handleException(e);
        }
        return null;
    }

    /**
     * Sets the property value. <br>
     * If this session operates in {@link FlushMode#AUTO} mode, it stores the value directly into storage engine, otherwise it
     * keep the value in memory.
     * 
     * @param property property to be setted
     * @param value the value to be stored
     * @throws RuntimeException if there is any exception on storage engine during this operation
     * @throws IllegalArgumentException if property param is null
     */
    public void setPropertyValue(final Property property, final byte[] value)
        throws RuntimeException, IllegalArgumentException {
        checkNotNull("property", property);

        if (property.getParent() instanceof StorageNode) {
            if (flushMode.equals(FlushMode.AUTO)) {
                serializeNodeProperty(null, property);
            } else {
                dirtyNodeProperties.put((StorageNode) property.getParent(), property);
            }
        } else if (property.getParent() instanceof StorageLink) {
            if (flushMode.equals(FlushMode.AUTO)) {
                serializeLinkProperty(null, property);
            } else {
                dirtyLinkProperties.put((StorageLink) property.getParent(), property);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StorageLink addLink(final StorageNode source, final StorageNode target, final String type)
        throws RuntimeException, IllegalArgumentException {
        checkNotNull("source", source);
        checkNotNull("target", target);
        checkNotEmpty("type", type);

        final StorageLink link = new StorageLinkImpl(type, source, target, true);
        if (getFlushMode().equals(FlushMode.AUTO)) {
            try {
                storageEngine.persistLink(link);
            } catch (final Exception e) {
                handleException(e);
            }
        } else {
            final RL ref = storageEngine.createLinkReference(link);
            final Pair<StorageLink, RL> pair = newPair(link, ref);
            StorageSessionImpl.this.newLinks.add(pair);
        }
        return link;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void discardTransient() {
        this.newNodes.clear();
        this.removedNodes.clear();
        this.dirtyNodeProperties.clear();
        this.dirtyLinkProperties.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flushTransient()
        throws RuntimeException {
        if (getFlushMode() == FlushMode.AUTO) { return; }
        final Set<Partition> partitions = newHashSet();
        final Map<StorageNode, RN> referenceNodeMap = newHashMap();
        final Map<StorageLink, RL> referenceLinkMap = newHashMap();

        for (final Pair<StorageNode, RN> newNode: newNodes) {
            try {
                partitions.add(newNode.getK1().getKey().getPartition());
                storageEngine.persistNode(newNode.getK2(), newNode.getK1());
                referenceNodeMap.put(newNode.getK1(), newNode.getK2());
            } catch (final Exception e) {
                handleException(e);
            }
        }
        for (final Pair<StorageLink, RL> link: this.newLinks) {
            try {
                storageEngine.persistLink(link.getK1());
                referenceLinkMap.put(link.getK1(), link.getK2());
            } catch (final Exception e) {
                handleException(e);
            }
        }

        for (final StorageNode node: dirtyNodeProperties.keySet()) {
            partitions.add(node.getPartition());

            RN reference = referenceNodeMap.get(node);
            for (final Property data: dirtyNodeProperties.get(node)) {
                try {
                    serializeNodeProperty(reference, data);
                } catch (final Exception e) {
                    handleException(e);
                }
            }
        }

        for (final StorageLink link: dirtyLinkProperties.keySet()) {
            partitions.add(link.getPartition());

            RL reference = referenceLinkMap.get(link);
            for (final Property data: dirtyLinkProperties.get(link)) {
                try {
                    serializeLinkProperty(reference, data);
                } catch (final Exception e) {
                    handleException(e);
                }
            }
        }

        for (final StorageNode removedNode: removedNodes) {
            try {
                partitions.add(removedNode.getKey().getPartition());
                storageEngine.deleteNode(removedNode);
            } catch (final Exception e) {
                handleException(e);
            }
        }
        for (final StorageLink link: this.removedLinks) {
            try {
                storageEngine.deleteLink(link);
            } catch (final Exception e) {
                handleException(e);
            }
        }
        try {
            storageEngine.save(partitions.toArray(new Partition[partitions.size()]));
        } catch (final Exception e) {
            handleException(e);
        }
        discardTransient();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FlushMode getFlushMode() {
        return flushMode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StorageLink getLink(final StorageNode source, final StorageNode target, final String type)
        throws RuntimeException, IllegalArgumentException {
        checkNotNull("source", source);
        checkNotNull("target", target);
        checkNotEmpty("type", type);
        try {
            return SLCollections.firstOf(storageEngine.getLinks(source, target, type));
        } catch (final Exception e) {
            handleException(e);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<StorageLink> getLinks(final StorageNode source)
        throws RuntimeException, IllegalArgumentException {
        checkNotNull("source", source);

        try {
            return storageEngine.getLinks(source, null, null);
        } catch (final Exception e) {
            handleException(e);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<StorageLink> getLinks(final StorageNode source, final StorageNode target)
        throws RuntimeException, IllegalArgumentException {
        checkNotNull("source", source);
        checkNotNull("target", target);
        try {
            return storageEngine.getLinks(source, target, null);
        } catch (final Exception e) {
            handleException(e);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<StorageLink> getLinks(final StorageNode source, final String type)
        throws RuntimeException, IllegalArgumentException {
        checkNotNull("source", source);
        checkNotEmpty("type", type);
        try {
            return storageEngine.getLinks(source, null, type);
        } catch (final Exception e) {
            handleException(e);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StorageNode getNode(final String key)
        throws RuntimeException, IllegalArgumentException {
        checkNotEmpty("key", key);
        final Partition partition = partitionFactory.getPartition(StringKeysSupport.getPartitionName(key));
        return withPartition(partition).createCriteria().withUniqueKeyAsString(key).buildCriteria().andSearchUnique(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeLink(final StorageLink link)
        throws RuntimeException, IllegalArgumentException {
        checkNotNull("link", link);
        try {
            switch (getFlushMode()) {
                case AUTO:
                    storageEngine.deleteLink(link);
                    break;
                case EXPLICIT:
                    removedLinks.add(link);
                    break;
            }
        } catch (final Exception e) {
            handleException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeLink(final StorageNode source, final StorageNode target, final String type)
        throws RuntimeException, IllegalArgumentException {
        checkNotNull("source", source);
        checkNotNull("target", target);
        checkNotNull("type", type);

        removeLink(new StorageLinkImpl(type, source, target, false));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeNode(final StorageNode node)
        throws RuntimeException, IllegalArgumentException {
        checkNotNull("node", node);

        final List<StorageNode> removedItems = newLinkedList();
        collectChildren(node, removedItems);
        Collections.reverse(removedItems);
        for (final StorageNode r: removedItems) {
            deleteNode(r);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartitionMethods withPartition(final Partition partition)
        throws IllegalArgumentException {
        checkNotNull("partition", partition);

        PartitionMethods result = partitionMethods.get(partition);
        if (result == null) {
            result = new PartitionMethodsImpl(partition);
            partitionMethods.put(partition, result);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeResources() {
        storageEngine.closeResources();
    }

}
