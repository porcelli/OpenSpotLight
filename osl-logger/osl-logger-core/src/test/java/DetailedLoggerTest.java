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

import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.common.LazyType;
import org.openspotlight.common.SharedConstants;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.ArtifactWithSyntaxInformation;
import org.openspotlight.federation.domain.artifact.ChangeType;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.log.DetailedJcrLoggerFactory;
import org.openspotlight.federation.log.DetailedJcrLoggerFactory.LogEntry;
import org.openspotlight.federation.log.DetailedJcrLoggerFactory.LoggedObjectInformation;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLNode;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.log.DetailedLogger;
import org.openspotlight.log.DetailedLogger.ErrorCode;
import org.openspotlight.log.DetailedLogger.LogEventType;
import org.openspotlight.persist.support.SimplePersistSupport;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;

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

	private static DetailedJcrLoggerFactory factory;

	private Session session;

	private SLGraphSession graphSession;

	private static JcrConnectionProvider provider;

	private static SLGraph graph;

	private static AuthenticatedUser user;

	@BeforeClass
	public static void setupJcr() throws Exception {
		provider = JcrConnectionProvider
				.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
		graph = AbstractFactory.getDefaultInstance(SLGraphFactory.class)
				.createGraph(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
		factory = new DetailedJcrLoggerFactory(
				DefaultJcrDescriptor.TEMP_DESCRIPTOR);
		final SecurityFactory securityFactory = AbstractFactory
				.getDefaultInstance(SecurityFactory.class);
		final User simpleUser = securityFactory.createUser("testUser");
		user = securityFactory.createIdentityManager(
				DefaultJcrDescriptor.TEMP_DESCRIPTOR).authenticate(simpleUser,
				"password");

	}

	private DetailedLogger logger;

	@After
	public void releaseAttributes() throws Exception {
		factory.closeResources();
		if (session != null) {
			if (session.isLive()) {
				session.logout();
			}
			session = null;
		}
		if (graphSession != null) {
			graphSession.close();
			graphSession = null;
		}
		logger = null;
	}

	@Before
	public void setupAttributes() throws Exception {
		session = provider.openSession();
		graphSession = graph.openSession(user, "tempRepo");
		logger = factory.createNewLogger();
	}

	@Test
	public void shouldLogSomeStuff() throws Exception {

		final ArtifactWithSyntaxInformation artifact = Artifact.createArtifact(
				StringArtifact.class, "a/b/c/d", ChangeType.INCLUDED);
		final SLNode node = graphSession.createContext("ctx").getRootNode()
				.addNode("node1");
		final SLNode node2 = node.addNode("node2");
		final SLNode node3 = node2.addNode("node3");
		logger.log(user, "tempRepo", LogEventType.DEBUG, new CustomErrorCode(),
				"firstEntry", node3, artifact);

		logger.log(user, "tempRepo", LogEventType.DEBUG, new CustomErrorCode(),
				"secondEntry", artifact);
		logger.log(user, "tempRepo", LogEventType.DEBUG, new CustomErrorCode(),
				"thirdEntry", node3);

		final Query query = session.getWorkspace().getQueryManager()
				.createQuery(
						SharedConstants.DEFAULT_JCR_ROOT_NAME
								+ "//"
								+ SimplePersistSupport
										.getJcrNodeName(LogEntry.class),
						Query.XPATH);
		final NodeIterator foundNodes = query.execute().getNodes();
		final Iterable<LogEntry> foundEntries = SimplePersistSupport
				.convertJcrsToBeans(session, foundNodes, LazyType.EAGER);
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
			assertThat(entry.getErrorCode(), is(notNullValue()));
		}
		assertThat(hasAnyEntry, is(true));
		assertThat(hasAnyObject, is(true));

	}

}
