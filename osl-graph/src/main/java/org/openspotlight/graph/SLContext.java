package org.openspotlight.graph;

public interface SLContext {
	
	/**
	 * 
	 * @return
	 */
	public SLGraphSession getSession();
	
	/**
	 * 
	 * @return
	 */
	public Long getID() throws SLGraphSessionException;
	
	/**
	 * 
	 * @return
	 * @throws SLGraphSessionException
	 */
	public SLNode getRootNode() throws SLGraphSessionException;

}

