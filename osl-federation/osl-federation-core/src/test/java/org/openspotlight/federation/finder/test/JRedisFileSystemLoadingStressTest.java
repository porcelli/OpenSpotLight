package org.openspotlight.federation.finder.test;

import com.google.inject.Module;
import org.openspotlight.storage.STRepositoryPath;
import org.openspotlight.storage.StorageSessionport org.openspotlight.storage.domain.RegularPartitionitionition;
import org.openspotlight.storage.redis.guice.JRedisFactory;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

/**
 * Created by User: feu - Date: Jun 18, 2010 - Time: 12:26:16 PM
 */
public class JRedisFileSystemLoadingStressTest extends AbstractFileSystemLoadingStressTest {

    @Override
    protected void clearData() throws Exception{
        injector.getInstance(JRedisFactory.classRegularPartitionrPartitionrPartition.GRAPH).flushall();

    }

    @Override
    protected Module createStorageModule(STRepositoryPath repositoryPath) {
        return new JRedisStorageModule(StStStorageSessionMode.AUTO,
                ExampleRedisConfig.EXAMPLE.getMappedServerConfig(),
                repositoryPath);

    }
}
