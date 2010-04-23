package org.openspotlight.federation.util.test;

import org.openspotlight.storage.redis.guice.JRedisServerDetail;


public enum JRedisServerConfigExample implements JRedisServerDetail {
        DEFAULT("localhost", 6379, 0);

        private JRedisServerConfigExample(String serverName, int serverPort, int db) {
            this.serverName = serverName;
            this.serverPort = serverPort;
            this.db = db;
        }

        private final String serverName;

        private final int db;

        public int getDb() {
            return db;
        }

        public String getPassword() {
            return null;
        }

        private final int serverPort;

        public String getServerName() {
            return serverName;
        }

        public int getServerPort() {
            return serverPort;
        }
    }