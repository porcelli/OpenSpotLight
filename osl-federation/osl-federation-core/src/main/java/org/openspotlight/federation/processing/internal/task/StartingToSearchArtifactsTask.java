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
package org.openspotlight.federation.processing.internal.task;

import static org.openspotlight.common.concurrent.Priority.createPriority;
import static org.openspotlight.common.util.PatternMatcher.filterNamesByPattern;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openspotlight.common.concurrent.Priority;
import org.openspotlight.common.task.exception.RunnableWithException;
import org.openspotlight.common.util.PatternMatcher.FilterResult;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.BundleProcessorType;
import org.openspotlight.federation.domain.BundleSource;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.LastProcessStatus;
import org.openspotlight.federation.finder.ArtifactFinder;
import org.openspotlight.federation.processing.BundleProcessorArtifactPhase;
import org.openspotlight.federation.processing.BundleProcessorGlobalPhase;
import org.openspotlight.federation.processing.SaveBehavior;
import org.openspotlight.federation.processing.internal.RunnableWithBundleContext;
import org.openspotlight.federation.processing.internal.domain.ArtifactChangesImpl;
import org.openspotlight.federation.processing.internal.domain.ArtifactsToBeProcessedImpl;
import org.openspotlight.federation.processing.internal.domain.CurrentProcessorContextImpl;
import org.openspotlight.log.DetailedLogger.LogEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartingToSearchArtifactsTask<T extends Artifact> extends
		RunnableWithBundleContext {
	private class SecondPhaseDto {

		public final T artifact;
		public final BundleProcessorArtifactPhase<T> bundleProcessor;
		public final Class<T> artifactType;
		public final SaveBehavior saveBehavior;
		public final int subpriority;

		public SecondPhaseDto(final T artifact,
				final BundleProcessorArtifactPhase<T> bundleProcessor,
				final Class<T> artifactType, final SaveBehavior saveBehavior,
				final int subpriority) {
			super();
			this.artifact = artifact;
			this.bundleProcessor = bundleProcessor;
			this.artifactType = artifactType;
			this.saveBehavior = saveBehavior;
			this.subpriority = subpriority;
		}
	}

	private final Class<T> artifactType;

	private final ArtifactChangesImpl<T> changes;

	private final ArtifactsToBeProcessedImpl<T> toBeReturned;

	private final BundleProcessorType bundleProcessorType;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@SuppressWarnings("unchecked")
	public StartingToSearchArtifactsTask(
			final CurrentProcessorContextImpl currentContext,
			final Class<? extends Artifact> artifactType,
			final BundleProcessorType bundleProcessorType) {
		this.artifactType = (Class<T>) artifactType;
		this.bundleProcessorType = bundleProcessorType;
		this.toBeReturned = new ArtifactsToBeProcessedImpl<T>();
		this.changes = new ArtifactChangesImpl<T>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@SuppressWarnings("unchecked")
	public void doTask() throws Exception {
		try {
			final BundleProcessorGlobalPhase<?> rawBundleProcessor = this.bundleProcessorType
					.getGlobalPhase().newInstance();
			boolean hasAnyType = false;
			for (final Class<?> artifactType : rawBundleProcessor
					.getArtifactTypes()) {
				if (artifactType.isAssignableFrom(this.artifactType)) {
					hasAnyType = true;
					break;
				}
			}
			if (!hasAnyType) {
				return;
			}
			final Set<T> changedArtifacts = new HashSet<T>();
			final Set<T> excludedArtifacts = new HashSet<T>();
			final Set<T> includedArtifacts = new HashSet<T>();
			final Set<T> notChangedArtifacts = new HashSet<T>();
			final BundleProcessorGlobalPhase<T> bundleProcessor = (BundleProcessorGlobalPhase<T>) rawBundleProcessor;

			final ArtifactFinder<T> finder = getBundleContext()
					.getArtifactFinder(this.artifactType);
			for (final BundleSource src : this.bundleProcessorType.getSources()) {

				final Set<String> rawNames = finder
						.retrieveAllArtifactNames(src.getRelative());
				final FilterResult newNames = filterNamesByPattern(rawNames,
						src.getIncludeds(), src.getExcludeds(), false);
				for (final String name : newNames.getIncludedNames()) {
					final T savedArtifact = finder.findByPath(name);
					switch (savedArtifact.getChangeType()) {
					case CHANGED:
						changedArtifacts.add(savedArtifact);
						break;
					case EXCLUDED:
						excludedArtifacts.add(savedArtifact);
						break;
					case INCLUDED:
						includedArtifacts.add(savedArtifact);
						break;
					case NOT_CHANGED:
						notChangedArtifacts.add(savedArtifact);
						break;
					}
				}

			}

			this.changes.setChangedArtifacts(changedArtifacts);
			this.changes.setExcludedArtifacts(excludedArtifacts);
			this.changes.setIncludedArtifacts(includedArtifacts);
			this.changes.setNotChangedArtifacts(notChangedArtifacts);

			final Set<T> artifactsAlreadyProcessed = new HashSet<T>();
			this.toBeReturned
					.setArtifactsAlreadyProcessed(artifactsAlreadyProcessed);
			final Set<T> artifactsToBeProcessed = new HashSet<T>();
			artifactsToBeProcessed.addAll(changedArtifacts);
			artifactsToBeProcessed.addAll(includedArtifacts);
			this.toBeReturned.setArtifactsToBeProcessed(artifactsToBeProcessed);
			final Date lastProcessedDate = new Date();
			try {
				bundleProcessor.selectArtifactsToBeProcessed(
						this.getCurrentContext(), getBundleContext(), this.changes,
						this.toBeReturned);
				for (final T artifactAlreadyProcessed : this.toBeReturned
						.getArtifactsAlreadyProcessed()) {
					artifactAlreadyProcessed
							.setLastProcessedDate(lastProcessedDate);
					artifactAlreadyProcessed
							.setLastProcessStatus(LastProcessStatus.PROCESSED);
				}
				final List<BundleProcessorArtifactPhase<T>> artifactPhases = new ArrayList<BundleProcessorArtifactPhase<T>>();
				int subpriority = 0;
				final SaveBehavior behavior = bundleProcessor.getSaveBehavior();
				if (bundleProcessor instanceof BundleProcessorArtifactPhase) {
					artifactPhases
							.add((BundleProcessorArtifactPhase<T>) bundleProcessor);
				}
				for (final Class<? extends BundleProcessorArtifactPhase<?>> bundleProcessorArtifactPhaseTypes : bundleProcessorType
						.getArtifactPhases()) {
					final BundleProcessorArtifactPhase<?> artifactPhase = bundleProcessorArtifactPhaseTypes
							.newInstance();
					if (artifactPhase.getArtifactType().isAssignableFrom(
							artifactType)) {
						logger
								.info(" selecting processor artifact phase for bundle type "
										+ artifactPhase.getClass()
												.getSimpleName()
										+ " for artifact type "
										+ this.artifactType.getSimpleName()
										+ " due to its acceptable type "
										+ artifactPhase.getArtifactType()
												.getSimpleName());

						artifactPhases
								.add((BundleProcessorArtifactPhase<T>) artifactPhase);
					} else {
						logger
								.info(" ignoring processor artifact phase for bundle type "
										+ artifactPhase.getClass()
												.getSimpleName()
										+ " for artifact type "
										+ this.artifactType.getSimpleName()
										+ " due to its acceptable type "
										+ artifactPhase.getArtifactType()
												.getSimpleName());

					}

				}
				final List<SecondPhaseDto> phaseTwoData = new ArrayList<SecondPhaseDto>();
				for (final BundleProcessorArtifactPhase<T> artifactPhase : artifactPhases) {
					subpriority++;
					for (final T artifactToProcess : this.toBeReturned
							.getArtifactsToBeProcessed()) {
						logger
								.info(" Adding processor artifact phase for bundle type "
										+ artifactPhase.getClass()
												.getSimpleName()
										+ " and artifact type "
										+ this.artifactType.getSimpleName());

						phaseTwoData.add(new SecondPhaseDto(
								artifactToProcess, artifactPhase, artifactType,
								behavior, subpriority));
					}
				}

				final int lastSubPriority = 1;
				for (final SecondPhaseDto dto : phaseTwoData) {
					queue.add(new _2_EachArtifactTask<T>(dto.currentCtx,
							dto.artifact, dto.bundleProcessor,
							dto.artifactType, dto.saveBehavior,
							dto.subpriority));
				}
				queue.add(new _3_EndingToProcessArtifactsTask<T>(
						this.changes, bundleProcessor, repository.getName()));

			} catch (final Exception e) {
				for (final T artifactWithError : this.toBeReturned
						.getArtifactsToBeProcessed()) {
					artifactWithError.setLastProcessedDate(lastProcessedDate);
					artifactWithError
							.setLastProcessStatus(LastProcessStatus.EXCEPTION_DURRING_PROCESS);
					context.getLogger()
							.log(
									context.getUser(),
									LogEventType.ERROR,
									"Error on trying to process artifact on starting for bundle Processor "
											+ bundleProcessor.getClass()
													.getSimpleName(),
									artifactWithError);
				}
				throw e;
			}
		}

	}

	public CurrentProcessorContextImpl getCurrentContext() {
		return currentContext;
	}

	public Priority getPriority() {
		return priority;
	}

	public String getRepositoryName() {
		return repository.getName();
	}

	public boolean isAwaitingParent(final long quantity, final TimeUnit unit)
			throws InterruptedException {
		return false;
	}

	public void setBundleContext(final ExecutionContext context) {
		this.context = context;

	}
}
