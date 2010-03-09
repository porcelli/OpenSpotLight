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
package org.openspotlight.federation.scheduler;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Schedulable.SchedulableCommand;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.federation.finder.ArtifactFinder;
import org.openspotlight.federation.finder.ArtifactFinderSupport;
import org.openspotlight.federation.finder.ArtifactFinderWithSaveCapabilitie;
import org.openspotlight.federation.loader.ArtifactLoader;
import org.openspotlight.federation.loader.ArtifactLoaderFactory;
import org.openspotlight.federation.registry.ArtifactTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ArtifactSourceSchedulable.
 */
public class ArtifactSourceSchedulable implements
SchedulableCommand<ArtifactSource> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@SuppressWarnings("unchecked")
	public void execute(final GlobalSettings settigns,
			final ExecutionContext ctx, final ArtifactSource schedulable) {
		if (logger.isDebugEnabled()) {
			logger.debug(" >>>> Executing artifact loadgin from source"
					+ schedulable.toUniqueJobString());
		}
		final ArtifactLoader loader = ArtifactLoaderFactory
		.createNewLoader(settigns);
		final Set<Class<? extends Artifact>> types = ArtifactTypeRegistry.INSTANCE
		.getRegisteredArtifactTypes();

		final Map<Class<? extends Artifact>, Set<Artifact>> newArtifactsByType = new HashMap<Class<? extends Artifact>, Set<Artifact>>();
		final Map<Class<? extends Artifact>, Set<Artifact>> existentArtifactsByType = new HashMap<Class<? extends Artifact>, Set<Artifact>>();
		for (final Class<? extends Artifact> type : types) {
			newArtifactsByType.put(type, new HashSet<Artifact>());
		}

		final Iterable<Artifact> loadedArtifacts = loader
		.loadArtifactsFromSource(schedulable);

		for (final Artifact artifact : loadedArtifacts) {
			for (final Class<? extends Artifact> type : types) {
				if (type.isAssignableFrom(artifact.getClass())) {
					newArtifactsByType.get(type).add(artifact);
					logger.info("adding artifact "
							+ artifact.getArtifactCompleteName()
							+ " on type map " + type);
					continue;
				}
			}
		}
		for (final Class<? extends Artifact> type : types) {
			final ArtifactFinder<Artifact> finder = (ArtifactFinder<Artifact>) ctx
			.getArtifactFinder(type);
			Set<Artifact> existentArtifacts;
			if (finder != null) {
				existentArtifacts = finder.listByPath(schedulable
						.getInitialLookup());
				existentArtifactsByType.put(type, existentArtifacts);
			} else {
				existentArtifactsByType.put(type, Collections
						.<Artifact> emptySet());
			}
		}
		for (final Class<? extends Artifact> type : types) {
			final ArtifactFinder<Artifact> finder = (ArtifactFinder<Artifact>) ctx
			.getArtifactFinder(type);
			if (finder instanceof ArtifactFinderWithSaveCapabilitie<?>) {
				final ArtifactFinderWithSaveCapabilitie<Artifact> finderWithSaveCapabilitie = (ArtifactFinderWithSaveCapabilitie<Artifact>) finder;
				final Set<Artifact> existentArtifacts = existentArtifactsByType
				.get(type);
				final Set<Artifact> newArtifacts = newArtifactsByType.get(type);
				final Set<Artifact> withDifferences = ArtifactFinderSupport
				.applyDifferenceOnExistents(existentArtifacts,
								newArtifacts, finderWithSaveCapabilitie
										.finderSession());
				// FIXME this could be parallel
				for (final Artifact toSave : withDifferences) {

					finderWithSaveCapabilitie.addTransientArtifact(toSave);
					finderWithSaveCapabilitie.save();
					logger.info("saving transient artifact " + toSave);

				}
			}
		}
	}

	public String getRepositoryNameBeforeExecution(
			final ArtifactSource schedulable) {
		return schedulable.getRepository().getName();
	}

}
