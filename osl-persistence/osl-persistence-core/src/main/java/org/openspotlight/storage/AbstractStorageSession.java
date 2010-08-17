/*
 * OpenSpotLight - Open Source IT Governance Platform Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA or third-party contributors as indicated by the @author tags or express copyright attribution statements applied by the
 * authors. All third-party contributions are distributed under license by CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA. This copyrighted material is made available to anyone wishing to use, modify, copy, or redistribute it subject to the
 * terms and conditions of the GNU Lesser General Public License, as published by the Free Software Foundation. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a
 * copy of the GNU Lesser General Public License along with this distribution; if not, write to: Free Software Foundation, Inc. 51
 * Franklin Street, Fifth Floor Boston, MA 02110-1301 USA ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto * Direitos Autorais Reservados (c) 2009, CARAVELATECH
 * CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor. Todas as contribuições de terceiros
 * estão distribuídas sob licença da CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. Este programa é software livre;
 * você pode redistribuí-lo e/ou modificá-lo sob os termos da Licença Pública Geral Menor do GNU conforme publicada pela Free
 * Software Foundation. Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA GARANTIA; nem mesmo a
 * garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU
 * para mais detalhes. Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este programa; se não,
 * escreva para: Free Software Foundation, Inc. 51 Franklin Street, Fifth Floor Boston, MA 02110-1301 USA
 */

package org.openspotlight.storage;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static org.openspotlight.common.Pair.newPair;

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
import org.openspotlight.storage.Criteria.CriteriaItem.CompisiteKeyCriteriaItem;
import org.openspotlight.storage.Criteria.CriteriaItem.NodeKeyCriteriaItem;
import org.openspotlight.storage.Criteria.CriteriaItem.PropertyContainsString;
import org.openspotlight.storage.Criteria.CriteriaItem.PropertyCriteriaItem;
import org.openspotlight.storage.Criteria.CriteriaItem.PropertyEndsWithString;
import org.openspotlight.storage.Criteria.CriteriaItem.PropertyStartsWithString;
import org.openspotlight.storage.CriteriaImpl.CriteriaBuilderImpl;
import org.openspotlight.storage.domain.Link;
import org.openspotlight.storage.domain.Node;
import org.openspotlight.storage.domain.NodeFactory;
import org.openspotlight.storage.domain.NodeFactory.NodeBuilder;
import org.openspotlight.storage.domain.Property;
import org.openspotlight.storage.domain.PropertyContainer;
import org.openspotlight.storage.domain.key.NodeKey;
import org.openspotlight.storage.domain.key.NodeKey.CompositeKey;
import org.openspotlight.storage.domain.key.NodeKey.CompositeKey.SimpleKey;
import org.openspotlight.storage.domain.key.NodeKeyImpl;
import org.openspotlight.storage.domain.key.NodeKeyImpl.CompositeKeyImpl;
import org.openspotlight.storage.domain.key.NodeKeyImpl.CompositeKeyImpl.SimpleKeyImpl;
import org.openspotlight.storage.domain.node.LinkImpl;
import org.openspotlight.storage.domain.node.NodeImpl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * Created by User: feu - Date: Mar 22, 2010 - Time: 2:19:49 PM
 */
public abstract class AbstractStorageSession<R> implements StorageSession {

    @Override
    public Node findNodeByStringId(
                                   final String idAsString) {

        final Partition partition = partitionFactory
            .getPartitionByName(StringIDSupport
                .getPartitionName(idAsString));
        return withPartition(partition).createCriteria().withUniqueKeyAsString(
            idAsString).buildCriteria().andFindUnique(this);
    }

    protected abstract void internalSavePartitions(
                                                   Partition... partitions)
        throws Exception;

    @Override
    public RepositoryPath getRepositoryPath() {
        return repositoryPath;
    }

    protected final RepositoryPath repositoryPath;

    @Override
    public void discardTransient() {
        this.newNodes.clear();
        this.removedNodes.clear();
        this.dirtyProperties.clear();
    }

    private final Map<Partition, PartitionMethods> partitionMethods = newHashMap();

    public class PartitionMethodsImpl implements PartitionMethods {

        @Override
        public NodeFactory.NodeBuilder createWithType(
                                                      final StorageSession session,
                                                      final String type) {
            return new NodeBuilderImpl(type, partition);
        }

        @Override
        public NodeFactory.NodeBuilder createWithType(
                                                      final String type) {
            return this.createWithType(AbstractStorageSession.this, type);
        }

        @Override
        public Iterable<Node> findByCriteria(
                                             final Criteria criteria) {
            try {
                if (!criteria.getPartition().equals(partition)) { throw new IllegalArgumentException(); }
                boolean hasGlobal = false;
                boolean hasOther = false;
                for (final CriteriaItem item: criteria.getCriteriaItems()) {
                    if (item instanceof PropertyCriteriaItem) {
                        hasOther = true;
                    } else if (item instanceof CompisiteKeyCriteriaItem) {
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
        public Iterable<Node> findByType(
                                         final String nodeType) {
            try {
                return internalFindByType(partition, nodeType);
            } catch (final Exception e) {
                handleException(e);
                return null;
            }
        }

        @Override
        public Node findUniqueByCriteria(
                                         final Criteria criteria) {
            try {
                final Iterable<Node> result = findByCriteria(criteria);
                if (result == null) { return null; }
                final Iterator<Node> it = result.iterator();
                if (!it.hasNext()) { return null; }
                return it.next();

            } catch (final Exception e) {
                handleException(e);
                return null;
            }
        }

        @Override
        public CriteriaBuilder createCriteria() {
            return new CriteriaBuilderImpl(partition);
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
                        partition, repositoryPath);
            }
            return parentKey;
        }

        @Override
        public Node createNewSimpleNode(
                                        final String... nodePaths) {
            Node parent = null;
            NodeKey parentKey = null;
            for (final String nodePath: nodePaths) {
                parentKey = new NodeKeyImpl(new CompositeKeyImpl(Collections
                    .<SimpleKey>emptySet(), nodePath),
                    parentKey != null ? parentKey.getKeyAsString() : null,
                    partition, repositoryPath);
                parent = new NodeImpl(parentKey, false);
                handleNewItem(parent);
            }

            return parent;
        }

        @Override
        public NodeKeyBuilder createKey(
                                          final String nodeType) {
            return new NodeKeyBuilderImpl(nodeType, partition,
                repositoryPath);
        }

        private final Partition partition;

        private PartitionMethodsImpl(final Partition currentPartition) {
            this.partition = currentPartition;
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

    protected abstract Iterable<String> internalGetAllNodeTypes(
                                                                Partition partition)
        throws Exception;

    @Override
    public void removeNode(
                           final Node stNodeEntry) {
        final List<Node> removedItems = new LinkedList<Node>();
        searchItemsToRemove(stNodeEntry, removedItems);
        Collections.reverse(removedItems);
        for (final Node r: removedItems) {
            handleRemovedItem(r);
        }
    }

    private void searchItemsToRemove(
                                     final Node stNodeEntry,
                                     final List<Node> removedItems) {
        removedItems.add(stNodeEntry);
        final Iterable<Partition> partitions =
            SLCollections.iterableOf(stNodeEntry
                .getKey().getPartition(),
                partitionFactory.getValues());
        for (final Partition p: partitions) {
            final Iterable<Node> children = stNodeEntry.getChildren(p, this);
            for (final Node e: children) {
                searchItemsToRemove(e, removedItems);
            }
        }
    }

    protected AbstractStorageSession(final FlushMode flushMode,
                                     final RepositoryPath repositoryPath, final PartitionFactory partitionFactory) {
        if (flushMode == null) { throw new NullPointerException(); }
        if (repositoryPath == null) { throw new NullPointerException(); }
        if (partitionFactory == null) { throw new NullPointerException(); }
        this.flushMode = flushMode;
        this.repositoryPath = repositoryPath;
        this.partitionFactory = partitionFactory;
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

    protected void handleException(
                                   final Exception e) {
        if (e instanceof RuntimeException) { throw (RuntimeException) e; }
        throw new RuntimeException(e);
    }

    private final PartitionFactory                        partitionFactory;

    private final FlushMode                               flushMode;

    protected final Set<Pair<Node, R>>                    newNodes        = newLinkedHashSet();
    protected final Set<Pair<Link, R>>                    newLinks        = newLinkedHashSet();

    protected final Multimap<PropertyContainer, Property> dirtyProperties = ArrayListMultimap
                                                                              .create();

    protected final Set<Node>                             removedNodes    = newLinkedHashSet();
    protected final Set<Link>                             removedLinks    = newLinkedHashSet();

    private void handleNewItem(
                               final Node entry) {
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

    protected abstract R createNodeReferenceIfNecessary(
                                                        Partition partition,
                                                        Node entry);

    protected abstract R createLinkReferenceIfNecessary(
                                                        Partition partition,
                                                        Link entry);

    private void handleRemovedItem(
                                   final Node entry) {
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

    protected abstract byte[] internalPropertyGetValue(
                                                       Partition partition,
                                                       Property stProperty)
        throws Exception;

    @Override
    public FlushMode getFlushMode() {
        return flushMode;
    }

    private final class NodeBuilderImpl implements
        NodeFactory.NodeBuilder {

        private NodeBuilderImpl(final String type, final Partition partition) {
            this.type = type;
            this.partition = partition;
        }

        private final Partition      partition;

        private final String         type;

        private String               parentKey = null;

        private final Set<SimpleKey> keys      = newHashSet();
        private final Set<String>    keyNames  = newHashSet();

        @Override
        public NodeFactory.NodeBuilder withSimpleKey(
                                                     final String name,
                                                     final String value) {
            if (keyNames.contains(name)) { throw new IllegalStateException("key name already inserted"); }
            this.keys.add(new SimpleKeyImpl(name, value));
            this.keyNames.add(name);
            return this;
        }

        @Override
        public NodeFactory.NodeBuilder withParentKey(
                                                     final NodeKey parentKey) {
            if (this.parentKey != null) { throw new IllegalStateException(); }
            this.parentKey = parentKey.getKeyAsString();
            return this;
        }

        @Override
        public NodeFactory.NodeBuilder withParent(
                                                  final Node parent) {
            return withParentKey(parent.getKey());
        }

        @Override
        public Node andCreate() {
            final CompositeKeyImpl localKey = new CompositeKeyImpl(keys, type);

            final NodeKeyImpl uniqueKey = new NodeKeyImpl(localKey,
                parentKey, partition, repositoryPath);
            final NodeImpl result = new NodeImpl(uniqueKey, false);
            if (getFlushMode().equals(FlushMode.AUTO)) {

                AbstractStorageSession.this.handleNewItem(result);
            } else {
                final R ref = AbstractStorageSession.this
                    .createNodeReferenceIfNecessary(partition, result);
                final Pair<Node, R> pair = newPair((Node) result, ref);
                AbstractStorageSession.this.newNodes.add(pair);
            }
            return result;
        }

        @Override
        public NodeBuilder withParentAsString(
                                              final String parentAsString) {
            this.parentKey = parentAsString;
            return this;
        }

    }

    @Override
    public void flushTransient() {
        final Set<Partition> partitions = newHashSet();
        final Map<PropertyContainer, R> referenceMap = newHashMap();
        for (final Pair<Node, R> newNode: newNodes) {
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
                if (propertyContainer instanceof Node) {
                    reference = createNodeReferenceIfNecessary(
                        propertyContainer.getPartition(),
                        (Node) propertyContainer);
                } else if (propertyContainer instanceof Link) {
                    reference = createLinkReferenceIfNecessary(
                        propertyContainer.getPartition(),
                        (Link) propertyContainer);
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
        for (final Node removedNode: removedNodes) {
            try {
                partitions.add(removedNode.getKey().getPartition());
                flushRemovedItem(removedNode.getKey().getPartition(),
                    removedNode);
            } catch (final Exception e) {
                handleException(e);
            }
        }
        for (final Pair<Link, R> p: this.newLinks) {
            try {
                handleNewLink(p.getK1().getPartition(), p.getK1().getOrigin(),
                    p.getK1());
            } catch (final Exception e) {
                handleException(e);
            }
        }
        for (final Link link: this.removedLinks) {
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

    public static class NodeKeyBuilderImpl implements NodeKeyBuilder {

        private final Set<SimpleKey>     localEntries = newHashSet();
        private final String             type;

        private final Partition          partition;

        private final NodeKeyBuilderImpl child;

        private final RepositoryPath     repositoryPath;

        private String                   parentKey;

        public NodeKeyBuilderImpl(final String type, final Partition partition,
                                    final RepositoryPath repositoryPath) {
            this.type = type;
            this.partition = partition;
            this.child = null;
            this.repositoryPath = repositoryPath;
        }

        private NodeKeyBuilderImpl(final String type,
                                     final NodeKeyBuilderImpl child, final Partition partition,
                                     final RepositoryPath repositoryPath) {
            this.type = type;
            this.child = child;
            this.partition = partition;
            this.repositoryPath = repositoryPath;
        }

        @Override
        public NodeKeyBuilder withSimpleKey(
                                            final String propertyName,
                                            final String value) {
            this.localEntries.add(new SimpleKeyImpl(propertyName, value));
            return this;
        }

        @Override
        public NodeKeyBuilder withParent(
                                           final Partition newPartition,
                                           final String nodeType) {
            return new NodeKeyBuilderImpl(nodeType, this,
                newPartition, repositoryPath);
        }

        @Override
        public NodeKeyBuilder withParent(
                                           final String parentId) {
            this.parentKey = parentId;
            return this;
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
                            : null, currentBuilder.partition,
                        repositoryPath);
                    currentBuilder = currentBuilder.child;
                } while (currentBuilder != null);
            } else {
                final CompositeKey localKey = new CompositeKeyImpl(
                    currentBuilder.localEntries, currentBuilder.type);
                currentKey = new NodeKeyImpl(localKey, parentKey,
                    partition, repositoryPath);

            }
            return currentKey;

        }

    }

    protected abstract Iterable<Node> internalFindByCriteria(
                                                             Partition partition,
                                                             Criteria criteria)
        throws Exception;

    protected abstract void flushNewItem(
                                         R reference,
                                         Partition partition,
                                         Node entry)
        throws Exception;

    protected abstract void flushRemovedItem(
                                             Partition partition,
                                             Node entry)
        throws Exception;

    protected abstract Iterable<Node> internalNodeEntryGetChildrenByType(
                                                                         Partition partition,
                                                                         Node stNodeEntry,
                                                                         String type)
        throws Exception;

    protected abstract void internalFlushSimpleProperty(
                                                        R reference,
                                                        Partition partition,
                                                        Property dirtyProperty)
        throws Exception;

    protected abstract Iterable<Node> internalNodeEntryGetChildren(
                                                                   Partition partition,
                                                                   Node stNodeEntry)
        throws Exception;

    protected abstract Node internalNodeEntryGetParent(
                                                       Partition partition,
                                                       Node stNodeEntry)
        throws Exception;

    protected abstract Set<Property> internalPropertyContainerLoadProperties(
                                                                             R reference,
                                                                             Partition partition,
                                                                             PropertyContainer stNodeEntry)
        throws Exception;

    protected abstract Iterable<Node> internalFindByType(
                                                         Partition partition,
                                                         String nodeType)
        throws Exception;

    protected abstract Iterable<Link> internalFindLinks(
                                                        Partition partition,
                                                        Node origin,
                                                        Node destiny,
                                                        String type)
        throws Exception;

    @Override
    public Link addLink(
                        final Node origin,
                        final Node target,
                        final String type) {
        final Link link = new LinkImpl(type, origin, target, true);
        if (getFlushMode().equals(FlushMode.AUTO)) {
            try {
                this.handleNewLink(link.getOrigin().getPartition(), link
                    .getOrigin(), link);
            } catch (final Exception e) {
                handleException(e);
            }
        } else {
            final R ref = createLinkReferenceIfNecessary(origin.getPartition(), link);
            final Pair<Link, R> pair = newPair(link, ref);
            AbstractStorageSession.this.newLinks.add(pair);
        }
        return link;
    }

    protected abstract void handleNewLink(
                                          Partition partition,
                                          Node origin,
                                          Link link)
        throws Exception;

    @Override
    public Iterable<Link> findLinks(
                                    final Node origin,
                                    final Node destiny) {
        try {
            return internalFindLinks(origin.getPartition(), origin, destiny,
                null);
        } catch (final Exception e) {
            handleException(e);
            return null;
        }
    }

    @Override
    public Iterable<Link> findLinks(
                                    final Node origin,
                                    final String type) {
        try {
            return internalFindLinks(origin.getPartition(), origin, null, type);
        } catch (final Exception e) {
            handleException(e);
            return null;
        }
    }

    @Override
    public Iterable<Link> findLinks(
                                    final Node origin) {
        try {
            return internalFindLinks(origin.getPartition(), origin, null, null);
        } catch (final Exception e) {
            handleException(e);
            return null;
        }
    }

    @Override
    public Link getLink(
                        final Node origin,
                        final Node destiny,
                        final String type) {
        try {
            return SLCollections.firstOf(internalFindLinks(origin
                .getPartition(), origin, destiny, type));
        } catch (final Exception e) {
            handleException(e);
            return null;
        }
    }

    @Override
    public void removeLink(
                           final Link link) {
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

    protected abstract void flushRemovedLink(
                                             Partition partition,
                                             Link link)
        throws Exception;

    @Override
    public void removeLink(
                           final Node origin,
                           final Node target,
                           final String type) {
        removeLink(new LinkImpl(type, origin, target, false));
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

    public Set<Property> propertyContainerLoadProperties(
                                                         final PropertyContainer stNodeEntry) {
        try {
            return internalPropertyContainerLoadProperties(null, stNodeEntry.getPartition(),
                stNodeEntry);
        } catch (final Exception e) {
            handleException(e);
        }
        return null;
    }

    public Iterable<Node> nodeEntryGetChildrenByType(
                                                     final Partition partition,
                                                     final Node stNodeEntry,
                                                     final String type) {
        if (!partition.equals(stNodeEntry.getKey().getPartition())) { throw new IllegalArgumentException(
            "wrong partition for this node entry"); }

        try {
            return internalNodeEntryGetChildrenByType(partition,
                stNodeEntry, type);

        } catch (final Exception e) {
            handleException(e);
        }
        return null;
    }

    public NodeFactory.NodeBuilder nodeEntryCreateWithType(
                                                           final Node stNodeEntry,
                                                           final String type) {
        return withPartition(stNodeEntry.getPartition()).createWithType(
            AbstractStorageSession.this, type)
            .withParent(stNodeEntry);
    }

    public Node nodeEntryGetParent(
                                   final Node stNodeEntry) {
        try {
            return internalNodeEntryGetParent(stNodeEntry.getPartition(), stNodeEntry);
        } catch (final Exception e) {
            handleException(e);
        }
        return null;
    }

    public Iterable<Node> nodeEntryGetChildren(
                                               final Partition partition,
                                               final Node stNodeEntry) {
        try {
            return internalNodeEntryGetChildren(partition, stNodeEntry);
        } catch (final Exception e) {
            handleException(e);
        }
        return null;
    }

}
