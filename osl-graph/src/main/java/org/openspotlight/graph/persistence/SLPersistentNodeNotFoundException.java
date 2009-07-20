package org.openspotlight.graph.persistence;

public class SLPersistentNodeNotFoundException extends SLPersistentTreeSessionException {

	private static final long serialVersionUID = 1L;

	public SLPersistentNodeNotFoundException(String nodeID, Throwable cause) {
		super("Persistent node of ID " + nodeID + " not found.", cause);
	}
}
