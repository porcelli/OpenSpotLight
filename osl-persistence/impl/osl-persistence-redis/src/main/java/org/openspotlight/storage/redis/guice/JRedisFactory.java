package org.openspotlight.storage.redis.guice;

import org.jredis.JRedis;
import org.openspotlight.storage.STPartition;

/**
 * Created by IntelliJ IDEA.
 * User: feuteston
 * Date: 30/03/2010
 * Time: 17:43:16
 * To change this template use File | Settings | File Templates.
 */
public interface JRedisFactory {

    JRedis getFrom(STPartition partition);


}
