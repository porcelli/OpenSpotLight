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
package org.openspotlight.federation.processing.internal;

import java.io.File;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openspotlight.common.concurrent.NeedsSyncronizationCollection;
import org.openspotlight.common.util.Files;
import org.openspotlight.federation.context.*;
import org.openspotlight.federation.domain.ArtifactSourceMapping;
import org.openspotlight.federation.domain.BundleProcessorType;
import org.openspotlight.federation.domain.BundleSource;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.domain.GroupListener;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.federation.domain.artifact.LastProcessStatus;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.finder.FileSystemOriginArtifactLoader;
import org.openspotlight.federation.finder.PersistentArtifactManagerProviderImpl;
import org.openspotlight.federation.loader.ArtifactLoaderManager;
import org.openspotlight.federation.loader.ConfigurationManager;
import org.openspotlight.federation.loader.XmlConfigurationManagerFactory;
import org.openspotlight.federation.log.DetailedLoggerModule;
import org.openspotlight.federation.processing.DefaultBundleProcessorManager;
import org.openspotlight.federation.processing.BundleProcessorManager.GlobalExecutionStatus;
import org.openspotlight.federation.scheduler.GlobalSettingsSupport;
import org.openspotlight.graph.SLConsts;
import org.openspotlight.graph.SLLink;
import org.openspotlight.graph.SLNode;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;

import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

public class BundleProcessorManagerTest {

    public static class SampleGroupListener implements GroupListener {

        public static AtomicInteger count = new AtomicInteger();

        public ListenerAction groupAdded( final SLNode groupNode,
                                          final ExecutionContext context ) {
            count.incrementAndGet();
            return ListenerAction.CONTINUE;
        }

        public ListenerAction groupRemoved( final SLNode groupNode,
                                            final ExecutionContext context ) {
            return null;
        }

    }

    @Before
    public void cleanGroupListenerCount() throws Exception {
        SampleGroupListener.count.set(0);
    }

    @Before
    public void cleanupOldEntries() throws Exception {
        JcrConnectionProvider.createFromData(
                                             DefaultJcrDescriptor.TEMP_DESCRIPTOR)
                             .closeRepositoryAndCleanResources();
    }

    @Test
    public void shouldProcessMappedArtifactsUsingJcrStreamArtifacts()
            throws Exception {
        ExampleBundleProcessor.allStatus.clear();

        Injector injector = Guice.createInjector(
                new JRedisStorageModule(STStorageSession.STFlushMode.AUTO,
                        ExampleRedisConfig.mappedServerConfig, repositoryPath("repository")),
                new SimplePersistModule(),
                new DetailedLoggerModule(),
                new SingleGraphSessionExecutionContextFactoryModule());


        final ArtifactSource source = new ArtifactSource();
        final String initialRawPath = Files
                                           .getNormalizedFileName(new File(".."));
        final String initial = initialRawPath.substring(0, initialRawPath
                                                                         .lastIndexOf('/'));
        final String finalStr = initialRawPath.substring(initial.length());
        final ArtifactSourceMapping mapping = new ArtifactSourceMapping();
        mapping.setSource(source);
        mapping.setFrom(finalStr);
        mapping.setTo("/sources/java/myProject");
        mapping.setExcludeds(new HashSet<String>());
        mapping.getIncludeds().add("**/ConfigurationManagerProvider.java");
        source.setMappings(new HashSet<ArtifactSourceMapping>());
        source.getMappings().add(mapping);
        source.setActive(true);
        source.setInitialLookup(initial);
        source.setName("sourceName");

        final GlobalSettings settings = new GlobalSettings();
        settings.getLoaderRegistry().add(FileSystemOriginArtifactLoader.class);
        settings.setDefaultSleepingIntervalInMilliseconds(1000);
        final Repository repository = new Repository();
        repository.setActive(true);
        repository.setName("repository");
        source.setRepository(repository);
        final Group group = new Group();
        group.setActive(true);
        group.setName("Group name");
        group.setRepository(repository);
        repository.getGroups().add(group);
        final BundleProcessorType bundleType = new BundleProcessorType();
        bundleType.setActive(true);
        bundleType.setGroup(group);
        bundleType.setGlobalPhase(ExampleBundleProcessor.class);
        group.getBundleTypes().add(bundleType);
        final BundleSource bundleSource = new BundleSource();
        bundleType.getSources().add(bundleSource);
        bundleSource.setBundleProcessorType(bundleType);
        bundleSource.setRelative("/sources/java/myProject");
        bundleSource.getIncludeds().add("**/ConfigurationManagerProvider.java");
        final ExecutionContextFactory contextFactory = injector.getInstance(ExecutionContextFactory.class);
        PersistentArtifactManagerProviderImpl provider = new PersistentArtifactManagerProviderImpl(injector.getInstance(SimplePersistFactory.class), repository);
        ArtifactLoaderManager.INSTANCE.refreshResources(settings, source,
                                                        provider);

        final ExecutionContext context = contextFactory.createExecutionContext(
                                                                               "username", "password", DefaultJcrDescriptor.TEMP_DESCRIPTOR,
                                                                               repository);
        context.getDefaultConfigurationManager().saveGlobalSettings(settings);
        context.getDefaultConfigurationManager().saveRepository(repository);
        contextFactory.closeResources();

        final GlobalExecutionStatus result = DefaultBundleProcessorManager.INSTANCE
                                                                                   .executeBundles("username", "password",
                                                                                                   DefaultJcrDescriptor.TEMP_DESCRIPTOR,
                                                                                                   contextFactory, settings, group);
        Assert.assertThat(ExampleBundleProcessor.allStatus
                                                          .contains(LastProcessStatus.ERROR), Is.is(false));
        Assert.assertThat(ExampleBundleProcessor.allStatus
                                                          .contains(LastProcessStatus.EXCEPTION_DURRING_PROCESS), Is
                                                                                                                    .is(false));
        Assert.assertThat(result, Is.is(GlobalExecutionStatus.SUCCESS));
        final ConfigurationManager xmlManager = XmlConfigurationManagerFactory
                                                                              .loadMutableFromFile("target/BundleProcessorManagerTest/exampleConfigurationFile.xml");
        GlobalSettingsSupport.initializeScheduleMap(settings);
        xmlManager.saveGlobalSettings(settings);
        xmlManager.saveRepository(repository);
        final ExecutionContext context1 = contextFactory
                                                        .createExecutionContext("username", "password",
                                                                                DefaultJcrDescriptor.TEMP_DESCRIPTOR, repository);
        final String nodeName = "/sources/java/myProject/osl-federation-api/src/main/java/org/openspotlight/federation/loader/ConfigurationManagerProvider.java1";
        final SLNode node = context1.getGraphSession().getContext(
                                                                  SLConsts.DEFAULT_GROUP_CONTEXT).getRootNode().getNode(
                                                                                                                        group.getUniqueName()).getNode(nodeName);
        Assert.assertThat(node, Is.is(IsNull.notNullValue()));
        final SLNode node2 = node.getNode(nodeName);
        Assert.assertThat(node2, Is.is(IsNull.notNullValue()));
        final NeedsSyncronizationCollection<SLLink> links = context1
                                                                    .getGraphSession().getLinks(node, node2);
        Assert.assertThat(links.size(), Is.is(IsNot.not(0)));
        final StringArtifact sourceFile = context1
                                                  .getPersistentArtifactManager()
                                                  .findByPath(
                                                              StringArtifact.class,
                                                              "/sources/java/myProject/osl-federation-api/src/main/java/org/openspotlight/federation/loader/ConfigurationManagerProvider.java");
        Assert.assertThat(sourceFile, Is.is(IsNull.notNullValue()));
        Assert.assertThat(sourceFile.getUnwrappedSyntaxInformation(
                                                                   context1.getPersistentArtifactManager()
                                                                                    .getSimplePersist()).size(), Is.is(IsNot.not(0)));

    }

    @After
    public void sleepAndGc() throws Exception {
        Thread.sleep(500);
        System.gc();
    }

}
