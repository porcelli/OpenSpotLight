package org.openspotlight.storage.redis.guice;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.jredis.JRedis;
import org.jredis.ri.alphazero.JRedisClient;
import org.openspotlight.storage.STStorageSession;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: feuteston
 * Date: 30/03/2010
 * Time: 17:45:29
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class JRedisFacoryImpl implements JRedisFactory {

    private final Map<STStorageSession.STPartition, JRedisServerDetail> mappedServerConfig;

    private ThreadLocal<Map<STStorageSession.STPartition, JRedis>> threadLocalCache = new ThreadLocal<Map<STStorageSession.STPartition, JRedis>>();

    @Inject
    JRedisFacoryImpl(Map<STStorageSession.STPartition, JRedisServerDetail> mappedServerConfig) {
        this.mappedServerConfig = mappedServerConfig;
    }


    public synchronized JRedis create(STStorageSession.STPartition partition) {
        JRedisServerDetail serverDetail = mappedServerConfig.get(partition);
        Map<STStorageSession.STPartition, JRedis> cache = threadLocalCache.get();
        JRedis jRedis = cache != null ? cache.get(partition) : null;
        if (jRedis == null) {
            if (cache == null) {
                cache = new HashMap();
                threadLocalCache.set(cache);

            }
            jRedis = new JRedisClient(serverDetail.getServerName(), serverDetail.getServerPort());
            cache.put(partition, jRedis);
        }
        return jRedis;
    }
}
