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
import org.junit.*;
import org.openspotlight.common.exception.AbstractFactoryException;
import org.openspotlight.graph.exception.SLGraphException;
import org.openspotlight.graph.guice.SLGraphModule;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;
import org.openspotlight.security.idm.auth.IdentityException;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

public class MultipleGraphSessionsTest {

    private static SLGraph graph = null;
    private static SLSimpleGraphSession session = null;
    private static AuthenticatedUser user;

    @AfterClass
    public static void finish() {
        MultipleGraphSessionsTest.session.close();
        MultipleGraphSessionsTest.graph.shutdown();
    }

    @BeforeClass
    public static void init() throws AbstractFactoryException, IdentityException {
        Injector injector = Guice.createInjector(new JRedisStorageModule(STStorageSession.STFlushMode.AUTO,
                ExampleRedisConfig.EXAMPLE.getMappedServerConfig(),
                repositoryPath("repository")),
                new SimplePersistModule(), new SLGraphModule(DefaultJcrDescriptor.TEMP_DESCRIPTOR));


        graph = injector.getInstance(SLGraph.class);

        final SecurityFactory securityFactory = injector.getInstance(SecurityFactory.class);
        final User simpleUser = securityFactory.createUser("testUser");
        MultipleGraphSessionsTest.user = securityFactory.createIdentityManager(DefaultJcrDescriptor.TEMP_DESCRIPTOR).authenticate(
                simpleUser,
                "password");
    }

    @Ignore
    // FIXME test this again
    @Test
    public void testMultipleSessions() throws AbstractFactoryException, Exception {
        final SLSimpleGraphSession session = MultipleGraphSessionsTest.graph.openSession(MultipleGraphSessionsTest.user,
                SLConsts.DEFAULT_REPOSITORY_NAME);
        final SLSimpleGraphSession session2 = MultipleGraphSessionsTest.graph.openSession(MultipleGraphSessionsTest.user,
                SLConsts.DEFAULT_REPOSITORY_NAME);

        final SLNode abstractTestNode = session.createContext("abstractTest").getRootNode();
        final SLNode node1 = abstractTestNode.addChildNode("teste!");
        final SLNode testRootNode = session.createContext("test").getRootNode();
        final SLNode node2 = testRootNode.addChildNode("teste!");

        Assert.assertEquals(false, node1.getID().equals(node2.getID()));

        final String node1ID = node1.getID();
        final String node2ID = node2.getID();

        session.close();

        final SLNode abstractTestNode2 = session2.createContext("abstractTest").getRootNode();
        final SLNode node3 = abstractTestNode2.addChildNode("teste!");
        final SLNode testRootNode2 = session2.createContext("test").getRootNode();
        final SLNode node4 = testRootNode2.addChildNode("teste!");

        Assert.assertEquals(false, node3.getID().equals(node4.getID()));

        System.out.println("node4: " + node4.getID());
        System.out.println("node2ID: " + node2ID);

        System.out.println("node3: " + node3.getID());
        System.out.println("node1ID: " + node1ID);

        Assert.assertEquals(true, node2ID.equals(node4.getID()));
        Assert.assertEquals(true, node1ID.equals(node3.getID()));

        session2.close();
    }

    @Test
    public void testOpenCloseSessions() throws AbstractFactoryException, SLGraphException {
        MultipleGraphSessionsTest.session = MultipleGraphSessionsTest.graph.openSession(MultipleGraphSessionsTest.user,
                SLConsts.DEFAULT_REPOSITORY_NAME);

        SLNode abstractTestNode = MultipleGraphSessionsTest.session.createContext("abstractTest").getRootNode();
        SLNode node1 = abstractTestNode.addChildNode("teste!");
        SLNode testRootNode = MultipleGraphSessionsTest.session.createContext("test").getRootNode();
        SLNode node2 = testRootNode.addChildNode("teste!");

        Assert.assertEquals(false, node1.getID().equals(node2.getID()));
        MultipleGraphSessionsTest.session.close();
        MultipleGraphSessionsTest.session = MultipleGraphSessionsTest.graph.openSession(MultipleGraphSessionsTest.user,
                SLConsts.DEFAULT_REPOSITORY_NAME);

        abstractTestNode = MultipleGraphSessionsTest.session.createContext("abstractTest").getRootNode();
        node1 = abstractTestNode.addChildNode("teste!");
        testRootNode = MultipleGraphSessionsTest.session.createContext("test").getRootNode();
        node2 = testRootNode.addChildNode("teste!");

        Assert.assertEquals(false, node1.getID().equals(node2.getID()));
    }

}
