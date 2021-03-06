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
package org.openspotlight.federation.finder.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.jredis.JRedis;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.common.util.SLCollections;
import org.openspotlight.domain.ArtifactSource;
import org.openspotlight.domain.Repository;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.finder.FileSystemOriginArtifactLoader;
import org.openspotlight.federation.finder.PersistentArtifactManagerImpl;
import org.openspotlight.federation.log.DetailedLoggerModule;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.storage.PartitionFactory.RegularPartitions;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.redis.guice.JRedisFactory;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class PersistentArtifactManagerTest {
    /**
     * The provider.
     */

    private static ArtifactSource                artifactSource;

    private static JRedis                        jredis;

    private static PersistentArtifactManagerImpl persistenArtifactManager;
    private static Repository                    repository;

    @AfterClass
    public static void closeResources() {
        persistenArtifactManager.closeResources();
    }

    /**
     * Setup.
     * 
     * @throws Exception the exception
     */
    @BeforeClass
    public static void setup()
        throws Exception {
        final Injector injector = Guice.createInjector(
                new JRedisStorageModule(StorageSession.FlushMode.AUTO,
                        ExampleRedisConfig.EXAMPLE.getMappedServerConfig()),
                new SimplePersistModule(), new DetailedLoggerModule());
        jredis = injector.getInstance(JRedisFactory.class).getFrom(
                RegularPartitions.FEDERATION);
        jredis.flushall();

        artifactSource = new ArtifactSource();
        artifactSource.setName("classpath");
        artifactSource.setInitialLookup("./src");
        repository = new Repository();
        repository.setName("name");
        artifactSource.setRepository(repository);
        final FileSystemOriginArtifactLoader fileSystemFinder = new FileSystemOriginArtifactLoader();
        final Set<StringArtifact> artifacts = fileSystemFinder.listByPath(
                StringArtifact.class, artifactSource, null, null);
        persistenArtifactManager = new PersistentArtifactManagerImpl(
                repository, injector.getInstance(SimplePersistFactory.class));
        for (final StringArtifact artifact: artifacts) {
            artifact.setMappedTo("/src");
            persistenArtifactManager.addTransient(artifact);
        }
        persistenArtifactManager.saveTransientData();

    }

    @Test
    public void shouldFindArtifacts()
        throws Exception {
        final StringArtifact sa = persistenArtifactManager.findByPath(
                StringArtifact.class,
                "/test/resources/artifacts/included/folder/file_included2");
        assertThat(sa, is(notNullValue()));
        assertThat(sa.getContent(), is(notNullValue()));

    }

    @Test
    public void shouldListArtifactNames()
        throws Exception {
        final Iterable<String> artifacts = persistenArtifactManager
                .getInternalMethods().retrieveNames(StringArtifact.class, null);

        assertThat(artifacts, is(notNullValue()));
        assertThat(SLCollections.iterableToList(artifacts).size(), is(not(0)));
        for (final String s: artifacts) {
            assertThat(s, is(notNullValue()));
        }
    }

    @Test
    public void shouldListArtifacts()
        throws Exception {
        final Iterable<StringArtifact> artifacts = persistenArtifactManager
                .listByInitialPath(StringArtifact.class, "/src");

        assertThat(artifacts, is(notNullValue()));
        assertThat(SLCollections.iterableToList(artifacts).size(), is(not(0)));
        for (final StringArtifact sa: artifacts) {
            assertThat(sa, is(notNullValue()));
            assertThat(sa.getContent(), is(notNullValue()));
        }
    }

}
