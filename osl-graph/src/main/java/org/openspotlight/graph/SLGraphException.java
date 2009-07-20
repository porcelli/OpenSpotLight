package org.openspotlight.graph;

import org.openspotlight.SLException;

public class SLGraphException extends SLException {

	private static final long serialVersionUID = 1L;
	
	public SLGraphException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public SLGraphException(String message) {
		super(message);
	}
	
	public SLGraphException(Throwable cause) {
		super(cause);
	}
}
