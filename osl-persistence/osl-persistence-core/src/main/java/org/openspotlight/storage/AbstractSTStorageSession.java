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
import org.openspotlight.storage.domain.node.STNodeEntryImpl;
import org.openspotlight.storage.domain.node.STProperty;

import java.io.Serializable;
import java.util.*;

import static org.openspotlight.common.util.Sha1.getSha1Signature;
import static org.openspotlight.common.util.Sha1.getSha1SignatureEncodedAsBase64;

/**
 * Created by User: feu - Date: Mar 22, 2010 - Time: 2:19:49 PM
 */
public abstract class AbstractSTStorageSession implements STStorageSession {

    protected final STStorageSessionSupportMethods supportMethods = new STStorageSessionSupportMethods();

    protected final STNodeEntry createEntryWithKey(STUniqueKey uniqueKey){
        return new STNodeEntryImpl(uniqueKey);
    }

    protected class STStorageSessionSupportMethods {


        public String getLocalKeyAsStringHash(STLocalKey localKey) {
            return getSha1SignatureEncodedAsBase64(getLocalKeyAsSimpleString(localKey));
        }

        public String getUniqueKeyAsStringHash(STUniqueKey uniqueKey) {
            return getSha1SignatureEncodedAsBase64(getUniqueKeyAsSimpleString(uniqueKey));
        }


        public byte[] getLocalKeyAsByteHash(STLocalKey localKey) {
            return getSha1Signature(getLocalKeyAsSimpleString(localKey));
        }


        public byte[] getUniqueKeyAsByteHash(STUniqueKey uniqueKey) {
            return getSha1Signature(getUniqueKeyAsSimpleString(uniqueKey));

        }


        public String getLocalKeyAsSimpleString(STLocalKey localKey) {
            StringBuilder sb = new StringBuilder();
            sb.append(localKey.getNodeEntryName());
            for (STKeyEntry entry : localKey.getEntries()) {
                sb.append(":").append(entry.getPropertyName()).append(":")
                        .append(entry.getType().getName()).append(":").append(entry.getValue());
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

            return sb.toString();
        }

    }

    private static class STUniqueKeyCriteriaItemImpl implements STUniqueKeyCriteriaItem {
        private STUniqueKeyCriteriaItemImpl(STUniqueKey value, boolean not, String nodeEntryName) {
            this.value = value;
            this.not = not;
            this.nodeEntryName = nodeEntryName;
        }

        private final STUniqueKey value;

        private final boolean not;

        private final String nodeEntryName;

        public STUniqueKey getValue() {
            return value;
        }

        public boolean isNot() {
            return not;
        }

        public String getNodeEntryName() {
            return nodeEntryName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            STUniqueKeyCriteriaItemImpl that = (STUniqueKeyCriteriaItemImpl) o;

            if (not != that.not) return false;
            if (nodeEntryName != null ? !nodeEntryName.equals(that.nodeEntryName) : that.nodeEntryName != null)
                return false;
            if (value != null ? !value.equals(that.value) : that.value != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = value != null ? value.hashCode() : 0;
            result = 31 * result + (not ? 1 : 0);
            result = 31 * result + (nodeEntryName != null ? nodeEntryName.hashCode() : 0);
            return result;
        }


    }

    private static class STLocalKeyCriteriaItemImpl implements STLocalKeyCriteriaItem {
        private STLocalKeyCriteriaItemImpl(STLocalKey value, boolean not, String nodeEntryName) {
            this.value = value;
            this.not = not;
            this.nodeEntryName = nodeEntryName;
        }

        private final STLocalKey value;

        private final boolean not;

        private final String nodeEntryName;

        public STLocalKey getValue() {
            return value;
        }

        public boolean isNot() {
            return not;
        }

        public String getNodeEntryName() {
            return nodeEntryName;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            STLocalKeyCriteriaItemImpl that = (STLocalKeyCriteriaItemImpl) o;

            if (not != that.not) return false;
            if (nodeEntryName != null ? !nodeEntryName.equals(that.nodeEntryName) : that.nodeEntryName != null)
                return false;
            if (value != null ? !value.equals(that.value) : that.value != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = value != null ? value.hashCode() : 0;
            result = 31 * result + (not ? 1 : 0);
            result = 31 * result + (nodeEntryName != null ? nodeEntryName.hashCode() : 0);
            return result;
        }

    }

    private static class STPropertyCriteriaItemImpl<T extends Serializable> implements STPropertyCriteriaItem<T> {
        private STPropertyCriteriaItemImpl(T value, Class<T> type, String propertyName, boolean not, String nodeEntryName) {
            this.value = value;
            this.type = type;
            this.propertyName = propertyName;
            this.not = not;
            this.nodeEntryName = nodeEntryName;
        }

        private final T value;

        private final Class<T> type;

        private final String propertyName;

        private final boolean not;

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

        public boolean isNot() {
            return not;
        }

        public String getNodeEntryName() {
            return nodeEntryName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            STPropertyCriteriaItemImpl that = (STPropertyCriteriaItemImpl) o;

            if (not != that.not) return false;
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
            result = 31 * result + (not ? 1 : 0);
            result = 31 * result + (nodeEntryName != null ? nodeEntryName.hashCode() : 0);
            return result;
        }


    }

    private static class STCriteriaImpl implements STCriteria {
        private STCriteriaImpl(String nodeName, Set<STCriteriaItem> criteriaItems) {
            this.nodeName = nodeName;
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

            return true;
        }

        @Override
        public int hashCode() {
            int result = nodeName != null ? nodeName.hashCode() : 0;
            result = 31 * result + (criteriaItems != null ? criteriaItems.hashCode() : 0);
            return result;
        }

        private final String nodeName;

        private final Set<STCriteriaItem> criteriaItems;

        public String getNodeName() {
            return nodeName;
        }

        public Set<STCriteriaItem> getCriteriaItems() {
            return criteriaItems;
        }

        public Set<STNodeEntry> andFind(STStorageSession session) {
            return session.findByCriteria(this);
        }

        public STNodeEntry andFindUnique(STStorageSession session) {
            return session.findUniqueByCriteria(this);
        }
    }

    private static class STCriteriaBuilderImpl implements STCriteriaBuilder {

        private String transientNodeEntryName;

        private String transientPropertyName;

        private Boolean transientNot;

        private STUniqueKey transientUniqueKey;

        private STLocalKey transientLocalKey;

        private Class<? extends Serializable> transientPropertyType;

        private Serializable transientPropertyValue;

        Set<STCriteriaItem> items = new LinkedHashSet();

        private void breakIfNotNull(Object o) {
            if (o != null) throw new IllegalStateException();
        }

        private void breakIfNull(Object o) {
            if (o == null) throw new IllegalStateException();
        }

        private void breakHere() {
            throw new IllegalStateException();
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

        public <T extends Serializable> STCriteriaBuilder equals(Class<T> type, T value) {
            breakIfNotNull(transientUniqueKey);
            breakIfNotNull(transientLocalKey);

            breakIfNull(transientPropertyName);
            breakIfNotNull(transientPropertyType);
            breakIfNotNull(transientPropertyValue);
            transientPropertyType = type;
            transientPropertyValue = value;
            transientNot = false;
            and();
            return this;
        }

        public <T extends Serializable> STCriteriaBuilder notEquals(Class<T> type, T value) {
            breakIfNotNull(transientUniqueKey);
            breakIfNotNull(transientLocalKey);

            breakIfNull(transientPropertyName);
            breakIfNotNull(transientPropertyType);
            breakIfNotNull(transientPropertyValue);
            transientPropertyType = type;
            transientPropertyValue = value;
            transientNot = true;
            and();
            return this;
        }

        public STCriteriaBuilder and() {
            STCriteriaItem item = null;
            breakIfNull(transientNodeEntryName);
            if (transientUniqueKey != null) {
                item = new STUniqueKeyCriteriaItemImpl(transientUniqueKey, false, transientNodeEntryName);

            } else if (transientLocalKey != null) {

                item = new STLocalKeyCriteriaItemImpl(transientLocalKey, false, transientNodeEntryName);

            } else if (transientPropertyName != null) {

                breakIfNull(transientPropertyValue);
                breakIfNull(transientPropertyType);
                breakIfNull(transientNot);

                item = new STPropertyCriteriaItemImpl(transientPropertyValue, transientPropertyType, transientPropertyName, transientNot, transientNodeEntryName);

            }
            transientPropertyName = null;
            transientNot = null;
            transientUniqueKey = null;
            transientLocalKey = null;
            transientPropertyType = null;
            transientPropertyValue = null;
            this.items.add(item);

            return this;
        }

        public STCriteria buildCriteria() {
            breakIfNull(transientNodeEntryName);
            if (this.items.size() == 0) breakHere();

            STCriteriaImpl result = new STCriteriaImpl(transientNodeEntryName, this.items);
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
            and();
            return this;
        }
    }

    protected abstract Set<STNodeEntry> internalFindByCriteria(STCriteria criteria) throws Exception;


    public Set<STNodeEntry> findByCriteria(STCriteria criteria) {
        try {
            return internalFindByCriteria(criteria);
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    public STNodeEntry findUniqueByCriteria(STCriteria criteria) {
        try {
            Set<STNodeEntry> result = internalFindByCriteria(criteria);
            if (result == null) return null;
            if (result.size() == 0) return null;
            if (result.size() == 1) return result.iterator().next();
            throw new IllegalStateException();
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    public STCriteriaBuilder createCriteria() {
        return new STCriteriaBuilderImpl();
    }

    protected AbstractSTStorageSession(STFlushMode flushMode, STPartition partition) {
        this.flushMode = flushMode;
        this.partition = partition;

    }


    protected abstract STStorageSession createNewInstance(STFlushMode flushMode, STPartition partition);

    public STPartition getCurrentPartition() {
        return partition;
    }

    public STStorageSession withPartition(STPartition partition) {
        return createNewInstance(this.flushMode, partition);
    }

    protected void handleException(Exception e) {
        throw new RuntimeException(e);
    }

    private final STFlushMode flushMode;

    private final STPartition partition;

    protected final Set<STNodeEntry> newNodes = new LinkedHashSet<STNodeEntry>();

    protected final Set<STAData> dirtyProperties = new LinkedHashSet<STAData>();

    protected final Set<STNodeEntry> removedNodes = new LinkedHashSet<STNodeEntry>();

    private final STStorageSessionInternalMethods internalMethods = new STStorageSessionInternalMethodsImpl();


    protected abstract void flushNewItem(STNodeEntry entry) throws Exception;

    protected abstract void flushRemovedItem(STNodeEntry entry) throws Exception;

    private void handleNewItem(STNodeEntry entry) {
        try {
            switch (getFlushMode()) {
                case AUTO:
                    flushNewItem(entry);
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
                    flushRemovedItem(entry);
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


        public STNodeEntryBuilder nodeEntryCreateWithName(STNodeEntry stNodeEntry, String name) {
            return createWithName(AbstractSTStorageSession.this, name).withParent(stNodeEntry);
        }

        public <T> T propertyGetPropertyAs(STProperty stProperty, Class<T> type) {
            switch (stProperty.getDescription()) {
                case SIMPLE:
                    return internalPropertyGetSimplePropertyAs(stProperty,type);
                case LIST:
                    return internalPropertyGetListPropertyAs(stProperty,type);
                case SET:
                    return internalPropertyGetSetPropertyAs(stProperty,type);
                case MAP:
                    return internalPropertyGetMapPropertyAs(stProperty,type);
                case SERIALIZED_LIST:
                    return internalPropertyGetSerializedListPropertyAs(stProperty,type);
                case SERIALIZED_SET:
                    return internalPropertyGetSerializedSetPropertyAs(stProperty,type);
                case SERIALIZED_MAP:
                    return internalPropertyGetSerializedMapPropertyAs(stProperty,type);
                case SERIALIZED_POJO:
                    return internalPropertyGetSerializedPojoPropertyAs(stProperty,type);
                case INPUT_STREAM:
                    return internalPropertyGetInputStreamPropertyAs(stProperty,type);
                default:
                    throw new IllegalArgumentException("missing entry on Property description");
            }
        }

        public <T> void propertySetProperty(STProperty stProperty, T value) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public Set<STProperty> nodeEntryLoadProperties(STNodeEntry stNodeEntry) {
            return internalNodeEntryLoadProperties(stNodeEntry);
        }

    }

    protected abstract <T> T internalPropertyGetInputStreamPropertyAs(STProperty stProperty, Class<T> type);

    protected abstract <T> T internalPropertyGetSerializedPojoPropertyAs(STProperty stProperty, Class<T> type);

    protected abstract <T> T internalPropertyGetSerializedMapPropertyAs(STProperty stProperty, Class<T> type);

    protected abstract <T> T internalPropertyGetSerializedSetPropertyAs(STProperty stProperty, Class<T> type);

    protected abstract <T> T internalPropertyGetSerializedListPropertyAs(STProperty stProperty, Class<T> type);

    protected abstract <T> T internalPropertyGetMapPropertyAs(STProperty stProperty, Class<T> type);

    protected abstract <T> T internalPropertyGetSetPropertyAs(STProperty stProperty, Class<T> type);

    protected abstract <T> T internalPropertyGetListPropertyAs(STProperty stProperty, Class<T> type);

    protected abstract <T> T internalPropertyGetSimplePropertyAs(STProperty stProperty, Class<T> type);

    protected abstract Set<STProperty> internalNodeEntryLoadProperties(STNodeEntry stNodeEntry);



    public STFlushMode getFlushMode() {
        return flushMode;
    }

    public STNodeEntryBuilder createWithName(String name) {
        return this.createWithName(this, name);
    }

    public STStorageSessionInternalMethods getInternalMethods() {
        return internalMethods;
    }

    public STNodeEntryBuilder createWithName(STStorageSession session, String name) {
        return new STNodeEntryBuilderImpl(name);
    }

    private final class STNodeEntryBuilderImpl implements STNodeEntryBuilder {

        private STNodeEntryBuilderImpl(String name) {
            this.name = name;
        }

        private final String name;

        private STNodeEntry parent = null;

        private Set<STKeyEntry<?>> keys = new HashSet<STKeyEntry<?>>();

        public <T extends Serializable> STNodeEntryBuilder withKey(String name, Class<T> type, T value) {
            this.keys.add(STKeyEntryImpl.create(type, value, name));
            return this;
        }

        public STNodeEntryBuilder withParent(STNodeEntry parent) {
            if (this.parent != null) {
                throw new IllegalStateException();
            }
            this.parent = parent;
            return this;
        }

        public STNodeEntry andCreate() {
            STLocalKeyImpl localKey = new STLocalKeyImpl(keys, name);

            STUniqueKeyImpl uniqueKey = new STUniqueKeyImpl(localKey, parent!=null?parent.getUniqueKey():null);
            STNodeEntryImpl result = new STNodeEntryImpl(uniqueKey);
            AbstractSTStorageSession.this.handleNewItem(result);
            return result;
        }


    }

    public void flushTransient() {

        for (STNodeEntry newNode : newNodes) {
            try {
                flushNewItem(newNode);
            } catch (Exception e) {
                handleException(e);
            }
        }
        for (STNodeEntry removedNode : removedNodes) {
            try {
                flushRemovedItem(removedNode);
            } catch (Exception e) {
                handleException(e);
            }
        }

        for (STAData data : dirtyProperties) {
            try {
                flushDirtyProperty(data);
            } catch (Exception e) {
                handleException(e);
            }
        }


        this.newNodes.clear();
        this.removedNodes.clear();
        this.dirtyProperties.clear();

    }

    private void flushDirtyProperty(STAData data) {
        //FIXME implement

    }

    public STUniqueKeyBuilder createKey(String nodeEntryName) {
        return new STUniqueKeyBuilderImpl(nodeEntryName);
    }


    private static class STUniqueKeyBuilderImpl implements STUniqueKeyBuilder {

        private Set<STKeyEntry<?>> localEntries = new HashSet<STKeyEntry<?>>();

        private final String name;

        private final STUniqueKeyBuilderImpl child;

        public STUniqueKeyBuilderImpl(String name) {
            this.name = name;
            this.child = null;
        }

        private STUniqueKeyBuilderImpl(String name, STUniqueKeyBuilderImpl child) {
            this.name = name;
            this.child = child;
        }

        public <T extends Serializable> STUniqueKeyBuilder withEntry(String propertyName, Class<T> type, Serializable value) {
            this.localEntries.add(STKeyEntryImpl.<T>create(type, (T)value, propertyName));
            return this;
        }

        public STUniqueKeyBuilder withParent(String nodeEntryName) {
            return new STUniqueKeyBuilderImpl(nodeEntryName, this);
        }

        public STUniqueKey andCreate() {

            STUniqueKey currentKey = null;
            STUniqueKeyBuilderImpl currentBuilder = this;
            while (currentBuilder != null) {
                STLocalKey localKey = new STLocalKeyImpl(currentBuilder.localEntries, currentBuilder.name);
                currentKey = new STUniqueKeyImpl(localKey, currentKey);
                currentBuilder = currentBuilder.child;
            }
            return currentKey;

        }


    }
}
