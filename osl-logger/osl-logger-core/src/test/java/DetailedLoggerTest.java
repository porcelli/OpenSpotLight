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

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.ArtifactWithSyntaxInformation;
import org.openspotlight.federation.domain.artifact.ChangeType;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.log.DetailedLoggerProvider;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLNode;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.log.DetailedLogger;
import org.openspotlight.log.DetailedLogger.ErrorCode;
import org.openspotlight.persist.support.SimplePersistCapable;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.persist.support.SimplePersistFactoryImpl;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.SLPartition;
import org.openspotlight.storage.domain.node.STNodeEntry;
import org.openspotlight.storage.redis.guice.JRedisFactory;
import org.openspotlight.storage.redis.guice.JRedisSTStorageSessionProvider;
import org.openspotlight.storage.redis.guice.JRedisServerDetail;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

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


    private SLGraphSession graphSession;

    private static JcrConnectionProvider provider;

    private static SLGraph graph;

    private static AuthenticatedUser user;




    private static SimplePersistCapable<STNodeEntry, STStorageSession> simplePersist;


    @BeforeClass
    public static void setupJcr() throws Exception {

        Injector autoFlushInjector = Guice.createInjector(new JRedisStorageModule(STStorageSession.STFlushMode.AUTO,
                ExampleRedisConfig.EXAMPLE.getMappedServerConfig(), repositoryPath("repositoryPath")));
        autoFlushInjector.getInstance(JRedisFactory.class).getFrom(SLPartition.LOG).flushall();
        graph = AbstractFactory.getDefaultInstance(SLGraphFactory.class)
                .createGraph(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
        SimplePersistFactory simplePersistFactory = new SimplePersistFactoryImpl(autoFlushInjector.getProvider(STStorageSession.class));

        simplePersist = simplePersistFactory.createSimplePersist(SLPartition.LOG);
        loggerProvider = new DetailedLoggerProvider(simplePersistFactory, autoFlushInjector.getInstance(JRedisSTStorageSessionProvider.class));
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
        loggerProvider.closeResources();
        if (graphSession != null) {
            graphSession.close();
            graphSession = null;
        }
        logger = null;
    }

    @Before
    public void setupAttributes() throws Exception {

    }

    @Test
    public void shouldLogSomeStuff() throws Exception {

        graphSession = graph.openSession(user,"tempRepo");
        logger =loggerProvider.get();
        final ArtifactWithSyntaxInformation artifact = Artifact.createArtifact(
                StringArtifact.class, "a/b/c/d", ChangeType.INCLUDED);
        final SLNode node = graphSession.createContext("ctx").getRootNode()
                .addNode("node1");
        final SLNode node2 = node.addNode("node2");
        final SLNode node3 = node2.addNode("node3");
        logger.log(user, "tempRepo", DetailedLogger.LogEventType.DEBUG, new CustomErrorCode(),
                "firstEntry", node3, artifact);

        logger.log(user, "tempRepo", DetailedLogger.LogEventType.DEBUG, new CustomErrorCode(),
                "secondEntry", artifact);
        logger.log(user, "tempRepo", DetailedLogger.LogEventType.DEBUG, new CustomErrorCode(),
                "thirdEntry", node3);

        final Iterable<DetailedLoggerProvider.LogEntry> foundEntries = simplePersist.findAll(DetailedLoggerProvider.LogEntry.class);

        boolean hasAnyEntry = false;
        boolean hasAnyObject = false;
        for (final DetailedLoggerProvider.LogEntry entry : foundEntries) {
            hasAnyEntry = true;
            for (final DetailedLoggerProvider.LoggedObjectInformation info : entry.getNodes()) {
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
