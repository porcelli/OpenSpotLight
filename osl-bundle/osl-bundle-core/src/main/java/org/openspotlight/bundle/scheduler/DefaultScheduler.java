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
package org.openspotlight.bundle.scheduler;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.openspotlight.bundle.annotation.SchedulableCommandMap;
import org.openspotlight.bundle.context.ExecutionContextFactory;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.SLCollections;
import org.openspotlight.domain.GlobalSettings;
import org.openspotlight.domain.Repository;
import org.openspotlight.domain.Schedulable;
import org.openspotlight.federation.util.RepositorySet;
import org.openspotlight.persist.util.SimpleNodeTypeVisitor;
import org.openspotlight.persist.util.SimpleNodeTypeVisitorSupport;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultScheduler implements Scheduler {

    public static class OslInternalImmediateCommand extends OslInternalSchedulerCommand {

        private final ExecutionContextFactory                                                    factory;
        private final String                                                                     identifier;

        private final Map<Class<? extends Schedulable>, Class<? extends SchedulableTaskFactory>> schedulableMap;

        public OslInternalImmediateCommand(
                                           final Schedulable schedulable,
                                           final ExecutionContextFactory executionContextFactory,
                                           final AtomicReference<GlobalSettings> settings,
                                           final ExecutionContextFactory factory,
                                           final Map<Class<? extends Schedulable>, Class<? extends SchedulableTaskFactory>> schedulableMap) {
            super(schedulable, executionContextFactory, settings, IMMEDIATE, factory, schedulableMap);
            this.factory = factory;
            this.schedulableMap = schedulableMap;
            identifier = UUID.randomUUID().toString();

        }

        @Override
        public String getUniqueName() {
            return identifier;
        }

    }

    public static class OslInternalSchedulerCommand {

        private final String                                                                     cronInformation;

        private final ExecutionContextFactory                                                    executionContextFactory;

        private final ExecutionContextFactory                                                    factory;

        private final String                                                                     jobName;

        private final Logger                                                                     logger =
                                                                                                            LoggerFactory
                                                                                                                .getLogger(getClass());

        private final Schedulable                                                                schedulable;
        private final Map<Class<? extends Schedulable>, Class<? extends SchedulableTaskFactory>> schedulableMap;

        private final AtomicReference<GlobalSettings>                                            settings;

        public OslInternalSchedulerCommand(
                                           final Schedulable schedulable,
                                           final ExecutionContextFactory executionContextFactory,
                                           final AtomicReference<GlobalSettings> settings,
                                           final String cronInformation,
                                           final ExecutionContextFactory factory,
                                           final Map<Class<? extends Schedulable>, Class<? extends SchedulableTaskFactory>> schedulableMap) {
            this.schedulable = schedulable;
            this.settings = settings;
            this.executionContextFactory = executionContextFactory;
            this.cronInformation = cronInformation;
            this.factory = factory;
            this.schedulableMap = schedulableMap;
            jobName = schedulable.toUniqueJobString();
        }

        @SuppressWarnings("unchecked")
        public void execute()
            throws JobExecutionException {
            try {
                final GlobalSettings settingsCopy = settings.get();

                final SchedulableTaskFactory factory = getFactoryFromClass(schedulable.getClass(), schedulableMap);
                final SchedulerTask[] tasksToRun = factory.createTasks(schedulable, this.factory);
                for (final SchedulerTask s: tasksToRun) {
                    logger.info("about to execute " + s.getClass() + " with schedulable " + schedulable.toUniqueJobString());
                    s.call();
                    logger.info("executed successfully " + s.getClass() + " with schedulable "
                            + schedulable.toUniqueJobString());
                }

            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e, JobExecutionException.class);
            }
        }

        public String getCronInformation() {
            return cronInformation;
        }

        public String getJobName() {
            return jobName;
        }

        public String getUniqueName() {
            return jobName + ":" + cronInformation;
        }

    }

    public static class SchedulableVisitor implements SimpleNodeTypeVisitor<Schedulable> {

        private final List<Schedulable> beans = new LinkedList<Schedulable>();

        public List<Schedulable> getBeans() {
            return beans;
        }

        @Override
        public void visitBean(final Schedulable bean) {
            beans.add(bean);

        }

    }

    private static final String                                                              DEFAULT_GROUP        =
                                                                                                                      "DEFAULT_GROUP";

    private static DefaultScheduler                                                          defaultInstance      = null;

    public static final String                                                               IMMEDIATE            = "immediate";

    private final ExecutionContextFactory                                                    executionContextFactory;

    private final ConcurrentHashMap<String, OslInternalSchedulerCommand>                     oslCronCommands      =
                                                                                                                      new ConcurrentHashMap<String, OslInternalSchedulerCommand>();

    private final ConcurrentHashMap<String, OslInternalSchedulerCommand>                     oslImmediateCommands =
                                                                                                                      new ConcurrentHashMap<String, OslInternalSchedulerCommand>();

    private final org.quartz.Scheduler                                                       quartzScheduler;

    private final Map<Class<? extends Schedulable>, Class<? extends SchedulableTaskFactory>> schedulableMap;
    private final AtomicReference<GlobalSettings>                                            settings             =
                                                                                                                      new AtomicReference<GlobalSettings>();

    @Inject
    public DefaultScheduler(final ExecutionContextFactory executionContextFactory,
                            @SchedulableCommandMap final Map<Class<? extends Schedulable>, Class<? extends SchedulableTaskFactory>> schedulableMap) {
        this.executionContextFactory = executionContextFactory;
        this.schedulableMap = schedulableMap;
        try {
            System.setProperty("org.quartz.threadPool.threadCount", "1");
            quartzScheduler = new StdSchedulerFactory().getScheduler();
        } catch (final SchedulerException e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
        defaultInstance = this;
    }

    private static SchedulableTaskFactory
        getFactoryFromClass(final Class<? extends Schedulable> targetSchedulableType,
                            final Map<Class<? extends Schedulable>, Class<? extends SchedulableTaskFactory>> schedulableMap)
            throws Exception {

        Class<? extends SchedulableTaskFactory> commandType = null;
        Class<? extends Schedulable> lastClass = targetSchedulableType;

        while (commandType == null && lastClass != null && !Object.class.equals(lastClass)) {

            commandType = schedulableMap.get(lastClass);
            if (commandType != null) {
                break;
            }
            lastClass = (Class<? extends Schedulable>) lastClass.getSuperclass();
        }
        return commandType != null ? commandType.newInstance() : null;
    }

    public static DefaultScheduler getDefaultInstance() {
        return defaultInstance;
    }

    private Map<String, OslInternalSchedulerCommand> groupJobsByCronInformation(final GlobalSettings settings,
                                                                                final Iterable<Repository> repositories) {

        final RepositorySet repositorySet = new RepositorySet();
        repositorySet.setRepositories(repositories);
        final SchedulableVisitor visitor = new SchedulableVisitor();
        SimpleNodeTypeVisitorSupport.acceptVisitorOn(Schedulable.class, repositorySet, visitor);
        final List<Schedulable> schedulableList = visitor.getBeans();
        final Map<String, OslInternalSchedulerCommand> newJobs = new HashMap<String, OslInternalSchedulerCommand>();
        for (final Schedulable s: schedulableList) {
            for (final String cronInformation: s.getCronInformation()) {

                final OslInternalSchedulerCommand job = new OslInternalSchedulerCommand(s, executionContextFactory,
                        this.settings, cronInformation, executionContextFactory, schedulableMap);
                newJobs.put(job.getUniqueName(), job);
            }
        }
        return newJobs;
    }

    @SuppressWarnings("unchecked")
    private <T extends Schedulable> Set<String> internalFireCommand(final String username,
                                                                    final String password,
                                                                    final T... schedulables) {
        Assertions.checkNotNull("schedulables", schedulables);
        Assertions.checkNotNull("executionContextFactory", executionContextFactory.get());
        Assertions.checkNotNull("settings", settings.get());
        final Set<String> ids = new HashSet<String>();
        final GlobalSettings settingsReference = settings.get();
        for (final Schedulable schedulable: schedulables) {
            Assertions.checkNotNull("schedulable", schedulable);
            try {

                final OslInternalImmediateCommand command = new OslInternalImmediateCommand(schedulable, executionContextFactory,
                        settings, executionContextFactory, schedulableMap);
                oslImmediateCommands.put(command.getUniqueName(), command);
                ids.add(command.getUniqueName());
                final Date runTime = TriggerUtils.getNextGivenSecondDate(new Date(), 1);
                final JobDetail job = new JobDetail(command.getUniqueName(), DEFAULT_GROUP, OslQuartzJob.class);
                final SimpleTrigger trigger = new SimpleTrigger(command.getUniqueName(), DEFAULT_GROUP, runTime);
                quartzScheduler.scheduleJob(job, trigger);
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
            }

        }
        return ids;
    }

    private boolean isExecutingAnyOfImmediateCommands(final Set<String> ids) {
        final Set<String> executingKeys = new HashSet<String>(oslImmediateCommands.keySet());
        for (final String id: ids) {
            if (executingKeys.contains(id)) { return true; }
        }
        return false;
    }

    OslInternalSchedulerCommand getCommandByName(final String name) {
        OslInternalSchedulerCommand command = oslCronCommands.get(name);
        if (command == null) {
            command = oslImmediateCommands.get(name);
        }
        return command;
    }

    @Override
    public <T extends Schedulable> void fireSchedulable(final String username,
                                                        final String password,
                                                        final T... schedulables) {
        final Set<String> ids = internalFireCommand(username, password, schedulables);
        final long sleep = settings.get().getDefaultSleepingIntervalInMilliseconds();
        while (isExecutingAnyOfImmediateCommands(ids)) {
            try {
                Thread.sleep(sleep);
            } catch (final InterruptedException e) {}
        }
    }

    @Override
    public <T extends Schedulable> void fireSchedulableInBackground(final String username,
                                                                    final String password,
                                                                    final T... schedulables) {
        internalFireCommand(username, password, schedulables);

    }

    @Override
    public synchronized void refreshJobs(final GlobalSettings settings,
                                         final Iterable<Repository> repositories) {
        Assertions.checkNotNull("settings", settings);
        Assertions.checkNotNull("repositories", repositories);
        Assertions.checkNotNull("executionContextFactory", executionContextFactory.get());
        this.settings.set(settings);
        final Map<String, OslInternalSchedulerCommand> jobMap = groupJobsByCronInformation(settings, repositories);
        oslCronCommands.clear();
        oslCronCommands.putAll(jobMap);
        try {
            final Set<String> jobsToRemove = SLCollections.setOf(quartzScheduler.getJobNames(DEFAULT_GROUP));
            final Set<String> newJobNames = new HashSet<String>(jobMap.keySet());
            newJobNames.removeAll(jobsToRemove);

            jobsToRemove.removeAll(newJobNames);
            for (final String jobToRemove: jobsToRemove) {
                quartzScheduler.deleteJob(jobToRemove, DEFAULT_GROUP);
            }
            for (final String newJob: newJobNames) {
                final OslInternalSchedulerCommand command = jobMap.get(newJob);
                final JobDetail job = new JobDetail(command.getUniqueName(), DEFAULT_GROUP, OslQuartzJob.class);
                final CronTrigger trigger = new CronTrigger(command.getUniqueName(), DEFAULT_GROUP, command.getUniqueName(),
                        DEFAULT_GROUP, command.getCronInformation());
                quartzScheduler.scheduleJob(job, trigger);

            }
        } catch (final Exception e) {
            stopScheduler();
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }

    }

    public void removeIfImediate(final OslInternalSchedulerCommand command) {
        Assertions.checkNotNull("command", command);
        if (IMMEDIATE.equals(command.getCronInformation())) {
            oslImmediateCommands.remove(command.getUniqueName());
        }

    }

    @Override
    public void startScheduler() {
        Assertions.checkNotNull("executionContextFactory", executionContextFactory.get());
        try {
            quartzScheduler.start();
        } catch (final Exception e) {
            stopScheduler();
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }

    }

    @Override
    public void stopScheduler() {
        try {
            quartzScheduler.shutdown();
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }
}
