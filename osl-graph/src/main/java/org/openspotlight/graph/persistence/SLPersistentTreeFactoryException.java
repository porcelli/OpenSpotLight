package org.openspotlight.graph.persistence;

import org.openspotlight.graph.util.AbstractFactoryException;

public class SLPersistentTreeFactoryException extends AbstractFactoryException {

	private static final long serialVersionUID = 1L;
	
	public SLPersistentTreeFactoryException(String message) {
		super(message);
	}
	
	public SLPersistentTreeFactoryException(Throwable cause) {
		super(cause);
	}

	public SLPersistentTreeFactoryException(String message, Throwable cause) {
		super(message, cause);
	}
}
