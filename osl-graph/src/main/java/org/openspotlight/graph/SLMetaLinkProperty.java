package org.openspotlight.graph;

import java.io.Serializable;

public interface SLMetaLinkProperty extends SLMetaElement {
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public SLMetaLink getMetaLink() throws SLGraphSessionException;
	
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
