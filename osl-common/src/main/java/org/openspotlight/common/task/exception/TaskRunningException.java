package org.openspotlight.common.task.exception;

import org.openspotlight.common.exception.SLRuntimeException;

public class TaskRunningException extends SLRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4418769247671137720L;

	public TaskRunningException() {
	}

	public TaskRunningException(final String message) {
		super(message);
	}

	public TaskRunningException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public TaskRunningException(final Throwable cause) {
		super(cause);
	}

}
