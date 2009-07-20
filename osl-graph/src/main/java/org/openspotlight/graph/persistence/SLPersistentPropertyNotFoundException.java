package org.openspotlight.graph.persistence;

public class SLPersistentPropertyNotFoundException extends SLPersistentTreeSessionException {

	private static final long serialVersionUID = 1L;

	public SLPersistentPropertyNotFoundException(String name) {
		super("Persistent property " + name + " not found.");
	}
}
