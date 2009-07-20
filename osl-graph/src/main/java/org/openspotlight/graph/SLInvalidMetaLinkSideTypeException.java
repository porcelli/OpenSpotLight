package org.openspotlight.graph;

public class SLInvalidMetaLinkSideTypeException extends SLGraphSessionException {

	private static final long serialVersionUID = 1L;

	public SLInvalidMetaLinkSideTypeException() {
		super("Node type is not part of the meta link.");
	}

}
