package org.openspotlight.bundle.domain;

import org.openspotlight.bundle.context.ExecutionContext;
import org.openspotlight.bundle.context.ExecutionContextFactory;

public interface SchedulableCommand<S extends Schedulable> {

	/**
	 * Execute.
	 * 
	 * @param schedulable
	 *            the schedulable
	 */
	public void execute(GlobalSettings settings, ExecutionContext ctx,
			S schedulable) throws Exception;

	public String getRepositoryNameBeforeExecution(S schedulable);

	public static interface SchedulableCommandWithContextFactory<S extends Schedulable>
			extends SchedulableCommand<S> {
		public void setContextFactoryBeforeExecution(GlobalSettings settings,
				String username, String password, String repository,
				ExecutionContextFactory factory);
	}

}