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
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.graph.meta.SLMetadata.BooleanOperator;
import org.openspotlight.graph.meta.SLMetadata.LogicOperator;
import org.openspotlight.graph.meta.SLMetadata.MetaNodeTypeProperty;
import org.openspotlight.graph.annotation.SLVisibility.VisibilityLevel;
import org.openspotlight.graph.exception.SLMetaNodeTypeNotFoundException;
import org.openspotlight.graph.guice.SLGraphModule;
import org.openspotlight.graph.meta.SLMetaNodeType;
import org.openspotlight.graph.query.SLGraphQueryTest;
import org.openspotlight.graph.test.domain.node.*;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

/**
 * The Class SLGraphMetadataPropertiesTest.
 *
 * @author porcelli
 */
public class SLGraphMetadataTest {

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
    public void testMetadataTypeByParent() throws SLMetaNodeTypeNotFoundException {
        final SLNode rootNode = session.createContext("Test1").getRootNode();
        rootNode.addChildNode(JavaClassNodeWithoutProperties.class, "testNode");
        rootNode.addChildNode(JavaClassNode.class, "testNode2");
        session.save();
        final SLMetaNodeType foundType = session.getMetadata().getMetaNodeType(JavaClassNode.class);

        Assert.assertNotNull(foundType);
        Assert.assertEquals(foundType.getTypeName(), JavaClassNode.class.getName());
        Assert.assertEquals(foundType.getParent().getTypeName(), JavaElementNode.class.getName());
        Assert.assertEquals(foundType.getParent().getParent(), null);
    }

    @Test
    public void testSearchMetadataEqualsMoreElementsByDescriptionUsingAnd() {
        final SLNode rootNode = session.createContext("Test1").getRootNode();
        rootNode.addChildNode(JavaClassNodeWithoutPropertiesAndDescription.class, "testNode");
        rootNode.addChildNode(JavaClassNodeWithoutDescription.class, "testNode2");

        final List<String> values2Find = new ArrayList<String>();
        values2Find.add(JavaClassNodeWithoutProperties.class.getName());
        values2Find.add(JavaElementNode.class.getName());

        final Collection<SLMetaNodeType> foundTypes = session.getMetadata().searchMetaNodeType(SLRecursiveMode.RECURSIVE,
                VisibilityLevel.PUBLIC,
                MetaNodeTypeProperty.DESCRIPTION,
                LogicOperator.EQUALS,
                BooleanOperator.AND, values2Find);

        Assert.assertEquals(0, foundTypes.size());
    }

    @Test
    public void testSearchMetadataEqualsMoreElementsByDescriptionUsingOr() {
        final SLNode rootNode = session.createContext("Test1").getRootNode();
        rootNode.addChildNode(JavaClassNodeWithoutPropertiesAndDescription.class, "testNode");
        rootNode.addChildNode(JavaClassNodeWithoutDescription.class, "testNode2");

        final List<String> values2Find = new ArrayList<String>();
        values2Find.add(JavaClassNodeWithoutPropertiesAndDescription.class.getName());
        values2Find.add(JavaElementNode.class.getName());
        session.save();
        final Collection<SLMetaNodeType> foundTypes = session.getMetadata().searchMetaNodeType(SLRecursiveMode.RECURSIVE,
                VisibilityLevel.PUBLIC,
                MetaNodeTypeProperty.DESCRIPTION,
                LogicOperator.EQUALS,
                BooleanOperator.OR, values2Find);

        Assert.assertEquals(2, foundTypes.size());
    }

    @Test
    public void testSearchMetadataEqualsMoreElementsByDescriptionUsingOrAndVisibilityNull() {
        final SLNode rootNode = session.createContext("Test1").getRootNode();
        rootNode.addChildNode(JavaClassNodeWithoutPropertiesAndDescription.class, "testNode");
        rootNode.addChildNode(JavaClassNodeWithoutDescription.class, "testNode2");

        final List<String> values2Find = new ArrayList<String>();
        values2Find.add(JavaClassNodeWithoutPropertiesAndDescription.class.getName());
        values2Find.add(JavaElementNode.class.getName());
        session.save();
        final Collection<SLMetaNodeType> foundTypes = session.getMetadata().searchMetaNodeType(SLRecursiveMode.RECURSIVE, null,
                MetaNodeTypeProperty.DESCRIPTION,
                LogicOperator.EQUALS,
                BooleanOperator.OR, values2Find);

        Assert.assertEquals(2, foundTypes.size());
    }

    @Test
    public void testSearchMetadataEqualsMoreElementsByDescriptionUsingOrAndVisibilityNullNotRecursive() {
        final SLNode rootNode = session.createContext("Test1").getRootNode();
        rootNode.addChildNode(JavaClassNodeWithoutPropertiesAndDescription.class, "testNode");
        rootNode.addChildNode(JavaClassNodeWithoutDescription.class, "testNode2");

        final List<String> values2Find = new ArrayList<String>();
        values2Find.add(JavaClassNodeWithoutDescription.class.getName());
        values2Find.add(JavaElementNode.class.getName());
        session.save();
        final Collection<SLMetaNodeType> foundTypes = session.getMetadata().searchMetaNodeType(SLRecursiveMode.NOT_RECURSIVE,
                null,
                MetaNodeTypeProperty.DESCRIPTION,
                LogicOperator.EQUALS,
                BooleanOperator.OR, values2Find);

        Assert.assertEquals(1, foundTypes.size());
    }

    @Test
    public void testSearchMetadataEqualsMoreElementsByDescriptionUsingOrAndVisibilityPrivate() {
        final SLNode rootNode = session.createContext("Test1").getRootNode();
        rootNode.addChildNode(JavaClassNodeWithoutPropertiesAndDescription.class, "testNode");
        rootNode.addChildNode(JavaClassNodeWithoutDescription.class, "testNode2");

        final List<String> values2Find = new ArrayList<String>();
        values2Find.add(JavaClassNodeWithoutPropertiesAndDescription.class.getName());
        values2Find.add(JavaElementNode.class.getName());

        final Collection<SLMetaNodeType> foundTypes = session.getMetadata().searchMetaNodeType(SLRecursiveMode.RECURSIVE,
                VisibilityLevel.PRIVATE,
                MetaNodeTypeProperty.DESCRIPTION,
                LogicOperator.EQUALS,
                BooleanOperator.OR, values2Find);

        Assert.assertEquals(0, foundTypes.size());
    }

    @Test
    public void testSearchMetadataEqualsMoreElementsByNameUsingAnd() {
        final SLNode rootNode = session.createContext("Test1").getRootNode();
        rootNode.addChildNode(JavaClassNodeWithoutProperties.class, "testNode");
        rootNode.addChildNode(JavaClassNode.class, "testNode2");

        final List<String> values2Find = new ArrayList<String>();
        values2Find.add(JavaClassNodeWithoutProperties.class.getName());
        values2Find.add(JavaElementNode.class.getName());

        final Collection<SLMetaNodeType> foundTypes = session.getMetadata().searchMetaNodeType(SLRecursiveMode.RECURSIVE,
                VisibilityLevel.PUBLIC,
                MetaNodeTypeProperty.NAME,
                LogicOperator.EQUALS,
                BooleanOperator.AND, values2Find);

        Assert.assertEquals(0, foundTypes.size());
    }

    @Test
    public void testSearchMetadataEqualsMoreElementsByNameUsingOr() {
        final SLNode rootNode = session.createContext("Test1").getRootNode();
        rootNode.addChildNode(JavaClassNodeWithoutProperties.class, "testNode");
        rootNode.addChildNode(JavaClassNode.class, "testNode2");

        final List<String> values2Find = new ArrayList<String>();
        values2Find.add(JavaClassNodeWithoutProperties.class.getName());
        values2Find.add(JavaElementNode.class.getName());
        session.save();
        final Collection<SLMetaNodeType> foundTypes = session.getMetadata().searchMetaNodeType(SLRecursiveMode.RECURSIVE,
                VisibilityLevel.PUBLIC,
                MetaNodeTypeProperty.NAME,
                LogicOperator.EQUALS,
                BooleanOperator.OR, values2Find);

        Assert.assertEquals(2, foundTypes.size());
    }

    @Test
    public void testSearchMetadataEqualsMoreElementsByNameUsingOrAndVisibilityNull() {
        final SLNode rootNode = session.createContext("Test1").getRootNode();
        rootNode.addChildNode(JavaClassNodeWithoutProperties.class, "testNode");
        rootNode.addChildNode(JavaClassNode.class, "testNode2");

        final List<String> values2Find = new ArrayList<String>();
        values2Find.add(JavaClassNodeWithoutProperties.class.getName());
        values2Find.add(JavaElementNode.class.getName());
        session.save();
        final Collection<SLMetaNodeType> foundTypes = session.getMetadata().searchMetaNodeType(SLRecursiveMode.RECURSIVE, null,
                MetaNodeTypeProperty.NAME,
                LogicOperator.EQUALS,
                BooleanOperator.OR, values2Find);

        Assert.assertEquals(2, foundTypes.size());
    }

    @Test
    public void testSearchMetadataEqualsMoreElementsByNameUsingOrAndVisibilityNullNotRecursive() {
        final SLNode rootNode = session.createContext("Test1").getRootNode();
        rootNode.addChildNode(JavaClassNodeWithoutProperties.class, "testNode");
        rootNode.addChildNode(JavaClassNode.class, "testNode2");

        final List<String> values2Find = new ArrayList<String>();
        values2Find.add(JavaClassNode.class.getName());
        values2Find.add(JavaElementNode.class.getName());
        session.save();
        final Collection<SLMetaNodeType> foundTypes = session.getMetadata().searchMetaNodeType(SLRecursiveMode.NOT_RECURSIVE,
                null, MetaNodeTypeProperty.NAME,
                LogicOperator.EQUALS,
                BooleanOperator.OR, values2Find);

        Assert.assertEquals(1, foundTypes.size());
    }

    @Test
    public void testSearchMetadataEqualsMoreElementsByNameUsingOrAndVisibilityPrivate() {
        final SLNode rootNode = session.createContext("Test1").getRootNode();
        rootNode.addChildNode(JavaClassNodeWithoutProperties.class, "testNode");
        rootNode.addChildNode(JavaClassNode.class, "testNode2");

        final List<String> values2Find = new ArrayList<String>();
        values2Find.add(JavaClassNodeWithoutProperties.class.getName());
        values2Find.add(JavaElementNode.class.getName());

        final Collection<SLMetaNodeType> foundTypes = session.getMetadata().searchMetaNodeType(SLRecursiveMode.RECURSIVE,
                VisibilityLevel.PRIVATE,
                MetaNodeTypeProperty.NAME,
                LogicOperator.EQUALS,
                BooleanOperator.OR, values2Find);

        Assert.assertEquals(0, foundTypes.size());
    }

    @Test
    public void testSearchMetadataEqualsOneElementByDescription() {
        final SLNode rootNode = session.createContext("Test1").getRootNode();
        rootNode.addChildNode(JavaClassNodeWithoutDescription.class, "testNode2");
        rootNode.addChildNode(JavaClassNodeWithoutPropertiesAndDescription.class, "testNode");

        final List<String> values2Find = new ArrayList<String>();
        values2Find.add(JavaClassNodeWithoutDescription.class.getName());
        session.save();
        final Collection<SLMetaNodeType> foundTypes = session.getMetadata().searchMetaNodeType(SLRecursiveMode.RECURSIVE,
                VisibilityLevel.PUBLIC,
                MetaNodeTypeProperty.DESCRIPTION,
                LogicOperator.EQUALS,
                BooleanOperator.AND, values2Find);

        Assert.assertEquals(1, foundTypes.size());
        Assert.assertEquals(JavaClassNodeWithoutDescription.class.getName(), foundTypes.iterator().next().getTypeName());

    }

    @Test
    public void testSearchMetadataEqualsOneElementByName() {
        final SLNode rootNode = session.createContext("Test1").getRootNode();
        rootNode.addChildNode(JavaClassNodeWithoutProperties.class, "testNode");
        rootNode.addChildNode(JavaClassNode.class, "testNode2");

        final List<String> values2Find = new ArrayList<String>();
        values2Find.add("org.openspotlight.graph.test.domain.node.JavaClassNode");
        session.save();
        final Collection<SLMetaNodeType> foundTypes = session.getMetadata().searchMetaNodeType(SLRecursiveMode.RECURSIVE,
                VisibilityLevel.PUBLIC,
                MetaNodeTypeProperty.NAME,
                LogicOperator.EQUALS,
                BooleanOperator.AND, values2Find);

        Assert.assertEquals(1, foundTypes.size());
        Assert.assertEquals("org.openspotlight.graph.test.domain.node.JavaClassNode", foundTypes.iterator().next().getTypeName());

    }

    @Test
    public void testSearchMetadataLikeBeginsWithOneElementByDescription() {
        final SLNode rootNode = session.createContext("Test1").getRootNode();
        rootNode.addChildNode(JavaClassNodeWithoutPropertiesAndDescription.class, "testNode");
        rootNode.addChildNode(JavaClassNodeWithoutDescription.class, "testNode2");

        final List<String> values2Find = new ArrayList<String>();
        values2Find.add("JavaClass");

        final Collection<SLMetaNodeType> foundTypes = session.getMetadata().searchMetaNodeType(SLRecursiveMode.RECURSIVE,
                VisibilityLevel.PUBLIC,
                MetaNodeTypeProperty.DESCRIPTION,
                LogicOperator.LIKE_BEGINS_WITH,
                BooleanOperator.AND, values2Find);
        Assert.assertEquals(0, foundTypes.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSearchMetadataLikeBeginsWithOneElementByName() {
        final SLNode rootNode = session.createContext("Test1").getRootNode();
        rootNode.addChildNode(JavaClassNodeWithoutProperties.class, "testNode");
        rootNode.addChildNode(JavaClassNode.class, "testNode2");

        final List<String> values2Find = new ArrayList<String>();
        values2Find.add("JavaClass");

        session.getMetadata().searchMetaNodeType(SLRecursiveMode.RECURSIVE, VisibilityLevel.PUBLIC, MetaNodeTypeProperty.NAME,
                LogicOperator.LIKE_BEGINS_WITH, BooleanOperator.AND, values2Find);
    }

    @Test
    public void testSearchMetadataLikeContainsWithOneElementByDescription() {
        final SLNode rootNode = session.createContext("Test1").getRootNode();
        rootNode.addChildNode(JavaClassNodeWithoutPropertiesAndDescription.class, "testNode");
        rootNode.addChildNode(JavaClassNodeWithoutDescription.class, "testNode2");

        final List<String> values2Find = new ArrayList<String>();
        values2Find.add("JavaClass");
        session.save();
        final Collection<SLMetaNodeType> foundTypes = session.getMetadata().searchMetaNodeType(SLRecursiveMode.RECURSIVE,
                VisibilityLevel.PUBLIC,
                MetaNodeTypeProperty.DESCRIPTION,
                LogicOperator.LIKE_CONTAINS,
                BooleanOperator.AND, values2Find);
        Assert.assertEquals(2, foundTypes.size());
    }

    @Test
    public void testSearchMetadataLikeEndsWithOneElementByDescription() {
        final SLNode rootNode = session.createContext("Test1").getRootNode();
        rootNode.addChildNode(JavaClassNodeWithoutPropertiesAndDescription.class, "testNode");
        rootNode.addChildNode(JavaClassNodeWithoutDescription.class, "testNode2");

        final List<String> values2Find = new ArrayList<String>();
        values2Find.add("WithoutDescription");
        session.save();
        final Collection<SLMetaNodeType> foundTypes = session.getMetadata().searchMetaNodeType(SLRecursiveMode.RECURSIVE,
                VisibilityLevel.PUBLIC,
                MetaNodeTypeProperty.DESCRIPTION,
                LogicOperator.LIKE_ENDS_WITH,
                BooleanOperator.AND, values2Find);
        Assert.assertEquals(1, foundTypes.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSearchMetadataLikeEndsWithOneElementByName() {
        final SLNode rootNode = session.createContext("Test1").getRootNode();
        rootNode.addChildNode(JavaClassNodeWithoutProperties.class, "testNode");
        rootNode.addChildNode(JavaClassNode.class, "testNode2");

        final List<String> values2Find = new ArrayList<String>();
        values2Find.add("JavaClass");

        session.getMetadata().searchMetaNodeType(SLRecursiveMode.RECURSIVE, VisibilityLevel.PUBLIC, MetaNodeTypeProperty.NAME,
                LogicOperator.LIKE_ENDS_WITH, BooleanOperator.AND, values2Find);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSearchMetadataLikeOneElementByName() {
        final SLNode rootNode = session.createContext("Test1").getRootNode();
        rootNode.addChildNode(JavaClassNodeWithoutProperties.class, "testNode");
        rootNode.addChildNode(JavaClassNode.class, "testNode2");

        final List<String> values2Find = new ArrayList<String>();
        values2Find.add("JavaClass");

        session.getMetadata().searchMetaNodeType(SLRecursiveMode.RECURSIVE, VisibilityLevel.PUBLIC, MetaNodeTypeProperty.NAME,
                LogicOperator.LIKE_CONTAINS, BooleanOperator.AND, values2Find);
    }

    @Test
    public void testSearchSubMetadata() {
        final SLNode rootNode = session.createContext("Test1").getRootNode();
        rootNode.addChildNode(JavaClassNodeWithoutPropertiesAndDescription.class, "testNode");
        rootNode.addChildNode(JavaClassNodeWithoutDescription.class, "testNode2");

        List<String> values2Find = new ArrayList<String>();
        values2Find.add(JavaElementNode.class.getName());

        Collection<SLMetaNodeType> foundTypes = session.getMetadata().searchMetaNodeType(SLRecursiveMode.RECURSIVE,
                VisibilityLevel.PUBLIC,
                MetaNodeTypeProperty.NAME,
                LogicOperator.EQUALS,
                BooleanOperator.AND, values2Find);
        Assert.assertEquals(1, foundTypes.size());

        final SLMetaNodeType rootType = foundTypes.iterator().next();

        Assert.assertNotNull(rootType.getSubMetaNodeType(JavaClassNodeWithoutDescription.class.getName()));

        values2Find = new ArrayList<String>();
        values2Find.add(JavaClassNodeWithoutDescription.class.getName());

        foundTypes = rootType.searchSubMetaNodeTypes(SLRecursiveMode.NOT_RECURSIVE, VisibilityLevel.PUBLIC,
                MetaNodeTypeProperty.NAME, LogicOperator.EQUALS, BooleanOperator.AND,
                values2Find);
        Assert.assertEquals(1, foundTypes.size());
    }

    @Test
    public void testSearchSubMetadata2() {
        final SLNode rootNode = session.createContext("Test1").getRootNode();
        rootNode.addChildNode(JavaClassNodeWithoutPropertiesAndDescription.class, "testNode");
        rootNode.addChildNode(JavaClassNodeWithoutDescription.class, "testNode2");
        rootNode.addChildNode(SubJavaClassNodeWithoutDescription.class, "testNode3");

        List<String> values2Find = new ArrayList<String>();
        values2Find.add(JavaElementNode.class.getName());

        Collection<SLMetaNodeType> foundTypes = session.getMetadata().searchMetaNodeType(SLRecursiveMode.RECURSIVE,
                VisibilityLevel.PUBLIC,
                MetaNodeTypeProperty.NAME,
                LogicOperator.EQUALS,
                BooleanOperator.AND, values2Find);
        Assert.assertEquals(1, foundTypes.size());

        final SLMetaNodeType rootType = foundTypes.iterator().next();

        Assert.assertNotNull(rootType.getSubMetaNodeType(JavaClassNodeWithoutDescription.class.getName()));
        Assert.assertEquals(
                1,
                rootType.getSubMetaNodeType(JavaClassNodeWithoutDescription.class.getName()).getSubMetaNodeTypes().size());

        values2Find = new ArrayList<String>();
        values2Find.add("JavaClass");
        session.save();

        foundTypes = rootType.searchSubMetaNodeTypes(SLRecursiveMode.RECURSIVE, VisibilityLevel.PUBLIC,
                MetaNodeTypeProperty.DESCRIPTION, LogicOperator.LIKE_CONTAINS,
                BooleanOperator.AND, values2Find);
        Assert.assertEquals(2, foundTypes.size());
    }

}
