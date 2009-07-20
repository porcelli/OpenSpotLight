package org.openspotlight.graph;


public class SLInvalidLinkSideException extends SLGraphSessionException {

	private static final long serialVersionUID = 1L;

	public SLInvalidLinkSideException() {
		super("Node is not part of the link.");
	}
}
