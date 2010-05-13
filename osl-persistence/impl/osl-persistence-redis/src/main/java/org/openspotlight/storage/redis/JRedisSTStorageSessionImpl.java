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
import com.google.inject.internal.ImmutableList;
import org.apache.commons.io.IOUtils;
import org.jredis.JRedis;
import org.openspotlight.storage.AbstractSTStorageSession;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STRepositoryPath;
import org.openspotlight.storage.domain.key.STKeyEntry;
import org.openspotlight.storage.domain.key.STUniqueKey;
import org.openspotlight.storage.domain.node.STNodeEntry;
import org.openspotlight.storage.domain.node.STProperty;
import org.openspotlight.storage.domain.node.STPropertyImpl;
import org.openspotlight.storage.redis.guice.JRedisFactory;

import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Class.forName;
import static java.text.MessageFormat.format;
import static java.util.Collections.emptyList;
import static org.jredis.ri.alphazero.support.DefaultCodec.toStr;
import static org.openspotlight.common.util.Conversion.convert;
import static org.openspotlight.common.util.Reflection.findClassWithoutPrimitives;

/**
 * Created by User: feu - Date: Mar 23, 2010 - Time: 4:46:25 PM
 */
public class JRedisSTStorageSessionImpl extends AbstractSTStorageSession {

    private class JRedisLoggedExecution {
        private final String usedKeys;

        private final JRedis jredis;

        private JRedisLoggedExecution(String uuid, JRedis jredis) {
            this.usedKeys = format(SET_WITH_ALL_DEPENDENT_KEYS, uuid);
            this.jredis = jredis;
        }

        public void sadd(String s, byte[] o) throws Exception {
            jredis.sadd(s, o);
            jredis.sadd(usedKeys, s);
        }

        public void sadd(String s, Number o) throws Exception {
            jredis.sadd(s, o);
            jredis.sadd(usedKeys, s);
        }

        public void sadd(String s, String o) throws Exception {
            jredis.sadd(s, o);
            jredis.sadd(usedKeys, s);
        }

        public void sadd(String s, Serializable o) throws Exception {
            jredis.sadd(s, o);
            jredis.sadd(usedKeys, s);
        }

        public void set(String s, Number o) throws Exception {
            jredis.set(s, o);
            jredis.sadd(usedKeys, s);
        }

        public void set(String s, String o) throws Exception {
            jredis.set(s, o);
            jredis.sadd(usedKeys, s);
        }

        public void set(String s, Serializable o) throws Exception {
            jredis.set(s, o);
            jredis.sadd(usedKeys, s);
        }

        public void set(String s, byte[] o) throws Exception {
            jredis.set(s, o);
            jredis.sadd(usedKeys, s);
        }
    }


    private static final String SET_WITH_ALL_DEPENDENT_KEYS = "{0}:dependent-keys";
    private static final String SET_WITH_ALL_KEYS = "uids";
    private static final String SET_WITH_ALL_NODE_KEYS_FOR_NAME = "name:{0}:uids";
    private static final String SET_WITH_ALL_LOCAL_KEYS = "lkeys:{0}:uids";
    private static final String SET_WITH_NODE_KEYS_NAMES = "nuid:{0}:key-names";
    private static final String SET_WITH_NODE_CHILDREN_KEYS = "nuid:{0}:cld-uids";
    private static final String SET_WITH_NODE_CHILDREN_NAMED_KEYS = "nuid:{0}:nname:{1}:cld-uids";
    private static final String SET_WITH_NODE_PROPERTY_NAMES = "nuid:{0}:pnames";
    private static final String KEY_WITH_PROPERTY_VALUE = "nuid:{0}:pname:{1}:value";
    private static final String KEY_WITH_PROPERTY_DESCRIPTION = "nuid:{0}:pname:{1}:desc";
    private static final String KEY_WITH_PROPERTY_PARAMETERIZED_1 = "nuid:{0}:pname:{1}:prt1";
    private static final String KEY_WITH_PROPERTY_PARAMETERIZED_2 = "nuid:{0}:pname:{1}:prt2";
    private static final String KEY_WITH_PROPERTY_TYPE = "nuid:{0}:pname:{1}:type";
    private static final String KEY_WITH_PARENT_UNIQUE_ID = "nuid:{0}:prt-uid";
    private static final String SET_WITH_NODE_ENTRY_NAME_AND_PROPERTY_VALUE = "name:{0}:ptype:{1}:pname:{2}";
    private static final String VALUE_WITH_NODE_KEY_AND_PROPERTY_VALUE = "{0}:{1}";
    private static final String SET_WITH_PROPERTY_VALUE = "ptype:{1}:pname:{2}";
    private static final String KEY_WITH_NODE_ENTRY_NAME = "nuid:{0}:nname";

    private static enum SearchType {
        EQUAL, STRING_STARTS_WITH, STRING_ENDS_WITH, STRING_CONTAINS
    }


    private final JRedisFactory factory;

    @Inject
    public JRedisSTStorageSessionImpl(STFlushMode flushMode, JRedisFactory factory, STRepositoryPath repositoryPath) {
        super(flushMode, repositoryPath);
        this.factory = factory;
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
                    propertiesIntersection.addAll(keysFromProperty(jredis, p.getNodeEntryName(), p.getPropertyName(), p.getType(), SearchType.EQUAL, p.getValue()));
                    first = false;
                } else {
                    propertiesIntersection.retainAll(keysFromProperty(jredis, p.getNodeEntryName(), p.getPropertyName(), p.getType(), SearchType.EQUAL, p.getValue()));
                }
            }
            if (c instanceof STPropertyContainsString) {
                STPropertyContainsString p = (STPropertyContainsString) c;
                if (first) {
                    propertiesIntersection.addAll(keysFromProperty(jredis, p.getNodeEntryName(), p.getPropertyName(), String.class, SearchType.STRING_CONTAINS, p.getValue()));
                    first = false;
                } else {
                    propertiesIntersection.retainAll(keysFromProperty(jredis, p.getNodeEntryName(), p.getPropertyName(), String.class, SearchType.STRING_CONTAINS, p.getValue()));
                }
            }
            if (c instanceof STPropertyStartsWithString) {
                STPropertyStartsWithString p = (STPropertyStartsWithString) c;
                if (first) {
                    propertiesIntersection.addAll(keysFromProperty(jredis, p.getNodeEntryName(), p.getPropertyName(), String.class, SearchType.STRING_STARTS_WITH, p.getValue()));
                    first = false;
                } else {
                    propertiesIntersection.retainAll(keysFromProperty(jredis, p.getNodeEntryName(), p.getPropertyName(), String.class, SearchType.STRING_STARTS_WITH, p.getValue()));
                }
            }
            if (c instanceof STPropertyEndsWithString) {
                STPropertyEndsWithString p = (STPropertyEndsWithString) c;
                if (first) {
                    propertiesIntersection.addAll(keysFromProperty(jredis, p.getNodeEntryName(), p.getPropertyName(), String.class, SearchType.STRING_ENDS_WITH, p.getValue()));
                    first = false;
                } else {
                    propertiesIntersection.retainAll(keysFromProperty(jredis, p.getNodeEntryName(), p.getPropertyName(), String.class, SearchType.STRING_ENDS_WITH, p.getValue()));
                }
            }
            if (c instanceof STUniqueKeyCriteriaItem) {
                STUniqueKeyCriteriaItem uniqueCriteria = (STUniqueKeyCriteriaItem) c;
                uniqueIds.add(supportMethods.getUniqueKeyAsStringHash(uniqueCriteria.getValue()));

            }
            if (c instanceof STLocalKeyCriteriaItem) {
                STLocalKeyCriteriaItem uniqueCriteria = (STLocalKeyCriteriaItem) c;
                String localHash = supportMethods.getLocalKeyAsStringHash(uniqueCriteria.getValue());
                uniqueIdsFromLocalOnes.addAll(listBytesToListString(jredis.smembers(format(SET_WITH_ALL_LOCAL_KEYS, localHash))));

            }
        }
        if (criteria.getCriteriaItems().size() == 0) {
            List<String> keys = jredis.keys(format(SET_WITH_ALL_NODE_KEYS_FOR_NAME, criteria.getNodeName()));
            for (String key : keys) {
                uniqueIds.addAll(listBytesToListString(jredis.smembers(key)));
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

    private Collection<String> keysFromProperty(JRedis jredis, String nodeEntryName, String propertyName, Class type, SearchType equal, Serializable value) throws Exception {

        String setWithPropertyInformation = nodeEntryName == null ? format(SET_WITH_PROPERTY_VALUE, type.getName(), propertyName) : format(SET_WITH_NODE_ENTRY_NAME_AND_PROPERTY_VALUE, nodeEntryName, type.getName(), propertyName);
        if (!jredis.exists(setWithPropertyInformation)) {
            return emptyList();
        }
        String transientValueAsString = convert(value, String.class);
        if (transientValueAsString == null) transientValueAsString = "null";

        List<String> members = listBytesToListString(jredis.smembers(setWithPropertyInformation));
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        for (String member : members) {
            int idx = member.indexOf(':');
            String nodeKey = member.substring(0, idx);
            String propertyValue = member.substring(idx + 1);
            boolean needsToAdd = false;
            switch (equal) {
                case EQUAL:
                    needsToAdd = propertyValue.equals(transientValueAsString);
                    break;
                case STRING_CONTAINS:
                    needsToAdd = propertyValue.contains(transientValueAsString);
                    break;
                case STRING_ENDS_WITH:
                    needsToAdd = propertyValue.endsWith(transientValueAsString);
                    break;
                case STRING_STARTS_WITH:
                    needsToAdd = propertyValue.startsWith(transientValueAsString);
                    break;
            }
            if (needsToAdd) {
                builder.add(nodeKey);
            }
        }
        return builder.build();
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
                Serializable value = (Serializable) convert(typeValueAsString, type);
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

        JRedisLoggedExecution jredisExec = new JRedisLoggedExecution(uniqueKey, jredis);
        jredisExec.sadd(SET_WITH_ALL_KEYS, uniqueKey);
        jredisExec.sadd(format(SET_WITH_ALL_NODE_KEYS_FOR_NAME, entry.getNodeEntryName()), uniqueKey);
        jredisExec.set(format(KEY_WITH_NODE_ENTRY_NAME, uniqueKey), entry.getNodeEntryName());
        STUniqueKey parentKey = entry.getUniqueKey().getParentKey();
        if (parentKey != null) {
            String parentAsString = supportMethods.getUniqueKeyAsStringHash(parentKey);
            jredisExec.set(format(KEY_WITH_PARENT_UNIQUE_ID, uniqueKey), parentAsString);
            jredisExec.sadd(format(SET_WITH_NODE_CHILDREN_KEYS, parentAsString), uniqueKey);
            jredisExec.sadd(format(SET_WITH_NODE_CHILDREN_NAMED_KEYS, parentAsString, entry.getNodeEntryName()), uniqueKey);
        }
        String localKey = supportMethods.getLocalKeyAsStringHash(entry.getLocalKey());
        jredisExec.sadd(format(SET_WITH_ALL_LOCAL_KEYS, localKey), uniqueKey);

        for (STKeyEntry<?> k : entry.getLocalKey().getEntries()) {
            internalFlushSimplePropertyAndCreateIndex(jredisExec,
                    partition, k.getPropertyName(),
                    k.getType(),
                    k.getValue(),
                    uniqueKey,
                    entry.getNodeEntryName());


            jredisExec.sadd(format(SET_WITH_NODE_KEYS_NAMES, uniqueKey), k.getPropertyName());
            jredisExec.set(format(KEY_WITH_PROPERTY_TYPE, uniqueKey, k.getPropertyName()), findClassWithoutPrimitives(k.getType()).getName());
            String valueAsString = convert(k.getValue(), String.class);
            if (valueAsString != null)
                jredisExec.set(format(KEY_WITH_PROPERTY_VALUE, uniqueKey, k.getPropertyName()), valueAsString);
        }

    }

    @Override
    protected void flushRemovedItem(STPartition partition, STNodeEntry entry) throws Exception {
        JRedis jredis = factory.getFrom(partition);

        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(entry.getUniqueKey());
        jredis.srem(SET_WITH_ALL_KEYS, uniqueKey);
        jredis.srem(format(SET_WITH_ALL_NODE_KEYS_FOR_NAME, entry.getNodeEntryName()), uniqueKey);
        String dependentKeys = format(SET_WITH_ALL_DEPENDENT_KEYS, uniqueKey);

        List<String> keys = listBytesToListString(jredis.smembers(dependentKeys));

        for (String key : keys) {
            if (key.contains(uniqueKey)) {
                jredis.del(key);
            } else {
                List<String> possibleValues = listBytesToListString(jredis.smembers(key));
                for (String possibleValue : possibleValues) {
                    if (possibleValue.contains(uniqueKey)) {
                        jredis.srem(key,possibleValue);
                    }
                }

            }
        }
        jredis.del(dependentKeys);
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
            if (property != null) result.add(property);

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

    private STProperty loadProperty(STPartition partition, STNodeEntry stNodeEntry, String parentKey, String propertyName) throws Exception {
        JRedis jredis = factory.getFrom(partition);

        String typeName = toStr(jredis.get(format(KEY_WITH_PROPERTY_TYPE, parentKey, propertyName)));
        if (typeName == null) return null;
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
        JRedisLoggedExecution jredisExecution = new JRedisLoggedExecution(uniqueKey, jredis);
        jredisExecution.sadd(format(SET_WITH_NODE_PROPERTY_NAMES, uniqueKey), dirtyProperty.getPropertyName());

        jredisExecution.set(format(KEY_WITH_PROPERTY_TYPE, uniqueKey, dirtyProperty.getPropertyName()), dirtyProperty.getInternalMethods().<Object>getPropertyType().getName());
        jredisExecution.set(format(KEY_WITH_PROPERTY_VALUE, uniqueKey, dirtyProperty.getPropertyName()), outputStream.toByteArray());
        jredisExecution.set(format(KEY_WITH_PROPERTY_DESCRIPTION, uniqueKey, dirtyProperty.getPropertyName()), convert(STProperty.STPropertyDescription.INPUT_STREAM, String.class));
        jredis.del(format(KEY_WITH_PROPERTY_PARAMETERIZED_1, uniqueKey, dirtyProperty.getPropertyName()));
        jredis.del(format(KEY_WITH_PROPERTY_PARAMETERIZED_2, uniqueKey, dirtyProperty.getPropertyName()));

    }

    @Override
    protected void internalFlushSerializedPojoProperty(STPartition partition, STProperty dirtyProperty) throws Exception {
        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(dirtyProperty.getParent().getUniqueKey());
        JRedis jredis = factory.getFrom(partition);
        JRedisLoggedExecution jredisExecution = new JRedisLoggedExecution(uniqueKey, jredis);
        flushStream(jredisExecution, partition, dirtyProperty, uniqueKey);

        jredisExecution.set(format(KEY_WITH_PROPERTY_DESCRIPTION, uniqueKey, dirtyProperty.getPropertyName()),
                convert(STProperty.STPropertyDescription.SERIALIZED_POJO, String.class));
        jredis.del(format(KEY_WITH_PROPERTY_PARAMETERIZED_1, uniqueKey, dirtyProperty.getPropertyName()));
        jredis.del(format(KEY_WITH_PROPERTY_PARAMETERIZED_2, uniqueKey, dirtyProperty.getPropertyName()));

    }

    private void flushStream(JRedisLoggedExecution jredisExecution, STPartition partition, STProperty dirtyProperty, String uniqueKey) throws Exception {
        Serializable value = dirtyProperty.getInternalMethods().<Serializable>getTransientValue();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(value);
        objectOutputStream.flush();
        JRedis jredis = factory.getFrom(partition);

        jredisExecution.sadd(format(SET_WITH_NODE_PROPERTY_NAMES, uniqueKey), dirtyProperty.getPropertyName());

        jredisExecution.set(format(KEY_WITH_PROPERTY_TYPE, uniqueKey, dirtyProperty.getPropertyName()), dirtyProperty.getInternalMethods().<Object>getPropertyType().getName());
        jredisExecution.set(format(KEY_WITH_PROPERTY_VALUE, uniqueKey, dirtyProperty.getPropertyName()), byteArrayOutputStream.toByteArray());
    }

    @Override
    protected void internalFlushSerializedMapProperty(STPartition partition, STProperty dirtyProperty) throws Exception {
        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(dirtyProperty.getParent().getUniqueKey());
        JRedis jredis = factory.getFrom(partition);
        JRedisLoggedExecution jredisExecution = new JRedisLoggedExecution(uniqueKey, jredis);
        flushStream(jredisExecution, partition, dirtyProperty, uniqueKey);
        jredisExecution.set(format(KEY_WITH_PROPERTY_DESCRIPTION, uniqueKey, dirtyProperty.getPropertyName()), convert(STProperty.STPropertyDescription.SERIALIZED_MAP, String.class));
        jredisExecution.set(format(KEY_WITH_PROPERTY_PARAMETERIZED_1, uniqueKey, dirtyProperty.getPropertyName()), dirtyProperty.getInternalMethods().<Object>getFirstParameterizedType().getName());
        jredisExecution.set(format(KEY_WITH_PROPERTY_PARAMETERIZED_2, uniqueKey, dirtyProperty.getPropertyName()), dirtyProperty.getInternalMethods().<Object>getSecondParameterizedType().getName());
    }

    @Override
    protected void internalFlushSerializedSetProperty(STPartition partition, STProperty dirtyProperty) throws Exception {
        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(dirtyProperty.getParent().getUniqueKey());
        JRedis jredis = factory.getFrom(partition);
        JRedisLoggedExecution jredisExecution = new JRedisLoggedExecution(uniqueKey, jredis);
        flushStream(jredisExecution, partition, dirtyProperty, uniqueKey);
        jredisExecution.set(format(KEY_WITH_PROPERTY_DESCRIPTION, uniqueKey, dirtyProperty.getPropertyName()), convert(STProperty.STPropertyDescription.SERIALIZED_SET, String.class));
        jredisExecution.set(format(KEY_WITH_PROPERTY_PARAMETERIZED_1, uniqueKey, dirtyProperty.getPropertyName()), dirtyProperty.getInternalMethods().<Object>getFirstParameterizedType().getName());
        jredis.del(format(KEY_WITH_PROPERTY_PARAMETERIZED_2, uniqueKey, dirtyProperty.getPropertyName()));

    }

    @Override
    protected void internalFlushSerializedListProperty(STPartition partition, STProperty dirtyProperty) throws Exception {
        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(dirtyProperty.getParent().getUniqueKey());
        JRedis jredis = factory.getFrom(partition);
        JRedisLoggedExecution jredisExecution = new JRedisLoggedExecution(uniqueKey, jredis);
        flushStream(jredisExecution, partition, dirtyProperty, uniqueKey);
        jredisExecution.set(format(KEY_WITH_PROPERTY_DESCRIPTION, uniqueKey, dirtyProperty.getPropertyName()), convert(STProperty.STPropertyDescription.SERIALIZED_LIST, String.class));
        jredisExecution.set(format(KEY_WITH_PROPERTY_PARAMETERIZED_1, uniqueKey, dirtyProperty.getPropertyName()), dirtyProperty.getInternalMethods().<Object>getFirstParameterizedType().getName());
        jredis.del(format(KEY_WITH_PROPERTY_PARAMETERIZED_2, uniqueKey, dirtyProperty.getPropertyName()));

    }

    @Override
    protected void internalFlushSimpleProperty(STPartition partition, STProperty dirtyProperty) throws Exception {
        JRedis jredis = factory.getFrom(partition);
        String uniqueKey = getSupportMethods().getUniqueKeyAsStringHash(dirtyProperty.getParent().getUniqueKey());
        JRedisLoggedExecution jredisExecution = new JRedisLoggedExecution(uniqueKey, jredis);

        internalFlushSimplePropertyAndCreateIndex(jredisExecution, partition, dirtyProperty.getPropertyName(),
                dirtyProperty.getInternalMethods().getPropertyType(),
                dirtyProperty.getInternalMethods().<Object>getTransientValue(),
                uniqueKey,
                dirtyProperty.getParent().getNodeEntryName());


    }

    private void internalFlushSimplePropertyAndCreateIndex(JRedisLoggedExecution exec, STPartition partition, String propertyName, Class<?> propertyType,
                                                           Object propertyValue, String uniqueKey, String nodeEntryName) throws Exception {

        JRedis jredis = factory.getFrom(partition);


        String transientValueAsString = convert(propertyValue, String.class);

        exec.sadd(format(SET_WITH_NODE_PROPERTY_NAMES, uniqueKey), propertyName);
        String setWithPropertyInformation = format(SET_WITH_NODE_ENTRY_NAME_AND_PROPERTY_VALUE, nodeEntryName,
                propertyType.getName(), propertyName);
        String setWithPropertyInformationWithoutName = format(SET_WITH_PROPERTY_VALUE,
                propertyType.getName(), propertyName);


        String valueFromSetPropertyInformation = format(VALUE_WITH_NODE_KEY_AND_PROPERTY_VALUE, uniqueKey, transientValueAsString);
        if (jredis.exists(setWithPropertyInformation)) {
            List<String> existentValues = listBytesToListString(jredis.smembers(setWithPropertyInformation));
            boolean found = false;
            for (String value : existentValues) {
                if (value.startsWith(uniqueKey)) {
                    if (value.equals(valueFromSetPropertyInformation)) {
                        found = true;
                        break;
                    } else {
                        jredis.srem(setWithPropertyInformation, value);
                    }
                }
            }
            if (!found) {
                exec.sadd(setWithPropertyInformation, valueFromSetPropertyInformation);
            }
        } else {
            exec.sadd(setWithPropertyInformation, valueFromSetPropertyInformation);
        }
        if (jredis.exists(setWithPropertyInformationWithoutName)) {
            List<String> existentValues = listBytesToListString(jredis.smembers(setWithPropertyInformationWithoutName));
            boolean found = false;
            for (String value : existentValues) {
                if (value.startsWith(uniqueKey)) {
                    if (value.equals(valueFromSetPropertyInformation)) {
                        found = true;
                        break;
                    } else {
                        jredis.srem(setWithPropertyInformation, value);
                    }
                }
            }
            if (!found) {
                exec.sadd(setWithPropertyInformationWithoutName, valueFromSetPropertyInformation);
            }
        } else {
            exec.sadd(setWithPropertyInformationWithoutName, valueFromSetPropertyInformation);
        }

        exec.set(format(KEY_WITH_PROPERTY_TYPE, uniqueKey, propertyName), propertyType.getName());
        if (transientValueAsString != null) {
            exec.set(format(KEY_WITH_PROPERTY_VALUE, uniqueKey, propertyName), transientValueAsString);
        } else {
            jredis.del(format(KEY_WITH_PROPERTY_VALUE, uniqueKey, propertyName));
        }
        exec.set(format(KEY_WITH_PROPERTY_DESCRIPTION, uniqueKey, propertyName), convert(STProperty.STPropertyDescription.SIMPLE, String.class));
//        jredis.del(format(KEY_WITH_PROPERTY_PARAMETERIZED_1, uniqueKey, dirtyProperty.getPropertyName()));
//        jredis.del(format(KEY_WITH_PROPERTY_PARAMETERIZED_2, uniqueKey, dirtyProperty.getPropertyName()));
    }
}
