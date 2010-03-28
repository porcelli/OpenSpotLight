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

package org.openspotlight.storage.redis;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;
import com.google.inject.internal.ImmutableSet;
import com.google.inject.internal.Sets;
import org.jredis.JRedis;

import static com.google.common.collect.Sets.newHashSet;
import static org.openspotlight.common.util.Conversion.convert;

import org.jredis.RedisException;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.storage.AbstractSTStorageSession;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.key.STKeyEntry;
import org.openspotlight.storage.domain.key.STLocalKey;
import org.openspotlight.storage.domain.key.STUniqueKey;
import org.openspotlight.storage.domain.node.STNodeEntry;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.lang.Class.forName;
import static java.text.MessageFormat.format;
import static org.jredis.ri.alphazero.support.DefaultCodec.toStr;

/**
 * Created by User: feu - Date: Mar 23, 2010 - Time: 4:46:25 PM
 */
public class JRedisSTStorageSessionImpl extends AbstractSTStorageSession {

    private final String SET_WITH_ALL_KEYS = "all-unique-keys";
    private final String SET_WITH_ALL_LOCAL_KEYS = "local-keys:{0}:unique-keys";
    private final String SET_WITH_NODE_KEYS_NAMES = "node-unique-key:{0}:key-names";
    private final String SET_WITH_NODE_PROPERTY_NAMES = "node-unique-key:{0}:property-names";
    private final String SET_WITH_PROPERTY_NODE_IDS = "property-name:{0}:property-type:{1}:property-value:{2}:node-unique-keys";
    private final String KEY_WITH_PROPERTY_VALUE = "node-unique-key:{0}:property-name:{1}:value";
    private final String KEY_WITH_PROPERTY_TYPE = "node-unique-key:{0}:property-name:{1}:type";
    private final String KEY_WITH_PARENT_UNIQUE_ID = "node-unique-key:{0}:parent-unique-id";
    private final String KEY_WITH_NODE_ENTRY_NAME = "node-unique-key:{0}:node-entry-name";

    private final JRedis jRedis;


    public JRedisSTStorageSessionImpl(JRedis jredis, STFlushMode flushMode, STPartition partition) {
        super(flushMode, partition);
        this.jRedis = jredis;
    }

    @Override
    protected Set<STNodeEntry> internalFindByCriteria(STCriteria criteria) throws Exception {
        List<String> inter = Lists.newLinkedList();
        for (STCriteriaItem c : criteria.getCriteriaItems()) {
            if (c instanceof STPropertyCriteriaItem) {
                STPropertyCriteriaItem p = (STPropertyCriteriaItem) c;
                inter.add(format(SET_WITH_PROPERTY_NODE_IDS, p.getPropertyName(), p.getType().getName(), p.getValue()));
            }
        }
        List<String> ids;
        if (inter.size() > 1) {
            ids = listBytesToListString(jRedis.sinter(inter.get(0), inter.subList(1, inter.size()).toArray(new String[0])));
        } else if (inter.size() == 1) {
            ids = listBytesToListString(jRedis.sinter(inter.get(0)));

        } else {
            ids = Collections.emptyList();
        }
        Set<STNodeEntry> nodeEntries = newHashSet();
        for (String id : ids) {
            String parentKey = id;
            STNodeEntry nodeEntry = loadNode(parentKey);
            nodeEntries.add(nodeEntry);
        }

        return ImmutableSortedSet.copyOf(nodeEntries);
    }

    private STNodeEntry loadNode(String parentKey) throws Exception {
        STUniqueKeyBuilder keyBuilder = null;
        do {
            List<String> keyPropertyNames = listBytesToListString(jRedis.smembers(format(SET_WITH_NODE_KEYS_NAMES, parentKey)));
            String nodeEntryName = toStr(jRedis.get(format(KEY_WITH_NODE_ENTRY_NAME, parentKey)));
            keyBuilder = keyBuilder == null ? createKey(nodeEntryName) : keyBuilder.withParent(nodeEntryName);

            for (String keyName : keyPropertyNames) {
                String typeName = toStr(jRedis.get(format(KEY_WITH_PROPERTY_TYPE, parentKey, keyName)));
                String typeValueAsString = toStr(jRedis.get(format(KEY_WITH_PROPERTY_VALUE, parentKey, keyName)));
                Class<? extends Serializable> type = (Class<? extends Serializable>) forName(typeName);
                Serializable value = convert(typeValueAsString, type);
                keyBuilder.withEntry(keyName, type, value);
            }
            parentKey = toStr(jRedis.get(format(KEY_WITH_PARENT_UNIQUE_ID, parentKey)));

        } while (parentKey != null);
        STUniqueKey uniqueKey = keyBuilder.andCreate();
        STNodeEntry nodeEntry = createEntryWithKey(uniqueKey);
        return nodeEntry;
    }

    private List<String> listBytesToListString(List<byte[]> ids) {
        List<String> idsAsString = Lists.newLinkedList();
        if (ids != null) {
            for (byte[] b : ids) {
                String s = toStr(b);
                idsAsString.add(s);
            }
        }
        return idsAsString;
    }

    @Override
    protected STStorageSession createNewInstance(STFlushMode flushMode, STPartition partition) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void flushNewItem(STNodeEntry entry) throws Exception {

        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(entry.getUniqueKey());
        jRedis.sadd(SET_WITH_ALL_KEYS, uniqueKey);
        jRedis.set(format(KEY_WITH_NODE_ENTRY_NAME, uniqueKey), entry.getNodeEntryName());
        STUniqueKey parentKey = entry.getUniqueKey().getParentKey();
        if (parentKey != null) {
            jRedis.set(format(KEY_WITH_PARENT_UNIQUE_ID, uniqueKey), supportMethods.getUniqueKeyAsStringHash(parentKey));
        }
        String localKey = supportMethods.getLocalKeyAsStringHash(entry.getLocalKey());
        jRedis.sadd(format(SET_WITH_ALL_LOCAL_KEYS, localKey), uniqueKey);

        for (STKeyEntry<?> k : entry.getLocalKey().getEntries()) {
            jRedis.sadd(format(SET_WITH_NODE_KEYS_NAMES, uniqueKey), k.getPropertyName());
            jRedis.sadd(format(SET_WITH_NODE_PROPERTY_NAMES, uniqueKey), k.getPropertyName());
            jRedis.sadd(format(SET_WITH_PROPERTY_NODE_IDS, k.getPropertyName(), k.getType().getName(),
                    k.getValue()), uniqueKey);
            jRedis.set(format(KEY_WITH_PROPERTY_TYPE, uniqueKey, k.getPropertyName()), k.getType().getName());
            jRedis.set(format(KEY_WITH_PROPERTY_VALUE, uniqueKey, k.getPropertyName()), convert(k.getValue(),String.class));
        }


    }

    @Override
    protected void flushRemovedItem(STNodeEntry entry) throws Exception {
        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(entry.getUniqueKey());
        jRedis.srem(SET_WITH_ALL_KEYS, uniqueKey);

        String localKey = supportMethods.getLocalKeyAsStringHash(entry.getLocalKey());
        jRedis.srem(format(SET_WITH_ALL_LOCAL_KEYS, localKey), uniqueKey);

        for (STKeyEntry<?> k : entry.getLocalKey().getEntries()) {
            jRedis.del(format(SET_WITH_NODE_KEYS_NAMES, uniqueKey));
            jRedis.del(format(SET_WITH_NODE_PROPERTY_NAMES, uniqueKey));
            jRedis.srem(format(SET_WITH_PROPERTY_NODE_IDS, k.getPropertyName(), k.getType().getName(),
                    k.getValue()), uniqueKey);
            jRedis.del(format(KEY_WITH_PROPERTY_TYPE, uniqueKey, k.getPropertyName()));
            jRedis.del(format(KEY_WITH_PROPERTY_VALUE, uniqueKey, k.getPropertyName()));
        }
    }
}
