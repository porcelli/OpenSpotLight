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
package org.openspotlight.graph;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.graph.guice.SLGraphModule;
import org.openspotlight.graph.query.SLGraphQueryTest;
import org.openspotlight.graph.test.domain.node.JavaInterface;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

/**
 * The Class SLGraphNodeByIDTest.
 *
 * @author Vitor Hugo Chagas
 */

public class SLGraphNodeByIDTest {

    /**
     * The Constant LOGGER.
     */
    static final Logger LOGGER = Logger.getLogger(SLGraphQueryTest.class);

    /**
     * The graph.
     */
    private static SLGraph graph;

    /**
     * The session.
     */
    private static SLGraphSession session;

    private static AuthenticatedUser user;

    /**
     * Finish.
     */
    @AfterClass
    public static void finish() {
        session.close();
        graph.shutdown();
    }

    /**
     * Sets the up.
     */
    @BeforeClass
    public static void setUp() {
        try {

            JcrConnectionProvider.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR).closeRepositoryAndCleanResources();
            Injector injector = Guice.createInjector(new JRedisStorageModule(STStorageSession.STFlushMode.AUTO,
                    ExampleRedisConfig.EXAMPLE.getMappedServerConfig(),
                    repositoryPath("repository")),
                    new SimplePersistModule(), new SLGraphModule(DefaultJcrDescriptor.TEMP_DESCRIPTOR));


            graph = injector.getInstance(SLGraph.class);

            final SecurityFactory securityFactory = injector.getInstance(SecurityFactory.class);
            final User simpleUser = securityFactory.createUser("testUser");
            AuthenticatedUser user = securityFactory.createIdentityManager(DefaultJcrDescriptor.TEMP_DESCRIPTOR).authenticate(simpleUser, "password");
            session = graph.openSession(user, SLConsts.DEFAULT_REPOSITORY_NAME);
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Test get node by id casting.
     */
    @Test
    public void testGetNodeByIDCasting() {
        try {

            final SLContext context = session.createContext("linkCountTest");
            final SLNode root = context.getRootNode();

            final JavaInterface javaInterface = root.addNode(JavaInterface.class, "javaInterface");
            final JavaInterface javaInterface2 = (JavaInterface) session.getNodeByID(javaInterface.getID());

            assertThat(javaInterface, is(javaInterface2));
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
