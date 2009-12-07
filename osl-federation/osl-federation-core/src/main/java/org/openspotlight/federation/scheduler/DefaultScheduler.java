package org.openspotlight.federation.scheduler;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Collections;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.domain.Schedulable;
import org.openspotlight.federation.domain.Schedulable.SchedulableCommand;
import org.openspotlight.federation.domain.Schedulable.SchedulableContext;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.util.SimpleNodeTypeVisitor;
import org.openspotlight.persist.util.SimpleNodeTypeVisitorSupport;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;

public enum DefaultScheduler implements SLScheduler {
	INSTANCE;

	public static class OslInternalSchedulerCommand {

		private final String jobName;

		private final String cronInformation;

		@SuppressWarnings("unchecked")
		private final Class<? extends SchedulableCommand> commandType;

		private final Schedulable schedulable;

		private final SchedulableContextFactory factory;

		@SuppressWarnings("unchecked")
		public OslInternalSchedulerCommand(final Schedulable schedulable,
				final Class<? extends SchedulableCommand> commandType,
				final SchedulableContextFactory factory,
				final String cronInformation) {
			this.schedulable = schedulable;
			this.factory = factory;
			this.commandType = commandType;
			this.cronInformation = cronInformation;
			jobName = schedulable.toUniqueJobString();
		}

		@SuppressWarnings("unchecked")
		public void execute() throws JobExecutionException {

			SchedulableContext context = null;
			try {
				context = factory.createContext();
				final SchedulableCommand<Schedulable> command = commandType
						.newInstance();
				command.execute(context, schedulable);
			} catch (final Exception e) {
				Exceptions.logAndReturnNew(e, JobExecutionException.class);
			} finally {
				if (context != null) {
					context.closeResources();
				}
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

	public static class RepositorySet implements SimpleNodeType {

		private Set<Repository> repositories;

		public Set<Repository> getRepositories() {
			return repositories;
		}

		public void setRepositories(final Set<Repository> repositories) {
			this.repositories = repositories;
		}
	}

	private static class SchedulableVisitor implements
			SimpleNodeTypeVisitor<Schedulable> {

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

	private static String DEFAULT_GROUP = "osl jobs";

	private final AtomicReference<SchedulableContextFactory> contextFactory = new AtomicReference<SchedulableContextFactory>();

	private final Scheduler quartzScheduler;

	private final AtomicReference<GlobalSettings> settings = new AtomicReference<GlobalSettings>();

	public static final String IMMEDIATE = "immediate";

	private DefaultScheduler() {
		try {
			quartzScheduler = new StdSchedulerFactory().getScheduler();
		} catch (final SchedulerException e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends Schedulable> void fireSchedulable(final T schedulable) {
		try {
			Assertions.checkNotNull("schedulable", schedulable);
			Assertions.checkNotNull("contextFactory", contextFactory.get());
			Assertions.checkNotNull("settings", settings.get());
			final Class<? extends SchedulableCommand> commandType = settings
					.get().getSchedulableCommandMap().get(
							schedulable.getClass());

			Assertions.checkNotNull("commandType:" + schedulable.getClass(),
					commandType);

			final OslInternalSchedulerCommand command = new OslInternalSchedulerCommand(
					schedulable, commandType, getContextFactory(), IMMEDIATE);
			oslImmediateCommands.put(command.getUniqueName(), command);
			final Date runTime = TriggerUtils.getNextGivenSecondDate(
					new Date(), 10);
			final JobDetail job = new JobDetail(command.getUniqueName(),
					DEFAULT_GROUP, OslQuartzJob.class);
			final SimpleTrigger trigger = new SimpleTrigger(command
					.getUniqueName(), DEFAULT_GROUP, runTime);
			quartzScheduler.scheduleJob(job, trigger);
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	OslInternalSchedulerCommand getCommandByName(final String name) {
		OslInternalSchedulerCommand command = oslCronCommands.get(name);
		if (command == null) {
			command = oslImmediateCommands.get(name);
		}
		return command;
	}

	private SchedulableContextFactory getContextFactory() {
		return contextFactory.get();
	}

	@SuppressWarnings("unchecked")
	private Map<String, OslInternalSchedulerCommand> groupJobsByCronInformation(
			final GlobalSettings settings, final Set<Repository> repositories) {
		@SuppressWarnings("unused")
		final Map<Class<? extends Schedulable>, Class<? extends SchedulableCommand>> commandMap = settings
				.getSchedulableCommandMap();

		final RepositorySet repositorySet = new RepositorySet();
		repositorySet.setRepositories(repositories);
		final SchedulableVisitor visitor = new SchedulableVisitor();
		SimpleNodeTypeVisitorSupport.acceptVisitorOn(Schedulable.class,
				repositorySet, visitor);
		final List<Schedulable> schedulableList = visitor.getBeans();
		final Map<String, OslInternalSchedulerCommand> newJobs = new HashMap<String, OslInternalSchedulerCommand>();
		for (final Schedulable s : schedulableList) {
			for (final String cronInformation : s.getCronInformation()) {

				final Class<? extends SchedulableCommand> commandType = settings
						.getSchedulableCommandMap().get(s.getClass());

				Assertions.checkNotNull("commandType:" + s.getClass(),
						commandType);

				final OslInternalSchedulerCommand job = new OslInternalSchedulerCommand(
						s, commandType, getContextFactory(), cronInformation);
				newJobs.put(job.getUniqueName(), job);
			}
		}
		return newJobs;
	}

	public synchronized void refreshJobs(final GlobalSettings settings,
			final Set<Repository> repositories) {
		Assertions.checkNotNull("settings", settings);
		Assertions.checkNotNull("repositories", repositories);
		Assertions.checkNotNull("contextFactory", contextFactory.get());
		this.settings.set(settings);
		final Map<String, OslInternalSchedulerCommand> jobMap = groupJobsByCronInformation(
				settings, repositories);
		oslCronCommands.clear();
		oslCronCommands.putAll(jobMap);
		try {
			final Set<String> jobsToRemove = Collections.setOf(quartzScheduler
					.getJobNames(DEFAULT_GROUP));
			final Set<String> newJobNames = new HashSet<String>(jobMap.keySet());
			newJobNames.removeAll(jobsToRemove);

			jobsToRemove.removeAll(newJobNames);
			for (final String jobToRemove : jobsToRemove) {
				quartzScheduler.deleteJob(jobToRemove, DEFAULT_GROUP);
			}
			for (final String newJob : newJobNames) {
				final OslInternalSchedulerCommand command = jobMap.get(newJob);
				final JobDetail job = new JobDetail(command.getUniqueName(),
						DEFAULT_GROUP, OslQuartzJob.class);
				final CronTrigger trigger = new CronTrigger(command
						.getUniqueName(), DEFAULT_GROUP, command
						.getUniqueName(), DEFAULT_GROUP, command
						.getCronInformation());
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

	public synchronized void setSchedulableContextFactory(
			final SchedulableContextFactory contextFactory) {
		this.contextFactory.set(contextFactory);
	}

	public void startScheduler() {
		Assertions.checkNotNull("contextFactory", contextFactory.get());
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
