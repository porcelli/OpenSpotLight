package org.openspotlight.common.task.exception;

import org.openspotlight.common.exception.SLRuntimeException;

public class TaskAlreadyRunnedException extends SLRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9192445490505896383L;

	public TaskAlreadyRunnedException() {
	}

	public TaskAlreadyRunnedException(final String message) {
		super(message);
	}

	public TaskAlreadyRunnedException(final String message,
			final Throwable cause) {
		super(message, cause);
	}

	public TaskAlreadyRunnedException(final Throwable cause) {
		super(cause);
	}

}
