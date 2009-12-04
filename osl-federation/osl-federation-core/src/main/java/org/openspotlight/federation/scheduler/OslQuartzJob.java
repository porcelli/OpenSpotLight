package org.openspotlight.federation.scheduler;

import org.openspotlight.common.util.Assertions;
import org.openspotlight.federation.scheduler.DefaultScheduler.OslInternalSchedulerCommand;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.quartz.UnableToInterruptJobException;

public class OslQuartzJob implements StatefulJob, InterruptableJob {

	public void execute(final JobExecutionContext arg0)
			throws JobExecutionException {
		OslInternalSchedulerCommand command = null;
		try {
			final String jobName = arg0.getJobDetail().getName();
			command = DefaultScheduler.INSTANCE.getCommandByName(jobName);
			Assertions.checkNotNull("command", command);
			command.execute();

		} finally {
			if (command != null) {
				DefaultScheduler.INSTANCE.removeIfImediate(command);
			}
		}
	}

	public void interrupt() throws UnableToInterruptJobException {

	}

}
