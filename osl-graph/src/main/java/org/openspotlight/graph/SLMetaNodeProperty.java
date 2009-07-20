package org.openspotlight.graph;

import java.io.Serializable;

public interface SLMetaNodeProperty extends SLMetaElement {
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public SLMetaNode getMetaNode() throws SLGraphSessionException;
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public String getName() throws SLGraphSessionException;
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public Class<? extends Serializable> getType() throws SLGraphSessionException;

}
