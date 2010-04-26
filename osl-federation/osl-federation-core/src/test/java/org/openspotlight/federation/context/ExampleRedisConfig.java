package org.openspotlight.federation.context;

import com.google.common.collect.ImmutableMap;
import org.junit.Ignore;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.domain.SLPartition;
import org.openspotlight.storage.redis.guice.JRedisServerDetail;


@Ignore
public enum ExampleRedisConfig implements JRedisServerDetail {

    INSTANCE("localhost", 6379, 1, null);


    public static final ImmutableMap<STPartition, JRedisServerDetail> mappedServerConfig = ImmutableMap.<STPartition, JRedisServerDetail>builder()
            .put(SLPartition.GRAPH, ExampleRedisConfig.INSTANCE)
            .put(SLPartition.FEDERATION, ExampleRedisConfig.INSTANCE)
            .put(SLPartition.LOG, ExampleRedisConfig.INSTANCE).build();


    private ExampleRedisConfig(String serverName, int serverPort, int db, String password) {
        this.serverName = serverName;
        this.serverPort = serverPort;
        this.db = db;
        this.password = password;
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
