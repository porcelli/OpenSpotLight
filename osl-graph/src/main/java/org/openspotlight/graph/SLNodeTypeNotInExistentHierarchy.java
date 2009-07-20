package org.openspotlight.graph;

public class SLNodeTypeNotInExistentHierarchy extends SLGraphSessionException {
	
	private static final long serialVersionUID = 1L;
	
	public SLNodeTypeNotInExistentHierarchy(Class<?> nodeClass, Class<?> existentNodeClass) {
		super(nodeClass.getName() + " does not belong to the same hierarchy of " + existentNodeClass.getName() + ". Node overwriting can only happen within the class hierarchy.");
	}
}
