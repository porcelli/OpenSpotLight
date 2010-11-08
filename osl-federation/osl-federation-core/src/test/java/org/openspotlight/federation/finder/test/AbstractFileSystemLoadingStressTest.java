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

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.openspotlight.domain.ArtifactSource;
import org.openspotlight.domain.ArtifactSourceMapping;
import org.openspotlight.domain.GlobalSettings;
import org.openspotlight.domain.Group;
import org.openspotlight.domain.Repository;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.finder.PersistentArtifactManager;
import org.openspotlight.federation.loader.MutableConfigurationManager;
import org.openspotlight.federation.log.DetailedLoggerModule;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.task.ExecutorInstance;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.internal.ImmutableList;

public abstract class AbstractFileSystemLoadingStressTest {

    private static ArtifactSource artifactSource;
    protected Injector            injector;

    private static class RepositoryData {
        public final GlobalSettings settings;
        public final Repository     repository;
        public final Group          group;
        public final ArtifactSource artifactSource;

        public RepositoryData(final GlobalSettings settings,
                              final Repository repository, final Group group,
                              final ArtifactSource artifactSource) {
            this.settings = settings;
            this.repository = repository;
            this.group = group;
            this.artifactSource = artifactSource;
        }
    }

    private static RepositoryData data;

    @AfterClass
    public static void closeResources()
        throws Exception {
        // TODO
    }

    private static RepositoryData createRepositoryData() {
        final GlobalSettings settings = new GlobalSettings();
        settings.setDefaultSleepingIntervalInMilliseconds(300);

        final Repository repository = new Repository();
        repository.setName("sampleRepository");
        repository.setActive(true);
        final Group group = new Group();
        group.setName("sampleGroup");
        group.setRepository(repository);
        repository.getGroups().add(group);
        group.setActive(true);
        artifactSource = new ArtifactSource();
        group.getArtifactSources().add(artifactSource);
        artifactSource.setRepository(repository);
        artifactSource.setName("lots of files");
        artifactSource.setActive(true);
        artifactSource.setBinary(false);
        artifactSource.setInitialLookup("/Users/feu/much-data");
        artifactSource.setInitialLookup("./");
        final ArtifactSourceMapping mapping = new ArtifactSourceMapping();
        mapping.setSource(artifactSource);
        artifactSource.getMappings().add(mapping);
        mapping.setFrom("files");
        mapping.setFrom("src");
        mapping.setTo("OSL");
        artifactSource.getMappings().add(mapping);
        mapping.getIncludeds().add("**/*");
        // mapping.getIncludeds().add("**/XmlConfigurationManagerFactory.java");
        // //TODO remove this

        return new RepositoryData(settings, repository, group, artifactSource);
    }

    private boolean                     runned = false;
    private MutableConfigurationManager configurationManager;
    private PersistentArtifactManager   persistentArtifactManager;

    @Before
    public void setupResources()
        throws Exception {
        if (!runned) {
            injector = Guice.createInjector(createStorageModule(), new SimplePersistModule(),
                    new DetailedLoggerModule());
            clearData();
            data = createRepositoryData();
            configurationManager = injector
                    .getInstance(MutableConfigurationManager.class);

            configurationManager.saveGlobalSettings(data.settings);
            configurationManager.saveRepository(data.repository);
            configurationManager.closeResources();
            persistentArtifactManager = injector.getInstance(PersistentArtifactManager.class);
            runned = true;
        }

    }

    protected abstract void clearData()
        throws Exception;

    protected abstract Module createStorageModule()
            throws Exception;

    @After
    public void closeTestResources() {
        // TODO
    }

    private void reloadArtifacts() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void shouldProcessJarFile()
        throws Exception {
        System.out.println("about to load all items from its origin");
        reloadArtifacts();
        System.out.println("finished to load all items from its origin");

        System.out.println("about to load item names from persistent storage");
        final Iterable<String> list = persistentArtifactManager
                .getInternalMethods().retrieveNames(StringArtifact.class, null);
        System.out
                .println("finished to load item names from persistent storage");

        final int size = 50;
        // size = 1 ;//TODO remove this
        final AtomicInteger loadedSize = new AtomicInteger(0);
        final AtomicInteger nullSize = new AtomicInteger(0);
        final AtomicInteger fileContentNotEqualsSize = new AtomicInteger(0);
        System.out
                .println("about to load item contents from persistent storage");
        final List<Callable<Void>> callables = newArrayList();
        for (final String s: list) {
            callables.add(new Callable<Void>() {
                @Override
                public Void call()
                    throws Exception {
                    final StringArtifact file = persistentArtifactManager.findByPath(
                                    StringArtifact.class, s);
                    assertThat(file, is(notNullValue()));
                    final List<String> lazyLoadedContent = file.getContent().get(
                            persistentArtifactManager
                                    .getSimplePersist());
                    if (lazyLoadedContent == null) {
                        nullSize.incrementAndGet();
                        System.out.println(s + " got null content");
                    }

                    if (lazyLoadedContent == null
                            || !lazyLoadedContent
                                    .equals(getFileContentAsStringList(file
                                            .getOriginalName()))) {
                        fileContentNotEqualsSize.incrementAndGet();
                    }
                    if (lazyLoadedContent != null
                            && lazyLoadedContent.size() != 0) {
                        loadedSize.incrementAndGet();
                    }
                    return null;
                }

            });

        }
        ExecutorInstance.INSTANCE.invokeAll(callables);
        System.out
                .println("finished to load item contents from persistent storage");
        assertThat(loadedSize.get() >= size, is(true));
        assertThat(nullSize.get(), is(0));
        assertThat(fileContentNotEqualsSize.get(), is(0));

    }

    private List<String> getFileContentAsStringList(final String originalName)
            throws Exception {
        final BufferedReader reader = new BufferedReader(new FileReader(originalName));
        final ImmutableList.Builder<String> builder = ImmutableList.builder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            builder.add(line);
        }
        reader.close();
        return builder.build();
    }

}
