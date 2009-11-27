package org.openspotlight.federation.domain;

import java.util.List;

/**
 * The Interface Schedulable.
 */
public interface Schedulable {

	/**
	 * The Interface SchedulableCommand.
	 */
	public static interface SchedulableCommand<S extends Schedulable> {

		/**
		 * Execute.
		 * 
		 * @param schedulable
		 *            the schedulable
		 */
		public void execute(S schedulable);
	}

	/**
	 * Gets the cron information. For each String, please follow the cron syntax
	 * described on
	 * http://www.quartz-scheduler.org/docs/tutorials/crontrigger.html
	 * 
	 * @return the cron information
	 */
	public List<String> getCronInformation();

}
