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

package org.openspotlight.storage.mongodb;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptySet;
import static org.openspotlight.common.Pair.newPair;
import static org.openspotlight.common.util.Conversion.convert;
import static org.openspotlight.storage.StringKeysSupport.getNodeType;
import static org.openspotlight.storage.StringKeysSupport.getPartition;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.openspotlight.common.Pair;
import org.openspotlight.common.collection.IteratorBuilder;
import org.openspotlight.common.collection.IteratorBuilder.Converter;
import org.openspotlight.common.collection.IteratorBuilder.SimpleIteratorBuilder;
import org.openspotlight.common.util.SLCollections;
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
import org.openspotlight.storage.StorageSession.FlushMode;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

/**
 * Created by User: feu - Date: Mar 23, 2010 - Time: 4:46:25 PM
 */
public class MongoStorageSessionImpl implements StorageEngineBind<DBObject> {

    private static final String                                    _                = "_";
    private static final String                                    ID               = "_id";
    private static final String                                    LOCAL_ID         = "node_local_id";
    private static final String                                    PARENT_ID        = "node_parent_id";
    private static final String                                    KEY_NAMES        = "node_key_names";
    private static final String                                    PROPERTIES       = "node_properties";
    private static final String                                    INDEXED          = "node_indexed";
    private static final String                                    LINKS            = "links";
    private static final String                                    NODE_TYPE        = "node_type";
    private static final String                                    NULL_VALUE       = "!!!NULL!!!";

    private final FlushMode                                        flushMode;
    private final int                                              maxCacheSize;
    private final Mongo                                            mongo;

    private final Map<Pair<String, String>, DBCollection>          collectionsMap   = newHashMap();
    private final Map<String, GridFS>                              gridFSMap;

    private final LinkedList<Pair<NodeKey, DBObject>>              objectCache      = newLinkedList();

    private final PartitionFactory                                 partitionFactory;

    private final Map<String, DB>                                  partitionMap;

    private final Multimap<Partition, Pair<StorageNode, DBObject>> transientObjects = HashMultimap
                                                                                        .create();

    Set<String>                                                    allIndexes       = newHashSet();

    @Inject
    public MongoStorageSessionImpl(final Mongo mongo, final FlushMode flushMode, final PartitionFactory partitionFactory,
                                   final int maxCacheSize) {
        this.partitionFactory = partitionFactory;
        this.maxCacheSize = maxCacheSize;
        partitionMap = newHashMap();
        this.mongo = mongo;
        gridFSMap = newHashMap();
        this.flushMode = flushMode;
    }

    private static String beforeRegex(final String s) {
        return s;
    }

    private StorageNode convertToNode(final Partition partition,
                                      final DBObject dbObject)
        throws Exception {
        final DBObject keyAsDbObj = (DBObject) dbObject.get(INDEXED);
        final List<String> keyNames = (List<String>) dbObject.get(KEY_NAMES);

        NodeKeyBuilder keyBuilder = new NodeKeyBuilderImpl((String) dbObject.get(NODE_TYPE), partition);

        for (final String s: keyAsDbObj.keySet()) {
            if (keyNames.contains(s)) {
                String valueAsString = convert(keyAsDbObj.get(s), String.class);
                if (NULL_VALUE.equals(valueAsString)) {
                    valueAsString = null;
                }
                keyBuilder.withSimpleKey(s, valueAsString);
            }
        }
        final String parentId = (String) dbObject.get(PARENT_ID);
        if (parentId != null) {
            keyBuilder.withParent(parentId);
        }
        final NodeKey uniqueKey = keyBuilder.andCreate();
        final StorageNode node = new StorageNodeImpl(uniqueKey, false);
        return node;
    }

    private void ensureIndexed(final Partition partition,
                               final String parentName, final String groupName,
                               final String propertyName, final StorageLink possibleParentAsLink) {
        final String key = partition.getPartitionName() + parentName
                + groupName + propertyName;
        if (!allIndexes.contains(key)) {
            allIndexes.add(key);
            getCachedCollection(partition, parentName).ensureIndex(
                    groupName != null ? (groupName + "." + propertyName)
                            : propertyName);
        }
    }

    private DBObject findReferenceOrReturnNull(final Partition partition,
                                               final PropertyContainer entry) {
        DBObject basicDBObject = null;

        StorageNode node;
        if (entry instanceof StorageNode) {
            node = (StorageNode) entry;
        } else if (entry instanceof StorageLink) {
            node = ((StorageLink) entry).getSource();
        } else {
            throw new IllegalStateException();
        }

        final Pair<StorageNode, DBObject> p = Pair.<StorageNode, DBObject>newPair(node, null, Pair.PairEqualsMode.K1);
        if (transientObjects.get(partition).contains(p)) {
            for (final Pair<StorageNode, DBObject> pair: transientObjects.get(partition)) {
                if (pair.equals(p)) {
                    basicDBObject = pair.getK2();
                    break;
                }
            }
        }
        NodeKey key;
        String collectionName;
        key = node.getKey();
        collectionName = node.getType();

        final Pair<NodeKey, DBObject> p1 = newPair(key, null, Pair.PairEqualsMode.K1);
        final int idx = objectCache.indexOf(p1);
        if (idx != -1) {
            basicDBObject = objectCache.get(idx).getK2();
        }
        if (basicDBObject == null) {
            final DBCollection coll = getCachedCollection(partition, collectionName);
            final BasicDBObject queryObject = new BasicDBObject();
            queryObject.put(ID, key.getKeyAsString());
            basicDBObject = coll.findOne(queryObject);
            if (basicDBObject == null) {
                basicDBObject = new BasicDBObject();
                basicDBObject.put(ID, key.getKeyAsString());
            }
            objectCache.addFirst(newPair(key, basicDBObject, Pair.PairEqualsMode.K1));
            if (objectCache.size() > maxCacheSize) {
                objectCache.removeLast();
            }
        }
        return basicDBObject;
    }

    private String getBigPropertyName(final Property dirtyProperty) {
        return "big_" + dirtyProperty.getPropertyName();
    }

    private DBCollection getCachedCollection(final Partition partition,
                                             final String collectionName) {
        return getCachedCollection(partition.getPartitionName(), collectionName);
    }

    private DBCollection getCachedCollection(final String partition,
                                             final String collectionName) {
        final Pair<String, String> key = newPair(partition, collectionName);
        DBCollection collection = collectionsMap.get(key);
        if (collection == null) {
            final DB db = getCachedDbForPartition(partition);
            collection = db.getCollection(collectionName);
            collectionsMap.put(key, collection);
        }
        return collection;
    }

    private DB getCachedDbForPartition(final Partition partition) {
        return getCachedDbForPartition(partition.getPartitionName());
    }

    private DB getCachedDbForPartition(final String partitionName) {
        DB db = partitionMap.get(partitionName);
        if (db == null) {
            db = mongo.getDB(partitionName);
            partitionMap.put(partitionName, db);
        }
        return db;
    }

    private GridFS getCachedGridFSForPartition(final Partition partition) {
        return getCachedGridFSForPartition(partition.getPartitionName());
    }

    private GridFS getCachedGridFSForPartition(final String partition) {
        GridFS fs = gridFSMap.get(partition);
        if (fs == null) {
            final DB db = getCachedDbForPartition(partition);
            fs = new GridFS(db);
            gridFSMap.put(partition, fs);
        }
        return fs;

    }

    private String getFileName(final Partition partition,
                               final Property dirtyProperty) {
        StorageNode nodeEntry;
        if (dirtyProperty.getParent() instanceof StorageNode) {
            nodeEntry = (StorageNode) dirtyProperty.getParent();
        } else if (dirtyProperty.getParent() instanceof StorageLink) {
            nodeEntry = ((StorageLink) dirtyProperty.getParent()).getSource();
        } else {
            throw new IllegalStateException();
        }

        final String key = partition.getPartitionName() + _
                + nodeEntry.getKey().getKeyAsString() + _
                + dirtyProperty.getPropertyName();
        return key;
    }

    private Iterable<StorageNode> internalGetChildren(
                                                      final Partition partition, final StorageNode StorageNode,
                                                      final String name)
        throws Exception {
        final BasicDBObject baseDbObj = new BasicDBObject();
        baseDbObj.put(PARENT_ID, StorageNode.getKey().getKeyAsString());
        final ImmutableSet.Builder<String> names = ImmutableSet.builder();
        if (name != null) {
            names.add(name);
        } else {
            names.addAll(getCachedDbForPartition(partition)
                    .getCollectionNames());
        }
        final List<Iterable<DBObject>> dbCursors = newLinkedList();
        for (final String s: names.build()) {
            final DBCursor resultAsDbObject = getCachedCollection(partition, s)
                    .find(baseDbObj);
            dbCursors.add(resultAsDbObject);

        }

        final IteratorBuilder.SimpleIteratorBuilder<StorageNode, DBObject> b = IteratorBuilder
                .createIteratorBuilder();
        b.withConverter(new IteratorBuilder.Converter<StorageNode, DBObject>() {
            @Override
            public StorageNode convert(final DBObject nodeEntry)
                    throws Exception {
                return convertToNode(partition, nodeEntry);
            }
        });
        final Iterable<StorageNode> result = b.withItems(
                SLCollections.<DBObject>iterableOfAll(dbCursors)).andBuild();
        return result;
    }

    @Override
    public DBObject createLinkReferenceIfNecessary(
                                                      final Partition partition, final StorageLink link) {
        final StorageNode source = link.getSource();
        final DBObject nodeRef = createNodeReferenceIfNecessary(partition,
                source);
        DBObject linkRef = null;
        @SuppressWarnings("unchecked")
        List<DBObject> links = (List<DBObject>) nodeRef.get(LINKS);
        if (links == null) {
            links = new ArrayList<DBObject>();
            linkRef = new BasicDBObject();
            links.add(linkRef);
            linkRef.put(ID, link.getKeyAsString());
            nodeRef.put(LINKS, links);
        } else {
            for (final DBObject possibleLink: links) {
                if (possibleLink.get(ID).equals(link.getKeyAsString())) {
                    linkRef = possibleLink;
                    break;
                }
            }
            if (linkRef == null) {
                linkRef = new BasicDBObject();
                links.add(linkRef);
                linkRef.put(ID, link.getKeyAsString());
            }
        }

        nodeRef.put(LINKS, links);
        return linkRef;
    }

    @Override
    public DBObject createNodeReferenceIfNecessary(
                                                      final Partition partition, final StorageNode entry) {
        final DBObject basicDBObject = findReferenceOrReturnNull(partition,
                entry);
        final Pair<StorageNode, DBObject> p = Pair
                .<StorageNode, DBObject>newPair(entry, basicDBObject,
                        Pair.PairEqualsMode.K1);
        if (!transientObjects.get(partition).contains(p)) {
            transientObjects.put(partition, p);
        }
        return basicDBObject;
    }

    @Override
    public void flushNewItem(final DBObject reference, final Partition partition, final StorageNode entry)
            throws Exception {
        reference.put(LOCAL_ID, entry.getKey().getCompositeKey()
                .getKeyAsString());
        ensureIndexed(partition, entry.getType(), null, LOCAL_ID, null);

        final NodeKey uniqueId = entry.getKey();
        final String parentId = uniqueId.getParentKeyAsString();
        if (parentId != null) {
            reference.put(PARENT_ID, parentId);
        }
        final BasicDBObject key = new BasicDBObject();
        final List<String> keyNames = newArrayList();
        for (final SimpleKey keyEntry: uniqueId.getCompositeKey().getKeys()) {
            keyNames.add(keyEntry.getKeyName());
            key.put(keyEntry.getKeyName(),
                    keyEntry.getValue() != null ? keyEntry.getValue()
                            : NULL_VALUE);
            ensureIndexed(partition, entry.getType(), INDEXED,
                    keyEntry.getKeyName(), null);

        }
        reference.put(ID, uniqueId.getKeyAsString());
        reference.put(KEY_NAMES, keyNames);
        reference.put(INDEXED, key);
        reference.put(NODE_TYPE, uniqueId.getCompositeKey().getNodeType());
        if (FlushMode.AUTO.equals(flushMode)) {
            final DBCollection col = getCachedCollection(partition,
                    entry.getType());
            col.save(reference);
        } else {
            final Pair<StorageNode, DBObject> p = Pair
                    .<StorageNode, DBObject>newPair(entry, reference,
                            Pair.PairEqualsMode.K1);
            if (!transientObjects.get(partition).contains(p)) {
                transientObjects.put(partition, p);
            }
        }
    }

    @Override
    public void flushRemovedItem(final Partition partition,
                                    final StorageNode entry)
        throws Exception {
        final DBCollection collection = getCachedCollection(partition,
                entry.getType());
        collection
                .remove(new BasicDBObject(ID, entry.getKey().getKeyAsString()));
    }

    @Override
    public void flushRemovedLink(final Partition partition,
                                    final StorageLink link) {
        final DBObject basicDBObject = findReferenceOrReturnNull(partition,
                link.getSource());
        if (basicDBObject != null) {
            @SuppressWarnings("unchecked")
            final List<DBObject> links = (List<DBObject>) basicDBObject
                    .get(LINKS);
            if (links != null) {
                for (final DBObject possibleLink: links) {
                    if (possibleLink.get(ID).equals(link.getKeyAsString())) {
                        links.remove(possibleLink);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void handleNewLink(final Partition partition,
                                 final StorageNode source, final StorageLink link)
        throws Exception {
        createLinkReferenceIfNecessary(partition, link);
        if (flushMode.equals(FlushMode.AUTO)) {
            final DBObject nodeRef = createNodeReferenceIfNecessary(partition,
                    source);
            final DBCollection col = getCachedCollection(partition,
                    source.getType());
            col.save(nodeRef);

        }
    }

    @Override
    public Iterable<StorageNode> findByCriteria(final Partition partition, final NodeCriteria criteria)
            throws Exception {

        final DBObject criteriaAsObj = new BasicDBObject();

        for (final NodeCriteriaItem c: criteria.getCriteriaItems()) {
            if (c instanceof PropertyCriteriaItem) {
                final PropertyCriteriaItem p = (PropertyCriteriaItem) c;
                criteriaAsObj.put(INDEXED + "." + p.getPropertyName(),
                        p.getValue() == null ? NULL_VALUE : p.getValue());

            }
            if (c instanceof PropertyContainsString) {
                final PropertyContainsString p = (PropertyContainsString) c;
                criteriaAsObj.put(
                        INDEXED + "." + p.getPropertyName(),
                        Pattern.compile("(.*)" + beforeRegex(p.getValue())
                                + "(.*)"));
            }
            if (c instanceof PropertyStartsWithString) {
                final PropertyStartsWithString p = (PropertyStartsWithString) c;
                criteriaAsObj.put(
                        INDEXED + "." + p.getPropertyName(),
                        Pattern.compile("^" + beforeRegex(p.getValue())
                                + "(.*)"));
            }
            if (c instanceof PropertyEndsWithString) {
                final PropertyEndsWithString p = (PropertyEndsWithString) c;
                criteriaAsObj.put(
                        INDEXED + "." + p.getPropertyName(),
                        Pattern.compile("(.*)" + beforeRegex(p.getValue())
                                + "$"));
            }
            if (c instanceof NodeKeyCriteriaItem) {
                final NodeKeyCriteriaItem uniqueCriteria = (NodeKeyCriteriaItem) c;
                criteriaAsObj.put(ID, uniqueCriteria.getValue()
                        .getKeyAsString());
            }
            if (c instanceof NodeKeyAsStringCriteriaItem) {
                final NodeKeyAsStringCriteriaItem uniqueCriteria = (NodeKeyAsStringCriteriaItem) c;
                criteriaAsObj.put(ID, uniqueCriteria.getKeyAsString());
            }
            if (c instanceof CompositeKeyCriteriaItem) {
                final CompositeKeyCriteriaItem uniqueCriteria = (CompositeKeyCriteriaItem) c;
                final String localHash = uniqueCriteria.getValue()
                        .getKeyAsString();
                criteriaAsObj.put(LOCAL_ID, localHash);
            }
        }

        final ImmutableSet.Builder<String> nodeNamesBuilder = ImmutableSet
                .builder();
        if (criteria.getNodeType() != null) {
            nodeNamesBuilder.add(criteria.getNodeType());
        } else {
            nodeNamesBuilder.addAll(getCachedDbForPartition(partition)
                    .getCollectionNames());
        }
        final List<Iterable<DBObject>> dbCursors = newLinkedList();
        for (final String s: nodeNamesBuilder.build()) {
            final DBCursor resultAsDbObject = getCachedCollection(partition, s)
                    .find(criteriaAsObj);
            dbCursors.add(resultAsDbObject);

        }

        final IteratorBuilder.SimpleIteratorBuilder<StorageNode, DBObject> b = IteratorBuilder
                .createIteratorBuilder();
        b.withConverter(new IteratorBuilder.Converter<StorageNode, DBObject>() {
            @Override
            public StorageNode convert(final DBObject nodeEntry)
                    throws Exception {
                return convertToNode(partition, nodeEntry);
            }
        });
        final Iterable<StorageNode> result = b.withItems(SLCollections.<DBObject>iterableOfAll(dbCursors)).andBuild();
        return result;

    }

    @Override
    public Iterable<StorageNode> findByType(
                                                       final Partition partition, final String nodeType)
        throws Exception {
        final DBCursor cursor = getCachedCollection(partition, nodeType).find();
        final ImmutableSet.Builder<StorageNode> builder = ImmutableSet
                .builder();
        while (cursor.hasNext()) {
            builder.add(convertToNode(partition, cursor.next()));
        }
        return builder.build();
    }

    @Override
    public Iterable<StorageLink> findLinks(final Partition partition, final StorageNode source,
                                                   final StorageNode target, final String type) {
        final Builder<String> rawItems = ImmutableList.builder();
        final DBObject basicDBObject = findReferenceOrReturnNull(partition, source);
        if (basicDBObject != null) {
            @SuppressWarnings("unchecked")
            final List<DBObject> links = (List<DBObject>) basicDBObject.get(LINKS);
            if (links != null) {
                for (final DBObject possibleLink: links) {
                    final String linkId = (String) possibleLink.get(ID);
                    if (type != null && target != null) {
                        if (StringKeysSupport.getLinkTypeFromLinkKey(linkId).equals(type) &&
                            StringKeysSupport.getTargeyKeyAsStringFromLinkKey(linkId).equals(target.getKeyAsString())) {
                            rawItems.add(linkId);
                        }
                    } else if (type != null) {
                        if (StringKeysSupport.getLinkTypeFromLinkKey(linkId).equals(type)) {
                            rawItems.add(linkId);
                        }
                    } else if (target != null) {
                        if (StringKeysSupport.getTargeyKeyAsStringFromLinkKey(linkId).equals(target.getKeyAsString())) {
                            rawItems.add(linkId);
                        }
                    } else {
                        rawItems.add(linkId);
                    }
                }
            }
        }

        @SuppressWarnings("unchecked")
        final SimpleIteratorBuilder<StorageLink, String> result = IteratorBuilder
                .<StorageLink, String>createIteratorBuilder()
                .withItems(rawItems.build())
                .withConverter(new Converter<StorageLink, String>() {
                    @Override
                    public StorageLink convert(final String o)
                        throws Exception {
                        StorageNode foundTarget = target;
                        if (foundTarget == null) {
                            final String targetId = StringKeysSupport.getTargeyKeyAsStringFromLinkKey(o);
                            final Partition targetPartition =
                                partitionFactory.getPartition(StringKeysSupport.getPartitionName(targetId));

                            foundTarget = internalGetNode(targetId, targetPartition);
                            if (foundTarget == null) {
                                throw new IllegalStateException();
                            }
                        }
                        final String foundName = StringKeysSupport.getLinkTypeFromLinkKey(o);
                        return new StorageLinkImpl(foundName, source, foundTarget, true);
                    }
                });
        return result.andBuild();
    }

    @Override
    public void flushSimpleProperty(
                                               final DBObject possibleReference, final Partition partition,
                                               final Property dirtyProperty)
        throws Exception {
        DBObject reference;
        String collectionName;
        if (possibleReference != null) {
            reference = possibleReference;
            collectionName = StringKeysSupport
                    .getNodeType((String) reference.get(ID));
        } else if (dirtyProperty.getParent() instanceof StorageNode) {
            reference = createNodeReferenceIfNecessary(partition,
                    (StorageNode) dirtyProperty.getParent());
            collectionName = ((StorageNode) dirtyProperty.getParent())
                    .getType();
        } else if (dirtyProperty.getParent() instanceof StorageLink) {
            reference = createLinkReferenceIfNecessary(partition,
                    (StorageLink) dirtyProperty.getParent());
            collectionName = ((StorageLink) dirtyProperty.getParent())
                    .getSource().getType();

        } else {
            throw new IllegalStateException();
        }

        String objName = null;
        Object value = null;

        if (dirtyProperty.isIndexed()) {
            ensureIndexed(partition, collectionName, INDEXED,
                    dirtyProperty.getPropertyName(), null);
            objName = INDEXED;
            value = ((PropertyImpl) dirtyProperty).getTransientValueAsString();
            if (value == null) {
                value = NULL_VALUE;
            }
        } else if (!dirtyProperty.isKey()) {
            objName = PROPERTIES;
            value = ((PropertyImpl) dirtyProperty).getTransientValueAsBytes();
        }
        if (objName == null) { return; }
        DBObject obj = (DBObject) reference.get(objName);
        if (obj == null) {
            obj = new BasicDBObject();
            reference.put(objName, obj);
        }
        if (value instanceof byte[] && isBiggerThan4mb((byte[]) value)) {
            obj.put(getBigPropertyName(dirtyProperty), true);
        } else {
            obj.removeField(getBigPropertyName(dirtyProperty));
            obj.put(dirtyProperty.getPropertyName(), value);
            StorageNode nodeEntry;
            if (dirtyProperty.getParent() instanceof StorageNode) {
                nodeEntry = (StorageNode) dirtyProperty.getParent();
            } else if (dirtyProperty.getParent() instanceof StorageLink) {
                nodeEntry = ((StorageLink) dirtyProperty.getParent())
                        .getSource();
            } else {
                throw new IllegalStateException();
            }

            if (FlushMode.AUTO.equals(flushMode)) {
                getCachedCollection(partition, nodeEntry.getType()).save(
                        reference);
            } else {
                final Pair<StorageNode, DBObject> p = newPair(nodeEntry,
                        reference, Pair.PairEqualsMode.K1);
                if (!transientObjects.get(partition).contains(p)) {
                    transientObjects.put(partition, p);
                }

            }
        }

    }

    @Override
    public Iterable<String> getAllNodeTypes(final Partition partition) {
        final HashSet<String> set = new HashSet<String>();
        set.addAll(getCachedDbForPartition(partition).getCollectionNames());
        set.remove("system.indexes");
        return ImmutableSet.copyOf(set);
    }

    @Override
    public Iterable<StorageNode> getChildren(final Partition partition, final StorageNode StorageNode)
            throws Exception {
        if (StorageNode == null) { return emptySet(); }
        return internalGetChildren(partition, StorageNode, null);

    }

    @Override
    public StorageNode getNode(String key)
        throws Exception {
        return internalGetNode(key, getPartition(key, partitionFactory));
    }

    public StorageNode internalGetNode(String key, Partition partition)
        throws Exception {
        final String nodeType = getNodeType(key);
        final BasicDBObject parameter = new BasicDBObject();
        parameter.put(ID, key);
        final DBCollection collection = getCachedCollection(partition, nodeType);
        final DBObject result = collection.findOne(parameter);
        return convertToNode(partition, result);

    }

    @Override
    public Iterable<StorageNode> getChildrenByType(
                                                                       final Partition initialPartition,
                                                                       final StorageNode StorageNode,
                                                                       final String type)
        throws Exception {
        if (StorageNode == null) { return emptySet(); }
        return internalGetChildren(initialPartition, StorageNode, type);
    }

    @Override
    public StorageNode getParent(final Partition partition, final StorageNode StorageNode)
        throws Exception {
        final String parentKey = StorageNode.getKey().getParentKeyAsString();
        if (parentKey == null) { return null; }
        return getNode(parentKey);
    }

    @Override
    public Set<Property> loadProperties(
                                                                    final DBObject possibleReference, final Partition partition,
                                                                    final PropertyContainer propertyContainer)
        throws Exception {

        if (propertyContainer instanceof StorageNode) {
            final StorageNode nodeEntry = (StorageNode) propertyContainer;
            final ImmutableSet.Builder<Property> builder = ImmutableSet
                    .builder();
            for (final SimpleKey entry: nodeEntry.getKey().getCompositeKey()
                    .getKeys()) {
                final PropertyImpl p = PropertyImpl.createKey(entry.getKeyName(), propertyContainer);
                (p).setStringValueOnLoad(entry.getValue());
                builder.add(p);
            }
            final DBObject reference =
                possibleReference == null ? createNodeReferenceIfNecessary(partition, nodeEntry) : possibleReference;
            final DBObject indexed = (DBObject) reference.get(INDEXED);
            final List<String> keyNames = (List<String>) reference.get(KEY_NAMES);
            if (indexed != null) {
                for (final String s: indexed.keySet()) {
                    if (!keyNames.contains(s)) {
                        final PropertyImpl p = PropertyImpl.createIndexed(s, propertyContainer);
                        String value = (String) indexed.get(s);
                        if (NULL_VALUE.equals(value)) {
                            value = null;
                        }
                        (p).setStringValueOnLoad(value);
                        builder.add(p);
                    }
                }
            }

            final DBObject properties = (DBObject) reference.get(PROPERTIES);
            if (properties != null) {
                for (final String s: properties.keySet()) {
                    final PropertyImpl p = PropertyImpl.createSimple(s,
                            propertyContainer);
                    builder.add(p);
                }
            }

            return builder.build();

        } else if (propertyContainer instanceof StorageLink) {
            final StorageLink linkEntry = (StorageLink) propertyContainer;
            final ImmutableSet.Builder<Property> builder = ImmutableSet
                    .builder();
            final DBObject reference = createLinkReferenceIfNecessary(
                    partition, linkEntry);
            final DBObject indexed = (DBObject) reference.get(INDEXED);
            if (indexed != null) {
                for (final String s: indexed.keySet()) {
                    final PropertyImpl p = PropertyImpl.createIndexed(s,
                            propertyContainer);
                    String value = (String) indexed.get(s);
                    if (NULL_VALUE.equals(value)) {
                        value = null;
                    }
                    (p).setStringValueOnLoad(value);
                    builder.add(p);
                }
            }

            final DBObject properties = (DBObject) reference.get(PROPERTIES);
            if (properties != null) {
                for (final String s: properties.keySet()) {
                    final PropertyImpl p = PropertyImpl.createSimple(s,
                            propertyContainer);
                    builder.add(p);
                }
            }

            return builder.build();
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public byte[] getPropertyValue(final Partition partition,
                                              final Property stProperty)
        throws Exception {
        byte[] value = null;
        if (stProperty.isKey()) {
            final StorageNode parent = (StorageNode) stProperty.getParent();
            for (final SimpleKey e: parent.getKey().getCompositeKey()
                    .getKeys()) {
                if (e.getKeyName().equals(stProperty.getPropertyName())) {
                    value = e.getValue() != null ? e.getValue().getBytes()
                            : null;
                    if (NULL_VALUE.equals(new String(value))) {
                        value = null;
                    }
                    break;
                }
            }

        } else {
            final DBObject reference = findReferenceOrReturnNull(partition,
                    stProperty.getParent());
            if (reference != null) {
                if (stProperty.isIndexed()) {
                    final DBObject innerObj = (DBObject) reference.get(INDEXED);
                    if (innerObj != null) {
                        value = ((String) innerObj.get(stProperty
                                .getPropertyName())).getBytes();
                        if (NULL_VALUE.equals(new String(value))) {
                            value = null;
                        }
                    }
                } else {
                    final DBObject innerObj = (DBObject) reference
                            .get(PROPERTIES);
                    if (innerObj != null) {
                        final Boolean isBig = (Boolean) innerObj
                                .get(getBigPropertyName(stProperty));
                        if (Boolean.TRUE.equals(isBig)) {
                            value = readAsGridFS(partition, stProperty);

                        } else {
                            value = (byte[]) innerObj.get(stProperty
                                    .getPropertyName());
                        }
                        if (NULL_VALUE.equals(new String(value))) {
                            value = null;
                        }
                    }
                }
            }
        }
        return value;
    }

    @Override
    public void savePartitions(final Partition... partitions)
            throws Exception {
        for (final Partition partition: partitions) {
            for (final Pair<StorageNode, DBObject> p: transientObjects
                    .get(partition)) {
                final StorageNode n = p.getK1();
                final DBCollection coll = getCachedCollection(partition,
                        n.getType());
                coll.save(p.getK2());
            }
        }
        transientObjects.clear();

    }

    boolean isBiggerThan4mb(final byte[] bytes) {
        return (double) (bytes == null ? 0 : bytes.length) / (1024 * 1024) > 4.0;
    }

    @Override
    public void closeResources() {
        mongo.close();
    }

    public byte[] readAsGridFS(final Partition partition,
                               final Property property)
        throws Exception {
        final String key = getFileName(partition, property);
        final GridFS fs = getCachedGridFSForPartition(partition);
        final GridFSDBFile file = fs.findOne(key);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(file.getInputStream(), baos);
        return baos.toByteArray();
    }

    public void storeInGridFS(final Partition partition,
                              final Property property, final byte[] value)
        throws Exception {
        final String key = getFileName(partition, property);
        final GridFS fs = getCachedGridFSForPartition(partition);
        final GridFSInputFile file = fs.createFile(value);
        file.setFilename(key);
        file.save();
    }

}
