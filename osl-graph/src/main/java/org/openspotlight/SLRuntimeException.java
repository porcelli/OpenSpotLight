package org.openspotlight;

public class SLRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public SLRuntimeException(String message) {
		super(message);
	}
	
	public SLRuntimeException(Throwable cause) {
		super(cause);
	}
	
	public SLRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
}
