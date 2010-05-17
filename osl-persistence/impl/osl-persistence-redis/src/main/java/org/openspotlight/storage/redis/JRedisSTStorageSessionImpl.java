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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
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
import static org.jredis.ri.alphazero.support.DefaultCodec.toStr;
import static org.openspotlight.common.util.Conversion.convert;
import static org.openspotlight.common.util.Reflection.findClassWithoutPrimitives;

/**
 * Created by User: feu - Date: Mar 23, 2010 - Time: 4:46:25 PM
 */
public class JRedisSTStorageSessionImpl extends AbstractSTStorageSession {

    private static class CustomizedFormat {

        private final String[] items;
        public final int sizeOfParameters;

        public CustomizedFormat(String itemsAsString) {
            items = itemsAsString.split("[ ]");
            sizeOfParameters = items.length - 1;
        }

        public String format(String s) {
            if (sizeOfParameters != 1)
                throw new IllegalArgumentException("Expected " + sizeOfParameters + " parameters");
            StringBuilder b = new StringBuilder();
            b.append(items[0]);
            b.append(s);
            b.append(items[1]);
            return b.toString();
        }

        public String format(String s1, String s2) {
            if (sizeOfParameters != 2)
                throw new IllegalArgumentException("Expected " + sizeOfParameters + " parameters");
            StringBuilder b = new StringBuilder();
            b.append(items[0]);
            b.append(s1);
            b.append(items[1]);
            b.append(s2);
            b.append(items[2]);
            return b.toString();
        }

    }

    private class JRedisLoggedExecution {
        private final String usedKeys;

        private final JRedis jredis;

        private JRedisLoggedExecution(String uuid, JRedis jredis) {
            this.usedKeys = SET_WITH_ALL_DEPENDENT_KEYS.format(uuid);
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


    private static final CustomizedFormat SET_WITH_INDEX_ENTRY = new CustomizedFormat("index: :pname: :uid");
    private static final CustomizedFormat SET_WITH_ALL_DEPENDENT_KEYS = new CustomizedFormat("nuid: :dependent-keys");
    private static final String SET_WITH_ALL_KEYS = "uids";
    private static final CustomizedFormat SET_WITH_ALL_NODE_KEYS_FOR_NAME = new CustomizedFormat("name: :uids");
    private static final CustomizedFormat SET_WITH_ALL_LOCAL_KEYS = new CustomizedFormat("lkeys: :uids");
    private static final CustomizedFormat SET_WITH_NODE_KEYS_NAMES = new CustomizedFormat("nuid: :key-names");
    private static final CustomizedFormat SET_WITH_NODE_CHILDREN_KEYS = new CustomizedFormat("nuid: :cld-uids");
    private static final CustomizedFormat SET_WITH_NODE_CHILDREN_NAMED_KEYS = new CustomizedFormat("nuid: :nname: :cld-uids");
    private static final CustomizedFormat SET_WITH_NODE_PROPERTY_NAMES = new CustomizedFormat("nuid: :pnames");
    private static final CustomizedFormat KEY_WITH_PROPERTY_VALUE = new CustomizedFormat("nuid: :pname: :value");
    private static final CustomizedFormat KEY_WITH_PROPERTY_DESCRIPTION = new CustomizedFormat("nuid: :pname: :desc");
    private static final CustomizedFormat KEY_WITH_PROPERTY_PARAMETERIZED_1 = new CustomizedFormat("nuid: :pname: :prt1");
    private static final CustomizedFormat KEY_WITH_PROPERTY_PARAMETERIZED_2 = new CustomizedFormat("nuid: :pname: :prt2");
    private static final CustomizedFormat KEY_WITH_PROPERTY_TYPE = new CustomizedFormat("nuid: :pname: :type");
    private static final CustomizedFormat KEY_WITH_PARENT_UNIQUE_ID = new CustomizedFormat("nuid: :prt-uid");
    private static final CustomizedFormat KEY_WITH_NODE_ENTRY_NAME = new CustomizedFormat("nuid: :nname");

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
                uniqueIdsFromLocalOnes.addAll(listBytesToListString(jredis.smembers(SET_WITH_ALL_LOCAL_KEYS.format(localHash))));

            }
        }
        if (criteria.getCriteriaItems().size() == 0) {
            List<String> keys = jredis.keys(SET_WITH_ALL_NODE_KEYS_FOR_NAME.format(criteria.getNodeName()));
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
        if (!SearchType.EQUAL.equals(equal))
            throw new UnsupportedOperationException("Finding by " + equal + " isn't supported");


        ImmutableList.Builder<String> builder = ImmutableList.builder();
        String transientValueAsString = stripString(convert(value, String.class));
        List<String> ids = listBytesToListString(jredis.smembers(SET_WITH_INDEX_ENTRY.format(transientValueAsString,propertyName)));
        for (String id : ids) {
            String propertyValue = toStr(jredis.get(KEY_WITH_PROPERTY_VALUE.format(id, propertyName)));
            if(propertyValue==null) propertyValue = "null";
            boolean needsToAdd = false;
            switch (equal) {
                case EQUAL:
                    needsToAdd = propertyValue.equals(transientValueAsString);
                    break;

            }
            if (nodeEntryName != null && needsToAdd) {
                String name = toStr(jredis.get(KEY_WITH_NODE_ENTRY_NAME.format(id)));
                if (!nodeEntryName.equals(name)) {
                    needsToAdd = false;
                }
            }
            if (needsToAdd) {
                builder.add(id);
            }
        }


        return builder.build();
    }


    private STNodeEntry loadNodeOrReturnNull(String parentKey, STPartition partition) throws Exception {
        STUniqueKeyBuilder keyBuilder = null;
        JRedis jredis = factory.getFrom(partition);
        do {
            List<String> keyPropertyNames = listBytesToListString(jredis.smembers(SET_WITH_NODE_KEYS_NAMES.format(parentKey)));
            String nodeEntryName = toStr(jredis.get(KEY_WITH_NODE_ENTRY_NAME.format(parentKey)));
            if (nodeEntryName == null) break;

            keyBuilder = keyBuilder == null ? withPartition(partition).createKey(nodeEntryName) : keyBuilder.withParent(nodeEntryName);

            for (String keyName : keyPropertyNames) {
                String typeName = toStr(jredis.get(KEY_WITH_PROPERTY_TYPE.format(parentKey, keyName)));
                String typeValueAsString = toStr(jredis.get(KEY_WITH_PROPERTY_VALUE.format(parentKey, keyName)));
                Class<? extends Serializable> type = (Class<? extends Serializable>) findClassWithoutPrimitives(typeName);
                Serializable value = (Serializable) convert(typeValueAsString, type);
                keyBuilder.withEntry(keyName, type, value);
            }
            parentKey = toStr(jredis.get(KEY_WITH_PARENT_UNIQUE_ID.format(parentKey)));

        } while (parentKey != null);
        STNodeEntry nodeEntry;
        if (keyBuilder != null) {
            STUniqueKey uniqueKey = keyBuilder.andCreate();
            nodeEntry = createFoundEntryWithKey(uniqueKey);

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
        jredisExec.sadd(SET_WITH_ALL_NODE_KEYS_FOR_NAME.format(entry.getNodeEntryName()), uniqueKey);
        jredisExec.set(KEY_WITH_NODE_ENTRY_NAME.format(uniqueKey), entry.getNodeEntryName());
        STUniqueKey parentKey = entry.getUniqueKey().getParentKey();
        if (parentKey != null) {
            String parentAsString = supportMethods.getUniqueKeyAsStringHash(parentKey);
            jredisExec.set(KEY_WITH_PARENT_UNIQUE_ID.format(uniqueKey), parentAsString);
            jredisExec.sadd(SET_WITH_NODE_CHILDREN_KEYS.format(parentAsString), uniqueKey);
            jredisExec.sadd(SET_WITH_NODE_CHILDREN_NAMED_KEYS.format(parentAsString, entry.getNodeEntryName()), uniqueKey);
        }
        String localKey = supportMethods.getLocalKeyAsStringHash(entry.getLocalKey());
        jredisExec.sadd(SET_WITH_ALL_LOCAL_KEYS.format(localKey), uniqueKey);

        for (STKeyEntry<?> k : entry.getLocalKey().getEntries()) {
            internalFlushSimplePropertyAndCreateIndex(jredisExec,
                    partition, k.getPropertyName(),
                    k.getType(),
                    k.getValue(),
                    uniqueKey,
                    entry.getNodeEntryName(), STProperty.STPropertyDescription.KEY);


            jredisExec.sadd(SET_WITH_NODE_KEYS_NAMES.format(uniqueKey), k.getPropertyName());
            jredisExec.set(KEY_WITH_PROPERTY_TYPE.format(uniqueKey, k.getPropertyName()), findClassWithoutPrimitives(k.getType()).getName());
            String valueAsString = convert(k.getValue(), String.class);
            if (valueAsString != null)
                jredisExec.set(KEY_WITH_PROPERTY_VALUE.format(uniqueKey, k.getPropertyName()), valueAsString);
        }

    }

    @Override
    protected void flushRemovedItem(STPartition partition, STNodeEntry entry) throws Exception {
        JRedis jredis = factory.getFrom(partition);

        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(entry.getUniqueKey());
        jredis.srem(SET_WITH_ALL_KEYS, uniqueKey);
        jredis.srem(SET_WITH_ALL_NODE_KEYS_FOR_NAME.format(entry.getNodeEntryName()), uniqueKey);
        String dependentKeys = SET_WITH_ALL_DEPENDENT_KEYS.format(uniqueKey);

        List<String> keys = listBytesToListString(jredis.smembers(dependentKeys));

        for (String key : keys) {
            if (key.contains(uniqueKey)) {
                jredis.del(key);
            } else {
                List<String> possibleValues = listBytesToListString(jredis.smembers(key));
                for (String possibleValue : possibleValues) {
                    if (possibleValue.contains(uniqueKey)) {
                        jredis.srem(key, possibleValue);
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
        String keyName = name == null ? SET_WITH_NODE_CHILDREN_KEYS.format(parentKey) : SET_WITH_NODE_CHILDREN_NAMED_KEYS.format(parentKey, name);
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

        return jredis.exists(KEY_WITH_PROPERTY_TYPE.format(uniqueKey, stProperty.getPropertyName()));

    }

    @Override
    protected Class<?> internalPropertyDiscoverType(STPartition partition, STProperty stProperty) throws Exception {
        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(stProperty.getParent().getUniqueKey());
        JRedis jredis = factory.getFrom(partition);

        String typeName = toStr(jredis.get(KEY_WITH_PROPERTY_TYPE.format(uniqueKey, stProperty.getPropertyName())));

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
        return createFoundEntryWithKey(parentKey);
    }

    @Override
    protected <T> T internalPropertyGetKeyPropertyAs(STPartition partition, STProperty stProperty, Class<T> type) throws Exception {
        return this.<T>internalPropertyGetSimplePropertyAs(partition, stProperty, type);
    }

    @Override
    protected InputStream internalPropertyGetInputStreamProperty(STPartition partition, STProperty stProperty) throws Exception {
        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(stProperty.getParent().getUniqueKey());
        JRedis jredis = factory.getFrom(partition);

        byte[] fromStorage = jredis.get(KEY_WITH_PROPERTY_VALUE.format(uniqueKey, stProperty.getPropertyName()));

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

        String typeValueAsString = toStr(jredis.get(KEY_WITH_PROPERTY_VALUE.format(uniqueKey, stProperty.getPropertyName())));
        T value = convert(typeValueAsString, type);
        return value;
    }

    @Override
    protected Set<STProperty> internalNodeEntryLoadProperties(STPartition partition, STNodeEntry stNodeEntry) throws Exception {
        JRedis jredis = factory.getFrom(partition);
        Set<STProperty> result = newHashSet();

        String parentKey = supportMethods.getUniqueKeyAsStringHash(stNodeEntry.getUniqueKey());
        String properties = SET_WITH_NODE_PROPERTY_NAMES.format(parentKey);
        if (jredis.exists(properties)) {
            List<String> propertyNames = listBytesToListString(jredis.smembers(properties));
            for (String propertyName : propertyNames) {
                STProperty property = loadProperty(partition, stNodeEntry, parentKey, propertyName);
                if (property != null) result.add(property);

            }

        }
        return result;
    }

    @Override
    protected Set<STNodeEntry> internalFindNamed(STPartition partition, String nodeEntryName) throws Exception {

        JRedis jRedis = factory.getFrom(partition);
        List<String> ids = listBytesToListString(jRedis.smembers(SET_WITH_ALL_NODE_KEYS_FOR_NAME.format(nodeEntryName)));
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

        String typeName = toStr(jredis.get(KEY_WITH_PROPERTY_TYPE.format(parentKey, propertyName)));
        if (typeName == null) return null;
        String descriptionAsString = toStr(jredis.get(KEY_WITH_PROPERTY_DESCRIPTION.format(parentKey, propertyName)));
        if (descriptionAsString == null) return null;

        STProperty.STPropertyDescription description = STProperty.STPropertyDescription.valueOf(descriptionAsString);
        Class<?> type = findClassWithoutPrimitives(typeName);

        Class<?> parameterized1 = null;
        Class<?> parameterized2 = null;
        if (jredis.exists(KEY_WITH_PROPERTY_PARAMETERIZED_1.format(parentKey, propertyName))) {
            String typeName1 = toStr(jredis.get(KEY_WITH_PROPERTY_PARAMETERIZED_1.format(parentKey, propertyName)));
            parameterized1 = forName(typeName1);

        }
        if (jredis.exists(KEY_WITH_PROPERTY_PARAMETERIZED_2.format(parentKey, propertyName))) {
            String typeName2 = toStr(jredis.get(KEY_WITH_PROPERTY_PARAMETERIZED_2.format(parentKey, propertyName)));
            parameterized2 = forName(typeName2);

        }

        STProperty property = new STPropertyImpl(stNodeEntry, propertyName, description, type, parameterized1, parameterized2);
        if (description.getLoadWeight().equals(STProperty.STPropertyDescription.STLoadWeight.EASY)) {
            String typeValueAsString = toStr(jredis.get(KEY_WITH_PROPERTY_VALUE.format(parentKey, propertyName)));
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
        jredisExecution.sadd(SET_WITH_NODE_PROPERTY_NAMES.format(uniqueKey), dirtyProperty.getPropertyName());

        jredisExecution.set(KEY_WITH_PROPERTY_TYPE.format(uniqueKey, dirtyProperty.getPropertyName()), dirtyProperty.getInternalMethods().<Object>getPropertyType().getName());
        jredisExecution.set(KEY_WITH_PROPERTY_VALUE.format(uniqueKey, dirtyProperty.getPropertyName()), outputStream.toByteArray());
        jredisExecution.set(KEY_WITH_PROPERTY_DESCRIPTION.format(uniqueKey, dirtyProperty.getPropertyName()), convert(STProperty.STPropertyDescription.INPUT_STREAM, String.class));
        jredis.del(KEY_WITH_PROPERTY_PARAMETERIZED_1.format(uniqueKey, dirtyProperty.getPropertyName()));
        jredis.del(KEY_WITH_PROPERTY_PARAMETERIZED_2.format(uniqueKey, dirtyProperty.getPropertyName()));

    }

    @Override
    protected void internalFlushSerializedPojoProperty(STPartition partition, STProperty dirtyProperty) throws Exception {
        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(dirtyProperty.getParent().getUniqueKey());
        JRedis jredis = factory.getFrom(partition);
        JRedisLoggedExecution jredisExecution = new JRedisLoggedExecution(uniqueKey, jredis);
        flushStream(jredisExecution, partition, dirtyProperty, uniqueKey);

        jredisExecution.set(KEY_WITH_PROPERTY_DESCRIPTION.format(uniqueKey, dirtyProperty.getPropertyName()),
                convert(STProperty.STPropertyDescription.SERIALIZED_POJO, String.class));
        jredis.del(KEY_WITH_PROPERTY_PARAMETERIZED_1.format(uniqueKey, dirtyProperty.getPropertyName()));
        jredis.del(KEY_WITH_PROPERTY_PARAMETERIZED_2.format(uniqueKey, dirtyProperty.getPropertyName()));

    }

    private void flushStream(JRedisLoggedExecution jredisExecution, STPartition partition, STProperty dirtyProperty, String uniqueKey) throws Exception {
        Serializable value = dirtyProperty.getInternalMethods().<Serializable>getTransientValue();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(value);
        objectOutputStream.flush();
        JRedis jredis = factory.getFrom(partition);

        jredisExecution.sadd(SET_WITH_NODE_PROPERTY_NAMES.format(uniqueKey), dirtyProperty.getPropertyName());

        jredisExecution.set(KEY_WITH_PROPERTY_TYPE.format(uniqueKey, dirtyProperty.getPropertyName()), dirtyProperty.getInternalMethods().<Object>getPropertyType().getName());
        jredisExecution.set(KEY_WITH_PROPERTY_VALUE.format(uniqueKey, dirtyProperty.getPropertyName()), byteArrayOutputStream.toByteArray());
    }

    @Override
    protected void internalFlushSerializedMapProperty(STPartition partition, STProperty dirtyProperty) throws Exception {
        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(dirtyProperty.getParent().getUniqueKey());
        JRedis jredis = factory.getFrom(partition);
        JRedisLoggedExecution jredisExecution = new JRedisLoggedExecution(uniqueKey, jredis);
        flushStream(jredisExecution, partition, dirtyProperty, uniqueKey);
        jredisExecution.set(KEY_WITH_PROPERTY_DESCRIPTION.format(uniqueKey, dirtyProperty.getPropertyName()), convert(STProperty.STPropertyDescription.SERIALIZED_MAP, String.class));
        jredisExecution.set(KEY_WITH_PROPERTY_PARAMETERIZED_1.format(uniqueKey, dirtyProperty.getPropertyName()), dirtyProperty.getInternalMethods().<Object>getFirstParameterizedType().getName());
        jredisExecution.set(KEY_WITH_PROPERTY_PARAMETERIZED_2.format(uniqueKey, dirtyProperty.getPropertyName()), dirtyProperty.getInternalMethods().<Object>getSecondParameterizedType().getName());
    }

    @Override
    protected void internalFlushSerializedSetProperty(STPartition partition, STProperty dirtyProperty) throws Exception {
        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(dirtyProperty.getParent().getUniqueKey());
        JRedis jredis = factory.getFrom(partition);
        JRedisLoggedExecution jredisExecution = new JRedisLoggedExecution(uniqueKey, jredis);
        flushStream(jredisExecution, partition, dirtyProperty, uniqueKey);
        jredisExecution.set(KEY_WITH_PROPERTY_DESCRIPTION.format(uniqueKey, dirtyProperty.getPropertyName()), convert(STProperty.STPropertyDescription.SERIALIZED_SET, String.class));
        jredisExecution.set(KEY_WITH_PROPERTY_PARAMETERIZED_1.format(uniqueKey, dirtyProperty.getPropertyName()), dirtyProperty.getInternalMethods().<Object>getFirstParameterizedType().getName());
        jredis.del(KEY_WITH_PROPERTY_PARAMETERIZED_2.format(uniqueKey, dirtyProperty.getPropertyName()));

    }

    @Override
    protected void internalFlushSerializedListProperty(STPartition partition, STProperty dirtyProperty) throws Exception {
        String uniqueKey = supportMethods.getUniqueKeyAsStringHash(dirtyProperty.getParent().getUniqueKey());
        JRedis jredis = factory.getFrom(partition);
        JRedisLoggedExecution jredisExecution = new JRedisLoggedExecution(uniqueKey, jredis);
        flushStream(jredisExecution, partition, dirtyProperty, uniqueKey);
        jredisExecution.set(KEY_WITH_PROPERTY_DESCRIPTION.format(uniqueKey, dirtyProperty.getPropertyName()), convert(STProperty.STPropertyDescription.SERIALIZED_LIST, String.class));
        jredisExecution.set(KEY_WITH_PROPERTY_PARAMETERIZED_1.format(uniqueKey, dirtyProperty.getPropertyName()), dirtyProperty.getInternalMethods().<Object>getFirstParameterizedType().getName());
        jredis.del(KEY_WITH_PROPERTY_PARAMETERIZED_2.format(uniqueKey, dirtyProperty.getPropertyName()));

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
                dirtyProperty.getParent().getNodeEntryName(), dirtyProperty.getInternalMethods().getDescription());


    }

    private void internalFlushSimplePropertyAndCreateIndex(JRedisLoggedExecution exec, STPartition partition, String propertyName, Class<?> propertyType,
                                                           Object propertyValue, String uniqueKey, String nodeEntryName, STProperty.STPropertyDescription description) throws Exception {

        JRedis jredis = factory.getFrom(partition);


        String transientValueAsString = convert(propertyValue, String.class);
        exec.sadd(SET_WITH_NODE_PROPERTY_NAMES.format(uniqueKey), propertyName);
        String valueKey = KEY_WITH_PROPERTY_VALUE.format(uniqueKey, propertyName);
        if (description.isIndexed()) {
            String stripped = stripString(transientValueAsString);


            if (jredis.exists(valueKey)) {
                String existent = stripString(toStr(jredis.get(valueKey)));
                if (!existent.equals(stripped)) {
                    jredis.srem(SET_WITH_INDEX_ENTRY.format(existent, propertyName), uniqueKey);
                    exec.sadd(SET_WITH_INDEX_ENTRY.format(stripped, propertyName), uniqueKey);
                }
            } else {
                exec.sadd(SET_WITH_INDEX_ENTRY.format(stripped, propertyName), uniqueKey);
            }
        }
        exec.set(KEY_WITH_PROPERTY_TYPE.format(uniqueKey, propertyName), propertyType.getName());
        if (transientValueAsString != null) {
            exec.set(valueKey, transientValueAsString);
        } else {
            jredis.del(valueKey);
        }
        exec.set(KEY_WITH_PROPERTY_DESCRIPTION.format(uniqueKey, propertyName), convert(description, String.class));
//        jredis.del(KEY_WITH_PROPERTY_PARAMETERIZED_1, uniqueKey, dirtyProperty.getPropertyName()));
//        jredis.del(KEY_WITH_PROPERTY_PARAMETERIZED_2, uniqueKey, dirtyProperty.getPropertyName()));

    }

    private String stripString(String transientValueAsString) {
        return transientValueAsString == null ? "null" : transientValueAsString.replaceAll("[ ]|[\n]|[\t]|[\r]", "-");
    }
}
