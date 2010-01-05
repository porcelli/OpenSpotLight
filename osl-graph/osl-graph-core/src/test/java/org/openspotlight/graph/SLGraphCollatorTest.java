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

import java.text.Collator;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.common.exception.AbstractFactoryException;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.graph.annotation.SLVisibility.VisibilityLevel;
import org.openspotlight.graph.test.domain.JavaClassJavaMethodSimpleLink;
import org.openspotlight.graph.test.domain.JavaClassNode;
import org.openspotlight.graph.test.domain.JavaMethodNode;
import org.openspotlight.graph.test.domain.SQLElement;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;
import org.openspotlight.security.idm.auth.IdentityException;

public class SLGraphCollatorTest {

    static final Logger              LOGGER = Logger.getLogger(SLGraphTest.class);

    private static SLGraph           graph;

    private static SLGraphSession    session;

    private static AuthenticatedUser user;

    @AfterClass( )
    public static void finish() {
        session.close();
        graph.shutdown();
    }

    @BeforeClass
    public static void init() throws AbstractFactoryException,
        SLInvalidCredentialException, IdentityException {

        JcrConnectionProvider.createFromData(
                                             DefaultJcrDescriptor.TEMP_DESCRIPTOR).closeRepositoryAndCleanResources();

        final SLGraphFactory factory = AbstractFactory
                                                      .getDefaultInstance(SLGraphFactory.class);
        graph = factory.createGraph(DefaultJcrDescriptor.TEMP_DESCRIPTOR);

        final SecurityFactory securityFactory = AbstractFactory
                                                               .getDefaultInstance(SecurityFactory.class);
        final User simpleUser = securityFactory.createUser("testUser");
        user = securityFactory.createIdentityManager(
                                                     DefaultJcrDescriptor.TEMP_DESCRIPTOR).authenticate(simpleUser,
                                                                                                        "password");
    }

    @After
    public void afterTest() throws SLGraphSessionException {
        session.clear();
    }

    @Before
    public void beforeTest() throws SLGraphException,
        SLInvalidCredentialException {
        if (session == null) {
            session = graph.openSession(user, SLConsts.DEFAULT_REPOSITORY_NAME);
        }
    }

    @Test
    public void testLinkPropertyCollator() {

        try {

            final SLNode root1 = session.createContext("1L").getRootNode();
            final JavaClassNode javaClassNode1 = root1.addNode(
                                                               JavaClassNode.class, "javaClassNode1");
            final JavaMethodNode javaMethodNode1 = javaClassNode1.addNode(
                                                                          JavaMethodNode.class, "javaMethodNode1");

            final JavaClassJavaMethodSimpleLink link = session.addLink(
                                                                       JavaClassJavaMethodSimpleLink.class, javaClassNode1,
                                                                       javaMethodNode1, false);

            final SLLinkProperty<String> prop1 = link.setProperty(String.class, VisibilityLevel.PUBLIC,
                                                                  "selecao", "great");
            final SLLinkProperty<String> prop2 = link.getProperty(String.class,
                                                                  "sele\u00E7\u00E3o");

            Assert.assertEquals(prop1, prop2);
            Assert.assertEquals(prop1.getName(), "selecao");
            Assert.assertEquals(prop1.getName(), "selecao");

            try {
                final Collator collator = Collator.getInstance(Locale.US);
                collator.setStrength(Collator.TERTIARY);
                link.getProperty(String.class, "sele\u00E7\u00E3o", collator);
                Assert.fail();
            } catch (final SLNodePropertyNotFoundException e) {
                Assert.assertTrue(true);
            }
        } catch (final SLException e) {
            LOGGER.error(e.getMessage(), e);
            Assert.fail();
        } catch (final SLInvalidCredentialException e) {
            LOGGER.error(e);
            Assert.fail();
        }
    }

    @Test
    public void testNodeCollator() {

        try {
            final SLNode root1 = session.createContext("1L").getRootNode();

            // test addNode ...
            final SQLElement element1 = root1.addNode(SQLElement.class,
                                                      "selecao");
            final SQLElement element2 = root1.addNode(SQLElement.class,
                                                      "sele\u00E7\u00E3o");
            Assert.assertEquals(element1, element2);

            // test getNode ...
            final SQLElement element3 = root1.getNode(SQLElement.class,
                                                      "sele\u00E7\u00E3o");
            Assert.assertEquals(element1, element3);

            // the original name remains ...
            Assert.assertEquals(element1.getName(), "selecao");
            Assert.assertEquals(element2.getName(), "selecao");
            Assert.assertEquals(element3.getName(), "selecao");
        } catch (final SLException e) {
            LOGGER.error(e.getMessage(), e);
            Assert.fail();
        } catch (final SLInvalidCredentialException e) {
            LOGGER.error(e);
            Assert.fail();
        }
    }

    @Test
    public void testNodePropertyCollator() {

        try {

            final SLNode root1 = session.createContext("1L").getRootNode();
            final SQLElement element = root1.addNode(SQLElement.class,
                                                     "element");

            final SLNodeProperty<String> prop1 = element.setProperty(
                                                                     String.class, VisibilityLevel.PUBLIC, "selecao", "great");
            final SLNodeProperty<String> prop2 = element.getProperty(
                                                                     String.class, "sele\u00E7\u00E3o");
            Assert.assertEquals(prop1, prop2);
            Assert.assertEquals(prop1.getName(), "selecao");
            Assert.assertEquals(prop1.getName(), "selecao");

            try {
                final Collator collator = Collator.getInstance(Locale.US);
                collator.setStrength(Collator.TERTIARY);
                element
                       .getProperty(String.class, "sele\u00E7\u00E3o",
                                    collator);
                Assert.fail();
            } catch (final SLNodePropertyNotFoundException e) {
                Assert.assertTrue(true);
            }
        } catch (final SLException e) {
            LOGGER.error(e.getMessage(), e);
            Assert.fail();
        } catch (final SLInvalidCredentialException e) {
            LOGGER.error(e);
            Assert.fail();
        }
    }

}
