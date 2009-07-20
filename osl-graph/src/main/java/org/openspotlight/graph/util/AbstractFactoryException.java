package org.openspotlight.graph.util;

import org.openspotlight.SLException;

public class AbstractFactoryException extends SLException {
	
	private static final long serialVersionUID = 1L;

	public AbstractFactoryException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public AbstractFactoryException(String message) {
		super(message);
	}
	
	public AbstractFactoryException(Throwable cause) {
		super(cause);
	}
}
