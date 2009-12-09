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
package org.openspotlight.federation.loader;

import static org.openspotlight.common.util.PatternMatcher.filterNamesByPattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openspotlight.common.Pair;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.Strings;
import org.openspotlight.common.util.PatternMatcher.FilterResult;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.ArtifactFinderRegistry;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.ArtifactSourceMapping;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.PathElement;
import org.openspotlight.federation.finder.ArtifactFinder;
import org.openspotlight.federation.finder.ArtifactFinderBySourceProvider;
import org.openspotlight.federation.registry.ArtifactTypeRegistry;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating ArtifactLoader objects.
 */
public class ArtifactLoaderFactory {

	/**
	 * The Class ArtifactLoaderImpl.
	 */
	private static class ArtifactLoaderImpl implements ArtifactLoader {

		/**
		 * The Class SourcesToProcessItems.
		 */
		private static class SourcesToProcessItems {

			/** The artifact finder. */
			final ArtifactFinder<?> artifactFinder;

			/** The artifact name. */
			final String artifactName;

			/** The mapping. */
			final ArtifactSourceMapping mapping;

			/**
			 * Instantiates a new sources to process items.
			 * 
			 * @param artifactFinder
			 *            the artifact finder
			 * @param artifactSource
			 *            the artifact source
			 * @param artifactName
			 *            the artifact name
			 * @param mapping
			 *            the mapping
			 */
			public SourcesToProcessItems(
					final ArtifactFinder<?> artifactFinder,
					final String artifactName,
					final ArtifactSourceMapping mapping) {
				this.artifactFinder = artifactFinder;
				this.artifactName = artifactName;
				this.mapping = mapping;
			}

		}

		/** The configuration. */
		private final GlobalSettings configuration;

		/** The artifact providers. */
		private final Set<ArtifactFinderBySourceProvider> artifactProviders;

		/** The artifact types. */
		private Set<Class<? extends Artifact>> artifactTypes;

		/** The sleep time. */
		private final long sleepTime;

		/** The executor. */
		private ExecutorService executor;

		/**
		 * Instantiates a new artifact loader impl.
		 * 
		 * @param configuration
		 *            the configuration
		 * @param artifactProviders
		 *            the artifact providers
		 */
		public ArtifactLoaderImpl(final GlobalSettings configuration) {
			try {
				this.configuration = configuration;
				final ArtifactFinderRegistry registry = this.configuration
						.getArtifactFinderRegistryClass().newInstance();
				artifactProviders = registry
						.getRegisteredArtifactFinderProviders();
				Assertions.checkCondition("artifactProvidersNotEmpty",
						artifactProviders.size() > 0);
				sleepTime = configuration
						.getDefaultSleepingIntervalInMilliseconds();
				Assertions.checkCondition("sleepTimePositive", sleepTime > 0);
			} catch (final Exception e) {
				throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
			}

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.openspotlight.federation.loader.ArtifactLoader#closeResources()
		 */
		public void closeResources() {
			try {
				executor.shutdown();
			} catch (final Exception e) {
				Exceptions.catchAndLog(e);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seeorg.openspotlight.federation.loader.ArtifactLoader#
		 * loadArtifactsFromSource
		 * (org.openspotlight.federation.domain.ArtifactSource[])
		 */
		public Iterable<Artifact> loadArtifactsFromSource(
				final ArtifactSource... sources) {
			final Queue<Pair<ArtifactFinder<?>, ArtifactSource>> sourcesToLoad = new ConcurrentLinkedQueue<Pair<ArtifactFinder<?>, ArtifactSource>>();
			final Queue<ArtifactLoaderImpl.SourcesToProcessItems> sourcesToProcess = new ConcurrentLinkedQueue<ArtifactLoaderImpl.SourcesToProcessItems>();
			final Queue<Artifact> loadedArtifacts = new ConcurrentLinkedQueue<Artifact>();

			addingSources: for (final ArtifactSource source : sources) {
				if (!source.isActive()) {
					continue addingSources;
				}
				for (final ArtifactFinderBySourceProvider provider : artifactProviders) {
					for (final Class<? extends Artifact> type : artifactTypes) {
						final ArtifactFinder<? extends Artifact> artifactFinder = provider
								.getForType(type, source);
						sourcesToLoad
								.add(new Pair<ArtifactFinder<?>, ArtifactSource>(
										artifactFinder, source));

					}
				}
			}
			if (sourcesToLoad.size() == 0) {
				return Collections.emptySet();
			}
			for (final Pair<ArtifactFinder<?>, ArtifactSource> pair : new ArrayList<Pair<ArtifactFinder<?>, ArtifactSource>>(
					sourcesToLoad)) {
				executor.execute(new Runnable() {

					public void run() {
						try {
							for (final ArtifactSourceMapping mapping : pair
									.getK2().getMappings()) {
								final Set<String> rawNames = pair.getK1()
										.retrieveAllArtifactNames(
												mapping.getFrom());
								final FilterResult newNames = filterNamesByPattern(
										rawNames, mapping.getIncludeds(),
										mapping.getExcludeds(), false);

								for (final String name : newNames
										.getIncludedNames()) {

									sourcesToProcess
											.add(new SourcesToProcessItems(pair
													.getK1(), name, mapping));
								}
							}
						} catch (final Exception e) {
							Exceptions.catchAndLog(e);
						} finally {
							sourcesToLoad.remove(pair);
						}
					}
				});
			}
			while (sourcesToLoad.size() != 0) {
				try {
					Thread.sleep(sleepTime);
				} catch (final InterruptedException e) {

				}
			}
			if (sourcesToProcess.size() == 0) {
				return Collections.emptySet();
			}
			for (final ArtifactLoaderImpl.SourcesToProcessItems sourceToProcess : new ArrayList<ArtifactLoaderImpl.SourcesToProcessItems>(
					sourcesToProcess)) {
				executor.execute(new Runnable() {

					public void run() {
						try {

							final Artifact loaded = sourceToProcess.artifactFinder
									.findByPath(sourceToProcess.artifactName);
							String currentPathString = loaded.getParent()
									.getCompletePath();
							if (!currentPathString.startsWith("/")) {
								currentPathString = "/" + currentPathString;
							}
							String toRemove = sourceToProcess.mapping.getFrom();
							if (!toRemove.startsWith("/")) {
								toRemove = "/" + toRemove;
							}
							if (currentPathString.startsWith(toRemove)) {
								currentPathString = Strings
										.removeBegginingFrom(toRemove,
												currentPathString);
							}
							final String newPathString = sourceToProcess.mapping
									.getTo()
									+ currentPathString;
							final PathElement newPath = PathElement
									.createFromPathString(newPathString);
							loaded.setParent(newPath);
							loadedArtifacts.add(loaded);

						} catch (final Exception e) {
							Exceptions.catchAndLog(e);
						} finally {
							sourcesToProcess.remove(sourceToProcess);
						}

					}
				});
			}
			while (sourcesToProcess.size() != 0) {
				try {
					Thread.sleep(sleepTime);
				} catch (final InterruptedException e) {

				}
			}
			return new ArrayList<Artifact>(loadedArtifacts);
		}

		/**
		 * Setup.
		 */
		public synchronized void setup() {
			executor = Executors.newFixedThreadPool(configuration
					.getNumberOfParallelThreads());
			artifactTypes = ArtifactTypeRegistry.INSTANCE
					.getRegisteredArtifactTypes();
		}
	}

	/**
	 * Creates a new ArtifactLoader object.
	 * 
	 * @param configuration
	 *            the configuration
	 * @param artifactSourceProviders
	 *            the artifact source providers
	 * @return the artifact loader
	 */
	public static ArtifactLoader createNewLoader(
			final GlobalSettings configuration) {
		Assertions.checkNotNull("configuration", configuration);
		Assertions.checkNotNull("configurationWithRegistryClass", configuration
				.getArtifactFinderRegistryClass());
		final ArtifactLoaderFactory.ArtifactLoaderImpl loader = new ArtifactLoaderImpl(
				configuration);
		loader.setup();
		return loader;
	}

	/**
	 * Instantiates a new artifact loader factory.
	 */
	private ArtifactLoaderFactory() {
	}

}
