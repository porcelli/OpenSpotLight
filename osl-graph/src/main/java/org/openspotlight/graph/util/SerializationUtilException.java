package org.openspotlight.graph.util;

import org.openspotlight.SLException;

public class SerializationUtilException extends SLException {
	
	private static final long serialVersionUID = 1L;

	public SerializationUtilException(String message) {
		super(message);
	}

	public SerializationUtilException(Throwable cause) {
		super(cause);
	}

	public SerializationUtilException(String message, Throwable cause) {
		super(message, cause);
	}
}
