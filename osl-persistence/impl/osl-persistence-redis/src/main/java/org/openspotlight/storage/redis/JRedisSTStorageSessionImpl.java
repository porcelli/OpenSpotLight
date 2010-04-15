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

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.jredis.JRedis;
import org.jredis.RedisException;
import org.openspotlight.storage.AbstractSTStorageSession;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.domain.key.STKeyEntry;
import org.openspotlight.storage.domain.key.STUniqueKey;
import org.openspotlight.storage.domain.node.STNodeEntry;
import org.openspotlight.storage.domain.node.STProperty;
import org.openspotlight.storage.domain.node.STPropertyImpl;
import org.openspotlight.storage.redis.guice.JRedisFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Class.forName;
import static java.text.MessageFormat.format;
import static org.jredis.ri.alphazero.support.DefaultCodec.toStr;
import static org.openspotlight.common.util.Conversion.convert;
import static org.openspotlight.common.util.Reflection.findClassWithoutPrimitives;

/**
 * Created by User: feu - Date: Mar 23, 2010 - Time: 4:46:25 PM
 */
public class JRedisSTStorageSessionImpl extends AbstractSTStorageSession {

    private static final String NULL_VALUE_AS_STRING="___N_U_L_L__V_A_L_U_E___";
    private static final String SET_WITH_ALL_KEYS_CONTAINING = "*{0}*";
    private static final String SET_WITH_ALL_KEYS = "all-unique-keys";
    private static final String SET_WITH_ALL_NODE_KEYS_FOR_NAME = "name:{0}:unique-keys";
    private static final String SET_WITH_ALL_LOCAL_KEYS = "local-keys:{0}:unique-keys";
    private static final String SET_WITH_NODE_KEYS_NAMES = "node-unique-key:{0}:key-names";
    private static final String SET_WITH_NODE_CHILDREN_KEYS = "node-unique-key:{0}:children-unique-keys";
    private static final String SET_WITH_NODE_CHILDREN_NAMED_KEYS = "node-unique-key:{0}:node-name:{1}:children-unique-keys";
    private static final String SET_WITH_NODE_PROPERTY_NAMES = "node-unique-key:{0}:property-names";
    private static final String KEY_WITH_PROPERTY_NODE_ID = "property-name:{0}:property-type:{1}:property-value:{2}:node-unique-keys:{3}";
    private static final String KEY_WITH_PROPERTY_VALUE = "node-unique-key:{0}:property-name:{1}:value";
    private static final String KEY_WITH_PROPERTY_DESCRIPTION = "node-unique-key:{0}:property-name:{1}:description";
    private static final String KEY_WITH_PROPERTY_PARAMETERIZED_1 = "node-unique-key:{0}:property-name:{1}:parameterized-1";
    private static final String KEY_WITH_PROPERTY_PARAMETERIZED_2 = "node-unique-key:{0}:property-name:{1}:parameterized-2";
    private static final String KEY_WITH_PROPERTY_TYPE = "node-unique-key:{0}:property-name:{1}:type";
    private static final String KEY_WITH_PARENT_UNIQUE_ID = "node-unique-key:{0}:parent-unique-id";
    private static final String KEY_WITH_NODE_ENTRY_NAME = "node-unique-key:{0}:node-entry-name";


    private final JRedisFactory factory;

    @Inject
    public JRedisSTStorageSessionImpl(STFlushMode flushMode, JRedisFactory factory) {
        super(flushMode);
        this.factory = factory;
    }

    private static String getPropertyKey(String propertyName, Class<?> type, Object value, String nodeId) throws Exception {
        String valueAsString = convert(value, String.class);
        if (valueAsString != null) {
            valueAsString = valueAsString.replaceAll(" ", "");
        }else{
             valueAsString = NULL_VALUE_AS_STRING;
        }
        String proposedKey = format(KEY_WITH_PROPERTY_NODE_ID, propertyName, type.getName(), valueAsString, nodeId);
        return proposedKey;


    }

    private static interface FindPropertyByTwoParameters {
        String find(String name, String value) throws Exception;
    }

    private final FindPropertyByTwoParameters propertyStartingWith = new FindPropertyByTwoParameters() {
        public String find(String name, String value) throws Exception {
            return format(KEY_WITH_PROPERTY_NODE_ID, name, String.class.getName(), value + "*", "*");
        }
    };

    private final FindPropertyByTwoParameters propertyEndsWith = new FindPropertyByTwoParameters() {
        public String find(String name, String value) throws Exception {
            return format(KEY_WITH_PROPERTY_NODE_ID, name, String.class.getName(), "*" + value, "*");
        }
    };

    private final FindPropertyByTwoParameters propertyContains = new FindPropertyByTwoParameters() {
        public String find(String name, String value) throws Exception {
            return format(KEY_WITH_PROPERTY_NODE_ID, name, String.class.getName(), "*" + value + "*", "*");
        }
    };

    private static String getPropertyOldKey(String propertyName, String nodeId) throws Exception {
        return format(KEY_WITH_PROPERTY_NODE_ID, propertyName, "*", "*", nodeId);
    }

    private static List<String> keysFrom(JRedis jRedis, String nodeEntryName, FindPropertyByTwoParameters function, String name, String value) throws Exception {
        String proposedKey = function.find(name, value);
        List<String> propertyKeys = jRedis.keys(proposedKey);
        List<String> keys = new ArrayList<String>(propertyKeys.size());
        loopingIntoProperties:
        for (String key : propertyKeys) {
            String foundKey = toStr(jRedis.get(key));
            if (nodeEntryName != null) {
                if (jRedis.sismember(format(SET_WITH_ALL_NODE_KEYS_FOR_NAME, nodeEntryName), foundKey)) {
                    keys.add(foundKey);
                }
            } else {
                List<String> foundKeys = jRedis.keys(format(SET_WITH_ALL_NODE_KEYS_FOR_NAME, "*"));
                for (String s : foundKeys) {
                    if (jRedis.sismember(s, foundKey)) {
                        keys.add(foundKey);
                        continue loopingIntoProperties;
                    }
                }
            }
        }
        return keys;
    }


    private static List<String> keysFromProperty(JRedis jRedis, String nodeEntryName, String propertyName, Class<?> type, Object value) throws Exception {
        String proposedKey = getPropertyKey(propertyName, type, value, "*");
        List<String> propertyKeys = jRedis.keys(proposedKey);
        List<String> keys = new ArrayList<String>(propertyKeys.size());
        loopingIntoProperties: for (String key : propertyKeys) {
            String foundKey = toStr(jRedis.get(key));
            if (nodeEntryName != null) {
                if (jRedis.sismember(format(SET_WITH_ALL_NODE_KEYS_FOR_NAME, nodeEntryName), foundKey)) {
                    keys.add(foundKey);
                }
            } else {
                List<String> foundKeys = jRedis.keys(format(SET_WITH_ALL_NODE_KEYS_FOR_NAME, "*"));
                for (String s : foundKeys) {
                    if (jRedis.sismember(s, foundKey)) {
                        keys.add(foundKey);
                        continue loopingIntoProperties;
                    }
                }
            }

        }
        return keys;
    }

    @Override
    protected Set<STNodeEntry> internalFindByCriteria(STPartition partition, STCriteria criteria) throws Exception {
        List<String> propertiesIntersection = newLinkedList();
        List<String> uniqueIdsFromLocalOnes = newLinkedList();
        boolean first = true;
        List<String> uniqueIds = newLinkedList();
        JRedis jredis = factory.getFrom(partition);
        for (STCriteriaItem c : criteria.getCriteriaItems()) {
            if (c instanceof STPropertyCriteriaItem) {
                STPropertyCriteriaItem p = (STPropertyCriteriaItem) c;
                if (first) {
                    propertiesIntersection.addAll(keysFromProperty(jredis, p.getNodeEntryName(), p.getPropertyName(), p.getType(), p.getValue()));
                    first = false;
                } else {
                    propertiesIntersection.retainAll(keysFromProperty(jredis, p.getNodeEntryName(), p.getPropertyName(), p.getType(), p.getValue()));
                }
            } else if (c instanceof STPropertyContainsString) {
                STPropertyContainsString p = (STPropertyContainsString) c;
                if (first) {
                    propertiesIntersection.addAll(keysFrom(jredis, p.getNodeEntryName(), this.propertyContains, p.getPropertyName(), p.getValue()));
                    first = false;
                } else {
                    propertiesIntersection.retainAll(keysFrom(jredis, p.getNodeEntryName(), this.propertyContains, p.getPropertyName(), p.getValue()));
                }
            } else if (c instanceof STPropertyStartsWithString) {
                STPropertyStartsWithString p = (STPropertyStartsWithString) c;
                if (first) {
                    propertiesIntersection.addAll(keysFrom(jredis, p.getNodeEntryName(), this.propertyStartingWith, p.getPropertyName(), p.getValue()));
                    first = false;
                } else {
                    propertiesIntersection.retainAll(keysFrom(jredis, p.getNodeEntryName(), this.propertyStartingWith, p.getPropertyName(), p.getValue()));
                }
            } else if (c instanceof STPropertyEndsWithString) {
                STPropertyEndsWithString p = (STPropertyEndsWithString) c;
                if (first) {
                    propertiesIntersection.addAll(keysFrom(jredis, p.getNodeEntryName(), this.propertyEndsWith, p.getPropertyName(), p.getValue()));
                    first = false;
                } else {
                    propertiesIntersection.retainAll(keysFrom(jredis, p.getNodeEntryName(), this.propertyEndsWith, p.getPropertyName(), p.getValue()));
                }
            } else if (c instanceof STUniqueKeyCriteriaItem) {
                STUniqueKeyCriteriaItem uniqueCriteria = (STUniqueKeyCriteriaItem) c;
                uniqueIds.add(supportMethods.getUniqueKeyAsStringHash(uniqueCriteria.getValue()));
            } else if (c instanceof STLocalKeyCriteriaItem) {
                STLocalKeyCriteriaItem uniqueCriteria = (STLocalKeyCriteriaItem) c;
                String localHash = supportMethods.getLocalKeyAsStringHash(uniqueCriteria.getValue());
                uniqueIdsFromLocalOnes.addAll(listBytesToListString(jredis.smembers(format(SET_WITH_ALL_LOCAL_KEYS, localHash))));
            } else {
                throw new IllegalArgumentException("invalid criteria");
            }
        }
        if (!uniqueIds.isEmpty() && !propertiesIntersection.isEmpty())
            throw new IllegalArgumentException("criteria with unique ids can't be used with other criteria types");

        List<String> ids;
        if (uniqueIds.isEmpty()) {
            ids = propertiesIntersection;
            if (!uniqueIdsFromLocalOnes.isEmpty()) {
                if (ids.isEmpty()) {
                    ids = uniqueIdsFromLocalOnes;
                } else {
                    ids.retainAll(uniqueIdsFromLocalOnes);
                }
            }
        } else {
            ids = uniqueIds;
        }


        Set<STNodeEntry> nodeEntries = newHashSet();
        for (String id : ids) {
            STNodeEntry nodeEntry = loadNodeOrReturnNull(id, criteria.getPartition());
            if (nodeEntry != null) {
                nodeEntries.add(nodeEntry);
            }
        }

        return ImmutableSet.copyOf(nodeEntries);
    }

    private STNodeEntry loadNodeOrReturnNull(String parentKey, STPartition partition) throws Exception {
        STUniqueKeyBuilder keyBuilder = null;
        JRedis jredis = factory.getFrom(partition);
        do {
            List<String> keyPropertyNames = listBytesToListString(jredis.smembers(format(SET_WITH_NODE_KEYS_NAMES, parentKey)));
            String nodeEntryName = toStr(jredis.get(format(KEY_WITH_NODE_ENTRY_NAME, parentKey)));
            if (nodeEntryName == null) break;

            keyBuilder = keyBuilder == null ? withPartition(partition).createKey(nodeEntryName) : keyBuilder.withParent(nodeEntryName);

            for (String keyName : keyPropertyNames) {
                String typeName = toStr(jredis.get(format(KEY_WITH_PROPERTY_TYPE, parentKey, keyName)));
                String typeValueAsString = toStr(jredis.get(format(KEY_WITH_PROPERTY_VALUE, parentKey, keyName)));
                Class<? extends Serializable> type = (Class<? extends Serializable>) findClassWithoutPrimitives(typeName);
                Serializable value = (Serializable)convert(typeValueAsString, type);
                keyBuilder.withEntry(keyName, type, value);
            }
            parentKey = toStr(jredis.get(format(KEY_WITH_PARENT_UNIQUE_ID, parentKey)));

        } while (parentKey != null);
        STNodeEntry nodeEntry;
        if (keyBuilder != null) {
            STUniqueKey uniqueKey = keyBuilder.andCreate();
            nodeEntry = createEntryWithKey(uniqueKey);

        } else {
            nodeEntry = null;
        }
        return nodeEntry;
    }

    private static List<String> listBytesToListString(List<byte[]> ids) {
        List<String> idsAsString = newLinkedList();
        if (ids != null) {
            for (byte[] b : ids) {
                String s = toStr(b);
                idsAsString.add(s);
            }
        }
        return idsAsString;
    }

    @Override
    protected void flushNewItem(STPartition partition, STNodeEntry entry) throws Exception {
        JRedis jredis = factory.getFrom(partition);

        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(entry.getUniqueKey());
        jredis.sadd(SET_WITH_ALL_KEYS, uniqueKey);
        jredis.sadd(format(SET_WITH_ALL_NODE_KEYS_FOR_NAME, entry.getNodeEntryName()), uniqueKey);
        jredis.set(format(KEY_WITH_NODE_ENTRY_NAME, uniqueKey), entry.getNodeEntryName());
        STUniqueKey parentKey = entry.getUniqueKey().getParentKey();
        if (parentKey != null) {
            String parentAsString = supportMethods.getUniqueKeyAsStringHash(parentKey);
            jredis.set(format(KEY_WITH_PARENT_UNIQUE_ID, uniqueKey), parentAsString);
            jredis.sadd(format(SET_WITH_NODE_CHILDREN_KEYS, parentAsString), uniqueKey);
            jredis.sadd(format(SET_WITH_NODE_CHILDREN_NAMED_KEYS, parentAsString, entry.getNodeEntryName()), uniqueKey);
        }
        String localKey = supportMethods.getLocalKeyAsStringHash(entry.getLocalKey());
        jredis.sadd(format(SET_WITH_ALL_LOCAL_KEYS, localKey), uniqueKey);

        for (STKeyEntry<?> k : entry.getLocalKey().getEntries()) {
            jredis.sadd(format(SET_WITH_NODE_KEYS_NAMES, uniqueKey), k.getPropertyName());
            jredis.sadd(format(SET_WITH_NODE_PROPERTY_NAMES, uniqueKey), k.getPropertyName());
            jredis.set(getPropertyKey(k.getPropertyName(), k.getType(), k.getValue(), uniqueKey), uniqueKey);
            jredis.set(format(KEY_WITH_PROPERTY_TYPE, uniqueKey, k.getPropertyName()), k.getType().getName());
            String valueAsString = convert(k.getValue(), String.class);
            if(valueAsString!=null) jredis.set(format(KEY_WITH_PROPERTY_VALUE, uniqueKey, k.getPropertyName()), valueAsString);
            jredis.set(format(KEY_WITH_PROPERTY_DESCRIPTION, uniqueKey, k.getPropertyName()), convert(STProperty.STPropertyDescription.KEY, String.class));
        }

    }

    @Override
    protected void flushRemovedItem(STPartition partition, STNodeEntry entry) throws Exception {
        JRedis jredis = factory.getFrom(partition);

        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(entry.getUniqueKey());
        jredis.srem(SET_WITH_ALL_KEYS, uniqueKey);
        jredis.srem(format(SET_WITH_ALL_NODE_KEYS_FOR_NAME, entry.getNodeEntryName()), uniqueKey);
        List<String> keys = jredis.keys(format(SET_WITH_ALL_KEYS_CONTAINING, uniqueKey));

        for (String key : keys) {
            jredis.del(key);
        }
    }

    @Override
    protected Set<STNodeEntry> internalNodeEntryGetNamedChildren(STPartition partition, STNodeEntry stNodeEntry, String name) throws Exception {
        JRedis jredis = factory.getFrom(partition);

        String parentKey = supportMethods.getUniqueKeyAsStringHash(stNodeEntry.getUniqueKey());
        String keyName = name == null ? format(SET_WITH_NODE_CHILDREN_KEYS, parentKey) : format(SET_WITH_NODE_CHILDREN_NAMED_KEYS, parentKey, name);
        List<String> childrenKeys = listBytesToListString(jredis.smembers(keyName));
        ImmutableSet.Builder<STNodeEntry> builder = ImmutableSet.builder();
        for (String id : childrenKeys) {
            STNodeEntry loadedNode = loadNodeOrReturnNull(id, stNodeEntry.getUniqueKey().getPartition());
            if (loadedNode != null) {
                builder.add(loadedNode);
            }
        }
        return builder.build();
    }

    @Override
    protected boolean internalHasSavedProperty(STPartition partition, STProperty stProperty) throws Exception {
        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(stProperty.getParent().getUniqueKey());
        JRedis jredis = factory.getFrom(partition);

        return jredis.exists(format(KEY_WITH_PROPERTY_TYPE, uniqueKey, stProperty.getPropertyName()));

    }

    @Override
    protected Class<?> internalPropertyDiscoverType(STPartition partition, STProperty stProperty) throws Exception {
        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(stProperty.getParent().getUniqueKey());
        JRedis jredis = factory.getFrom(partition);

        String typeName = toStr(jredis.get(format(KEY_WITH_PROPERTY_TYPE, uniqueKey, stProperty.getPropertyName())));

        Class<?> type = forName(typeName);
        return type;


    }

    @Override
    protected Set<STNodeEntry> internalNodeEntryGetChildren(STPartition partition, STNodeEntry stNodeEntry) throws Exception {
        return internalNodeEntryGetNamedChildren(partition, stNodeEntry, null);
    }

    @Override
    protected STNodeEntry internalNodeEntryGetParent(STPartition partition, STNodeEntry stNodeEntry) throws Exception {
        STUniqueKey parentKey = stNodeEntry.getUniqueKey().getParentKey();
        if (parentKey == null) return null;
        return createEntryWithKey(parentKey);
    }

    @Override
    protected <T> T internalPropertyGetKeyPropertyAs(STPartition partition, STProperty stProperty, Class<T> type) throws Exception {
        return this.<T>internalPropertyGetSimplePropertyAs(partition, stProperty, type);
    }

    @Override
    protected InputStream internalPropertyGetInputStreamProperty(STPartition partition, STProperty stProperty) throws Exception {
        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(stProperty.getParent().getUniqueKey());
        JRedis jredis = factory.getFrom(partition);

        byte[] fromStorage = jredis.get(format(KEY_WITH_PROPERTY_VALUE, uniqueKey, stProperty.getPropertyName()));

        return new ByteArrayInputStream(fromStorage);
    }

    @Override
    protected <T> T internalPropertyGetSerializedPojoPropertyAs(STPartition partition, STProperty stProperty, Class<T> type) throws Exception {
        InputStream inputStream = internalPropertyGetInputStreamProperty(partition, stProperty);
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        return (T) objectInputStream.readObject();

    }

    @Override
    protected <T> T internalPropertyGetSerializedMapPropertyAs(STPartition partition, STProperty stProperty, Class<T> type) throws Exception {
        return internalPropertyGetSerializedPojoPropertyAs(partition, stProperty, type);
    }

    @Override
    protected <T> T internalPropertyGetSerializedSetPropertyAs(STPartition partition, STProperty stProperty, Class<T> type) throws Exception {
        return internalPropertyGetSerializedPojoPropertyAs(partition, stProperty, type);
    }

    @Override
    protected <T> T internalPropertyGetSerializedListPropertyAs(STPartition partition, STProperty stProperty, Class<T> type) throws Exception {
        return internalPropertyGetSerializedPojoPropertyAs(partition, stProperty, type);
    }

    @Override
    protected <T> T internalPropertyGetSimplePropertyAs(STPartition partition, STProperty stProperty, Class<T> type) throws Exception {
        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(stProperty.getParent().getUniqueKey());
        JRedis jredis = factory.getFrom(partition);

        String typeValueAsString = toStr(jredis.get(format(KEY_WITH_PROPERTY_VALUE, uniqueKey, stProperty.getPropertyName())));
        T value = convert(typeValueAsString, type);
        return value;
    }

    @Override
    protected Set<STProperty> internalNodeEntryLoadProperties(STPartition partition, STNodeEntry stNodeEntry) throws Exception {
        JRedis jredis = factory.getFrom(partition);

        String parentKey = supportMethods.getUniqueKeyAsStringHash(stNodeEntry.getUniqueKey());
        List<String> propertyNames = listBytesToListString(jredis.smembers(format(SET_WITH_NODE_PROPERTY_NAMES, parentKey)));
        Set<STProperty> result = newHashSet();
        for (String propertyName : propertyNames) {
            STProperty property = loadProperty(partition, stNodeEntry, parentKey, propertyName);

            result.add(property);

        }

        return result;
    }

    @Override
    protected Set<STNodeEntry> internalFindNamed(STPartition partition, String nodeEntryName) throws Exception {

        JRedis jRedis = factory.getFrom(partition);
        List<String> ids = listBytesToListString(jRedis.smembers(format(SET_WITH_ALL_NODE_KEYS_FOR_NAME, nodeEntryName)));
        ImmutableSet.Builder<STNodeEntry> builder = ImmutableSet.<STNodeEntry>builder();
        for (String id : ids) {
            STNodeEntry loadedNode = loadNodeOrReturnNull(id, partition);
            if (loadedNode != null) {
                builder.add(loadedNode);
            }
        }
        return builder.build();
    }

    private STProperty loadProperty(STPartition partition, STNodeEntry stNodeEntry, String parentKey, String propertyName) throws Exception{
        JRedis jredis = factory.getFrom(partition);

        String typeName = toStr(jredis.get(format(KEY_WITH_PROPERTY_TYPE, parentKey, propertyName)));
        String descriptionAsString = toStr(jredis.get(format(KEY_WITH_PROPERTY_DESCRIPTION, parentKey, propertyName)));
        STProperty.STPropertyDescription description = STProperty.STPropertyDescription.valueOf(descriptionAsString);
        Class<?> type = findClassWithoutPrimitives(typeName);

        Class<?> parameterized1 = null;
        Class<?> parameterized2 = null;
        if (jredis.exists(format(KEY_WITH_PROPERTY_PARAMETERIZED_1, parentKey, propertyName))) {
            String typeName1 = toStr(jredis.get(format(KEY_WITH_PROPERTY_PARAMETERIZED_1, parentKey, propertyName)));
            parameterized1 = forName(typeName1);

        }
        if (jredis.exists(format(KEY_WITH_PROPERTY_PARAMETERIZED_2, parentKey, propertyName))) {
            String typeName2 = toStr(jredis.get(format(KEY_WITH_PROPERTY_PARAMETERIZED_2, parentKey, propertyName)));
            parameterized2 = forName(typeName2);

        }

        STProperty property = new STPropertyImpl(stNodeEntry, propertyName, description, type, parameterized1, parameterized2);
        if (description.getLoadWeight().equals(STProperty.STPropertyDescription.STLoadWeight.EASY)) {
            String typeValueAsString = toStr(jredis.get(format(KEY_WITH_PROPERTY_VALUE, parentKey, propertyName)));
            Serializable value = (Serializable) convert(typeValueAsString, type);
            property.getInternalMethods().setValueOnLoad(value);
        }
        return property;
    }

    @Override
    protected void internalFlushInputStreamProperty(STPartition partition, STProperty dirtyProperty) throws Exception {
        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(dirtyProperty.getParent().getUniqueKey());

        InputStream valueAsStream = dirtyProperty.getInternalMethods().<InputStream>getTransientValue();
        if (valueAsStream.markSupported()) {
            valueAsStream.reset();
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IOUtils.copy(valueAsStream, outputStream);
        JRedis jredis = factory.getFrom(partition);

        jredis.sadd(format(SET_WITH_NODE_PROPERTY_NAMES, uniqueKey), dirtyProperty.getPropertyName());

        jredis.set(format(KEY_WITH_PROPERTY_TYPE, uniqueKey, dirtyProperty.getPropertyName()), dirtyProperty.getInternalMethods().<Object>getPropertyType().getName());
        jredis.set(format(KEY_WITH_PROPERTY_VALUE, uniqueKey, dirtyProperty.getPropertyName()), outputStream.toByteArray());
        jredis.set(format(KEY_WITH_PROPERTY_DESCRIPTION, uniqueKey, dirtyProperty.getPropertyName()), convert(STProperty.STPropertyDescription.INPUT_STREAM, String.class));
        jredis.del(format(KEY_WITH_PROPERTY_PARAMETERIZED_1, uniqueKey, dirtyProperty.getPropertyName()));
        jredis.del(format(KEY_WITH_PROPERTY_PARAMETERIZED_2, uniqueKey, dirtyProperty.getPropertyName()));

    }

    @Override
    protected void internalFlushSerializedPojoProperty(STPartition partition, STProperty dirtyProperty) throws Exception {
        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(dirtyProperty.getParent().getUniqueKey());
        flushStream(partition, dirtyProperty, uniqueKey);
        JRedis jredis = factory.getFrom(partition);

        jredis.set(format(KEY_WITH_PROPERTY_DESCRIPTION, uniqueKey, dirtyProperty.getPropertyName()),
                convert(STProperty.STPropertyDescription.SERIALIZED_POJO, String.class));
        jredis.del(format(KEY_WITH_PROPERTY_PARAMETERIZED_1, uniqueKey, dirtyProperty.getPropertyName()));
        jredis.del(format(KEY_WITH_PROPERTY_PARAMETERIZED_2, uniqueKey, dirtyProperty.getPropertyName()));

    }

    private void flushStream(STPartition partition, STProperty dirtyProperty, String uniqueKey) throws IOException, RedisException {
        Serializable value = dirtyProperty.getInternalMethods().<Serializable>getTransientValue();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(value);
        objectOutputStream.flush();
        JRedis jredis = factory.getFrom(partition);

        jredis.sadd(format(SET_WITH_NODE_PROPERTY_NAMES, uniqueKey), dirtyProperty.getPropertyName());

        jredis.set(format(KEY_WITH_PROPERTY_TYPE, uniqueKey, dirtyProperty.getPropertyName()), dirtyProperty.getInternalMethods().<Object>getPropertyType().getName());
        jredis.set(format(KEY_WITH_PROPERTY_VALUE, uniqueKey, dirtyProperty.getPropertyName()), byteArrayOutputStream.toByteArray());
    }

    @Override
    protected void internalFlushSerializedMapProperty(STPartition partition, STProperty dirtyProperty) throws Exception {
        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(dirtyProperty.getParent().getUniqueKey());
        flushStream(partition, dirtyProperty, uniqueKey);
        JRedis jredis = factory.getFrom(partition);
        jredis.set(format(KEY_WITH_PROPERTY_DESCRIPTION, uniqueKey, dirtyProperty.getPropertyName()), convert(STProperty.STPropertyDescription.SERIALIZED_MAP, String.class));
        jredis.set(format(KEY_WITH_PROPERTY_PARAMETERIZED_1, uniqueKey, dirtyProperty.getPropertyName()), dirtyProperty.getInternalMethods().<Object>getFirstParameterizedType().getName());
        jredis.set(format(KEY_WITH_PROPERTY_PARAMETERIZED_2, uniqueKey, dirtyProperty.getPropertyName()), dirtyProperty.getInternalMethods().<Object>getSecondParameterizedType().getName());
    }

    @Override
    protected void internalFlushSerializedSetProperty(STPartition partition, STProperty dirtyProperty) throws Exception {
        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(dirtyProperty.getParent().getUniqueKey());
        flushStream(partition, dirtyProperty, uniqueKey);
        JRedis jredis = factory.getFrom(partition);
        jredis.set(format(KEY_WITH_PROPERTY_DESCRIPTION, uniqueKey, dirtyProperty.getPropertyName()), convert(STProperty.STPropertyDescription.SERIALIZED_SET, String.class));
        jredis.set(format(KEY_WITH_PROPERTY_PARAMETERIZED_1, uniqueKey, dirtyProperty.getPropertyName()), dirtyProperty.getInternalMethods().<Object>getFirstParameterizedType().getName());
        jredis.del(format(KEY_WITH_PROPERTY_PARAMETERIZED_2, uniqueKey, dirtyProperty.getPropertyName()));

    }

    @Override
    protected void internalFlushSerializedListProperty(STPartition partition, STProperty dirtyProperty) throws Exception {
        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(dirtyProperty.getParent().getUniqueKey());
        flushStream(partition, dirtyProperty, uniqueKey);
        JRedis jredis = factory.getFrom(partition);
        jredis.set(format(KEY_WITH_PROPERTY_DESCRIPTION, uniqueKey, dirtyProperty.getPropertyName()), convert(STProperty.STPropertyDescription.SERIALIZED_LIST, String.class));
        jredis.set(format(KEY_WITH_PROPERTY_PARAMETERIZED_1, uniqueKey, dirtyProperty.getPropertyName()), dirtyProperty.getInternalMethods().<Object>getFirstParameterizedType().getName());
        jredis.del(format(KEY_WITH_PROPERTY_PARAMETERIZED_2, uniqueKey, dirtyProperty.getPropertyName()));

    }

    @Override
    protected void internalFlushSimpleProperty(STPartition partition, STProperty dirtyProperty) throws Exception {


        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(dirtyProperty.getParent().getUniqueKey());

        JRedis jredis = factory.getFrom(partition);


        List<String> keysToBeRemoved = jredis.keys(getPropertyOldKey(dirtyProperty.getPropertyName(), uniqueKey));
        for (String key : keysToBeRemoved) jredis.del(key);

        String transientValueAsString = convert(dirtyProperty.getInternalMethods().<Object>getTransientValue(), String.class);

        jredis.sadd(format(SET_WITH_NODE_PROPERTY_NAMES, uniqueKey), dirtyProperty.getPropertyName());
        jredis.set(getPropertyKey(dirtyProperty.getPropertyName(), dirtyProperty.getInternalMethods().getPropertyType(), transientValueAsString, uniqueKey), uniqueKey);

        jredis.set(format(KEY_WITH_PROPERTY_TYPE, uniqueKey, dirtyProperty.getPropertyName()), dirtyProperty.getInternalMethods().<Object>getPropertyType().getName());
        if (transientValueAsString != null) {
            jredis.set(format(KEY_WITH_PROPERTY_VALUE, uniqueKey, dirtyProperty.getPropertyName()), transientValueAsString);
        } else {
            jredis.del(format(KEY_WITH_PROPERTY_VALUE, uniqueKey, dirtyProperty.getPropertyName()));
        }
        jredis.set(format(KEY_WITH_PROPERTY_DESCRIPTION, uniqueKey, dirtyProperty.getPropertyName()), convert(STProperty.STPropertyDescription.SIMPLE, String.class));
        jredis.del(format(KEY_WITH_PROPERTY_PARAMETERIZED_1, uniqueKey, dirtyProperty.getPropertyName()));
        jredis.del(format(KEY_WITH_PROPERTY_PARAMETERIZED_2, uniqueKey, dirtyProperty.getPropertyName()));


    }
}
