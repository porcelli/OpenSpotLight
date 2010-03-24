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

package org.openspotlight.storage;

import org.openspotlight.storage.domain.key.STLocalKey;
import org.openspotlight.storage.domain.key.STUniqueKey;
import org.openspotlight.storage.domain.node.STNodeEntry;
import org.openspotlight.storage.domain.node.STNodeEntryFactory;
import org.openspotlight.storage.domain.property.*;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * This class is an abstraction of a current state of storage session. The implementation classes must not store
 * any kind of connection state. This implementation must not be shared between threads.
 */
public interface STStorageSession extends STNodeEntryFactory {
    List<STNodeEntry> findByCriteria(STCriteria criteria);
    STNodeEntry findUniqueByCriteria(STCriteria criteria);

    public interface STPartition {
        String getPartitionName();
    }

    public interface STCriteriaBuilder {
        STCriteriaBuilder withProperty(String propertyName);

        STCriteriaBuilder withNodeEntry(String nodeName);

        <T extends Serializable> STCriteriaBuilder equals(Class<T> type, T value);

        <T extends Serializable> STCriteriaBuilder notEquals(Class<T> type, T value);

        STCriteriaBuilder and();

        STCriteria buildCriteria();

        STCriteriaBuilder withLocalKey(STLocalKey localKey);

        STCriteriaBuilder withUniqueKey(STUniqueKey uniqueKey);
    }


    public interface STCriteria {

        public interface STCriteriaItem<T extends Serializable> {
            public T getValue();

            public Class<T> getType();

            public String getPropertyName();

            public boolean isNot();
        }

        public String getNodeName();

        public List<STCriteriaItem<?>> getCriteriaItems();

        public List<STNodeEntry> andFind(STStorageSession session);

        public STNodeEntry andFindUnique(STStorageSession session);


    }

    public static enum STFlushMode {
        AUTO, EXPLICIT
    }

    public STFlushMode getFlushMode();

    public STPartition getCurrentPartition();

    public STStorageSession withPartition(STPartition partition);

    public STCriteriaBuilder createCriteria();

    STNodeEntryBuilder createWithName(String name);

    STStorageSessionInternalMethods getInternalMethods();

    interface STStorageSessionInternalMethods {

        <T> STSetProperty nodeEntryGetSetProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name);

        <T> void nodeEntrySetSetProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name, Set<T> value);

        List<STSetProperty> nodeEntryGetSetProperties(STNodeEntry stNodeEntry);

        STNodeEntryBuilder nodeEntryCreateWithName(STNodeEntry stNodeEntry, String name);

        List<STListProperty> nodeEntryGetListProperties(STNodeEntry stNodeEntry);

        <T> void nodeEntrySetListProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name, List<T> value);

        <T> STListProperty nodeEntryGetListProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name);

        List<STSimpleProperty> nodeEntryGetSimpleProperties(STNodeEntry stNodeEntry);

        <T> void nodeEntrySetSimpleProperty(STNodeEntry stNodeEntry, Class<T> type, String name, T value);

        <T> STSimpleProperty nodeEntryGetSimpleProperty(STNodeEntry stNodeEntry, Class<T> type, String name);

        List<STStreamProperty> nodeEntryGetStreamProperties(STNodeEntry stNodeEntry);

        <T> void nodeEntrySetStreamProperty(STNodeEntry stNodeEntry, String name, T value);

        STStreamProperty nodeEntryGetStreamProperty(STNodeEntry stNodeEntry, String name);

        List<STMapProperty> nodeEntryGetMapProperties(STNodeEntry stNodeEntry);

        <K, V> void nodeEntrySetMapProperty(STNodeEntry stNodeEntry, Class<K> keyType, Class<V> valueType, String name, Map<K, V> value);

        <K, V> STMapProperty nodeEntryGetMapProperty(STNodeEntry stNodeEntry, Class<K> keyType, Class<V> valueType, String name);

        List<STSerializableListProperty> nodeEntryGetSerializableListProperties(STNodeEntry stNodeEntry);

        <T> void nodeEntrySetSerializableListProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name, List<T> value);

        <T> STSerializableListProperty nodeEntryGetSerializableListProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name);

        List<STSerializableMapProperty> nodeEntryGetSerializableMapProperties(STNodeEntry stNodeEntry);

        <K, V> void nodeEntrySetSerializableMapProperty(STNodeEntry stNodeEntry, Class<K> keyType, Class<V> valueType, String name, Map<K, V> value);

        <K, V> STSerializableMapProperty nodeEntryGetSerializableMapProperty(STNodeEntry stNodeEntry, Class<K> keyType, Class<V> valueType, String name);

        List<STSerializableSetProperty> nodeEntryGetSerializableSetProperties(STNodeEntry stNodeEntry);

        <T> void nodeEntrySetSerializableSetProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name, Set<T> value);

        <T> STSerializableSetProperty nodeEntryGetSerializableSetProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name);

        List<STPojoProperty> nodeEntryGetPojoProperties(STNodeEntry stNodeEntry);

        <T> void nodeEntrySetPojoProperty(STNodeEntry stNodeEntry, Class<T> type, String name, T value);

        <T> STPojoProperty nodeEntryGetPojoProperty(STNodeEntry stNodeEntry, Class<T> type, String name);

        <T> List<T> listPropertyGetItems(org.openspotlight.storage.domain.property.STListProperty stListProperty);

        <T> Set<T> setPropertyGetItems(org.openspotlight.storage.domain.property.STSetProperty stSetProperty);

        <K, T> Map<K, T> mapPropertyGetMap(org.openspotlight.storage.domain.property.STMapProperty stMapProperty);

        <T> List<T> serializableListPropertyGetItems(org.openspotlight.storage.domain.property.STSerializableListProperty stSerializableListProperty);

        InputStream streamPropertyGetValue(org.openspotlight.storage.domain.property.STStreamProperty stStreamProperty);

        <T extends Serializable> T pojoPropertyGetValue(org.openspotlight.storage.domain.property.STPojoProperty stPojoProperty);

        <T> Set<T> serializableSetPropertyGetItems(org.openspotlight.storage.domain.property.STSerializableSetProperty stSerializableSetProperty);

        <K, T> Map<K, T> serializableMapGetMap(org.openspotlight.storage.domain.property.STSerializableMapProperty stSerializableMapProperty);

        <T extends Serializable> T simplePropertyGetValue(org.openspotlight.storage.domain.property.STSimpleProperty stSimpleProperty);
    }


    void flushTransient();
}
