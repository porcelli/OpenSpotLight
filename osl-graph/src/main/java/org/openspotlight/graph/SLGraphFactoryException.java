package org.openspotlight.graph;

import org.openspotlight.graph.util.AbstractFactoryException;

public class SLGraphFactoryException extends AbstractFactoryException {

	private static final long serialVersionUID = 1L;

	public SLGraphFactoryException(String message, Throwable cause) {
		super(message, cause);
	}

	public SLGraphFactoryException(String message) {
		super(message);
	}

	public SLGraphFactoryException(Throwable cause) {
		super(cause);
	}
}
