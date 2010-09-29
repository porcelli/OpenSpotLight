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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.openspotlight.bundle.annotation.SchedulableCommandMap;
import org.openspotlight.bundle.context.ExecutionContextFactory;
import org.openspotlight.bundle.domain.GlobalSettings;
import org.openspotlight.bundle.domain.Repository;
import org.openspotlight.bundle.domain.Schedulable;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.SLCollections;
import org.openspotlight.federation.util.RepositorySet;
import org.openspotlight.persist.util.SimpleNodeTypeVisitor;
import org.openspotlight.persist.util.SimpleNodeTypeVisitorSupport;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;


@Singleton
public class DefaultScheduler implements Scheduler {

    private static final String DEFAULT_GROUP = "DEFAULT_GROUP";

    private static class InternalData {
        public final String username;
        public final String password;
        public final ExecutionContextFactory contextFactory;

        public InternalData(
                final String username, final String password,
                final ExecutionContextFactory contextFactory) {
            super();
            this.username = username;
            this.password = password;
            this.contextFactory = contextFactory;
        }
    }

    public static class OslInternalImmediateCommand extends OslInternalSchedulerCommand {

        private final String identifier;
        private final ExecutionContextFactory factory;


        private final Map<Class<? extends Schedulable>, Class<? extends SchedulableTaskFactory>> schedulableMap;

        @SuppressWarnings("unchecked")
        
        public OslInternalImmediateCommand(
                final Schedulable schedulable,
                final AtomicReference<InternalData> internalData,
                final AtomicReference<GlobalSettings> settings, ExecutionContextFactory factory, Map<Class<? extends Schedulable>, Class<? extends SchedulableTaskFactory>> schedulableMap) {
            super(schedulable, internalData, settings, IMMEDIATE, factory, schedulableMap);
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

        private final String jobName;

        private final String cronInformation;

        private final AtomicReference<GlobalSettings> settings;

        private final AtomicReference<InternalData> internalData;

        private final Schedulable schedulable;


        private final Map<Class<? extends Schedulable>, Class<? extends SchedulableTaskFactory>> schedulableMap;
        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final ExecutionContextFactory factory;

        @SuppressWarnings("unchecked")
        public OslInternalSchedulerCommand(
                final Schedulable schedulable,
                final AtomicReference<InternalData> internalData,
                final AtomicReference<GlobalSettings> settings, final String cronInformation, ExecutionContextFactory factory, Map<Class<? extends Schedulable>, Class<? extends SchedulableTaskFactory>> schedulableMap) {
            this.schedulable = schedulable;
            this.settings = settings;
            this.internalData = internalData;
            this.cronInformation = cronInformation;
            this.factory = factory;
            this.schedulableMap = schedulableMap;
            jobName = schedulable.toUniqueJobString();
        }

        @SuppressWarnings("unchecked")
        public void execute() throws JobExecutionException {
            try {
                final GlobalSettings settingsCopy = settings.get();
                final InternalData data = internalData.get();

                SchedulableTaskFactory factory = getFactoryFromClass(schedulable.getClass(), this.schedulableMap);
                SchedulerTask[] tasksToRun = factory.createTasks(schedulable, this.factory);
                for (SchedulerTask s : tasksToRun) {
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


    private static SchedulableTaskFactory getFactoryFromClass(Class<? extends Schedulable> targetSchedulableType, Map<Class<? extends Schedulable>, Class<? extends SchedulableTaskFactory>> schedulableMap) throws Exception {

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

    public static class SchedulableVisitor implements SimpleNodeTypeVisitor<Schedulable> {

        private final List<Schedulable> beans = new LinkedList<Schedulable>();

        public List<Schedulable> getBeans() {
            return beans;
        }

        public void visitBean(final Schedulable bean) {
            beans.add(bean);

        }

    }

    private final ConcurrentHashMap<String, OslInternalSchedulerCommand> oslCronCommands = new ConcurrentHashMap<String, OslInternalSchedulerCommand>();

    private final ConcurrentHashMap<String, OslInternalSchedulerCommand> oslImmediateCommands = new ConcurrentHashMap<String, OslInternalSchedulerCommand>();

    private final AtomicReference<InternalData> internalData = new AtomicReference<InternalData>();

    private final org.quartz.Scheduler quartzScheduler;

    private final AtomicReference<GlobalSettings> settings = new AtomicReference<GlobalSettings>();

    public static final String IMMEDIATE = "immediate";


    private final Map<Class<? extends Schedulable>, Class<? extends SchedulableTaskFactory>> schedulableMap;
    private final ExecutionContextFactory executionContextFactory;

    @Inject
    public DefaultScheduler(ExecutionContextFactory executionContextFactory, @SchedulableCommandMap Map<Class<? extends Schedulable>, Class<? extends SchedulableTaskFactory>> schedulableMap) {
        this.executionContextFactory = executionContextFactory;
        this.schedulableMap = schedulableMap;
        try {
            System.setProperty("org.quartz.threadPool.threadCount", "1");
            quartzScheduler = new StdSchedulerFactory().getScheduler();
        } catch (final SchedulerException e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    public <T extends Schedulable> void fireSchedulable(final String username,
                                                        final String password,
                                                        final T... schedulables) {
        final Set<String> ids = internalFireCommand(username, password, schedulables);
        final long sleep = settings.get().getDefaultSleepingIntervalInMilliseconds();
        while (isExecutingAnyOfImmediateCommands(ids)) {
            try {
                Thread.sleep(sleep);
            } catch (final InterruptedException e) {
            }
        }
    }


    public <T extends Schedulable> void fireSchedulableInBackground(final String username,
                                                                    final String password,
                                                                    final T... schedulables) {
        internalFireCommand(username, password, schedulables);

    }


    OslInternalSchedulerCommand getCommandByName(final String name) {
        OslInternalSchedulerCommand command = oslCronCommands.get(name);
        if (command == null) {
            command = oslImmediateCommands.get(name);
        }
        return command;
    }

    @SuppressWarnings("unchecked")
    private Map<String, OslInternalSchedulerCommand> groupJobsByCronInformation(final GlobalSettings settings,
                                                                                final Iterable<Repository> repositories) {

        final RepositorySet repositorySet = new RepositorySet();
        repositorySet.setRepositories(repositories);
        final SchedulableVisitor visitor = new SchedulableVisitor();
        SimpleNodeTypeVisitorSupport.acceptVisitorOn(Schedulable.class, repositorySet, visitor);
        final List<Schedulable> schedulableList = visitor.getBeans();
        final Map<String, OslInternalSchedulerCommand> newJobs = new HashMap<String, OslInternalSchedulerCommand>();
        for (final Schedulable s : schedulableList) {
            for (final String cronInformation : s.getCronInformation()) {

                final OslInternalSchedulerCommand job = new OslInternalSchedulerCommand(s, internalData,
                        this.settings, cronInformation, this.executionContextFactory, schedulableMap);
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
        Assertions.checkNotNull("internalData", internalData.get());
        Assertions.checkNotNull("settings", settings.get());
        final Set<String> ids = new HashSet<String>();
        final GlobalSettings settingsReference = settings.get();
        for (final Schedulable schedulable : schedulables) {
            Assertions.checkNotNull("schedulable", schedulable);
            try {

                final InternalData copy = new InternalData(username, password,
                        internalData.get().contextFactory);
                final AtomicReference<InternalData> copyRef = new AtomicReference<InternalData>(copy);
                final OslInternalImmediateCommand command = new OslInternalImmediateCommand(schedulable, copyRef,
                        settings, this.executionContextFactory, schedulableMap);
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
        for (final String id : ids) {
            if (executingKeys.contains(id)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void refreshJobs(final GlobalSettings settings,
                                         final Iterable<Repository> repositories) {
        Assertions.checkNotNull("settings", settings);
        Assertions.checkNotNull("repositories", repositories);
        Assertions.checkNotNull("internalData", internalData.get());
        this.settings.set(settings);
        final Map<String, OslInternalSchedulerCommand> jobMap = groupJobsByCronInformation(settings, repositories);
        oslCronCommands.clear();
        oslCronCommands.putAll(jobMap);
        try {
            final Set<String> jobsToRemove = SLCollections.setOf(quartzScheduler.getJobNames(DEFAULT_GROUP));
            final Set<String> newJobNames = new HashSet<String>(jobMap.keySet());
            newJobNames.removeAll(jobsToRemove);

            jobsToRemove.removeAll(newJobNames);
            for (final String jobToRemove : jobsToRemove) {
                quartzScheduler.deleteJob(jobToRemove, DEFAULT_GROUP);
            }
            for (final String newJob : newJobNames) {
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

    public void startScheduler() {
        Assertions.checkNotNull("internalData", internalData.get());
        try {
            quartzScheduler.start();
        } catch (final Exception e) {
            stopScheduler();
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }

    }

    public void stopScheduler() {
        try {
            quartzScheduler.shutdown();
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

}
