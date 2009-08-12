package org.openspotlight.graph.query;

import org.openspotlight.common.exception.SLRuntimeException;

public class SLInvalidQuerySyntaxRuntimeException extends SLRuntimeException {

	private static final long serialVersionUID = 1L;
	
	public SLInvalidQuerySyntaxRuntimeException(String message) {
		super(message);
	}
}
