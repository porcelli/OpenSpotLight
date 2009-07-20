package org.openspotlight;

public class SLException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public SLException(String message) {
		super(message);
	}
	
	public SLException(Throwable cause) {
		super(cause);
	}
	
	public SLException(String message, Throwable cause) {
		super(message, cause);
	}
}
