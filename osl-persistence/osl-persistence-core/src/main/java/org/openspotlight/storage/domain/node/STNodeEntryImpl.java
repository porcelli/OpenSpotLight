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
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.key.STLocalKey;
import org.openspotlight.storage.domain.key.STUniqueKey;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import static com.google.common.collect.Maps.newHashMap;


public class STNodeEntryImpl implements STNodeEntry {

    private static final long TIMEOUT = 60 * 1000;

    private long lastLoad;

    public STNodeEntryImpl(STUniqueKey uniqueKey, Set<STProperty> properties, boolean resetTimeout) {
        this.lastLoad = resetTimeout ? -1 : System.currentTimeMillis();
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
        namedChildrenWeakReference = new WeakHashMap<Set<STNodeEntry>, String>();
    }


    public STNodeEntryImpl(STUniqueKey uniqueKey, boolean resetTimeout) {
        this(uniqueKey, null, resetTimeout);
    }

    private final STPartition partition;

    private WeakReference<STNodeEntry> parentWeakReference = null;

    private WeakReference<Set<STNodeEntry>> childrenWeakReference = null;

    private WeakHashMap<Set<STNodeEntry>, String> namedChildrenWeakReference;

    private final Map<String, STProperty> propertiesByName;

    private final String nodeEntryName;

    private final STLocalKey localKey;

    private final STUniqueKey uniqueKey;

    public void forceReload() {
        this.propertiesByName.clear();
        this.lastLoad = -1;
    }

    public boolean isChildOf(STNodeEntry possibleParent) {
        STUniqueKey parentKey = this.getUniqueKey();
        STUniqueKey possibleParentKey = possibleParent.getUniqueKey();
        while (parentKey != null) {
            if (parentKey.equals(possibleParentKey)) return true;
            parentKey = parentKey.getParentKey();
        }
        return false;
    }

    public String getNodeEntryName() {
        return nodeEntryName;
    }

    public STLocalKey getLocalKey() {
        return localKey;
    }

    public STUniqueKey getUniqueKey() {
        return uniqueKey;
    }

    public String getPropertyAsString(STStorageSession session, String name) {
        STProperty prop = getProperty(session, name);
        if (prop != null) {
            return prop.getValueAsString(session);
        }
        return null;
    }

    public InputStream getPropertyAsStream(STStorageSession session, String name) {

        STProperty prop = getProperty(session, name);
        if (prop != null) {
            return prop.getValueAsStream(session);
        }
        return null;
    }

    public byte[] getPropertyAsBytes(STStorageSession session, String name) {

        STProperty prop = getProperty(session, name);
        if (prop != null) {
            return prop.getValueAsBytes(session);
        }
        return null;
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

    @Override
    public STProperty setSimpleProperty(STStorageSession session, String name, String value) {
        if (localKey.getEntryNames().contains(name)) throw new IllegalStateException();
        STProperty currentProperty = getProperty(session, name);
        if (currentProperty == null) {
            currentProperty = STPropertyImpl.createSimple(name, this);
            propertiesByName.put(name, currentProperty);
        }
        currentProperty.setStringValue(session, value);

        return currentProperty;
    }

    @Override
    public STProperty setSimpleProperty(STStorageSession session, String name, InputStream value) {
        if (localKey.getEntryNames().contains(name)) throw new IllegalStateException();
        STProperty currentProperty = getProperty(session, name);
        if (currentProperty == null) {
            currentProperty = STPropertyImpl.createSimple(name, this);
            propertiesByName.put(name, currentProperty);
        }
        currentProperty.setStreamValue(session, value);

        return currentProperty;
    }

    @Override
    public STProperty setSimpleProperty(STStorageSession session, String name, byte[] value) {
        if (localKey.getEntryNames().contains(name)) throw new IllegalStateException();
        STProperty currentProperty = getProperty(session, name);
        if (currentProperty == null) {
            currentProperty = STPropertyImpl.createSimple(name, this);
            propertiesByName.put(name, currentProperty);
        }
        currentProperty.setBytesValue(session, value);

        return currentProperty;

    }

    @Override
    public STProperty setIndexedProperty(STStorageSession session, String name, String value) {
        if (localKey.getEntryNames().contains(name)) throw new IllegalStateException();
        STProperty currentProperty = getProperty(session, name);
        if (currentProperty == null) {
            currentProperty = STPropertyImpl.createIndexed(name, this);
            propertiesByName.put(name, currentProperty);
        }
        currentProperty.setStringValue(session, value);

        return currentProperty;
    }

    private void reloadProperties(STStorageSession session) {
        boolean tooOld = this.lastLoad < (System.currentTimeMillis() + TIMEOUT);
        boolean empty = propertiesByName.isEmpty();
        if (tooOld && empty) {
            Set<STProperty> result = session.withPartition(partition).getInternalMethods().nodeEntryLoadProperties(this);
            for (STProperty property : result) {
                propertiesByName.put(property.getPropertyName(), property);
            }
            this.lastLoad = System.currentTimeMillis();
        }
    }

    private void loadPropertiesOnce(STStorageSession session) {
        if (propertiesByName.isEmpty()) {
            reloadProperties(session);
        }
    }

    public STNodeEntryBuilder createWithName(final STStorageSession session, final String name, boolean rootKey) {
        return session.withPartition(partition).getInternalMethods().nodeEntryCreateWithName(this, name, rootKey);

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

    @Override
    public String toString() {
        return "STNodeEntryImpl{" +
                "partition=" + partition +
                ", nodeEntryName='" + nodeEntryName + '\'' +
                ", uniqueKey=" + uniqueKey +
                '}';
    }
}
