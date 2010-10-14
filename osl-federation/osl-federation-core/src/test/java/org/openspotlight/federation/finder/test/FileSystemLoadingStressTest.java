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
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.Strings.concatPaths;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.bundle.domain.GlobalSettings;
import org.openspotlight.bundle.domain.Group;
import org.openspotlight.bundle.domain.Repository;
import org.openspotlight.common.util.SLCollections;
import org.openspotlight.federation.domain.ArtifactSourceMapping;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.finder.PersistentArtifactManager;
import org.openspotlight.federation.log.DetailedLoggerModule;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.RegularPartitions;
import org.openspotlight.storage.redis.guice.JRedisFactory;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.internal.ImmutableList;

public class FileSystemLoadingStressTest {

	private static final Logger logger = LoggerFactory
			.getLogger(FileSystemLoadingStressTest.class);
	private static ArtifactSource artifactSource;

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

	private static RepositoryData data;
	private static PersistentArtifactManager persistentManager;

	@AfterClass
	public static void closeResources() throws Exception {
		// contextFactory.closeResources();
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
		repository.getArtifactSources().add(artifactSource);
		artifactSource.setRepository(repository);
		artifactSource.setName("lots of files");
		artifactSource.setActive(true);
		artifactSource.setBinary(false);
		// artifactSource.setInitialLookup("/Users/feu/much-data");
		artifactSource.setInitialLookup("./");
		final ArtifactSourceMapping mapping = new ArtifactSourceMapping();
		mapping.setSource(artifactSource);
		artifactSource.getMappings().add(mapping);
		// mapping.setFrom("files");
		mapping.setFrom("src");
		mapping.setTo("OSL");
		artifactSource.getMappings().add(mapping);
		mapping.getIncludeds().add("**/*");

		return new RepositoryData(settings, repository, group, artifactSource);
	}

	@BeforeClass
	public static void setupResources() throws Exception {

		Injector injector = Guice.createInjector(
				new JRedisStorageModule(StorageSession.FlushMode.AUTO,
						ExampleRedisConfig.EXAMPLE.getMappedServerConfig()),
				new SimplePersistModule(), new DetailedLoggerModule());

		injector.getInstance(JRedisFactory.class)
				.getFrom(RegularPartitions.FEDERATION).flushall();
		data = createRepositoryData();
		persistentManager = injector
				.getInstance(PersistentArtifactManager.class);
	}

	@After
	public void closeTestResources() {

	}

	private void reloadArtifacts() {
		throw new UnsupportedOperationException();
	}

	@Test
	public void shouldProcessJarFile() throws Exception {
		logger.debug("about to load all items from its origin");
		reloadArtifacts();
		logger.debug("finished to load all items from its origin");
		logger.debug("about to load item names from persistent storage");
		Iterable<String> list = persistentManager.getInternalMethods()
				.retrieveNames(StringArtifact.class, null);
		logger.debug("finished to load item names from persistent storage");

		assertThat(SLCollections.iterableToList(list).size() > 50, is(true));
		int loadedSize = 0;
		logger.debug("about to load item contents from persistent storage");
		for (String s : list) {
			StringArtifact file = persistentManager.findByPath(
					StringArtifact.class, s);
			assertThat(file, is(notNullValue()));
			List<String> lazyLoadedContent = file.getContent().get(
					persistentManager.getSimplePersist());
			assertThat(lazyLoadedContent, is(notNullValue()));
			assertThat(lazyLoadedContent.equals(getFileContentAsStringList(
					artifactSource, file.getOriginalName())), is(true));

			if (lazyLoadedContent.size() != 0) {
				loadedSize++;
			}
		}
		logger.debug("finished to load item contents from persistent storage");
		assertThat(loadedSize > 50, is(true));

	}

	private List<String> getFileContentAsStringList(
			ArtifactSource artifactSource, String originalName)
			throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(concatPaths(
				artifactSource.getInitialLookup(), originalName)));
		ImmutableList.Builder<String> builder = ImmutableList.builder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			builder.add(line);
		}
		reader.close();
		return builder.build();
	}

}