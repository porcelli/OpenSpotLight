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

import com.google.common.collect.ImmutableList;
import org.openspotlight.storage.domain.STAData;
import org.openspotlight.storage.domain.key.*;
import org.openspotlight.storage.domain.node.STNodeEntry;
import org.openspotlight.storage.domain.node.STNodeEntryImpl;
import org.openspotlight.storage.domain.property.*;

import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

/**
 * Created by User: feu - Date: Mar 22, 2010 - Time: 2:19:49 PM
 */
public abstract class AbstractSTStorageSession implements STStorageSession {

    private static class STCriteriaItemImpl<T extends Serializable> implements STCriteria.STCriteriaItem<T>{
        private STCriteriaItemImpl(T value, Class<T> type, String propertyName, boolean not) {
            this.value = value;
            this.type = type;
            this.propertyName = propertyName;
            this.not = not;
        }

        private final T value;

        private final Class<T> type;

        private final String propertyName;

        private final boolean not;

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            STCriteriaItemImpl that = (STCriteriaItemImpl) o;

            if (not != that.not) return false;
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
            return result;
        }
    }

    private static class STCriteriaImpl implements STCriteria{
        private STCriteriaImpl(String nodeName, List<STCriteriaItem<?>> criteriaItems) {
            this.nodeName = nodeName;
            this.criteriaItems = ImmutableList.copyOf(criteriaItems);
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

        private final List<STCriteriaItem<?>> criteriaItems;

        public String getNodeName() {
            return nodeName;
        }

        public List<STCriteriaItem<?>> getCriteriaItems() {
            return criteriaItems;
        }

        public List<STNodeEntry> andFind(STStorageSession session) {
            return session.findByCriteria(this);
        }

        public STNodeEntry andFindUnique(STStorageSession session) {
            return session.findUniqueByCriteria(this);
        }
    }

    private static class STCriteriaBuilderImpl implements STCriteriaBuilder{

        public STCriteriaBuilder withProperty(String propertyName) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public STCriteriaBuilder withNodeEntry(String nodeName) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T extends Serializable> STCriteriaBuilder equals(Class<T> type, T value) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T extends Serializable> STCriteriaBuilder notEquals(Class<T> type, T value) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public STCriteriaBuilder and() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public STCriteria buildCriteria() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public STCriteriaBuilder withLocalKey(STLocalKey localKey) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public STCriteriaBuilder withUniqueKey(STUniqueKey uniqueKey) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    protected abstract List<STNodeEntry> internalFindByCriteria(STCriteria criteria);

    protected abstract STNodeEntry internalFindUniqueByCriteria(STCriteria criteria);

    public List<STNodeEntry> findByCriteria(STCriteria criteria) {
        return internalFindByCriteria(criteria);
    }

    public STNodeEntry findUniqueByCriteria(STCriteria criteria) {
        return internalFindUniqueByCriteria(criteria);
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
        return createNewInstance(this.flushMode,partition);
    }

    protected void handleException(Exception e) {
        throw new RuntimeException(e);
    }

    private final STFlushMode flushMode;

    private final STPartition partition;

    protected final List<STNodeEntry> newNodes = new LinkedList<STNodeEntry>();

    protected final List<STAData> dirtyProperties = new LinkedList<STAData>();

    protected final List<STNodeEntry> removedNodes = new LinkedList<STNodeEntry>();

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

        public <T> STSetProperty nodeEntryGetSetProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T> void nodeEntrySetSetProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name, Set<T> value) {
            return;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public List<STSetProperty> nodeEntryGetSetProperties(STNodeEntry stNodeEntry) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public STNodeEntryBuilder nodeEntryCreateWithName(STNodeEntry stNodeEntry, String name) {
            return createWithName(AbstractSTStorageSession.this, name).withParent(stNodeEntry);
        }

        public List<STListProperty> nodeEntryGetListProperties(STNodeEntry stNodeEntry) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T> void nodeEntrySetListProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name, List<T> value) {
            return;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T> STListProperty nodeEntryGetListProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public List<STSimpleProperty> nodeEntryGetSimpleProperties(STNodeEntry stNodeEntry) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T> void nodeEntrySetSimpleProperty(STNodeEntry stNodeEntry, Class<T> type, String name, T value) {
            return;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T> STSimpleProperty nodeEntryGetSimpleProperty(STNodeEntry stNodeEntry, Class<T> type, String name) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public List<STStreamProperty> nodeEntryGetStreamProperties(STNodeEntry stNodeEntry) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T> void nodeEntrySetStreamProperty(STNodeEntry stNodeEntry, String name, T value) {
            return;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public STStreamProperty nodeEntryGetStreamProperty(STNodeEntry stNodeEntry, String name) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public List<STMapProperty> nodeEntryGetMapProperties(STNodeEntry stNodeEntry) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <K, V> void nodeEntrySetMapProperty(STNodeEntry stNodeEntry, Class<K> keyType, Class<V> valueType, String name, Map<K, V> value) {
            return;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <K, V> STMapProperty nodeEntryGetMapProperty(STNodeEntry stNodeEntry, Class<K> keyType, Class<V> valueType, String name) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public List<STSerializableListProperty> nodeEntryGetSerializableListProperties(STNodeEntry stNodeEntry) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T> void nodeEntrySetSerializableListProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name, List<T> value) {
            return;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T> STSerializableListProperty nodeEntryGetSerializableListProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public List<STSerializableMapProperty> nodeEntryGetSerializableMapProperties(STNodeEntry stNodeEntry) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <K, V> void nodeEntrySetSerializableMapProperty(STNodeEntry stNodeEntry, Class<K> keyType, Class<V> valueType, String name, Map<K, V> value) {
            return;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <K, V> STSerializableMapProperty nodeEntryGetSerializableMapProperty(STNodeEntry stNodeEntry, Class<K> keyType, Class<V> valueType, String name) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public List<STSerializableSetProperty> nodeEntryGetSerializableSetProperties(STNodeEntry stNodeEntry) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T> void nodeEntrySetSerializableSetProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name, Set<T> value) {
            return;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T> STSerializableSetProperty nodeEntryGetSerializableSetProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public List<STPojoProperty> nodeEntryGetPojoProperties(STNodeEntry stNodeEntry) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T> void nodeEntrySetPojoProperty(STNodeEntry stNodeEntry, Class<T> type, String name, T value) {
            return;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T> STPojoProperty nodeEntryGetPojoProperty(STNodeEntry stNodeEntry, Class<T> type, String name) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T> List<T> listPropertyGetItems(STListProperty stListProperty) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T> Set<T> setPropertyGetItems(STSetProperty stSetProperty) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <K, T> Map<K, T> mapPropertyGetMap(STMapProperty stMapProperty) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T> List<T> serializableListPropertyGetItems(STSerializableListProperty stSerializableListProperty) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public InputStream streamPropertyGetValue(STStreamProperty stStreamProperty) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T extends Serializable> T pojoPropertyGetValue(STPojoProperty stPojoProperty) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T> Set<T> serializableSetPropertyGetItems(STSerializableSetProperty stSerializableSetProperty) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <K, T> Map<K, T> serializableMapGetMap(STSerializableMapProperty stSerializableMapProperty) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T extends Serializable> T simplePropertyGetValue(STSimpleProperty stSimpleProperty) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }


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
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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

            STUniqueKeyImpl uniqueKey;
            if (parent != null) {
                List<STLocalKey> allKeys = parent.getUniqueKey().getAllKeys();
                allKeys.add(localKey);
                uniqueKey = new STUniqueKeyImpl(allKeys);

            } else {
                uniqueKey = new STUniqueKeyImpl(ImmutableList.<STLocalKey>builder().add(localKey).build());
            }


            STNodeEntryImpl result = new STNodeEntryImpl(name, localKey, uniqueKey);
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
}
