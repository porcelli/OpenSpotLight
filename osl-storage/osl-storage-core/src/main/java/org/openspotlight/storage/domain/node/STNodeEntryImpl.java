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

package org.openspotlight.storage.domain.node;

import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.key.STLocalKey;
import org.openspotlight.storage.domain.key.STUniqueKey;
import org.openspotlight.storage.domain.property.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 4:48:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class STNodeEntryImpl implements STNodeEntry {

    public STNodeEntryImpl(final String nodeEntryName, final STLocalKey localKey, final STUniqueKey uniqueKey) {

        this.nodeEntryName = nodeEntryName;
        this.localKey = localKey;
        this.uniqueKey = uniqueKey
                ;
    }

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

    public STNodeEntryBuilder createWithName(final STStorageSession session, final String name) {
        return session.getInternalMethods().nodeEntryCreateWithName(this, name);

    }

    public List<STListProperty> getListProperties(STStorageSession session) {
        return session.getInternalMethods().nodeEntryGetListProperties(this);

    }

    public <T> STListProperty setListProperty(STStorageSession session, Class<T> valueType, String name, List<T> value) {
        return session.getInternalMethods().nodeEntrySetListProperty(this, valueType, name, value);

    }

    public <T> STListProperty getListProperty(STStorageSession session, Class<T> valueType, String name) {
        return session.getInternalMethods().nodeEntryGetListProperty(this, valueType, name);

    }

    public List<STSimpleProperty> getSimpleProperties(STStorageSession session) {
        return session.getInternalMethods().nodeEntryGetSimpleProperties(this);

    }

    public <T> STSimpleProperty setSimpleProperty(STStorageSession session, Class<T> type, String name, T value) {
        return session.getInternalMethods().nodeEntrySetSimpleProperty(this, type, name, value);

    }

    public <T> STSimpleProperty getSimpleProperty(STStorageSession session, Class<T> type, String name) {
        return session.getInternalMethods().nodeEntryGetSimpleProperty(this, type, name);

    }

    public List<STStreamProperty> getStreamProperties(STStorageSession session) {
        return session.getInternalMethods().nodeEntryGetStreamProperties(this);

    }

    public <T> STStreamProperty setStreamProperty(STStorageSession session, String name, T value) {
        return session.getInternalMethods().nodeEntrySetStreamProperty(this, name, value);

    }

    public <T> STStreamProperty getStreamProperty(STStorageSession session, String name) {
        return session.getInternalMethods().nodeEntryGetStreamProperty(this, name);

    }

    public List<STMapProperty> getMapProperties(STStorageSession session) {
        return session.getInternalMethods().nodeEntryGetMapProperties(this);

    }

    public <K, V> STMapProperty setMapProperty(STStorageSession session, Class<K> keyType, Class<V> valueType, String name, Map<K, V> value) {
        return session.getInternalMethods().nodeEntrySetMapProperty(this, keyType, valueType, name, value);

    }

    public <K, V> STMapProperty getMapProperty(STStorageSession session, Class<K> keyType, Class<V> valueType, String name) {
        return session.getInternalMethods().nodeEntryGetMapProperty(this, keyType, valueType, name);

    }

    public List<STSerializableListProperty> getSerializableListProperties(STStorageSession session) {
        return session.getInternalMethods().nodeEntryGetSerializableListProperties(this);

    }

    public <T> STSerializableListProperty setSerializableListProperty(STStorageSession session, Class<T> valueType, String name, List<T> value) {
        return session.getInternalMethods().nodeEntrySetSerializableListProperty(this, valueType, name, value);

    }

    public <T> STSerializableListProperty getSerializableListProperty(STStorageSession session, Class<T> valueType, String name) {
        return session.getInternalMethods().nodeEntryGetSerializableListProperty(this, valueType, name);

    }

    public List<STSerializableMapProperty> getSerializableMapProperties(STStorageSession session) {
        return session.getInternalMethods().nodeEntryGetSerializableMapProperties(this);

    }

    public <K, V> STSerializableMapProperty setSerializableMapProperty(STStorageSession session, Class<K> keyType, Class<V> valueType, String name, Map<K, V> value) {
        return session.getInternalMethods().nodeEntrySetSerializableMapProperty(this, keyType, valueType, name, value);

    }

    public <K, V> STSerializableMapProperty getSerializableMapProperty(STStorageSession session, Class<K> keyType, Class<V> valueType, String name) {
        return session.getInternalMethods().nodeEntryGetSerializableMapProperty(this, keyType, valueType, name);

    }

    public List<STSerializableSetProperty> getSerializableSetProperties(STStorageSession session) {
        return session.getInternalMethods().nodeEntryGetSerializableSetProperties(this);

    }

    public <T> STSerializableSetProperty setSerializableSetProperty(STStorageSession session, Class<T> valueType, String name, Set<T> value) {
        return session.getInternalMethods().nodeEntrySetSerializableSetProperty(this, valueType, name, value);

    }

    public <T> STSerializableSetProperty getSerializableSetProperty(STStorageSession session, Class<T> valueType, String name) {
        return session.getInternalMethods().nodeEntryGetSerializableSetProperty(this, valueType, name);

    }

    public List<STPojoProperty> getPojoProperties(STStorageSession session) {
        return session.getInternalMethods().nodeEntryGetPojoProperties(this);
    }

    public <T> STPojoProperty setPojoProperty(STStorageSession session, Class<T> type, String name, T value) {
        return session.getInternalMethods().nodeEntrySetPojoProperty(this, type, name, value);
    }

    public <T> STPojoProperty getPojoProperty(STStorageSession session, Class<T> type, String name) {
        return session.getInternalMethods().nodeEntryGetPojoProperty(this, type, name);
    }

    public List<STSetProperty> getSetProperties(STStorageSession session) {
        return session.getInternalMethods().nodeEntryGetSetProperties(this);
    }

    public <T> STSetProperty setSetProperty(STStorageSession session, Class<T> valueType, String name, Set<T> value) {
        return session.getInternalMethods().nodeEntrySetSetProperty(this, valueType, name, value);

    }

    public <T> STSetProperty getSetProperty(STStorageSession session, Class<T> valueType, String name) {
        return session.getInternalMethods().nodeEntryGetSetProperty(this, valueType, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        STNodeEntryImpl that = (STNodeEntryImpl) o;

        if (localKey != null ? !localKey.equals(that.localKey) : that.localKey != null) return false;
        if (nodeEntryName != null ? !nodeEntryName.equals(that.nodeEntryName) : that.nodeEntryName != null)
            return false;
        if (uniqueKey != null ? !uniqueKey.equals(that.uniqueKey) : that.uniqueKey != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = nodeEntryName != null ? nodeEntryName.hashCode() : 0;
        result = 31 * result + (localKey != null ? localKey.hashCode() : 0);
        result = 31 * result + (uniqueKey != null ? uniqueKey.hashCode() : 0);
        return result;
    }
}
