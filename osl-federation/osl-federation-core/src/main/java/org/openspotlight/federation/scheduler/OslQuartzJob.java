package org.openspotlight.federation.scheduler;

import org.openspotlight.federation.scheduler.DefaultScheduler.OslCommand;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class OslQuartzJob implements Job {

	public void execute(final JobExecutionContext arg0)
			throws JobExecutionException {
		OslCommand command = null;
		try {
			final String jobName = arg0.getJobDetail().getName();
			command = DefaultScheduler.INSTANCE.getCommandByName(jobName);
			command.execute();

		} finally {
			DefaultScheduler.INSTANCE.removeIfImediate(command);
		}
	}

}
