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
 * OpenSpotLight - Plataforma de Governan�a de TI de C�digo Aberto
 * *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribui��o de direito autoral declarada e atribu�da pelo autor.
 * Todas as contribui��es de terceiros est�o distribu�das sob licen�a da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA.
 *
 * Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo sob os
 * termos da Licen�a P�blica Geral Menor do GNU conforme publicada pela Free Software
 * Foundation.
 *
 * Este programa � distribu�do na expectativa de que seja �til, por�m, SEM NENHUMA
 * GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU ADEQUA��O A UMA
 * FINALIDADE ESPEC�FICA. Consulte a Licen�a P�blica Geral Menor do GNU para mais detalhes.
 *
 * Voc� deve ter recebido uma c�pia da Licen�a P�blica Geral Menor do GNU junto com este
 * programa; se n�o, escreva para:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */

package org.openspotlight.storage.domain.node;

import com.google.inject.internal.ImmutableSet;
import org.apache.commons.io.IOUtils;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.key.STLocalKey;
import org.openspotlight.storage.domain.key.STUniqueKey;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import static com.google.common.collect.Maps.newHashMap;
import static java.text.MessageFormat.format;
import static org.openspotlight.common.util.Equals.eachEquality;


public class STNodeEntryImpl implements STNodeEntry {

    public STNodeEntryImpl(STUniqueKey uniqueKey, Set<STProperty> properties) {

        this.nodeEntryName = uniqueKey.getLocalKey().getNodeEntryName();
        if (nodeEntryName == null) throw new IllegalStateException();
        this.localKey = uniqueKey.getLocalKey();
        this.uniqueKey = uniqueKey;
        propertiesByName = newHashMap();
        if (properties != null) {
            for (STProperty property : properties) {
                this.propertiesByName.put(property.getPropertyName(), property);
            }
        }
        partition = uniqueKey.getPartition();
        verifiedOperations = new STPropertyOperationImpl(true, this);
        unverifiedOperations = new STPropertyOperationImpl(false, this);
        namedChildrenWeakReference = new WeakHashMap<Set<STNodeEntry>, String>();
    }


    public STNodeEntryImpl(STUniqueKey uniqueKey) {
        this(uniqueKey, null);
    }

    private final STPartition partition;

    private WeakReference<STNodeEntry> parentWeakReference = null;

    private WeakReference<Set<STNodeEntry>> childrenWeakReference = null;

    private WeakHashMap<Set<STNodeEntry>, String> namedChildrenWeakReference;

    private final Map<String, STProperty> propertiesByName;

    private final STPropertyOperation verifiedOperations;

    private final STPropertyOperation unverifiedOperations;

    private final String nodeEntryName;

    private final STLocalKey localKey;

    private final STUniqueKey uniqueKey;

    public String getNodeEntryName() {
        return nodeEntryName;
    }

    public STLocalKey getLocalKey() {
        return localKey;
    }

    public STUniqueKey getUniqueKey() {
        return uniqueKey;
    }

    public STProperty getProperty(STStorageSession session, String name) {
        loadPropertiesOnce(session);
        STProperty result = propertiesByName.get(name);
        if (result == null) {
            reloadProperties(session);
            result = propertiesByName.get(name);
        }
        return result;
    }

    public <T> T getPropertyValue(STStorageSession session, String name) {
        loadPropertiesOnce(session);
        STProperty property = getProperty(session, name);
        return property != null ? property.<T>getValue(session) : null;
    }

    public Set<STNodeEntry> getChildren(STStorageSession session) {
        Set<STNodeEntry> children = childrenWeakReference != null ? childrenWeakReference.get() : null;
        if (children == null) {
            children = getChildrenForcingReload(session);
        }
        return children;
    }

    public Set<STNodeEntry> getChildrenNamed(STStorageSession session, String name) {

        Set<STNodeEntry> thisChildren = null;
        if (namedChildrenWeakReference.containsValue(name)) {
            for (Map.Entry<Set<STNodeEntry>, String> entry : namedChildrenWeakReference.entrySet()) {
                if (name.equals(entry.getValue())) {
                    thisChildren = entry.getKey();
                    break;
                }
            }
        }

        if (thisChildren == null) {
            thisChildren = getChildrenNamedForcingReload(session, name);
        }
        return thisChildren;
    }

    public Set<STNodeEntry> getChildrenForcingReload(STStorageSession session) {
        Set<STNodeEntry> children = session.withPartition(partition).getInternalMethods().nodeEntryGetChildren(this);
        childrenWeakReference = new WeakReference<Set<STNodeEntry>>(children);
        return children;
    }

    public Set<STNodeEntry> getChildrenNamedForcingReload(STStorageSession session, String name) {
        Set<STNodeEntry> children = session.withPartition(partition).getInternalMethods().nodeEntryGetNamedChildren(this, name);
        namedChildrenWeakReference.put(children, name);
        return children;
    }

    public STNodeEntry getParent(STStorageSession session) {
        STNodeEntry parent = parentWeakReference != null ? parentWeakReference.get() : null;
        if (parent == null) {
            parent = session.withPartition(partition).getInternalMethods().nodeEntryGetParent(this);
            parentWeakReference = new WeakReference<STNodeEntry>(parent);
        }
        return parent;
    }

    public void removeNode(STStorageSession session) {
        session.removeNode(this);
    }

    public Set<String> getPropertyNames(STStorageSession session) {
        reloadProperties(session);
        return ImmutableSet.copyOf(propertiesByName.keySet());
    }

    public Set<STProperty> getProperties(STStorageSession session) {
        reloadProperties(session);
        return ImmutableSet.copyOf(propertiesByName.values());
    }

    private void reloadProperties(STStorageSession session) {
        Set<STProperty> result = session.withPartition(partition).getInternalMethods().nodeEntryLoadProperties(this);
        for (STProperty property : result) {
            propertiesByName.put(property.getPropertyName(), property);
        }
    }

    private void loadPropertiesOnce(STStorageSession session) {
        if (propertiesByName.isEmpty()) {
            reloadProperties(session);
        }
    }

    public STNodeEntryBuilder createWithName(final STStorageSession session, final String name) {
        return session.withPartition(partition).getInternalMethods().nodeEntryCreateWithName(this, name);

    }

    public STPropertyOperation getVerifiedOperations() {
        return verifiedOperations;
    }

    public STPropertyOperation getUnverifiedOperations() {
        return unverifiedOperations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        STNodeEntryImpl that = (STNodeEntryImpl) o;

        if (localKey != null ? !localKey.equals(that.localKey) : that.localKey != null) return false;
        if (nodeEntryName != null ? !nodeEntryName.equals(that.nodeEntryName) : that.nodeEntryName != null)
            return false;
        return !(uniqueKey != null ? !uniqueKey.equals(that.uniqueKey) : that.uniqueKey != null);
    }

    @Override
    public int hashCode() {
        int result = nodeEntryName != null ? nodeEntryName.hashCode() : 0;
        result = 31 * result + (localKey != null ? localKey.hashCode() : 0);
        result = 31 * result + (uniqueKey != null ? uniqueKey.hashCode() : 0);
        return result;
    }

    public int compareTo(STNodeEntry o) {
        return this.getLocalKey().compareTo(o.getLocalKey());
    }

    private static class STPropertyOperationImpl implements STPropertyOperation {

        private final boolean verifyBefore;

        private final STNodeEntryImpl parent;

        public STPropertyOperationImpl(boolean verifyBefore, STNodeEntryImpl parent) {
            this.verifyBefore = verifyBefore;
            this.parent = parent;
        }

        public <T extends Serializable> STProperty setSimpleProperty(STStorageSession session, String name, Class<? super T> propertyType, T value) {
            STProperty currentProperty = parent.getProperty(session, name);
            if (currentProperty != null) {
                validatePropertyDescription(currentProperty, STProperty.STPropertyDescription.SIMPLE,propertyType,null,null);
            } else {
                currentProperty = new STPropertyImpl(parent, name, STProperty.STPropertyDescription.SIMPLE, propertyType);
                parent.propertiesByName.put(name, currentProperty);
            }
            currentProperty.setValue(session, value);

            return currentProperty;
        }

        public <V extends Serializable> STProperty setSerializedListProperty(STStorageSession session, String name, Class<? super V> parameterizedType, List<V> value) {
            STProperty currentProperty = parent.getProperty(session, name);
            if (currentProperty != null) {
                validatePropertyDescription(currentProperty, STProperty.STPropertyDescription.SERIALIZED_LIST,List.class,parameterizedType,null);
            } else {
                currentProperty = new STPropertyImpl(parent, name, STProperty.STPropertyDescription.SERIALIZED_LIST, List.class, parameterizedType);
            }
            currentProperty.setValue(session, value);
            parent.propertiesByName.put(name, currentProperty);
            return currentProperty;
        }

        public <V extends Serializable> STProperty setSerializedSetProperty(STStorageSession session, String name, Class<? super V> parameterizedType, Set<V> value) {
            STProperty currentProperty = parent.getProperty(session, name);
            if (currentProperty != null) {
                validatePropertyDescription(currentProperty, STProperty.STPropertyDescription.SERIALIZED_SET,Set.class,parameterizedType,null);
            } else {
                currentProperty = new STPropertyImpl(parent, name, STProperty.STPropertyDescription.SERIALIZED_SET, Set.class, parameterizedType);
            }
            currentProperty.setValue(session, value);
            parent.propertiesByName.put(name, currentProperty);
            return currentProperty;
        }

        public <K extends Serializable, V extends Serializable> STProperty setSerializedMapProperty(STStorageSession session, String name, Class<? super K> keyType, Class<? super V> valueType, Map<K, V> value) {
            STProperty currentProperty = parent.getProperty(session, name);
            if (currentProperty != null) {
                validatePropertyDescription(currentProperty, STProperty.STPropertyDescription.SERIALIZED_MAP,Map.class,keyType,valueType);
            } else {
                currentProperty = new STPropertyImpl(parent, name, STProperty.STPropertyDescription.SERIALIZED_MAP, Map.class, keyType, valueType);
            }
            currentProperty.setValue(session, value);
            parent.propertiesByName.put(name, currentProperty);
            return currentProperty;
        }

        public <S extends Serializable> STProperty setSerializedPojoProperty(STStorageSession session, String name, Class<? super S> propertyType, S value) {
            STProperty currentProperty = parent.getProperty(session, name);
            if (currentProperty != null) {
                validatePropertyDescription(currentProperty, STProperty.STPropertyDescription.SERIALIZED_POJO,propertyType,null,null);
            } else {
                currentProperty = new STPropertyImpl(parent, name, STProperty.STPropertyDescription.SERIALIZED_POJO, propertyType);
            }
            currentProperty.setValue(session, value);
            parent.propertiesByName.put(name, currentProperty);
            return currentProperty;
        }

        public STProperty setInputStreamProperty(STStorageSession session, String name, InputStream value) {
            STProperty currentProperty = parent.getProperty(session, name);
            if (currentProperty != null) {
                validatePropertyDescription(currentProperty, STProperty.STPropertyDescription.INPUT_STREAM, InputStream.class,null,null);
            } else {
                currentProperty = new STPropertyImpl(parent, name, STProperty.STPropertyDescription.INPUT_STREAM, InputStream.class);
            }
            InputStream newValue;
            if (value == null) {
                newValue = null;
            } else {
                ByteArrayOutputStream input = new ByteArrayOutputStream();
                try {
                    if (value.markSupported()) {
                        value.reset();
                    }
                    IOUtils.copy(value, input);
                } catch (Exception e) {
                    throw new IllegalStateException(e.getMessage(), e);
                }
                newValue = new ByteArrayInputStream(input.toByteArray());
            }
            currentProperty.setValue(session, newValue);
            parent.propertiesByName.put(name, currentProperty);
            return currentProperty;
        }

        private void validatePropertyDescription(STProperty currentProperty, STProperty.STPropertyDescription newDescription, Class<?> type, Class<?> firstParameterizedType, Class<?> secondParameterizedType ) {
            if (verifyBefore) {
                if (!currentProperty.getInternalMethods().getDescription().equals(newDescription)) {
                    throw new IllegalArgumentException(
                            format("wrong description of property {2}: should be {0} instead of {1}",
                                    currentProperty.getInternalMethods().getDescription(), newDescription,currentProperty.getPropertyName()));
                }
                if (!currentProperty.getInternalMethods().getPropertyType().equals(type)) {
                    throw new IllegalArgumentException(
                            format("wrong type of property {2}: should be {0} instead of {1}",
                                    currentProperty.getInternalMethods().getPropertyType(),type));

                }
                if (!eachEquality(currentProperty.getInternalMethods().getFirstParameterizedType(), firstParameterizedType)) {
                    throw new IllegalArgumentException(
                            format("wrong first parameterized type of property {2}: should be {0} instead of {1}",
                                    currentProperty.getInternalMethods().getFirstParameterizedType(), firstParameterizedType, currentProperty.getPropertyName()));

                }
                if (!eachEquality(currentProperty.getInternalMethods().getSecondParameterizedType(), secondParameterizedType)) {
                    throw new IllegalArgumentException(
                            format("wrong second parameterized type of property {2}: should be {0} instead of {1}",
                                    currentProperty.getInternalMethods().getSecondParameterizedType(), secondParameterizedType, currentProperty.getPropertyName()));

                }
            }
        }
    }
}
