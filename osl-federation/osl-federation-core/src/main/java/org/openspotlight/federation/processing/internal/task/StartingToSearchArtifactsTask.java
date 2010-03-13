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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.taskexec.TaskExec;
import org.openspotlight.common.taskexec.TaskExecGroup;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.Strings;
import org.openspotlight.common.util.PatternMatcher.FilterResult;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.BundleProcessorType;
import org.openspotlight.federation.domain.BundleSource;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.LastProcessStatus;
import org.openspotlight.federation.finder.OriginArtifactLoader;
import org.openspotlight.federation.processing.ArtifactChanges;
import org.openspotlight.federation.processing.ArtifactsToBeProcessed;
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

public class StartingToSearchArtifactsTask extends RunnableWithBundleContext {

	private final CurrentProcessorContextImpl currentContext;
	private final BundleProcessorType bundleProcessorType;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Repository repository;
	private final TaskExecGroup currentGroup;

	public StartingToSearchArtifactsTask(
			final CurrentProcessorContextImpl currentContext,
			final BundleProcessorType bundleProcessorType,
			final Repository repository, final TaskExecGroup currentGroup) {
		super(repository.getName());
		this.currentContext = currentContext;
		this.bundleProcessorType = bundleProcessorType;
		this.repository = repository;
		this.currentGroup = currentGroup;
	}

	@SuppressWarnings("unchecked")
	public void doIt() throws Exception {
		final BundleProcessorGlobalPhase<? extends Artifact> bundleProcessor = bundleProcessorType
		.getGlobalPhase().newInstance();

		final Map<Class<? extends Artifact>, ArtifactChanges<Artifact>> changesByType = new HashMap<Class<? extends Artifact>, ArtifactChanges<Artifact>>();
		final Map<Class<? extends Artifact>, ArtifactsToBeProcessed<Artifact>> returnByType = new HashMap<Class<? extends Artifact>, ArtifactsToBeProcessed<Artifact>>();

		for (final Class<? extends Artifact> artifactType : bundleProcessor
				.getArtifactTypes()) {

			final ArtifactChangesImpl<Artifact> changes = new ArtifactChangesImpl<Artifact>();
			final ArtifactsToBeProcessedImpl<Artifact> toBeReturned = new ArtifactsToBeProcessedImpl<Artifact>();
			changesByType.put(artifactType, changes);
			returnByType.put(artifactType, toBeReturned);
			final OriginArtifactLoader<? extends Artifact> finder = getBundleContext()
			.getArtifactFinder(artifactType);
			for (final BundleSource src : bundleProcessorType.getSources()) {

				final Set<String> rawNames = finder
				.retrieveAllArtifactNames(src.getRelative());
				final FilterResult newNames = filterNamesByPattern(Strings
						.rootPath(src.getRelative()), rawNames,
						src.getIncludeds(), src.getExcludeds(), false);
				for (final String name : newNames.getIncludedNames()) {
					final Artifact savedArtifact = finder.findByPath(name);
					if (savedArtifact != null) {
						switch (savedArtifact.getChangeType()) {
						case CHANGED:
							changes.getChangedArtifacts().add(savedArtifact);
							break;
						case EXCLUDED:
							changes.getExcludedArtifacts().add(savedArtifact);
							break;
						case INCLUDED:
							changes.getIncludedArtifacts().add(savedArtifact);
							break;
						case NOT_CHANGED:
							changes.getNotChangedArtifacts().add(savedArtifact);
							break;
						}
					} else {
						logger.info("null artifact " + name
								+ " on finder of type "
								+ artifactType.getName());
					}
				}
			}

			toBeReturned.getArtifactsToBeProcessed().addAll(
					changes.getChangedArtifacts());
			toBeReturned.getArtifactsToBeProcessed().addAll(
					changes.getIncludedArtifacts());
			final Date lastProcessedDate = new Date();
			try {
				bundleProcessor.selectArtifactsToBeProcessed(currentContext,
						getBundleContext(), changes, toBeReturned);
				for (final Artifact artifactAlreadyProcessed : toBeReturned
						.getArtifactsAlreadyProcessed()) {
					artifactAlreadyProcessed
					.setLastProcessedDate(lastProcessedDate);
					artifactAlreadyProcessed
					.setLastProcessStatus(LastProcessStatus.PROCESSED);

				}
			} catch (final Exception e) {
				for (final Artifact artifactWithError : toBeReturned
						.getArtifactsToBeProcessed()) {
					artifactWithError.setLastProcessedDate(lastProcessedDate);
					artifactWithError
					.setLastProcessStatus(LastProcessStatus.EXCEPTION_DURRING_PROCESS);
				}
				throw e;
			}

		}

		for (final Class<? extends Artifact> artifactType : bundleProcessor
				.getArtifactTypes()) {
			final List<BundleProcessorArtifactPhase<Artifact>> artifactPhases = new ArrayList<BundleProcessorArtifactPhase<Artifact>>();
			final SaveBehavior behavior = bundleProcessor.getSaveBehavior();
			if (bundleProcessor instanceof BundleProcessorArtifactPhase<?>) {
				artifactPhases
				.add((BundleProcessorArtifactPhase<Artifact>) bundleProcessor);
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
					.add((BundleProcessorArtifactPhase<Artifact>) artifactPhase);
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

			final List<TaskExec> parentTasks = new LinkedList<TaskExec>();
			final List<TaskExec> allParentTasks = new LinkedList<TaskExec>();
			boolean first = true;
			final ArtifactChanges<Artifact> changes = changesByType
			.get(artifactType);
			final ArtifactsToBeProcessed<Artifact> toBeReturned = returnByType
			.get(artifactType);

			for (final BundleProcessorArtifactPhase<Artifact> artifactPhase : artifactPhases) {

				final List<TaskExec> thisPhaseTasks = new LinkedList<TaskExec>();
				for (final Artifact artifactToProcess : toBeReturned
						.getArtifactsToBeProcessed()) {

					logger
					.info(" Adding processor artifact phase for bundle type "
							+ artifactPhase.getClass().getSimpleName()
							+ " and artifact type "
							+ artifactType.getSimpleName());
					final EachArtifactTask<Artifact> phaseTwo = new EachArtifactTask<Artifact>(
							first, getRepositoryName(), artifactType,
							artifactToProcess, behavior, artifactPhase,
							currentContext);
					final TaskExec currentTask = currentGroup.prepareTask()
					.withParentTasks(parentTasks)
					.withReadableDescriptionAndUniqueId(
							artifactPhase.getClass().getSimpleName()
							+ ":"
							+ artifactToProcess
							.getArtifactCompleteName()
							+ ":"
							+ currentContext.getCurrentGroup()
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

			final EndingToProcessArtifactsTask phaseThree = new EndingToProcessArtifactsTask(
					changes, bundleProcessor, repository.getName(),
					getBundleContext(), currentContext);
			currentGroup.prepareTask().withParentTasks(allParentTasks)
			.withReadableDescriptionAndUniqueId(
					bundleProcessor.getClass().getSimpleName()
					+ ":"
					+ getRepositoryName()
					+ ":"
					+ artifactType.getSimpleName()
					+ ":"
					+ bundleProcessorType.getName()
					+ ":"
					+ bundleProcessorType.getGroup()
					.getUniqueName()).withRunnable(
							phaseThree).andPublishTask();

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
			currentContext.setGroupContext(groupContext);
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

}
