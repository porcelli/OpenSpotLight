package org.openspotlight.graph;

public class SLContextAlreadyExistsException extends SLGraphSessionException {

	private static final long serialVersionUID = 1L;

	public SLContextAlreadyExistsException(Long id) {
		super("Context " + id + " already exists.");
	}
}
