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
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.key.STLocalKey;
import org.openspotlight.storage.domain.key.STUniqueKey;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.text.MessageFormat.format;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 4:48:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class STNodeEntryImpl implements STNodeEntry {

    public STNodeEntryImpl(STUniqueKey uniqueKey, Set<STProperty> properties) {

        this.nodeEntryName = uniqueKey.getLocalKey().getNodeEntryName();
        this.localKey = uniqueKey.getLocalKey();
        this.uniqueKey = uniqueKey;
        for (STProperty property : properties) {
            this.propertiesByName.put(property.getPropertyName(), property);
        }
    }

    public STNodeEntryImpl(STUniqueKey uniqueKey) {

        this.nodeEntryName = uniqueKey.getLocalKey().getNodeEntryName();
        this.localKey = uniqueKey.getLocalKey();
        this.uniqueKey = uniqueKey;
    }

    private final Map<String, STProperty> propertiesByName = new HashMap<String, STProperty>();

    private final STPropertyOperation verifiedOperations = new STPropertyOperationImpl(true, this);

    private final STPropertyOperation unverifiedOperations = new STPropertyOperationImpl(false, this);

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
        return propertiesByName.get(name);
    }

    public Set<String> getPropertyNames() {
        return ImmutableSet.copyOf(propertiesByName.keySet());
    }

    public Set<STProperty> getProperties(STStorageSession session) {
        if (propertiesByName.isEmpty()) {
            Set<STProperty> result = session.getInternalMethods().nodeEntryLoadProperties(session, this);
            for (STProperty property : result) {
                propertiesByName.put(property.getPropertyName(), property);
            }
        }
        return ImmutableSet.copyOf(propertiesByName.values());
    }

    public STNodeEntryBuilder createWithName(final STStorageSession session, final String name) {
        return session.getInternalMethods().nodeEntryCreateWithName(this, name);

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
        if (nodeEntryName != null ? !nodeEntryName.equals(that.nodeEntryName) : that.nodeEntryName != null) return false;
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

        private final STNodeEntry parent;

        public STPropertyOperationImpl(boolean verifyBefore, STNodeEntry parent) {
            this.verifyBefore = verifyBefore;
            this.parent = parent;
        }

        public <T extends Serializable, V extends T> STProperty setSimpleProperty(STStorageSession session, String name, Class<T> propertyType, V value) {
            STProperty currentProperty = parent.getProperty(session, name);
            if (currentProperty != null) {
                validatePropertyDescription(currentProperty, STProperty.STPropertyDescription.SIMPLE);
            } else {
                currentProperty = new STPropertyImpl(parent, name, STProperty.STPropertyDescription.SIMPLE, propertyType, false);
            }
            currentProperty.setValue(session, value);

            return currentProperty;
        }

        private void validatePropertyDescription(STProperty currentProperty, STProperty.STPropertyDescription toValidate) {
            if (verifyBefore) {
                if (!currentProperty.getDescription().equals(toValidate)) {
                    throw new IllegalArgumentException(
                            format("wrong type of property: should be {0} instead of {1}",
                                    currentProperty.getDescription(), toValidate));
                }
            }
        }

        public <V extends Serializable> STProperty setListProperty(STStorageSession session, String name, Class<V> parameterizedType, List<V> value) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <V extends Serializable> STProperty setSetProperty(STStorageSession session, String name, Class<V> parameterizedType, Set<V> value) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <K extends Serializable, V extends Serializable> STProperty setMapProperty(STStorageSession session, String name, Class<K> keyType, Class<V> valueType, Map<K, V> value) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <V extends Serializable> STProperty setSerializedListProperty(STStorageSession session, String name, Class<V> parameterizedType, List<V> value) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <V extends Serializable> STProperty setSerializedSetProperty(STStorageSession session, String name, Class<V> parameterizedType, Set<V> value) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <K extends Serializable, V extends Serializable> STProperty setSerializedMapProperty(STStorageSession session, String name, Class<K> keyType, Class<V> valueType, Map<K, V> value) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <S extends Serializable> STProperty setSerializedPojoProperty(STStorageSession session, String name, Class<S> propertyType, S value) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public STProperty setInputStreamProperty(STStorageSession session, String name, InputStream value) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
