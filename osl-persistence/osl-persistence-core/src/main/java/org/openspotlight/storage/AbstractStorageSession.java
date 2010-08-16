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
import org.openspotlight.storage.domain.Link;
import org.openspotlight.storage.domain.Node;
import org.openspotlight.storage.domain.NodeFactory;
import org.openspotlight.storage.domain.NodeFactory.NodeBuilder;
import org.openspotlight.storage.domain.Property;
import org.openspotlight.storage.domain.PropertyContainer;
import org.openspotlight.storage.domain.key.Key;
import org.openspotlight.storage.domain.key.KeyImpl;
import org.openspotlight.storage.domain.key.LocalImpl;
import org.openspotlight.storage.domain.key.LocalKey;
import org.openspotlight.storage.domain.key.UniqueKey;
import org.openspotlight.storage.domain.key.UniqueKeyImpl;
import org.openspotlight.storage.domain.node.LinkImpl;
import org.openspotlight.storage.domain.node.NodeImpl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * Created by User: feu - Date: Mar 22, 2010 - Time: 2:19:49 PM
 */
public abstract class AbstractStorageSession<R> implements StorageSession {

    @Override
    public Node findNodeByStringId(final String idAsString) {

        final Partition partition = partitionFactory
                                              .getPartitionByName(StringIDSupport
                                                                                 .getPartitionName(idAsString));
        return withPartition(partition).createCriteria().withUniqueKeyAsString(
                                                                               idAsString).buildCriteria().andFindUnique(this);
    }

    protected abstract void internalSavePartitions(Partition... partitions)
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
        public NodeFactory.NodeBuilder createWithName(
                                                       final StorageSession session,
                                                       final String name) {
            return new NodeEntryBuilderImpl(name, partition);
        }

        @Override
        public NodeFactory.NodeBuilder createWithName(final String name) {
            return this.createWithName(AbstractStorageSession.this, name);
        }

        @Override
        public Iterable<Node> findByCriteria(final Criteria criteria) {
            try {
                if (!criteria.getPartition().equals(partition)) { throw new IllegalArgumentException(); }
                boolean hasGlobal = false;
                boolean hasOther = false;
                for (final CriteriaItem item: criteria.getCriteriaItems()) {
                    if (item instanceof PropertyCriteriaItem) {
                        hasOther = true;
                    } else if (item instanceof LocalKeyCriteriaItem) {
                        hasOther = true;
                    } else if (item instanceof PropertyContainsString) {
                        hasOther = true;
                    } else if (item instanceof PropertyStartsWithString) {
                        hasOther = true;
                    } else if (item instanceof PropertyEndsWithString) {
                        hasOther = true;
                    } else if (item instanceof UniqueKeyCriteriaItem) {
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
        public Iterable<Node> findNamed(final String nodeEntryName) {
            try {
                return internalFindNamed(partition, nodeEntryName);
            } catch (final Exception e) {
                handleException(e);
                return null;
            }
        }

        @Override
        public Node findUniqueByCriteria(final Criteria criteria) {
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
        public UniqueKey createNewSimpleKey(final String... nodePaths) {
            UniqueKey parentKey = null;
            for (final String path: nodePaths) {
                parentKey =
                    new UniqueKeyImpl(
                        new LocalImpl(Collections
                                                                              .<Key>emptySet(), path),
                        parentKey
                                                                                                                .getKeyAsString(),
                        partition, repositoryPath);
            }
            return parentKey;
        }

        @Override
        public Node createNewSimpleNode(final String... nodePaths) {
            Node parent = null;
            UniqueKey parentKey = null;
            for (final String nodePath: nodePaths) {
                parentKey = new UniqueKeyImpl(new LocalImpl(Collections
                                                                              .<Key>emptySet(), nodePath),
                                                parentKey != null ? parentKey.getKeyAsString() : null,
                                                partition, repositoryPath);
                parent = new NodeImpl(parentKey, false);
                handleNewItem(parent);
            }

            return parent;
        }

        @Override
        public UniqueKeyBuilder createKey(final String nodeEntryName) {
            return new STUniqueKeyBuilderImpl(nodeEntryName, partition,
                                              repositoryPath);
        }

        private final Partition partition;

        private PartitionMethodsImpl(final Partition currentPartition) {
            this.partition = currentPartition;
        }

        @Override
        public Iterable<String> getAllNodeNames() {
            try {
                return internalGetAllNodeNames(partition);
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
            }
        }

    }

    protected abstract Iterable<String> internalGetAllNodeNames(
                                                                 Partition partition)
        throws Exception;

    private static class UniqueKeyAsStringCriteriaItemImpl implements
            UniqueKeyAsStringCriteriaItem {

        public UniqueKeyAsStringCriteriaItemImpl(final String keyAsString) {
            this.keyAsString = keyAsString;
            this.nodeEntryName = StringIDSupport.getNodeEntryName(keyAsString);
        }

        private final String keyAsString;

        private final String nodeEntryName;

        @Override
        public String getKeyAsString() {
            return keyAsString;
        }

        @Override
        public String getNodeEntryName() {
            return nodeEntryName;
        }

    }

    private static class UniqueKeyCriteriaItemImpl implements
            UniqueKeyCriteriaItem {
        private UniqueKeyCriteriaItemImpl(final UniqueKey value,
                                             final String nodeEntryName) {
            this.value = value;
            this.nodeEntryName = nodeEntryName;
        }

        private final UniqueKey value;

        private final String    nodeEntryName;

        @Override
        public UniqueKey getValue() {
            return value;
        }

        @Override
        public String getNodeEntryName() {
            return nodeEntryName;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }

            final UniqueKeyCriteriaItemImpl that = (UniqueKeyCriteriaItemImpl) o;

            if (nodeEntryName != null ? !nodeEntryName
                                                      .equals(that.nodeEntryName) : that.nodeEntryName != null) { return false; }
            if (value != null ? !value.equals(that.value) : that.value != null) { return false; }

            return true;
        }

        @Override
        public int hashCode() {
            int result = value != null ? value.hashCode() : 0;
            result = 31 * result
                     + (nodeEntryName != null ? nodeEntryName.hashCode() : 0);
            return result;
        }

    }

    private static class LocalKeyCriteriaItemImpl implements
            LocalKeyCriteriaItem {
        private LocalKeyCriteriaItemImpl(final LocalKey value,
                                            final String nodeEntryName) {
            this.value = value;
            this.nodeEntryName = nodeEntryName;
        }

        private final LocalKey value;

        private final String   nodeEntryName;

        @Override
        public LocalKey getValue() {
            return value;
        }

        @Override
        public String getNodeEntryName() {
            return nodeEntryName;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }

            final LocalKeyCriteriaItemImpl that = (LocalKeyCriteriaItemImpl) o;

            if (nodeEntryName != null ? !nodeEntryName
                                                      .equals(that.nodeEntryName) : that.nodeEntryName != null) { return false; }
            if (value != null ? !value.equals(that.value) : that.value != null) { return false; }

            return true;
        }

        @Override
        public int hashCode() {
            int result = value != null ? value.hashCode() : 0;
            result = 31 * result
                     + (nodeEntryName != null ? nodeEntryName.hashCode() : 0);
            return result;
        }

    }

    private static class PropertyCriteriaItemImpl implements
            PropertyCriteriaItem {
        private PropertyCriteriaItemImpl(final String propertyName, final String value,
                                            final String nodeEntryName) {
            this.value = value;
            this.propertyName = propertyName;
            this.nodeEntryName = nodeEntryName;
        }

        private final String value;

        private final String propertyName;

        private final String nodeEntryName;

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String getPropertyName() {
            return propertyName;
        }

        @Override
        public String getNodeEntryName() {
            return nodeEntryName;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }

            final PropertyCriteriaItemImpl that = (PropertyCriteriaItemImpl) o;

            if (nodeEntryName != null ? !nodeEntryName
                                                      .equals(that.nodeEntryName) : that.nodeEntryName != null) { return false; }
            if (propertyName != null ? !propertyName.equals(that.propertyName)
                    : that.propertyName != null) { return false; }
            if (value != null ? !value.equals(that.value) : that.value != null) { return false; }

            return true;
        }

        @Override
        public int hashCode() {
            int result = value != null ? value.hashCode() : 0;
            result = 31 * result
                     + (propertyName != null ? propertyName.hashCode() : 0);
            result = 31 * result
                     + (nodeEntryName != null ? nodeEntryName.hashCode() : 0);
            return result;
        }

    }

    private static class PropertyEndsWithStringImpl implements
            PropertyEndsWithString {

        private PropertyEndsWithStringImpl(final String nodeEntryName,
                                              final String propertyName, final String value) {
            this.nodeEntryName = nodeEntryName;
            this.propertyName = propertyName;
            this.value = value;
        }

        private final String nodeEntryName;

        private final String propertyName;

        private final String value;

        @Override
        public String getNodeEntryName() {
            return nodeEntryName;
        }

        @Override
        public String getPropertyName() {
            return propertyName;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }

            final PropertyEndsWithStringImpl that = (PropertyEndsWithStringImpl) o;

            if (nodeEntryName != null ? !nodeEntryName
                                                      .equals(that.nodeEntryName) : that.nodeEntryName != null) { return false; }
            if (propertyName != null ? !propertyName.equals(that.propertyName)
                    : that.propertyName != null) { return false; }
            if (value != null ? !value.equals(that.value) : that.value != null) { return false; }

            return true;
        }

        @Override
        public int hashCode() {
            int result = nodeEntryName != null ? nodeEntryName.hashCode() : 0;
            result = 31 * result
                     + (propertyName != null ? propertyName.hashCode() : 0);
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }
    }

    private static class PropertyStartsWithStringImpl implements
            PropertyStartsWithString {

        private PropertyStartsWithStringImpl(final String nodeEntryName,
                                                final String propertyName, final String value) {
            this.nodeEntryName = nodeEntryName;
            this.propertyName = propertyName;
            this.value = value;
        }

        private final String nodeEntryName;

        private final String propertyName;

        private final String value;

        @Override
        public String getNodeEntryName() {
            return nodeEntryName;
        }

        @Override
        public String getPropertyName() {
            return propertyName;
        }

        @Override
        public String getValue() {
            return value;

        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }

            final PropertyStartsWithStringImpl that = (PropertyStartsWithStringImpl) o;

            if (nodeEntryName != null ? !nodeEntryName
                                                      .equals(that.nodeEntryName) : that.nodeEntryName != null) { return false; }
            if (propertyName != null ? !propertyName.equals(that.propertyName)
                    : that.propertyName != null) { return false; }
            if (value != null ? !value.equals(that.value) : that.value != null) { return false; }

            return true;
        }

        @Override
        public int hashCode() {
            int result = nodeEntryName != null ? nodeEntryName.hashCode() : 0;
            result = 31 * result
                     + (propertyName != null ? propertyName.hashCode() : 0);
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }
    }

    private static class PropertyContainsStringImpl implements
            PropertyContainsString {

        private PropertyContainsStringImpl(final String nodeEntryName,
                                              final String propertyName, final String value) {
            this.nodeEntryName = nodeEntryName;
            this.propertyName = propertyName;
            this.value = value;
        }

        private final String nodeEntryName;

        private final String propertyName;

        private final String value;

        @Override
        public String getNodeEntryName() {
            return nodeEntryName;
        }

        @Override
        public String getPropertyName() {
            return propertyName;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }

            final PropertyContainsStringImpl that = (PropertyContainsStringImpl) o;

            if (nodeEntryName != null ? !nodeEntryName
                                                      .equals(that.nodeEntryName) : that.nodeEntryName != null) { return false; }
            if (propertyName != null ? !propertyName.equals(that.propertyName)
                    : that.propertyName != null) { return false; }
            if (value != null ? !value.equals(that.value) : that.value != null) { return false; }

            return true;
        }

        @Override
        public int hashCode() {
            int result = nodeEntryName != null ? nodeEntryName.hashCode() : 0;
            result = 31 * result
                     + (propertyName != null ? propertyName.hashCode() : 0);
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }
    }

    private static class CriteriaImpl implements Criteria {
        private CriteriaImpl(final String nodeName,
                                final Set<CriteriaItem> criteriaItems, final Partition partition) {
            this.nodeName = nodeName;
            this.partition = partition;
            this.criteriaItems = ImmutableSet.copyOf(criteriaItems);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }

            final CriteriaImpl that = (CriteriaImpl) o;

            if (criteriaItems != null ? !criteriaItems
                                                      .equals(that.criteriaItems) : that.criteriaItems != null) { return false; }
            if (nodeName != null ? !nodeName.equals(that.nodeName)
                    : that.nodeName != null) { return false; }
            if (partition != null ? !partition.equals(that.partition)
                    : that.partition != null) { return false; }

            return true;
        }

        @Override
        public int hashCode() {
            int result = nodeName != null ? nodeName.hashCode() : 0;
            result = 31 * result
                     + (partition != null ? partition.hashCode() : 0);
            result = 31 * result
                     + (criteriaItems != null ? criteriaItems.hashCode() : 0);
            return result;
        }

        private final String            nodeName;

        private final Partition         partition;

        private final Set<CriteriaItem> criteriaItems;

        @Override
        public String getNodeName() {
            return nodeName;
        }

        @Override
        public Partition getPartition() {
            return partition;
        }

        @Override
        public Set<CriteriaItem> getCriteriaItems() {
            return criteriaItems;
        }

        @Override
        public Iterable<Node> andFind(final StorageSession session) {
            return session.withPartition(partition).findByCriteria(this);
        }

        @Override
        public Node andFindUnique(final StorageSession session) {
            return session.withPartition(partition).findUniqueByCriteria(this);
        }
    }

    private static class CriteriaBuilderImpl implements CriteriaBuilder {

        private final Partition partition;

        private String          transientNodeEntryName;

        private String          transientPropertyName;

        private UniqueKey       transientUniqueKey;

        private LocalKey        transientLocalKey;

        private String          transientPropertyValue;

        private String          transientIdAsString;

        private String          startsWith;
        private String          endsWith;
        private String          contains;

        Set<CriteriaItem>       items;

        public CriteriaBuilderImpl(final Partition partition) {
            this.partition = partition;
            items = newLinkedHashSet();
        }

        private void breakIfNotNull(final Object o) {
            if (o != null) { throw new IllegalStateException(); }
        }

        private void breakIfNull(final Object o) {
            if (o == null) { throw new IllegalStateException(); }
        }

        @Override
        public CriteriaBuilder withProperty(final String propertyName) {
            breakIfNotNull(transientUniqueKey);
            breakIfNotNull(transientPropertyValue);
            breakIfNotNull(transientLocalKey);

            breakIfNotNull(transientPropertyName);

            this.transientPropertyName = propertyName;
            return this;
        }

        @Override
        public CriteriaBuilder withNodeEntry(final String nodeName) {
            breakIfNotNull(transientNodeEntryName);
            this.transientNodeEntryName = nodeName;
            return this;
        }

        @Override
        public CriteriaBuilder equalsTo(final String value) {
            breakIfNotNull(transientUniqueKey);
            breakIfNotNull(transientLocalKey);

            breakIfNull(transientPropertyName);
            breakIfNotNull(transientPropertyValue);
            transientPropertyValue = value;
            and();
            return this;
        }

        @Override
        public CriteriaBuilder containsString(final String value) {
            breakIfNotNull(transientUniqueKey);
            breakIfNotNull(transientLocalKey);

            breakIfNull(transientPropertyName);
            contains = value;
            and();
            return this;

        }

        @Override
        public CriteriaBuilder startsWithString(final String value) {

            breakIfNotNull(transientUniqueKey);
            breakIfNotNull(transientLocalKey);

            breakIfNull(transientPropertyName);
            startsWith = value;
            and();
            return this;

        }

        @Override
        public CriteriaBuilder endsWithString(final String value) {
            breakIfNotNull(transientUniqueKey);
            breakIfNotNull(transientLocalKey);

            breakIfNull(transientPropertyName);
            endsWith = value;
            and();
            return this;
        }

        @Override
        public CriteriaBuilder and() {
            CriteriaItem item = null;
            if (transientUniqueKey != null) {
                breakIfNull(transientNodeEntryName);

                item = new UniqueKeyCriteriaItemImpl(transientUniqueKey,
                                                       transientNodeEntryName);

            } else if (transientLocalKey != null) {
                breakIfNull(transientNodeEntryName);

                item = new LocalKeyCriteriaItemImpl(transientLocalKey,
                                                      transientNodeEntryName);

            } else if (transientIdAsString != null) {

                item = new UniqueKeyAsStringCriteriaItemImpl(
                                                               transientIdAsString);

            } else if (transientPropertyName != null) {

                if (startsWith != null) {
                    item = new PropertyStartsWithStringImpl(
                                                              transientNodeEntryName, transientPropertyName,
                                                              startsWith);
                } else if (endsWith != null) {
                    item = new PropertyEndsWithStringImpl(
                                                            transientNodeEntryName, transientPropertyName,
                                                            endsWith);
                } else if (contains != null) {
                    item = new PropertyContainsStringImpl(
                                                            transientNodeEntryName, transientPropertyName,
                                                            contains);
                } else {
                    item = new PropertyCriteriaItemImpl(
                                                          transientPropertyName, transientPropertyValue,
                                                          transientNodeEntryName);
                }
            }
            transientPropertyName = null;
            transientUniqueKey = null;
            transientLocalKey = null;
            transientPropertyValue = null;
            transientIdAsString = null;
            this.items.add(item);

            return this;
        }

        @Override
        public Criteria buildCriteria() {
            and();
            final CriteriaImpl result = new CriteriaImpl(transientNodeEntryName,
                                                       this.items, partition);

            return result;
        }

        @Override
        public CriteriaBuilder withLocalKey(final LocalKey localKey) {
            breakIfNotNull(transientUniqueKey);
            breakIfNotNull(transientPropertyName);
            breakIfNotNull(transientPropertyValue);

            breakIfNotNull(transientLocalKey);

            transientLocalKey = localKey;
            transientNodeEntryName = localKey.getNodeEntryName();
            and();
            return this;
        }

        @Override
        public CriteriaBuilder withUniqueKey(final UniqueKey uniqueKey) {

            breakIfNotNull(transientLocalKey);
            breakIfNotNull(transientPropertyName);
            breakIfNotNull(transientPropertyValue);

            breakIfNotNull(transientUniqueKey);

            transientUniqueKey = uniqueKey;
            transientNodeEntryName = uniqueKey.getLocalKey().getNodeEntryName();
            and();
            return this;
        }

        @Override
        public CriteriaBuilder withUniqueKeyAsString(final String uniqueKeyAsString) {
            this.transientIdAsString = uniqueKeyAsString;
            and();
            return this;
        }
    }

    @Override
    public void removeNode(final Node stNodeEntry) {
        final List<Node> removedItems = new LinkedList<Node>();
        searchItemsToRemove(stNodeEntry, removedItems);
        Collections.reverse(removedItems);
        for (final Node r: removedItems) {
            handleRemovedItem(r);
        }
    }

    private void searchItemsToRemove(final Node stNodeEntry,
                                      final List<Node> removedItems) {
        removedItems.add(stNodeEntry);
        final Iterable<Partition> partitions =
            SLCollections.iterableOf(stNodeEntry
                                                                             .getUniqueKey().getPartition(),
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
        this.flushMode = flushMode;
        this.repositoryPath = repositoryPath;
        this.partitionFactory = partitionFactory;
    }

    @Override
    public PartitionMethods withPartition(final Partition partition) {
        PartitionMethods result = partitionMethods.get(partition);
        if (result == null) {
            result = new PartitionMethodsImpl(partition);
            partitionMethods.put(partition, result);
        }
        return result;
    }

    protected void handleException(final Exception e) {
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

    private void handleNewItem(final Node entry) {
        try {
            R reference;
            switch (getFlushMode()) {
                case AUTO:
                    reference = createNodeReferenceIfNecessary(entry.getUniqueKey()
                                                                    .getPartition(), entry);
                    flushNewItem(reference, entry.getUniqueKey().getPartition(),
                                 entry);
                case EXPLICIT:
                    reference = createNodeReferenceIfNecessary(entry.getUniqueKey()
                                                                    .getPartition(), entry);
                    newNodes.add(newPair(entry, reference));
                default:
                    throw new IllegalStateException();
            }
        } catch (final Exception e) {
            handleException(e);
        }
    }

    protected abstract R createNodeReferenceIfNecessary(Partition partition,
                                                         Node entry);

    protected abstract R createLinkReferenceIfNecessary(Partition partition,
                                                         Link entry);

    private void handleRemovedItem(final Node entry) {
        try {
            switch (getFlushMode()) {
                case AUTO:
                    flushRemovedItem(entry.getUniqueKey().getPartition(), entry);
                    break;
                case EXPLICIT:
                    removedNodes.add(entry);
                    break;
            }
        } catch (final Exception e) {
            handleException(e);
        }
    }

    protected abstract byte[] internalPropertyGetValue(Partition partition,
                                                        Property stProperty)
        throws Exception;

    @Override
    public FlushMode getFlushMode() {
        return flushMode;
    }

    private final class NodeEntryBuilderImpl implements
            NodeFactory.NodeBuilder {

        private NodeEntryBuilderImpl(final String name, final Partition partition) {
            this.name = name;
            this.partition = partition;
        }

        private final Partition   partition;

        private final String      name;

        private String            parentKey = null;

        private final Set<Key>    keys      = newHashSet();
        private final Set<String> keyNames  = newHashSet();

        @Override
        public NodeFactory.NodeBuilder withKeyEntry(final String name,
                                                     final String value) {
            if (keyNames.contains(name)) { throw new IllegalStateException("key name already inserted"); }
            this.keys.add(new KeyImpl(name, value));
            this.keyNames.add(name);
            return this;
        }

        @Override
        public NodeFactory.NodeBuilder withParentKey(
                                                      final UniqueKey parentKey) {
            if (this.parentKey != null) { throw new IllegalStateException(); }
            this.parentKey = parentKey.getKeyAsString();
            return this;
        }

        @Override
        public NodeFactory.NodeBuilder withParent(
                                                   final Node parent) {
            return withParentKey(parent.getUniqueKey());
        }

        @Override
        public Node andCreate() {
            final LocalImpl localKey = new LocalImpl(keys, name);

            final UniqueKeyImpl uniqueKey = new UniqueKeyImpl(localKey,
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
        public NodeBuilder withParentAsString(final String parentAsString) {
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
                partitions.add(newNode.getK1().getUniqueKey().getPartition());
                flushNewItem(newNode.getK2(), newNode.getK1().getUniqueKey()
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
                partitions.add(removedNode.getUniqueKey().getPartition());
                flushRemovedItem(removedNode.getUniqueKey().getPartition(),
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

    private void flushDirtyProperty(final R reference,
                                     final Property dirtyProperty) {
        try {

            internalFlushSimpleProperty(reference, dirtyProperty.getParent()
                                                                .getPartition(), dirtyProperty);

        } catch (final Exception e) {
            handleException(e);
        }
    }

    public static class STUniqueKeyBuilderImpl implements UniqueKeyBuilder {

        private final Set<Key>               localEntries = newHashSet();
        private final String                 name;

        private final Partition              partition;

        private final STUniqueKeyBuilderImpl child;

        private final RepositoryPath         repositoryPath;

        private String                       parentKey;

        public STUniqueKeyBuilderImpl(final String name, final Partition partition,
                                       final RepositoryPath repositoryPath) {
            this.name = name;
            this.partition = partition;
            this.child = null;
            this.repositoryPath = repositoryPath;
        }

        private STUniqueKeyBuilderImpl(final String name,
                                        final STUniqueKeyBuilderImpl child, final Partition partition,
                                        final RepositoryPath repositoryPath) {
            this.name = name;
            this.child = child;
            this.partition = partition;
            this.repositoryPath = repositoryPath;
        }

        @Override
        public UniqueKeyBuilder withEntry(final String propertyName,
                                           final String value) {
            this.localEntries.add(new KeyImpl(propertyName, value));
            return this;
        }

        @Override
        public UniqueKeyBuilder withParent(final Partition newPartition,
                                            final String nodeEntryName) {
            return new STUniqueKeyBuilderImpl(nodeEntryName, this,
                                              newPartition, repositoryPath);
        }

        @Override
        public UniqueKeyBuilder withParent(final String parentId) {
            this.parentKey = parentId;
            return this;
        }

        @Override
        public UniqueKey andCreate() {

            UniqueKey currentKey = null;
            STUniqueKeyBuilderImpl currentBuilder = this;
            if (parentKey == null) {
                do {
                    final LocalKey localKey = new LocalImpl(
                                                           currentBuilder.localEntries, currentBuilder.name);
                    currentKey = new UniqueKeyImpl(localKey,
                                                     currentKey != null ? currentKey.getKeyAsString()
                                                         : null, currentBuilder.partition,
                                                     repositoryPath);
                    currentBuilder = currentBuilder.child;
                } while (currentBuilder != null);
            } else {
                final LocalKey localKey = new LocalImpl(
                                                       currentBuilder.localEntries, currentBuilder.name);
                currentKey = new UniqueKeyImpl(localKey, parentKey,
                                                 partition, repositoryPath);

            }
            return currentKey;

        }

    }

    protected abstract Iterable<Node> internalFindByCriteria(
                                                                     Partition partition,
                                                                     Criteria criteria)
        throws Exception;

    protected abstract void flushNewItem(R reference,
                                          Partition partition,
                                          Node entry)
        throws Exception;

    protected abstract void flushRemovedItem(Partition partition,
                                              Node entry)
        throws Exception;

    protected abstract Iterable<Node> internalNodeEntryGetNamedChildren(
                                                                                Partition partition,
                                                                                Node stNodeEntry,
                                                                                String name)
            throws Exception;

    protected abstract void internalFlushSimpleProperty(R reference,
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

    protected abstract Iterable<Node> internalFindNamed(
                                                                Partition partition,
                                                                String nodeEntryName)
        throws Exception;

    protected abstract Iterable<Link> internalFindLinks(
                                                                Partition partition,
                                                                Node origin,
                                                                Node destiny,
                                                                String name)
        throws Exception;

    @Override
    public Link addLink(final Node origin,
                                final Node target,
                                final String name) {
        final Link link = new LinkImpl(name, origin, target, true);
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

    protected abstract void handleNewLink(Partition partition,
                                           Node origin,
                                           Link link)
        throws Exception;

    @Override
    public Iterable<Link> findLinks(final Node origin,
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
    public Iterable<Link> findLinks(final Node origin,
                                            final String name) {
        try {
            return internalFindLinks(origin.getPartition(), origin, null, name);
        } catch (final Exception e) {
            handleException(e);
            return null;
        }
    }

    @Override
    public Iterable<Link> findLinks(final Node origin) {
        try {
            return internalFindLinks(origin.getPartition(), origin, null, null);
        } catch (final Exception e) {
            handleException(e);
            return null;
        }
    }

    @Override
    public Link getLink(final Node origin,
                                final Node destiny,
                                final String name) {
        try {
            return SLCollections.firstOf(internalFindLinks(origin
                                                                 .getPartition(), origin, destiny, name));
        } catch (final Exception e) {
            handleException(e);
            return null;
        }
    }

    @Override
    public void removeLink(final Link link) {
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

    protected abstract void flushRemovedLink(Partition partition,
                                              Link link)
        throws Exception;

    @Override
    public void removeLink(final Node origin,
                            final Node target,
                            final String name) {
        removeLink(new LinkImpl(name, origin, target, false));
    }

    public void propertySetProperty(final Property stProperty,
                                     final byte[] value) {
        if (flushMode.equals(FlushMode.AUTO)) {
            flushDirtyProperty(null, stProperty);
        } else {
            dirtyProperties.put(stProperty.getParent(), stProperty);
        }

    }

    public byte[] propertyGetValue(final Property stProperty) {
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

    public Iterable<Node> nodeEntryGetNamedChildren(
                                                            final Partition partition,
                                                            final Node stNodeEntry,
                                                            final String name) {
        if (!partition.equals(stNodeEntry.getUniqueKey().getPartition())) { throw new IllegalArgumentException(
                                               "wrong partition for this node entry"); }

        try {
            return internalNodeEntryGetNamedChildren(partition,
                                                     stNodeEntry, name);

        } catch (final Exception e) {
            handleException(e);
        }
        return null;
    }

    public NodeFactory.NodeBuilder nodeEntryCreateWithName(
                                                            final Node stNodeEntry,
                                                            final String name) {
        return withPartition(stNodeEntry.getPartition()).createWithName(
                                                                        AbstractStorageSession.this, name)
                                                        .withParent(stNodeEntry);
    }

    public Node nodeEntryGetParent(final Node stNodeEntry) {
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
