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

package org.openspotlight.storage.domain;

import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.node.STANodeEntryFactory;
import org.openspotlight.storage.domain.node.STNodeEntry;
import org.openspotlight.storage.domain.property.*;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by User: feu - Date: Mar 22, 2010 - Time: 2:19:49 PM
 */
public class AbstractSTStorageSession implements STStorageSession{

    private AtomicReference<WeakReference<>>

    private final class STStorageSessionInternalMethodsImpl implements STStorageSessionInternalMethods{

        public <T> STSetProperty nodeEntryGetSetProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T> STSetProperty nodeEntrySetSetProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name, Set<T> value) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public List<STSetProperty> nodeEntryGetSetProperties(STNodeEntry stNodeEntry) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public STNodeEntryBuilder nodeEntryCreateWithName(STNodeEntry stNodeEntry, String name) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public List<STListProperty> nodeEntryGetListProperties(STNodeEntry stNodeEntry) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T> STListProperty nodeEntrySetListProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name, List<T> value) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T> STListProperty nodeEntryGetListProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public List<STSimpleProperty> nodeEntryGetSimpleProperties(STNodeEntry stNodeEntry) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T> STSimpleProperty nodeEntrySetSimpleProperty(STNodeEntry stNodeEntry, Class<T> type, String name, T value) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T> STSimpleProperty nodeEntryGetSimpleProperty(STNodeEntry stNodeEntry, Class<T> type, String name) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public List<STStreamProperty> nodeEntryGetStreamProperties(STNodeEntry stNodeEntry) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T> STStreamProperty nodeEntrySetStreamProperty(STNodeEntry stNodeEntry, String name, T value) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public STStreamProperty nodeEntryGetStreamProperty(STNodeEntry stNodeEntry, String name) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public List<STMapProperty> nodeEntryGetMapProperties(STNodeEntry stNodeEntry) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <K, V> STMapProperty nodeEntrySetMapProperty(STNodeEntry stNodeEntry, Class<K> keyType, Class<V> valueType, String name, Map<K, V> value) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <K, V> STMapProperty nodeEntryGetMapProperty(STNodeEntry stNodeEntry, Class<K> keyType, Class<V> valueType, String name) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public List<STSerializableListProperty> nodeEntryGetSerializableListProperties(STNodeEntry stNodeEntry) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T> STSerializableListProperty nodeEntrySetSerializableListProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name, List<T> value) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T> STSerializableListProperty nodeEntryGetSerializableListProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public List<STSerializableMapProperty> nodeEntryGetSerializableMapProperties(STNodeEntry stNodeEntry) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <K, V> STSerializableMapProperty nodeEntrySetSerializableMapProperty(STNodeEntry stNodeEntry, Class<K> keyType, Class<V> valueType, String name, Map<K, V> value) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <K, V> STSerializableMapProperty nodeEntryGetSerializableMapProperty(STNodeEntry stNodeEntry, Class<K> keyType, Class<V> valueType, String name) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public List<STSerializableSetProperty> nodeEntryGetSerializableSetProperties(STNodeEntry stNodeEntry) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T> STSerializableSetProperty nodeEntrySetSerializableSetProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name, Set<T> value) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T> STSerializableSetProperty nodeEntryGetSerializableSetProperty(STNodeEntry stNodeEntry, Class<T> valueType, String name) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public List<STPojoProperty> nodeEntryGetPojoProperties(STNodeEntry stNodeEntry) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public <T> STPojoProperty nodeEntrySetPojoProperty(STNodeEntry stNodeEntry, Class<T> type, String name, T value) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
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


    public <T> T get() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public STNodeEntryBuilder createWithName(String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public STStorageSessionInternalMethods getInternalMethods() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public STNodeEntryBuilder createWithName(STStorageSession session, String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
