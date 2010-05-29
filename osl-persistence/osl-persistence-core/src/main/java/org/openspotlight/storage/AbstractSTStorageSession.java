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

import com.google.common.collect.ImmutableSet;
import org.openspotlight.storage.domain.STAData;
import org.openspotlight.storage.domain.key.*;
import org.openspotlight.storage.domain.node.STNodeEntry;
import org.openspotlight.storage.domain.node.STNodeEntryFactory;
import org.openspotlight.storage.domain.node.STNodeEntryImpl;
import org.openspotlight.storage.domain.node.STProperty;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static org.openspotlight.common.util.Sha1.getSha1Signature;
import static org.openspotlight.common.util.Sha1.getSha1SignatureEncodedAsBase64;

/**
 * Created by User: feu - Date: Mar 22, 2010 - Time: 2:19:49 PM
 */
public abstract class AbstractSTStorageSession implements STStorageSession {
    public STRepositoryPath getRepositoryPath() {
        return repositoryPath;
    }

    protected final STRepositoryPath repositoryPath;


    public void discardTransient() {
        this.newNodes.clear();
        this.removedNodes.clear();
        this.dirtyProperties.clear();
    }

    private final Map<STPartition, STPartitionMethods> partitionMethods = newHashMap();


    private class STPartitionMethodsImpl implements STPartitionMethods {

        public STNodeEntryFactory.STNodeEntryBuilder createWithName(STStorageSession session, String name) {
            return new STNodeEntryBuilderImpl(name, partition);
        }


        public STNodeEntryFactory.STNodeEntryBuilder createWithName(String name) {
            return this.createWithName(AbstractSTStorageSession.this, name);
        }


        public Set<STNodeEntry> findByCriteria(STCriteria criteria) {
            try {
                if (!criteria.getPartition().equals(partition)) throw new IllegalArgumentException();
                boolean hasGlobal = false;
                boolean hasOther = false;
                for (STCriteriaItem item : criteria.getCriteriaItems()) {
                    if (item instanceof STPropertyCriteriaItem) {
                        hasOther = true;
                    } else if (item instanceof STLocalKeyCriteriaItem) {
                        hasOther = true;
                    } else if (item instanceof STPropertyContainsString) {
                        hasOther = true;
                    } else if (item instanceof STPropertyStartsWithString) {
                        hasOther = true;
                    } else if (item instanceof STPropertyEndsWithString) {
                        hasOther = true;
                    } else if (item instanceof STUniqueKeyCriteriaItem) {
                        hasGlobal = true;
                    }
                    if (hasOther && hasGlobal) throw new IllegalArgumentException();
                }
                return internalFindByCriteria(criteria.getPartition(), criteria);
            } catch (Exception e) {
                handleException(e);
                return null;
            }
        }

        public Set<STNodeEntry> findNamed(String nodeEntryName) {
            try {
                return internalFindNamed(partition, nodeEntryName);
            } catch (Exception e) {
                handleException(e);
                return null;
            }
        }

        public STNodeEntry findUniqueByCriteria(STCriteria criteria) {
            try {
                Set<STNodeEntry> result = findByCriteria(criteria);
                if (result == null) return null;
                if (result.size() == 0) return null;
                if (result.size() == 1) return result.iterator().next();
                throw new IllegalStateException();
            } catch (Exception e) {
                handleException(e);
                return null;
            }
        }

        private final STStorageSessionInternalMethods internalMethods;


        public STCriteriaBuilder createCriteria() {
            return new STCriteriaBuilderImpl(partition);
        }


        public STStorageSessionInternalMethods getInternalMethods() {
            return internalMethods;
        }

        public STUniqueKey createNewSimpleKey(String... nodePaths) {
            STUniqueKey parentKey = null;
            for (String path : nodePaths) {
                parentKey = new STUniqueKeyImpl(new STLocalKeyImpl(Collections.<STKeyEntry>emptySet(), path),
                        parentKey, partition, repositoryPath);
            }
            return parentKey;
        }

        public STNodeEntry createNewSimpleNode(String... nodePaths) {
            STNodeEntry parent = null;
            STUniqueKey parentKey = null;
            for (String nodePath : nodePaths) {
                parentKey = new STUniqueKeyImpl(new STLocalKeyImpl(Collections.<STKeyEntry>emptySet(), nodePath),
                        parentKey, partition, repositoryPath);
                parent = new STNodeEntryImpl(parentKey, false);
                handleNewItem(parent);
            }

            return parent;
        }

        public STUniqueKeyBuilder createKey(String nodeEntryName) {
            return new STUniqueKeyBuilderImpl(nodeEntryName, partition);
        }

        private final STPartition partition;

        private STPartitionMethodsImpl(STPartition currentPartition) {
            this.partition = currentPartition;
            internalMethods = new STStorageSessionInternalMethodsImpl(partition);
        }
    }


    protected final STStorageSessionSupportMethods supportMethods = new STStorageSessionSupportMethodsImpl();

    public STStorageSessionSupportMethods getSupportMethods() {
        return supportMethods;
    }

    protected final STNodeEntry createFoundEntryWithKey(STUniqueKey uniqueKey) {
        return new STNodeEntryImpl(uniqueKey, true);
    }

    public final class STStorageSessionSupportMethodsImpl implements STStorageSessionSupportMethods {

        private final WeakHashMap<STAData, Object> cache = new WeakHashMap<STAData, Object>();

        private <T> T getFromCache(STAData key) {
            return (T) cache.get(key);
        }


        private <T> T fillCache(STAData key, T toCache) {
            cache.put(key, toCache);
            return toCache;
        }

        public String getLocalKeyAsStringHash(STLocalKey localKey) {
            String fromCache = getFromCache(localKey);
            if (fromCache != null) return fromCache;

            return fillCache(localKey, getSha1SignatureEncodedAsBase64(getLocalKeyAsSimpleString(localKey)));
        }

        public String getUniqueKeyAsStringHash(STUniqueKey uniqueKey) {
            String fromCache = getFromCache(uniqueKey);
            if (fromCache != null) return fromCache;

            String result = fillCache(uniqueKey, getSha1SignatureEncodedAsBase64(getUniqueKeyAsSimpleString(uniqueKey)));
            return result;
        }


        public byte[] getLocalKeyAsByteHash(STLocalKey localKey) {
            byte[] fromCache = getFromCache(localKey);
            if (fromCache != null) return fromCache;


            return fillCache(localKey, getSha1Signature(getLocalKeyAsSimpleString(localKey)));
        }


        public byte[] getUniqueKeyAsByteHash(STUniqueKey uniqueKey) {

            byte[] fromCache = getFromCache(uniqueKey);
            if (fromCache != null) return fromCache;

            return fillCache(uniqueKey, getSha1Signature(getUniqueKeyAsSimpleString(uniqueKey)));

        }


        public String getLocalKeyAsSimpleString(STLocalKey localKey) {
            StringBuilder sb = new StringBuilder();
            sb.append(localKey.getNodeEntryName());
            for (STKeyEntry entry : localKey.getEntries()) {
                sb.append(":").append(entry.getPropertyName()).append(":")
                        .append(":").append(entry.getValue());
            }
            return sb.toString();
        }

        public String getUniqueKeyAsSimpleString(STUniqueKey uniqueKey) {
            StringBuilder sb = new StringBuilder();
            STUniqueKey currentKey = uniqueKey;

            while (currentKey != null) {
                sb.append(getLocalKeyAsSimpleString(currentKey.getLocalKey())).append(":");
                currentKey = currentKey.getParentKey();
            }
            sb.append(uniqueKey.getRepositoryPath().getRepositoryPathAsString());
            return sb.toString();
        }

    }

    private static class STUniqueKeyCriteriaItemImpl implements STUniqueKeyCriteriaItem {
        private STUniqueKeyCriteriaItemImpl(STUniqueKey value, String nodeEntryName) {
            this.value = value;
            this.nodeEntryName = nodeEntryName;
        }

        private final STUniqueKey value;


        private final String nodeEntryName;

        public STUniqueKey getValue() {
            return value;
        }

        public String getNodeEntryName() {
            return nodeEntryName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            STUniqueKeyCriteriaItemImpl that = (STUniqueKeyCriteriaItemImpl) o;

            if (nodeEntryName != null ? !nodeEntryName.equals(that.nodeEntryName) : that.nodeEntryName != null)
                return false;
            if (value != null ? !value.equals(that.value) : that.value != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = value != null ? value.hashCode() : 0;
            result = 31 * result + (nodeEntryName != null ? nodeEntryName.hashCode() : 0);
            return result;
        }


    }

    private static class STLocalKeyCriteriaItemImpl implements STLocalKeyCriteriaItem {
        private STLocalKeyCriteriaItemImpl(STLocalKey value, String nodeEntryName) {
            this.value = value;
            this.nodeEntryName = nodeEntryName;
        }

        private final STLocalKey value;


        private final String nodeEntryName;

        public STLocalKey getValue() {
            return value;
        }


        public String getNodeEntryName() {
            return nodeEntryName;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            STLocalKeyCriteriaItemImpl that = (STLocalKeyCriteriaItemImpl) o;

            if (nodeEntryName != null ? !nodeEntryName.equals(that.nodeEntryName) : that.nodeEntryName != null)
                return false;
            if (value != null ? !value.equals(that.value) : that.value != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = value != null ? value.hashCode() : 0;
            result = 31 * result + (nodeEntryName != null ? nodeEntryName.hashCode() : 0);
            return result;
        }

    }

    private static class STPropertyCriteriaItemImpl<T extends Serializable> implements STPropertyCriteriaItem<T> {
        private STPropertyCriteriaItemImpl(T value, Class<T> type, String propertyName, String nodeEntryName) {
            this.value = value;
            this.type = type;
            this.propertyName = propertyName;
            this.nodeEntryName = nodeEntryName;
        }

        private final T value;

        private final Class<T> type;

        private final String propertyName;

        private final String nodeEntryName;

        public T getValue() {
            return value;
        }

        public Class<T> getType() {
            return type;
        }

        public String getPropertyName() {
            return propertyName;
        }


        public String getNodeEntryName() {
            return nodeEntryName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            STPropertyCriteriaItemImpl that = (STPropertyCriteriaItemImpl) o;

            if (nodeEntryName != null ? !nodeEntryName.equals(that.nodeEntryName) : that.nodeEntryName != null)
                return false;
            if (propertyName != null ? !propertyName.equals(that.propertyName) : that.propertyName != null)
                return false;
            if (type != null ? !type.equals(that.type) : that.type != null) return false;
            if (value != null ? !value.equals(that.value) : that.value != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = value != null ? value.hashCode() : 0;
            result = 31 * result + (type != null ? type.hashCode() : 0);
            result = 31 * result + (propertyName != null ? propertyName.hashCode() : 0);
            result = 31 * result + (nodeEntryName != null ? nodeEntryName.hashCode() : 0);
            return result;
        }


    }


    private static class STPropertyEndsWithStringImpl implements STPropertyEndsWithString {

        private STPropertyEndsWithStringImpl(String nodeEntryName, String propertyName, String value) {
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
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            STPropertyEndsWithStringImpl that = (STPropertyEndsWithStringImpl) o;

            if (nodeEntryName != null ? !nodeEntryName.equals(that.nodeEntryName) : that.nodeEntryName != null)
                return false;
            if (propertyName != null ? !propertyName.equals(that.propertyName) : that.propertyName != null)
                return false;
            if (value != null ? !value.equals(that.value) : that.value != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = nodeEntryName != null ? nodeEntryName.hashCode() : 0;
            result = 31 * result + (propertyName != null ? propertyName.hashCode() : 0);
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }
    }

    private static class STPropertyStartsWithStringImpl implements STPropertyStartsWithString {

        private STPropertyStartsWithStringImpl(String nodeEntryName, String propertyName, String value) {
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
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            STPropertyStartsWithStringImpl that = (STPropertyStartsWithStringImpl) o;

            if (nodeEntryName != null ? !nodeEntryName.equals(that.nodeEntryName) : that.nodeEntryName != null)
                return false;
            if (propertyName != null ? !propertyName.equals(that.propertyName) : that.propertyName != null)
                return false;
            if (value != null ? !value.equals(that.value) : that.value != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = nodeEntryName != null ? nodeEntryName.hashCode() : 0;
            result = 31 * result + (propertyName != null ? propertyName.hashCode() : 0);
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }
    }


    private static class STPropertyContainsStringImpl implements STPropertyContainsString {

        private STPropertyContainsStringImpl(String nodeEntryName, String propertyName, String value) {
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
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            STPropertyContainsStringImpl that = (STPropertyContainsStringImpl) o;

            if (nodeEntryName != null ? !nodeEntryName.equals(that.nodeEntryName) : that.nodeEntryName != null)
                return false;
            if (propertyName != null ? !propertyName.equals(that.propertyName) : that.propertyName != null)
                return false;
            if (value != null ? !value.equals(that.value) : that.value != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = nodeEntryName != null ? nodeEntryName.hashCode() : 0;
            result = 31 * result + (propertyName != null ? propertyName.hashCode() : 0);
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }
    }

    private static class STCriteriaImpl implements STCriteria {
        private STCriteriaImpl(String nodeName, Set<STCriteriaItem> criteriaItems, STPartition partition) {
            this.nodeName = nodeName;
            this.partition = partition;
            this.criteriaItems = ImmutableSet.copyOf(criteriaItems);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            STCriteriaImpl that = (STCriteriaImpl) o;

            if (criteriaItems != null ? !criteriaItems.equals(that.criteriaItems) : that.criteriaItems != null)
                return false;
            if (nodeName != null ? !nodeName.equals(that.nodeName) : that.nodeName != null) return false;
            if (partition != null ? !partition.equals(that.partition) : that.partition != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = nodeName != null ? nodeName.hashCode() : 0;
            result = 31 * result + (partition != null ? partition.hashCode() : 0);
            result = 31 * result + (criteriaItems != null ? criteriaItems.hashCode() : 0);
            return result;
        }

        private final String nodeName;

        private final STPartition partition;

        private final Set<STCriteriaItem> criteriaItems;

        public String getNodeName() {
            return nodeName;
        }

        public STPartition getPartition() {
            return partition;
        }

        public Set<STCriteriaItem> getCriteriaItems() {
            return criteriaItems;
        }

        public Set<STNodeEntry> andFind(STStorageSession session) {
            return session.withPartition(partition).findByCriteria(this);
        }

        public STNodeEntry andFindUnique(STStorageSession session) {
            return session.withPartition(partition).findUniqueByCriteria(this);
        }
    }

    private static class STCriteriaBuilderImpl implements STCriteriaBuilder {

        private final STPartition partition;

        private String transientNodeEntryName;

        private String transientPropertyName;

        private STUniqueKey transientUniqueKey;

        private STLocalKey transientLocalKey;

        private Class<? extends Serializable> transientPropertyType;

        private Serializable transientPropertyValue;

        private String startsWith;
        private String endsWith;
        private String contains;

        Set<STCriteriaItem> items;

        public STCriteriaBuilderImpl(STPartition partition) {
            this.partition = partition;
            items = newLinkedHashSet();
        }

        private void breakIfNotNull(Object o) {
            if (o != null) throw new IllegalStateException();
        }

        private void breakIfNull(Object o) {
            if (o == null) throw new IllegalStateException();
        }


        public STCriteriaBuilder withProperty(String propertyName) {
            breakIfNotNull(transientUniqueKey);
            breakIfNotNull(transientPropertyType);
            breakIfNotNull(transientPropertyValue);
            breakIfNotNull(transientLocalKey);

            breakIfNotNull(transientPropertyName);

            this.transientPropertyName = propertyName;
            return this;
        }

        public STCriteriaBuilder withNodeEntry(String nodeName) {
            breakIfNotNull(transientNodeEntryName);
            this.transientNodeEntryName = nodeName;
            return this;
        }

        public <T extends Serializable> STCriteriaBuilder equals(Class<T> type, Serializable value) {
            breakIfNotNull(transientUniqueKey);
            breakIfNotNull(transientLocalKey);

            breakIfNull(transientPropertyName);
            breakIfNotNull(transientPropertyType);
            breakIfNotNull(transientPropertyValue);
            transientPropertyType = type;
            transientPropertyValue = value;
            and();
            return this;
        }

        public STCriteriaBuilder containsString(String value) {
            breakIfNotNull(transientUniqueKey);
            breakIfNotNull(transientLocalKey);

            breakIfNull(transientPropertyName);
            breakIfNotNull(transientPropertyType);
            transientPropertyType = String.class;
            contains = value;
            and();
            return this;

        }

        public STCriteriaBuilder startsWithString(String value) {

            breakIfNotNull(transientUniqueKey);
            breakIfNotNull(transientLocalKey);

            breakIfNull(transientPropertyName);
            breakIfNotNull(transientPropertyType);
            transientPropertyType = String.class;
            startsWith = value;
            and();
            return this;

        }

        public STCriteriaBuilder endsWithString(String value) {
            breakIfNotNull(transientUniqueKey);
            breakIfNotNull(transientLocalKey);

            breakIfNull(transientPropertyName);
            breakIfNotNull(transientPropertyType);
            transientPropertyType = String.class;
            endsWith = value;
            and();
            return this;
        }

        public STCriteriaBuilder and() {
            STCriteriaItem item = null;
            if (transientUniqueKey != null) {
                breakIfNull(transientNodeEntryName);

                item = new STUniqueKeyCriteriaItemImpl(transientUniqueKey, transientNodeEntryName);

            } else if (transientLocalKey != null) {
                breakIfNull(transientNodeEntryName);

                item = new STLocalKeyCriteriaItemImpl(transientLocalKey, transientNodeEntryName);

            } else if (transientPropertyName != null) {

                breakIfNull(transientPropertyType);

                if (startsWith != null) {
                    item = new STPropertyStartsWithStringImpl(transientNodeEntryName, transientPropertyName, startsWith);
                } else if (endsWith != null) {
                    item = new STPropertyEndsWithStringImpl(transientNodeEntryName, transientPropertyName, endsWith);
                } else if (contains != null) {
                    item = new STPropertyContainsStringImpl(transientNodeEntryName, transientPropertyName, contains);
                } else {
                    item = new STPropertyCriteriaItemImpl(transientPropertyValue, transientPropertyType,
                            transientPropertyName, transientNodeEntryName);
                }
            }
            transientPropertyName = null;
            transientUniqueKey = null;
            transientLocalKey = null;
            transientPropertyType = null;
            transientPropertyValue = null;
            this.items.add(item);

            return this;
        }

        public STCriteria buildCriteria() {

            STCriteriaImpl result = new STCriteriaImpl(transientNodeEntryName, this.items, partition);
            and();

            return result;
        }

        public STCriteriaBuilder withLocalKey(STLocalKey localKey) {
            breakIfNotNull(transientUniqueKey);
            breakIfNotNull(transientPropertyName);
            breakIfNotNull(transientPropertyType);
            breakIfNotNull(transientPropertyValue);

            breakIfNotNull(transientLocalKey);

            transientLocalKey = localKey;
            transientNodeEntryName = localKey.getNodeEntryName();
            and();
            return this;
        }

        public STCriteriaBuilder withUniqueKey(STUniqueKey uniqueKey) {

            breakIfNotNull(transientLocalKey);
            breakIfNotNull(transientPropertyName);
            breakIfNotNull(transientPropertyType);
            breakIfNotNull(transientPropertyValue);

            breakIfNotNull(transientUniqueKey);

            transientUniqueKey = uniqueKey;
            transientNodeEntryName = uniqueKey.getLocalKey().getNodeEntryName();
            and();
            return this;
        }
    }

    public void removeNode(STNodeEntry stNodeEntry) {
        handleRemovedItem(stNodeEntry);
    }


    protected AbstractSTStorageSession(STFlushMode flushMode, STRepositoryPath repositoryPath) {
        this.flushMode = flushMode;
        this.repositoryPath = repositoryPath;
    }

    public STPartitionMethods withPartition(STPartition partition) {
        STPartitionMethods result = partitionMethods.get(partition);
        if (result == null) {
            result = new STPartitionMethodsImpl(partition);
            partitionMethods.put(partition, result);
        }
        return result;
    }

    protected void handleException(Exception e) {
        if (e instanceof RuntimeException) throw (RuntimeException) e;
        throw new RuntimeException(e);
    }

    private final STFlushMode flushMode;


    protected final Set<STNodeEntry> newNodes = newLinkedHashSet();

    protected final Set<STProperty> dirtyProperties = newLinkedHashSet();

    protected final Set<STNodeEntry> removedNodes = newLinkedHashSet();


    private void handleNewItem(STNodeEntry entry) {
        try {
            switch (getFlushMode()) {
                case AUTO:
                    flushNewItem(entry.getUniqueKey().getPartition(), entry);
                    break;
                case EXPLICIT:
                    newNodes.add(entry);
                    break;
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void handleRemovedItem(STNodeEntry entry) {
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

    private final class STStorageSessionInternalMethodsImpl implements STStorageSessionInternalMethods {

        private final STPartition partition;

        public STStorageSessionInternalMethodsImpl(STPartition partition) {
            this.partition = partition;
        }


        public STNodeEntryFactory.STNodeEntryBuilder nodeEntryCreateWithName(STNodeEntry stNodeEntry, String name) {
            return withPartition(partition).createWithName(AbstractSTStorageSession.this, name).withParent(stNodeEntry);
        }


        public void propertySetProperty(STProperty stProperty, byte[] value) {
            if (!partition.equals(stProperty.getParent().getUniqueKey().getPartition()))
                throw new IllegalArgumentException("wrong partition for this property");

            if (flushMode.equals(STFlushMode.AUTO)) {
                flushDirtyProperty(stProperty);
            } else {
                dirtyProperties.add(stProperty);
            }

        }

        public Set<STProperty> nodeEntryLoadProperties(STNodeEntry stNodeEntry) {
            if (!partition.equals(stNodeEntry.getUniqueKey().getPartition()))
                throw new IllegalArgumentException("wrong partition for this node entry");

            try {
                return internalNodeEntryLoadProperties(partition, stNodeEntry);
            } catch (Exception e) {
                handleException(e);
            }
            return null;
        }

        public STNodeEntry nodeEntryGetParent(STNodeEntry stNodeEntry) {
            if (!partition.equals(stNodeEntry.getUniqueKey().getPartition()))
                throw new IllegalArgumentException("wrong partition for this node entry");

            try {
                return internalNodeEntryGetParent(partition, stNodeEntry);
            } catch (Exception e) {
                handleException(e);
            }
            return null;
        }

        public Set<STNodeEntry> nodeEntryGetChildren(STNodeEntry stNodeEntry) {
            if (!partition.equals(stNodeEntry.getUniqueKey().getPartition()))
                throw new IllegalArgumentException("wrong partition for this node entry");

            try {
                return internalNodeEntryGetChildren(partition, stNodeEntry);
            } catch (Exception e) {
                handleException(e);
            }
            return null;
        }

        @Override
        public byte[] propertyGetValue(STProperty stProperty) {
            return internalPropertyGetValue(stProperty.getParent().getUniqueKey().getPartition(), stProperty);
        }


        public Set<STNodeEntry> nodeEntryGetNamedChildren(STNodeEntry stNodeEntry, String name) {
            if (!partition.equals(stNodeEntry.getUniqueKey().getPartition()))
                throw new IllegalArgumentException("wrong partition for this node entry");


            try {
                return internalNodeEntryGetNamedChildren(partition, stNodeEntry, name);

            } catch (Exception e) {
                handleException(e);
            }
            return null;
        }

    }

    protected abstract byte[] internalPropertyGetValue(STPartition partition, STProperty stProperty);


    public STFlushMode getFlushMode() {
        return flushMode;
    }


    private final class STNodeEntryBuilderImpl implements STNodeEntryFactory.STNodeEntryBuilder {

        private STNodeEntryBuilderImpl(String name, STPartition partition) {
            this.name = name;
            this.partition = partition;
        }

        private final STPartition partition;

        private final String name;

        private STUniqueKey parentKey = null;

        private Set<STKeyEntry> keys = newHashSet();
        private Set<String> keyNames = newHashSet();

        public STNodeEntryFactory.STNodeEntryBuilder withKey(String name, String value) {
            if (keyNames.contains(name)) throw new IllegalStateException("key name already inserted");
            this.keys.add(new STKeyEntryImpl(name, value));
            this.keyNames.add(name);
            return this;
        }

        public STNodeEntryFactory.STNodeEntryBuilder withParentKey(STUniqueKey parentKey) {
            if (this.parentKey != null) {
                throw new IllegalStateException();
            }
            this.parentKey = parentKey;
            return this;
        }

        public STNodeEntryFactory.STNodeEntryBuilder withParent(STNodeEntry parent) {
            return withParentKey(parent.getUniqueKey());
        }

        public STNodeEntry andCreate() {
            STLocalKeyImpl localKey = new STLocalKeyImpl(keys, name);

            STUniqueKeyImpl uniqueKey = new STUniqueKeyImpl(localKey, parentKey, partition, repositoryPath);
            STNodeEntryImpl result = new STNodeEntryImpl(uniqueKey, false);
            AbstractSTStorageSession.this.handleNewItem(result);
            return result;
        }


    }

    public void flushTransient() {
        Set<STPartition> partitions = newHashSet();

        for (STNodeEntry newNode : newNodes) {
            try {
                partitions.add(newNode.getUniqueKey().getPartition());
                flushNewItem(newNode.getUniqueKey().getPartition(), newNode);
            } catch (Exception e) {
                handleException(e);
            }
        }
        for (STNodeEntry removedNode : removedNodes) {
            try {
                partitions.add(removedNode.getUniqueKey().getPartition());
                flushRemovedItem(removedNode.getUniqueKey().getPartition(), removedNode);
            } catch (Exception e) {
                handleException(e);
            }
        }

        for (STProperty data : dirtyProperties) {
            try {
                flushDirtyProperty(data);
            } catch (Exception e) {
                handleException(e);
            }
        }
        try {

            internalSave(partitions);
        } catch (Exception e) {
            handleException(e);
        }
        discardTransient();

    }

    protected abstract void internalSave(Set<STPartition> partitions) throws Exception;

    private void flushDirtyProperty(STProperty dirtyProperty) {
        try {

            internalFlushSimpleProperty(dirtyProperty.getParent().getUniqueKey().getPartition(), dirtyProperty);

        } catch (Exception e) {
            handleException(e);
        }
    }


    private class STUniqueKeyBuilderImpl implements STUniqueKeyBuilder {

        private Set<STKeyEntry> localEntries = newHashSet();
        private Set<String> namesInsideEntries = newHashSet();
        private final String name;

        private final STPartition partition;

        private final STUniqueKeyBuilderImpl child;

        public STUniqueKeyBuilderImpl(String name, STPartition partition) {
            this.name = name;
            this.partition = partition;
            this.child = null;
        }

        private STUniqueKeyBuilderImpl(String name, STUniqueKeyBuilderImpl child, STPartition partition) {
            this.name = name;
            this.child = child;
            this.partition = partition;
        }

        public STUniqueKeyBuilder withEntry(String propertyName, String value) {
            this.localEntries.add(new STKeyEntryImpl(propertyName, value));
            return this;
        }

        public STUniqueKeyBuilder withParent(String nodeEntryName) {
            return new STUniqueKeyBuilderImpl(nodeEntryName, this, partition);
        }

        public STUniqueKey andCreate() {

            STUniqueKey currentKey = null;
            STUniqueKeyBuilderImpl currentBuilder = this;
            while (currentBuilder != null) {
                STLocalKey localKey = new STLocalKeyImpl(currentBuilder.localEntries, currentBuilder.name);
                currentKey = new STUniqueKeyImpl(localKey, currentKey, partition, repositoryPath);
                currentBuilder = currentBuilder.child;
            }
            return currentKey;

        }


    }


    protected abstract Set<STNodeEntry> internalFindByCriteria(STPartition partition, STCriteria criteria) throws Exception;

    protected abstract void flushNewItem(STPartition partition, STNodeEntry entry) throws Exception;

    protected abstract void flushRemovedItem(STPartition partition, STNodeEntry entry) throws Exception;

    protected abstract Set<STNodeEntry> internalNodeEntryGetNamedChildren(STPartition partition, STNodeEntry stNodeEntry, String name) throws Exception;

    protected abstract boolean internalHasSavedProperty(STPartition partition, STProperty stProperty) throws Exception;

    protected abstract void internalFlushSimpleProperty(STPartition partition, STProperty dirtyProperty) throws Exception;

    protected abstract Set<STNodeEntry> internalNodeEntryGetChildren(STPartition partition, STNodeEntry stNodeEntry) throws Exception;

    protected abstract STNodeEntry internalNodeEntryGetParent(STPartition partition, STNodeEntry stNodeEntry) throws Exception;

    protected abstract <T> T internalPropertyGetSimplePropertyAs(STPartition partition, STProperty stProperty, Class<T> type) throws Exception;

    protected abstract Set<STProperty> internalNodeEntryLoadProperties(STPartition partition, STNodeEntry stNodeEntry) throws Exception;

    protected abstract Set<STNodeEntry> internalFindNamed(STPartition partition, String nodeEntryName) throws Exception;

}
