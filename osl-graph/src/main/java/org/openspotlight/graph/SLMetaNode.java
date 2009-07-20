package org.openspotlight.graph;

import java.util.Collection;

public interface SLMetaNode extends SLMetaElement {
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public Class<? extends SLNode> getType() throws SLGraphSessionException;

	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public Collection<SLMetaNodeProperty> getMetaProperties() throws SLGraphSessionException;
	
	/**
	 * 
	 * @param name
	 * @return
	 * @throws SLGraphSessionException
	 */
	public SLMetaNodeProperty getMetaProperty(String name) throws SLGraphSessionException;
	
	/**
	 * 
	 * @param nodeClass
	 * @return
	 * @throws SLGraphSessionException
	 */
	public SLMetaNode getMetaNode(Class<? extends SLNode> nodeClass) throws SLGraphSessionException;
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public Collection<SLMetaNode> getMetaNodes() throws SLGraphSessionException;
}

