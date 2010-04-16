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
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openspotlight.common.exception.AbstractFactoryException;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;
import org.openspotlight.security.idm.auth.IdentityException;

@Ignore
public class GraphSavingTest {

    /** The Constant LOGGER. */
    static final Logger              LOGGER = Logger.getLogger(SLGraphTest.class);

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
        GraphSavingTest.session.close();
        GraphSavingTest.graph.shutdown();
    }

    /**
     * Inits the.
     * 
     * @throws AbstractFactoryException the abstract factory exception
     */
    @BeforeClass
    public static void init() throws AbstractFactoryException, IdentityException {
        final SLGraphFactory factory = AbstractFactory
                                                      .getDefaultInstance(SLGraphFactory.class);
        GraphSavingTest.graph = factory
                                       .createGraph(DefaultJcrDescriptor.TEMP_DESCRIPTOR);

        final SecurityFactory securityFactory = AbstractFactory
                                                               .getDefaultInstance(SecurityFactory.class);
        final User simpleUser = securityFactory.createUser("testUser");
        GraphSavingTest.user = securityFactory.createIdentityManager(
                                                                     DefaultJcrDescriptor.TEMP_DESCRIPTOR).authenticate(simpleUser,
                                                                                                                        "password");
    }

    @Test
    public void shouldDoNotSaveChanges() throws Exception {
        final SLGraphSession session = GraphSavingTest.graph
                                                            .openSession(GraphSavingTest.user, SLConsts.DEFAULT_REPOSITORY_NAME);
        session.createContext("new context not saved").getRootNode().addNode(
                                                                             "node 1 not saved").addNode("node 2 not saved");
        session.close();

        final SLGraphSession session1 = GraphSavingTest.graph
                                                             .openSession(GraphSavingTest.user, SLConsts.DEFAULT_REPOSITORY_NAME);
        final SLContext createdCtx = session1
                                             .getContext("new context not saved");
        Assert.assertThat(createdCtx, Is.is(IsNull.nullValue()));
        session1.close();

    }

    @Test
    public void shouldSaveChanges() throws Exception {

        final SLGraphSession session = GraphSavingTest.graph
                                                            .openSession(GraphSavingTest.user, SLConsts.DEFAULT_REPOSITORY_NAME);
        session.createContext("new context").getRootNode().addNode("node 1")
                .addNode("node 2");
        session.save();
        session.close();

        final SLGraphSession session1 = GraphSavingTest.graph
                                                             .openSession(GraphSavingTest.user, SLConsts.DEFAULT_REPOSITORY_NAME);
        final SLContext createdCtx = session1.getContext("new context");
        Assert.assertThat(createdCtx, Is.is(IsNull.notNullValue()));
        final SLNode createdNode1 = createdCtx.getRootNode().getNode("node 1");
        Assert.assertThat(createdNode1, Is.is(IsNull.notNullValue()));
        final SLNode createdNode2 = createdNode1.getNode("node 2");
        Assert.assertThat(createdNode2, Is.is(IsNull.notNullValue()));
        session1.close();
    }

    @Test
    public void shouldSaveChangesOnOneSessionAndDontSaveOnAnother()
            throws Exception {

        final SLGraphSession sessionToSave = GraphSavingTest.graph
                                                                  .openSession(GraphSavingTest.user, SLConsts.DEFAULT_REPOSITORY_NAME);
        sessionToSave.createContext("new saved context").getRootNode().addNode(
                                                                               "node 1 saved").addNode("node 2 saved");
        sessionToSave.save();
        sessionToSave.close();
        final SLGraphSession sessionToDismiss = GraphSavingTest.graph
                                                                     .openSession(GraphSavingTest.user, SLConsts.DEFAULT_REPOSITORY_NAME);
        sessionToDismiss.createContext("new context not saved").getRootNode()
                        .addNode("node 1 not saved").addNode("node 2 not saved");
        sessionToDismiss.close();

        final SLGraphSession session1 = GraphSavingTest.graph
                                                             .openSession(GraphSavingTest.user, SLConsts.DEFAULT_REPOSITORY_NAME);
        final SLContext createdCtx = session1.getContext("new saved context");
        Assert.assertThat(createdCtx, Is.is(IsNull.notNullValue()));
        final SLNode createdNode1 = createdCtx.getRootNode().getNode(
                                                                     "node 1 saved");
        Assert.assertThat(createdNode1, Is.is(IsNull.notNullValue()));
        final SLNode createdNode2 = createdNode1.getNode("node 2 saved");
        Assert.assertThat(createdNode2, Is.is(IsNull.notNullValue()));
        final SLContext nonCreatedCtx = session1
                                                .getContext("new context not saved");
        Assert.assertThat(nonCreatedCtx, Is.is(IsNull.nullValue()));

        session1.close();
    }

}
