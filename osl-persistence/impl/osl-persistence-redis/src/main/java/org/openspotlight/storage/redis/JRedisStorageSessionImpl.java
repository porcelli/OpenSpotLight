/*
 * OpenSpotLight - Open Source IT Governance Platform Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA or third-party contributors as indicated by the @author tags or express copyright attribution statements applied by the
 * authors. All third-party contributions are distributed under license by CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA. This copyrighted material is made available to anyone wishing to use, modify, copy, or redistribute it subject to the
 * terms and conditions of the GNU Lesser General Public License, as published by the Free Software Foundation. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a
 * copy of the GNU Lesser General Public License along with this distribution; if not, write to: Free Software Foundation, Inc. 51
 * Franklin Street, Fifth Floor Boston, MA 02110-1301 USA ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto * Direitos Autorais Reservados (c) 2009, CARAVELATECH
 * CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor. Todas as contribuições de terceiros
 * estão distribuídas sob licença da CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. Este programa é software livre;
 * você pode redistribuí-lo e/ou modificá-lo sob os termos da Licença Pública Geral Menor do GNU conforme publicada pela Free
 * Software Foundation. Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA GARANTIA; nem mesmo a
 * garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU
 * para mais detalhes. Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este programa; se não,
 * escreva para: Free Software Foundation, Inc. 51 Franklin Street, Fifth Floor Boston, MA 02110-1301 USA
 */

package org.openspotlight.storage.redis;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Sets.newHashSet;
import static org.jredis.ri.alphazero.support.DefaultCodec.toStr;
import static org.openspotlight.storage.StringIDSupport.getNodeEntryName;
import static org.openspotlight.storage.StringIDSupport.getPartition;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jredis.JRedis;
import org.openspotlight.common.CustomizedFormat;
import org.openspotlight.common.collection.IteratorBuilder;
import org.openspotlight.common.collection.IteratorBuilder.Converter;
import org.openspotlight.storage.AbstractStorageSession;
import org.openspotlight.storage.Criteria;
import org.openspotlight.storage.Criteria.CriteriaItem;
import org.openspotlight.storage.Criteria.CriteriaItem.CompisiteKeyCriteriaItem;
import org.openspotlight.storage.Criteria.CriteriaItem.NodeKeyAsStringCriteriaItem;
import org.openspotlight.storage.Criteria.CriteriaItem.NodeKeyCriteriaItem;
import org.openspotlight.storage.Criteria.CriteriaItem.PropertyContainsString;
import org.openspotlight.storage.Criteria.CriteriaItem.PropertyCriteriaItem;
import org.openspotlight.storage.Criteria.CriteriaItem.PropertyEndsWithString;
import org.openspotlight.storage.Criteria.CriteriaItem.PropertyStartsWithString;
import org.openspotlight.storage.Partition;
import org.openspotlight.storage.PartitionFactory;
import org.openspotlight.storage.RepositoryPath;
import org.openspotlight.storage.StringIDSupport;
import org.openspotlight.storage.domain.StorageLink;
import org.openspotlight.storage.domain.StorageNode;
import org.openspotlight.storage.domain.Property;
import org.openspotlight.storage.domain.PropertyContainer;
import org.openspotlight.storage.domain.key.NodeKey;
import org.openspotlight.storage.domain.key.NodeKey.CompositeKey.SimpleKey;
import org.openspotlight.storage.domain.node.StorageLinkImpl;
import org.openspotlight.storage.domain.node.StorageNodeImpl;
import org.openspotlight.storage.domain.node.PropertyImpl;
import org.openspotlight.storage.redis.guice.JRedisFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

/**
 * Created by User: feu - Date: Mar 23, 2010 - Time: 4:46:25 PM
 */

enum Nothing {
    NOTHING
}

public class JRedisStorageSessionImpl extends
        AbstractStorageSession<Nothing> {

    private final PartitionFactory partitionFactory;

    private static enum PropertyType {
        SIMPLE(SET_WITH_NODE_PROPERTY_SIMPLE_NAMES, false, false),
        INDEXED(
                SET_WITH_NODE_PROPERTY_INDEXED_NAMES, true, false),
        KEY(
                SET_WITH_NODE_PROPERTY_KEY_NAMES, true, true);

        private final boolean key;
        private final boolean indexed;

        public boolean isKey() {
            return key;
        }

        public boolean isIndexed() {
            return indexed;
        }

        private PropertyType(final CustomizedFormat f, final boolean indexed, final boolean key) {
            this.f = f;
            this.key = key;
            this.indexed = indexed;
        }

        private final CustomizedFormat f;

        public String getSetName(final String uniqueId) {
            return f.format(uniqueId);
        }

        public PropertyImpl createProperty(final String name,
                                              final PropertyContainer parent) {
            if (isKey()) { return PropertyImpl.createKey(name, parent); }
            if (isIndexed()) { return PropertyImpl.createIndexed(name, parent); }
            return PropertyImpl.createSimple(name, parent);

        }

    }

    private class JRedisLoggedExecution {
        private final String usedKeys;
        private final String uuid;

        private final JRedis jredis;

        private JRedisLoggedExecution(final String uuid, final JRedis jredis) {
            usedKeys = SET_WITH_ALL_DEPENDENT_KEYS.format(uuid);
            this.jredis = jredis;
            this.uuid = uuid;
        }

        public void sadd(final String s,
                          final String o)
            throws Exception {
            jredis.sadd(s, o);
            if (!s.contains(uuid)) {
                jredis.sadd(usedKeys, s);
            }
        }

        public void set(final String s,
                         final String o)
            throws Exception {
            jredis.set(s, o);
            if (!s.contains(uuid)) {
                jredis.sadd(usedKeys, s);
            }
        }

        public void set(final String s,
                        final byte[] o)
            throws Exception {
            jredis.set(s, o);
            if (!s.contains(uuid)) {
                jredis.sadd(usedKeys, s);
            }
        }

        //        public void sadd(final String s,
        //                         final byte[] o)
        //            throws Exception {
        //            jredis.sadd(s, o);
        //            if (!s.contains(uuid)) {
        //                jredis.sadd(usedKeys, s);
        //            }
        //        }
        //
        //        public void sadd(final String s,
        //                         final Number o)
        //            throws Exception {
        //            jredis.sadd(s, o);
        //            if (!s.contains(uuid)) {
        //                jredis.sadd(usedKeys, s);
        //            }
        //        }
        //
        //        public void sadd(final String s,
        //                          final Serializable o)
        //            throws Exception {
        //            jredis.sadd(s, o);
        //            if (!s.contains(uuid)) {
        //                jredis.sadd(usedKeys, s);
        //            }
        //        }
        //
        //        public void set(final String s,
        //                         final Number o)
        //            throws Exception {
        //            jredis.set(s, o);
        //            if (!s.contains(uuid)) {
        //                jredis.sadd(usedKeys, s);
        //            }
        //        }
        //
        //        public void set(final String s,
        //                         final Serializable o)
        //            throws Exception {
        //            jredis.set(s, o);
        //            if (!s.contains(uuid)) {
        //                jredis.sadd(usedKeys, s);
        //            }
        //        }
    }

    private static final CustomizedFormat SET_WITH_ALL_LINKS_FOR_TYPE          = new CustomizedFormat(
                                                                                                      "ltype: :uids");
    private static final CustomizedFormat SET_WITH_ALL_LINKS_FOR_ORIGIN        = new CustomizedFormat(
                                                                                                      "lorigin: :uids");
    private static final CustomizedFormat SET_WITH_ALL_LINKS_FOR_TARGET        = new CustomizedFormat(
                                                                                                      "ltarget: :uids");

    private static final CustomizedFormat SET_WITH_INDEX_ENTRY                 = new CustomizedFormat(
                                                                                                      "index: :pname: :uid");
    private static final CustomizedFormat SET_WITH_ALL_DEPENDENT_KEYS          = new CustomizedFormat(
                                                                                                      "nuid: :dependent-keys");
    private static final String           SET_WITH_ALL_KEYS                    = "uids";
    private static final String           SET_WITH_ALL_KEY_NAMES               = "names";
    private static final CustomizedFormat SET_WITH_ALL_NODE_KEYS_FOR_TYPE      = new CustomizedFormat(
                                                                                                      "type: :uids");
    private static final CustomizedFormat SET_WITH_ALL_LOCAL_KEYS              = new CustomizedFormat(
                                                                                                      "lkeys: :uids");
    private static final CustomizedFormat SET_WITH_NODE_PROPERTY_KEY_NAMES     = new CustomizedFormat(
                                                                                                      "nuid: :knames");
    private static final CustomizedFormat SET_WITH_NODE_PROPERTY_SIMPLE_NAMES  = new CustomizedFormat(
                                                                                                      "nuid: :pnames");
    private static final CustomizedFormat SET_WITH_NODE_PROPERTY_INDEXED_NAMES = new CustomizedFormat(
                                                                                                      "nuid: :inames");
    private static final CustomizedFormat SET_WITH_NODE_CHILDREN_KEYS          = new CustomizedFormat(
                                                                                                      "nuid: :cld-uids");
    private static final CustomizedFormat SET_WITH_NODE_CHILDREN_NAMED_KEYS    = new CustomizedFormat(
                                                                                                      "nuid: :nname: :cld-uids");
    private static final CustomizedFormat KEY_WITH_PROPERTY_VALUE              = new CustomizedFormat(
                                                                                                      "nuid: :pname: :value");
    private static final CustomizedFormat KEY_WITH_PARENT_UNIQUE_ID            = new CustomizedFormat(
                                                                                                      "nuid: :prt-uid");

    private static enum SearchType {
        EQUAL,
        STRING_STARTS_WITH,
        STRING_ENDS_WITH,
        STRING_CONTAINS
    }

    private final JRedisFactory factory;

    @Inject
    public JRedisStorageSessionImpl(final FlushMode flushMode,
                                       final JRedisFactory factory, final RepositoryPath repositoryPath,
                                       final PartitionFactory partitionFactory) {
        super(flushMode, repositoryPath, partitionFactory);
        this.factory = factory;
        this.partitionFactory = partitionFactory;
    }

    @Override
    protected Nothing createNodeReferenceIfNecessary(final Partition partition,
                                                      final StorageNode entry) {
        return Nothing.NOTHING;
    }

    @Override
    protected byte[] internalPropertyGetValue(final Partition partition,
                                               final Property stProperty)
        throws Exception {
        final JRedis jredis = factory.getFrom(partition);
        final String uniqueKey = stProperty.getParent().getKeyAsString();
        final byte[] propertyValue = jredis.get(KEY_WITH_PROPERTY_VALUE.format(
                                                                         uniqueKey, stProperty.getPropertyName()));
        return propertyValue;

    }

    @Override
    protected void internalSavePartitions(final Partition... partitions)
            throws Exception {
        for (final Partition p: partitions) {
            factory.getFrom(p).save();
        }
    }

    @Override
    protected Set<StorageNode> internalFindByCriteria(final Partition partition,
                                                       final Criteria criteria)
        throws Exception {
        final List<String> propertiesIntersection = newLinkedList();
        final List<String> uniqueIdsFromLocalOnes = newLinkedList();
        boolean first = true;
        final List<String> uniqueIds = newLinkedList();
        final JRedis jredis = factory.getFrom(partition);
        for (final CriteriaItem c: criteria.getCriteriaItems()) {
            if (c instanceof PropertyCriteriaItem) {
                final PropertyCriteriaItem p = (PropertyCriteriaItem) c;
                if (first) {
                    propertiesIntersection.addAll(keysFromProperty(jredis, p
                                                                            .getNodeType(), p.getPropertyName(),
                                                                   SearchType.EQUAL, p.getValue()));
                    first = false;
                } else {
                    propertiesIntersection.retainAll(keysFromProperty(jredis, p
                                                                               .getNodeType(), p.getPropertyName(),
                                                                      SearchType.EQUAL, p.getValue()));
                }
            }
            if (c instanceof PropertyContainsString) {
                final PropertyContainsString p = (PropertyContainsString) c;
                if (first) {
                    propertiesIntersection.addAll(keysFromProperty(jredis, p
                                                                            .getNodeType(), p.getPropertyName(),
                                                                   SearchType.STRING_CONTAINS, p.getValue()));
                    first = false;
                } else {
                    propertiesIntersection.retainAll(keysFromProperty(jredis, p
                                                                               .getNodeType(), p.getPropertyName(),
                                                                      SearchType.STRING_CONTAINS, p.getValue()));
                }
            }
            if (c instanceof PropertyStartsWithString) {
                final PropertyStartsWithString p = (PropertyStartsWithString) c;
                if (first) {
                    propertiesIntersection.addAll(keysFromProperty(jredis, p
                                                                            .getNodeType(), p.getPropertyName(),
                                                                   SearchType.STRING_STARTS_WITH, p.getValue()));
                    first = false;
                } else {
                    propertiesIntersection.retainAll(keysFromProperty(jredis, p
                                                                               .getNodeType(), p.getPropertyName(),
                                                                      SearchType.STRING_STARTS_WITH, p.getValue()));
                }
            }
            if (c instanceof PropertyEndsWithString) {
                final PropertyEndsWithString p = (PropertyEndsWithString) c;
                if (first) {
                    propertiesIntersection.addAll(keysFromProperty(jredis, p
                                                                            .getNodeType(), p.getPropertyName(),
                                                                   SearchType.STRING_ENDS_WITH, p.getValue()));
                    first = false;
                } else {
                    propertiesIntersection.retainAll(keysFromProperty(jredis, p
                                                                               .getNodeType(), p.getPropertyName(),
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
            if (c instanceof CompisiteKeyCriteriaItem) {
                final CompisiteKeyCriteriaItem uniqueCriteria = (CompisiteKeyCriteriaItem) c;
                final String localHash = uniqueCriteria.getValue().getKeyAsString();
                uniqueIdsFromLocalOnes.addAll(listBytesToListString(jredis
                                                                          .smembers(SET_WITH_ALL_LOCAL_KEYS.format(localHash))));

            }
        }
        if (criteria.getCriteriaItems().size() == 0) {
            final List<String> keys =
                listBytesToListString(jredis
                                                            .smembers(SET_WITH_ALL_NODE_KEYS_FOR_TYPE.format(criteria
                                                                                                                     .getNodeType())));
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
            final StorageNode nodeEntry = loadNodeOrReturnNull(id, criteria
                                                                     .getPartition());
            if (nodeEntry != null) {
                nodeEntries.add(nodeEntry);
            }
        }

        return ImmutableSet.copyOf(nodeEntries);
    }

    private Collection<String> keysFromProperty(final JRedis jredis,
                                                 final String nodeEntryName,
                                                 final String propertyName,
                                                 final SearchType equal,
                                                 final String value)
        throws Exception {
        if (!SearchType.EQUAL.equals(equal)) { throw new UnsupportedOperationException("Finding by " + equal
                                                    + " isn't supported"); }

        final ImmutableList.Builder<String> builder = ImmutableList.builder();
        final String transientValueAsString = stripString(value);
        final List<String> ids = listBytesToListString(jredis
                                                       .smembers(SET_WITH_INDEX_ENTRY.format(transientValueAsString,
                                                                                             propertyName)));
        for (final String id: ids) {
            String propertyValue = toStr(jredis.get(KEY_WITH_PROPERTY_VALUE
                                                                           .format(id, propertyName)));
            if (propertyValue == null) {
                propertyValue = "null";
            }
            boolean needsToAdd = false;
            switch (equal) {
                case EQUAL:
                    needsToAdd = stripString(propertyValue).equals(
                                                                   transientValueAsString);
                    break;

            }
            if (nodeEntryName != null && needsToAdd) {

                final String name = getNodeEntryName(id);
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

    private StorageNode loadNodeOrReturnNull(final String firstParentKey,
                                              final Partition firstPartition)
        throws Exception {

        final JRedis jredis = factory.getFrom(firstPartition);
        if (!jredis.sismember(SET_WITH_ALL_KEYS, firstParentKey)) { return null; }
        NodeKeyBuilder keyBuilder = withPartition(firstPartition)
                                                                     .createKey(getNodeEntryName(firstParentKey));
        final List<String> keyPropertyNames =
            listBytesToListString(jredis
                                                                    .smembers(SET_WITH_NODE_PROPERTY_KEY_NAMES
                                                                                                              .format(firstParentKey)));

        for (final String keyName: keyPropertyNames) {
            final String value = toStr(jredis.get(KEY_WITH_PROPERTY_VALUE.format(
                                                                           firstParentKey, keyName)));
            keyBuilder.withSimpleKey(keyName, value);
        }
        final String parentKey = toStr(jredis.get(KEY_WITH_PARENT_UNIQUE_ID
                                                                     .format(firstParentKey)));
        if (parentKey != null) {
            keyBuilder = keyBuilder.withParent(parentKey);
        }
        final NodeKey uniqueKey = keyBuilder.andCreate();
        final StorageNode nodeEntry = createFoundEntryWithKey(uniqueKey);

        return nodeEntry;
    }

    protected final StorageNode createFoundEntryWithKey(final NodeKey uniqueKey) {
        return new StorageNodeImpl(uniqueKey, true);
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

    @Override
    protected void flushNewItem(final Nothing reference,
                                 final Partition partition,
                                 final StorageNode entry)
        throws Exception {
        final String uniqueKey = entry.getKey().getKeyAsString();
        final JRedis jredis = factory.getFrom(partition);
        final JRedisLoggedExecution jredisExec = new JRedisLoggedExecution(uniqueKey,
                                                                     jredis);
        jredisExec.sadd(SET_WITH_ALL_KEYS, uniqueKey);
        jredisExec.sadd(SET_WITH_ALL_NODE_KEYS_FOR_TYPE.format(entry
                                                                    .getType()), uniqueKey);
        jredis.sadd(SET_WITH_ALL_KEY_NAMES, entry.getType());
        final String parentKeyAsString = entry.getKey().getParentKeyAsString();
        if (parentKeyAsString != null) {
            jredisExec.set(KEY_WITH_PARENT_UNIQUE_ID.format(uniqueKey),
                           parentKeyAsString);

            jredisExec.sadd(SET_WITH_NODE_CHILDREN_KEYS
                                                       .format(parentKeyAsString), uniqueKey);
            jredisExec.sadd(SET_WITH_NODE_CHILDREN_NAMED_KEYS.format(
                                                                     parentKeyAsString, entry.getType()), uniqueKey);
        }
        final String localKey = entry.getKey().getCompositeKey().getKeyAsString();
        jredisExec.sadd(SET_WITH_ALL_LOCAL_KEYS.format(localKey), uniqueKey);
        for (final SimpleKey k: entry.getKey().getCompositeKey().getKeys()) {
            internalFlushSimplePropertyAndCreateIndex(
                jredisExec,
                partition,
                k
                                                                              .getKeyName(),
                k.getValue() != null
                    ? k.getValue()
                                                                                                                          .getBytes()
                    : null, uniqueKey, PropertyType.KEY);

        }
    }

    private void flushRemoved(final Partition partition,
                               final PropertyContainer entry,
                               final String entryName)
        throws Exception {
        final JRedis jredis = factory.getFrom(partition);

        final String uniqueKey = entry.getKeyAsString();
        jredis.srem(SET_WITH_ALL_KEYS, uniqueKey);
        if (entryName != null) {
            jredis.srem(SET_WITH_ALL_NODE_KEYS_FOR_TYPE.format(entryName),
                        uniqueKey);
        }
        final String simpleProperties = SET_WITH_NODE_PROPERTY_SIMPLE_NAMES
                                                                     .format(uniqueKey);
        final String keyProperties = SET_WITH_NODE_PROPERTY_KEY_NAMES
                                                               .format(uniqueKey);
        final String indexedProperties = SET_WITH_NODE_PROPERTY_INDEXED_NAMES
                                                                       .format(uniqueKey);
        final List<String> allPropertyNames = newLinkedList();
        allPropertyNames.addAll(listBytesToListString(jredis
                                                            .smembers(simpleProperties)));
        allPropertyNames.addAll(listBytesToListString(jredis
                                                            .smembers(keyProperties)));
        allPropertyNames.addAll(listBytesToListString(jredis
                                                            .smembers(indexedProperties)));
        for (final String s: allPropertyNames) {
            jredis.del(SET_WITH_NODE_CHILDREN_NAMED_KEYS.format(uniqueKey, s),
                       KEY_WITH_PROPERTY_VALUE.format(uniqueKey, s));
        }
        jredis.del(simpleProperties, keyProperties, indexedProperties,
                   SET_WITH_NODE_CHILDREN_KEYS.format(uniqueKey),
                   KEY_WITH_PARENT_UNIQUE_ID.format(uniqueKey));

        final String dependentKeys = SET_WITH_ALL_DEPENDENT_KEYS.format(uniqueKey);
        final List<String> keys = listBytesToListString(jredis
                                                        .smembers(dependentKeys));

        for (final String key: keys) {
            if (key.contains(uniqueKey)) {
                jredis.del(key);
            } else {
                final List<String> possibleValues = listBytesToListString(jredis
                                                                          .smembers(key));
                for (final String possibleValue: possibleValues) {
                    if (possibleValue.contains(uniqueKey)) {
                        jredis.srem(key, possibleValue);
                    }
                }

            }
        }
        jredis.del(dependentKeys);

    }

    @Override
    protected void flushRemovedItem(final Partition partition,
                                     final StorageNode entry)
            throws Exception {
        flushRemoved(partition, entry, entry.getType());
    }

    @Override
    protected Set<StorageNode> internalNodeEntryGetChildrenByType(
                                                                  final Partition partition,
                                                                  final StorageNode stNodeEntry,
                                                                  final String type)
            throws Exception {
        final JRedis jredis = factory.getFrom(partition);

        final String parentKey = stNodeEntry.getKey().getKeyAsString();
        final String keyName =
            type == null
                ? SET_WITH_NODE_CHILDREN_KEYS
                                                                   .format(parentKey)
                : SET_WITH_NODE_CHILDREN_NAMED_KEYS
                    .format(
                                                                                                                                 parentKey,
                        type);
        final List<String> childrenKeys = listBytesToListString(jredis
                                                                .smembers(keyName));
        final ImmutableSet.Builder<StorageNode> builder = ImmutableSet.builder();
        for (final String id: childrenKeys) {
            final Partition childPartition = getPartition(id, partitionFactory);
            if (partition.equals(childPartition)) {
                final StorageNode loadedNode = loadNodeOrReturnNull(id,
                                                              childPartition);
                if (loadedNode != null) {
                    builder.add(loadedNode);
                }
            }
        }
        return builder.build();
    }

    @Override
    protected Set<StorageNode> internalNodeEntryGetChildren(
                                                             final Partition partition,
                                                             final StorageNode stNodeEntry)
        throws Exception {
        return internalNodeEntryGetChildrenByType(partition, stNodeEntry, null);
    }

    @Override
    protected StorageNode internalNodeEntryGetParent(final Partition partition,
                                                      final StorageNode stNodeEntry)
        throws Exception {

        final String parentKeyAsString = stNodeEntry.getKey()
                                              .getParentKeyAsString();
        if (parentKeyAsString == null) { return null; }
        return loadNodeOrReturnNull(parentKeyAsString, partition);
    }

    @Override
    protected Set<StorageNode> internalFindByType(final Partition partition,
                                                  final String type)
        throws Exception {

        final JRedis jRedis = factory.getFrom(partition);
        final List<String> ids = listBytesToListString(jRedis
                                                       .smembers(SET_WITH_ALL_NODE_KEYS_FOR_TYPE.format(type)));
        final ImmutableSet.Builder<StorageNode> builder = ImmutableSet
                                                                .<StorageNode>builder();
        for (final String id: ids) {
            final StorageNode loadedNode = loadNodeOrReturnNull(id, partition);
            if (loadedNode != null) {
                builder.add(loadedNode);
            }
        }
        return builder.build();
    }

    private Property loadProperty(final Partition partition,
                                     final PropertyContainer stNodeEntry,
                                     final String parentKey,
                                     final String propertyName,
                                     final PropertyType type)
        throws Exception {
        final JRedis jredis = factory.getFrom(partition);

        final Property property = type.createProperty(propertyName, stNodeEntry);
        if (type.isKey()) {
            final String value = toStr(jredis.get(KEY_WITH_PROPERTY_VALUE.format(
                                                                           parentKey, propertyName)));
            ((PropertyImpl) property).setStringValueOnLoad(this, value);
        }
        return property;
    }

    @Override
    protected void internalFlushSimpleProperty(final Nothing reference,
                                                final Partition partition,
                                                final Property dirtyProperty)
        throws Exception {
        final JRedis jredis = factory.getFrom(partition);
        final String uniqueKey = dirtyProperty.getParent().getKeyAsString();
        final JRedisLoggedExecution jredisExecution = new JRedisLoggedExecution(
                                                                          uniqueKey, jredis);
        final PropertyType type = dirtyProperty.isKey() ? PropertyType.KEY
                : (dirtyProperty.isIndexed() ? PropertyType.INDEXED
                        : PropertyType.SIMPLE);

        internalFlushSimplePropertyAndCreateIndex(jredisExecution, partition,
                                                  dirtyProperty.getPropertyName(),
            ((PropertyImpl) dirtyProperty).getTransientValueAsBytes(this),
                                                  uniqueKey, type);

    }

    private void internalFlushSimplePropertyAndCreateIndex(
                                                            final JRedisLoggedExecution exec,
                                                            final Partition partition,
                                                            final String propertyName,
                                                            final byte[] propertyValue,
                                                            final String uniqueKey,
                                                            final PropertyType propertyType)
        throws Exception {

        final JRedis jredis = factory.getFrom(partition);
        final String setName = propertyType.getSetName(uniqueKey);
        exec.sadd(setName, propertyName);
        final String valueKey = KEY_WITH_PROPERTY_VALUE.format(uniqueKey,
                                                         propertyName);
        if (propertyType.isIndexed()) {
            final String stripped = stripString(propertyValue != null ? new String(
                                                                             propertyValue) : null);
            if (jredis.exists(valueKey)) {
                final String existent = stripString(toStr(jredis.get(valueKey)));
                if (!existent.equals(stripped)) {
                    jredis.srem(SET_WITH_INDEX_ENTRY.format(existent,
                                                            propertyName), uniqueKey);
                    exec.sadd(SET_WITH_INDEX_ENTRY.format(stripped,
                                                          propertyName), uniqueKey);
                }
            } else {
                exec.sadd(SET_WITH_INDEX_ENTRY.format(stripped, propertyName),
                          uniqueKey);
            }
        }
        if (propertyValue != null) {
            exec.set(valueKey, propertyValue);
        } else {
            jredis.del(valueKey);
        }
    }

    private String stripString(final String transientValueAsString) {
        return transientValueAsString == null ? "null" : transientValueAsString
                                                                               .replaceAll("[ ]|[\n]|[\t]|[\r]", "-");
    }

    @Override
    protected Iterable<String> internalGetAllNodeTypes(final Partition partition)
            throws Exception {
        final JRedis jredis = factory.getFrom(partition);
        return listBytesToListString(jredis.smembers(SET_WITH_ALL_KEY_NAMES));
    }

    @Override
    protected Nothing createLinkReferenceIfNecessary(final Partition partition,
                                                      final StorageLink entry) {
        return Nothing.NOTHING;
    }

    @Override
    protected void flushRemovedLink(final Partition partition,
                                     final StorageLink link)
            throws Exception {
        final JRedis jredis = factory.getFrom(partition);
        jredis.srem(SET_WITH_ALL_LINKS_FOR_TYPE.format(link.getLinkType()),
                    link.getKeyAsString());
        jredis.srem(SET_WITH_ALL_LINKS_FOR_ORIGIN.format(link.getOrigin()
                                                             .getKeyAsString()), link.getKeyAsString());
        jredis.srem(SET_WITH_ALL_LINKS_FOR_TARGET.format(link.getTarget()
                                                             .getKeyAsString()), link.getKeyAsString());
        flushRemoved(partition, link, null);
    }

    @Override
    protected void handleNewLink(final Partition partition,
                                  final StorageNode origin,
                                  final StorageLink link)
        throws Exception {
        final JRedis jredis = factory.getFrom(partition);
        jredis.sadd(SET_WITH_ALL_LINKS_FOR_TYPE.format(link.getLinkType()),
                    link.getKeyAsString());
        jredis.sadd(SET_WITH_ALL_LINKS_FOR_ORIGIN.format(link.getOrigin()
                                                             .getKeyAsString()), link.getKeyAsString());
        jredis.sadd(SET_WITH_ALL_LINKS_FOR_TARGET.format(link.getTarget()
                                                             .getKeyAsString()), link.getKeyAsString());

    }

    @SuppressWarnings("unchecked")
    @Override
    protected Iterable<StorageLink> internalFindLinks(
                                                       final Partition partition,
                                                       final StorageNode origin,
                                                       final StorageNode destiny,
                                                       final String name)
        throws Exception {
        final JRedis jredis = factory.getFrom(partition);
        final List<String> linkIds =
            listBytesToListString(jredis
                                                           .smembers(SET_WITH_ALL_LINKS_FOR_ORIGIN.format(origin
                                                                                                                .getKeyAsString())));
        if (destiny != null) {
            final List<String> newIds =
                listBytesToListString(jredis
                                                              .smembers(SET_WITH_ALL_LINKS_FOR_TARGET.format(destiny
                                                                                                                    .getKeyAsString())));
            linkIds.retainAll(newIds);
        }
        if (name != null) {
            final List<String> newIds = listBytesToListString(jredis
                                                              .smembers(SET_WITH_ALL_LINKS_FOR_TYPE.format(name)));
            linkIds.retainAll(newIds);
        }
        if (linkIds.size() == 0) { return Collections.emptyList(); }
        return IteratorBuilder.<StorageLink, String>createIteratorBuilder()
                              .withItems(linkIds).withConverter(
                                                                new Converter<StorageLink, String>() {

                                                                    @Override
                                                                    public StorageLink convert(final String o)
                                                                        throws Exception {
                                                                        final StorageNode originNode =
                                                                            JRedisStorageSessionImpl.this
                                                                                                                                .loadNodeOrReturnNull(
                                                                                                                                                      StringIDSupport
                                                                                                                                                                     .getOriginKeyAsStringFromLinkKey(o),
                                                                                                                                                      partition);
                                                                        final String targetId =
                                                                            StringIDSupport
                                                                                                         .getTargeyKeyAsStringFromLinkKey(o);
                                                                        final Partition targetPartition =
                                                                            StringIDSupport
                                                                                                                     .getPartition(
                                                                                                                                   targetId,
                                                                                                                                   partitionFactory);
                                                                        final StorageNode targetNode =
                                                                            JRedisStorageSessionImpl.this
                                                                                                                                .loadNodeOrReturnNull(
                                                                                                                                    targetId,
                                                                                                                                                      targetPartition);

                                                                        return new StorageLinkImpl(
                                                                            StringIDSupport
                                                                                                                  .getLinkNameFromLinkKey(o),
                                                                            originNode,
                                                                                                   targetNode, true);
                                                                    }
                                                                }).andBuild();
    }

    @Override
    protected Set<Property> internalPropertyContainerLoadProperties(
                                                                       final Nothing reference,
                                                                       final Partition partition,
                                                                       final PropertyContainer stNodeEntry)
        throws Exception {
        final JRedis jredis = factory.getFrom(partition);
        final Set<Property> result = newHashSet();

        final String parentKey = stNodeEntry.getKeyAsString();
        for (final PropertyType type: PropertyType.values()) {
            final String properties = type.getSetName(parentKey);
            if (jredis.exists(properties)) {
                final List<String> propertyNames = listBytesToListString(jredis
                                                                         .smembers(properties));
                for (final String propertyName: propertyNames) {
                    final Property property = loadProperty(partition, stNodeEntry,
                                                       parentKey, propertyName, type);
                    if (property != null) {
                        result.add(property);
                    }
                }
            }
        }
        return result;
    }

}
