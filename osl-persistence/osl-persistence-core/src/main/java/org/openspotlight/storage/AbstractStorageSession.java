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
import org.openspotlight.storage.Criteria.CriteriaBuilder;
import org.openspotlight.storage.Criteria.CriteriaItem;
import org.openspotlight.storage.Criteria.CriteriaItem.CompositeKeyCriteriaItem;
import org.openspotlight.storage.Criteria.CriteriaItem.NodeKeyCriteriaItem;
import org.openspotlight.storage.Criteria.CriteriaItem.PropertyContainsString;
import org.openspotlight.storage.Criteria.CriteriaItem.PropertyCriteriaItem;
import org.openspotlight.storage.Criteria.CriteriaItem.PropertyEndsWithString;
import org.openspotlight.storage.Criteria.CriteriaItem.PropertyStartsWithString;
import org.openspotlight.storage.CriteriaImpl.CriteriaBuilderImpl;
import org.openspotlight.storage.domain.NodeFactory;
import org.openspotlight.storage.domain.NodeFactory.NodeBuilder;
import org.openspotlight.storage.domain.Property;
import org.openspotlight.storage.domain.PropertyContainer;
import org.openspotlight.storage.domain.StorageLink;
import org.openspotlight.storage.domain.StorageNode;
import org.openspotlight.storage.domain.key.NodeKey;
import org.openspotlight.storage.domain.key.NodeKey.CompositeKey;
import org.openspotlight.storage.domain.key.NodeKey.CompositeKey.SimpleKey;
import org.openspotlight.storage.domain.key.NodeKeyImpl;
import org.openspotlight.storage.domain.key.NodeKeyImpl.CompositeKeyImpl;
import org.openspotlight.storage.domain.key.NodeKeyImpl.CompositeKeyImpl.SimpleKeyImpl;
import org.openspotlight.storage.domain.node.StorageLinkImpl;
import org.openspotlight.storage.domain.node.StorageNodeImpl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * Created by User: feu - Date: Mar 22, 2010 - Time: 2:19:49 PM
 */
public abstract class AbstractStorageSession<R> implements StorageSession {

    private final class NodeBuilderImpl implements
        NodeFactory.NodeBuilder {

        private final Set<String>    keyNames  = newHashSet();

        private final Set<SimpleKey> keys      = newHashSet();

        private String               parentKey = null;

        private final Partition      partition;

        private final String         type;

        private NodeBuilderImpl(final String type, final Partition partition) {
            this.type = type;
            this.partition = partition;
        }

        private NodeFactory.NodeBuilder withParentKey(
                                                      final NodeKey parentKey)
            throws IllegalArgumentException, IllegalStateException {
            checkNotNull("parentKey", parentKey);

            if (this.parentKey != null) { throw new IllegalStateException(); }
            this.parentKey = parentKey.getKeyAsString();
            return this;
        }

        @Override
        public StorageNode andCreate() {
            final CompositeKeyImpl localKey = new CompositeKeyImpl(keys, type);

            final NodeKeyImpl uniqueKey = new NodeKeyImpl(localKey, parentKey, partition);
            final StorageNodeImpl result = new StorageNodeImpl(uniqueKey, false);
            if (getFlushMode().equals(FlushMode.AUTO)) {

                AbstractStorageSession.this.handleNewItem(result);
            } else {
                final R ref = AbstractStorageSession.this
                    .createNodeReferenceIfNecessary(partition, result);
                final Pair<StorageNode, R> pair = newPair((StorageNode) result, ref);
                AbstractStorageSession.this.newNodes.add(pair);
            }
            return result;
        }

        @Override
        public NodeFactory.NodeBuilder withParent(
                                                  final StorageNode parent)
            throws IllegalArgumentException, IllegalStateException {
            checkNotNull("parent", parent);

            return withParentKey(parent.getKey());
        }

        @Override
        public NodeBuilder withParent(final String parentAsString) {
            checkNotEmpty("parentAsString", parentAsString);

            this.parentKey = parentAsString;
            return this;
        }

        @Override
        public NodeFactory.NodeBuilder withSimpleKey(
                                                     final String name,
                                                     final String value)
            throws IllegalArgumentException, IllegalStateException {
            checkNotEmpty("name", name);
            checkNotEmpty("value", value);

            if (keyNames.contains(name)) { throw new IllegalStateException("key name already inserted"); }
            this.keys.add(new SimpleKeyImpl(name, value));
            this.keyNames.add(name);
            return this;
        }
    }

    public static class NodeKeyBuilderImpl implements NodeKeyBuilder {

        private final NodeKeyBuilderImpl child;
        private final Set<SimpleKey>     localEntries = newHashSet();

        private String                   parentKey;

        private final Partition          partition;

        private final String             type;

        private NodeKeyBuilderImpl(final String type,
                                     final NodeKeyBuilderImpl child, final Partition partition) {
            this.type = type;
            this.child = child;
            this.partition = partition;
        }

        public NodeKeyBuilderImpl(final String type, final Partition partition) {
            this.type = type;
            this.partition = partition;
            this.child = null;
        }

        @Override
        public NodeKey andCreate() {

            NodeKey currentKey = null;
            NodeKeyBuilderImpl currentBuilder = this;
            if (parentKey == null) {
                do {
                    final CompositeKey localKey = new CompositeKeyImpl(
                        currentBuilder.localEntries, currentBuilder.type);
                    currentKey = new NodeKeyImpl(localKey,
                        currentKey != null ? currentKey.getKeyAsString()
                            : null, currentBuilder.partition);
                    currentBuilder = currentBuilder.child;
                } while (currentBuilder != null);
            } else {
                final CompositeKey localKey = new CompositeKeyImpl(
                    currentBuilder.localEntries, currentBuilder.type);
                currentKey = new NodeKeyImpl(localKey, parentKey,
                    partition);

            }
            return currentKey;

        }

        @Override
        public NodeKeyBuilder withParent(
                                           final Partition newPartition,
                                           final String nodeType) {
            return new NodeKeyBuilderImpl(nodeType, this,
                newPartition);
        }

        @Override
        public NodeKeyBuilder withParent(
                                           final String parentId) {
            this.parentKey = parentId;
            return this;
        }

        @Override
        public NodeKeyBuilder withSimpleKey(
                                            final String propertyName,
                                            final String value) {
            this.localEntries.add(new SimpleKeyImpl(propertyName, value));
            return this;
        }

    }

    public class PartitionMethodsImpl implements PartitionMethods {

        private final Partition partition;

        private PartitionMethodsImpl(final Partition currentPartition) {
            this.partition = currentPartition;
        }

        @Override
        public CriteriaBuilder createCriteria() {
            return new CriteriaBuilderImpl(partition);
        }

        @Override
        public NodeKeyBuilder createKey(
                                          final String nodeType) {
            return new NodeKeyBuilderImpl(nodeType, partition);
        }

        @Override
        public NodeKey createNewSimpleKey(
                                            final String... nodePaths) {
            NodeKey parentKey = null;
            for (final String path: nodePaths) {
                parentKey =
                    new NodeKeyImpl(
                        new CompositeKeyImpl(Collections
                            .<SimpleKey>emptySet(), path),
                        parentKey
                            .getKeyAsString(),
                        partition);
            }
            return parentKey;
        }

        @Override
        public StorageNode createNewSimpleNode(
                                               final String... nodePaths) {
            StorageNode parent = null;
            NodeKey parentKey = null;
            for (final String nodePath: nodePaths) {
                parentKey = new NodeKeyImpl(new CompositeKeyImpl(Collections
                    .<SimpleKey>emptySet(), nodePath),
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
        public NodeFactory.NodeBuilder createWithType(final StorageSession session,
                                                        final String type)
            throws IllegalArgumentException {
            checkNotNull("session", session);
            checkNotEmpty("type", type);

            return new NodeBuilderImpl(type, partition);
        }

        @Override
        public NodeFactory.NodeBuilder createWithType(final String type) {
            checkNotEmpty("type", type);

            return this.createWithType(AbstractStorageSession.this, type);
        }

        @Override
        public Iterable<StorageNode> findByCriteria(final Criteria criteria) {
            try {
                if (!criteria.getPartition().equals(partition)) { throw new IllegalArgumentException(); }
                boolean hasGlobal = false;
                boolean hasOther = false;
                for (final CriteriaItem item: criteria.getCriteriaItems()) {
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
                return internalFindByCriteria(criteria.getPartition(), criteria);
            } catch (final Exception e) {
                handleException(e);
                return null;
            }
        }

        @Override
        public Iterable<StorageNode> findByType(
                                                final String nodeType) {
            try {
                return internalFindByType(partition, nodeType);
            } catch (final Exception e) {
                handleException(e);
                return null;
            }
        }

        @Override
        public StorageNode findUniqueByCriteria(
                                                final Criteria criteria) {
            try {
                final Iterable<StorageNode> result = findByCriteria(criteria);
                if (result == null) { return null; }
                final Iterator<StorageNode> it = result.iterator();
                if (!it.hasNext()) { return null; }
                return it.next();

            } catch (final Exception e) {
                handleException(e);
                return null;
            }
        }

        @Override
        public Iterable<String> getAllNodeTypes() {
            try {
                return internalGetAllNodeTypes(partition);
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
            }
        }

    }

    private final FlushMode                               flushMode;

    private final PartitionFactory                        partitionFactory;

    private final Map<Partition, PartitionMethods>        partitionMethods = newHashMap();

    protected final Multimap<PropertyContainer, Property> dirtyProperties  = ArrayListMultimap
                                                                               .create();

    protected final Set<Pair<StorageLink, R>>             newLinks         = newLinkedHashSet();

    protected final Set<Pair<StorageNode, R>>             newNodes         = newLinkedHashSet();

    protected final Set<StorageLink>                      removedLinks     = newLinkedHashSet();

    protected final Set<StorageNode>                      removedNodes     = newLinkedHashSet();

    protected AbstractStorageSession(final FlushMode flushMode,
                                     final PartitionFactory partitionFactory) {
        if (flushMode == null) { throw new NullPointerException(); }
        if (partitionFactory == null) { throw new NullPointerException(); }
        this.flushMode = flushMode;
        this.partitionFactory = partitionFactory;
    }

    private void flushDirtyProperty(
                                    final R reference,
                                    final Property dirtyProperty) {
        try {

            internalFlushSimpleProperty(reference, dirtyProperty.getParent()
                .getPartition(), dirtyProperty);

        } catch (final Exception e) {
            handleException(e);
        }
    }

    private void handleNewItem(
                               final StorageNode entry) {
        try {
            R reference;
            switch (getFlushMode()) {
                case AUTO:
                    reference = createNodeReferenceIfNecessary(entry.getKey()
                        .getPartition(), entry);
                    flushNewItem(reference, entry.getKey().getPartition(),
                        entry);
                    break;
                case EXPLICIT:
                    reference = createNodeReferenceIfNecessary(entry.getKey()
                        .getPartition(), entry);
                    newNodes.add(newPair(entry, reference));
                    break;
                default:
                    throw new IllegalStateException();
            }
        } catch (final Exception e) {
            handleException(e);
        }
    }

    private void handleRemovedItem(
                                   final StorageNode entry) {
        try {
            switch (getFlushMode()) {
                case AUTO:
                    flushRemovedItem(entry.getKey().getPartition(), entry);
                    break;
                case EXPLICIT:
                    removedNodes.add(entry);
                    break;
            }
        } catch (final Exception e) {
            handleException(e);
        }
    }

    private void searchItemsToRemove(
                                     final StorageNode StorageNode,
                                     final List<StorageNode> removedItems) {
        removedItems.add(StorageNode);
        final Iterable<Partition> partitions =
            SLCollections.iterableOf(StorageNode
                .getKey().getPartition(),
                partitionFactory.getValues());
        for (final Partition p: partitions) {
            final Iterable<StorageNode> children = StorageNode.getChildren(p, this);
            for (final StorageNode e: children) {
                searchItemsToRemove(e, removedItems);
            }
        }
    }

    protected abstract R createLinkReferenceIfNecessary(
                                                        Partition partition,
                                                        StorageLink entry);

    protected abstract R createNodeReferenceIfNecessary(
                                                        Partition partition,
                                                        StorageNode entry);

    protected abstract void flushNewItem(
                                         R reference,
                                         Partition partition,
                                         StorageNode entry)
        throws Exception;

    protected abstract void flushRemovedItem(
                                             Partition partition,
                                             StorageNode entry)
        throws Exception;

    protected abstract void flushRemovedLink(
                                             Partition partition,
                                             StorageLink link)
        throws Exception;

    protected void handleException(
                                   final Exception e) {
        if (e instanceof RuntimeException) { throw (RuntimeException) e; }
        throw new RuntimeException(e);
    }

    protected abstract void handleNewLink(
                                          Partition partition,
                                          StorageNode origin,
                                          StorageLink link)
        throws Exception;

    protected abstract Iterable<StorageNode> internalFindByCriteria(
                                                                    Partition partition,
                                                                    Criteria criteria)
        throws Exception;

    protected abstract Iterable<StorageNode> internalFindByType(
                                                                Partition partition,
                                                                String nodeType)
        throws Exception;

    protected abstract Iterable<StorageLink> internalFindLinks(
                                                               Partition partition,
                                                               StorageNode origin,
                                                               StorageNode destiny,
                                                               String type)
        throws Exception;

    protected abstract void internalFlushSimpleProperty(
                                                        R reference,
                                                        Partition partition,
                                                        Property dirtyProperty)
        throws Exception;

    protected abstract Iterable<String> internalGetAllNodeTypes(
                                                                Partition partition)
        throws Exception;

    protected abstract Iterable<StorageNode> internalNodeEntryGetChildren(
                                                                          Partition partition,
                                                                          StorageNode StorageNode)
        throws Exception;

    protected abstract Iterable<StorageNode> internalNodeEntryGetChildrenByType(
                                                                                Partition partition,
                                                                                StorageNode StorageNode,
                                                                                String type)
        throws Exception;

    protected abstract StorageNode internalNodeEntryGetParent(
                                                              Partition partition,
                                                              StorageNode StorageNode)
        throws Exception;

    protected abstract Set<Property> internalPropertyContainerLoadProperties(
                                                                             R reference,
                                                                             Partition partition,
                                                                             PropertyContainer StorageNode)
        throws Exception;

    protected abstract byte[] internalPropertyGetValue(
                                                       Partition partition,
                                                       Property stProperty)
        throws Exception;

    protected abstract void internalSavePartitions(
                                                   Partition... partitions)
        throws Exception;

    @Override
    public StorageLink addLink(
                               final StorageNode origin,
                               final StorageNode target,
                               final String type) {
        final StorageLink link = new StorageLinkImpl(type, origin, target, true);
        if (getFlushMode().equals(FlushMode.AUTO)) {
            try {
                this.handleNewLink(link.getOrigin().getPartition(), link
                    .getOrigin(), link);
            } catch (final Exception e) {
                handleException(e);
            }
        } else {
            final R ref = createLinkReferenceIfNecessary(origin.getPartition(), link);
            final Pair<StorageLink, R> pair = newPair(link, ref);
            AbstractStorageSession.this.newLinks.add(pair);
        }
        return link;
    }

    @Override
    public void discardTransient() {
        this.newNodes.clear();
        this.removedNodes.clear();
        this.dirtyProperties.clear();
    }

    @Override
    public Iterable<StorageLink> findLinks(
                                           final StorageNode origin) {
        try {
            return internalFindLinks(origin.getPartition(), origin, null, null);
        } catch (final Exception e) {
            handleException(e);
            return null;
        }
    }

    @Override
    public Iterable<StorageLink> findLinks(
                                           final StorageNode origin,
                                           final StorageNode destiny) {
        try {
            return internalFindLinks(origin.getPartition(), origin, destiny,
                null);
        } catch (final Exception e) {
            handleException(e);
            return null;
        }
    }

    @Override
    public Iterable<StorageLink> findLinks(
                                           final StorageNode origin,
                                           final String type) {
        try {
            return internalFindLinks(origin.getPartition(), origin, null, type);
        } catch (final Exception e) {
            handleException(e);
            return null;
        }
    }

    @Override
    public StorageNode findNodeByStringKey(
                                           final String idAsString) {

        final Partition partition = partitionFactory
            .getPartitionByName(StringKeysSupport
                .getPartitionName(idAsString));
        return withPartition(partition).createCriteria().withUniqueKeyAsString(
            idAsString).buildCriteria().andFindUnique(this);
    }

    @Override
    public void flushTransient() {
        final Set<Partition> partitions = newHashSet();
        final Map<PropertyContainer, R> referenceMap = newHashMap();
        for (final Pair<StorageNode, R> newNode: newNodes) {
            try {
                partitions.add(newNode.getK1().getKey().getPartition());
                flushNewItem(newNode.getK2(), newNode.getK1().getKey()
                    .getPartition(), newNode.getK1());
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
                    reference = createNodeReferenceIfNecessary(
                        propertyContainer.getPartition(),
                        (StorageNode) propertyContainer);
                } else if (propertyContainer instanceof StorageLink) {
                    reference = createLinkReferenceIfNecessary(
                        propertyContainer.getPartition(),
                        (StorageLink) propertyContainer);
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
                flushRemovedItem(removedNode.getKey().getPartition(),
                    removedNode);
            } catch (final Exception e) {
                handleException(e);
            }
        }
        for (final Pair<StorageLink, R> p: this.newLinks) {
            try {
                handleNewLink(p.getK1().getPartition(), p.getK1().getOrigin(),
                    p.getK1());
            } catch (final Exception e) {
                handleException(e);
            }
        }
        for (final StorageLink link: this.removedLinks) {
            try {
                flushRemovedLink(link.getPartition(), link);
            } catch (final Exception e) {
                handleException(e);
            }
        }
        try {

            internalSavePartitions(partitions
                .toArray(new Partition[partitions.size()]));
        } catch (final Exception e) {
            handleException(e);
        }
        discardTransient();

    }

    @Override
    public FlushMode getFlushMode() {
        return flushMode;
    }

    @Override
    public StorageLink getLink(
                               final StorageNode origin,
                               final StorageNode destiny,
                               final String type) {
        try {
            return SLCollections.firstOf(internalFindLinks(origin
                .getPartition(), origin, destiny, type));
        } catch (final Exception e) {
            handleException(e);
            return null;
        }
    }

    public NodeFactory.NodeBuilder nodeEntryCreateWithType(
                                                           final StorageNode StorageNode,
                                                           final String type) {
        return withPartition(StorageNode.getPartition()).createWithType(
            AbstractStorageSession.this, type)
            .withParent(StorageNode);
    }

    public Iterable<StorageNode> nodeEntryGetChildren(
                                                      final Partition partition,
                                                      final StorageNode StorageNode) {
        try {
            return internalNodeEntryGetChildren(partition, StorageNode);
        } catch (final Exception e) {
            handleException(e);
        }
        return null;
    }

    public Iterable<StorageNode> nodeEntryGetChildrenByType(
                                                            final Partition partition,
                                                            final StorageNode StorageNode,
                                                            final String type) {
        if (!partition.equals(StorageNode.getKey().getPartition())) { throw new IllegalArgumentException(
            "wrong partition for this node entry"); }

        try {
            return internalNodeEntryGetChildrenByType(partition,
                StorageNode, type);

        } catch (final Exception e) {
            handleException(e);
        }
        return null;
    }

    public StorageNode nodeEntryGetParent(
                                          final StorageNode StorageNode) {
        try {
            return internalNodeEntryGetParent(StorageNode.getPartition(), StorageNode);
        } catch (final Exception e) {
            handleException(e);
        }
        return null;
    }

    public Set<Property> propertyContainerLoadProperties(final PropertyContainer storageNode) {
        try {
            return internalPropertyContainerLoadProperties(null, storageNode.getPartition(), storageNode);
        } catch (final Exception e) {
            handleException(e);
        }
        return null;
    }

    public byte[] propertyGetValue(
                                   final Property stProperty) {
        try {
            return internalPropertyGetValue(stProperty.getParent()
                .getPartition(), stProperty);
        } catch (final Exception e) {
            handleException(e);
        }
        return null;
    }

    public void propertySetProperty(
                                    final Property stProperty,
                                    final byte[] value) {
        if (flushMode.equals(FlushMode.AUTO)) {
            flushDirtyProperty(null, stProperty);
        } else {
            dirtyProperties.put(stProperty.getParent(), stProperty);
        }

    }

    @Override
    public void removeLink(
                           final StorageLink link) {
        try {
            switch (getFlushMode()) {
                case AUTO:
                    flushRemovedLink(link.getPartition(), link);
                    break;
                case EXPLICIT:
                    removedLinks.add(link);
                    break;
            }
        } catch (final Exception e) {
            handleException(e);
        }
    }

    @Override
    public void removeLink(
                           final StorageNode origin,
                           final StorageNode target,
                           final String type) {
        removeLink(new StorageLinkImpl(type, origin, target, false));
    }

    @Override
    public void removeNode(
                           final StorageNode StorageNode) {
        final List<StorageNode> removedItems = new LinkedList<StorageNode>();
        searchItemsToRemove(StorageNode, removedItems);
        Collections.reverse(removedItems);
        for (final StorageNode r: removedItems) {
            handleRemovedItem(r);
        }
    }

    @Override
    public PartitionMethods withPartition(
                                          final Partition partition) {
        PartitionMethods result = partitionMethods.get(partition);
        if (result == null) {
            result = new PartitionMethodsImpl(partition);
            partitionMethods.put(partition, result);
        }
        return result;
    }

}
