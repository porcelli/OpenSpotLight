package org.openspotlight.graph.test;

import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

import java.util.Map;

import org.openspotlight.graph.SLGraphModule;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.SLPartition;
import org.openspotlight.storage.redis.guice.JRedisFactory;
import org.openspotlight.storage.redis.guice.JRedisServerDetail;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class RedisGraphTest extends AbstractGraphTest {

	private enum JRedisServerConfigExample implements JRedisServerDetail {

		GRAPH("localhost", 6379, 0), FEDERATION("localhost", 6379, 1), SYNTAX_HIGHLIGHT(
				"localhost", 6379, 2), LINE_REFERENCE("localhost", 6379, 3), SECURITY(
				"localhost", 6379, 4), LOG("localhost", 6379, 5);

		private JRedisServerConfigExample(String serverName, int serverPort,
				int db) {
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

	final Map<STPartition, JRedisServerDetail> mappedServerConfig = ImmutableMap
			.<STPartition, JRedisServerDetail> builder().put(
					SLPartition.FEDERATION,
					JRedisServerConfigExample.FEDERATION).put(
					SLPartition.GRAPH, JRedisServerConfigExample.GRAPH).put(
					SLPartition.LINE_REFERENCE,
					JRedisServerConfigExample.LINE_REFERENCE).put(
					SLPartition.LOG, JRedisServerConfigExample.LOG).put(
					SLPartition.SECURITY, JRedisServerConfigExample.SECURITY)
			.put(SLPartition.SYNTAX_HIGHLIGHT,
					JRedisServerConfigExample.SYNTAX_HIGHLIGHT).build();

	@Override
	protected void clearData() throws Exception {
		JRedisFactory autoFlushFactory = injector
				.getInstance(JRedisFactory.class);
		autoFlushFactory.getFrom(SLPartition.GRAPH).flushall();
		autoFlushFactory.getFrom(SLPartition.GRAPH).save();
	}

	@Override
	protected Injector createInjector() throws Exception {
		return Guice.createInjector(new JRedisStorageModule(
				STStorageSession.STFlushMode.AUTO, mappedServerConfig,
				repositoryPath("repositoryPath"), SLPartition.FACTORY),
				new SLGraphModule());
	}

}
