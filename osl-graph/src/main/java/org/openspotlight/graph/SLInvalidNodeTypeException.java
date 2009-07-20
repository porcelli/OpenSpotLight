package org.openspotlight.graph;


public class SLInvalidNodeTypeException extends SLGraphSessionException {

	private static final long serialVersionUID = 1L;
	
	public SLInvalidNodeTypeException(Class<?> invalidClass, Class<?> nodeClass) {
		super("Node cannot be retrieved as " + invalidClass.getName() + ". " + nodeClass.getName() + " or super class should be used instead.");
	}
}
