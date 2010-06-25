package org.openspotlight.storage.mongodb.test;

import com.google.common.collect.Multimap;
import com.mongodb.*;
import org.junit.Test;
import org.openspotlight.storage.DefaultSTPartitionFactory;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.SLPartition;
import org.openspotlight.storage.domain.node.STNodeEntry;
import sun.java2d.pipe.SpanShapeRenderer;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

/**
 * Created by User: feu - Date: Jun 9, 2010 - Time: 10:24:03 AM
 */
public class RawStorageTest {


    @Test
    public void shouldStoreAndRetrieveSomething() throws Exception {

        Mongo m  = new Mongo();
        DB db = m.getDB("db");
        BasicDBObject someObj = new BasicDBObject("_id","sameID");
                someObj.put("la","la");
                db.getCollection("col").save(someObj);
        BasicDBObject sameObj = new BasicDBObject("_id","sameID");
                someObj.put("le","le");
                db.getCollection("col").save(sameObj);

    }

}
