package org.openspotlight.storage.mongodb.test;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.Mongo;
import org.openspotlight.guice.ThreadLocalProvider;
import org.openspotlight.storage.STPartitionFactory;
import org.openspotlight.storage.STRepositoryPath;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.mongodb.MongoMaxCacheSize;
import org.openspotlight.storage.mongodb.MongoSTStorageSessionImpl;

/**
 * Created by User: feu - Date: Jun 11, 2010 - Time: 10:22:50 AM
 */
@Singleton
public class MongoSTStorageSessionProvider extends ThreadLocalProvider<STStorageSession> {
    private final int maxCacheSize;

    @Inject
    public MongoSTStorageSessionProvider(STStorageSession.STFlushMode flushMode, STRepositoryPath repositoryPath, Mongo mongo, STPartitionFactory partitionFactory, @MongoMaxCacheSize int maxCacheSize) {
        this.flushMode = flushMode;
        this.repositoryPath = repositoryPath;
        this.mongo = mongo;
        this.partitionFactory = partitionFactory;
        this.maxCacheSize = maxCacheSize;
    }

    private final Mongo mongo;
    private final STRepositoryPath repositoryPath;
    private final STStorageSession.STFlushMode flushMode;

    private final STPartitionFactory partitionFactory;

    @Override
    protected STStorageSession createInstance() {
        return new MongoSTStorageSessionImpl(mongo, flushMode, repositoryPath, partitionFactory, maxCacheSize);
    }
}
