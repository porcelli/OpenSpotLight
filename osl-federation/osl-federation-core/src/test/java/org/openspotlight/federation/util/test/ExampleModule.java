package org.openspotlight.federation.util.test;

import com.google.common.collect.ImmutableMap;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.persist.support.SimplePersistFactoryImpl;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STRepositoryPath;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.SLPartition;
import org.openspotlight.storage.redis.guice.JRedisServerDetail;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;

import java.util.Map;

/**
 * Created by User: feu - Date: Apr 23, 2010 - Time: 3:03:43 PM
 */
public class ExampleModule extends JRedisStorageModule{
    public ExampleModule(STRepositoryPath repositoryPath) {
        this(STStorageSession.STFlushMode.AUTO, createSampleConfigurationMap(), repositoryPath);
    }

    private static Map<STPartition, JRedisServerDetail> createSampleConfigurationMap() {
        ImmutableMap.Builder<STPartition, JRedisServerDetail> builder = ImmutableMap.<STPartition, JRedisServerDetail>builder();

        for(SLPartition p: SLPartition.values()){
        builder.put(p, JRedisServerConfigExample.DEFAULT);
        }
        Map<STPartition, JRedisServerDetail> mappedServerConfig = builder.build();
        return mappedServerConfig;
    }

    public ExampleModule(STStorageSession.STFlushMode flushMode, Map<STPartition, JRedisServerDetail> mappedServerConfig, STRepositoryPath repositoryPath) {
        super(flushMode, mappedServerConfig, repositoryPath);
    }

    @Override
    protected void configure() {
        super.configure();
        bind(SimplePersistFactory.class).toInstance(new SimplePersistFactoryImpl(sessionProvider));
    }
}
