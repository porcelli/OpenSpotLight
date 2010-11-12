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

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static org.openspotlight.common.Pair.newPair;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openspotlight.common.Pair;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
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

/**
 * Created by User: feu - Date: Mar 22, 2010 - Time: 2:19:49 PM
 */
public class StorageSessionImpl<R> implements StorageSession {

    /**
     * Internal (default) implementation of {@link NodeFactory.NodeBuilder}.
     * 
     * @author feuteston
     * @author porcelli
     */
    private final class NodeBuilderImpl implements NodeFactory.NodeBuilder {

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
        public StorageNode andCreate() {
            final CompositeKeyImpl localKey = new CompositeKeyImpl(keys, type);

            final NodeKeyImpl uniqueKey = new NodeKeyImpl(localKey, parentKey, partition);
            final StorageNodeImpl result = new StorageNodeImpl(uniqueKey, false);
            if (getFlushMode().equals(FlushMode.AUTO)) {
                StorageSessionImpl.this.handleNewItem(result);
            } else {
                final R ref = storageEngine.createNodeReference(result);
                final Pair<StorageNode, R> pair = newPair((StorageNode) result, ref);
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
            checkNotEmpty("parentAsString", key);

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
        public StorageNode createNewSimpleNode(final String... nodeTypes) {
            StorageNode parent = null;
            NodeKey parentKey = null;
            for (final String nodeType: nodeTypes) {
                parentKey =
                    new NodeKeyImpl(new CompositeKeyImpl(Collections.<SimpleKey>emptySet(), nodeType),
                        parentKey != null ? parentKey.getKeyAsString() : null,
                        partition);
                parent = new StorageNodeImpl(parentKey, false);
                handleNewItem(parent);
            }

            return parent;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public NodeKeyBuilder createNodeKeyWithType(final String nodeType) {
            return new NodeKeyBuilderImpl(nodeType, partition);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public NodeFactory.NodeBuilder createNodeWithType(final String type) {
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
        public Iterable<String> getAllNodeTypes() {
            try {
                return storageEngine.getAllNodeTypes(partition);
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterable<StorageNode> getNodes(final String nodeType) {
            try {
                return storageEngine.getNodes(partition, nodeType);
            } catch (final Exception e) {
                handleException(e);
                return null;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterable<StorageNode> search(final NodeCriteria criteria) {
            try {
                if (!criteria.getPartition().equals(partition)) { throw new IllegalArgumentException(); }
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
                    if (hasOther && hasGlobal) { throw new IllegalArgumentException(); }
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
        public StorageNode searchUnique(final NodeCriteria criteria) {
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

    private final FlushMode                             flushMode;

    private final PartitionFactory                      partitionFactory;

    private final StorageEngineBind<R>                  storageEngine;

    private final Map<Partition, PartitionMethods>      partitionMethods = newHashMap();

    private final Multimap<PropertyContainer, Property> dirtyProperties  = ArrayListMultimap.create();

    private final Set<Pair<StorageLink, R>>             newLinks         = newLinkedHashSet();

    private final Set<Pair<StorageNode, R>>             newNodes         = newLinkedHashSet();

    private final Set<StorageLink>                      removedLinks     = newLinkedHashSet();

    private final Set<StorageNode>                      removedNodes     = newLinkedHashSet();

    public StorageSessionImpl(final FlushMode flushMode, final PartitionFactory partitionFactory,
                              final StorageEngineBind<R> storageEngine) {
        if (flushMode == null) { throw new NullPointerException(); }
        if (partitionFactory == null) { throw new NullPointerException(); }
        this.flushMode = flushMode;
        this.partitionFactory = partitionFactory;
        this.storageEngine = storageEngine;
    }

    private void flushDirtyProperty(final R reference, final Property dirtyProperty) {
        try {
            storageEngine.flushSimpleProperty(reference, dirtyProperty.getParent().getPartition(), dirtyProperty);
        } catch (final Exception e) {
            handleException(e);
        }
    }

    private void handleNewItem(final StorageNode entry) {
        try {
            R reference;
            switch (getFlushMode()) {
                case AUTO:
                    reference = storageEngine.createNodeReference(entry);
                    storageEngine.flushNewItem(reference, entry);
                    break;
                case EXPLICIT:
                    reference = storageEngine.createNodeReference(entry);
                    newNodes.add(newPair(entry, reference));
                    break;
                default:
                    throw new IllegalStateException();
            }
        } catch (final Exception e) {
            handleException(e);
        }
    }

    private void handleRemovedItem(final StorageNode entry) {
        try {
            switch (getFlushMode()) {
                case AUTO:
                    storageEngine.flushRemovedItem(entry);
                    break;
                case EXPLICIT:
                    removedNodes.add(entry);
                    break;
            }
        } catch (final Exception e) {
            handleException(e);
        }
    }

    private void searchItemsToRemove(final StorageNode node, final List<StorageNode> removedItems) {
        removedItems.add(node);
        for (final Partition p: partitionFactory.getValues()) {
            final Iterable<StorageNode> children = node.getChildren(p, this);
            for (final StorageNode e: children) {
                searchItemsToRemove(e, removedItems);
            }
        }
    }

    private void handleException(final Exception e) {
        if (e instanceof RuntimeException) { throw (RuntimeException) e; }
        throw new RuntimeException(e);
    }

    public NodeFactory.NodeBuilder nodeEntryCreateWithType(final StorageNode StorageNode, final String type) {
        return withPartition(StorageNode.getPartition()).createWithType(StorageSessionImpl.this, type)
            .withParent(StorageNode);
    }

    public Iterable<StorageNode> nodeEntryGetChildren(final Partition partition, final StorageNode node) {
        try {
            return storageEngine.getChildren(node);
        } catch (final Exception e) {
            handleException(e);
        }
        return null;
    }

    public Iterable<StorageNode> nodeEntryGetChildrenByType(final Partition partition, final StorageNode node,
                                                            final String type) {
        if (!partition.equals(node.getPartition())) { throw new IllegalArgumentException(
            "wrong partition for this node entry"); }
        try {
            return storageEngine.getChildren(node, type);
        } catch (final Exception e) {
            handleException(e);
        }
        return null;
    }

    public StorageNode nodeEntryGetParent(final StorageNode node) {
        try {
            return storageEngine.getParent(node);
        } catch (final Exception e) {
            handleException(e);
        }
        return null;
    }

    public Set<Property> propertyContainerLoadProperties(final PropertyContainer storageNode) {
        try {
            return storageEngine.loadProperties(storageNode);
        } catch (final Exception e) {
            handleException(e);
        }
        return null;
    }

    public byte[] propertyGetValue(final Property stProperty) {
        try {
            return storageEngine.getPropertyValue(stProperty.getParent().getPartition(), stProperty);
        } catch (final Exception e) {
            handleException(e);
        }
        return null;
    }

    public void propertySetProperty(final Property stProperty, final byte[] value) {
        if (flushMode.equals(FlushMode.AUTO)) {
            flushDirtyProperty(null, stProperty);
        } else {
            dirtyProperties.put(stProperty.getParent(), stProperty);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StorageLink addLink(final StorageNode source, final StorageNode target, final String type) {
        final StorageLink link = new StorageLinkImpl(type, source, target, true);
        if (getFlushMode().equals(FlushMode.AUTO)) {
            try {
                storageEngine.handleNewLink(link.getSource(), link);
            } catch (final Exception e) {
                handleException(e);
            }
        } else {
            final R ref = storageEngine.createLinkReference(link);
            final Pair<StorageLink, R> pair = newPair(link, ref);
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
        this.dirtyProperties.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flushTransient() {
        if (getFlushMode() == FlushMode.AUTO) { return; }
        final Set<Partition> partitions = newHashSet();
        final Map<PropertyContainer, R> referenceMap = newHashMap();
        for (final Pair<StorageNode, R> newNode: newNodes) {
            try {
                partitions.add(newNode.getK1().getKey().getPartition());
                storageEngine.flushNewItem(newNode.getK2(), newNode.getK1());
                referenceMap.put(newNode.getK1(), newNode.getK2());
            } catch (final Exception e) {
                handleException(e);
            }
        }
        for (final PropertyContainer propertyContainer: dirtyProperties.keySet()) {
            partitions.add(propertyContainer.getPartition());

            R reference = referenceMap.get(propertyContainer);
            if (reference == null) {
                if (propertyContainer instanceof StorageNode) {
                    reference = storageEngine.createNodeReference((StorageNode) propertyContainer);
                } else if (propertyContainer instanceof StorageLink) {
                    reference = storageEngine.createLinkReference((StorageLink) propertyContainer);
                } else {
                    throw new IllegalStateException();
                }
            }
            for (final Property data: dirtyProperties.get(propertyContainer)) {
                try {
                    flushDirtyProperty(reference, data);
                } catch (final Exception e) {
                    handleException(e);
                }
            }

        }
        for (final StorageNode removedNode: removedNodes) {
            try {
                partitions.add(removedNode.getKey().getPartition());
                storageEngine.flushRemovedItem(removedNode);
            } catch (final Exception e) {
                handleException(e);
            }
        }
        for (final Pair<StorageLink, R> p: this.newLinks) {
            try {
                storageEngine.handleNewLink(p.getK1().getSource(), p.getK1());
            } catch (final Exception e) {
                handleException(e);
            }
        }
        for (final StorageLink link: this.removedLinks) {
            try {
                storageEngine.flushRemovedLink(link);
            } catch (final Exception e) {
                handleException(e);
            }
        }
        try {
            storageEngine.savePartitions(partitions.toArray(new Partition[partitions.size()]));
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
    public StorageLink getLink(final StorageNode source, final StorageNode target, final String type) {
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
    public Iterable<StorageLink> getLinks(final StorageNode source) {
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
    public Iterable<StorageLink> getLinks(final StorageNode source, final StorageNode target) {
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
    public Iterable<StorageLink> getLinks(final StorageNode source, final String type) {
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
    public StorageNode getNode(final String key) {
        final Partition partition = partitionFactory.getPartition(StringKeysSupport.getPartitionName(key));
        return withPartition(partition).createCriteria().withUniqueKeyAsString(key).buildCriteria().andSearchUnique(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeLink(final StorageLink link) {
        try {
            switch (getFlushMode()) {
                case AUTO:
                    storageEngine.flushRemovedLink(link);
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
    public void removeLink(final StorageNode source, final StorageNode target, final String type) {
        removeLink(new StorageLinkImpl(type, source, target, false));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeNode(final StorageNode node) {
        final List<StorageNode> removedItems = new LinkedList<StorageNode>();
        searchItemsToRemove(node, removedItems);
        Collections.reverse(removedItems);
        for (final StorageNode r: removedItems) {
            handleRemovedItem(r);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartitionMethods withPartition(final Partition partition) {
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
