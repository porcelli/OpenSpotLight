package org.openspotlight.graph;

public interface SLLinkedNodeDeletionMark {

	/**
	 * 
	 * @return
	 */
	public Class<? extends SLLink> getLinkType();

	/**
	 * 
	 * @return
	 */
	public SLNode getNode();
}
