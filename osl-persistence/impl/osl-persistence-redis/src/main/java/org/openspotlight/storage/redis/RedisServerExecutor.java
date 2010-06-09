package org.openspotlight.storage.redis;

import org.jredis.JRedis;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.redis.guice.JRedisFactory;

import java.io.File;

import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.Strings.concatPaths;

/**
 * Created by User: feu - Date: May 18, 2010 - Time: 11:22:15 AM
 */
public enum RedisServerExecutor {
    INSTANCE;
    private static final String RELATIVE_REDIS_PATH = "osl-persistence/impl/osl-persistence-redis/native/redis";
    private static final String REDIS_EXEC = "redis-server";
    private static final String MAKE = "make";
    private static final String ERROR_MESSAGE = "It is necessary to newPair an environment variable or JVM property for OSL_HOME ." +
            " It is possible also to newPair a variable or JVM property for REDIS_HOME";
    private Process currentProcess = null;
    private static final String ERROR_COMPILING_REDIS = "Error on redis compilation. The executable wasn't found after the make call.";

    public synchronized void startServerIfNecessary(final STPartition somePartition, final JRedisFactory factory) {
        if (currentProcess == null) {
            String oslHome = getVar("OSL_HOME");
            String redisHome = getVar("REDIS_HOME");
            if (oslHome == null && redisHome == null)
                throw new IllegalStateException(ERROR_MESSAGE);
            try {
                if (redisHome == null) redisHome = concatPaths(oslHome, RELATIVE_REDIS_PATH);
                String redisExec = concatPaths(redisHome, REDIS_EXEC);
                if (!new File(redisExec).exists()) {
                    Process make = Runtime.getRuntime().exec(MAKE,null,new File(redisHome));
                    make.waitFor();
                    if (!new File(redisExec).exists()) {
                        throw new IllegalStateException(ERROR_COMPILING_REDIS);
                    }
                }
                currentProcess = Runtime.getRuntime().exec(redisExec,null,new File(redisHome));
                Thread.sleep(500);//give some time to redis server start
                Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

                    @Override
                    public void run() {
                        if (currentProcess != null) {
                            try {
                                JRedis redis = factory.getFrom(somePartition);
                                redis.save();
                                redis.shutdown();
                                currentProcess = null;
                            } catch (Exception e) {
                                throw logAndReturnNew(e, SLRuntimeException.class);
                            }
                        }
                    }
                }));
            } catch (Exception e) {
                throw logAndReturnNew(e, SLRuntimeException.class);
            }
        }
    }

    private String getVar(String varName) {
        String var = System.getenv(varName);
        if(var==null) var = System.getProperty(varName);
        return var;
    }


}
