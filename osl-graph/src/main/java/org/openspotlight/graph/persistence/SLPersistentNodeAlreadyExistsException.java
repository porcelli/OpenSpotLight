package org.openspotlight.graph.persistence;

public class SLPersistentNodeAlreadyExistsException extends SLPersistentTreeSessionException {

	private static final long serialVersionUID = 1L;

	public SLPersistentNodeAlreadyExistsException(String name) {
		super("Persistent node " + name + " already exists.");
	}
}
