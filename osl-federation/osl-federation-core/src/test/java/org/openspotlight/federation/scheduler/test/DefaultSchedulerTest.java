package org.openspotlight.federation.scheduler.test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.hamcrest.number.IsCloseTo;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.domain.Schedulable.SchedulableCommand;
import org.openspotlight.federation.domain.Schedulable.SchedulableContext;
import org.openspotlight.federation.scheduler.DefaultScheduler;
import org.openspotlight.federation.scheduler.SLScheduler;
import org.openspotlight.federation.scheduler.SLScheduler.SchedulableContextFactory;

public class DefaultSchedulerTest {

	public static class SampleArtifactSourceSchedulableCommand implements
			SchedulableCommand<ArtifactSource> {

		public void execute(final SchedulableContext ctx,
				final ArtifactSource schedulable) {
			// TODO Auto-generated method stub

		}

	}

	public static class SampleContextFactory implements
			SchedulableContextFactory {

		public SchedulableContext createContext() {
			return new SchedulableContext() {

				public void closeResources() {

				}
			};
		}

	}

	public static class SampleGroupSchedulableCommand implements
			SchedulableCommand<Group> {

		private static AtomicBoolean wasExecuted = new AtomicBoolean();

		private static AtomicInteger counter = new AtomicInteger();

		public void execute(final SchedulableContext ctx,
				final Group schedulable) {
			System.out.println(schedulable.getName());
			wasExecuted.set(true);
			counter.incrementAndGet();
		}

	}

	private static SLScheduler scheduler = DefaultScheduler.INSTANCE;

	private static Set<Repository> repositories = new HashSet<Repository>();

	private static GlobalSettings settings = new GlobalSettings();

	@BeforeClass
	public static void setupScheduler() {

		settings = new GlobalSettings();
		settings.getSchedulableCommandMap().clear();
		settings.getSchedulableCommandMap().put(ArtifactSource.class,
				SampleArtifactSourceSchedulableCommand.class);
		settings.getSchedulableCommandMap().put(Group.class,
				SampleGroupSchedulableCommand.class);
		final Repository repository = new Repository();
		repositories.add(repository);
		repository.setActive(true);
		repository.setName("repository");

		final Group group = new Group();
		group.setActive(true);
		group.setName("new group");
		group.setRepository(repository);
		group.setType("types");
		repository.getGroups().add(group);
		scheduler.setSchedulableContextFactory(new SampleContextFactory());
		scheduler.refreshJobs(settings, repositories);
		scheduler.startScheduler();
	}

	@AfterClass
	public static void stopScheduler() {
		scheduler.stopScheduler();
	}

	@Before
	public void resetStatus() {
		SampleGroupSchedulableCommand.wasExecuted.set(false);
		SampleGroupSchedulableCommand.counter.set(0);
	}

	@Test
	public void shouldStartCronJobs() throws Exception {
		final Group group = repositories.iterator().next().getGroups()
				.iterator().next();
		try {
			group.getCronInformation().add("0/1 * * * * ?");
			scheduler.refreshJobs(settings, repositories);
			Thread.sleep(10000);
			Assert.assertThat((double) SampleGroupSchedulableCommand.counter
					.get(), IsCloseTo.closeTo(10d, 1d));

		} finally {
			group.getCronInformation().clear();
			scheduler.refreshJobs(settings, repositories);
		}
	}

	@Test
	public void shouldStartImediateJob() throws Exception {
		scheduler.fireSchedulable(repositories.iterator().next().getGroups()
				.iterator().next());
		for (int i = 0; i < 20; i++) {
			if (SampleGroupSchedulableCommand.wasExecuted.get()) {
				return;
			}
			Thread.sleep(1000);
		}
		Assert.fail("Didn't execute in 20 seconds!");
	}
}
