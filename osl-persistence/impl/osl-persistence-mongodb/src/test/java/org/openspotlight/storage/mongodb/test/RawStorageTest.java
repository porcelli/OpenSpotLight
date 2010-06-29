package org.openspotlight.storage.mongodb.test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.Mongo;
import org.junit.Test;

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
