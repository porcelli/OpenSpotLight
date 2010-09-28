/*
 * OpenSpotLight - Open Source IT Governance Platform
 *
 * Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA
 * or third-party contributors as indicated by the @author tags or express
 * copyright attribution statements applied by the authors.  All third-party
 * contributions are distributed under license by CARAVELATECH CONSULTORIA E
 * TECNOLOGIA EM INFORMATICA LTDA.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License  for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 * Todas as contribuições de terceiros estão distribuídas sob licença da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA.
 *
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software
 * Foundation.
 *
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 * FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 * programa; se não, escreva para:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openspotlight.storage.RepositoryPath.repositoryPath;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.ArtifactWithSyntaxInformation;
import org.openspotlight.federation.domain.artifact.ChangeType;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.log.DetailedLoggerProvider;
import org.openspotlight.federation.log.LogEntry;
import org.openspotlight.federation.log.LoggedObjectInformation;
import org.openspotlight.graph.Context;
import org.openspotlight.graph.FullGraphSession;
import org.openspotlight.graph.GraphLocation;
import org.openspotlight.graph.GraphModule;
import org.openspotlight.graph.Node;
import org.openspotlight.graph.manipulation.GraphWriter;
import org.openspotlight.graph.test.node.JavaType;
import org.openspotlight.log.DetailedLogger;
import org.openspotlight.log.DetailedLogger.ErrorCode;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.persist.support.SimplePersistCapable;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.persist.support.SimplePersistFactoryImpl;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.RegularPartitions;
import org.openspotlight.storage.domain.StorageNode;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.guice.JRedisStorageSessionProvider;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class DetailedLoggerTest {

	public static class CustomErrorCode implements ErrorCode {

		/**
         *
         */
		private static final long serialVersionUID = -3703345396653682388L;

		public String getDescription() {
			return "CustomErrorCode:description";
		}

		public String getErrorCode() {
			return "CustomErrorCode:errorCode";
		}

		public void setDescription(final String s) {

		}

		public void setErrorCode(final String s) {

		}

	}

	private static DetailedLoggerProvider loggerProvider;

	private static SimplePersistCapable<StorageNode, StorageSession> simplePersist;

	private static FullGraphSession graphSession;

	@BeforeClass
	public static void setupJcr() throws Exception {

		Injector autoFlushInjector = Guice.createInjector(
				new JRedisStorageModule(StorageSession.FlushMode.AUTO,
						ExampleRedisConfig.EXAMPLE.getMappedServerConfig(),
						repositoryPath("repositoryPath")),
				new SimplePersistModule(), new GraphModule());

		graphSession = autoFlushInjector.getInstance(FullGraphSession.class);

		SimplePersistFactory simplePersistFactory = new SimplePersistFactoryImpl(
				autoFlushInjector.getProvider(StorageSession.class));

		simplePersist = simplePersistFactory
				.createSimplePersist(RegularPartitions.LOG);
		loggerProvider = new DetailedLoggerProvider(simplePersistFactory,
				autoFlushInjector
						.getInstance(JRedisStorageSessionProvider.class));

	}

	private DetailedLogger logger;

	@After
	public void releaseAttributes() throws Exception {
		loggerProvider.closeResources();
		graphSession.dispose();
		logger = null;
	}

	@Before
	public void setupAttributes() throws Exception {

	}

	@Test
	public void shouldLogSomeStuff() throws Exception {

		logger = loggerProvider.get();
		final ArtifactWithSyntaxInformation artifact = Artifact.createArtifact(
				StringArtifact.class, "a/b/c/d", ChangeType.INCLUDED);
		Context root = graphSession.from(GraphLocation.SERVER)
				.getContext("ctx");
		GraphWriter writer = graphSession.toServer();
		final Node node = writer.addNode(root, JavaType.class, "node1");
		final Node node2 = writer.addChildNode(node, JavaType.class, "node2");
		final Node node3 = writer.addChildNode(node2, JavaType.class, "node3");
		logger.log(null, "tempRepo", DetailedLogger.LogEventType.DEBUG,
				new CustomErrorCode(), "firstEntry", node3, artifact);

		logger.log(null, "tempRepo", DetailedLogger.LogEventType.DEBUG,
				new CustomErrorCode(), "secondEntry", artifact);
		logger.log(null, "tempRepo", DetailedLogger.LogEventType.DEBUG,
				new CustomErrorCode(), "thirdEntry", node3);

		final Iterable<LogEntry> foundEntries = simplePersist
				.findAll(LogEntry.class);

		boolean hasAnyEntry = false;
		boolean hasAnyObject = false;
		for (final LogEntry entry : foundEntries) {
			hasAnyEntry = true;
			for (final LoggedObjectInformation info : entry.getNodes()) {
				hasAnyObject = true;
				assertThat(info.getClassName(), is(notNullValue()));
				assertThat(info.getFriendlyDescription(), is(notNullValue()));
			}
			assertThat(entry.getType(), is(notNullValue()));
			assertThat(entry.getDate(), is(notNullValue()));
			assertThat(entry.getDetailedMessage(), is(notNullValue()));
		}
		assertThat(hasAnyEntry, is(true));
		assertThat(hasAnyObject, is(true));

	}

}
