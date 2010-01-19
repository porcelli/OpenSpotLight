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

import static org.openspotlight.common.util.PatternMatcher.filterNamesByPattern;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.task.Task;
import org.openspotlight.common.task.TaskGroup;
import org.openspotlight.common.util.Exceptions;
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
import org.openspotlight.graph.SLConsts;
import org.openspotlight.graph.SLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartingToSearchArtifactsTask<T extends Artifact> extends
		RunnableWithBundleContext {
	private final Class<T> artifactType;

	private final CurrentProcessorContextImpl currentContext;

	private final BundleProcessorType bundleProcessorType;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Repository repository;
	private final TaskGroup currentGroup;

	@SuppressWarnings("unchecked")
	public StartingToSearchArtifactsTask(
			final Class<? extends Artifact> artifactType,
			final CurrentProcessorContextImpl currentContext,
			final BundleProcessorType bundleProcessorType,
			final Repository repository, final TaskGroup currentGroup) {
		super(repository.getName());
		this.artifactType = (Class<T>) artifactType;
		this.currentContext = currentContext;
		this.bundleProcessorType = bundleProcessorType;
		this.repository = repository;
		this.currentGroup = currentGroup;
	}

	@SuppressWarnings("unchecked")
	public void doIt() throws Exception {
		final BundleProcessorGlobalPhase<?> rawBundleProcessor = bundleProcessorType
				.getGlobalPhase().newInstance();
		boolean hasAnyType = false;
		for (final Class<?> artifactType : rawBundleProcessor
				.getArtifactTypes()) {
			if (artifactType.isAssignableFrom(artifactType)) {
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
		if (!getBundleContext().artifactFinderSupportsThisType(artifactType)) {
			logger.info(" ignoring artifacts of type "
					+ artifactType.getSimpleName()
					+ " due to it's unsupported execution context of type "
					+ getBundleContext().getClass().getSimpleName());
			return;
		}
		final ArtifactFinder<T> finder = getBundleContext().getArtifactFinder(
				artifactType);
		for (final BundleSource src : bundleProcessorType.getSources()) {

			final Set<String> rawNames = finder.retrieveAllArtifactNames(src
					.getRelative());
			final FilterResult newNames = filterNamesByPattern(rawNames, src
					.getIncludeds(), src.getExcludeds(), false);
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
		final ArtifactChangesImpl<T> changes = new ArtifactChangesImpl<T>();
		final ArtifactsToBeProcessedImpl<T> toBeReturned = new ArtifactsToBeProcessedImpl<T>();

		changes.setChangedArtifacts(changedArtifacts);
		changes.setExcludedArtifacts(excludedArtifacts);
		changes.setIncludedArtifacts(includedArtifacts);
		changes.setNotChangedArtifacts(notChangedArtifacts);

		final Set<T> artifactsAlreadyProcessed = new HashSet<T>();
		toBeReturned.setArtifactsAlreadyProcessed(artifactsAlreadyProcessed);
		final Set<T> artifactsToBeProcessed = new HashSet<T>();
		artifactsToBeProcessed.addAll(changedArtifacts);
		artifactsToBeProcessed.addAll(includedArtifacts);
		toBeReturned.setArtifactsToBeProcessed(artifactsToBeProcessed);
		final Date lastProcessedDate = new Date();
		try {
			bundleProcessor.selectArtifactsToBeProcessed(currentContext,
					getBundleContext(), changes, toBeReturned);
			for (final T artifactAlreadyProcessed : toBeReturned
					.getArtifactsAlreadyProcessed()) {
				artifactAlreadyProcessed
						.setLastProcessedDate(lastProcessedDate);
				artifactAlreadyProcessed
						.setLastProcessStatus(LastProcessStatus.PROCESSED);

			}
			final List<BundleProcessorArtifactPhase<T>> artifactPhases = new ArrayList<BundleProcessorArtifactPhase<T>>();
			final SaveBehavior behavior = bundleProcessor.getSaveBehavior();
			if (bundleProcessor instanceof BundleProcessorArtifactPhase<?>) {
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
									+ artifactPhase.getClass().getSimpleName()
									+ " for artifact type "
									+ artifactType.getSimpleName()
									+ " due to its acceptable type "
									+ artifactPhase.getArtifactType()
											.getSimpleName());

					artifactPhases
							.add((BundleProcessorArtifactPhase<T>) artifactPhase);
				} else {
					logger
							.info(" ignoring processor artifact phase for bundle type "
									+ artifactPhase.getClass().getSimpleName()
									+ " for artifact type "
									+ artifactType.getSimpleName()
									+ " due to its acceptable type "
									+ artifactPhase.getArtifactType()
											.getSimpleName());

				}

			}
			final List<Task> parentTasks = new LinkedList<Task>();
			final List<Task> allParentTasks = new LinkedList<Task>();
			boolean first = true;
			for (final BundleProcessorArtifactPhase<T> artifactPhase : artifactPhases) {
				final List<Task> thisPhaseTasks = new LinkedList<Task>();
				for (final T artifactToProcess : toBeReturned
						.getArtifactsToBeProcessed()) {

					final CurrentProcessorContextImpl taskCtx = new CurrentProcessorContextImpl();
					taskCtx.setBundleProperties(bundleProcessorType
							.getBundleProperties());
					taskCtx.setCurrentGroup(currentContext.getCurrentGroup());
					taskCtx.setCurrentRepository(currentContext
							.getCurrentRepository());
					logger
							.info(" Adding processor artifact phase for bundle type "
									+ artifactPhase.getClass().getSimpleName()
									+ " and artifact type "
									+ artifactType.getSimpleName());
					final EachArtifactTask<T> phaseTwo = new EachArtifactTask<T>(
							first, getRepositoryName(), artifactType,
							artifactToProcess, behavior, artifactPhase, taskCtx);
					final Task currentTask = currentGroup.prepareTask()
							.withParentTasks(parentTasks)
							.withReadableDescriptionAndUniqueId(
									artifactPhase.getClass().getSimpleName()
											+ ":"
											+ artifactToProcess
													.getArtifactCompleteName()
											+ ":"
											+ taskCtx.getCurrentGroup()
													.getUniqueName())
							.withRunnable(phaseTwo).andPublishTask();
					thisPhaseTasks.add(currentTask);
					allParentTasks.add(currentTask);
				}
				first = false;
				parentTasks.clear();
				parentTasks.addAll(thisPhaseTasks);
				thisPhaseTasks.clear();
			}
			final EndingToProcessArtifactsTask<T> phaseThree = new EndingToProcessArtifactsTask<T>(
					changes, bundleProcessor, repository.getName());
			currentGroup.prepareTask().withParentTasks(allParentTasks)
					.withReadableDescriptionAndUniqueId(
							bundleProcessor.getClass().getSimpleName() + ":"
									+ getRepositoryName()
									+ artifactType.getSimpleName())
					.withRunnable(phaseThree).andPublishTask();

		} catch (final Exception e) {
			for (final T artifactWithError : toBeReturned
					.getArtifactsToBeProcessed()) {
				artifactWithError.setLastProcessedDate(lastProcessedDate);
				artifactWithError
						.setLastProcessStatus(LastProcessStatus.EXCEPTION_DURRING_PROCESS);
			}
			throw e;
		}
	}

	public CurrentProcessorContextImpl getCurrentContext() {
		return currentContext;
	}

	@Override
	public void setBundleContext(final ExecutionContext bundleContext) {
		super.setBundleContext(bundleContext);
		SLContext groupContext;
		try {
			groupContext = bundleContext.getGraphSession().createContext(
					SLConsts.DEFAULT_GROUP_CONTEXT);
			this.currentContext.setGroupContext(groupContext);
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

}
