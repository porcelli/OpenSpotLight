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
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.*;
import org.openspotlight.bundle.language.java.bundle.JavaBinaryProcessor;
import org.openspotlight.bundle.language.java.bundle.JavaGlobalPhase;
import org.openspotlight.common.util.SLCollections;
import org.openspotlight.federation.context.DefaultExecutionContextFactoryModule;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.context.ExecutionContextFactory;
import org.openspotlight.federation.domain.*;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.federation.domain.artifact.LastProcessStatus;
import org.openspotlight.federation.domain.artifact.StreamArtifact;
import org.openspotlight.federation.log.DetailedLoggerModule;
import org.openspotlight.federation.scheduler.DefaultScheduler;
import org.openspotlight.federation.scheduler.GlobalSettingsSupport;
import org.openspotlight.graph.guice.SLGraphModule;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.SLPartition;
import org.openspotlight.storage.redis.guice.JRedisFactory;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

import java.util.Set;

import static org.openspotlight.common.util.Files.delete;
import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

public class JavaBinaryProcessorTest {

    private static class RepositoryData {
        public final GlobalSettings settings;
        public final Repository repository;
        public final Group group;
        public final ArtifactSource artifactSource;

        public RepositoryData(
                final GlobalSettings settings, final Repository repository, final Group group,
                final ArtifactSource artifactSource) {
            this.settings = settings;
            this.repository = repository;
            this.group = group;
            this.artifactSource = artifactSource;
        }
    }

    private static ExecutionContextFactory contextFactory;
    private static RepositoryData data;
    private static DefaultScheduler scheduler;

    @AfterClass
    public static void closeResources() throws Exception {
        contextFactory.closeResources();
    }

    private static RepositoryData createRepositoryData() {
        final GlobalSettings settings = new GlobalSettings();
        settings.setDefaultSleepingIntervalInMilliseconds(1000);

        GlobalSettingsSupport.initializeScheduleMap(settings);
        final Repository repository = new Repository();
        repository.setName("sampleRepository");
        repository.setActive(true);
        final Group group = new Group();
        group.setName("sampleGroup");
        group.setRepository(repository);
        repository.getGroups().add(group);
        group.setActive(true);
        final ArtifactSource artifactSource = new ArtifactSource();
        repository.getArtifactSources().add(artifactSource);
        artifactSource.setRepository(repository);
        artifactSource.setName("jar files");
        artifactSource.setActive(true);
        artifactSource.setBinary(true);
        artifactSource.setInitialLookup("src/test/");

        final ArtifactSourceMapping mapping = new ArtifactSourceMapping();
        mapping.setSource(artifactSource);
        artifactSource.getMappings().add(mapping);
        mapping.setFrom("resources/");
        mapping.setTo("jars");
        artifactSource.getMappings().add(mapping);
        mapping.getIncludeds().add("**dynamo-file-gen-1.0.1.jar");
        final BundleProcessorType commonProcessor = new BundleProcessorType();
        commonProcessor.setActive(true);
        commonProcessor.setGroup(group);
        commonProcessor.setGlobalPhase(JavaGlobalPhase.class);
        commonProcessor.getArtifactPhases().add(JavaBinaryProcessor.class);
        group.getBundleTypes().add(commonProcessor);

        final BundleSource bundleSource = new BundleSource();
        commonProcessor.getSources().add(bundleSource);
        bundleSource.setBundleProcessorType(commonProcessor);
        bundleSource.setRelative("/jars/");
        bundleSource.getIncludeds().add("**/dynamo-file-gen-1.0.1.jar");

        return new RepositoryData(settings, repository, group, artifactSource);
    }

    @BeforeClass
    public static void setupResources() throws Exception {
        delete("./target/test-data/DbTableArtifactBundleProcessorTest"); //$NON-NLS-1$

        JcrConnectionProvider.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR).closeRepositoryAndCleanResources();
        Injector injector = Guice.createInjector(new JRedisStorageModule(STStorageSession.STFlushMode.AUTO,
                ExampleRedisConfig.EXAMPLE.getMappedServerConfig(),
                repositoryPath("repository")),
                new SimplePersistModule(), new DetailedLoggerModule(),
                new DefaultExecutionContextFactoryModule(), new SLGraphModule(DefaultJcrDescriptor.TEMP_DESCRIPTOR));

        injector.getInstance(JRedisFactory.class).getFrom(SLPartition.GRAPH).flushall();
        data = createRepositoryData();
        contextFactory = injector.getInstance(ExecutionContextFactory.class);

        final ExecutionContext context = contextFactory.createExecutionContext("username", "password",
                DefaultJcrDescriptor.TEMP_DESCRIPTOR,
                data.repository);

        context.getDefaultConfigurationManager().saveGlobalSettings(data.settings);
        context.getDefaultConfigurationManager().saveRepository(data.repository);
        context.closeResources();

        scheduler = DefaultScheduler.INSTANCE;
        scheduler.initializeSettings(contextFactory, "user", "password", DefaultJcrDescriptor.TEMP_DESCRIPTOR);
        scheduler.refreshJobs(data.settings, SLCollections.setOf(data.repository));
        scheduler.startScheduler();

    }

    @After
    public void closeTestResources() {
        contextFactory.closeResources();
    }

    private void reloadArtifactsAndCallBundleProcessor() {
        scheduler.fireSchedulable("username", "password", data.artifactSource);
        scheduler.fireSchedulable("username", "password", data.group);
    }

    @Test
    public void shouldProcessJarFile() throws Exception {
        reloadArtifactsAndCallBundleProcessor();

        final ExecutionContext context = contextFactory.createExecutionContext("", "", DefaultJcrDescriptor.TEMP_DESCRIPTOR,
                data.repository);
        Iterable<String> list = context.getPersistentArtifactManager().getInternalMethods().retrieveNames(StreamArtifact.class, null);
        for (String s : list)
            System.err.println(s);
        final StreamArtifact jarArtifact = context.getPersistentArtifactManager().findByPath(StreamArtifact.class,
                "/jars/resources/dynamo-file-gen-1.0.1.jar");
        Assert.assertThat(jarArtifact.getLastProcessStatus(), Is.is(LastProcessStatus.PROCESSED));
        Assert.assertThat(jarArtifact.getUniqueContextName(), Is.is(IsNull.notNullValue()));
    }

}
