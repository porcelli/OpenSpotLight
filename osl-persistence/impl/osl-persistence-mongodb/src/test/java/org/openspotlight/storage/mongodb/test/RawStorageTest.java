package org.openspotlight.storage.mongodb.test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import org.junit.Test;

/**
 * Created by User: feu - Date: Jun 9, 2010 - Time: 10:24:03 AM
 */
public class RawStorageTest {


    @Test
    public void shouldStoreAndRetrieveSomething() throws Exception {

        Mongo m = new Mongo();
        DB db = m.getDB("mydb");

        BasicDBObject doc = new BasicDBObject();
        DBCollection coll = db.getCollection("testCollection");

        doc.put("name", "MongoDB");
        doc.put("type", "database");
        doc.put("count", 1);

        BasicDBObject info = new BasicDBObject();

        info.put("x", 203);
        info.put("y", 102);

        doc.put("info", info);

        coll.insert(doc);


    }

}
