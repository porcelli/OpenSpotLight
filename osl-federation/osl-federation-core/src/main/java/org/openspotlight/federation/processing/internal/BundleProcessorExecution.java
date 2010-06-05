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

import org.openspotlight.common.SharedConstants;
import org.openspotlight.common.taskexec.TaskExecGroup;
import org.openspotlight.common.taskexec.TaskExecManager;
import org.openspotlight.common.taskexec.TaskExecPool;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.context.ExecutionContextFactory;
import org.openspotlight.federation.domain.*;
import org.openspotlight.federation.domain.GroupListener.ListenerAction;
import org.openspotlight.federation.domain.Repository.GroupVisitor;
import org.openspotlight.federation.processing.BundleExecutionException;
import org.openspotlight.federation.processing.BundleProcessorManager.GlobalExecutionStatus;
import org.openspotlight.federation.processing.internal.domain.CurrentProcessorContextImpl;
import org.openspotlight.federation.processing.internal.task.SaveGraphTask;
import org.openspotlight.federation.processing.internal.task.StartingToSearchArtifactsTask;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BundleProcessorExecution {

    private final GlobalExecutionStatus status = GlobalExecutionStatus.SUCCESS;
    /**
     * The executor.
     */
    private final TaskExecPool pool;
    private final String username;
    private final String password;
    private final JcrConnectionDescriptor descriptor;

    /**
     * The repositories.
     */
    private final Group[] groups;

    /**
     * The context factory.
     */
    private final ExecutionContextFactory contextFactory;

    /**
     * The default sleep interval in millis.
     */
    private final long defaultSleepIntervalInMillis;

    /**
     * The queue.
     */

    private final Set<String> activeReposities = new HashSet<String>();

    /**
     * Instantiates a new bundle processor execution.
     *
     * @param contextFactory the context factory
     * @param settings       the settings
     * @param groups         the repositories
     */
    public BundleProcessorExecution(
            final String username, final String password, final JcrConnectionDescriptor descriptor,
            final ExecutionContextFactory contextFactory, final GlobalSettings settings,
            final Group[] groups) {
        this.username = username;
        this.password = password;
        this.descriptor = descriptor;
        final Repository[] repositories = new Repository[groups.length];
        for (int i = 0, size = groups.length; i < size; i++) {
            repositories[i] = groups[i].getRootRepository();
            activeReposities.add(repositories[i].getName());
        }

        this.groups = groups;
        this.contextFactory = contextFactory;
        defaultSleepIntervalInMillis = settings.getDefaultSleepingIntervalInMilliseconds();
        if (defaultSleepIntervalInMillis <= 0) {
            Exceptions.logAndThrow(new IllegalStateException("Default Thread sleep time in millis must be positive!"));
        }
        pool = TaskExecManager.INSTANCE.createTaskPool("bundle-processor", settings.getParallelThreads());
        final BundleContextThreadInjector listener = new BundleContextThreadInjector(contextFactory, repositories, username,
                password, descriptor);
        pool.addListener(listener);

    }

    /**
     * Execute.
     *
     * @throws BundleExecutionException the bundle execution exception
     * @throws InterruptedException
     */
    public GlobalExecutionStatus execute() throws BundleExecutionException, InterruptedException {
        setupParentNodesAndCallGroupListeners();
        final List<Group> groupsWithBundles = findGroupsWithBundles();

        fillTaskQueue(groupsWithBundles);
        pool.startExecutorBlockingUntilFinish();
        contextFactory.closeResources();

        return status;
    }

    private void fillTaskQueue(final List<Group> groupsWithBundles) {
        int priority = 1;
        for (final Group group : groupsWithBundles) {
            for (final BundleProcessorType bundleProcessorType : group.getBundleTypes()) {
                final Repository repository = group.getRootRepository();
                final CurrentProcessorContextImpl currentContext = new CurrentProcessorContextImpl();
                currentContext.setBundleProcessor(bundleProcessorType);
                currentContext.setCurrentGroup(group);
                currentContext.setCurrentRepository(repository);

                final String idPrefix = bundleProcessorType.getGlobalPhase().getSimpleName() + ":" + repository.getName() + ":"
                        + currentContext.getCurrentGroup().toUniqueJobString() + ":"
                        + bundleProcessorType.getName() + ":" + bundleProcessorType.getGlobalPhase().getName();
                final String seachId = idPrefix + ":searchArtifacts";
                final TaskExecGroup searchArtifacts = pool.createTaskGroup(seachId, priority);

                final StartingToSearchArtifactsTask searchTask = new StartingToSearchArtifactsTask(currentContext,
                        bundleProcessorType,
                        repository, searchArtifacts);
                searchArtifacts.prepareTask().withReadableDescriptionAndUniqueId(seachId).withRunnable(searchTask).andPublishTask();
                final String saveId = idPrefix + ":saveGraph";
                final TaskExecGroup saveGraph = pool.createTaskGroup(saveId, ++priority);
                saveGraph.prepareTask().withReadableDescriptionAndUniqueId(saveId).withRunnable(
                        new SaveGraphTask(
                                repository.getName())).andPublishTask();
                ++priority;

            }
        }

    }

    private List<Group> findGroupsWithBundles() {
        final List<Group> groupsWithBundles = new ArrayList<Group>();
        final GroupVisitor visitor = new GroupVisitor() {
            public void visitGroup(final Group group) {
                if (group.isActive()) {
                    if (group.getBundleTypes() != null && group.getBundleTypes().size() > 0) {
                        for (final BundleProcessorType type : group.getBundleTypes()) {
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
            if (group.isActive() && group.getRootRepository().isActive()) {
                group.acceptVisitor(visitor);
            }
        }
        return groupsWithBundles;
    }

    private void setupParentNodesAndCallGroupListeners() {
        try {
            Repository defaultRepo = new Repository();
            defaultRepo.setName(SLConsts.DEFAULT_REPOSITORY_NAME);
            defaultRepo.setActive(true);
            final ExecutionContext context = contextFactory.createExecutionContext(username, password, descriptor, defaultRepo);
            final SessionWithLock session = context.getDefaultConnectionProvider().openSession();
            final Set<Repository> repositories = new HashSet<Repository>();
            for (final Group group : groups) {
                repositories.add(group.getRootRepository());
            }

            for (final Repository repository : repositories) {
                JCRUtil.getOrCreateByPath(session, session.getRootNode(), SharedConstants.DEFAULT_JCR_ROOT_NAME + "/"
                        + repository.getName());
            }
            session.save();// here the new repository nodes needs to be seen by
            // another opened sessions
            final Set<Group> allGroups = new HashSet<Group>();
            final AggregateVisitor<Group> visitor = new AggregateVisitor<Group>(allGroups);
            for (final Group group : groups) {
                SimpleNodeTypeVisitorSupport.acceptVisitorOn(Group.class, group, visitor);
            }

            for (final Repository repository : repositories) {

                final ExecutionContext internalContext = contextFactory.createExecutionContext(username, password, descriptor,
                        repository);
                final GroupDifferences differences = GroupSupport.getDifferences(
                        internalContext.getPersistentArtifactManager().getSimplePersist(),
                        repository.getName());

                final SLContext groupContext = internalContext.getGraphSession().createContext(SLConsts.DEFAULT_GROUP_CONTEXT);
                for (final Group group : allGroups) {
                    groupContext.getRootNode().addNode(group.getName());
                }
                final Set<GroupListener> groupListenerInstances = new HashSet<GroupListener>();
                if (repository.getGroupListeners() != null) {
                    for (final Class<? extends GroupListener> listenerType : repository.getGroupListeners()) {
                        groupListenerInstances.add(listenerType.newInstance());
                    }
                }
                for (final GroupListener instance : groupListenerInstances) {
                    adding:
                    for (final String added : differences.getAddedGroups()) {
                        final SLNode groupAsSLNode = groupContext.getRootNode().getNode(added);
                        try {
                            final ListenerAction response = instance.groupAdded(groupAsSLNode, internalContext);
                            if (ListenerAction.IGNORE.equals(response)) {
                                continue adding;
                            }
                        } catch (final Exception e) {
                            Exceptions.catchAndLog(e);
                        }
                    }
                    removing:
                    for (final String removed : differences.getRemovedGroups()) {
                        final SLNode groupAsSLNode = groupContext.getRootNode().getNode(removed);
                        try {
                            final ListenerAction response = instance.groupRemoved(groupAsSLNode, internalContext);
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

}
