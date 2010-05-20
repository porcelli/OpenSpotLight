package org.openspotlight.storage.redis.guice;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.jredis.JRedis;
import org.jredis.ri.alphazero.JRedisClient;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.redis.RedisServerExecutor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptySet;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;

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
    JRedisFacoryImpl(Map<STPartition, JRedisServerDetail> mappedServerConfig, @StartRedisLocally boolean needsToStart) {
        this.mappedServerConfig = mappedServerConfig;
        if (needsToStart) {
            STPartition samplePartition = mappedServerConfig.keySet().iterator().next();
            RedisServerExecutor.INSTANCE.startServerIfNecessary(samplePartition, this);
        }
    }


    public JRedis getFrom(STPartition partition) {
        final JRedisServerDetail serverDetail = mappedServerConfig.get(partition);
        Map<STPartition, JRedis> cache = threadLocalCache.get();
        if (cache == null) {
            cache = new HashMap();
            threadLocalCache.set(cache);
        }
        JRedis jRedis = cache.get(partition);
        try {
            if (jRedis == null) {


                jRedis = (JRedis) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{JRedis.class}, new InvocationHandler() {

                    private final JRedis redis = new JRedisClient(serverDetail.getServerName(), serverDetail.getServerPort(), serverDetail.getPassword(), serverDetail.getDb());

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                        System.out.println(">>> " + method.getName() + " " + Arrays.toString(args));
                        return method.invoke(redis, args);

                    }
                });
                cache.put(partition, jRedis);
            }
        } catch (Exception e) {
            throw logAndReturnNew(e, SLRuntimeException.class);
        }
        return jRedis;
    }

    public Set<JRedis> getAllActive() {
        Set<JRedis> result;
        Map<STPartition, JRedis> cache = threadLocalCache.get();
        if (cache != null) {
            result = ImmutableSet.copyOf(cache.values());
        } else {
            result = emptySet();
        }
        return result;
    }

}
