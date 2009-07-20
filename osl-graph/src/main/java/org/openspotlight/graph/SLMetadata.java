package org.openspotlight.graph;

import java.util.Collection;

public interface SLMetadata {

	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public Collection<SLMetaNode> getMetaNodes() throws SLGraphSessionException;
	
	/**
	 * 
	 * @param nodeClass
	 * @return
	 * @throws SLGraphSessionException
	 */
	public SLMetaNode getMetaNode(Class<? extends SLNode> nodeClass) throws SLGraphSessionException;
	
	/**
	 * 
	 * @param linkType
	 * @return
	 * @throws SLGraphSessionException
	 */
	public SLMetaLinkType getMetaLinkType(Class<? extends SLLink> linkType) throws SLGraphSessionException;
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public Collection<SLMetaLinkType> getMetaLinkTypes() throws SLGraphSessionException;
}


