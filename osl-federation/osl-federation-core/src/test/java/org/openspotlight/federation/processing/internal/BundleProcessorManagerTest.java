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
import java.util.Set;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.common.util.Collections;
import org.openspotlight.common.util.Files;
import org.openspotlight.federation.context.DefaultExecutionContextFactory;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.context.ExecutionContextFactory;
import org.openspotlight.federation.context.SingleGraphSessionExecutionContextFactory;
import org.openspotlight.federation.context.TestExecutionContextFactory;
import org.openspotlight.federation.context.TestExecutionContextFactory.ArtifactFinderType;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.ArtifactFinderRegistry;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.ArtifactSourceMapping;
import org.openspotlight.federation.domain.BundleProcessorType;
import org.openspotlight.federation.domain.BundleSource;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.domain.LastProcessStatus;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.domain.StreamArtifact;
import org.openspotlight.federation.finder.ArtifactFinderBySourceProvider;
import org.openspotlight.federation.finder.ArtifactFinderWithSaveCapabilitie;
import org.openspotlight.federation.finder.FileSystemArtifactBySourceProvider;
import org.openspotlight.federation.loader.ArtifactLoader;
import org.openspotlight.federation.loader.ArtifactLoaderFactory;
import org.openspotlight.federation.loader.ConfigurationManager;
import org.openspotlight.federation.loader.XmlConfigurationManagerFactory;
import org.openspotlight.federation.processing.BundleProcessorManagerImpl;
import org.openspotlight.federation.scheduler.GlobalSettingsSupport;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;

public class BundleProcessorManagerTest {

	public static class SampleArtifactRegistry implements
			ArtifactFinderRegistry {

		public Set<ArtifactFinderBySourceProvider> getRegisteredArtifactFinderProviders() {
			return Collections
					.<ArtifactFinderBySourceProvider> setOf(new FileSystemArtifactBySourceProvider());
		}

	}

	private static final int PARALLEL_THREADS = 8;

	@BeforeClass
	public static void cleanupOldEntries() throws Exception {
		JcrConnectionProvider.createFromData(
				DefaultJcrDescriptor.TEMP_DESCRIPTOR)
				.closeRepositoryAndCleanResources();
	}

	@Test
	public void shouldProcessMappedArtifactsUsingJcrStreamArtifacts()
			throws Exception {
		ExampleBundleProcessor.allStatus.clear();

		final ArtifactSource source = new ArtifactSource();
		final String initialRawPath = Files
				.getNormalizedFileName(new File(".."));
		final String initial = initialRawPath.substring(0, initialRawPath
				.lastIndexOf('/'));
		final String finalStr = initialRawPath.substring(initial.length());
		final ArtifactSourceMapping mapping = new ArtifactSourceMapping();
		mapping.setFrom(finalStr);
		mapping.setTo("/sources/java/myProject");
		mapping.setIncludeds(new HashSet<String>());
		mapping.setExcludeds(new HashSet<String>());
		mapping.getIncludeds().add("*.java");
		source.setMappings(new HashSet<ArtifactSourceMapping>());
		source.getMappings().add(mapping);
		source.setActive(true);
		source.setInitialLookup(initial);
		source.setName("sourceName");

		final GlobalSettings settings = new GlobalSettings();
		settings.setArtifactFinderRegistryClass(SampleArtifactRegistry.class);

		settings.setDefaultSleepingIntervalInMilliseconds(1000);
		settings.setNumberOfParallelThreads(PARALLEL_THREADS);
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
		bundleType.setType(ExampleBundleProcessor.class);
		group.getBundleTypes().add(bundleType);
		final BundleSource bundleSource = new BundleSource();
		bundleType.getSources().add(bundleSource);
		bundleSource.setBundleProcessorType(bundleType);
		bundleSource.setRelative("/sources/java/myProject");
		bundleSource.getIncludeds().add("**/*.java");

		final ArtifactLoader loader = ArtifactLoaderFactory
				.createNewLoader(settings);

		final Iterable<Artifact> artifacts = loader
				.loadArtifactsFromSource(source);

		final ExecutionContextFactory contextFactory = DefaultExecutionContextFactory
				.createFactory();
		final ExecutionContext context = contextFactory.createExecutionContext(
				"username", "password", DefaultJcrDescriptor.TEMP_DESCRIPTOR,
				repository.getName());
		final ArtifactFinderWithSaveCapabilitie<StreamArtifact> finder = (ArtifactFinderWithSaveCapabilitie<StreamArtifact>) context
				.getArtifactFinder(StreamArtifact.class);

		for (final Artifact a : artifacts) {
			finder.addTransientArtifact((StreamArtifact) a);
			finder.save();
		}
		contextFactory.closeResources();

		BundleProcessorManagerImpl.INSTANCE.executeBundles("username",
				"password", DefaultJcrDescriptor.TEMP_DESCRIPTOR,
				SingleGraphSessionExecutionContextFactory.createFactory(),
				settings, group);
		Assert.assertThat(ExampleBundleProcessor.allStatus
				.contains(LastProcessStatus.ERROR), Is.is(false));
		Assert.assertThat(ExampleBundleProcessor.allStatus
				.contains(LastProcessStatus.EXCEPTION_DURRING_PROCESS), Is
				.is(false));
		final ConfigurationManager xmlManager = XmlConfigurationManagerFactory
				.loadMutableFromFile("target/BundleProcessorManagerTest/exampleConfigurationFile.xml");
		GlobalSettingsSupport.initializeScheduleMap(settings);
		xmlManager.saveGlobalSettings(settings);
		xmlManager.saveRepository(repository);

	}

	@Test
	public void shouldProcessMappedArtifactsUsingLocalFiles() throws Exception {
		ExampleBundleProcessor.allStatus.clear();

		final ArtifactSource source = new ArtifactSource();
		source.setName("classpath");
		source.setInitialLookup("../../../OpenSpotLight");

		final GlobalSettings settings = new GlobalSettings();
		settings.setDefaultSleepingIntervalInMilliseconds(1000);
		settings.setNumberOfParallelThreads(PARALLEL_THREADS);
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
		bundleType.setType(ExampleBundleProcessor.class);
		group.getBundleTypes().add(bundleType);
		final BundleSource bundleSource = new BundleSource();
		bundleType.getSources().add(bundleSource);
		bundleSource.setBundleProcessorType(bundleType);
		bundleSource.setRelative("/osl-federation");
		bundleSource.getIncludeds().add("**/*.java");

		BundleProcessorManagerImpl.INSTANCE
				.executeBundles("username", "password",
						DefaultJcrDescriptor.TEMP_DESCRIPTOR,
						TestExecutionContextFactory.createFactory(
								ArtifactFinderType.FILESYSTEM, source),
						settings, group);
		Assert.assertThat(ExampleBundleProcessor.allStatus
				.contains(LastProcessStatus.ERROR), Is.is(false));
		Assert.assertThat(ExampleBundleProcessor.allStatus
				.contains(LastProcessStatus.EXCEPTION_DURRING_PROCESS), Is
				.is(false));
	}

}
