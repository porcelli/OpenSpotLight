package org.openspotlight.storage.mongodb.test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
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

        final MongoSTStorageSessionProvider provider = new MongoSTStorageSessionProvider(STStorageSession.STFlushMode.AUTO,repositoryPath("repository"),new Mongo(),new DefaultSTPartitionFactory());

        ExecutorService s = Executors.newFixedThreadPool(4);

        final ThreadLocal<Random> rnd = new ThreadLocal<Random>(){
            @Override
            protected Random initialValue() {
                return new Random();
            }
        };

        for (int i = 0; i < 4; i++) {

            s.execute(new Runnable() {

                @Override
                public void run() {
                    while (true) {

                        STStorageSession session = provider.createInstance();
                        STNodeEntry newNode = session.withPartition(SLPartition.FEDERATION).createWithName("sample",false).withKey("id",Integer.toString(rnd.get().nextInt())).andCreate();
                    }
                }
            });
        }
         s.awaitTermination(50000, TimeUnit.HOURS);
    }

}
