package org.openspotlight.graph;

	
public class SLLinkPropertyNotFoundException extends SLGraphSessionException {
	
	private static final long serialVersionUID = 1L;

	public SLLinkPropertyNotFoundException(String name, Throwable cause) {
		super("Link property " + name + " does not exit.", cause);
	}
}
