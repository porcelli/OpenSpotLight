package org.openspotlight.graph;

public interface SLGraphSessionEventListener {
	
	/**
	 * 
	 * @param event
	 * @throws SLGraphSessionException
	 */
	public void beforeSave(SLGraphSessionEvent event) throws SLGraphSessionException;
	
	/**
	 * 
	 * @param event
	 * @throws SLGraphSessionException
	 */
	public void nodeAdded(SLNodeEvent event) throws SLGraphSessionException;
	
	/**
	 * 
	 * @param event
	 * @throws SLGraphSessionException
	 */
	public void linkAdded(SLLinkEvent event) throws SLGraphSessionException;
	
}


