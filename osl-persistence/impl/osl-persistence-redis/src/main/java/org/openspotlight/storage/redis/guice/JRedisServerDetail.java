package org.openspotlight.storage.redis.guice;

/**
 * Created by IntelliJ IDEA.
 * User: feuteston
 * Date: 30/03/2010
 * Time: 17:44:16
 * To change this template use File | Settings | File Templates.
 */
public interface JRedisServerDetail {

    String getServerName();

    int getServerPort();

    int getDb();

    String getPassword();

}
