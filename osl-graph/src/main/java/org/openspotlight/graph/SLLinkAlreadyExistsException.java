package org.openspotlight.graph;

public class SLLinkAlreadyExistsException extends SLGraphSessionException {

	private static final long serialVersionUID = 1L;

	public SLLinkAlreadyExistsException(String aName, String bName) {
		super("Node " + aName + " is already linked to " + bName);
	}
}
