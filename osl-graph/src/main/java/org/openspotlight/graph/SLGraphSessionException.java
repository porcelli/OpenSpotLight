package org.openspotlight.graph;

public class SLGraphSessionException extends SLGraphException {

	private static final long serialVersionUID = 1L;

	public SLGraphSessionException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public SLGraphSessionException(String message) {
		super(message);
	}
	
	public SLGraphSessionException(Throwable cause) {
		super(cause);
	}

}
