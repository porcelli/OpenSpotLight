/**
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
 ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 *
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

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Sets.newHashSet;
import static org.jredis.ri.alphazero.support.DefaultCodec.toStr;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.storage.StringKeysSupport.getNodeType;
import static org.openspotlight.storage.StringKeysSupport.getPartition;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jredis.JRedis;
import org.openspotlight.common.CustomizedFormat;
import org.openspotlight.common.collection.IteratorBuilder;
import org.openspotlight.common.collection.IteratorBuilder.Converter;
import org.openspotlight.storage.NodeCriteria;
import org.openspotlight.storage.NodeCriteria.NodeCriteriaItem;
import org.openspotlight.storage.NodeCriteria.NodeCriteriaItem.CompositeKeyCriteriaItem;
import org.openspotlight.storage.NodeCriteria.NodeCriteriaItem.NodeKeyAsStringCriteriaItem;
import org.openspotlight.storage.NodeCriteria.NodeCriteriaItem.NodeKeyCriteriaItem;
import org.openspotlight.storage.NodeCriteria.NodeCriteriaItem.PropertyContainsString;
import org.openspotlight.storage.NodeCriteria.NodeCriteriaItem.PropertyCriteriaItem;
import org.openspotlight.storage.NodeCriteria.NodeCriteriaItem.PropertyEndsWithString;
import org.openspotlight.storage.NodeCriteria.NodeCriteriaItem.PropertyStartsWithString;
import org.openspotlight.storage.NodeKeyBuilder;
import org.openspotlight.storage.NodeKeyBuilderImpl;
import org.openspotlight.storage.Partition;
import org.openspotlight.storage.PartitionFactory;
import org.openspotlight.storage.StringKeysSupport;
import org.openspotlight.storage.domain.Property;
import org.openspotlight.storage.domain.PropertyContainer;
import org.openspotlight.storage.domain.PropertyImpl;
import org.openspotlight.storage.domain.StorageLink;
import org.openspotlight.storage.domain.StorageLinkImpl;
import org.openspotlight.storage.domain.StorageNode;
import org.openspotlight.storage.domain.StorageNodeImpl;
import org.openspotlight.storage.domain.key.NodeKey;
import org.openspotlight.storage.domain.key.NodeKey.CompositeKey.SimpleKey;
import org.openspotlight.storage.engine.StorageEngineBind;
import org.openspotlight.storage.redis.JRedisStorageSessionImpl.Nothing;
import org.openspotlight.storage.redis.guice.JRedisFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

/**
 * Created by User: feu - Date: Mar 23, 2010 - Time: 4:46:25 PM
 */

public class JRedisStorageSessionImpl implements StorageEngineBind<Nothing, Nothing> {

    public enum Nothing {
        NOTHING
    }

    private class JRedisLoggedExecution {
        private final JRedis jredis;
        private final String usedKeys;

        private final String uuid;

        private JRedisLoggedExecution(final String uuid, final JRedis jredis) {
            usedKeys = SET_WITH_ALL_DEPENDENT_KEYS.format(uuid);
            this.jredis = jredis;
            this.uuid = uuid;
        }

        public void sadd(final String s, final String o)
            throws Exception {
            jredis.sadd(s, o);
            if (!s.contains(uuid)) {
                jredis.sadd(usedKeys, s);
            }
        }

        public void set(final String s, final byte[] o)
            throws Exception {
            jredis.set(s, o);
            if (!s.contains(uuid)) {
                jredis.sadd(usedKeys, s);
            }
        }

        public void set(final String s, final String o)
            throws Exception {
            jredis.set(s, o);
            if (!s.contains(uuid)) {
                jredis.sadd(usedKeys, s);
            }
        }

        // public void sadd(final String s,
        // final byte[] o)
        // throws Exception {
        // jredis.sadd(s, o);
        // if (!s.contains(uuid)) {
        // jredis.sadd(usedKeys, s);
        // }
        // }
        //
        // public void sadd(final String s,
        // final Number o)
        // throws Exception {
        // jredis.sadd(s, o);
        // if (!s.contains(uuid)) {
        // jredis.sadd(usedKeys, s);
        // }
        // }
        //
        // public void sadd(final String s,
        // final Serializable o)
        // throws Exception {
        // jredis.sadd(s, o);
        // if (!s.contains(uuid)) {
        // jredis.sadd(usedKeys, s);
        // }
        // }
        //
        // public void set(final String s,
        // final Number o)
        // throws Exception {
        // jredis.set(s, o);
        // if (!s.contains(uuid)) {
        // jredis.sadd(usedKeys, s);
        // }
        // }
        //
        // public void set(final String s,
        // final Serializable o)
        // throws Exception {
        // jredis.set(s, o);
        // if (!s.contains(uuid)) {
        // jredis.sadd(usedKeys, s);
        // }
        // }
    }

    private static enum PropertyType {
        INDEXED(SET_WITH_NODE_PROPERTY_INDEXED_NAMES, true, false),
        KEY(SET_WITH_NODE_PROPERTY_KEY_NAMES, true, true),
        SIMPLE(SET_WITH_NODE_PROPERTY_SIMPLE_NAMES, false, false);

        private final CustomizedFormat f;
        private final boolean          indexed;

        private final boolean          key;

        private PropertyType(final CustomizedFormat f, final boolean indexed,
                             final boolean key) {
            this.f = f;
            this.key = key;
            this.indexed = indexed;
        }

        public PropertyImpl createProperty(final String name, final PropertyContainer parent) {
            if (isKey()) { return PropertyImpl.createKey(name, parent); }
            if (isIndexed()) { return PropertyImpl.createIndexed(name, parent); }
            return PropertyImpl.createSimple(name, parent);
        }

        public String getSetName(final String uniqueId) {
            return f.format(uniqueId);
        }

        public boolean isIndexed() {
            return indexed;
        }

        public boolean isKey() {
            return key;
        }

    }

    private static enum SearchType {
        EQUAL,
        STRING_CONTAINS,
        STRING_ENDS_WITH,
        STRING_STARTS_WITH
    }

    private static final String           SET_WITH_ALL_KEY_NAMES               = "names";
    private static final String           SET_WITH_ALL_KEYS                    = "uids";
    private static final CustomizedFormat KEY_WITH_PARENT_UNIQUE_ID            = new CustomizedFormat("nuid: :prt-uid");
    private static final CustomizedFormat KEY_WITH_PROPERTY_VALUE              = new CustomizedFormat("nuid: :pname: :value");
    private static final CustomizedFormat SET_WITH_ALL_DEPENDENT_KEYS          = new CustomizedFormat("nuid: :dependent-keys");
    private static final CustomizedFormat SET_WITH_ALL_LINKS_FOR_SOURCE        = new CustomizedFormat("lorigin: :uids");
    private static final CustomizedFormat SET_WITH_ALL_LINKS_FOR_TARGET        = new CustomizedFormat("ltarget: :uids");
    private static final CustomizedFormat SET_WITH_ALL_LINKS_FOR_TYPE          = new CustomizedFormat("ltype: :uids");
    private static final CustomizedFormat SET_WITH_ALL_LOCAL_KEYS              = new CustomizedFormat("lkeys: :uids");
    private static final CustomizedFormat SET_WITH_ALL_NODE_KEYS_FOR_TYPE      = new CustomizedFormat("type: :uids");
    private static final CustomizedFormat SET_WITH_INDEX_ENTRY                 = new CustomizedFormat("index: :pname: :uid");
    private static final CustomizedFormat SET_WITH_NODE_CHILDREN_KEYS          = new CustomizedFormat("nuid: :cld-uids");
    private static final CustomizedFormat SET_WITH_NODE_CHILDREN_NAMED_KEYS    = new CustomizedFormat("nuid: :nname: :cld-uids");
    private static final CustomizedFormat SET_WITH_NODE_PROPERTY_INDEXED_NAMES = new CustomizedFormat("nuid: :inames");
    private static final CustomizedFormat SET_WITH_NODE_PROPERTY_KEY_NAMES     = new CustomizedFormat("nuid: :knames");
    private static final CustomizedFormat SET_WITH_NODE_PROPERTY_SIMPLE_NAMES  = new CustomizedFormat("nuid: :pnames");

    private final JRedisFactory           factory;

    private final PartitionFactory        partitionFactory;

    @Inject
    public JRedisStorageSessionImpl(final JRedisFactory factory, final PartitionFactory partitionFactory) {
        this.factory = factory;
        this.partitionFactory = partitionFactory;
    }

    private static List<String> listBytesToListString(final List<byte[]> ids) {
        final List<String> idsAsString = newLinkedList();
        if (ids != null) {
            for (final byte[] b: ids) {
                final String s = toStr(b);
                idsAsString.add(s);
            }
        }
        return idsAsString;
    }

    private void flushRemoved(final PropertyContainer entry, final String entryName)
            throws Exception {
        final JRedis jredis = factory.getFrom(entry.getPartition());

        final String uniqueKey = entry.getKeyAsString();
        jredis.srem(SET_WITH_ALL_KEYS, uniqueKey);
        if (entryName != null) {
            jredis.srem(SET_WITH_ALL_NODE_KEYS_FOR_TYPE.format(entryName), uniqueKey);
        }
        final String simpleProperties = SET_WITH_NODE_PROPERTY_SIMPLE_NAMES.format(uniqueKey);
        final String keyProperties = SET_WITH_NODE_PROPERTY_KEY_NAMES.format(uniqueKey);
        final String indexedProperties = SET_WITH_NODE_PROPERTY_INDEXED_NAMES.format(uniqueKey);
        final List<String> allPropertyNames = newLinkedList();
        allPropertyNames.addAll(listBytesToListString(jredis.smembers(simpleProperties)));
        allPropertyNames.addAll(listBytesToListString(jredis.smembers(keyProperties)));
        allPropertyNames.addAll(listBytesToListString(jredis.smembers(indexedProperties)));
        for (final String s: allPropertyNames) {
            jredis.del(SET_WITH_NODE_CHILDREN_NAMED_KEYS.format(uniqueKey, s), KEY_WITH_PROPERTY_VALUE.format(uniqueKey, s));
        }
        jredis.del(simpleProperties, keyProperties, indexedProperties, SET_WITH_NODE_CHILDREN_KEYS.format(uniqueKey),
            KEY_WITH_PARENT_UNIQUE_ID.format(uniqueKey));

        final String dependentKeys = SET_WITH_ALL_DEPENDENT_KEYS.format(uniqueKey);
        final List<String> keys = listBytesToListString(jredis.smembers(dependentKeys));

        for (final String key: keys) {
            if (key.contains(uniqueKey)) {
                jredis.del(key);
            } else {
                final List<String> possibleValues = listBytesToListString(jredis.smembers(key));
                for (final String possibleValue: possibleValues) {
                    if (possibleValue.contains(uniqueKey)) {
                        jredis.srem(key, possibleValue);
                    }
                }

            }
        }
        jredis.del(dependentKeys);
    }

    private void internalFlushSimplePropertyAndCreateIndex(final JRedisLoggedExecution exec, final Partition partition,
                                                           final String propertyName, final byte[] propertyValue,
                                                           final String uniqueKey, final PropertyType propertyType)
            throws Exception {
        final JRedis jredis = factory.getFrom(partition);
        final String setName = propertyType.getSetName(uniqueKey);
        exec.sadd(setName, propertyName);
        final String valueKey = KEY_WITH_PROPERTY_VALUE.format(uniqueKey, propertyName);
        if (propertyType.isIndexed()) {
            final String stripped = stripString(propertyValue != null ? new String(propertyValue) : null);
            if (jredis.exists(valueKey)) {
                final String existent = stripString(toStr(jredis.get(valueKey)));
                if (!existent.equals(stripped)) {
                    jredis.srem(SET_WITH_INDEX_ENTRY.format(existent, propertyName), uniqueKey);
                    exec.sadd(SET_WITH_INDEX_ENTRY.format(stripped, propertyName), uniqueKey);
                }
            } else {
                exec.sadd(SET_WITH_INDEX_ENTRY.format(stripped, propertyName), uniqueKey);
            }
        }
        if (propertyValue != null) {
            exec.set(valueKey, propertyValue);
        } else {
            jredis.del(valueKey);
        }
    }

    private Collection<String> keysFromProperty(final JRedis jredis, final String nodeEntryName, final String propertyName,
                                                final SearchType equal, final String value)
        throws Exception {
        if (!SearchType.EQUAL.equals(equal)) { throw new UnsupportedOperationException("Finding by " + equal + " isn't supported"); }

        final ImmutableList.Builder<String> builder = ImmutableList.builder();
        final String transientValueAsString = stripString(value);
        final List<String> ids =
            listBytesToListString(jredis.smembers(SET_WITH_INDEX_ENTRY.format(transientValueAsString, propertyName)));
        for (final String id: ids) {
            String propertyValue = toStr(jredis.get(KEY_WITH_PROPERTY_VALUE.format(id, propertyName)));
            if (propertyValue == null) {
                propertyValue = "null";
            }
            boolean needsToAdd = false;
            switch (equal) {
                case EQUAL:
                    needsToAdd = stripString(propertyValue).equals(transientValueAsString);
                    break;
            }
            if (nodeEntryName != null && needsToAdd) {
                final String name = getNodeType(id);
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

    public StorageNode getNode(final String key)
        throws Exception, IllegalStateException {
        checkNotEmpty("key", key);

        Partition partition = StringKeysSupport.getPartition(key, partitionFactory);
        final JRedis jredis = factory.getFrom(partition);
        if (!jredis.sismember(SET_WITH_ALL_KEYS, key)) { return null; }

        NodeKeyBuilder keyBuilder = new NodeKeyBuilderImpl(getNodeType(key), partition);
        final List<String> keyPropertyNames =
            listBytesToListString(jredis.smembers(SET_WITH_NODE_PROPERTY_KEY_NAMES.format(key)));

        for (final String keyName: keyPropertyNames) {
            final String value = toStr(jredis.get(KEY_WITH_PROPERTY_VALUE.format(key, keyName)));
            keyBuilder.withSimpleKey(keyName, value);
        }
        final String parentKey = toStr(jredis.get(KEY_WITH_PARENT_UNIQUE_ID.format(key)));
        if (parentKey != null) {
            keyBuilder = keyBuilder.withParent(parentKey);
        }
        final NodeKey uniqueKey = keyBuilder.andCreate();
        final StorageNode nodeEntry = createFoundEntryWithKey(uniqueKey);

        return nodeEntry;
    }

    private Property loadProperty(final PropertyContainer node, final String key, final String propertyName,
                                  final PropertyType type)
            throws Exception {
        final JRedis jredis = factory.getFrom(node.getPartition());

        final Property property = type.createProperty(propertyName, node);
        if (type.isKey()) {
            final String value = toStr(jredis.get(KEY_WITH_PROPERTY_VALUE.format(key, propertyName)));
            ((PropertyImpl) property).setStringValueOnLoad(value);
        }
        return property;
    }

    private String stripString(final String transientValueAsString) {
        return transientValueAsString == null ? "null" : transientValueAsString.replaceAll("[ ]|[\n]|[\t]|[\r]", "-");
    }

    public final StorageNode createFoundEntryWithKey(final NodeKey uniqueKey) {
        return new StorageNodeImpl(uniqueKey, true);
    }

    @Override
    public Nothing createLinkReference(final StorageLink link)
        throws IllegalStateException {
        checkNotNull("link", link);
        return Nothing.NOTHING;
    }

    @Override
    public Nothing createNodeReference(final StorageNode node)
        throws IllegalStateException {
        checkNotNull("node", node);

        return Nothing.NOTHING;
    }

    @Override
    public void persistNode(final Nothing reference, final StorageNode node)
            throws Exception, IllegalStateException {
        checkNotNull("reference", reference);
        checkNotNull("node", node);

        final String uniqueKey = node.getKey().getKeyAsString();
        final JRedis jredis = factory.getFrom(node.getPartition());
        final JRedisLoggedExecution jredisExec = new JRedisLoggedExecution(uniqueKey, jredis);
        jredisExec.sadd(SET_WITH_ALL_KEYS, uniqueKey);
        jredisExec.sadd(SET_WITH_ALL_NODE_KEYS_FOR_TYPE.format(node.getType()), uniqueKey);
        jredis.sadd(SET_WITH_ALL_KEY_NAMES, node.getType());
        final String parentKeyAsString = node.getKey().getParentKeyAsString();
        if (parentKeyAsString != null) {
            jredisExec.set(KEY_WITH_PARENT_UNIQUE_ID.format(uniqueKey), parentKeyAsString);

            jredisExec.sadd(SET_WITH_NODE_CHILDREN_KEYS.format(parentKeyAsString), uniqueKey);
            jredisExec.sadd(SET_WITH_NODE_CHILDREN_NAMED_KEYS.format(parentKeyAsString, node.getType()), uniqueKey);
        }
        final String localKey = node.getKey().getCompositeKey().getKeyAsString();
        jredisExec.sadd(SET_WITH_ALL_LOCAL_KEYS.format(localKey), uniqueKey);
        for (final SimpleKey k: node.getKey().getCompositeKey().getKeys()) {
            internalFlushSimplePropertyAndCreateIndex(jredisExec, node.getPartition(), k.getKeyName(),
                k.getValue() != null ? k.getValue().getBytes() : null, uniqueKey, PropertyType.KEY);
        }
    }

    @Override
    public void deleteNode(final StorageNode node)
        throws Exception, IllegalArgumentException {
        checkNotNull("node", node);

        flushRemoved(node, node.getType());
    }

    @Override
    public void deleteLink(final StorageLink link)
        throws Exception, IllegalStateException {
        checkNotNull("link", link);

        final JRedis jredis = factory.getFrom(link.getPartition());
        jredis.srem(SET_WITH_ALL_LINKS_FOR_TYPE.format(link.getType()), link.getKeyAsString());
        jredis.srem(SET_WITH_ALL_LINKS_FOR_SOURCE.format(link.getSource().getKeyAsString()), link.getKeyAsString());
        jredis.srem(SET_WITH_ALL_LINKS_FOR_TARGET.format(link.getTarget().getKeyAsString()), link.getKeyAsString());
        flushRemoved(link, null);
    }

    @Override
    public void persistLink(final StorageLink link)
        throws Exception, IllegalStateException {
        checkNotNull("link", link);

        final JRedis jredis = factory.getFrom(link.getSource().getPartition());
        jredis.sadd(SET_WITH_ALL_LINKS_FOR_TYPE.format(link.getType()), link.getKeyAsString());
        jredis.sadd(SET_WITH_ALL_LINKS_FOR_SOURCE.format(link.getSource().getKeyAsString()), link.getKeyAsString());
        jredis.sadd(SET_WITH_ALL_LINKS_FOR_TARGET.format(link.getTarget().getKeyAsString()), link.getKeyAsString());
    }

    @Override
    public Set<StorageNode> search(final NodeCriteria criteria)
            throws Exception, IllegalStateException {
        checkNotNull("criteria", criteria);

        final List<String> propertiesIntersection = newLinkedList();
        final List<String> uniqueIdsFromLocalOnes = newLinkedList();
        boolean first = true;
        final List<String> uniqueIds = newLinkedList();
        final JRedis jredis = factory.getFrom(criteria.getPartition());
        for (final NodeCriteriaItem c: criteria.getCriteriaItems()) {
            if (c instanceof PropertyCriteriaItem) {
                final PropertyCriteriaItem p = (PropertyCriteriaItem) c;
                if (first) {
                    propertiesIntersection.addAll(keysFromProperty(jredis, p.getNodeType(), p.getPropertyName(),
                        SearchType.EQUAL, p.getValue()));
                    first = false;
                } else {
                    propertiesIntersection.retainAll(keysFromProperty(jredis, p.getNodeType(), p.getPropertyName(),
                        SearchType.EQUAL, p.getValue()));
                }
            }
            if (c instanceof PropertyContainsString) {
                final PropertyContainsString p = (PropertyContainsString) c;
                if (first) {
                    propertiesIntersection.addAll(keysFromProperty(jredis, p.getNodeType(), p.getPropertyName(),
                        SearchType.STRING_CONTAINS, p.getValue()));
                    first = false;
                } else {
                    propertiesIntersection.retainAll(keysFromProperty(jredis, p.getNodeType(), p.getPropertyName(),
                        SearchType.STRING_CONTAINS, p.getValue()));
                }
            }
            if (c instanceof PropertyStartsWithString) {
                final PropertyStartsWithString p = (PropertyStartsWithString) c;
                if (first) {
                    propertiesIntersection.addAll(keysFromProperty(jredis, p.getNodeType(), p.getPropertyName(),
                        SearchType.STRING_STARTS_WITH, p.getValue()));
                    first = false;
                } else {
                    propertiesIntersection.retainAll(keysFromProperty(jredis, p.getNodeType(), p.getPropertyName(),
                        SearchType.STRING_STARTS_WITH, p.getValue()));
                }
            }
            if (c instanceof PropertyEndsWithString) {
                final PropertyEndsWithString p = (PropertyEndsWithString) c;
                if (first) {
                    propertiesIntersection.addAll(keysFromProperty(jredis, p.getNodeType(), p.getPropertyName(),
                        SearchType.STRING_ENDS_WITH, p.getValue()));
                    first = false;
                } else {
                    propertiesIntersection.retainAll(keysFromProperty(jredis, p.getNodeType(), p.getPropertyName(),
                        SearchType.STRING_ENDS_WITH, p.getValue()));
                }
            }
            if (c instanceof NodeKeyCriteriaItem) {
                final NodeKeyCriteriaItem uniqueCriteria = (NodeKeyCriteriaItem) c;
                uniqueIds.add(uniqueCriteria.getValue().getKeyAsString());
            }
            if (c instanceof NodeKeyAsStringCriteriaItem) {
                final NodeKeyAsStringCriteriaItem uniqueCriteria = (NodeKeyAsStringCriteriaItem) c;
                uniqueIds.add(uniqueCriteria.getKeyAsString());
            }
            if (c instanceof CompositeKeyCriteriaItem) {
                final CompositeKeyCriteriaItem uniqueCriteria = (CompositeKeyCriteriaItem) c;
                final String localHash = uniqueCriteria.getValue().getKeyAsString();
                uniqueIdsFromLocalOnes.addAll(listBytesToListString(jredis.smembers(SET_WITH_ALL_LOCAL_KEYS.format(localHash))));
            }
        }
        if (criteria.getCriteriaItems().size() == 0) {
            final List<String> keys =
                listBytesToListString(jredis.smembers(SET_WITH_ALL_NODE_KEYS_FOR_TYPE.format(criteria.getNodeType())));
            uniqueIds.addAll(keys);
        }

        if (!uniqueIds.isEmpty() && !propertiesIntersection.isEmpty()) { throw new IllegalArgumentException(
            "criteria with unique ids can't be used with other criteria types"); }

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

        final Set<StorageNode> nodeEntries = newHashSet();
        for (final String id: ids) {
            final StorageNode nodeEntry = getNode(id);
            if (nodeEntry != null) {
                nodeEntries.add(nodeEntry);
            }
        }

        return ImmutableSet.copyOf(nodeEntries);
    }

    @Override
    public Set<StorageNode> getNodes(final Partition partition, final String type)
        throws Exception, IllegalStateException {
        checkNotNull("partition", partition);
        checkNotEmpty("type", type);

        final JRedis jRedis = factory.getFrom(partition);
        final List<String> ids = listBytesToListString(jRedis.smembers(SET_WITH_ALL_NODE_KEYS_FOR_TYPE.format(type)));
        final ImmutableSet.Builder<StorageNode> builder = ImmutableSet.<StorageNode>builder();
        for (final String id: ids) {
            final StorageNode loadedNode = getNode(id);
            if (loadedNode != null) {
                builder.add(loadedNode);
            }
        }
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterable<StorageLink> getLinks(final StorageNode source, final StorageNode target, final String type)
        throws Exception, IllegalStateException {
        checkNotNull("source", source);

        final JRedis jredis = factory.getFrom(source.getPartition());
        final List<String> linkIds =
            listBytesToListString(jredis.smembers(SET_WITH_ALL_LINKS_FOR_SOURCE.format(source.getKeyAsString())));
        if (target != null) {
            final List<String> newIds =
                listBytesToListString(jredis.smembers(SET_WITH_ALL_LINKS_FOR_TARGET.format(target.getKeyAsString())));
            linkIds.retainAll(newIds);
        }
        if (type != null) {
            final List<String> newIds = listBytesToListString(jredis.smembers(SET_WITH_ALL_LINKS_FOR_TYPE.format(type)));
            linkIds.retainAll(newIds);
        }
        if (linkIds.size() == 0) { return Collections.emptyList(); }
        return IteratorBuilder.<StorageLink, String>createIteratorBuilder().withItems(linkIds)
                .withConverter(new Converter<StorageLink, String>() {
                    @Override
                    public StorageLink convert(final String o)
                        throws Exception {
                        final StorageNode originNode =
                            JRedisStorageSessionImpl.this.getNode(StringKeysSupport.getOriginKeyAsStringFromLinkKey(o));
                        final String targetId = StringKeysSupport.getTargeyKeyAsStringFromLinkKey(o);
                        final StorageNode targetNode = JRedisStorageSessionImpl.this.getNode(targetId);

                        return new StorageLinkImpl(StringKeysSupport.getLinkTypeFromLinkKey(o), originNode, targetNode, true);
                    }
                }).andBuild();
    }

    @Override
    public void setNodeProperty(Nothing reference, Property property)
        throws Exception, IllegalStateException {
        flushSimpleProperty(reference, property);
    }

    @Override
    public void setLinkProperty(Nothing reference, Property property)
        throws Exception, IllegalStateException {
        flushSimpleProperty(reference, property);
    }

    private void flushSimpleProperty(final Nothing reference, final Property property)
            throws Exception, IllegalStateException {
        checkNotNull("property", property);

        final JRedis jredis = factory.getFrom(property.getParent().getPartition());
        final String uniqueKey = property.getParent().getKeyAsString();
        final JRedisLoggedExecution jredisExecution = new JRedisLoggedExecution(uniqueKey, jredis);
        final PropertyType type =
            property.isKey() ? PropertyType.KEY : (property.isIndexed() ? PropertyType.INDEXED : PropertyType.SIMPLE);

        internalFlushSimplePropertyAndCreateIndex(jredisExecution, property.getParent().getPartition(),
            property.getPropertyName(), ((PropertyImpl) property).getTransientValueAsBytes(), uniqueKey, type);
    }

    @Override
    public Iterable<String> getAllNodeTypes(final Partition partition)
            throws Exception, IllegalStateException {
        checkNotNull("partition", partition);

        final JRedis jredis = factory.getFrom(partition);
        return listBytesToListString(jredis.smembers(SET_WITH_ALL_KEY_NAMES));
    }

    @Override
    public Set<StorageNode> getChildren(final Partition partition, final StorageNode node)
            throws Exception, IllegalStateException {
        checkNotNull("partition", partition);
        checkNotNull("node", node);

        return internalGetChildren(partition, node, null);
    }

    @Override
    public Set<StorageNode> getChildren(final Partition partition, final StorageNode node, final String type)
        throws Exception, IllegalStateException {
        checkNotNull("partition", partition);
        checkNotNull("node", node);
        checkNotEmpty("type", type);

        return internalGetChildren(partition, node, type);
    }

    private Set<StorageNode> internalGetChildren(final Partition partition, final StorageNode node, final String type)
        throws Exception {
        final JRedis jredis = factory.getFrom(node.getPartition());

        final String parentKey = node.getKey().getKeyAsString();
        final String keyName =
            type == null ? SET_WITH_NODE_CHILDREN_KEYS.format(parentKey) : SET_WITH_NODE_CHILDREN_NAMED_KEYS.format(parentKey,
                type);
        final List<String> childrenKeys = listBytesToListString(jredis.smembers(keyName));
        final ImmutableSet.Builder<StorageNode> builder = ImmutableSet.builder();
        for (final String id: childrenKeys) {
            final Partition childPartition = getPartition(id, partitionFactory);
            if (partition.equals(childPartition)) {
                final StorageNode loadedNode = getNode(id);
                if (loadedNode != null) {
                    builder.add(loadedNode);
                }
            }
        }
        return builder.build();
    }

    @Override
    public StorageNode getParent(final StorageNode node)
        throws Exception, IllegalStateException {
        checkNotNull("node", node);

        final String parentKeyAsString = node.getKey().getParentKeyAsString();
        if (parentKeyAsString == null) { return null; }
        return getNode(parentKeyAsString);
    }

    @Override
    public Set<Property> getProperties(final PropertyContainer element)
        throws Exception, IllegalStateException {
        checkNotNull("element", element);

        final JRedis jredis = factory.getFrom(element.getPartition());
        final Set<Property> result = newHashSet();

        final String parentKey = element.getKeyAsString();
        for (final PropertyType type: PropertyType.values()) {
            final String properties = type.getSetName(parentKey);
            if (jredis.exists(properties)) {
                final List<String> propertyNames = listBytesToListString(jredis.smembers(properties));
                for (final String propertyName: propertyNames) {
                    final Property property = loadProperty(element, parentKey, propertyName, type);
                    if (property != null) {
                        result.add(property);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public byte[] getPropertyValue(final Property property)
        throws Exception, IllegalStateException {
        checkNotNull("property", property);

        final JRedis jredis = factory.getFrom(property.getParent().getPartition());
        final String uniqueKey = property.getParent().getKeyAsString();
        final byte[] propertyValue = jredis.get(KEY_WITH_PROPERTY_VALUE.format(uniqueKey, property.getPropertyName()));
        return propertyValue;
    }

    @Override
    public void save(final Partition... partitions)
            throws Exception {

        for (final Partition p: partitions) {
            factory.getFrom(p).save();
        }
    }

    @Override
    public void closeResources() {
        factory.closeResources();
    }

}
