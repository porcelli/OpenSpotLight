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

package org.openspotlight.storage.mongodb;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptySet;
import static org.openspotlight.common.Pair.newPair;
import static org.openspotlight.common.util.Conversion.convert;
import static org.openspotlight.storage.StringIDSupport.getNodeEntryName;
import static org.openspotlight.storage.StringIDSupport.getPartition;

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
import org.openspotlight.storage.AbstractStorageSession;
import org.openspotlight.storage.Criteria;
import org.openspotlight.storage.Criteria.CriteriaItem;
import org.openspotlight.storage.Criteria.CriteriaItem.CompisiteKeyCriteriaItem;
import org.openspotlight.storage.Criteria.CriteriaItem.PropertyContainsString;
import org.openspotlight.storage.Criteria.CriteriaItem.PropertyCriteriaItem;
import org.openspotlight.storage.Criteria.CriteriaItem.PropertyEndsWithString;
import org.openspotlight.storage.Criteria.CriteriaItem.PropertyStartsWithString;
import org.openspotlight.storage.Criteria.CriteriaItem.NodeKeyAsStringCriteriaItem;
import org.openspotlight.storage.Criteria.CriteriaItem.NodeKeyCriteriaItem;
import org.openspotlight.storage.Partition;
import org.openspotlight.storage.PartitionFactory;
import org.openspotlight.storage.RepositoryPath;
import org.openspotlight.storage.StringIDSupport;
import org.openspotlight.storage.domain.Link;
import org.openspotlight.storage.domain.Node;
import org.openspotlight.storage.domain.Property;
import org.openspotlight.storage.domain.PropertyContainer;
import org.openspotlight.storage.domain.key.NodeKey;
import org.openspotlight.storage.domain.key.NodeKey.CompositeKey.SimpleKey;
import org.openspotlight.storage.domain.node.LinkImpl;
import org.openspotlight.storage.domain.node.NodeImpl;
import org.openspotlight.storage.domain.node.PropertyImpl;

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
public class MongoStorageSessionImpl extends
        AbstractStorageSession<DBObject> {
    private final LinkedList<Pair<NodeKey, DBObject>>       objectCache      = newLinkedList();
    private final int                                       maxCacheSize;
    private static final String                             NULL_VALUE       = "!!!NULL!!!";
    private final Multimap<Partition, Pair<Node, DBObject>> transientObjects = HashMultimap
                                                                                            .create();
    private final Map<String, DB>                           partitionMap;
    private final Map<String, GridFS>                       gridFSMap;
    private final Map<Pair<String, String>, DBCollection>   collectionsMap   = newHashMap();

    private static final String                             ID               = "_id", LOCAL_ID = "node_local_id",
            PARENT_ID = "node_parent_id", KEY_NAMES = "node_key_names",
            PROPERTIES = "node_properties", INDEXED = "node_indexed",
            LINKS = "links", ENTRY_NAME = "node_entry_name";

    private final Mongo                                     mongo;
    private final RepositoryPath                            repositoryPath;

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

    private DB getCachedDbForPartition(final Partition partition) {
        return getCachedDbForPartition(partition.getPartitionName());
    }

    private DB getCachedDbForPartition(final String partitionName) {
        DB db = partitionMap.get(partitionName);
        if (db == null) {
            db = mongo.getDB(repositoryPath.getRepositoryPathAsString() + "_"
                             + partitionName);
            partitionMap.put(partitionName, db);
        }
        return db;
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

    @Override
    protected void internalSavePartitions(final Partition... partitions)
            throws Exception {
        for (final Partition partition: partitions) {
            for (final Pair<Node, DBObject> p: transientObjects
                                                                 .get(partition)) {
                final Node n = p.getK1();
                final DBCollection coll = getCachedCollection(partition, n
                                                                    .getType());
                coll.save(p.getK2());
            }
        }
        transientObjects.clear();

    }

    private DBObject findReferenceOrReturnNull(final Partition partition,
                                                final PropertyContainer entry) {
        DBObject basicDBObject = null;

        Node node;
        if (entry instanceof Node) {
            node = (Node) entry;
        } else if (entry instanceof Link) {
            node = ((Link) entry).getOrigin();
        } else {
            throw new IllegalStateException();
        }

        final Pair<Node, DBObject> p = Pair.<Node, DBObject>newPair(
                                                                            node, null, Pair.PairEqualsMode.K1);
        if (transientObjects.get(partition).contains(p)) {
            for (final Pair<Node, DBObject> pair: transientObjects
                                                                    .get(partition)) {
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

        final Pair<NodeKey, DBObject> p1 = newPair(key, null,
                                               Pair.PairEqualsMode.K1);
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
            objectCache.addFirst(newPair(key, basicDBObject,
                                         Pair.PairEqualsMode.K1));
            if (objectCache.size() > maxCacheSize) {
                objectCache.removeLast();
            }
        }
        return basicDBObject;
    }

    @Override
    protected byte[] internalPropertyGetValue(final Partition partition,
                                               final Property stProperty)
        throws Exception {
        byte[] value = null;
        if (stProperty.isKey()) {
            final Node parent = (Node) stProperty.getParent();
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
                    final DBObject innerObj = (DBObject) reference.get(PROPERTIES);
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

    private static String beforeRegex(final String s) {
        return s;
    }

    @Override
    protected Iterable<Node> internalFindByCriteria(
                                                            final Partition partition,
                                                            final Criteria criteria)
            throws Exception {

        final DBObject criteriaAsObj = new BasicDBObject();

        for (final CriteriaItem c: criteria.getCriteriaItems()) {
            if (c instanceof PropertyCriteriaItem) {
                final PropertyCriteriaItem p = (PropertyCriteriaItem) c;
                criteriaAsObj.put(INDEXED + "." + p.getPropertyName(), p
                                                                        .getValue() == null ? NULL_VALUE : p.getValue());

            }
            if (c instanceof PropertyContainsString) {
                final PropertyContainsString p = (PropertyContainsString) c;
                criteriaAsObj.put(
                    INDEXED + "." + p.getPropertyName(),
                    Pattern
                                                                              .compile("(.*)" + beforeRegex(p.getValue())
                                                                                  + "(.*)"));
            }
            if (c instanceof PropertyStartsWithString) {
                final PropertyStartsWithString p = (PropertyStartsWithString) c;
                criteriaAsObj.put(INDEXED + "." + p.getPropertyName(), Pattern
                                                                              .compile("^" + beforeRegex(p.getValue()) + "(.*)"));
            }
            if (c instanceof PropertyEndsWithString) {
                final PropertyEndsWithString p = (PropertyEndsWithString) c;
                criteriaAsObj.put(INDEXED + "." + p.getPropertyName(), Pattern
                                                                              .compile("(.*)" + beforeRegex(p.getValue()) + "$"));
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
            if (c instanceof CompisiteKeyCriteriaItem) {
                final CompisiteKeyCriteriaItem uniqueCriteria = (CompisiteKeyCriteriaItem) c;
                final String localHash = uniqueCriteria.getValue().getKeyAsString();
                criteriaAsObj.put(LOCAL_ID, localHash);
            }
        }

        final ImmutableSet.Builder<String> nodeNamesBuilder = ImmutableSet.builder();
        if (criteria.getNodeType() != null) {
            nodeNamesBuilder.add(criteria.getNodeType());
        } else {
            nodeNamesBuilder.addAll(getCachedDbForPartition(partition)
                                                                      .getCollectionNames());
        }
        final List<Iterable<DBObject>> dbCursors = newLinkedList();
        for (final String s: nodeNamesBuilder.build()) {
            final DBCursor resultAsDbObject = getCachedCollection(partition, s).find(
                                                                               criteriaAsObj);
            dbCursors.add(resultAsDbObject);

        }

        final IteratorBuilder.SimpleIteratorBuilder<Node, DBObject> b = IteratorBuilder
                                                                                        .createIteratorBuilder();
        b.withConverter(new IteratorBuilder.Converter<Node, DBObject>() {
            @Override
            public Node convert(final DBObject nodeEntry)
                throws Exception {
                return convertToNode(partition, nodeEntry);
            }
        });
        final Iterable<Node> result = b.withItems(
                                                   SLCollections.<DBObject>iterableOfAll(dbCursors)).andBuild();
        return result;

    }

    @Override
    protected void flushNewItem(final DBObject reference,
                                 final Partition partition,
                                 final Node entry)
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
        reference.put(ENTRY_NAME, uniqueId.getCompositeKey().getNodeName());
        if (FlushMode.AUTO.equals(getFlushMode())) {
            final DBCollection col = getCachedCollection(partition, entry
                                                                   .getType());
            col.save(reference);
        } else {
            final Pair<Node, DBObject> p = Pair
                                                .<Node, DBObject>newPair(entry, reference,
                                                                                Pair.PairEqualsMode.K1);
            if (!transientObjects.get(partition).contains(p)) {
                transientObjects.put(partition, p);
            }
        }
    }

    @Override
    protected void flushRemovedItem(final Partition partition,
                                     final Node entry)
            throws Exception {
        final DBCollection collection = getCachedCollection(partition, entry
                                                                      .getType());
        collection.remove(new BasicDBObject(ID, entry.getKey()
                                                     .getKeyAsString()));
    }

    @Override
    protected Iterable<Node> internalNodeEntryGetChildrenByType(
                                                                       final Partition initialPartition,
                                                                       final Node stNodeEntry,
                                                                       final String type)
            throws Exception {
        if (stNodeEntry == null) { return emptySet(); }
        return internalGetChildren(initialPartition, stNodeEntry, type);
    }

    private Iterable<Node> internalGetChildren(
                                                       final Partition partition,
                                                       final Node stNodeEntry,
                                                       final String name)
            throws Exception {
        final BasicDBObject baseDbObj = new BasicDBObject();
        baseDbObj.put(PARENT_ID, stNodeEntry.getKey().getKeyAsString());
        final ImmutableSet.Builder<String> names = ImmutableSet.builder();
        if (name != null) {
            names.add(name);
        } else {
            names.addAll(getCachedDbForPartition(partition)
                                                           .getCollectionNames());
        }
        final List<Iterable<DBObject>> dbCursors = newLinkedList();
        for (final String s: names.build()) {
            final DBCursor resultAsDbObject = getCachedCollection(partition, s).find(
                                                                               baseDbObj);
            dbCursors.add(resultAsDbObject);

        }

        final IteratorBuilder.SimpleIteratorBuilder<Node, DBObject> b = IteratorBuilder
                                                                                        .createIteratorBuilder();
        b.withConverter(new IteratorBuilder.Converter<Node, DBObject>() {
            @Override
            public Node convert(final DBObject nodeEntry)
                throws Exception {
                return convertToNode(partition, nodeEntry);
            }
        });
        final Iterable<Node> result = b.withItems(
                                                   SLCollections.<DBObject>iterableOfAll(dbCursors)).andBuild();
        return result;
    }

    @Override
    protected void internalFlushSimpleProperty(final DBObject possibleReference,
                                                final Partition partition,
                                                final Property dirtyProperty)
        throws Exception {
        DBObject reference;
        String collectionName;
        if (possibleReference != null) {
            reference = possibleReference;
            collectionName = StringIDSupport
                                            .getNodeEntryName((String) reference.get(ID));
        } else if (dirtyProperty.getParent() instanceof Node) {
            reference = createNodeReferenceIfNecessary(partition,
                                                       (Node) dirtyProperty.getParent());
            collectionName = ((Node) dirtyProperty.getParent())
                                                                     .getType();
        } else if (dirtyProperty.getParent() instanceof Link) {
            reference = createLinkReferenceIfNecessary(partition,
                                                       (Link) dirtyProperty.getParent());
            collectionName = ((Link) dirtyProperty.getParent())
                                                                     .getOrigin().getType();

        } else {
            throw new IllegalStateException();
        }

        String objName = null;
        Object value = null;

        if (dirtyProperty.isIndexed()) {
            ensureIndexed(partition, collectionName, INDEXED, dirtyProperty
                                                                           .getPropertyName(), null);
            objName = INDEXED;
            value = ((PropertyImpl) dirtyProperty).getTransientValueAsString(this);
            if (value == null) {
                value = NULL_VALUE;
            }
        } else if (!dirtyProperty.isKey()) {
            objName = PROPERTIES;
            value = ((PropertyImpl) dirtyProperty).getTransientValueAsBytes(this);
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
            Node nodeEntry;
            if (dirtyProperty.getParent() instanceof Node) {
                nodeEntry = (Node) dirtyProperty.getParent();
            } else if (dirtyProperty.getParent() instanceof Link) {
                nodeEntry = ((Link) dirtyProperty.getParent())
                                                                    .getOrigin();
            } else {
                throw new IllegalStateException();
            }

            if (FlushMode.AUTO.equals(getFlushMode())) {
                getCachedCollection(partition, nodeEntry.getType())
                                                                            .save(reference);
            } else {
                final Pair<Node, DBObject> p = newPair(nodeEntry, reference,
                                                        Pair.PairEqualsMode.K1);
                if (!transientObjects.get(partition).contains(p)) {
                    transientObjects.put(partition, p);
                }

            }
        }

    }

    private String getBigPropertyName(final Property dirtyProperty) {
        return "big_" + dirtyProperty.getPropertyName();
    }

    private static final String _ = "_";

    public void storeInGridFS(final Partition partition,
                               final Property property,
                               final byte[] value)
        throws Exception {
        final String key = getFileName(partition, property);
        final GridFS fs = getCachedGridFSForPartition(partition);
        final GridFSInputFile file = fs.createFile(value);
        file.setFilename(key);
        file.save();

    }

    private String getFileName(final Partition partition,
                                final Property dirtyProperty) {
        Node nodeEntry;
        if (dirtyProperty.getParent() instanceof Node) {
            nodeEntry = (Node) dirtyProperty.getParent();
        } else if (dirtyProperty.getParent() instanceof Link) {
            nodeEntry = ((Link) dirtyProperty.getParent()).getOrigin();
        } else {
            throw new IllegalStateException();
        }

        final String key = partition.getPartitionName() + _
                     + nodeEntry.getKey().getKeyAsString() + _
                     + dirtyProperty.getPropertyName();
        return key;
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

    boolean isBiggerThan4mb(final byte[] bytes) {
        return (double) (bytes == null ? 0 : bytes.length) / (1024 * 1024) > 4.0;
    }

    Set<String> allIndexes = newHashSet();

    private void ensureIndexed(final Partition partition,
                                final String parentName,
                                final String groupName,
                                final String propertyName,
                                final Link possibleParentAsLink) {
        final String key = partition.getPartitionName() + parentName + groupName
                     + propertyName;
        if (!allIndexes.contains(key)) {
            allIndexes.add(key);
            getCachedCollection(partition, parentName).ensureIndex(
                                                                   groupName != null ? (groupName + "." + propertyName)
                                                                       : propertyName);
        }
    }

    @Override
    protected Iterable<Node> internalNodeEntryGetChildren(
                                                                  final Partition partition,
                                                                  final Node stNodeEntry)
        throws Exception {
        if (stNodeEntry == null) { return emptySet(); }
        return internalGetChildren(partition, stNodeEntry, null);

    }

    @Override
    protected Node internalNodeEntryGetParent(final Partition partition,
                                                      final Node stNodeEntry)
        throws Exception {
        final String parentKey = stNodeEntry.getKey().getParentKeyAsString();
        if (parentKey == null) { return null; }
        final Partition parentPartition = getPartition(parentKey, partitionFactory);
        final String parentName = getNodeEntryName(parentKey);
        final BasicDBObject parameter = new BasicDBObject();
        parameter.put(ID, parentKey);
        final DBCollection collection = getCachedCollection(parentPartition,
                                                      parentName);
        final DBObject result = collection.findOne(parameter);
        return convertToNode(parentPartition, result);
    }

    @Override
    protected Iterable<Node> internalFindByType(final Partition partition,
                                                       final String nodeType)
        throws Exception {
        final DBCursor cursor = getCachedCollection(partition, nodeType).find();
        final ImmutableSet.Builder<Node> builder = ImmutableSet.builder();
        while (cursor.hasNext()) {
            builder.add(convertToNode(partition, cursor.next()));
        }
        return builder.build();
    }

    private Node convertToNode(final Partition partition,
                                       final DBObject dbObject)
            throws Exception {
        final DBObject keyAsDbObj = (DBObject) dbObject.get(INDEXED);
        final List<String> keyNames = (List<String>) dbObject.get(KEY_NAMES);

        final NodeKeyBuilder keyBuilder = withPartition(partition)
                                          .createKey((String) dbObject.get(ENTRY_NAME));
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
        final Node node = new NodeImpl(uniqueKey, false);
        return node;
    }

    private final PartitionFactory partitionFactory;

    @Inject
    public MongoStorageSessionImpl(final Mongo mongo, final FlushMode flushMode,
                                      final RepositoryPath repositoryPath,
                                      final PartitionFactory partitionFactory, final int maxCacheSize) {
        super(flushMode, repositoryPath, partitionFactory);
        this.partitionFactory = partitionFactory;
        this.maxCacheSize = maxCacheSize;
        partitionMap = newHashMap();
        this.mongo = mongo;
        this.repositoryPath = repositoryPath;
        gridFSMap = newHashMap();
    }

    @Override
    protected Iterable<String> internalGetAllNodeTypes(final Partition partition) {
        final HashSet<String> set = new HashSet<String>();
        set.addAll(getCachedDbForPartition(partition).getCollectionNames());
        set.remove("system.indexes");
        return ImmutableSet.copyOf(set);
    }

    @Override
    protected DBObject createLinkReferenceIfNecessary(final Partition partition,
                                                       final Link link) {
        final Node origin = link.getOrigin();
        final DBObject nodeRef = createNodeReferenceIfNecessary(partition, origin);
        DBObject linkRef = null;
        @SuppressWarnings("unchecked")
        List<DBObject> links = (List<DBObject>) nodeRef.get(LINKS);
        if (links == null) {
            links = new ArrayList<DBObject>();
            linkRef = new BasicDBObject();
            links.add(linkRef);
            linkRef.put(ID, link.getLinkId());
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
                linkRef.put(ID, link.getLinkId());
            }
        }

        nodeRef.put(LINKS, links);
        return linkRef;
    }

    @Override
    protected void flushRemovedLink(final Partition partition,
                                     final Link link) {
        final DBObject basicDBObject = findReferenceOrReturnNull(partition, link
                                                                          .getOrigin());
        if (basicDBObject != null) {
            @SuppressWarnings("unchecked")
            final List<DBObject> links = (List<DBObject>) basicDBObject.get(LINKS);
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
    protected Iterable<Link> internalFindLinks(
                                                       final Partition partition,
                                                       final Node origin,
                                                       final Node destiny,
                                                       final String name) {
        final Builder<String> rawItems = ImmutableList.builder();
        final DBObject basicDBObject = findReferenceOrReturnNull(partition, origin);
        if (basicDBObject != null) {
            @SuppressWarnings("unchecked")
            final List<DBObject> links = (List<DBObject>) basicDBObject.get(LINKS);
            if (links != null) {
                for (final DBObject possibleLink: links) {
                    final String linkId = (String) possibleLink.get(ID);
                    if (name != null && destiny != null) {
                        if (StringIDSupport.getLinkNameFromLinkKey(linkId)
                                           .equals(name)
                                && StringIDSupport
                                                  .getTargeyKeyAsStringFromLinkKey(linkId)
                                                  .equals(destiny.getKeyAsString())) {
                            rawItems.add(linkId);

                        }

                    } else if (name != null) {
                        if (StringIDSupport.getLinkNameFromLinkKey(linkId)
                                           .equals(name)) {
                            rawItems.add(linkId);
                        }
                    } else if (destiny != null) {
                        if (StringIDSupport.getTargeyKeyAsStringFromLinkKey(
                                                                            linkId).equals(destiny.getKeyAsString())) {
                            rawItems.add(linkId);

                        }
                    } else {
                        rawItems.add(linkId);
                    }
                }
            }
        }
        @SuppressWarnings("unchecked")
        final SimpleIteratorBuilder<Link, String> result =
            IteratorBuilder
                                                                           .<Link, String>createIteratorBuilder()
                .withItems(
                                                                                                                                   rawItems
                                                                                                                                       .build())
                .withConverter(
                                                                                                                                                                   new Converter<Link, String>() {

                                                                                                                                                                       @Override
                                                                                                                                                                       public
                                                                                                                                                                           Link
                                                                                                                                                                           convert(final String o)
                                                                                                                                                                               throws Exception {
                                                                                                                                                                           Node foundTarget =
                                                                                                                                                                               destiny;
                                                                                                                                                                           if (foundTarget == null) {
                                                                                                                                                                               final String targetId =
                                                                                                                                                                                   StringIDSupport
                                                                                                                                                                                                                .getTargeyKeyAsStringFromLinkKey(o);
                                                                                                                                                                               final Partition targetPartition =
                                                                                                                                                                                   partitionFactory
                                                                                                                                                                                       .getPartitionByName(StringIDSupport
                                                                                                                                                                                           .getPartitionName(targetId));

                                                                                                                                                                               foundTarget =
                                                                                                                                                                                   withPartition(
                                                                                                                                                                                       targetPartition)
                                                                                                                                                                                                                           .createCriteria()
                                                                                                                                                                                                                           .withUniqueKeyAsString(
                                                                                                                                                                                                                               targetId)
                                                                                                                                                                                                                           .buildCriteria()
                                                                                                                                                                                                                           .andFindUnique(
                                                                                                                                                                                                                                          MongoStorageSessionImpl.this);
                                                                                                                                                                               if (foundTarget == null) {
                                                                                                                                                                                throw new IllegalStateException();
                                                                                                                                                                            }
                                                                                                                                                                        }
                                                                                                                                                                        final String foundName =
                                                                                                                                                                               StringIDSupport
                                                                                                                                                                                                             .getLinkNameFromLinkKey(o);
                                                                                                                                                                        return new LinkImpl(
                                                                                                                                                                               foundName,
                                                                                                                                                                               origin,
                                                                                                                                                                                                      foundTarget,
                                                                                                                                                                               true);
                                                                                                                                                                    }
                                                                                                                                                                   });
        return result.andBuild();
    }

    @Override
    protected Set<Property> internalPropertyContainerLoadProperties(
                                                                     final DBObject possibleReference,
                                                                     final Partition partition,
                                                                     final PropertyContainer propertyContainer)
        throws Exception {

        if (propertyContainer instanceof Node) {
            final Node nodeEntry = (Node) propertyContainer;
            final ImmutableSet.Builder<Property> builder = ImmutableSet.builder();
            for (final SimpleKey entry: nodeEntry.getKey().getCompositeKey()
                                      .getKeys()) {
                final PropertyImpl p = PropertyImpl.createKey(entry
                                                             .getKeyName(), propertyContainer);
                (p).setStringValueOnLoad(this,
                                                       entry.getValue());
                builder.add(p);
            }
            final DBObject reference = possibleReference == null ? createNodeReferenceIfNecessary(
                                                                                            partition, nodeEntry)
                    : possibleReference;
            final DBObject indexed = (DBObject) reference.get(INDEXED);
            final List<String> keyNames = (List<String>) reference.get(KEY_NAMES);
            if (indexed != null) {
                for (final String s: indexed.keySet()) {
                    if (!keyNames.contains(s)) {
                        final PropertyImpl p = PropertyImpl.createIndexed(s,
                                                                    propertyContainer);
                        String value = (String) indexed.get(s);
                        if (NULL_VALUE.equals(value)) {
                            value = null;
                        }
                        (p).setStringValueOnLoad(this, value);
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

        } else if (propertyContainer instanceof Link) {
            final Link linkEntry = (Link) propertyContainer;
            final ImmutableSet.Builder<Property> builder = ImmutableSet.builder();
            final DBObject reference = createLinkReferenceIfNecessary(partition,
                                                                linkEntry);
            final DBObject indexed = (DBObject) reference.get(INDEXED);
            if (indexed != null) {
                for (final String s: indexed.keySet()) {
                    final PropertyImpl p = PropertyImpl.createIndexed(s,
                                                                propertyContainer);
                    String value = (String) indexed.get(s);
                    if (NULL_VALUE.equals(value)) {
                        value = null;
                    }
                    (p).setStringValueOnLoad(this, value);
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
    protected DBObject createNodeReferenceIfNecessary(final Partition partition,
                                                       final Node entry) {
        final DBObject basicDBObject = findReferenceOrReturnNull(partition, entry);
        final Pair<Node, DBObject> p = Pair.<Node, DBObject>newPair(
                                                                            entry, basicDBObject, Pair.PairEqualsMode.K1);
        if (!transientObjects.get(partition).contains(p)) {
            transientObjects.put(partition, p);
        }
        return basicDBObject;
    }

    @Override
    protected void handleNewLink(final Partition partition,
                                  final Node origin,
                                  final Link link)
        throws Exception {
        createLinkReferenceIfNecessary(partition, link);
        if (getFlushMode().equals(FlushMode.AUTO)) {
            final DBObject nodeRef = createNodeReferenceIfNecessary(partition, origin);
            final DBCollection col = getCachedCollection(partition, origin
                                                                    .getType());
            col.save(nodeRef);

        }
    }

}
