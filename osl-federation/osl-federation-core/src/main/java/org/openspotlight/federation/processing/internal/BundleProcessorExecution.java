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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

import org.openspotlight.common.SharedConstants;
import org.openspotlight.common.concurrent.GossipExecutor;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.context.ExecutionContextFactory;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.BundleProcessorType;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.domain.GroupListener;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.domain.GroupListener.ListenerAction;
import org.openspotlight.federation.domain.Repository.GroupVisitor;
import org.openspotlight.federation.processing.BundleExecutionException;
import org.openspotlight.federation.processing.BundleProcessorManager.GlobalExecutionStatus;
import org.openspotlight.federation.processing.internal.domain.CurrentProcessorContextImpl;
import org.openspotlight.federation.processing.internal.task.ArtifactTask;
import org.openspotlight.federation.processing.internal.task.ArtifactTaskPriorityComparator;
import org.openspotlight.federation.processing.internal.task._10_StartingToSearchArtifactsTask;
import org.openspotlight.federation.processing.internal.task._5_SaveGraphTask;
import org.openspotlight.federation.util.AggregateVisitor;
import org.openspotlight.federation.util.GroupDifferences;
import org.openspotlight.federation.util.GroupSupport;
import org.openspotlight.graph.SLConsts;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLNode;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.jcr.provider.SessionWithLock;
import org.openspotlight.jcr.util.JCRUtil;
import org.openspotlight.persist.util.SimpleNodeTypeVisitorSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class BundleProcessorExecution.
 */
public class BundleProcessorExecution {

	private GlobalExecutionStatus status = GlobalExecutionStatus.SUCCESS;
	/** The executor. */
	private final GossipExecutor executor;
	private final String username;
	private final String password;
	private final JcrConnectionDescriptor descriptor;
	/** The repositories. */
	private final Group[] groups;

	/** The context factory. */
	private final ExecutionContextFactory contextFactory;

	/** The artifact types. */
	private final Set<Class<? extends Artifact>> artifactTypes;

	/** The default sleep interval in millis. */
	private final long defaultSleepIntervalInMillis;

	/** The threads. */
	private final int threads;

	/** The queue. */
	private final PriorityBlockingQueue<ArtifactTask> queue = new PriorityBlockingQueue<ArtifactTask>(
			1000, new ArtifactTaskPriorityComparator());

	private final Set<String> activeReposities = new HashSet<String>();

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Instantiates a new bundle processor execution.
	 * 
	 * @param contextFactory
	 *            the context factory
	 * @param settings
	 *            the settings
	 * @param groups
	 *            the repositories
	 * @param artifactTypes
	 *            the artifact types
	 */
	public BundleProcessorExecution(final String username,
			final String password, final JcrConnectionDescriptor descriptor,
			final ExecutionContextFactory contextFactory,
			final GlobalSettings settings, final Group[] groups,
			final Set<Class<? extends Artifact>> artifactTypes) {
		this.username = username;
		this.password = password;
		this.descriptor = descriptor;
		final String[] repositoryNames = new String[groups.length];
		for (int i = 0, size = groups.length; i < size; i++) {
			repositoryNames[i] = groups[i].getRepository().getName();
			activeReposities.add(repositoryNames[i]);
		}

		this.groups = groups;
		this.contextFactory = contextFactory;
		this.artifactTypes = artifactTypes;
		threads = settings.getNumberOfParallelThreads();
		if (threads <= 0) {
			Exceptions.logAndThrow(new IllegalStateException(
					"Default Thread size must be positive!"));
		}
		defaultSleepIntervalInMillis = settings
				.getDefaultSleepingIntervalInMilliseconds();
		if (defaultSleepIntervalInMillis <= 0) {
			Exceptions.logAndThrow(new IllegalStateException(
					"Default Thread sleep time in millis must be positive!"));
		}
		executor = GossipExecutor.newFixedThreadPool(threads);
		final BundleContextThreadInjector listener = new BundleContextThreadInjector(
				contextFactory, repositoryNames, username, password, descriptor);
		executor.addTaskListener(listener);
		executor.addThreadListener(listener);
	}

	/**
	 * Execute.
	 * 
	 * @throws BundleExecutionException
	 *             the bundle execution exception
	 */
	public GlobalExecutionStatus execute() throws BundleExecutionException {
		setupParentNodesAndCallGroupListeners();
		final Set<Group> groupsWithBundles = findGroupsWithBundles();

		fillTaskQueue(groupsWithBundles);

		final List<ArtifactWorker> workers = setupWorkers();
		logger.info("creating " + workers.size() + " thread workers");

		monitorThreadActivity(workers);
		contextFactory.closeResources();

		return status;
	}

	private void fillTaskQueue(final Set<Group> groupsWithBundles) {
		for (final Class<? extends Artifact> artifactType : artifactTypes) {
			for (final Group group : groupsWithBundles) {
				for (final BundleProcessorType processor : group
						.getBundleTypes()) {
					final Repository repository = group.getRootRepository();
					final CurrentProcessorContextImpl currentContextImpl = new CurrentProcessorContextImpl();
					currentContextImpl.setCurrentGroup(group);
					currentContextImpl.setCurrentRepository(repository);
					final ArtifactTask task = new _10_StartingToSearchArtifactsTask<Artifact>(
							currentContextImpl, repository, artifactType,
							processor);
					queue.add(task);

				}
			}
		}
		for (final String repository : activeReposities) {
			queue.add(new _5_SaveGraphTask<Artifact>(repository));
		}

	}

	private Set<Group> findGroupsWithBundles() {
		final Set<Group> groupsWithBundles = new HashSet<Group>();
		final GroupVisitor visitor = new GroupVisitor() {
			public void visitGroup(final Group group) {
				if (group.isActive()) {
					if (group.getBundleTypes() != null
							&& group.getBundleTypes().size() > 0) {
						for (final BundleProcessorType type : group
								.getBundleTypes()) {
							if (type.isActive()) {
								groupsWithBundles.add(group);
								return;
							}
						}
					}
				}
			}
		};
		for (final Group group : groups) {
			if (group.isActive() && group.getRepository().isActive()) {
				group.acceptVisitor(visitor);
			}
		}
		return groupsWithBundles;
	}

	private void monitorThreadActivity(final List<ArtifactWorker> workers) {
		monitor: while (true) {
			try {
				Thread.sleep(defaultSleepIntervalInMillis);
			} catch (final InterruptedException e) {
				Exceptions.catchAndLog(e);
			}
			if (queue.isEmpty()) {
				boolean hasAnyWorker = false;
				findingWorkers: for (final ArtifactWorker worker : workers) {
					if (worker.hasError()) {
						status = GlobalExecutionStatus.ERROR;
					}
					if (!worker.isWorking()) {
						worker.stop();
						continue findingWorkers;
					} else {
						hasAnyWorker = true;
					}
				}
				if (!hasAnyWorker) {
					break monitor;
				}
			}
		}
	}

	private void setupParentNodesAndCallGroupListeners() {
		try {
			final ExecutionContext context = contextFactory
					.createExecutionContext(username, password, descriptor,
							SLConsts.DEFAULT_REPOSITORY_NAME);
			final SessionWithLock session = context
					.getDefaultConnectionProvider().openSession();
			final Set<String> repositoryNames = new HashSet<String>();
			for (final Group group : groups) {
				repositoryNames.add(group.getRepository().getName());
			}

			for (final String repository : repositoryNames) {
				JCRUtil.getOrCreateByPath(session, session.getRootNode(),
						SharedConstants.DEFAULT_JCR_ROOT_NAME + "/"
								+ repository);
			}
			final Set<Group> allGroups = new HashSet<Group>();
			final AggregateVisitor<Group> visitor = new AggregateVisitor<Group>(
					allGroups);
			for (final Group group : groups) {
				SimpleNodeTypeVisitorSupport.acceptVisitorOn(Group.class,
						group, visitor);
			}

			for (final String repositoryName : repositoryNames) {

				final ExecutionContext internalContext = contextFactory
						.createExecutionContext(username, password, descriptor,
								repositoryName);
				final GroupDifferences differences = GroupSupport
						.getDifferences(session, repositoryName);

				final SLContext groupContext = internalContext
						.getGraphSession().createContext(
								SLConsts.DEFAULT_GROUP_CONTEXT);
				for (final Group group : allGroups) {
					groupContext.getRootNode().addNode(group.getName());
				}
				final Repository repository = context
						.getDefaultConfigurationManager().getRepositoryByName(
								repositoryName);
				final Set<GroupListener> groupListenerInstances = new HashSet<GroupListener>();
				if (repository.getGroupListeners() != null) {
					for (final Class<? extends GroupListener> listenerType : repository
							.getGroupListeners()) {
						groupListenerInstances.add(listenerType.newInstance());
					}
				}
				for (final GroupListener instance : groupListenerInstances) {
					adding: for (final String added : differences
							.getAddedGroups()) {
						final SLNode groupAsSLNode = groupContext.getRootNode()
								.getNode(added);
						try {
							final ListenerAction response = instance
									.groupAdded(groupAsSLNode, internalContext);
							if (ListenerAction.IGNORE.equals(response)) {
								continue adding;
							}
						} catch (final Exception e) {
							Exceptions.catchAndLog(e);
						}
					}
					removing: for (final String removed : differences
							.getRemovedGroups()) {
						final SLNode groupAsSLNode = groupContext.getRootNode()
								.getNode(removed);
						try {
							final ListenerAction response = instance
									.groupRemoved(groupAsSLNode,
											internalContext);
							if (ListenerAction.IGNORE.equals(response)) {
								continue removing;
							}
						} catch (final Exception e) {
							Exceptions.catchAndLog(e);
						}
					}
				}
				internalContext.closeResources();
			}
			session.save();
			session.logout();

			context.closeResources();

		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, BundleExecutionException.class);
		}

	}

	private List<ArtifactWorker> setupWorkers() {
		final List<ArtifactWorker> workers = new ArrayList<ArtifactWorker>(
				threads);

		for (int i = 0; i < threads; i++) {
			final ArtifactWorker worker = new ArtifactWorker(
					defaultSleepIntervalInMillis, queue);
			workers.add(worker);
			executor.execute(worker);
		}
		return workers;
	}

}
