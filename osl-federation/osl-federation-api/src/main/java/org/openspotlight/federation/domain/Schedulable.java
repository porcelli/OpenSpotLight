package org.openspotlight.federation.domain;

import java.util.List;

import org.openspotlight.common.Disposable;
import org.openspotlight.persist.annotation.SimpleNodeType;

/**
 * The Interface Schedulable.
 */
public interface Schedulable extends SimpleNodeType {
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
		public void execute(SchedulableContext ctx, S schedulable);
	}

	public static interface SchedulableContext extends Disposable {
		// FIXME add entries
	}

	/**
	 * Gets the cron information. For each String, please follow the cron syntax
	 * described on
	 * http://www.quartz-scheduler.org/docs/tutorials/crontrigger.html
	 * 
	 * @return the cron information
	 */
	public List<String> getCronInformation();

	public void setSchedulableContext(SchedulableContext ctx);

	/**
	 * This string should return an unique identifier for this job to be used
	 * inside the scheduler.
	 * 
	 * @return
	 */
	public String toUniqueJobString();

}
