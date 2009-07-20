package org.openspotlight.graph;

public class SLNodeNotFoundException extends SLGraphSessionException {

	private static final long serialVersionUID = 1L;

	public SLNodeNotFoundException(String nodeID, Throwable cause) {
		super("Node of " + nodeID + " not found.", cause);
	}

}
