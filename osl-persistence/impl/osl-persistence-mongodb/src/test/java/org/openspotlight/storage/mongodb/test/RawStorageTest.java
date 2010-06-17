package org.openspotlight.storage.mongodb.test;

import com.mongodb.*;
import org.junit.Test;

import java.util.regex.Pattern;

/**
 * Created by User: feu - Date: Jun 9, 2010 - Time: 10:24:03 AM
 */
public class RawStorageTest {




    @Test
    public void shouldStoreAndRetrieveSomething() throws Exception {

        Mongo m = new Mongo();
        DB db = m.getDB("mydb");

        BasicDBObject doc = new BasicDBObject();
        DBCollection coll = db.getCollection("aa");

        doc.put("name", "MongoDB");
        doc.put("type", "database");
        doc.put("count", 1);

        BasicDBObject info = new BasicDBObject();

        info.put("x", 203);
        info.put("y", 102);

        doc.put("info", info);

        doc.put("_id", "alalala");
        coll.save(doc);
        Object id = doc.get("_id");
        System.err.println("ID:" + id);
        BasicDBObject obj = new BasicDBObject();
        obj.put("_id", "alalala");
        DBCursor cur = coll.find(obj);
        while (cur.hasNext()) {
            System.err.println(cur.next());
        }
        System.err.println("dam!");


    }

}
