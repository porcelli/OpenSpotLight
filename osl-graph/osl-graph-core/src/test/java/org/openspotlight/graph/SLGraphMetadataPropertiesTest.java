/**
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
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.graph.annotation.SLVisibility.VisibilityLevel;
import org.openspotlight.graph.exception.SLMetaLinkTypeNotFoundException;
import org.openspotlight.graph.guice.SLGraphModule;
import org.openspotlight.graph.meta.SLMetaLink;
import org.openspotlight.graph.meta.SLMetaLinkType;
import org.openspotlight.graph.meta.SLMetaNodeProperty;
import org.openspotlight.graph.meta.SLMetaNodeType;
import org.openspotlight.graph.query.SLGraphQueryTest;
import org.openspotlight.graph.test.domain.link.JavaClassHierarchy;
import org.openspotlight.graph.test.domain.link.JavaClassHierarchyWithoutProperties;
import org.openspotlight.graph.test.domain.node.JavaClassNode;
import org.openspotlight.graph.test.domain.node.JavaClassNodeWithoutProperties;
import org.openspotlight.graph.test.domain.node.JavaElementNode;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

/**
 * The Class SLGraphMetadataPropertiesTest.
 *
 * @author porcelli
 */
public class SLGraphMetadataPropertiesTest {

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
    private static SLSimpleGraphSession session;

    /**
     * The user.
     */
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

    @Test
    public void testLinkPropertyVisibility() throws SLMetaLinkTypeNotFoundException {
        final SLNode rootNode = session.createContext("Test1").getRootNode();
        final SLNode testNode1 = rootNode.addChildNode(JavaClassNode.class, "testNode");
        final SLNode testNode2 = rootNode.addChildNode(JavaClassNode.class, "testNode2");

        final JavaClassHierarchy link1 = session.addLink(JavaClassHierarchy.class, testNode1, testNode2, false);
        link1.setName("someName");
        link1.setProperty(String.class, VisibilityLevel.PUBLIC, "otherProp", "something");
        session.save();
        final SLMetaLinkType metaLinkType = session.getMetadata().getMetaLinkType(JavaClassHierarchy.class);
        final SLMetaLink metaLink = metaLinkType.getMetaLinks(JavaClassNode.class, JavaClassNode.class, false).iterator().next();
        Assert.assertEquals(VisibilityLevel.INTERNAL, metaLink.getMetaProperty("name").getVisibility());
        Assert.assertEquals(VisibilityLevel.PUBLIC, metaLink.getMetaProperty("otherProp").getVisibility());

        final JavaClassHierarchyWithoutProperties link2 = session.addLink(JavaClassHierarchyWithoutProperties.class, testNode1,
                testNode2, false);
        link2.setProperty(String.class, VisibilityLevel.PUBLIC, "otherProp", "something");
        link2.setProperty(String.class, VisibilityLevel.PRIVATE, "otherProp2", "something");
        session.save();
        final SLMetaLinkType metaLinkType2 = session.getMetadata().getMetaLinkType(JavaClassHierarchyWithoutProperties.class);
        final SLMetaLink metaLink2 = metaLinkType2.getMetaLinks(JavaClassNode.class, JavaClassNode.class, false).iterator().next();
        Assert.assertEquals(VisibilityLevel.PUBLIC, metaLink2.getMetaProperty("otherProp").getVisibility());
        Assert.assertEquals(VisibilityLevel.PRIVATE, metaLink2.getMetaProperty("otherProp2").getVisibility());
    }

    /**
     * Test node property visibility.
     */
    @Test
    public void testNodePropertyVisibility() {
        final SLNode rootNode = session.createContext("Test1").getRootNode();
        final SLNode testNode = rootNode.addChildNode(JavaClassNodeWithoutProperties.class, "testNode");
        testNode.setProperty(String.class, VisibilityLevel.PRIVATE, "somePropName", "value");

        for (final SLMetaNodeType metaType : session.getMetadata().getMetaNodesTypes()) {
            if (metaType.getTypeName().equals(JavaClassNodeWithoutProperties.class.getName())) {
                final SLMetaNodeProperty property = metaType.getMetaProperty("somePropName");
                Assert.assertEquals(VisibilityLevel.PRIVATE, property.getVisibility());
            }
        }

        final JavaClassNode testNodeWithProperty = rootNode.addChildNode(JavaClassNode.class, "testNode2");
        testNodeWithProperty.setClassName("someClassNAme");
        testNodeWithProperty.setProperty(String.class, VisibilityLevel.PUBLIC, "somePropName", "value2");
        testNodeWithProperty.setProperty(String.class, VisibilityLevel.PRIVATE, "somePropName2", "value2");

        for (final SLMetaNodeType metaType : session.getMetadata().getMetaNodesTypes()) {
            if (metaType.getTypeName().equals(JavaClassNodeWithoutProperties.class.getName())) {
                final SLMetaNodeProperty property = metaType.getMetaProperty("somePropName");
                Assert.assertEquals(VisibilityLevel.PRIVATE, property.getVisibility());
            } else if (metaType.getTypeName().equals(JavaElementNode.class.getName())) {
                final SLMetaNodeType subType = metaType.getSubMetaNodeType(JavaClassNode.class.getName());
                Assert.assertEquals(VisibilityLevel.PUBLIC, subType.getMetaProperty("somePropName").getVisibility());
                Assert.assertEquals(VisibilityLevel.PRIVATE, subType.getMetaProperty("somePropName2").getVisibility());
                Assert.assertEquals(VisibilityLevel.INTERNAL, subType.getMetaProperty("className").getVisibility());
            }
        }
    }
}
