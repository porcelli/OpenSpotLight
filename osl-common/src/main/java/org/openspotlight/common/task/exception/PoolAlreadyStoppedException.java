package org.openspotlight.common.task.exception;

import org.openspotlight.common.exception.SLRuntimeException;

public class PoolAlreadyStoppedException extends SLRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8816925609455849626L;

	public PoolAlreadyStoppedException() {
	}

	public PoolAlreadyStoppedException(final String message) {
		super(message);
	}

	public PoolAlreadyStoppedException(final String message,
			final Throwable cause) {
		super(message, cause);
	}

	public PoolAlreadyStoppedException(final Throwable cause) {
		super(cause);
	}

}
