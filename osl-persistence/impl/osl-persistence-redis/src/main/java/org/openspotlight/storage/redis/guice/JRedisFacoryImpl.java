package org.openspotlight.storage.redis.guice;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.jredis.JRedis;
import org.jredis.ri.alphazero.JRedisClient;
import org.openspotlight.storage.STPartition;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptySet;

/**
 * Created by IntelliJ IDEA.
 * User: feuteston
 * Date: 30/03/2010
 * Time: 17:45:29
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class JRedisFacoryImpl implements JRedisFactory {

    private final Map<STPartition, JRedisServerDetail> mappedServerConfig;

    private ThreadLocal<Map<STPartition, JRedis>> threadLocalCache = new ThreadLocal<Map<STPartition, JRedis>>();

    @Inject
    JRedisFacoryImpl(Map<STPartition, JRedisServerDetail> mappedServerConfig) {
        this.mappedServerConfig = mappedServerConfig;
    }


    public JRedis getFrom(STPartition partition) {
        JRedisServerDetail serverDetail = mappedServerConfig.get(partition);
        Map<STPartition, JRedis> cache = threadLocalCache.get();
        if (cache == null) {
            cache = new HashMap();
            threadLocalCache.set(cache);
        }
        JRedis jRedis = cache.get(partition);
        if (jRedis == null) {
            jRedis = new JRedisClient(serverDetail.getServerName(), serverDetail.getServerPort(), serverDetail.getPassword(), serverDetail.getDb());
            cache.put(partition, jRedis);
        }
        return jRedis;
    }

    public Set<JRedis> getAllActive() {
        Set<JRedis> result;
        Map<STPartition, JRedis> cache = threadLocalCache.get();
        if(cache!=null){
            result = ImmutableSet.copyOf(cache.values());
        }else{
            result = emptySet();
        }
        return result;
    }
}
