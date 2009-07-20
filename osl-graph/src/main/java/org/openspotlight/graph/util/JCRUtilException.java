package org.openspotlight.graph.util;

import org.openspotlight.SLException;

public class JCRUtilException extends SLException {

	private static final long serialVersionUID = 1L;

	public JCRUtilException(String message) {
		super(message);
	}

	public JCRUtilException(Throwable cause) {
		super(cause);
	}

	public JCRUtilException(String message, Throwable cause) {
		super(message, cause);
	}
}
