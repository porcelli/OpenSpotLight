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
package org.openspotlight.graph.server.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openspotlight.graph.SLConsts;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLSimpleGraphSession;
import org.openspotlight.graph.client.RemoteGraphSessionFactory;
import org.openspotlight.graph.client.RemoteGraphSessionFactory.RemoteGraphFactoryConnectionData;
import org.openspotlight.graph.exception.SLGraphException;
import org.openspotlight.graph.guice.SLGraphModule;
import org.openspotlight.graph.server.RemoteGraphSessionServer;
import org.openspotlight.graph.test.BaseGraphTest;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.remote.server.UserAuthenticator;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.SLPartition;
import org.openspotlight.storage.redis.guice.JRedisFactory;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

/**
 * The Class SLGraphTest.
 *
 * @author Vitor Hugo Chagas
 */

public class SLRemoteGraphTest extends BaseGraphTest {
    @Override
    protected void clearSession() {
        session.close();
        session = null;
    }

    private static final String userName = "testUser";

    private static final String pass = "password";

    private static RemoteGraphSessionFactory client;

    public static RemoteGraphSessionServer server;

    @BeforeClass
    public static void init() throws Exception {
        JcrConnectionProvider.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR).closeRepositoryAndCleanResources();
        Injector injector = Guice.createInjector(new JRedisStorageModule(STStorageSession.STFlushMode.AUTO,
                ExampleRedisConfig.EXAMPLE.getMappedServerConfig(),
                repositoryPath("repository")),
                new SimplePersistModule(), new SLGraphModule(DefaultJcrDescriptor.TEMP_DESCRIPTOR));
        injector.getInstance(JRedisFactory.class).getFrom(SLPartition.GRAPH).flushall();


        SLGraph graph = injector.getInstance(SLGraph.class);

        server = new RemoteGraphSessionServer(new UserAuthenticator() {

            public boolean canConnect(final String userName,
                                      final String password,
                                      final String clientHost) {
                return true;
            }

            @Override
            public boolean equals(Object obj) {
                return true;
            }
        }, 7070, 10 * 60 * 1000L, DefaultJcrDescriptor.TEMP_DESCRIPTOR, graph);

    }

    @Override
    public SLSimpleGraphSession openSession() throws SLGraphException {
        return client.createRemoteGraphSession(userName, pass, SLConsts.DEFAULT_REPOSITORY_NAME);
    }

    @Before
    public void setupClient() throws Exception {
        server.removeAllObjectsFromServer();

        client = new RemoteGraphSessionFactory(new RemoteGraphFactoryConnectionData() {

            public String getHost() {
                return "localhost";
            }

            public String getPassword() {
                return "***";
            }

            public int getPort() {
                return 7070;
            }

            public String getUserName() {
                return "***";
            }
        });
        JcrConnectionProvider.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR).closeRepositoryAndCleanResources();
        if (session == null) {
            session = client.createRemoteGraphSession(userName, pass, SLConsts.DEFAULT_REPOSITORY_NAME);
        }


    }
}
