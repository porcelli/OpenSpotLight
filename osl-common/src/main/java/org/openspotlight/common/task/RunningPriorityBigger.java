package org.openspotlight.common.task;

import org.openspotlight.common.exception.SLRuntimeException;

public class RunningPriorityBigger extends SLRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6767316530358426467L;

	public RunningPriorityBigger() {
	}

	public RunningPriorityBigger(final String message) {
		super(message);
	}

	public RunningPriorityBigger(final String message, final Throwable cause) {
		super(message, cause);
	}

	public RunningPriorityBigger(final Throwable cause) {
		super(cause);
	}

}
