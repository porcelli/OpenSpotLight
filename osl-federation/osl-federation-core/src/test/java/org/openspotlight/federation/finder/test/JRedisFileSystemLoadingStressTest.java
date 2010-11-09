package org.openspotlight.federation.finder.test;

import org.openspotlight.storage.PartitionFactory.RegularPartitions;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.redis.guice.JRedisFactory;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

import com.google.inject.Module;

/**
 * Created by User: feu - Date: Jun 18, 2010 - Time: 12:26:16 PM
 */
public class JRedisFileSystemLoadingStressTest extends AbstractFileSystemLoadingStressTest {

    @Override
    protected void clearData()
        throws Exception {
        injector.getInstance(JRedisFactory.class).getFrom(RegularPartitions.FEDERATION).flushall();

    }

    @Override
    protected Module createStorageModule() {
        return new JRedisStorageModule(StorageSession.FlushMode.AUTO,
                ExampleRedisConfig.EXAMPLE.getMappedServerConfig());

    }
}
