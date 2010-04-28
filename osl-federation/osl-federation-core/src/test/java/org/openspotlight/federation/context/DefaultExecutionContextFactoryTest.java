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
package org.openspotlight.federation.context;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.*;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.finder.PersistentArtifactManager;
import org.openspotlight.federation.loader.ConfigurationManager;
import org.openspotlight.federation.log.DetailedLoggerModule;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.log.DetailedLogger;
import org.openspotlight.log.DetailedLogger.LogEventType;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.SLPartition;
import org.openspotlight.storage.redis.guice.JRedisServerDetail;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

public class DefaultExecutionContextFactoryTest {

    private ExecutionContext               context;

    private static ExecutionContextFactory factory;

    @BeforeClass
    public static void setup() throws Exception {
        Injector injector = Guice.createInjector(new JRedisStorageModule(STStorageSession.STFlushMode.AUTO,
                                                                         ExampleRedisConfig.EXAMPLE.getMappedServerConfig(),
                                                                         repositoryPath("repository")),
                                                 new SimplePersistModule(), new DetailedLoggerModule(),
                                                 new DefaultExecutionContextFactoryModule());
        factory = injector.getInstance(ExecutionContextFactory.class);

    }

    @After
    public void closeResources() throws Exception {
        factory.closeResources();
    }

    @Before
    public void setupContext() throws Exception {
        Repository repo = new Repository();
        repo.setName("test");
        repo.setActive(true);

        context = factory.createExecutionContext("testUser", "testPassword", DefaultJcrDescriptor.TEMP_DESCRIPTOR, repo);
    }

    @Test
    public void shouldUseAllResourcesInsideContext() throws Exception {
        final PersistentArtifactManager manager = context.getPersistentArtifactManager();
        Assert.assertThat(manager, Is.is(IsNull.notNullValue()));
        final ConfigurationManager configurationManager = context.getDefaultConfigurationManager();
        Assert.assertThat(configurationManager, Is.is(IsNull.notNullValue()));
        final JcrConnectionProvider connectionProvider = context.getDefaultConnectionProvider();
        Assert.assertThat(connectionProvider, Is.is(IsNull.notNullValue()));
        final SLGraphSession graphSession = context.getGraphSession();
        Assert.assertThat(graphSession, Is.is(IsNull.notNullValue()));
        final DetailedLogger logger = context.getLogger();
        Assert.assertThat(logger, Is.is(IsNull.notNullValue()));
        manager.findByPath(StringArtifact.class, "/tmp");

        configurationManager.saveGlobalSettings(new GlobalSettings());

        graphSession.createContext("new context");

        logger.log(context.getUser(), LogEventType.DEBUG, "test");

    }

}
