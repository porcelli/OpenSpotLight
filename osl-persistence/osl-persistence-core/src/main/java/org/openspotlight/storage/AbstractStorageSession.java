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
 * ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 * *
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
import org.openspotlight.storage.domain.NodeFactory;
import org.openspotlight.storage.domain.NodeFactory.NodeBuilder;
import org.openspotlight.storage.domain.Property;
import org.openspotlight.storage.domain.PropertyContainer;
import org.openspotlight.storage.domain.STLinkEntry;
import org.openspotlight.storage.domain.STNodeEntry;
import org.openspotlight.storage.domain.key.Key;
import org.openspotlight.storage.domain.key.LocalKey;
import org.openspotlight.storage.domain.key.KeyImpl;
import org.openspotlight.storage.domain.key.LocalImpl;
import org.openspotlight.storage.domain.key.UniqueKeyImpl;
import org.openspotlight.storage.domain.key.UniqueKey;
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
    public STNodeEntry findNodeByStringId( String idAsString ) {

        Partition partition = partitionFactory
                                              .getPartitionByName(StringIDSupport
                                                                                 .getPartitionName(idAsString));
        return withPartition(partition).createCriteria().withUniqueKeyAsString(
                                                                               idAsString).buildCriteria().andFindUnique(this);
    }

    protected abstract void internalSavePartitions( Partition... partitions )
            throws Exception;

    public RepositoryPath getRepositoryPath() {
        return repositoryPath;
    }

    protected final RepositoryPath repositoryPath;

    public void discardTransient() {
        this.newNodes.clear();
        this.removedNodes.clear();
        this.dirtyProperties.clear();
    }

    private final Map<Partition, PartitionMethods> partitionMethods = newHashMap();

    public class PartitionMethodsImpl implements PartitionMethods {

        public NodeFactory.NodeBuilder createWithName(
                                                       StorageSession session,
                                                       String name ) {
            return new NodeEntryBuilderImpl(name, partition);
        }

        public NodeFactory.NodeBuilder createWithName( String name ) {
            return this.createWithName(AbstractStorageSession.this, name);
        }

        public Iterable<STNodeEntry> findByCriteria( Criteria criteria ) {
            try {
                if (!criteria.getPartition().equals(partition))
                    throw new IllegalArgumentException();
                boolean hasGlobal = false;
                boolean hasOther = false;
                for (CriteriaItem item : criteria.getCriteriaItems()) {
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
                    if (hasOther && hasGlobal)
                        throw new IllegalArgumentException();
                }
                return internalFindByCriteria(criteria.getPartition(), criteria);
            } catch (Exception e) {
                handleException(e);
                return null;
            }
        }

        public Iterable<STNodeEntry> findNamed( String nodeEntryName ) {
            try {
                return internalFindNamed(partition, nodeEntryName);
            } catch (Exception e) {
                handleException(e);
                return null;
            }
        }

        public STNodeEntry findUniqueByCriteria( Criteria criteria ) {
            try {
                Iterable<STNodeEntry> result = findByCriteria(criteria);
                if (result == null)
                    return null;
                Iterator<STNodeEntry> it = result.iterator();
                if (!it.hasNext())
                    return null;
                return it.next();

            } catch (Exception e) {
                handleException(e);
                return null;
            }
        }

        public CriteriaBuilder createCriteria() {
            return new CriteriaBuilderImpl(partition);
        }

        public UniqueKey createNewSimpleKey( String... nodePaths ) {
            UniqueKey parentKey = null;
            for (String path : nodePaths) {
                parentKey = new UniqueKeyImpl(new LocalImpl(Collections
                                                                              .<Key>emptySet(), path), parentKey
                                                                                                                .getKeyAsString(), partition, repositoryPath);
            }
            return parentKey;
        }

        public STNodeEntry createNewSimpleNode( String... nodePaths ) {
            STNodeEntry parent = null;
            UniqueKey parentKey = null;
            for (String nodePath : nodePaths) {
                parentKey = new UniqueKeyImpl(new LocalImpl(Collections
                                                                              .<Key>emptySet(), nodePath),
                                                parentKey != null ? parentKey.getKeyAsString() : null,
                                                partition, repositoryPath);
                parent = new NodeImpl(parentKey, false);
                handleNewItem(parent);
            }

            return parent;
        }

        public UniqueKeyBuilder createKey( String nodeEntryName ) {
            return new STUniqueKeyBuilderImpl(nodeEntryName, partition,
                                              repositoryPath);
        }

        private final Partition partition;

        private PartitionMethodsImpl( Partition currentPartition ) {
            this.partition = currentPartition;
        }

        @Override
        public Iterable<String> getAllNodeNames() {
            try {
                return internalGetAllNodeNames(partition);
            } catch (Exception e) {
                throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
            }
        }

    }

    protected abstract Iterable<String> internalGetAllNodeNames(
                                                                 Partition partition ) throws Exception;

    private static class UniqueKeyAsStringCriteriaItemImpl implements
            UniqueKeyAsStringCriteriaItem {

        public UniqueKeyAsStringCriteriaItemImpl( String keyAsString ) {
            this.keyAsString = keyAsString;
            this.nodeEntryName = StringIDSupport.getNodeEntryName(keyAsString);
        }

        private final String keyAsString;

        private final String nodeEntryName;

        public String getKeyAsString() {
            return keyAsString;
        }

        public String getNodeEntryName() {
            return nodeEntryName;
        }

    }

    private static class UniqueKeyCriteriaItemImpl implements
            UniqueKeyCriteriaItem {
        private UniqueKeyCriteriaItemImpl( UniqueKey value,
                                             String nodeEntryName ) {
            this.value = value;
            this.nodeEntryName = nodeEntryName;
        }

        private final UniqueKey value;

        private final String    nodeEntryName;

        public UniqueKey getValue() {
            return value;
        }

        public String getNodeEntryName() {
            return nodeEntryName;
        }

        @Override
        public boolean equals( Object o ) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            UniqueKeyCriteriaItemImpl that = (UniqueKeyCriteriaItemImpl)o;

            if (nodeEntryName != null ? !nodeEntryName
                                                      .equals(that.nodeEntryName) : that.nodeEntryName != null)
                return false;
            if (value != null ? !value.equals(that.value) : that.value != null)
                return false;

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
        private LocalKeyCriteriaItemImpl( LocalKey value,
                                            String nodeEntryName ) {
            this.value = value;
            this.nodeEntryName = nodeEntryName;
        }

        private final LocalKey value;

        private final String   nodeEntryName;

        public LocalKey getValue() {
            return value;
        }

        public String getNodeEntryName() {
            return nodeEntryName;
        }

        @Override
        public boolean equals( Object o ) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            LocalKeyCriteriaItemImpl that = (LocalKeyCriteriaItemImpl)o;

            if (nodeEntryName != null ? !nodeEntryName
                                                      .equals(that.nodeEntryName) : that.nodeEntryName != null)
                return false;
            if (value != null ? !value.equals(that.value) : that.value != null)
                return false;

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
        private PropertyCriteriaItemImpl( String propertyName, String value,
                                            String nodeEntryName ) {
            this.value = value;
            this.propertyName = propertyName;
            this.nodeEntryName = nodeEntryName;
        }

        private final String value;

        private final String propertyName;

        private final String nodeEntryName;

        public String getValue() {
            return value;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public String getNodeEntryName() {
            return nodeEntryName;
        }

        @Override
        public boolean equals( Object o ) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            PropertyCriteriaItemImpl that = (PropertyCriteriaItemImpl)o;

            if (nodeEntryName != null ? !nodeEntryName
                                                      .equals(that.nodeEntryName) : that.nodeEntryName != null)
                return false;
            if (propertyName != null ? !propertyName.equals(that.propertyName)
                    : that.propertyName != null)
                return false;
            if (value != null ? !value.equals(that.value) : that.value != null)
                return false;

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

        private PropertyEndsWithStringImpl( String nodeEntryName,
                                              String propertyName, String value ) {
            this.nodeEntryName = nodeEntryName;
            this.propertyName = propertyName;
            this.value = value;
        }

        private final String nodeEntryName;

        private final String propertyName;

        private final String value;

        public String getNodeEntryName() {
            return nodeEntryName;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public String getValue() {
            return value;
        }

        @Override
        public boolean equals( Object o ) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            PropertyEndsWithStringImpl that = (PropertyEndsWithStringImpl)o;

            if (nodeEntryName != null ? !nodeEntryName
                                                      .equals(that.nodeEntryName) : that.nodeEntryName != null)
                return false;
            if (propertyName != null ? !propertyName.equals(that.propertyName)
                    : that.propertyName != null)
                return false;
            if (value != null ? !value.equals(that.value) : that.value != null)
                return false;

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

        private PropertyStartsWithStringImpl( String nodeEntryName,
                                                String propertyName, String value ) {
            this.nodeEntryName = nodeEntryName;
            this.propertyName = propertyName;
            this.value = value;
        }

        private final String nodeEntryName;

        private final String propertyName;

        private final String value;

        public String getNodeEntryName() {
            return nodeEntryName;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public String getValue() {
            return value;

        }

        @Override
        public boolean equals( Object o ) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            PropertyStartsWithStringImpl that = (PropertyStartsWithStringImpl)o;

            if (nodeEntryName != null ? !nodeEntryName
                                                      .equals(that.nodeEntryName) : that.nodeEntryName != null)
                return false;
            if (propertyName != null ? !propertyName.equals(that.propertyName)
                    : that.propertyName != null)
                return false;
            if (value != null ? !value.equals(that.value) : that.value != null)
                return false;

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

        private PropertyContainsStringImpl( String nodeEntryName,
                                              String propertyName, String value ) {
            this.nodeEntryName = nodeEntryName;
            this.propertyName = propertyName;
            this.value = value;
        }

        private final String nodeEntryName;

        private final String propertyName;

        private final String value;

        public String getNodeEntryName() {
            return nodeEntryName;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public String getValue() {
            return value;
        }

        @Override
        public boolean equals( Object o ) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            PropertyContainsStringImpl that = (PropertyContainsStringImpl)o;

            if (nodeEntryName != null ? !nodeEntryName
                                                      .equals(that.nodeEntryName) : that.nodeEntryName != null)
                return false;
            if (propertyName != null ? !propertyName.equals(that.propertyName)
                    : that.propertyName != null)
                return false;
            if (value != null ? !value.equals(that.value) : that.value != null)
                return false;

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
        private CriteriaImpl( String nodeName,
                                Set<CriteriaItem> criteriaItems, Partition partition ) {
            this.nodeName = nodeName;
            this.partition = partition;
            this.criteriaItems = ImmutableSet.copyOf(criteriaItems);
        }

        @Override
        public boolean equals( Object o ) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            CriteriaImpl that = (CriteriaImpl)o;

            if (criteriaItems != null ? !criteriaItems
                                                      .equals(that.criteriaItems) : that.criteriaItems != null)
                return false;
            if (nodeName != null ? !nodeName.equals(that.nodeName)
                    : that.nodeName != null)
                return false;
            if (partition != null ? !partition.equals(that.partition)
                    : that.partition != null)
                return false;

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

        public String getNodeName() {
            return nodeName;
        }

        public Partition getPartition() {
            return partition;
        }

        public Set<CriteriaItem> getCriteriaItems() {
            return criteriaItems;
        }

        public Iterable<STNodeEntry> andFind( StorageSession session ) {
            return session.withPartition(partition).findByCriteria(this);
        }

        public STNodeEntry andFindUnique( StorageSession session ) {
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

        public CriteriaBuilderImpl( Partition partition ) {
            this.partition = partition;
            items = newLinkedHashSet();
        }

        private void breakIfNotNull( Object o ) {
            if (o != null)
                throw new IllegalStateException();
        }

        private void breakIfNull( Object o ) {
            if (o == null)
                throw new IllegalStateException();
        }

        public CriteriaBuilder withProperty( String propertyName ) {
            breakIfNotNull(transientUniqueKey);
            breakIfNotNull(transientPropertyValue);
            breakIfNotNull(transientLocalKey);

            breakIfNotNull(transientPropertyName);

            this.transientPropertyName = propertyName;
            return this;
        }

        public CriteriaBuilder withNodeEntry( String nodeName ) {
            breakIfNotNull(transientNodeEntryName);
            this.transientNodeEntryName = nodeName;
            return this;
        }

        public CriteriaBuilder equalsTo( String value ) {
            breakIfNotNull(transientUniqueKey);
            breakIfNotNull(transientLocalKey);

            breakIfNull(transientPropertyName);
            breakIfNotNull(transientPropertyValue);
            transientPropertyValue = value;
            and();
            return this;
        }

        public CriteriaBuilder containsString( String value ) {
            breakIfNotNull(transientUniqueKey);
            breakIfNotNull(transientLocalKey);

            breakIfNull(transientPropertyName);
            contains = value;
            and();
            return this;

        }

        public CriteriaBuilder startsWithString( String value ) {

            breakIfNotNull(transientUniqueKey);
            breakIfNotNull(transientLocalKey);

            breakIfNull(transientPropertyName);
            startsWith = value;
            and();
            return this;

        }

        public CriteriaBuilder endsWithString( String value ) {
            breakIfNotNull(transientUniqueKey);
            breakIfNotNull(transientLocalKey);

            breakIfNull(transientPropertyName);
            endsWith = value;
            and();
            return this;
        }

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

        public Criteria buildCriteria() {
            and();
            CriteriaImpl result = new CriteriaImpl(transientNodeEntryName,
                                                       this.items, partition);

            return result;
        }

        public CriteriaBuilder withLocalKey( LocalKey localKey ) {
            breakIfNotNull(transientUniqueKey);
            breakIfNotNull(transientPropertyName);
            breakIfNotNull(transientPropertyValue);

            breakIfNotNull(transientLocalKey);

            transientLocalKey = localKey;
            transientNodeEntryName = localKey.getNodeEntryName();
            and();
            return this;
        }

        public CriteriaBuilder withUniqueKey( UniqueKey uniqueKey ) {

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
        public CriteriaBuilder withUniqueKeyAsString( String uniqueKeyAsString ) {
            this.transientIdAsString = uniqueKeyAsString;
            and();
            return this;
        }
    }

    public void removeNode( STNodeEntry stNodeEntry ) {
        List<STNodeEntry> removedItems = new LinkedList<STNodeEntry>();
        searchItemsToRemove(stNodeEntry, removedItems);
        Collections.reverse(removedItems);
        for (STNodeEntry r : removedItems) {
            handleRemovedItem(r);
        }
    }

    private void searchItemsToRemove( STNodeEntry stNodeEntry,
                                      List<STNodeEntry> removedItems ) {
        removedItems.add(stNodeEntry);
        Iterable<Partition> partitions = SLCollections.iterableOf(stNodeEntry
                                                                             .getUniqueKey().getPartition(), partitionFactory.getValues());
        for (Partition p : partitions) {
            Iterable<STNodeEntry> children = stNodeEntry.getChildren(p, this);
            for (STNodeEntry e : children) {
                searchItemsToRemove(e, removedItems);
            }
        }
    }

    protected AbstractStorageSession( FlushMode flushMode,
                                        RepositoryPath repositoryPath, PartitionFactory partitionFactory ) {
        this.flushMode = flushMode;
        this.repositoryPath = repositoryPath;
        this.partitionFactory = partitionFactory;
    }

    public PartitionMethods withPartition( Partition partition ) {
        PartitionMethods result = partitionMethods.get(partition);
        if (result == null) {
            result = new PartitionMethodsImpl(partition);
            partitionMethods.put(partition, result);
        }
        return result;
    }

    protected void handleException( Exception e ) {
        if (e instanceof RuntimeException)
            throw (RuntimeException)e;
        throw new RuntimeException(e);
    }

    private final PartitionFactory                        partitionFactory;

    private final FlushMode                               flushMode;

    protected final Set<Pair<STNodeEntry, R>>             newNodes        = newLinkedHashSet();
    protected final Set<Pair<STLinkEntry, R>>             newLinks        = newLinkedHashSet();

    protected final Multimap<PropertyContainer, Property> dirtyProperties = ArrayListMultimap
                                                                                             .create();

    protected final Set<STNodeEntry>                      removedNodes    = newLinkedHashSet();
    protected final Set<STLinkEntry>                      removedLinks    = newLinkedHashSet();

    private void handleNewItem( STNodeEntry entry ) {
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
        } catch (Exception e) {
            handleException(e);
        }
    }

    protected abstract R createNodeReferenceIfNecessary( Partition partition,
                                                         STNodeEntry entry );

    protected abstract R createLinkReferenceIfNecessary( Partition partition,
                                                         STLinkEntry entry );

    private void handleRemovedItem( STNodeEntry entry ) {
        try {
            switch (getFlushMode()) {
                case AUTO:
                    flushRemovedItem(entry.getUniqueKey().getPartition(), entry);
                    break;
                case EXPLICIT:
                    removedNodes.add(entry);
                    break;
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    protected abstract byte[] internalPropertyGetValue( Partition partition,
                                                        Property stProperty ) throws Exception;

    public FlushMode getFlushMode() {
        return flushMode;
    }

    private final class NodeEntryBuilderImpl implements
            NodeFactory.NodeBuilder {

        private NodeEntryBuilderImpl( String name, Partition partition ) {
            this.name = name;
            this.partition = partition;
        }

        private final Partition partition;

        private final String    name;

        private String          parentKey = null;

        private Set<Key>        keys      = newHashSet();
        private Set<String>     keyNames  = newHashSet();

        public NodeFactory.NodeBuilder withKeyEntry( String name,
                                                     String value ) {
            if (keyNames.contains(name))
                throw new IllegalStateException("key name already inserted");
            this.keys.add(new KeyImpl(name, value));
            this.keyNames.add(name);
            return this;
        }

        public NodeFactory.NodeBuilder withParentKey(
                                                      UniqueKey parentKey ) {
            if (this.parentKey != null) {
                throw new IllegalStateException();
            }
            this.parentKey = parentKey.getKeyAsString();
            return this;
        }

        public NodeFactory.NodeBuilder withParent(
                                                   STNodeEntry parent ) {
            return withParentKey(parent.getUniqueKey());
        }

        public STNodeEntry andCreate() {
            LocalImpl localKey = new LocalImpl(keys, name);

            UniqueKeyImpl uniqueKey = new UniqueKeyImpl(localKey,
                                                            parentKey, partition, repositoryPath);
            NodeImpl result = new NodeImpl(uniqueKey, false);
            if (getFlushMode().equals(FlushMode.AUTO)) {

                AbstractStorageSession.this.handleNewItem(result);
            } else {
                R ref = AbstractStorageSession.this
                                                     .createNodeReferenceIfNecessary(partition, result);
                Pair<STNodeEntry, R> pair = newPair((STNodeEntry)result, ref);
                AbstractStorageSession.this.newNodes.add(pair);
            }
            return result;
        }

        @Override
        public NodeBuilder withParentAsString( String parentAsString ) {
            this.parentKey = parentAsString;
            return this;
        }

    }

    public void flushTransient() {
        Set<Partition> partitions = newHashSet();
        Map<PropertyContainer, R> referenceMap = newHashMap();
        for (Pair<STNodeEntry, R> newNode : newNodes) {
            try {
                partitions.add(newNode.getK1().getUniqueKey().getPartition());
                flushNewItem(newNode.getK2(), newNode.getK1().getUniqueKey()
                                                     .getPartition(), newNode.getK1());
                referenceMap.put(newNode.getK1(), newNode.getK2());
            } catch (Exception e) {
                handleException(e);
            }
        }
        for (PropertyContainer propertyContainer : dirtyProperties.keySet()) {
            partitions.add(propertyContainer.getPartition());

            R reference = referenceMap.get(propertyContainer);
            if (reference == null) {
                if (propertyContainer instanceof STNodeEntry) {
                    reference = createNodeReferenceIfNecessary(
                                                               propertyContainer.getPartition(),
                                                               (STNodeEntry)propertyContainer);
                } else if (propertyContainer instanceof STLinkEntry) {
                    reference = createLinkReferenceIfNecessary(
                                                               propertyContainer.getPartition(),
                                                               (STLinkEntry)propertyContainer);
                } else {
                    throw new IllegalStateException();
                }
            }
            for (Property data : dirtyProperties.get(propertyContainer)) {
                try {
                    flushDirtyProperty(reference, data);
                } catch (Exception e) {
                    handleException(e);
                }
            }

        }
        for (STNodeEntry removedNode : removedNodes) {
            try {
                partitions.add(removedNode.getUniqueKey().getPartition());
                flushRemovedItem(removedNode.getUniqueKey().getPartition(),
                                 removedNode);
            } catch (Exception e) {
                handleException(e);
            }
        }
        for (Pair<STLinkEntry, R> p : this.newLinks) {
            try {
                handleNewLink(p.getK1().getPartition(), p.getK1().getOrigin(),
                              p.getK1());
            } catch (Exception e) {
                handleException(e);
            }
        }
        for (STLinkEntry link : this.removedLinks) {
            try {
                flushRemovedLink(link.getPartition(), link);
            } catch (Exception e) {
                handleException(e);
            }
        }
        try {

            internalSavePartitions(partitions
                                             .toArray(new Partition[partitions.size()]));
        } catch (Exception e) {
            handleException(e);
        }
        discardTransient();

    }

    private void flushDirtyProperty( R reference,
                                     Property dirtyProperty ) {
        try {

            internalFlushSimpleProperty(reference, dirtyProperty.getParent()
                                                                .getPartition(), dirtyProperty);

        } catch (Exception e) {
            handleException(e);
        }
    }

    public static class STUniqueKeyBuilderImpl implements UniqueKeyBuilder {

        private Set<Key>                     localEntries = newHashSet();
        private final String                 name;

        private final Partition              partition;

        private final STUniqueKeyBuilderImpl child;

        private final RepositoryPath         repositoryPath;

        private String                       parentKey;

        public STUniqueKeyBuilderImpl( String name, Partition partition,
                                       RepositoryPath repositoryPath ) {
            this.name = name;
            this.partition = partition;
            this.child = null;
            this.repositoryPath = repositoryPath;
        }

        private STUniqueKeyBuilderImpl( String name,
                                        STUniqueKeyBuilderImpl child, Partition partition,
                                        RepositoryPath repositoryPath ) {
            this.name = name;
            this.child = child;
            this.partition = partition;
            this.repositoryPath = repositoryPath;
        }

        public UniqueKeyBuilder withEntry( String propertyName,
                                           String value ) {
            this.localEntries.add(new KeyImpl(propertyName, value));
            return this;
        }

        public UniqueKeyBuilder withParent( Partition newPartition,
                                            String nodeEntryName ) {
            return new STUniqueKeyBuilderImpl(nodeEntryName, this,
                                              newPartition, repositoryPath);
        }

        @Override
        public UniqueKeyBuilder withParent( String parentId ) {
            this.parentKey = parentId;
            return this;
        }

        public UniqueKey andCreate() {

            UniqueKey currentKey = null;
            STUniqueKeyBuilderImpl currentBuilder = this;
            if (parentKey == null) {
                do {
                    LocalKey localKey = new LocalImpl(
                                                           currentBuilder.localEntries, currentBuilder.name);
                    currentKey = new UniqueKeyImpl(localKey,
                                                     currentKey != null ? currentKey.getKeyAsString()
                                                         : null, currentBuilder.partition,
                                                     repositoryPath);
                    currentBuilder = currentBuilder.child;
                } while (currentBuilder != null);
            } else {
                LocalKey localKey = new LocalImpl(
                                                       currentBuilder.localEntries, currentBuilder.name);
                currentKey = new UniqueKeyImpl(localKey, parentKey,
                                                 partition, repositoryPath);

            }
            return currentKey;

        }

    }

    protected abstract Iterable<STNodeEntry> internalFindByCriteria(
                                                                     Partition partition,
                                                                     Criteria criteria ) throws Exception;

    protected abstract void flushNewItem( R reference,
                                          Partition partition,
                                          STNodeEntry entry ) throws Exception;

    protected abstract void flushRemovedItem( Partition partition,
                                              STNodeEntry entry ) throws Exception;

    protected abstract Iterable<STNodeEntry> internalNodeEntryGetNamedChildren(
                                                                                Partition partition,
                                                                                STNodeEntry stNodeEntry,
                                                                                String name )
            throws Exception;

    protected abstract void internalFlushSimpleProperty( R reference,
                                                         Partition partition,
                                                         Property dirtyProperty ) throws Exception;

    protected abstract Iterable<STNodeEntry> internalNodeEntryGetChildren(
                                                                           Partition partition,
                                                                           STNodeEntry stNodeEntry ) throws Exception;

    protected abstract STNodeEntry internalNodeEntryGetParent(
                                                               Partition partition,
                                                               STNodeEntry stNodeEntry ) throws Exception;

    protected abstract Set<Property> internalPropertyContainerLoadProperties(
                                                                              R reference,
                                                                              Partition partition,
                                                                              PropertyContainer stNodeEntry )
            throws Exception;

    protected abstract Iterable<STNodeEntry> internalFindNamed(
                                                                Partition partition,
                                                                String nodeEntryName ) throws Exception;

    protected abstract Iterable<STLinkEntry> internalFindLinks(
                                                                Partition partition,
                                                                STNodeEntry origin,
                                                                STNodeEntry destiny,
                                                                String name ) throws Exception;

    @Override
    public STLinkEntry addLink( STNodeEntry origin,
                                STNodeEntry target,
                                String name ) {
        STLinkEntry link = new LinkImpl(name, origin, target, true);
        if (getFlushMode().equals(FlushMode.AUTO)) {
            try {
                this.handleNewLink(link.getOrigin().getPartition(), link
                                                                        .getOrigin(), link);
            } catch (Exception e) {
                handleException(e);
            }
        } else {
            R ref = createLinkReferenceIfNecessary(origin.getPartition(), link);
            Pair<STLinkEntry, R> pair = newPair(link, ref);
            AbstractStorageSession.this.newLinks.add(pair);
        }
        return link;
    }

    protected abstract void handleNewLink( Partition partition,
                                           STNodeEntry origin,
                                           STLinkEntry link ) throws Exception;

    @Override
    public Iterable<STLinkEntry> findLinks( STNodeEntry origin,
                                            STNodeEntry destiny ) {
        try {
            return internalFindLinks(origin.getPartition(), origin, destiny,
                                     null);
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    @Override
    public Iterable<STLinkEntry> findLinks( STNodeEntry origin,
                                            String name ) {
        try {
            return internalFindLinks(origin.getPartition(), origin, null, name);
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    @Override
    public Iterable<STLinkEntry> findLinks( STNodeEntry origin ) {
        try {
            return internalFindLinks(origin.getPartition(), origin, null, null);
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    @Override
    public STLinkEntry getLink( STNodeEntry origin,
                                STNodeEntry destiny,
                                String name ) {
        try {
            return SLCollections.firstOf(internalFindLinks(origin
                                                                 .getPartition(), origin, destiny, name));
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    @Override
    public void removeLink( STLinkEntry link ) {
        try {
            switch (getFlushMode()) {
                case AUTO:
                    flushRemovedLink(link.getPartition(), link);
                    break;
                case EXPLICIT:
                    removedLinks.add(link);
                    break;
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    protected abstract void flushRemovedLink( Partition partition,
                                              STLinkEntry link ) throws Exception;

    @Override
    public void removeLink( STNodeEntry origin,
                            STNodeEntry target,
                            String name ) {
        removeLink(new LinkImpl(name, origin, target, false));
    }

    public void propertySetProperty( Property stProperty,
                                     byte[] value ) {
        if (flushMode.equals(FlushMode.AUTO)) {
            flushDirtyProperty(null, stProperty);
        } else {
            dirtyProperties.put(stProperty.getParent(), stProperty);
        }

    }

    public byte[] propertyGetValue( Property stProperty ) {
        try {
            return internalPropertyGetValue(stProperty.getParent()
                                                      .getPartition(), stProperty);
        } catch (Exception e) {
            handleException(e);
        }
        return null;
    }

    public Set<Property> propertyContainerLoadProperties(
                                                          PropertyContainer stNodeEntry ) {
        try {
            return internalPropertyContainerLoadProperties(null, stNodeEntry.getPartition(),
                                                           stNodeEntry);
        } catch (Exception e) {
            handleException(e);
        }
        return null;
    }

    public Iterable<STNodeEntry> nodeEntryGetNamedChildren(
                                                            Partition partition,
                                                            STNodeEntry stNodeEntry,
                                                            String name ) {
        if (!partition.equals(stNodeEntry.getUniqueKey().getPartition()))
            throw new IllegalArgumentException(
                                               "wrong partition for this node entry");

        try {
            return internalNodeEntryGetNamedChildren(partition,
                                                     stNodeEntry, name);

        } catch (Exception e) {
            handleException(e);
        }
        return null;
    }

    public NodeFactory.NodeBuilder nodeEntryCreateWithName(
                                                            STNodeEntry stNodeEntry,
                                                            String name ) {
        return withPartition(stNodeEntry.getPartition()).createWithName(
                                                                        AbstractStorageSession.this, name)
                                                        .withParent(stNodeEntry);
    }

    public STNodeEntry nodeEntryGetParent( STNodeEntry stNodeEntry ) {
        try {
            return internalNodeEntryGetParent(stNodeEntry.getPartition(), stNodeEntry);
        } catch (Exception e) {
            handleException(e);
        }
        return null;
    }

    public Iterable<STNodeEntry> nodeEntryGetChildren(
                                                      Partition partition,
                                                      STNodeEntry stNodeEntry ) {
       try {
           return internalNodeEntryGetChildren(partition, stNodeEntry);
       } catch (Exception e) {
           handleException(e);
       }
       return null;
   }

}
