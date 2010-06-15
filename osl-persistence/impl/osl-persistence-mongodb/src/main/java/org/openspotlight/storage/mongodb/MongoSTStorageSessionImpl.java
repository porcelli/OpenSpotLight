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

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.mongodb.*;
import org.openspotlight.storage.*;
import org.openspotlight.storage.domain.key.STKeyEntry;
import org.openspotlight.storage.domain.key.STUniqueKey;
import org.openspotlight.storage.domain.node.STNodeEntry;
import org.openspotlight.storage.domain.node.STProperty;

import java.util.*;

import static com.google.common.collect.Maps.newHashMap;


/**
 * Created by User: feu - Date: Mar 23, 2010 - Time: 4:46:25 PM
 */
public class MongoSTStorageSessionImpl extends AbstractSTStorageSession<DBObject> {


    private final Map<String, DB> partitionMap;

    private static final String
            ID = "_id",
            LOCAL_ID = "node-local-id",
            PARENT_ID = "node-parent-id",
            CHILDREN = "node-children",
            MULTIPLE_PARENT_PATH = "multiple-parent-path",
            MPP_KEY = "mpp-key",
            MPP_PARTITION = "mpp-partition",
            MPP_ROOT = "mpp-root",
            MPP_KEY_NAME = "mpp-key-name",
            KEY = "node-key",
            ENTRY_NAME = "node-entry-name",
            ROOT_NODE = "node-root";


    private final Mongo mongo;
    private final STRepositoryPath repositoryPath;

    private DB getDbForPartition(STPartition partition) {
        return getDbForPartition(partition.getPartitionName());
    }

    private DB getDbForPartition(String partitionName) {
        DB db = partitionMap.get(partitionName);
        if (db == null) {
            db = mongo.getDB(repositoryPath.getRepositoryPathAsString() + "/" + partitionName);
            partitionMap.put(partitionName, db);
        }
        return db;
    }

    private WeakHashMap<STNodeEntry, BasicDBObject> cache = new WeakHashMap();


    @Override
    protected void internalSavePartitions(STPartition... partitions) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected DBObject createReferenceIfNecessary(STPartition partition, STNodeEntry entry) {
        DBObject basicDBObject = findReferenceOrReturnNull(partition, entry);
        if (basicDBObject == null) {
            basicDBObject = new BasicDBObject();
            basicDBObject.put(ID, entry.getUniqueKey().getKeyAsString());
        }
        return basicDBObject;
    }

    private DBObject findReferenceOrReturnNull(STPartition partition, STNodeEntry entry) {
        DB db = getDbForPartition(partition);
        DBCollection coll = db.getCollection(entry.getNodeEntryName());
        BasicDBObject queryObject = new BasicDBObject();
        queryObject.put(ID, entry.getUniqueKey().getKeyAsString());
        DBObject basicDBObject = coll.findOne(queryObject);
        if (basicDBObject == null) {
            basicDBObject = new BasicDBObject();
            queryObject.put(ID, entry.getUniqueKey().getKeyAsString());
        }
        return basicDBObject;
    }

    @Override
    protected byte[] internalPropertyGetValue(STPartition partition, STProperty stProperty) throws Exception {
        DBObject reference = findReferenceOrReturnNull(partition, stProperty.getParent());
        if (reference != null) {
            byte[] value = (byte[]) reference.get(stProperty.getPropertyName());
            return value;
        }
        return null;
    }

    @Override
    protected Set<STNodeEntry> internalFindByCriteria(STPartition partition, STCriteria criteria) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
            key.put(keyEntry.getPropertyName(), keyEntry.getValue() != null ? keyEntry.getValue().getBytes() : null);
        }
        reference.put(KEY, key);
        reference.put(ENTRY_NAME, uniqueId.getLocalKey().getNodeEntryName());
        reference.put(ROOT_NODE, uniqueId.getLocalKey().isRootKey());
        getDbForPartition(partition).getCollection(entry.getNodeEntryName()).save(reference);
    }

    @Override
    protected void flushRemovedItem(STPartition partition, STNodeEntry entry) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected Set<STNodeEntry> internalNodeEntryGetNamedChildren(STPartition partition, STNodeEntry stNodeEntry, String name) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void internalFlushSimpleProperty(DBObject reference, STPartition partition, STProperty dirtyProperty) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected Set<STNodeEntry> internalNodeEntryGetChildren(STPartition partition, STNodeEntry stNodeEntry) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected STNodeEntry internalNodeEntryGetParent(STPartition partition, STNodeEntry stNodeEntry) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected Set<STProperty> internalNodeEntryLoadProperties(DBObject reference, STPartition partition, STNodeEntry stNodeEntry) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected Set<STNodeEntry> internalFindNamed(STPartition partition, String nodeEntryName) throws Exception {
        DBCursor cursor = getDbForPartition(partition).getCollection(nodeEntryName).find();
        Iterator<DBObject> it = cursor.iterator();
        ImmutableSet.Builder<STNodeEntry> builder = ImmutableSet.builder();
        while (it.hasNext()) {
            builder.add(convertToNode(partition, it.next()));
        }
        return builder.build();
    }

    private STNodeEntry convertToNode(STPartition partition, DBObject dbObject) {
        DBObject keyAsDbObj = (DBObject) dbObject.get(KEY);
        List<DBObject> list = (List<DBObject>) keyAsDbObj.get(MULTIPLE_PARENT_PATH);
        STKeyEntry key;
        STUniqueKey lastKey = null;

                                          STUniqueKeyBuilder keyBuilder=null;
//        this.withPartition(partitionFactory.getPartitionByName((String) o.get(MPP_PARTITION))).createKey((String) o.get(MPP_KEY_NAME), (Boolean) o.get(MPP_ROOT));
        for (DBObject o:list) {
//            if(keyBuilder==null){
//                keyBuilder =
//            }else{
//                keyBuilder.withParent()
//            }

            o.keySet();


        }

        return null;  //To change body of created methods use File | Settings | File Templates.
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
