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
package org.openspotlight.bundle.language.java.bundle.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openspotlight.bundle.language.java.bundle.JavaGlobalPhase;
import org.openspotlight.bundle.language.java.bundle.JavaLexerAndParserTypesPhase;
import org.openspotlight.bundle.language.java.bundle.JavaParserPublicElementsPhase;
import org.openspotlight.bundle.language.java.bundle.JavaTreePhase;
import org.openspotlight.bundle.context.DefaultExecutionContextFactoryModule;
import org.openspotlight.bundle.context.ExecutionContext;
import org.openspotlight.bundle.context.ExecutionContextFactory;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.federation.log.DetailedLoggerModule;
import org.openspotlight.bundle.processing.DefaultBundleProcessorManager;
import org.openspotlight.bundle.scheduler.GlobalSettingsSupport;
import org.openspotlight.graph.SLConsts;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.Nodeimport org.openspotlight.graph.guice.SLGraphModule;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.storage.StorageSessionimport org.openspotlight.storage.domain.RegularPartitionition;
import org.openspotlight.storage.redis.guice.JRedisFactory;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

public class JavaStringChangesTest {

    private ExecutionContextFactory includedFilesContextFactory;
    private ExecutionContextFactory changedFilesContextFactory;
    private ExecutionContextFactory removedFilesContextFactory;
    private GlobalSettings settings;
    private Group group;

    private final String username = "username";

    private final String password = "password";
    private final JcrConnectionDescriptor descriptor = DefaultJcrDescriptor.TEMP_DESCRIPTOR;

    @Before
    public void setupResources() throws Exception {
        final Repository repo = new Repository();
        repo.setName("name");
        repo.setActive(true);

        Injector injector = Guice.createInjector(new JRedisStorageModule(StStorageSessionlushMode.AUTO,
                ExampleRedisConfig.EXAMPLE.getMappedServerConfig(),
                repositoryPath("repository")),
                new SimplePersistModule(), new DetailedLoggerModule(),
                new DefaultExecutionContextFactoryModule(), new SLGraphModule(DefaultJcrDescriptor.TEMP_DESCRIPTOR));

        injector.getInstance(JRedisFactory.class).getRegularPartitionrPartition.GRAPH).flushall();

        final ArtifactSource includedSource = new ArtifactSource();
        includedSource.setRepository(repo);
        includedSource.setName("classpath");
        includedSource.setInitialLookup("./src/test/resources/stringArtifacts/new_file");
        includedFilesContextFactory = injector.getInstance(ExecutionContextFactory.class);

        final ArtifactSource changedSource = new ArtifactSource();
        changedSource.setRepository(repo);
        changedSource.setName("classpath");
        changedSource.setInitialLookup("./src/test/resources/stringArtifacts/changed_file");
        changedFilesContextFactory = includedFilesContextFactory;

        final ArtifactSource removedSource = new ArtifactSource();
        removedSource.setRepository(repo);
        removedSource.setName("classpath");
        removedSource.setInitialLookup("./src/test/resources/stringArtifacts/removed_file");
        removedFilesContextFactory = includedFilesContextFactory;
        settings = new GlobalSettings();
        settings.setDefaultSleepingIntervalInMilliseconds(1000);
        GlobalSettingsSupport.initializeScheduleMap(settings);
        group = new Group();
        group.setName("sampleGroup");
        group.setRepository(repo);
        repo.getGroups().add(group);
        group.setActive(true);

        final BundleProcessorType commonProcessor = new BundleProcessorType();
        commonProcessor.setActive(true);
        commonProcessor.setGroup(group);
        commonProcessor.setGlobalPhase(JavaGlobalPhase.class);
        commonProcessor.getArtifactPhases().add(JavaLexerAndParserTypesPhase.class);
        commonProcessor.getArtifactPhases().add(JavaParserPublicElementsPhase.class);
        commonProcessor.getArtifactPhases().add(JavaTreePhase.class);
        group.getBundleTypes().add(commonProcessor);

        final BundleSource bundleSource = new BundleSource();
        commonProcessor.getSources().add(bundleSource);
        bundleSource.setBundleProcessorType(commonProcessor);
        bundleSource.setRelative("tests/");
        bundleSource.getIncludeds().add("**/*.java");
        final ExecutionContext ctx = includedFilesContextFactory.createExecutionContext(username, password, descriptor,
                group.getRootRepository());
        ctx.getDefaultConfigurationManager().saveGlobalSettings(settings);
        ctx.getDefaultConfigurationManager().saveRepository(repo);
        ctx.closeResources();
    }

    @Ignore
    @Test
    public void shouldRemoveDeletedInnerClassWhenItIsRemovedFromFile() throws Exception {
        // FIXME write this test!
    }

    @Ignore
    @Test
    public void shouldRemoveDeletedPublicClassWhenItsFileIsRemoved() throws Exception {
        // FIXME write this test!
        DefaultBundleProcessorManager.INSTANCE.executeBundles(username, password, descriptor, includedFilesContextFactory,
                settings, group);
        final ExecutionContext context = includedFilesContextFactory.createExecutionContext(username, password, descriptor,
                group.getRootRepository());
        final SLContext ctx = context.getGraphSession().getContext(SLConsts.DEFAULT_GROUP_CONTEXT);
        final NoNodeupNode = ctx.getRootNode().getNode(group.getUniqueName());
        final NodeNodegeNode = groupNode.getNode("org.openspotlight.test");
        final Node cNodede = packageNode.getNode("ExamplePublicClass");
        Assert.assertThat(classNode, Is.is(IsNull.notNullValue()));

    }
}
