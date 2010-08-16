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
import org.openspotlight.storage.Partition;
import org.openspotlight.storage.PartitionFactory;
import org.openspotlight.storage.RepositoryPath;
import org.openspotlight.storage.StringIDSupport;
import org.openspotlight.storage.domain.STLinkEntry;
import org.openspotlight.storage.domain.STNodeEntry;
import org.openspotlight.storage.domain.Property;
import org.openspotlight.storage.domain.PropertyContainer;
import org.openspotlight.storage.domain.key.Key;
import org.openspotlight.storage.domain.key.UniqueKey;
import org.openspotlight.storage.domain.node.STLinkEntryImpl;
import org.openspotlight.storage.domain.node.STNodeEntryImpl;
import org.openspotlight.storage.domain.node.PropertyImpl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableList.Builder;
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
public class MongoSTStorageSessionImpl extends
        AbstractStorageSession<DBObject> {
    private final LinkedList<Pair<UniqueKey, DBObject>>      objectCache      = newLinkedList();
    private final int                                        maxCacheSize;
    private static final String                              NULL_VALUE       = "!!!NULL!!!";
    private Multimap<Partition, Pair<STNodeEntry, DBObject>> transientObjects = HashMultimap
                                                                                            .create();
    private final Map<String, DB>                            partitionMap;
    private final Map<String, GridFS>                        gridFSMap;
    private final Map<Pair<String, String>, DBCollection>    collectionsMap   = newHashMap();

    private static final String                              ID               = "_id", LOCAL_ID = "node_local_id",
            PARENT_ID = "node_parent_id", KEY_NAMES = "node_key_names",
            PROPERTIES = "node_properties", INDEXED = "node_indexed",
            LINKS = "links", ENTRY_NAME = "node_entry_name";

    private final Mongo                                      mongo;
    private final RepositoryPath                             repositoryPath;

    private GridFS getCachedGridFSForPartition( Partition partition ) {
        return getCachedGridFSForPartition(partition.getPartitionName());
    }

    private GridFS getCachedGridFSForPartition( String partition ) {
        GridFS fs = gridFSMap.get(partition);
        if (fs == null) {
            DB db = getCachedDbForPartition(partition);
            fs = new GridFS(db);
            gridFSMap.put(partition, fs);
        }
        return fs;

    }

    private DB getCachedDbForPartition( Partition partition ) {
        return getCachedDbForPartition(partition.getPartitionName());
    }

    private DB getCachedDbForPartition( String partitionName ) {
        DB db = partitionMap.get(partitionName);
        if (db == null) {
            db = mongo.getDB(repositoryPath.getRepositoryPathAsString() + "_"
                             + partitionName);
            partitionMap.put(partitionName, db);
        }
        return db;
    }

    private DBCollection getCachedCollection( Partition partition,
                                              String collectionName ) {
        return getCachedCollection(partition.getPartitionName(), collectionName);
    }

    private DBCollection getCachedCollection( String partition,
                                              String collectionName ) {
        Pair<String, String> key = newPair(partition, collectionName);
        DBCollection collection = collectionsMap.get(key);
        if (collection == null) {
            DB db = getCachedDbForPartition(partition);
            collection = db.getCollection(collectionName);
            collectionsMap.put(key, collection);
        }
        return collection;
    }

    @Override
    protected void internalSavePartitions( Partition... partitions )
            throws Exception {
        for (Partition partition : partitions) {
            for (Pair<STNodeEntry, DBObject> p : transientObjects
                                                                 .get(partition)) {
                STNodeEntry n = (STNodeEntry)p.getK1();
                DBCollection coll = getCachedCollection(partition, n
                                                                    .getNodeEntryName());
                coll.save(p.getK2());
            }
        }
        transientObjects.clear();

    }

    private DBObject findReferenceOrReturnNull( Partition partition,
                                                PropertyContainer entry ) {
        DBObject basicDBObject = null;

        STNodeEntry node;
        if (entry instanceof STNodeEntry) {
            node = (STNodeEntry)entry;
        } else if (entry instanceof STLinkEntry) {
            node = ((STLinkEntry)entry).getOrigin();
        } else {
            throw new IllegalStateException();
        }

        Pair<STNodeEntry, DBObject> p = Pair.<STNodeEntry, DBObject>newPair(
                                                                            node, null, Pair.PairEqualsMode.K1);
        if (transientObjects.get(partition).contains(p)) {
            for (Pair<STNodeEntry, DBObject> pair : transientObjects
                                                                    .get(partition)) {
                if (pair.equals(p)) {
                    basicDBObject = pair.getK2();
                    break;
                }
            }
        }
        UniqueKey key;
        String collectionName;
        key = node.getUniqueKey();
        collectionName = node.getNodeEntryName();

        Pair<UniqueKey, DBObject> p1 = newPair(key, null,
                                               Pair.PairEqualsMode.K1);
        int idx = objectCache.indexOf(p1);
        if (idx != -1) {
            basicDBObject = objectCache.get(idx).getK2();
        }
        if (basicDBObject == null) {
            DBCollection coll = getCachedCollection(partition, collectionName);
            BasicDBObject queryObject = new BasicDBObject();
            queryObject.put(ID, key.getKeyAsString());
            basicDBObject = coll.findOne(queryObject);
            if (basicDBObject == null) {
                basicDBObject = new BasicDBObject();
                basicDBObject.put(ID, key.getKeyAsString());
            }
            objectCache.addFirst(newPair(key, basicDBObject,
                                         Pair.PairEqualsMode.K1));
            if (objectCache.size() > maxCacheSize)
                objectCache.removeLast();
        }
        return basicDBObject;
    }

    @Override
    protected byte[] internalPropertyGetValue( Partition partition,
                                               Property stProperty ) throws Exception {
        byte[] value = null;
        if (stProperty.isKey()) {
            STNodeEntry parent = (STNodeEntry)stProperty.getParent();
            for (Key e : parent.getUniqueKey().getLocalKey()
                               .getEntries()) {
                if (e.getPropertyName().equals(stProperty.getPropertyName())) {
                    value = e.getValue() != null ? e.getValue().getBytes()
                            : null;
                    if (NULL_VALUE.equals(new String(value)))
                        value = null;
                    break;
                }
            }

        } else {
            DBObject reference = findReferenceOrReturnNull(partition,
                                                           stProperty.getParent());
            if (reference != null) {
                if (stProperty.isIndexed()) {
                    DBObject innerObj = (DBObject)reference.get(INDEXED);
                    if (innerObj != null) {
                        value = ((String)innerObj.get(stProperty
                                                                .getPropertyName())).getBytes();
                        if (NULL_VALUE.equals(new String(value)))
                            value = null;
                    }
                } else {
                    DBObject innerObj = (DBObject)reference.get(PROPERTIES);
                    if (innerObj != null) {
                        Boolean isBig = (Boolean)innerObj
                                                         .get(getBigPropertyName(stProperty));
                        if (Boolean.TRUE.equals(isBig)) {
                            value = readAsGridFS(partition, stProperty);

                        } else {
                            value = (byte[])innerObj.get(stProperty
                                                                   .getPropertyName());
                        }
                        if (NULL_VALUE.equals(new String(value)))
                            value = null;
                    }
                }
            }
        }
        return value;
    }

    private static String beforeRegex( String s ) {
        return s;
    }

    @Override
    protected Iterable<STNodeEntry> internalFindByCriteria(
                                                            final Partition partition,
                                                            final Criteria criteria )
            throws Exception {

        DBObject criteriaAsObj = new BasicDBObject();

        for (CriteriaItem c : criteria.getCriteriaItems()) {
            if (c instanceof PropertyCriteriaItem) {
                PropertyCriteriaItem p = (PropertyCriteriaItem)c;
                criteriaAsObj.put(INDEXED + "." + p.getPropertyName(), p
                                                                        .getValue() == null ? NULL_VALUE : p.getValue());

            }
            if (c instanceof PropertyContainsString) {
                PropertyContainsString p = (PropertyContainsString)c;
                criteriaAsObj.put(INDEXED + "." + p.getPropertyName(), Pattern
                                                                              .compile("(.*)" + beforeRegex(p.getValue()) + "(.*)"));
            }
            if (c instanceof PropertyStartsWithString) {
                PropertyStartsWithString p = (PropertyStartsWithString)c;
                criteriaAsObj.put(INDEXED + "." + p.getPropertyName(), Pattern
                                                                              .compile("^" + beforeRegex(p.getValue()) + "(.*)"));
            }
            if (c instanceof PropertyEndsWithString) {
                PropertyEndsWithString p = (PropertyEndsWithString)c;
                criteriaAsObj.put(INDEXED + "." + p.getPropertyName(), Pattern
                                                                              .compile("(.*)" + beforeRegex(p.getValue()) + "$"));
            }
            if (c instanceof UniqueKeyCriteriaItem) {
                UniqueKeyCriteriaItem uniqueCriteria = (UniqueKeyCriteriaItem)c;
                criteriaAsObj.put(ID, uniqueCriteria.getValue()
                                                    .getKeyAsString());
            }
            if (c instanceof UniqueKeyAsStringCriteriaItem) {
                UniqueKeyAsStringCriteriaItem uniqueCriteria = (UniqueKeyAsStringCriteriaItem)c;
                criteriaAsObj.put(ID, uniqueCriteria.getKeyAsString());
            }
            if (c instanceof LocalKeyCriteriaItem) {
                LocalKeyCriteriaItem uniqueCriteria = (LocalKeyCriteriaItem)c;
                String localHash = uniqueCriteria.getValue().getKeyAsString();
                criteriaAsObj.put(LOCAL_ID, localHash);
            }
        }

        ImmutableSet.Builder<String> nodeNamesBuilder = ImmutableSet.builder();
        if (criteria.getNodeName() != null) {
            nodeNamesBuilder.add(criteria.getNodeName());
        } else {
            nodeNamesBuilder.addAll(getCachedDbForPartition(partition)
                                                                      .getCollectionNames());
        }
        List<Iterable<DBObject>> dbCursors = newLinkedList();
        for (String s : nodeNamesBuilder.build()) {
            DBCursor resultAsDbObject = getCachedCollection(partition, s).find(
                                                                               criteriaAsObj);
            dbCursors.add(resultAsDbObject);

        }

        IteratorBuilder.SimpleIteratorBuilder<STNodeEntry, DBObject> b = IteratorBuilder
                                                                                        .createIteratorBuilder();
        b.withConverter(new IteratorBuilder.Converter<STNodeEntry, DBObject>() {
            @Override
            public STNodeEntry convert( DBObject nodeEntry ) throws Exception {
                return convertToNode(partition, nodeEntry);
            }
        });
        Iterable<STNodeEntry> result = b.withItems(
                                                   SLCollections.<DBObject>iterableOfAll(dbCursors)).andBuild();
        return result;

    }

    @Override
    protected void flushNewItem( DBObject reference,
                                 Partition partition,
                                 STNodeEntry entry ) throws Exception {
        reference.put(LOCAL_ID, entry.getUniqueKey().getLocalKey()
                                     .getKeyAsString());
        ensureIndexed(partition, entry.getNodeEntryName(), null, LOCAL_ID, null);

        final UniqueKey uniqueId = entry.getUniqueKey();
        final String parentId = uniqueId.getParentKeyAsString();
        if (parentId != null) {
            reference.put(PARENT_ID, parentId);
        }
        BasicDBObject key = new BasicDBObject();
        List<String> keyNames = newArrayList();
        for (Key keyEntry : uniqueId.getLocalKey().getEntries()) {
            keyNames.add(keyEntry.getPropertyName());
            key.put(keyEntry.getPropertyName(),
                    keyEntry.getValue() != null ? keyEntry.getValue()
                            : NULL_VALUE);
            ensureIndexed(partition, entry.getNodeEntryName(), INDEXED,
                          keyEntry.getPropertyName(), null);

        }
        reference.put(ID, uniqueId.getKeyAsString());
        reference.put(KEY_NAMES, keyNames);
        reference.put(INDEXED, key);
        reference.put(ENTRY_NAME, uniqueId.getLocalKey().getNodeEntryName());
        if (FlushMode.AUTO.equals(getFlushMode())) {
            DBCollection col = getCachedCollection(partition, entry
                                                                   .getNodeEntryName());
            col.save(reference);
        } else {
            Pair<STNodeEntry, DBObject> p = Pair
                                                .<STNodeEntry, DBObject>newPair(entry, reference,
                                                                                Pair.PairEqualsMode.K1);
            if (!transientObjects.get(partition).contains(p))
                transientObjects.put(partition, p);
        }
    }

    @Override
    protected void flushRemovedItem( Partition partition,
                                     STNodeEntry entry )
            throws Exception {
        DBCollection collection = getCachedCollection(partition, entry
                                                                      .getNodeEntryName());
        collection.remove(new BasicDBObject(ID, entry.getUniqueKey()
                                                     .getKeyAsString()));
    }

    @Override
    protected Iterable<STNodeEntry> internalNodeEntryGetNamedChildren(
                                                                       Partition initialPartition,
                                                                       STNodeEntry stNodeEntry,
                                                                       String name )
            throws Exception {
        if (stNodeEntry == null)
            return emptySet();
        return internalGetChildren(initialPartition, stNodeEntry, name);
    }

    private Iterable<STNodeEntry> internalGetChildren(
                                                       final Partition partition,
                                                       STNodeEntry stNodeEntry,
                                                       String name )
            throws Exception {
        BasicDBObject baseDbObj = new BasicDBObject();
        baseDbObj.put(PARENT_ID, stNodeEntry.getUniqueKey().getKeyAsString());
        ImmutableSet.Builder<String> names = ImmutableSet.builder();
        if (name != null) {
            names.add(name);
        } else {
            names.addAll(getCachedDbForPartition(partition)
                                                           .getCollectionNames());
        }
        List<Iterable<DBObject>> dbCursors = newLinkedList();
        for (String s : names.build()) {
            DBCursor resultAsDbObject = getCachedCollection(partition, s).find(
                                                                               baseDbObj);
            dbCursors.add(resultAsDbObject);

        }

        IteratorBuilder.SimpleIteratorBuilder<STNodeEntry, DBObject> b = IteratorBuilder
                                                                                        .createIteratorBuilder();
        b.withConverter(new IteratorBuilder.Converter<STNodeEntry, DBObject>() {
            @Override
            public STNodeEntry convert( DBObject nodeEntry ) throws Exception {
                return convertToNode(partition, nodeEntry);
            }
        });
        Iterable<STNodeEntry> result = b.withItems(
                                                   SLCollections.<DBObject>iterableOfAll(dbCursors)).andBuild();
        return result;
    }

    @Override
    protected void internalFlushSimpleProperty( DBObject possibleReference,
                                                Partition partition,
                                                Property dirtyProperty ) throws Exception {
        DBObject reference;
        String collectionName;
        if (possibleReference != null) {
            reference = possibleReference;
            collectionName = StringIDSupport
                                            .getNodeEntryName((String)reference.get(ID));
        } else if (dirtyProperty.getParent() instanceof STNodeEntry) {
            reference = createNodeReferenceIfNecessary(partition,
                                                       (STNodeEntry)dirtyProperty.getParent());
            collectionName = ((STNodeEntry)dirtyProperty.getParent())
                                                                     .getNodeEntryName();
        } else if (dirtyProperty.getParent() instanceof STLinkEntry) {
            reference = createLinkReferenceIfNecessary(partition,
                                                       (STLinkEntry)dirtyProperty.getParent());
            collectionName = ((STLinkEntry)dirtyProperty.getParent())
                                                                     .getOrigin().getNodeEntryName();

        } else {
            throw new IllegalStateException();
        }

        String objName = null;
        Object value = null;

        if (dirtyProperty.isIndexed()) {
            ensureIndexed(partition, collectionName, INDEXED, dirtyProperty
                                                                           .getPropertyName(), null);
            objName = INDEXED;
            value = ((PropertyImpl)dirtyProperty).getTransientValueAsString(this);
            if (value == null)
                value = NULL_VALUE;
        } else if (!dirtyProperty.isKey()) {
            objName = PROPERTIES;
            value = ((PropertyImpl)dirtyProperty).getTransientValueAsBytes(this);
        }
        if (objName == null)
            return;
        DBObject obj = (DBObject)reference.get(objName);
        if (obj == null) {
            obj = new BasicDBObject();
            reference.put(objName, obj);
        }
        if (value instanceof byte[] && isBiggerThan4mb((byte[])value)) {
            obj.put(getBigPropertyName(dirtyProperty), true);
        } else {
            obj.removeField(getBigPropertyName(dirtyProperty));
            obj.put(dirtyProperty.getPropertyName(), value);
            STNodeEntry nodeEntry;
            if (dirtyProperty.getParent() instanceof STNodeEntry) {
                nodeEntry = (STNodeEntry)dirtyProperty.getParent();
            } else if (dirtyProperty.getParent() instanceof STLinkEntry) {
                nodeEntry = ((STLinkEntry)dirtyProperty.getParent())
                                                                    .getOrigin();
            } else {
                throw new IllegalStateException();
            }

            if (FlushMode.AUTO.equals(getFlushMode())) {
                getCachedCollection(partition, nodeEntry.getNodeEntryName())
                                                                            .save(reference);
            } else {
                Pair<STNodeEntry, DBObject> p = newPair(nodeEntry, reference,
                                                        Pair.PairEqualsMode.K1);
                if (!transientObjects.get(partition).contains(p))
                    transientObjects.put(partition, p);

            }
        }

    }

    private String getBigPropertyName( Property dirtyProperty ) {
        return "big_" + dirtyProperty.getPropertyName();
    }

    private static final String _ = "_";

    public void storeInGridFS( Partition partition,
                               Property property,
                               byte[] value ) throws Exception {
        String key = getFileName(partition, property);
        GridFS fs = getCachedGridFSForPartition(partition);
        GridFSInputFile file = fs.createFile(value);
        file.setFilename(key);
        file.save();

    }

    private String getFileName( Partition partition,
                                Property dirtyProperty ) {
        STNodeEntry nodeEntry;
        if (dirtyProperty.getParent() instanceof STNodeEntry) {
            nodeEntry = (STNodeEntry)dirtyProperty.getParent();
        } else if (dirtyProperty.getParent() instanceof STLinkEntry) {
            nodeEntry = ((STLinkEntry)dirtyProperty.getParent()).getOrigin();
        } else {
            throw new IllegalStateException();
        }

        String key = partition.getPartitionName() + _
                     + nodeEntry.getUniqueKey().getKeyAsString() + _
                     + dirtyProperty.getPropertyName();
        return key;
    }

    public byte[] readAsGridFS( Partition partition,
                                Property property )
            throws Exception {
        String key = getFileName(partition, property);
        GridFS fs = getCachedGridFSForPartition(partition);
        GridFSDBFile file = fs.findOne(key);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(file.getInputStream(), baos);
        return baos.toByteArray();
    }

    boolean isBiggerThan4mb( byte[] bytes ) {
        return (double)(bytes == null ? 0 : bytes.length) / (1024 * 1024) > 4.0;
    }

    Set<String> allIndexes = newHashSet();

    private void ensureIndexed( Partition partition,
                                String parentName,
                                String groupName,
                                String propertyName,
                                STLinkEntry possibleParentAsLink ) {
        String key = partition.getPartitionName() + parentName + groupName
                     + propertyName;
        if (!allIndexes.contains(key)) {
            allIndexes.add(key);
            getCachedCollection(partition, parentName).ensureIndex(
                                                                   groupName != null ? (groupName + "." + propertyName)
                                                                       : propertyName);
        }
    }

    @Override
    protected Iterable<STNodeEntry> internalNodeEntryGetChildren(
                                                                  Partition partition,
                                                                  STNodeEntry stNodeEntry ) throws Exception {
        if (stNodeEntry == null)
            return emptySet();
        return internalGetChildren(partition, stNodeEntry, null);

    }

    @Override
    protected STNodeEntry internalNodeEntryGetParent( Partition partition,
                                                      STNodeEntry stNodeEntry ) throws Exception {
        String parentKey = stNodeEntry.getUniqueKey().getParentKeyAsString();
        if (parentKey == null)
            return null;
        Partition parentPartition = getPartition(parentKey, partitionFactory);
        String parentName = getNodeEntryName(parentKey);
        BasicDBObject parameter = new BasicDBObject();
        parameter.put(ID, parentKey);
        DBCollection collection = getCachedCollection(parentPartition,
                                                      parentName);
        DBObject result = collection.findOne(parameter);
        return convertToNode(parentPartition, result);
    }

    @Override
    protected Iterable<STNodeEntry> internalFindNamed( Partition partition,
                                                       String nodeEntryName ) throws Exception {
        DBCursor cursor = getCachedCollection(partition, nodeEntryName).find();
        ImmutableSet.Builder<STNodeEntry> builder = ImmutableSet.builder();
        while (cursor.hasNext()) {
            builder.add(convertToNode(partition, cursor.next()));
        }
        return builder.build();
    }

    private STNodeEntry convertToNode( Partition partition,
                                       DBObject dbObject )
            throws Exception {
        DBObject keyAsDbObj = (DBObject)dbObject.get(INDEXED);
        List<String> keyNames = (List<String>)dbObject.get(KEY_NAMES);

        UniqueKeyBuilder keyBuilder = this.withPartition(partition)
                                          .createKey((String)dbObject.get(ENTRY_NAME));
        for (String s : keyAsDbObj.keySet()) {
            if (keyNames.contains(s)) {
                String valueAsString = convert(keyAsDbObj.get(s), String.class);
                if (NULL_VALUE.equals(valueAsString))
                    valueAsString = null;
                keyBuilder.withEntry(s, valueAsString);
            }
        }
        String parentId = (String)dbObject.get(PARENT_ID);
        if (parentId != null) {
            keyBuilder.withParent(parentId);
        }
        UniqueKey uniqueKey = keyBuilder.andCreate();
        STNodeEntry node = new STNodeEntryImpl(uniqueKey, false);
        return node;
    }

    private final PartitionFactory partitionFactory;

    @Inject
    public MongoSTStorageSessionImpl( Mongo mongo, FlushMode flushMode,
                                      RepositoryPath repositoryPath,
                                      PartitionFactory partitionFactory, int maxCacheSize ) {
        super(flushMode, repositoryPath, partitionFactory);
        this.partitionFactory = partitionFactory;
        this.maxCacheSize = maxCacheSize;
        this.partitionMap = newHashMap();
        this.mongo = mongo;
        this.repositoryPath = repositoryPath;
        this.gridFSMap = newHashMap();
    }

    @Override
    protected Iterable<String> internalGetAllNodeNames( Partition partition ) {
        HashSet<String> set = new HashSet<String>();
        set.addAll(getCachedDbForPartition(partition).getCollectionNames());
        set.remove("system.indexes");
        return ImmutableSet.copyOf(set);
    }

    @Override
    protected DBObject createLinkReferenceIfNecessary( Partition partition,
                                                       STLinkEntry link ) {
        STNodeEntry origin = link.getOrigin();
        DBObject nodeRef = createNodeReferenceIfNecessary(partition, origin);
        DBObject linkRef = null;
        @SuppressWarnings( "unchecked" )
        List<DBObject> links = (List<DBObject>)nodeRef.get(LINKS);
        if (links == null) {
            links = new ArrayList<DBObject>();
            linkRef = new BasicDBObject();
            links.add(linkRef);
            linkRef.put(ID, link.getLinkId());
            nodeRef.put(LINKS, links);
        } else {
            for (DBObject possibleLink : links) {
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
    protected void flushRemovedLink( Partition partition,
                                     STLinkEntry link ) {
        DBObject basicDBObject = findReferenceOrReturnNull(partition, link
                                                                          .getOrigin());
        if (basicDBObject != null) {
            @SuppressWarnings( "unchecked" )
            List<DBObject> links = (List<DBObject>)basicDBObject.get(LINKS);
            if (links != null) {
                for (DBObject possibleLink : links) {
                    if (possibleLink.get(ID).equals(link.getKeyAsString())) {
                        links.remove(possibleLink);
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected Iterable<STLinkEntry> internalFindLinks(
                                                       final Partition partition,
                                                       final STNodeEntry origin,
                                                       final STNodeEntry destiny,
                                                       final String name ) {
        Builder<String> rawItems = ImmutableList.builder();
        DBObject basicDBObject = findReferenceOrReturnNull(partition, origin);
        if (basicDBObject != null) {
            @SuppressWarnings( "unchecked" )
            List<DBObject> links = (List<DBObject>)basicDBObject.get(LINKS);
            if (links != null) {
                for (DBObject possibleLink : links) {
                    String linkId = (String)possibleLink.get(ID);
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
        @SuppressWarnings( "unchecked" )
        SimpleIteratorBuilder<STLinkEntry, String> result = IteratorBuilder
                                                                           .<STLinkEntry, String>createIteratorBuilder().withItems(
                                                                                                                                   rawItems.build()).withConverter(
                                                                                                                                                                   new Converter<STLinkEntry, String>() {

                                                                                                                                                                       @Override
                                                                                                                                                                       public STLinkEntry convert( String o )
                                                                                                                                                                           throws Exception {
                                                                                                                                                                           STNodeEntry foundTarget = destiny;
                                                                                                                                                                           if (foundTarget == null) {
                                                                                                                                                                               String targetId = StringIDSupport
                                                                                                                                                                                                                .getTargeyKeyAsStringFromLinkKey(o);
                                                                                                                                                                               Partition targetPartition = partitionFactory.getPartitionByName(StringIDSupport.getPartitionName(targetId));

                                                                                                                                                                               foundTarget = withPartition(targetPartition)
                                                                                                                                                                                                                           .createCriteria()
                                                                                                                                                                                                                           .withUniqueKeyAsString(targetId)
                                                                                                                                                                                                                           .buildCriteria()
                                                                                                                                                                                                                           .andFindUnique(
                                                                                                                                                                                                                                          MongoSTStorageSessionImpl.this);
                                                                                                                                                                               if (foundTarget == null) throw new IllegalStateException();
                                                                                                                                                                           }
                                                                                                                                                                           String foundName = StringIDSupport
                                                                                                                                                                                                             .getLinkNameFromLinkKey(o);
                                                                                                                                                                           return new STLinkEntryImpl(foundName, origin,
                                                                                                                                                                                                      foundTarget, true);
                                                                                                                                                                       }
                                                                                                                                                                   });
        return result.andBuild();
    }

    @Override
    protected Set<Property> internalPropertyContainerLoadProperties(
                                                                     DBObject possibleReference,
                                                                     Partition partition,
                                                                     PropertyContainer propertyContainer ) throws Exception {

        if (propertyContainer instanceof STNodeEntry) {
            STNodeEntry nodeEntry = (STNodeEntry)propertyContainer;
            ImmutableSet.Builder<Property> builder = ImmutableSet.builder();
            for (Key entry : nodeEntry.getUniqueKey().getLocalKey()
                                      .getEntries()) {
                PropertyImpl p = PropertyImpl.createKey(entry
                                                             .getPropertyName(), propertyContainer);
                ((PropertyImpl)p).setStringValueOnLoad(this,
                                                       entry.getValue());
                builder.add(p);
            }
            DBObject reference = possibleReference == null ? createNodeReferenceIfNecessary(
                                                                                            partition, nodeEntry)
                    : possibleReference;
            DBObject indexed = (DBObject)reference.get(INDEXED);
            List<String> keyNames = (List<String>)reference.get(KEY_NAMES);
            if (indexed != null) {
                for (String s : indexed.keySet()) {
                    if (!keyNames.contains(s)) {
                        PropertyImpl p = PropertyImpl.createIndexed(s,
                                                                    propertyContainer);
                        String value = (String)indexed.get(s);
                        if (NULL_VALUE.equals(value))
                            value = null;
                        ((PropertyImpl)p).setStringValueOnLoad(this, value);
                        builder.add(p);
                    }
                }
            }

            DBObject properties = (DBObject)reference.get(PROPERTIES);
            if (properties != null) {
                for (String s : properties.keySet()) {
                    PropertyImpl p = PropertyImpl.createSimple(s,
                                                               propertyContainer);
                    builder.add(p);
                }
            }

            return builder.build();

        } else if (propertyContainer instanceof STLinkEntry) {
            STLinkEntry linkEntry = (STLinkEntry)propertyContainer;
            ImmutableSet.Builder<Property> builder = ImmutableSet.builder();
            DBObject reference = createLinkReferenceIfNecessary(partition,
                                                                linkEntry);
            DBObject indexed = (DBObject)reference.get(INDEXED);
            if (indexed != null) {
                for (String s : indexed.keySet()) {
                    PropertyImpl p = PropertyImpl.createIndexed(s,
                                                                propertyContainer);
                    String value = (String)indexed.get(s);
                    if (NULL_VALUE.equals(value))
                        value = null;
                    ((PropertyImpl)p).setStringValueOnLoad(this, value);
                    builder.add(p);
                }
            }

            DBObject properties = (DBObject)reference.get(PROPERTIES);
            if (properties != null) {
                for (String s : properties.keySet()) {
                    PropertyImpl p = PropertyImpl.createSimple(s,
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
    protected DBObject createNodeReferenceIfNecessary( Partition partition,
                                                       STNodeEntry entry ) {
        DBObject basicDBObject = findReferenceOrReturnNull(partition, entry);
        Pair<STNodeEntry, DBObject> p = Pair.<STNodeEntry, DBObject>newPair(
                                                                            entry, basicDBObject, Pair.PairEqualsMode.K1);
        if (!transientObjects.get(partition).contains(p))
            transientObjects.put(partition, p);
        return basicDBObject;
    }

    @Override
    protected void handleNewLink( Partition partition,
                                  STNodeEntry origin,
                                  STLinkEntry link ) throws Exception {
        createLinkReferenceIfNecessary(partition, link);
        if (getFlushMode().equals(FlushMode.AUTO)) {
            DBObject nodeRef = createNodeReferenceIfNecessary(partition, origin);
            DBCollection col = getCachedCollection(partition, origin
                                                                    .getNodeEntryName());
            col.save(nodeRef);

        }
    }

}
