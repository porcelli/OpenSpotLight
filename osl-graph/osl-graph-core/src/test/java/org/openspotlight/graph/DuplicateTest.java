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
/**
 *
 */
package org.openspotlight.graph;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openspotlight.common.concurrent.NeedsSyncronizationSet;
import org.openspotlight.common.exception.AbstractFactoryException;
import org.openspotlight.graph.exception.SLGraphException;
import org.openspotlight.graph.guice.SLGraphModule;
import org.openspotlight.graph.query.SLQueryApi;
import org.openspotlight.graph.query.SLQueryResult;
import org.openspotlight.graph.test.domain.node.JavaClass;
import org.openspotlight.graph.test.domain.node.JavaPackage;
import org.openspotlight.graph.test.domain.node.JavaType;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;
import org.openspotlight.security.idm.auth.IdentityException;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

/**
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
public class DuplicateTest {

    SLGraph graph = null;
    SLGraphSession session = null;
    AuthenticatedUser user = null;

    @Before
    public void setup() throws AbstractFactoryException, SLGraphException, IdentityException {

        Injector injector = Guice.createInjector(new JRedisStorageModule(STStorageSession.STFlushMode.AUTO,
                ExampleRedisConfig.EXAMPLE.getMappedServerConfig(),
                repositoryPath("repository")),
                new SimplePersistModule(), new SLGraphModule(DefaultJcrDescriptor.TEMP_DESCRIPTOR));


        graph = injector.getInstance(SLGraph.class);

        final SecurityFactory securityFactory = injector.getInstance(SecurityFactory.class);
        final User simpleUser = securityFactory.createUser("testUser");
        user = securityFactory.createIdentityManager(DefaultJcrDescriptor.TEMP_DESCRIPTOR).authenticate(simpleUser, "password");
        session = graph.openSession(user, SLConsts.DEFAULT_REPOSITORY_NAME);
    }

    @Test
    public void shouldInsertTwoDifferentNodes() throws Exception {
        final SLNode rootNode = session.createContext("tmpXX").getRootNode();

        final JavaClass javaClass = rootNode.addNode(JavaClass.class, "test");
        final JavaPackage javaPackage = rootNode.addNode(JavaPackage.class, "test");

        final List<SLNode> nodes = new ArrayList<SLNode>();
        nodes.add(javaClass);
        nodes.add(javaPackage);

        session.save();
        session.cleanCache();
        final NeedsSyncronizationSet<SLNode> foundNodes = rootNode.getNodes();
        for (final SLNode n : foundNodes) {
            System.err.println(n.getName() + " " + n.getClass().getInterfaces()[0].getSimpleName());
        }
        assertThat(foundNodes.size(), is(1));

        assertThat(session.getNodeByID(javaPackage.getID()).getID(), is(javaPackage.getID()));

        final NeedsSyncronizationSet<JavaClass> classChildren = rootNode.getChildNodes(JavaClass.class);
        assertThat(classChildren.size(), is(0));

        final NeedsSyncronizationSet<JavaPackage> allChildren = rootNode.getChildNodes(JavaPackage.class);
        assertThat(allChildren.size(), is(1));

    }

    @Test
    public void shouldNotInsertTwoEqualNodes() throws Exception {
        final SLNode rootNode = session.createContext("tmp").getRootNode();
        final SLNode rootNode1 = session.createContext("tmp1").getRootNode();
        final JavaClass parent = rootNode.addNode(JavaClass.class, "parent");
        final JavaClass parent1 = rootNode1.addNode(JavaClass.class, "parent");
        final JavaType n1 = parent.addNode(JavaClass.class, "someName");
        n1.setCaption("someName");
        final JavaType n2 = parent.addNode(JavaType.class, "another");
        n2.setCaption("another");
        final JavaType n3 = parent.addNode(JavaType.class, "someName");

        assertThat(n1, is(n3));

        n3.setCaption("someName");
        final JavaType n1_ = parent1.addNode(JavaClass.class, "someName");
        n1_.setCaption("someName");
        final JavaType n2_ = parent1.addNode(JavaType.class, "another");
        n2_.setCaption("another");
        final JavaType n3_ = parent1.addNode(JavaType.class, "someName");

        assertThat(n1_, is(n3_));

        n3_.setCaption("someName");
        session.save();
        session.close();
        session = graph.openSession(user, SLConsts.DEFAULT_REPOSITORY_NAME);
        final SLQueryApi query = session.createQueryApi();
        query

                .select().type(JavaType.class.getName()).subTypes().selectEnd().where().type(JavaType.class.getName()).subTypes().each().property(
                "caption").equalsTo().value(
                "someName").typeEnd().whereEnd();

        final SLQueryResult result = query.execute();
        // aqui o map possui uma lista de nodes para cada id de contexto.
        final Map<String, List<SLNode>> resultMap = new HashMap<String, List<SLNode>>();
        resultMap.put("tmp", new ArrayList<SLNode>());
        resultMap.put("tmp1", new ArrayList<SLNode>());
        for (final SLNode n : result.getNodes()) {
            resultMap.get(n.getContext().getID()).add(n);
        }
        // aqui é verificado se cada id de contexto possui apenas um node
        for (final Map.Entry<String, List<SLNode>> entry : resultMap.entrySet()) {
            assertThat(entry.getValue().size(), is(1));
        }

        session.close();
        graph.shutdown();
    }

    @Test
    public void shouldNotInsertTwoEqualNodes2() throws Exception {
        final SLNode rootNode = session.createContext("tmp").getRootNode();
        final SLNode rootNode1 = session.createContext("tmp1").getRootNode();
        final JavaClass parent = rootNode.addNode(JavaClass.class, "parent");
        final JavaClass parent1 = rootNode1.addNode(JavaClass.class, "parent");
        final JavaType n1 = parent.addNode(JavaClass.class, "someName");
        n1.setCaption("someName");
        final JavaType n2 = parent.addNode(JavaType.class, "another");
        n2.setCaption("another");
        final JavaType n3 = parent.addNode(JavaType.class, "someName");

        assertThat(n1, is(n3));

        n3.setCaption("someName");
        final JavaType n1_ = parent1.addNode(JavaClass.class, "someName");
        n1_.setCaption("someName");
        final JavaType n2_ = parent1.addNode(JavaType.class, "another");
        n2_.setCaption("another");
        final JavaType n3_ = parent1.addNode(JavaType.class, "someName");

        assertThat(n1_, is(n3_));

        n3_.setCaption("someName");
        // session.save();
        session.close();
        session = graph.openSession(user, SLConsts.DEFAULT_REPOSITORY_NAME);
        final SLQueryApi query = session.createQueryApi();
        query

                .select().type(JavaType.class.getName()).subTypes().selectEnd().where().type(JavaType.class.getName()).subTypes().each().property(
                "caption").equalsTo().value(
                "someName").typeEnd().whereEnd();

        final SLQueryResult result = query.execute();
        // aqui o map possui uma lista de nodes para cada id de contexto.
        final Map<String, List<SLNode>> resultMap = new HashMap<String, List<SLNode>>();
        resultMap.put("tmp", new ArrayList<SLNode>());
        resultMap.put("tmp1", new ArrayList<SLNode>());
        for (final SLNode n : result.getNodes()) {
            resultMap.get(n.getContext().getID()).add(n);
        }
        // aqui é verificado se cada id de contexto possui apenas um node
        for (final Map.Entry<String, List<SLNode>> entry : resultMap.entrySet()) {
            assertThat(entry.getValue().size(), is(1));
        }

    }

    @After
    public void shutdown() {
        session.close();
        graph.shutdown();
    }
}
