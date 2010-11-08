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
package org.openspotlight.federation.loader;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.openspotlight.common.util.Files;
import org.openspotlight.domain.ArtifactSource;
import org.openspotlight.domain.ArtifactSourceMapping;
import org.openspotlight.domain.GlobalSettings;
import org.openspotlight.domain.Repository;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.finder.FileSystemOriginArtifactLoader;
import org.openspotlight.federation.finder.OriginArtifactLoader;
import org.openspotlight.federation.finder.PersistentArtifactManagerProviderImpl;
import org.openspotlight.federation.log.DetailedLoggerModule;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.RegularPartitions;
import org.openspotlight.storage.redis.guice.JRedisFactory;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ArtifactLoaderManagerTest {

    @Test
    public void shouldLoad()
        throws Exception {
        final Injector injector = Guice.createInjector(
                new JRedisStorageModule(StorageSession.FlushMode.AUTO,
                        ExampleRedisConfig.EXAMPLE.getMappedServerConfig()),
                new SimplePersistModule(), new DetailedLoggerModule());
        injector.getInstance(JRedisFactory.class)
                .getFrom(RegularPartitions.FEDERATION).flushall();
        final GlobalSettings settings = new GlobalSettings();
        settings.setDefaultSleepingIntervalInMilliseconds(250);
        final String initialRawPath = Files
                .getNormalizedFileName(new File("."));
        final String initial = initialRawPath.substring(0,
                initialRawPath.lastIndexOf('/'));
        final String finalStr = initialRawPath.substring(initial.length());
        final ArtifactSource source = new ArtifactSource();
        final Repository repository = new Repository();
        repository.setName("repository");
        source.setRepository(repository);
        final ArtifactSourceMapping mapping = new ArtifactSourceMapping();
        mapping.setFrom(finalStr);
        mapping.setTo("/sources/java/myProject");
        mapping.setIncludeds(new HashSet<String>());
        mapping.setExcludeds(new HashSet<String>());
        mapping.getIncludeds().add("*.java");
        mapping.setSource(source);
        source.setMappings(new HashSet<ArtifactSourceMapping>());
        source.getMappings().add(mapping);
        source.setActive(true);
        source.setBinary(false);
        source.setInitialLookup(initial);
        source.setName("sourceName");
        final PersistentArtifactManagerProviderImpl provider = new PersistentArtifactManagerProviderImpl(
                injector.getInstance(SimplePersistFactory.class), repository);
        final List<Class<? extends OriginArtifactLoader>> loaderRegistry = new ArrayList<Class<? extends OriginArtifactLoader>>();
        loaderRegistry.add(FileSystemOriginArtifactLoader.class);
        ArtifactLoaderManager.INSTANCE.refreshResources(source,
                provider, loaderRegistry);
        final Iterable<StringArtifact> artifacts = provider.get().listByInitialPath(
                StringArtifact.class, null);
        provider.closeResources();
        boolean hasAny = false;

        for (final Artifact a: artifacts) {
            assertThat(a, is(notNullValue()));
            assertThat(
                    a.getArtifactCompleteName().startsWith(
                            mapping.getTo() + "/"), is(true));
            assertThat(
                    a.getArtifactCompleteName().contains(
                            mapping.getFrom() + "/"), is(false));
            hasAny = true;
        }
        assertThat(hasAny, is(true));
        provider.closeResources();
    }

}
