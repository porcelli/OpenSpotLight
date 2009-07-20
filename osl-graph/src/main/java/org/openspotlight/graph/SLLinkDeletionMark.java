package org.openspotlight.graph;

public interface SLLinkDeletionMark {

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
