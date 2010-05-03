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
import org.hamcrest.core.Is;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.common.exception.AbstractFactoryException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

public class MultithreadGraphSessionTest {
    private static enum State {
        NOT_STARTED,
        DONE,
        ERROR

    }

    private static class Worker implements Callable<State> {

        public State state = State.NOT_STARTED;

        public State call() throws Exception {
            try {
                for (int i = 0; i < 100; i++) {
                    MultithreadGraphSessionTest.newNode.addNode("node " + i);
                }
                this.state = State.DONE;
                MultithreadGraphSessionTest.session.save();
            } catch (final Exception e) {
                e.printStackTrace();
                this.state = State.ERROR;
            }
            return this.state;
        }
    }

    /**
     * The graph.
     */
    private static SLGraph graph;

    /**
     * The session.
     */
    private static SLGraphSession session;

    private static AuthenticatedUser user;

    private static SLNode rootNode;

    private static SLNode newNode;

    /**
     * Finish.
     */
    @AfterClass
    public static void finish() {
        MultithreadGraphSessionTest.graph.shutdown();
    }

    /**
     * Inits the.
     *
     * @throws AbstractFactoryException the abstract factory exception
     */
    @BeforeClass
    public static void init() throws AbstractFactoryException, IdentityException {
        Injector injector = Guice.createInjector(new JRedisStorageModule(STStorageSession.STFlushMode.AUTO,
                ExampleRedisConfig.EXAMPLE.getMappedServerConfig(),
                repositoryPath("repository")),
                new SimplePersistModule(), new SLGraphModule(DefaultJcrDescriptor.TEMP_DESCRIPTOR));


        graph = injector.getInstance(SLGraph.class);

        final SecurityFactory securityFactory = injector.getInstance(SecurityFactory.class);
        final User simpleUser = securityFactory.createUser("testUser");
        MultithreadGraphSessionTest.user = securityFactory.createIdentityManager(DefaultJcrDescriptor.TEMP_DESCRIPTOR).authenticate(
                simpleUser,
                "password");
    }

    @Test
    public void startExecutorAndSaveAllChangedGraphSessions() throws Exception {
        MultithreadGraphSessionTest.session = MultithreadGraphSessionTest.graph.openSession(MultithreadGraphSessionTest.user,
                SLConsts.DEFAULT_REPOSITORY_NAME);
        MultithreadGraphSessionTest.rootNode = MultithreadGraphSessionTest.session.createContext("new context").getRootNode();
        MultithreadGraphSessionTest.newNode = MultithreadGraphSessionTest.rootNode.addNode("abc");
        MultithreadGraphSessionTest.session.save();
        // session.close();

        final ExecutorService executor = Executors.newFixedThreadPool(8);
        final List<Callable<State>> workers = new ArrayList<Callable<State>>();
        for (int i = 0; i < 40; i++) {
            workers.add(new Worker());
        }
        final List<Future<State>> allStatus = executor.<State>invokeAll(workers);

        for (final Future<State> status : allStatus) {
            System.out.println(status.get());
        }

        for (final Future<State> status : allStatus) {
            Assert.assertThat(status.get(), Is.is(State.DONE));
        }

        executor.shutdown();
    }
}
