package org.openspotlight.federation.finder.test;

import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.mongodb.test.MongoModule;

import com.google.inject.Module;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.Mongo;

/**
 * Created by User: feu - Date: Jun 18, 2010 - Time: 12:26:16 PM
 */
public class MongoFileSystemLoadingStressTest extends AbstractFileSystemLoadingStressTest {
    private Mongo mongo;

    @Override
    protected void clearData()
        throws Exception {
        final BasicDBObject dbObject = new BasicDBObject();
        for (final String dbName: mongo.getDatabaseNames()) {
            final DB db = mongo.getDB(dbName);
            for (final String colName: db.getCollectionNames()) {
                db.getCollection(colName).remove(dbObject);
            }
        }
    }

    @Override
    protected Module createStorageModule()
        throws Exception {
        mongo = new Mongo();
        return new MongoModule(StorageSession.FlushMode.EXPLICIT, mongo);
    }
}
