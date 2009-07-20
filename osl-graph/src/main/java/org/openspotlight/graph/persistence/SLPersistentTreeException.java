package org.openspotlight.graph.persistence;

import org.openspotlight.graph.SLGraphException;

public class SLPersistentTreeException extends SLGraphException {

	private static final long serialVersionUID = 1L;

	public SLPersistentTreeException(String message) {
		super(message);
	}

	public SLPersistentTreeException(String message, Throwable cause) {
		super(message, cause);
	}
}
