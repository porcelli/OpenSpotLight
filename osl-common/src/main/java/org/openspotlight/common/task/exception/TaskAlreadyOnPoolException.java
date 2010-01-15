package org.openspotlight.common.task.exception;

import org.openspotlight.common.exception.SLRuntimeException;

public class TaskAlreadyOnPoolException extends SLRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9206311141690458463L;

	public TaskAlreadyOnPoolException() {
		super();
	}

	public TaskAlreadyOnPoolException(final String message) {
		super(message);
	}

	public TaskAlreadyOnPoolException(final String message,
			final Throwable cause) {
		super(message, cause);
	}

	public TaskAlreadyOnPoolException(final Throwable cause) {
		super(cause);
	}

}
