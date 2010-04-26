package org.openspotlight.storage.redis.util;

import com.google.common.collect.ImmutableMap;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.domain.SLPartition;
import org.openspotlight.storage.redis.guice.JRedisServerDetail;

/**
 * Created by User: feu - Date: Apr 26, 2010 - Time: 2:48:17 PM
 */
public enum ExampleRedisConfig implements JRedisServerDetail {

    EXAMPLE("localhost", 6379, 1, null);


    private final ImmutableMap<STPartition, JRedisServerDetail> mappedServerConfig;

    public ImmutableMap<STPartition, JRedisServerDetail> getMappedServerConfig() {
        return mappedServerConfig;
    }

    private ExampleRedisConfig(String serverName, int serverPort, int db, String password) {
        this.serverName = serverName;
        this.serverPort = serverPort;
        this.db = db;
        this.password = password;

        ImmutableMap.Builder<STPartition, JRedisServerDetail> builder = ImmutableMap.<STPartition, JRedisServerDetail>builder();
        for (SLPartition p : SLPartition.values()) {
            builder.put(p, this);
        }
        this.mappedServerConfig = builder.build();
    }

    private final String serverName;

    private final int serverPort;

    private final int db;

    private final String password;

    public String getServerName() {
        return serverName;
    }

    public int getServerPort() {
        return serverPort;
    }

    public int getDb() {
        return db;
    }

    public String getPassword() {
        return password;
    }
}
