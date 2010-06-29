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
package org.openspotlight.bundle.language.java.bundle.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.jackrabbit.rmi.remote.RemoteRepository;
import org.apache.jackrabbit.rmi.server.RemoteAdapterFactory;
import org.apache.jackrabbit.rmi.server.ServerAdapterFactory;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.guice.SLGraphModule;
import org.openspotlight.graph.server.RemoteGraphSessionServer;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.remote.server.UserAuthenticator;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

public class MainClass {

    public static void main( final String... args ) throws Exception {
        Injector injector = Guice.createInjector(new JRedisStorageModule(STStorageSession.STFlushMode.AUTO,
                ExampleRedisConfig.EXAMPLE.getMappedServerConfig(),
                repositoryPath("repository")),
                new SimplePersistModule(), new SLGraphModule(DefaultJcrDescriptor.TEMP_DESCRIPTOR));


        SLGraph graph = injector.getInstance(SLGraph.class);



        final long start = System.currentTimeMillis();
        JcrConnectionProvider.createFromData(DefaultJcrDescriptor.DEFAULT_DESCRIPTOR).openSession();
        final javax.jcr.Repository repository = JcrConnectionProvider.createFromData(DefaultJcrDescriptor.DEFAULT_DESCRIPTOR).getRepository();

        final RemoteAdapterFactory saFactory = new ServerAdapterFactory();
        final RemoteRepository remote = saFactory.getRemoteRepository(repository);

        final Registry registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        registry.bind("jackrabbit.repository", remote);
        final long end = System.currentTimeMillis();
        System.err.println("started in about " + (int)(end - start) / 1000);

        RemoteGraphSessionServer server = null;
        try {
            server = new RemoteGraphSessionServer(new UserAuthenticator() {

                public boolean canConnect( final String userName,
                                           final String password,
                                           final String clientHost ) {
                    return true;
                }

                public boolean equals( final Object o ) {
                    return this.getClass().equals(o.getClass());
                }
            }, 7070, 60 * 1000 * 10L, DefaultJcrDescriptor.DEFAULT_DESCRIPTOR,graph);
            System.err.println("Server waiting connections on port 7070");
            while (true) {
                Thread.sleep(5000);
            }
        } finally {
            if (server != null) {
                server.shutdown();
            }
        }

    }

}
