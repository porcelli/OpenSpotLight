package org.openspotlight.graph.query;

import org.openspotlight.graph.SLGraphSessionException;

public class SLInvalidQuerySyntaxException extends SLGraphSessionException {

	private static final long serialVersionUID = 1L;
	
	public SLInvalidQuerySyntaxException(String message) {
		super(message);
	}
}
