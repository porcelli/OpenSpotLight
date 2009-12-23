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

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.graph.query.SLGraphQueryTest;
import org.openspotlight.graph.test.domain.JavaClassNode;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;

/**
 * The Class SLGraphTest.
 * 
 * @author Vitor Hugo Chagas
 */

public class SLGraphMetadataPropertiesTest {

    /** The Constant LOGGER. */
    static final Logger              LOGGER = Logger.getLogger(SLGraphQueryTest.class);

    /** The graph. */
    private static SLGraph           graph;

    /** The session. */
    private static SLGraphSession    session;

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

            JcrConnectionProvider.createFromData(
                                                 DefaultJcrDescriptor.TEMP_DESCRIPTOR).closeRepositoryAndCleanResources();

            final SecurityFactory securityFactory = AbstractFactory
                                                                   .getDefaultInstance(SecurityFactory.class);

            final User simpleUser = securityFactory.createUser("testUser");
            user = securityFactory.createIdentityManager(
                                                         DefaultJcrDescriptor.TEMP_DESCRIPTOR).authenticate(
                                                                                                            simpleUser, "password");

            final SLGraphFactory factory = AbstractFactory
                                                          .getDefaultInstance(SLGraphFactory.class);
            graph = factory.createGraph(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
            session = graph.openSession(user, SLConsts.DEFAULT_REPOSITORY_NAME);
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Adds the add multiple link empty case.
     */
    @Test
    public void testAddProperty() throws SLContextAlreadyExistsException, SLGraphSessionException, SLInvalidCredentialException {
        SLNode rootNode = session.createContext("Test1").getRootNode();
        SLNode testNode = rootNode.addNode(JavaClassNode.class, "testNode");
        testNode.setProperty(String.class, "somePropName", "value");

        for (SLMetaNodeType metaType : session.getMetadata().getMetaNodesTypes()) {
            for (SLMetaNodeProperty metaProperty : metaType.getMetaProperties()) {
                System.out.println(metaProperty.getName() + ":" + metaProperty.getType());
            }
        }
    }
}
