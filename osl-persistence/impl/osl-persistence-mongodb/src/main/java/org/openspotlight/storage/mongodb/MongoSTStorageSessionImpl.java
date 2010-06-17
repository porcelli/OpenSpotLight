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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.mongodb.*;
import org.openspotlight.common.Pair;
import org.openspotlight.storage.AbstractSTStorageSession;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STPartitionFactory;
import org.openspotlight.storage.STRepositoryPath;
import org.openspotlight.storage.domain.key.STKeyEntry;
import org.openspotlight.storage.domain.key.STUniqueKey;
import org.openspotlight.storage.domain.node.STNodeEntry;
import org.openspotlight.storage.domain.node.STNodeEntryImpl;
import org.openspotlight.storage.domain.node.STProperty;
import org.openspotlight.storage.domain.node.STPropertyImpl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.emptySet;
import static org.openspotlight.common.Pair.newPair;
import static org.openspotlight.common.util.Conversion.convert;


/**
 * Created by User: feu - Date: Mar 23, 2010 - Time: 4:46:25 PM
 */
public class MongoSTStorageSessionImpl extends AbstractSTStorageSession<DBObject> {


    private static final String NULL_VALUE = "!!!NULL!!!";
    private Multimap<STPartition, Pair<STNodeEntry, DBObject>> transientObjects = HashMultimap.create();
    private final Map<String, DB> partitionMap;

    private static final String
            ID = "_id",
            LOCAL_ID = "node_local_id",
            PARENT_ID = "node_parent_id",
            MULTIPLE_PARENT_PATH = "multiple_parent_path",
            MPP_KEY = "mpp_key",
            MPP_PARTITION = "mpp_partition",
            MPP_ROOT = "mpp_root",
            MPP_KEY_NAME = "mpp_key_name",
            KEY = "node_key",
            PROPERTIES = "node_properties",
            INDEXED = "node_indexed",
            ENTRY_NAME = "node_entry_name",
            ROOT_NODE = "node_root";


    private final Mongo mongo;
    private final STRepositoryPath repositoryPath;

    private DB getDbForPartition(STPartition partition) {
        return getDbForPartition(partition.getPartitionName());
    }

    private DB getDbForPartition(String partitionName) {
        DB db = partitionMap.get(partitionName);
        if (db == null) {
            db = mongo.getDB(repositoryPath.getRepositoryPathAsString() + "_" + partitionName);
            partitionMap.put(partitionName, db);
        }
        return db;
    }

    @Override
    protected void internalSavePartitions(STPartition... partitions) throws Exception {
        for (STPartition partition : partitions) {
            DB db = getDbForPartition(partition);
            for (Pair<STNodeEntry, DBObject> p : transientObjects.get(partition)) {
                DBCollection coll = db.getCollection(p.getK1().getNodeEntryName());
                coll.save(p.getK2());
            }
        }
        transientObjects.clear();
    }

    @Override
    protected DBObject createReferenceIfNecessary(STPartition partition, STNodeEntry entry) {
        DBObject basicDBObject = findReferenceOrReturnNull(partition, entry);
        if (basicDBObject == null) {
            basicDBObject = new BasicDBObject();
            basicDBObject.put(ID, entry.getUniqueKey().getKeyAsString());
        }
        transientObjects.put(partition,newPair(entry,basicDBObject));
        return basicDBObject;
    }

    private DBObject findReferenceOrReturnNull(STPartition partition, STNodeEntry entry) {
        DBObject basicDBObject = null;
        for (Pair<STNodeEntry, DBObject> pair : transientObjects.get(partition)) {
            if (pair.getK1().equals(entry)) {
                basicDBObject = pair.getK2();
                break;
            }
        }
        if (basicDBObject == null) {
            DB db = getDbForPartition(partition);
            DBCollection coll = db.getCollection(entry.getNodeEntryName());
            BasicDBObject queryObject = new BasicDBObject();
            queryObject.put(ID, entry.getUniqueKey().getKeyAsString());
            basicDBObject = coll.findOne(queryObject);
            if (basicDBObject == null) {
                basicDBObject = new BasicDBObject();
                queryObject.put(ID, entry.getUniqueKey().getKeyAsString());
            }
        }
        return basicDBObject;
    }

    @Override
    protected byte[] internalPropertyGetValue(STPartition partition, STProperty stProperty) throws Exception {
        byte[] value = null;
        if (stProperty.isKey()) {
            for (STKeyEntry e : stProperty.getParent().getUniqueKey().getLocalKey().getEntries()) {
                if (e.getPropertyName().equals(stProperty.getPropertyName())) {
                    value = e.getValue() != null ? e.getValue().getBytes() : null;
                    break;
                }
            }

        } else {
            DBObject reference = findReferenceOrReturnNull(partition, stProperty.getParent());
            if (reference != null) {
                if (stProperty.isIndexed()) {
                    DBObject innerObj = (DBObject) reference.get(INDEXED);
                    if (innerObj != null) {
                        value = ((String) innerObj.get(stProperty.getPropertyName())).getBytes();
                        if (NULL_VALUE.equals(value)) value = null;
                    }

                } else {
                    DBObject innerObj = (DBObject) reference.get(PROPERTIES);
                    if (innerObj != null)
                        value = (byte[]) innerObj.get(stProperty.getPropertyName());

                }
            }

        }
        return value;
    }

    private static String beforeRegex(String s) {
        return s;
    }

    @Override
    protected Set<STNodeEntry> internalFindByCriteria(STPartition partition, STCriteria criteria) throws Exception {
        DB db = getDbForPartition(partition);

        DBObject possibleKeyParameter = new BasicDBObject();
        DBObject possibleIndexParameter = new BasicDBObject();

        for (STCriteriaItem c : criteria.getCriteriaItems()) {
            if (c instanceof STPropertyCriteriaItem) {
                STPropertyCriteriaItem p = (STPropertyCriteriaItem) c;
                possibleKeyParameter.put(KEY + "." + p.getPropertyName(), p.getValue() == null ? NULL_VALUE : p.getValue());
                possibleIndexParameter.put(INDEXED + "." + p.getPropertyName(), p.getValue() == null ? NULL_VALUE : p.getValue());

            }
            if (c instanceof STPropertyContainsString) {
                STPropertyContainsString p = (STPropertyContainsString) c;
                possibleKeyParameter.put(KEY + "." + p.getPropertyName(), Pattern.compile("(.*)" + beforeRegex(p.getValue()) + "(.*)"));
                possibleIndexParameter.put(INDEXED + "." + p.getPropertyName(), Pattern.compile("(.*)" + beforeRegex(p.getValue()) + "(.*)"));
            }
            if (c instanceof STPropertyStartsWithString) {
                STPropertyStartsWithString p = (STPropertyStartsWithString) c;
                possibleKeyParameter.put(KEY + "." + p.getPropertyName(), Pattern.compile("^" + beforeRegex(p.getValue()) + "(.*)"));
                possibleIndexParameter.put(INDEXED + "." + p.getPropertyName(), Pattern.compile("^" + beforeRegex(p.getValue()) + "(.*)"));
            }
            if (c instanceof STPropertyEndsWithString) {
                STPropertyEndsWithString p = (STPropertyEndsWithString) c;
                possibleKeyParameter.put(KEY + "." + p.getPropertyName(), Pattern.compile("(.*)" + beforeRegex(p.getValue()) + "$"));
                possibleIndexParameter.put(INDEXED + "." + p.getPropertyName(), Pattern.compile("(.*)" + beforeRegex(p.getValue()) + "$"));
            }
            if (c instanceof STUniqueKeyCriteriaItem) {
                STUniqueKeyCriteriaItem uniqueCriteria = (STUniqueKeyCriteriaItem) c;
                possibleIndexParameter.put(ID, uniqueCriteria.getValue().getKeyAsString());
                possibleKeyParameter.put(ID, uniqueCriteria.getValue().getKeyAsString());

            }
            if (c instanceof STLocalKeyCriteriaItem) {
                STLocalKeyCriteriaItem uniqueCriteria = (STLocalKeyCriteriaItem) c;
                String localHash = uniqueCriteria.getValue().getKeyAsString();
                possibleIndexParameter.put(LOCAL_ID, localHash);
                possibleKeyParameter.put(LOCAL_ID, localHash);

            }
        }

        ImmutableSet.Builder<STNodeEntry> resultBuilder = ImmutableSet.builder();

        for (DBObject criteriaAsObj : ImmutableSet.of(possibleIndexParameter, possibleKeyParameter)) {
            ImmutableSet.Builder<String> nodeNamesBuilder = ImmutableSet.builder();
            if (criteria.getNodeName() != null) {
                nodeNamesBuilder.add(criteria.getNodeName());
            } else {
                nodeNamesBuilder.addAll(db.getCollectionNames());
            }
            for (String s : nodeNamesBuilder.build()) {
                DBCursor resultAsDbObject = db.getCollection(s).find(criteriaAsObj);
                while (resultAsDbObject.hasNext()) {
                    resultBuilder.add(convertToNode(partition, resultAsDbObject.next()));
                }

            }
        }

        return resultBuilder.build();

    }


    @Override
    protected void flushNewItem(DBObject reference, STPartition partition, STNodeEntry entry) throws Exception {
        reference.put(LOCAL_ID, entry.getUniqueKey().getLocalKey().getKeyAsString());
        final STUniqueKey uniqueId = entry.getUniqueKey();
        final STUniqueKey parentId = uniqueId.getParentKey();
        if (parentId != null) {
            reference.put(PARENT_ID, parentId.getKeyAsString());
            BasicDBList parentList = new BasicDBList();
            STUniqueKey tempParentId = parentId;
            while (tempParentId != null) {
                BasicDBObject parentListItem = new BasicDBObject();
                parentListItem.put(MPP_KEY, tempParentId.getKeyAsString());
                parentListItem.put(MPP_PARTITION, tempParentId.getPartition().getPartitionName());
                parentListItem.put(MPP_ROOT, tempParentId.getLocalKey().isRootKey());
                parentListItem.put(MPP_KEY_NAME, tempParentId.getLocalKey().getNodeEntryName());
                for (STKeyEntry ke : tempParentId.getLocalKey().getEntries()) {
                    parentListItem.put(ke.getPropertyName(), ke.getValue());
                }
                parentList.add(parentListItem);
                tempParentId = tempParentId.getParentKey();
            }
            reference.put(MULTIPLE_PARENT_PATH, parentList);
        }
        BasicDBObject key = new BasicDBObject();

        for (STKeyEntry keyEntry : uniqueId.getLocalKey().getEntries()) {
            key.put(keyEntry.getPropertyName(), keyEntry.getValue() != null ? keyEntry.getValue() : NULL_VALUE);
        }
        reference.put(ID, uniqueId.getKeyAsString());
        reference.put(KEY, key);
        reference.put(ENTRY_NAME, uniqueId.getLocalKey().getNodeEntryName());
        reference.put(ROOT_NODE, uniqueId.getLocalKey().isRootKey());
        if (STFlushMode.AUTO.equals(getFlushMode())) {
            DB db = getDbForPartition(partition);
            DBCollection col = db.getCollection(entry.getNodeEntryName());
            col.save(reference);
        } else {
            transientObjects.put(partition, newPair(entry, reference));
        }
    }

    @Override
    protected void flushRemovedItem(STPartition partition, STNodeEntry entry) throws Exception {
        DB db = getDbForPartition(partition);
        DBCollection collection = db.getCollection(entry.getNodeEntryName());
        collection.remove(new BasicDBObject(ID, entry.getUniqueKey().getKeyAsString()));
    }

    @Override
    protected Set<STNodeEntry> internalNodeEntryGetNamedChildren(STPartition initialPartition, STNodeEntry stNodeEntry, String name) throws Exception {
        if (stNodeEntry == null) return emptySet();
        ImmutableSet.Builder<STNodeEntry> resultBuilder = ImmutableSet.builder();
        internalGetChildren(resultBuilder, stNodeEntry, name);
        return resultBuilder.build();
    }

    private void internalGetChildren(ImmutableSet.Builder<STNodeEntry> resultBuilder, STNodeEntry stNodeEntry, String name) throws Exception {
        STPartition[] partitions = partitionFactory.getValues();
        for (STPartition partition : partitions) {
            DB db = getDbForPartition(partition);
            BasicDBObject baseDbObj = new BasicDBObject();
            baseDbObj.put(PARENT_ID, stNodeEntry.getUniqueKey().getKeyAsString());
            ImmutableSet.Builder<String> names = ImmutableSet.builder();
            if (name != null) {
                names.add(name);
            } else {
                names.addAll(db.getCollectionNames());
            }
            for (String n : names.build()) {
                DBCursor resultAsDbObj = db.getCollection(n).find(baseDbObj);
                while (resultAsDbObj.hasNext()) {
                    resultBuilder.add(convertToNode(partition, resultAsDbObj.next()));
                }
            }
        }

    }

    @Override
    protected void internalFlushSimpleProperty(DBObject possibleReference, STPartition partition, STProperty dirtyProperty) throws Exception {
        DBObject reference = possibleReference != null ? possibleReference : createReferenceIfNecessary(partition, dirtyProperty.getParent());
        String objName = null;
        Object value = null;
        if (dirtyProperty.isIndexed()) {
            objName = INDEXED;
            value = dirtyProperty.getInternalMethods().getTransientValueAsString(this);
            if (value == null) value = NULL_VALUE;
        } else if (!dirtyProperty.isKey()) {
            objName = PROPERTIES;
            value = dirtyProperty.getInternalMethods().getTransientValueAsBytes(this);
        }
        if (objName == null) return;
        DBObject obj = (DBObject) reference.get(objName);
        if (obj == null) {
            obj = new BasicDBObject();
            reference.put(objName, obj);
        }
        obj.put(dirtyProperty.getPropertyName(), value);
        if (STFlushMode.AUTO.equals(getFlushMode())) {
            DB db = getDbForPartition(partition);
            db.getCollection(dirtyProperty.getParent().getNodeEntryName()).save(reference);
        } else {
            transientObjects.put(partition, newPair(dirtyProperty.getParent(), reference));
        }


    }

    @Override
    protected Set<STNodeEntry> internalNodeEntryGetChildren(STPartition partition, STNodeEntry stNodeEntry) throws Exception {
        if (stNodeEntry == null) return emptySet();
        ImmutableSet.Builder<STNodeEntry> resultBuilder = ImmutableSet.builder();
        internalGetChildren(resultBuilder, stNodeEntry, null);
        return resultBuilder.build();
    }

    @Override
    protected STNodeEntry internalNodeEntryGetParent(STPartition partition, STNodeEntry stNodeEntry) throws Exception {
        STUniqueKey parentKey = stNodeEntry.getUniqueKey().getParentKey();
        if (parentKey == null) return null;
        STPartition parentPartition = parentKey.getPartition();
        String parentName = parentKey.getLocalKey().getNodeEntryName();
        String parentIdAsString = parentKey.getKeyAsString();
        BasicDBObject parameter = new BasicDBObject();
        parameter.put(ID, parentIdAsString);
        DB db = getDbForPartition(parentPartition);
        DBCollection collection = db.getCollection(parentName);
        DBObject result = collection.findOne(parameter);
        return convertToNode(parentPartition, result);
    }

    @Override
    protected Set<STProperty> internalNodeEntryLoadProperties(DBObject possibleReference, STPartition partition, STNodeEntry stNodeEntry) throws Exception {
        ImmutableSet.Builder<STProperty> builder = ImmutableSet.builder();
        for (STKeyEntry entry : stNodeEntry.getUniqueKey().getLocalKey().getEntries()) {
            STPropertyImpl p = STPropertyImpl.createKey(entry.getPropertyName(), stNodeEntry);
            p.setStringValue(this, entry.getValue());
            builder.add(p);
        }
        DBObject reference = possibleReference != null ? possibleReference : createReferenceIfNecessary(partition, stNodeEntry);
        DBObject indexed = (DBObject) reference.get(INDEXED);
        if (indexed != null) {
            for (String s : indexed.keySet()) {
                STPropertyImpl p = STPropertyImpl.createIndexed(s, stNodeEntry);
                builder.add(p);
            }
        }

        DBObject properties = (DBObject) reference.get(PROPERTIES);
        if (properties != null) {
            for (String s : properties.keySet()) {
                STPropertyImpl p = STPropertyImpl.createSimple(s, stNodeEntry);
                builder.add(p);
            }
        }

        return builder.build();
    }

    @Override
    protected Set<STNodeEntry> internalFindNamed(STPartition partition, String nodeEntryName) throws Exception {
        DBCursor cursor = getDbForPartition(partition).getCollection(nodeEntryName).find();
        ImmutableSet.Builder<STNodeEntry> builder = ImmutableSet.builder();
        while (cursor.hasNext()) {
            builder.add(convertToNode(partition, cursor.next()));
        }
        return builder.build();
    }

    private STNodeEntry convertToNode(STPartition partition, DBObject dbObject) throws Exception {
        DBObject keyAsDbObj = (DBObject) dbObject.get(KEY);
        List<DBObject> list = (List<DBObject>) dbObject.get(MULTIPLE_PARENT_PATH);


        STUniqueKeyBuilder keyBuilder = this.withPartition(partition).createKey(
                (String) dbObject.get(ENTRY_NAME), (Boolean) dbObject.get(ROOT_NODE));
        for (String s : keyAsDbObj.keySet()) {
            String valueAsString = convert(keyAsDbObj.get(s), String.class);
            if (NULL_VALUE.equals(valueAsString)) valueAsString = null;
            keyBuilder.withEntry(s, valueAsString);
        }

        if (list != null) {
            for (DBObject o : list) {
                keyBuilder = keyBuilder.withParent(partitionFactory.getPartitionByName((String) o.get(MPP_PARTITION)),
                        (String) o.get(MPP_KEY_NAME), (Boolean) o.get(MPP_ROOT));
                for (String s : o.keySet()) {
                    if (MPP_KEY.equals(s) || MPP_KEY_NAME.equals(s) || MPP_PARTITION.equals(s) || MPP_ROOT.equals(s))
                        continue;
                    String valueAsString = (String) o.get(s);
                    if (NULL_VALUE.equals(valueAsString)) valueAsString = null;
                    keyBuilder.withEntry(s, valueAsString);
                }
            }
        }
        STUniqueKey uniqueKey = keyBuilder.andCreate();
        STNodeEntry node = new STNodeEntryImpl(uniqueKey, false);
        return node;
    }

    private final STPartitionFactory partitionFactory;

    @Inject
    public MongoSTStorageSessionImpl(Mongo mongo, STFlushMode flushMode, STRepositoryPath repositoryPath, STPartitionFactory partitionFactory) {
        super(flushMode, repositoryPath);
        this.partitionFactory = partitionFactory;
        this.partitionMap = newHashMap();
        this.mongo = mongo;
        this.repositoryPath = repositoryPath;
    }


}
