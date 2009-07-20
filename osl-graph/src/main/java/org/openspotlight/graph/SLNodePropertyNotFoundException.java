package org.openspotlight.graph;

public class SLNodePropertyNotFoundException extends SLGraphSessionException {

	private static final long serialVersionUID = 1L;

	public SLNodePropertyNotFoundException(String name, Throwable cause) {
		super("Property " + name + " does not exit.", cause);
	}
}
