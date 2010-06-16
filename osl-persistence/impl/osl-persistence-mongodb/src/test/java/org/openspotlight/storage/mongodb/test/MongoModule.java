package org.openspotlight.storage.mongodb.test;

import com.google.inject.AbstractModule;
import com.mongodb.Mongo;
import org.openspotlight.storage.DefaultSTPartitionFactory;
import org.openspotlight.storage.STPartitionFactory;
import org.openspotlight.storage.STRepositoryPath;
import org.openspotlight.storage.STStorageSession;

/**
 * Created by User: feu - Date: Jun 11, 2010 - Time: 10:42:37 AM
 */
public class MongoModule extends AbstractModule {

    private final STStorageSession.STFlushMode flushMode;

    private final Mongo mongo;

    private final STRepositoryPath repositoryPath;

    private final STPartitionFactory factory;

    public MongoModule(STStorageSession.STFlushMode flushMode, Mongo mongo, STRepositoryPath repositoryPath) {
        this.flushMode = flushMode;
        this.mongo = mongo;
        this.repositoryPath = repositoryPath;
        this.factory = new DefaultSTPartitionFactory();
    }

    public MongoModule(STStorageSession.STFlushMode flushMode, Mongo mongo, STRepositoryPath repositoryPath, STPartitionFactory factory) {
        this.flushMode = flushMode;
        this.mongo = mongo;
        this.repositoryPath = repositoryPath;
        this.factory = factory;
    }

    @Override
    protected void configure() {
        bind(STPartitionFactory.class).toInstance(factory);
        bind(Mongo.class).toInstance(mongo);
        bind(STRepositoryPath.class).toInstance(repositoryPath);
        bind(STStorageSession.STFlushMode.class).toInstance(flushMode);
        bind(STStorageSession.class).toProvider(MongoSTStorageSessionProvider.class);
    }
}
