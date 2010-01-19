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

import static org.openspotlight.common.util.Files.delete;

import java.util.Set;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.bundle.language.java.bundle.JavaGlobalPhase;
import org.openspotlight.bundle.language.java.bundle.JavaLexerAndParserTypesPhase;
import org.openspotlight.bundle.language.java.bundle.JavaParserPublicElementsPhase;
import org.openspotlight.bundle.language.java.bundle.JavaTreePhase;
import org.openspotlight.common.util.Collections;
import org.openspotlight.federation.context.DefaultExecutionContextFactory;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.context.ExecutionContextFactory;
import org.openspotlight.federation.domain.ArtifactFinderRegistry;
import org.openspotlight.federation.domain.ArtifactSourceMapping;
import org.openspotlight.federation.domain.BundleProcessorType;
import org.openspotlight.federation.domain.BundleSource;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.federation.domain.artifact.LastProcessStatus;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.finder.ArtifactFinderBySourceProvider;
import org.openspotlight.federation.finder.FileSystemArtifactBySourceProvider;
import org.openspotlight.federation.scheduler.DefaultScheduler;
import org.openspotlight.federation.scheduler.GlobalSettingsSupport;
import org.openspotlight.graph.SLConsts;
import org.openspotlight.graph.SLNode;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;

public class JavaStringArtifactProcessingTest {

	private static class RepositoryData {
		public final GlobalSettings settings;
		public final Repository repository;
		public final Group group;
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

	public static class SampleJavaArtifactRegistry implements
			ArtifactFinderRegistry {

		public Set<ArtifactFinderBySourceProvider> getRegisteredArtifactFinderProviders() {
			return Collections
					.<ArtifactFinderBySourceProvider> setOf(new FileSystemArtifactBySourceProvider());
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
		settings.setNumberOfParallelThreads(1);
		settings
				.setArtifactFinderRegistryClass(SampleJavaArtifactRegistry.class);
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
		artifactSource.setName("java files");
		artifactSource.setActive(true);
		artifactSource.setInitialLookup("src/test/");

		final ArtifactSourceMapping mapping = new ArtifactSourceMapping();
		mapping.setSource(artifactSource);
		artifactSource.getMappings().add(mapping);
		mapping.setFrom("java");
		mapping.setTo("tests");
		artifactSource.getMappings().add(mapping);
		mapping.getIncludeds().add("*.java");
		final BundleProcessorType commonProcessor = new BundleProcessorType();
		commonProcessor.setActive(true);
		commonProcessor.setGroup(group);
		commonProcessor.setGlobalPhase(JavaGlobalPhase.class);
		commonProcessor.getArtifactPhases().add(
				JavaLexerAndParserTypesPhase.class);
		commonProcessor.getArtifactPhases().add(
				JavaParserPublicElementsPhase.class);
		commonProcessor.getArtifactPhases().add(JavaTreePhase.class);
		group.getBundleTypes().add(commonProcessor);

		final BundleSource bundleSource = new BundleSource();
		commonProcessor.getSources().add(bundleSource);
		bundleSource.setBundleProcessorType(commonProcessor);
		bundleSource.setRelative("/tests/");
		bundleSource.getIncludeds().add("**/*.java");

		return new RepositoryData(settings, repository, group, artifactSource);
	}

	public static void main(final String... args) throws Exception {
		final JavaStringArtifactProcessingTest test = new JavaStringArtifactProcessingTest();
		setupResources();
		test.shouldProcessSourceFile();
		test.closeTestResources();
	}

	@BeforeClass
	public static void setupResources() throws Exception {
		delete("./target/test-data/DbTableArtifactBundleProcessorTest"); //$NON-NLS-1$

		JcrConnectionProvider.createFromData(
				DefaultJcrDescriptor.TEMP_DESCRIPTOR)
				.closeRepositoryAndCleanResources();

		data = createRepositoryData();

		contextFactory = DefaultExecutionContextFactory.createFactory();

		final ExecutionContext context = contextFactory.createExecutionContext(
				"username", "password", DefaultJcrDescriptor.TEMP_DESCRIPTOR,
				data.repository.getName());

		context.getDefaultConfigurationManager().saveGlobalSettings(
				data.settings);
		context.getDefaultConfigurationManager()
				.saveRepository(data.repository);
		context.closeResources();

		scheduler = DefaultScheduler.INSTANCE;
		scheduler.initializeSettings(contextFactory, "user", "password",
				DefaultJcrDescriptor.TEMP_DESCRIPTOR);
		scheduler
				.refreshJobs(data.settings, Collections.setOf(data.repository));
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
	public void shouldProcessSourceFile() throws Exception {
		reloadArtifactsAndCallBundleProcessor();

		final ExecutionContext context = contextFactory.createExecutionContext(
				"", "", DefaultJcrDescriptor.TEMP_DESCRIPTOR, data.repository
						.getName());
		final StringArtifact artifact = context
				.getArtifactFinder(StringArtifact.class)
				.findByPath(
						"/tests/org/openspotlight/bundle/language/java/ExampleGraphImport.java");
		Assert.assertThat(artifact.getLastProcessStatus(), Is
				.is(LastProcessStatus.PROCESSED));
		Assert.assertThat(artifact.getSyntaxInformationSet().size(), Is
				.is(IsNot.not(0)));
		final SLNode groupNode = context.getGraphSession().getContext(
				SLConsts.DEFAULT_GROUP_CONTEXT).getRootNode().getNode(
				data.group.getUniqueName());
		final SLNode packageNode = groupNode.getNode(getClass().getPackage()
				.getName());
		final SLNode classNode = packageNode
				.getNode(getClass().getSimpleName());
		final SLNode innerClassNode = classNode.getNode(data.getClass()
				.getSimpleName());
		Assert.assertThat(innerClassNode, Is.is(IsNull.notNullValue()));
	}

}
